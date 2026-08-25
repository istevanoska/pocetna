import { inject, Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';

const STORAGE_KEY = 'pocetna.account';

export interface Account {
  email: string;
  name: string;
}

/**
 * Registration is persisted server-side (POST /api/register saves email + name
 * to the database). The account is also cached in localStorage so the greeting
 * shows instantly on this device.
 */
@Injectable({ providedIn: 'root' })
export class AccountService {
  private http = inject(HttpClient);

  account = signal<Account | null>(this.readInitial());
  saving = signal(false);
  error = signal<string | null>(null);

  login(email: string, name: string): void {
    const trimmedEmail = email.trim();
    if (!trimmedEmail) return;
    const displayName = name.trim() || trimmedEmail.split('@')[0];

    this.saving.set(true);
    this.error.set(null);
    this.http.post<Account>('/api/register', { email: trimmedEmail, name: displayName }).subscribe({
      next: (acc) => {
        this.setAccount({ email: acc.email, name: acc.name });
        this.saving.set(false);
      },
      error: () => {
        // Still personalize locally even if the save failed.
        this.setAccount({ email: trimmedEmail, name: displayName });
        this.saving.set(false);
        this.error.set('Не можевме да зачуваме на серверот, но профилот е зачуван на овој уред.');
      },
    });
  }

  logout(): void {
    this.account.set(null);
    this.error.set(null);
    try {
      localStorage.removeItem(STORAGE_KEY);
    } catch {
      /* ignore */
    }
  }

  private setAccount(account: Account): void {
    this.account.set(account);
    try {
      localStorage.setItem(STORAGE_KEY, JSON.stringify(account));
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
