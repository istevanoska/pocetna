import { Component, inject } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Header } from './components/header/header';
import { Sidebar } from './components/sidebar/sidebar';
import { Footer } from './components/footer/footer';
import { ThemeService } from './core/theme.service';

@Component({
  selector: 'app-root',
  imports: [Header, RouterOutlet, Sidebar, Footer],
  templateUrl: './app.html',
  styleUrl: './app.scss',
})
export class App {
  private themeService = inject(ThemeService);
}
