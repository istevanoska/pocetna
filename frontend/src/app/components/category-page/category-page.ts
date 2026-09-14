import { Component, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { ApiService } from '../../core/api.service';
import { CategoryPageData } from '../../core/models';
import { categoryIcon } from '../../core/category-icon-map';
import { categoryColor } from '../../core/category-color';
import { faviconUrl } from '../../core/favicon';
import { SeoService, SITE_URL } from '../../core/seo.service';
import { Icon } from '../../shared/icon/icon';

const NOT_FOUND_TITLE = 'Страницата не постои — Почетна.мк';

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
  private seo = inject(SeoService);

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
          this.seo.set({
            title: `${data.title} — Почетна.мк`,
            description: data.description,
            path: `/${id}`,
          });
          // Lets the page show as "Почетна.мк › Образование" in search results.
          this.seo.jsonLd({
            '@context': 'https://schema.org',
            '@type': 'BreadcrumbList',
            itemListElement: [
              { '@type': 'ListItem', position: 1, name: 'Почетна.мк', item: `${SITE_URL}/` },
              { '@type': 'ListItem', position: 2, name: data.title, item: `${SITE_URL}/${id}` },
            ],
          });
        },
        error: () => {
          this.notFound.set(true);
          this.loading.set(false);
          this.seo.set({ title: NOT_FOUND_TITLE, path: `/${id}` });
          this.seo.jsonLd(null);
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
