import { Component, OnDestroy, OnInit, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../../core/api.service';
import { RadioStation } from '../../../core/models';
import { Icon } from '../../../shared/icon/icon';

const STATION_KEY = 'pocetna.radio.station';
const VOLUME_KEY = 'pocetna.radio.volume';
const DEFAULT_VOLUME = 0.8;

/** Give up on a stream that neither starts nor errors within this long. */
const CONNECT_TIMEOUT_MS = 20_000;

@Component({
  selector: 'app-radio-widget',
  imports: [FormsModule, Icon],
  templateUrl: './radio-widget.html',
  styleUrl: './radio-widget.scss',
})
export class RadioWidget implements OnInit, OnDestroy {
  private api = inject(ApiService);
  private audio: HTMLAudioElement | null = null;
  private connectTimer: ReturnType<typeof setTimeout> | null = null;

  stations = signal<RadioStation[]>([]);
  selectedId = signal<string>('');
  playing = signal(false);
  connecting = signal(false);
  failed = signal(false);
  volume = signal(DEFAULT_VOLUME);

  selected = computed<RadioStation | undefined>(() =>
    this.stations().find((station) => station.id === this.selectedId()),
  );

  ngOnInit(): void {
    this.volume.set(this.readStoredVolume());

    this.api.getRadioStations().subscribe({
      next: (stations) => {
        this.stations.set(stations);
        const stored = this.read(STATION_KEY);
        const known = stations.find((station) => station.id === stored);
        this.selectedId.set(known?.id ?? stations[0]?.id ?? '');
      },
      error: () => this.stations.set([]),
    });
  }

  ngOnDestroy(): void {
    this.teardown();
  }

  onStationChange(id: string): void {
    this.selectedId.set(id);
    this.write(STATION_KEY, id);
    this.failed.set(false);
    if (this.playing() || this.connecting()) {
      this.start();
    }
  }

  onVolumeChange(value: string): void {
    const parsed = Number(value);
    if (!Number.isFinite(parsed)) return;
    const v = Math.min(1, Math.max(0, parsed));
    this.volume.set(v);
    this.write(VOLUME_KEY, String(v));
    if (this.audio) {
      this.audio.volume = v;
    }
  }

  toggle(): void {
    if (this.playing() || this.connecting()) {
      this.stop();
    } else {
      this.start();
    }
  }

  stop(): void {
    this.teardown();
    this.connecting.set(false);
    this.playing.set(false);
  }

  private start(): void {
    const station = this.selected();
    if (!station) return;

    this.teardown();
    this.failed.set(false);
    this.connecting.set(true);
    this.playing.set(false);

    const audio = new Audio();
    audio.preload = 'none';
    audio.volume = this.volume();

    audio.addEventListener('playing', () => {
      this.clearTimer();
      this.connecting.set(false);
      this.playing.set(true);
    });
    // 'waiting' and 'stalled' are normal on a live stream when the buffer runs
    // dry; they mean "hold on", not "give up".
    audio.addEventListener('waiting', () => this.connecting.set(true));
    audio.addEventListener('error', () => this.onFailure());

    audio.src = station.streamUrl;
    this.audio = audio;

    this.connectTimer = setTimeout(() => {
      if (!this.playing()) this.onFailure();
    }, CONNECT_TIMEOUT_MS);

    audio.play().catch(() => this.onFailure());
  }

  private onFailure(): void {
    this.teardown();
    this.connecting.set(false);
    this.playing.set(false);
    this.failed.set(true);
  }

  /** Drops the connection as well as pausing: a paused stream keeps downloading. */
  private teardown(): void {
    this.clearTimer();
    const audio = this.audio;
    if (!audio) return;
    this.audio = null;
    audio.pause();
    audio.removeAttribute('src');
    audio.load();
  }

  private clearTimer(): void {
    if (this.connectTimer !== null) {
      clearTimeout(this.connectTimer);
      this.connectTimer = null;
    }
  }

  private readStoredVolume(): number {
    const raw = Number(this.read(VOLUME_KEY));
    return Number.isFinite(raw) && raw >= 0 && raw <= 1 ? raw : DEFAULT_VOLUME;
  }

  private read(key: string): string | null {
    try {
      return localStorage.getItem(key);
    } catch {
      return null;
    }
  }

  private write(key: string, value: string): void {
    try {
      localStorage.setItem(key, value);
    } catch {
      // Private browsing or blocked storage: the widget works, it just forgets.
    }
  }
}
