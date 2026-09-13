import { HttpInterceptorFn, HttpResponse } from '@angular/common/http';
import { EMPTY, delay, of } from 'rxjs';
import snapshot from '../../links.snapshot.json';

const LINKS_URL = '/api/links';

/**
 * Server-only. During prerendering there is no backend to call, so the link
 * directory is served out of the build-time snapshot instead.
 *
 * The response is delayed by a tick because a real HTTP response never arrives
 * synchronously, and emitting one inside ngOnInit writes the categories signal in the
 * middle of the first change-detection pass. This is defensive rather than a fix for
 * any observed bug: the half-rendered pages we saw were caused by the icon component
 * throwing during prerendering, not by this. The request counts towards SSR stability
 * while it is in flight, so the build still waits for it either way.
 *
 * Every other API call — weather, exchange rates, news, nameday — is answered with an
 * empty completed stream: the request has to finish so the build cannot hang, and
 * completing without a value leaves those widgets in their placeholder state without
 * triggering an unhandled error. They fill in normally in the browser, and none of
 * that content is worth indexing. The links are.
 */
export const prerenderApiInterceptor: HttpInterceptorFn = (req, next) => {
  const path = req.url.split('?')[0];

  if (!path.startsWith('/api/')) {
    return next(req);
  }

  if (path === LINKS_URL) {
    return of(new HttpResponse({ status: 200, body: snapshot.categories })).pipe(delay(0));
  }

  if (path.startsWith(`${LINKS_URL}/`)) {
    const id = decodeURIComponent(path.slice(LINKS_URL.length + 1));
    const pages = snapshot.pages as Record<string, unknown>;
    if (!Object.prototype.hasOwnProperty.call(pages, id)) {
      return EMPTY;
    }
    return of(new HttpResponse({ status: 200, body: pages[id] })).pipe(delay(0));
  }

  return EMPTY;
};
