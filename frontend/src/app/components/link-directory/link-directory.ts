import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { ApiService } from '../../core/api.service';
import { CurrentWeather, ExchangeRateList, LinkCategory, TodayInfo } from '../../core/models';
import { categoryIcon } from '../../core/category-icon-map';
import { weatherIconName } from '../../core/weather-icon';
import { faviconUrl } from '../../core/favicon';
import { categoryColor } from '../../core/category-color';
import { quoteOfTheDay, DailyQuote } from '../../core/quotes';
import { Icon } from '../../shared/icon/icon';

type GridItem =
  | { kind: 'category'; category: LinkCategory }
  | { kind: 'weather'; weather: CurrentWeather }
  | { kind: 'exchange'; exchange: ExchangeRateList }
  | { kind: 'today'; today: TodayInfo }
  | { kind: 'quote'; quote: DailyQuote }
  | { kind: 'popular' };

interface PopularSearch {
  label: string;
  query: string;
  icon: string;
}

// Positions (0-based, after this many category panels) where a mini info panel is inserted.
const WEATHER_AFTER = 2;
const QUOTE_AFTER = 3;
const EXCHANGE_AFTER = 5;
const POPULAR_AFTER = 6;
const TODAY_AFTER = 8;

@Component({
  selector: 'app-link-directory',
  imports: [Icon, DecimalPipe],
  templateUrl: './link-directory.html',
  styleUrl: './link-directory.scss',
})
export class LinkDirectory implements OnInit {
  private api = inject(ApiService);

  categories = signal<LinkCategory[]>([]);
  weather = signal<CurrentWeather | null>(null);
  exchange = signal<ExchangeRateList | null>(null);
  today = signal<TodayInfo | null>(null);
  loading = signal(true);

  quote = quoteOfTheDay();

  private now = new Date();

  greeting = computed(() => {
    const h = this.now.getHours();
    if (h < 6) return 'Добра ноќ';
    if (h < 11) return 'Добро утро';
    if (h < 17) return 'Добар ден';
    if (h < 22) return 'Добра вечер';
    return 'Добра ноќ';
  });

  popularSearches: PopularSearch[] = [
    { label: 'Временска прогноза', query: 'временска прогноза Македонија', icon: 'cloud-sun' },
    { label: 'Курс на НБРМ', query: 'курсна листа НБРМ денар', icon: 'coins' },
    { label: 'Резултати во живо', query: 'фудбал резултати во живо', icon: 'trophy' },
    { label: 'Најнови вести', query: 'најнови вести Македонија', icon: 'newspaper' },
    { label: 'Кино репертоар', query: 'кино репертоар Скопје', icon: 'tv' },
    { label: 'Рецепти', query: 'домашни рецепти', icon: 'briefcase' },
  ];

  gridItems = computed<GridItem[]>(() => {
    const items: GridItem[] = [];
    this.categories().forEach((category, i) => {
      items.push({ kind: 'category', category });

      const weather = this.weather();
      if (i === WEATHER_AFTER && weather) items.push({ kind: 'weather', weather });

      if (i === QUOTE_AFTER) items.push({ kind: 'quote', quote: this.quote });

      const exchange = this.exchange();
      if (i === EXCHANGE_AFTER && exchange && exchange.rates.length > 0) items.push({ kind: 'exchange', exchange });

      if (i === POPULAR_AFTER) items.push({ kind: 'popular' });

      const today = this.today();
      if (i === TODAY_AFTER && today) items.push({ kind: 'today', today });
    });
    return items;
  });

  ngOnInit(): void {
    this.api.getLinks().subscribe({
      next: (data) => {
        this.categories.set(data);
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });

    this.api.getWeather('skopje').subscribe({ next: (w) => this.weather.set(w), error: () => {} });
    this.api.getExchangeRates().subscribe({ next: (e) => this.exchange.set(e), error: () => {} });
    this.api.getNameday().subscribe({ next: (t) => this.today.set(t), error: () => {} });
  }

  icon(name: string): string {
    return categoryIcon(name);
  }

  weatherIcon(code: number): string {
    return weatherIconName(code);
  }

  favicon(url: string): string {
    return faviconUrl(url);
  }

  catColor(id: string): string {
    return categoryColor(id);
  }

  searchUrl(query: string): string {
    return `https://www.google.com/search?q=${encodeURIComponent(query)}`;
  }

  trackItem(_: number, item: GridItem): string {
    return item.kind === 'category' ? item.category.id : item.kind;
  }
}
