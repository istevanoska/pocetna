// The radio widget is built and working but deliberately not shown:
// components/widgets/radio-widget/ and the /api/radio endpoint are still here.
// To bring it back, import RadioWidget, add it to imports, and put
// <app-radio-widget /> back in sidebar.html under <app-weather-widget />.
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
