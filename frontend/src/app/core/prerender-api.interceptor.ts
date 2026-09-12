import { HttpInterceptorFn, HttpResponse } from '@angular/common/http';
import { EMPTY, of } from 'rxjs';
import snapshot from '../../links.snapshot.json';

const LINKS_URL = '/api/links';

/**
 * Server-only. During prerendering there is no backend to call, so the link
 * directory is served out of the build-time snapshot instead.
 *
 * Every other /api call — weather, exchange rates, news, nameday — is answered with
 * an empty completed stream rather than an error: the request has to finish so the
 * build cannot hang, and completing without a value leaves those widgets in their
 * placeholder state and never triggers an unhandled error. They fill in normally in
 * the browser. None of that content is worth indexing anyway; the links are.
 */
export const prerenderApiInterceptor: HttpInterceptorFn = (req, next) => {
  const path = req.url.split('?')[0];

  if (!path.startsWith('/api/')) {
    return next(req);
  }

  if (path === LINKS_URL) {
    return of(new HttpResponse({ status: 200, body: snapshot.categories }));
  }

  if (path.startsWith(`${LINKS_URL}/`)) {
    const id = decodeURIComponent(path.slice(LINKS_URL.length + 1));
    const pages = snapshot.pages as Record<string, unknown>;
    return Object.prototype.hasOwnProperty.call(pages, id)
      ? of(new HttpResponse({ status: 200, body: pages[id] }))
      : EMPTY;
  }

  return EMPTY;
};
