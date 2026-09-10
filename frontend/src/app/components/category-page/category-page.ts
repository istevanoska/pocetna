import { Component, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { ApiService } from '../../core/api.service';
import { CategoryPageData } from '../../core/models';
import { categoryIcon } from '../../core/category-icon-map';
import { categoryColor } from '../../core/category-color';
import { faviconUrl } from '../../core/favicon';
import { Icon } from '../../shared/icon/icon';

@Component({
  selector: 'app-category-page',
  host: { class: 'layout__main' },
  imports: [Icon, RouterLink],
  templateUrl: './category-page.html',
  styleUrl: './category-page.scss',
})
export class CategoryPage {
  private api = inject(ApiService);
  private route = inject(ActivatedRoute);

  page = signal<CategoryPageData | null>(null);
  loading = signal(true);
  notFound = signal(false);

  constructor() {
    this.route.paramMap.pipe(takeUntilDestroyed()).subscribe((params) => {
      const id = params.get('id') ?? '';
      this.loading.set(true);
      this.notFound.set(false);
      this.page.set(null);

      this.api.getCategoryPage(id).subscribe({
        next: (data) => {
          this.page.set(data);
          this.loading.set(false);
        },
        error: () => {
          this.notFound.set(true);
          this.loading.set(false);
        },
      });
    });
  }

  icon(name: string): string {
    return categoryIcon(name);
  }

  catColor(id: string): string {
    return categoryColor(id);
  }

  favicon(url: string): string {
    return faviconUrl(url);
  }
}
