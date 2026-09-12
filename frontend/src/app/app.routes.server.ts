import { RenderMode, ServerRoute } from '@angular/ssr';
import snapshot from '../links.snapshot.json';

/**
 * Which routes are turned into static HTML at build time.
 *
 * The parameterised route covers the category pages, so the ids come from the
 * build-time snapshot of the Kotlin link directory — a category gets a prerendered
 * page exactly when it has one in the backend.
 */
export const serverRoutes: ServerRoute[] = [
  {
    path: '',
    renderMode: RenderMode.Prerender,
  },
  {
    path: ':id',
    renderMode: RenderMode.Prerender,
    getPrerenderParams: async () => Object.keys(snapshot.pages).map((id) => ({ id })),
  },
];
