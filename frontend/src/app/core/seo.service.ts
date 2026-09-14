import { DOCUMENT, Injectable, inject } from '@angular/core';
import { Meta, Title } from '@angular/platform-browser';

/** Absolute origin of the live site. Canonical and og: URLs have to be absolute. */
export const SITE_URL = 'https://pocetna.mk';

/**
 * The homepage's description, and the fallback for any page without one of its own.
 * Kept in sync with the <meta name="description"> in index.html, which is what a page
 * shows before this service runs.
 */
export const SITE_DESCRIPTION =
  'Почетна.мк — пребарување, вести, време, курсна листа и најдобрите македонски сајтови на едно место.';

/** The share card: 1200×630, served from frontend/public/. */
const OG_IMAGE = `${SITE_URL}/og.png`;
const OG_IMAGE_ALT = 'Почетна.мк — сите македонски сајтови на едно место';

export interface PageSeo {
  title: string;
  description?: string;
  /** Route path with a leading slash: '/' or '/obrazovanie'. */
  path: string;
}

/**
 * Per-route <title>, description, canonical, and the Open Graph / Twitter Card tags
 * that decide what Viber, Messenger, Facebook, LinkedIn, X and Slack show when someone
 * shares a link.
 *
 * Every route used to serve the title and description from index.html, so search
 * engines saw one page repeated. Components call set() with their own; the values
 * land in the prerendered HTML because prerendering runs the same code — which matters
 * more for the share cards than for search, since no social scraper runs JavaScript.
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
    const text = description || SITE_DESCRIPTION;
    const url = SITE_URL + path;

    this.titleService.setTitle(title);
    this.meta.updateTag({ name: 'description', content: text });
    this.canonical(url);

    // Open Graph. og:image:width/height let a scraper lay the card out before it has
    // fetched the image; og:type stays "website" for every page here.
    this.meta.updateTag({ property: 'og:title', content: title });
    this.meta.updateTag({ property: 'og:description', content: text });
    this.meta.updateTag({ property: 'og:url', content: url });
    this.meta.updateTag({ property: 'og:type', content: 'website' });
    this.meta.updateTag({ property: 'og:site_name', content: 'Почетна.мк' });
    this.meta.updateTag({ property: 'og:locale', content: 'mk_MK' });
    this.meta.updateTag({ property: 'og:image', content: OG_IMAGE });
    this.meta.updateTag({ property: 'og:image:width', content: '1200' });
    this.meta.updateTag({ property: 'og:image:height', content: '630' });
    this.meta.updateTag({ property: 'og:image:alt', content: OG_IMAGE_ALT });

    // X reads og: for most of this, but not for the card size — without
    // summary_large_image it renders a small square thumbnail instead of the banner.
    this.meta.updateTag({ name: 'twitter:card', content: 'summary_large_image' });
    this.meta.updateTag({ name: 'twitter:title', content: title });
    this.meta.updateTag({ name: 'twitter:description', content: text });
    this.meta.updateTag({ name: 'twitter:image', content: OG_IMAGE });
    this.meta.updateTag({ name: 'twitter:image:alt', content: OG_IMAGE_ALT });
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
