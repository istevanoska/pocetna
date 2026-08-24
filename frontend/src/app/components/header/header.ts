import { Component, ElementRef, HostListener, inject, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { DatePipe } from '@angular/common';
import { ApiService } from '../../core/api.service';
import { SearchResponse, LinkCategory } from '../../core/models';
import { ThemeService, THEMES, ThemeId } from '../../core/theme.service';
import { AccountService } from '../../core/account.service';
import { categoryIcon } from '../../core/category-icon-map';
import { Icon } from '../../shared/icon/icon';

@Component({
  selector: 'app-header',
  imports: [FormsModule, DatePipe, Icon],
  templateUrl: './header.html',
  styleUrl: './header.scss',
})
export class Header implements OnInit {
  private api = inject(ApiService);
  private elementRef = inject(ElementRef<HTMLElement>);

  themeService = inject(ThemeService);
  accountService = inject(AccountService);
  themes = THEMES;

  query = signal('');
  loading = signal(false);
  panelOpen = signal(false);
  response = signal<SearchResponse | null>(null);

  navItems = signal<{ id: string; label: string; icon: string }[]>([]);

  themeMenuOpen = signal(false);
  accountMenuOpen = signal(false);
  loginEmail = signal('');
  loginName = signal('');

  readonly today = new Date();

  ngOnInit(): void {
    this.api.getLinks().subscribe((categories: LinkCategory[]) => {
      this.navItems.set(categories.map((c) => ({ id: c.id, label: c.title, icon: categoryIcon(c.icon) })));
    });
  }

  onSubmit(): void {
    const q = this.query().trim();
    if (!q) return;

    this.loading.set(true);
    this.api.search(q).subscribe({
      next: (res) => {
        this.loading.set(false);
        if (!res.configured) {
          window.open(res.googleFallbackUrl, '_blank', 'noopener');
          return;
        }
        this.response.set(res);
        this.panelOpen.set(true);
      },
      error: () => {
        this.loading.set(false);
        window.open(`https://www.google.com/search?q=${encodeURIComponent(q)}`, '_blank', 'noopener');
      },
    });
  }

  closePanel(): void {
    this.panelOpen.set(false);
  }

  /** Google vertical URLs for the Images / Videos / News tabs. */
  verticalUrl(type: 'images' | 'videos' | 'news'): string {
    const q = encodeURIComponent(this.response()?.query ?? this.query());
    if (type === 'images') return `https://www.google.com/search?tbm=isch&q=${q}`;
    if (type === 'videos') return `https://www.google.com/search?tbm=vid&q=${q}`;
    return `https://news.google.com/search?q=${q}`;
  }

  /** Click a related search — run it in place. */
  searchRelated(term: string): void {
    this.query.set(term);
    this.onSubmit();
  }

  toggleThemeMenu(): void {
    this.accountMenuOpen.set(false);
    this.themeMenuOpen.set(!this.themeMenuOpen());
  }

  selectTheme(id: ThemeId): void {
    this.themeService.setTheme(id);
    this.themeMenuOpen.set(false);
  }

  toggleAccountMenu(): void {
    this.themeMenuOpen.set(false);
    this.accountMenuOpen.set(!this.accountMenuOpen());
  }

  submitLogin(): void {
    if (!this.loginEmail().trim()) return;
    this.accountService.login(this.loginEmail(), this.loginName());
    this.loginEmail.set('');
    this.loginName.set('');
    this.accountMenuOpen.set(false);
  }

  logout(): void {
    this.accountService.logout();
    this.accountMenuOpen.set(false);
  }

  initials(): string {
    const name = this.accountService.account()?.name;
    if (!name) return '';
    return name
      .trim()
      .split(/\s+/)
      .slice(0, 2)
      .map((part) => part[0]?.toUpperCase())
      .join('');
  }

  currentThemeIcon(): string {
    return this.themes.find((t) => t.id === this.themeService.theme())?.icon ?? 'sun';
  }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    if (!this.elementRef.nativeElement.contains(event.target as Node)) {
      this.themeMenuOpen.set(false);
      this.accountMenuOpen.set(false);
    }
  }
}
