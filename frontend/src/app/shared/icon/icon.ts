import { Component, PLATFORM_ID, computed, inject, input } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { ICONS } from './icons';

@Component({
  selector: 'app-icon',
  imports: [],
  templateUrl: './icon.html',
  styleUrl: './icon.scss',
})
export class Icon {
  private sanitizer = inject(DomSanitizer);

  /**
   * Angular's server-side DOM has no HTML parser, so binding [innerHTML] while
   * prerendering throws NotYetImplemented — and the throw aborts the rest of the
   * surrounding template, which silently cost us twelve of thirteen category panels.
   * The icons are decorative, so the prerendered HTML simply leaves the <svg> empty
   * and the browser fills it in on bootstrap.
   */
  protected isBrowser = isPlatformBrowser(inject(PLATFORM_ID));

  name = input.required<string>();
  size = input(20);

  markup = computed<SafeHtml>(() =>
    this.sanitizer.bypassSecurityTrustHtml(ICONS[this.name()] ?? ICONS['external']),
  );
}
