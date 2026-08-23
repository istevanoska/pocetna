import { Injectable, signal } from '@angular/core';

const STORAGE_KEY = 'pocetna.account';

export interface Account {
  email: string;
  name: string;
}

/**
 * Lightweight client-side personalization only — no backend, no password, no
 * real session. Lets a visitor put a name + email in the corner; nothing more.
 */
@Injectable({ providedIn: 'root' })
export class AccountService {
  account = signal<Account | null>(this.readInitial());

  login(email: string, name: string): void {
    const trimmedEmail = email.trim();
    if (!trimmedEmail) return;
    const displayName = name.trim() || trimmedEmail.split('@')[0];
    const account: Account = { email: trimmedEmail, name: displayName };
    this.account.set(account);
    try {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(account));
    } catch {
      /* ignore */
    }
  }

  logout(): void {
    this.account.set(null);
    try {
      localStorage.removeItem(STORAGE_KEY);
    } catch {
      /* ignore */
    }
  }

  private readInitial(): Account | null {
    try {
      const raw = localStorage.getItem(STORAGE_KEY);
      return raw ? (JSON.parse(raw) as Account) : null;
    } catch {
      return null;
    }
  }
}
