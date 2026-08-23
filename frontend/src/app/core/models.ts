export interface SiteLink {
  name: string;
  url: string;
  description: string;
}

export interface LinkCategory {
  id: string;
  title: string;
  icon: string;
  links: SiteLink[];
}

export interface SearchResult {
  title: string;
  link: string;
  snippet: string;
  displayLink: string;
}

export interface SearchResponse {
  query: string;
  configured: boolean;
  results: SearchResult[];
  googleFallbackUrl: string;
}

export interface ExchangeRate {
  code: string;
  name: string;
  nominal: number;
  middleRate: number;
}

export interface ExchangeRateList {
  date: string;
  rates: ExchangeRate[];
}

export interface WeatherCity {
  id: string;
  name: string;
  lat: number;
  lon: number;
}

export interface DailyForecast {
  date: string;
  maxTemp: number;
  minTemp: number;
  weatherCode: number;
  description: string;
}

export interface CurrentWeather {
  city: string;
  temperature: number;
  humidity: number;
  windSpeed: number;
  weatherCode: number;
  description: string;
  forecast: DailyForecast[];
}

export interface NewsItem {
  title: string;
  link: string;
  pubDate: string;
  category: string | null;
  source: string;
}

export interface TodayInfo {
  isoDate: string;
  displayDate: string;
  dayOfWeek: string;
  names: string[];
}
