import { Component, inject, OnInit, signal } from '@angular/core';
import { DatePipe, DecimalPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from '../../../core/api.service';
import { CurrentWeather, WeatherCity } from '../../../core/models';
import { Icon } from '../../../shared/icon/icon';
import { weatherIconName as iconForCode } from '../../../core/weather-icon';

@Component({
  selector: 'app-weather-widget',
  imports: [DatePipe, DecimalPipe, FormsModule, Icon],
  templateUrl: './weather-widget.html',
  styleUrl: './weather-widget.scss',
})
export class WeatherWidget implements OnInit {
  private api = inject(ApiService);

  cities = signal<WeatherCity[]>([]);
  selectedCity = signal('skopje');
  weather = signal<CurrentWeather | null>(null);
  loading = signal(true);

  ngOnInit(): void {
    this.api.getWeatherCities().subscribe((cities) => this.cities.set(cities));
    this.loadWeather();
  }

  onCityChange(): void {
    this.loadWeather();
  }

  weatherIconName(code: number): string {
    return iconForCode(code);
  }

  private loadWeather(): void {
    this.loading.set(true);
    this.api.getWeather(this.selectedCity()).subscribe({
      next: (w) => {
        this.weather.set(w);
        this.loading.set(false);
      },
      error: () => {
        this.weather.set(null);
        this.loading.set(false);
      },
    });
  }
}
