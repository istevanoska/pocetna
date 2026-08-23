import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { ApiService } from '../../../core/api.service';
import { ExchangeRate, ExchangeRateList } from '../../../core/models';
import { Icon } from '../../../shared/icon/icon';

const HIGHLIGHTED = ['EUR', 'USD', 'GBP', 'CHF'];

@Component({
  selector: 'app-exchange-widget',
  imports: [DecimalPipe, Icon],
  templateUrl: './exchange-widget.html',
  styleUrl: './exchange-widget.scss',
})
export class ExchangeWidget implements OnInit {
  private api = inject(ApiService);

  data = signal<ExchangeRateList | null>(null);
  loading = signal(true);
  showAll = signal(false);

  highlighted = computed(() => this.filterRates(HIGHLIGHTED));
  rest = computed(() => this.filterRates(HIGHLIGHTED, true));

  ngOnInit(): void {
    this.api.getExchangeRates().subscribe({
      next: (data) => {
        this.data.set(data);
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });
  }

  toggleShowAll(): void {
    this.showAll.set(!this.showAll());
  }

  private filterRates(codes: string[], invert = false): ExchangeRate[] {
    const rates = this.data()?.rates ?? [];
    return rates.filter((r) => codes.includes(r.code) !== invert);
  }
}
