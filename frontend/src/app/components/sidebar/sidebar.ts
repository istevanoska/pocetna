import { Component } from '@angular/core';
import { ClockWidget } from '../widgets/clock-widget/clock-widget';
import { WeatherWidget } from '../widgets/weather-widget/weather-widget';
import { ExchangeWidget } from '../widgets/exchange-widget/exchange-widget';
import { NewsWidget } from '../widgets/news-widget/news-widget';

@Component({
  selector: 'app-sidebar',
  imports: [ClockWidget, WeatherWidget, ExchangeWidget, NewsWidget],
  templateUrl: './sidebar.html',
  styleUrl: './sidebar.scss',
})
export class Sidebar {}
