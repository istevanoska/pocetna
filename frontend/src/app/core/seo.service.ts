import { DOCUMENT, Injectable, inject } from '@angular/core';
import { Meta, Title } from '@angular/platform-browser';

/** Absolute origin of the live site. A canonical URL has to be absolute. */
export const SITE_URL = 'https://pocetna.mk';

/**
 * The homepage's description, and the fallback for any page without one of its own.
 * Kept in sync with the <meta name="description"> in index.html, which is what a page
 * shows before this service runs.
 */
export const SITE_DESCRIPTION =
  'Почетна.мк — пребарување, вести, време, курсна листа и најдобрите македонски сајтови на едно место.';

export interface PageSeo {
  title: string;
  description?: string;
  /** Route path with a leading slash: '/' or '/obrazovanie'. */
  path: string;
}

/**
 * Per-route <title>, description and canonical.
 *
 * Every route used to serve the title and description from index.html, so search
 * engines saw one page repeated. Components call set() with their own; the values
 * land in the prerendered HTML because prerendering runs the same code.
 *
 * The canonical link is written straight into <head> — Angular's Meta service only
 * handles <meta> elements. createElement/querySelector both work in the renderer's
 * server DOM, so this is safe at build time as well as in the browser.
 */
@Injectable({ providedIn: 'root' })
export class SeoService {
  private titleService = inject(Title);
  private meta = inject(Meta);
  private doc = inject(DOCUMENT);

  set({ title, description, path }: PageSeo): void {
    this.titleService.setTitle(title);
    this.meta.updateTag({ name: 'description', content: description || SITE_DESCRIPTION });
    this.canonical(SITE_URL + path);
  }

  private canonical(url: string): void {
    const head = this.doc.head;
    if (!head) return;

    let link = head.querySelector<HTMLLinkElement>('link[rel="canonical"]');
    if (!link) {
      link = this.doc.createElement('link');
      link.setAttribute('rel', 'canonical');
      head.appendChild(link);
    }
    link.setAttribute('href', url);
  }
}
