import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {
  CurrentWeather,
  ExchangeRateList,
  LinkCategory,
  NewsItem,
  SearchResponse,
  TodayInfo,
  WeatherCity,
} from './models';

@Injectable({ providedIn: 'root' })
export class ApiService {
  private http = inject(HttpClient);

  getLinks(): Observable<LinkCategory[]> {
    return this.http.get<LinkCategory[]>('/api/links');
  }

  search(query: string): Observable<SearchResponse> {
    return this.http.get<SearchResponse>('/api/search', { params: { q: query } });
  }

  getExchangeRates(): Observable<ExchangeRateList> {
    return this.http.get<ExchangeRateList>('/api/exchange-rate');
  }

  getWeatherCities(): Observable<WeatherCity[]> {
    return this.http.get<WeatherCity[]>('/api/weather/cities');
  }

  getWeather(cityId: string): Observable<CurrentWeather> {
    return this.http.get<CurrentWeather>('/api/weather', { params: { city: cityId } });
  }

  getNews(): Observable<NewsItem[]> {
    return this.http.get<NewsItem[]>('/api/news');
  }

  getNameday(): Observable<TodayInfo> {
    return this.http.get<TodayInfo>('/api/nameday');
  }

  register(email: string, name: string): Observable<{ email: string; name: string; alreadyRegistered: boolean }> {
    return this.http.post<{ email: string; name: string; alreadyRegistered: boolean }>('/api/register', { email, name });
  }
}
