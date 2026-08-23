import { Component, inject, OnInit, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { ApiService } from '../../../core/api.service';
import { NewsItem } from '../../../core/models';
import { Icon } from '../../../shared/icon/icon';

@Component({
  selector: 'app-news-widget',
  imports: [DatePipe, Icon],
  templateUrl: './news-widget.html',
  styleUrl: './news-widget.scss',
})
export class NewsWidget implements OnInit {
  private api = inject(ApiService);

  items = signal<NewsItem[]>([]);
  loading = signal(true);

  ngOnInit(): void {
    this.api.getNews().subscribe({
      next: (items) => {
        this.items.set(items);
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });
  }
}
