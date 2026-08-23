import { Injectable, signal } from '@angular/core';

export type ThemeId = 'light' | 'dark' | 'mk';

export interface ThemeOption {
  id: ThemeId;
  label: string;
  icon: string;
}

export const THEMES: ThemeOption[] = [
  { id: 'light', label: 'Светла', icon: 'sun' },
  { id: 'dark', label: 'Темна', icon: 'moon' },
  { id: 'mk', label: 'Македонска', icon: 'flag' },
];

const STORAGE_KEY = 'pocetna.theme';

@Injectable({ providedIn: 'root' })
export class ThemeService {
  theme = signal<ThemeId>(this.readInitial());

  constructor() {
    this.apply(this.theme());
  }

  setTheme(theme: ThemeId): void {
    this.theme.set(theme);
    this.apply(theme);
    try {
      localStorage.setItem(STORAGE_KEY, theme);
    } catch {
      /* private-browsing / storage disabled — theme just won't persist */
    }
  }

  private apply(theme: ThemeId): void {
    document.documentElement.setAttribute('data-theme', theme);
  }

  private readInitial(): ThemeId {
    try {
      const stored = localStorage.getItem(STORAGE_KEY) as ThemeId | null;
      if (stored === 'light' || stored === 'dark' || stored === 'mk') return stored;
    } catch {
      /* ignore */
    }
    return 'light';
  }
}
