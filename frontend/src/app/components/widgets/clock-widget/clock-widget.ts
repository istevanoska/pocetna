import { Component, DestroyRef, inject, OnInit, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { ApiService } from '../../../core/api.service';
import { TodayInfo } from '../../../core/models';

@Component({
  selector: 'app-clock-widget',
  imports: [DatePipe],
  templateUrl: './clock-widget.html',
  styleUrl: './clock-widget.scss',
})
export class ClockWidget implements OnInit {
  private api = inject(ApiService);
  private destroyRef = inject(DestroyRef);

  now = signal(new Date());
  today = signal<TodayInfo | null>(null);

  ngOnInit(): void {
    const timer = setInterval(() => this.now.set(new Date()), 1000);
    this.destroyRef.onDestroy(() => clearInterval(timer));

    this.api.getNameday().subscribe({ next: (info) => this.today.set(info) });
  }
}
