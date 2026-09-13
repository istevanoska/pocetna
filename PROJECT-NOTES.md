# PROJECT-NOTES — pocetna.mk

Working memory for Claude sessions. Keep under ~2 pages; edit rather than append.

## Stack & commands

- **Backend** — Spring Boot 4 (Kotlin), `src/main/kotlin/com/sorsix/pocetna`, port 8080.
  Run: `./gradlew bootRun`
- **Frontend** — Angular 21 (standalone components, signals), `frontend/`, port 4200.
  Run: `cd frontend && npm start`. `proxy.conf.json` forwards `/api/**` to :8080.
- **Type-check without a full build** — `cd frontend && node_modules/.bin/ngc -p tsconfig.app.json --noEmit`.
  Runs Angular's compiler including template type-checking, needs no esbuild, and
  works from Claude's Linux shell. Use it on every frontend change.
- **Link snapshot** — `./gradlew exportLinks` writes `frontend/src/links.snapshot.json`
  from the Kotlin link list. The frontend imports it to prerender; re-run it after
  editing links so the committed copy stays current (the Docker build regenerates it
  regardless, so production can never be stale).
- **Deploy** — `deploy.bat`: pushes to GitHub, then SSHes to `root@178.105.182.242`
  and runs `docker compose up -d --build`. Goce runs it himself. Never run it unasked.

## Layout

- `src/main/kotlin/com/sorsix/pocetna/` — `links/`, `news/`, `weather/`, `exchange/`,
  `nameday/`, `search/`, `account/`, `config/`, `common/TtlCache.kt`.
- `frontend/src/app/` — `app.routes.ts`; `components/{header,footer,sidebar,
  link-directory,category-page,widgets/*}`; `core/` (services, models, icon and
  colour maps); `shared/icon/` (hand-drawn inline SVG set).

## Conventions

- The link directory is a **hardcoded Kotlin list** in `links/LinkDirectoryService.kt`.
  No database, no CMS. Editing a link means editing that file.
- **Links are sorted A–Z within every category.** Cyrillic names sort by their Latin
  transliteration (Магелан under M, Скаут under S) so each list reads as one run.
  Category order on the page is deliberate, not alphabetical.
- A category panel shows **10 links**, then `повеќе...` expands the rest inline —
  unless the category has `hasPage = true`, in which case `повеќе...` links to its
  own page at `/<id>` and there is no expander. One or the other, never both.
- Adding a category page: set `hasPage = true`, add a `CategoryPage` to `pages` in
  the service. No frontend change needed.
- New icons go in `shared/icon/icons.ts` as stroke SVG on a 24×24 viewBox, mapped
  from the backend icon key in `core/category-icon-map.ts`; colours in
  `core/category-color.ts`.
- External data (курсна листа, време, вести) comes from public APIs cached in
  `common/TtlCache` (1h / 30min / 15min).
- User-facing copy is Macedonian Cyrillic, UTF-8. Do not translate or rewrite
  existing copy unless asked.

## Decisions

- 2026-09-08 — Line endings normalised to LF via `* text=auto eol=lf`. `.bat` stays CRLF.
- 2026-09-10 — Шопинг is **not** split into pure-play vs omnichannel. Splitting by
  what a shop sells beats splitting by business model, and the list is too short to
  split at all yet. Candidates when it grows: Техника и електроника, Огласи.
- 2026-09-10 — Vero and Tinex left out of Шопинг: their sites carry promotions and
  store locations but no online shopping.
- 2026-09-10 — Category pages start with Образование only, to prove the pattern
  before adding more.
- 2026-09-11 — Settled: `master` is the real branch (local work and the server
  checkout are both on it). `deploy.bat` pushes `origin master` only, not `main`.
- 2026-09-11 — `deploy.bat`'s SSH key lookup checks `%USERPROFILE%\.ssh\` for
  `id_ed26809` (the actual key filename), then falls back to standard names
  (`id_ed25519`, `id_ecdsa`, `id_rsa`), instead of a hardcoded per-machine path.

- 2026-09-12 — The site is **prerendered to static HTML**, not client-rendered. Chosen
  over runtime SSR (a Node process beside Spring) and over starting the backend during
  the Docker build: no new runtime service, no fragile build, and the Kotlin list stays
  the single source of truth. Hydration is deliberately **off** — `greeting()` and
  `quoteOfTheDay()` differ between build time and view time and would mismatch.

## Changelog

- 2026-09-08 — `3f5dd45` LF normalisation · `34f6019` drop dead fudbal24.mk.
- 2026-09-08 — `488c53e` refresh Технологија (out: meta.mk/tehnologija, php.mk,
  Netokracija; in: USB.mk, Smartportal, Иновативност, Конект.мк, Емитер).
- 2026-09-08 — `b5e3ebc` +18 travel agencies, category sorted A–Z (26 links).
- 2026-09-08 — `8994dcc` 10-link cap with the `повеќе...` expander.
- 2026-09-08 — `0597188` new categories Шопинг and Социјални медиуми, with two new icons.
- 2026-09-10 — `8eadf5b` fill Шопинг to 10 · `b10421a` sort links in every category.
- 2026-09-13 — **Deployed.** Verified live: pocetna.mk serves all 13 categories and the
  h1 as real HTML to a non-JS client; /obrazovanie serves its own page with all six
  sections and the descriptions on a second line. SpaForwardController routes correctly.
- 2026-09-13 — Category pages show each link's description as visible text under the
  name, instead of only in a `title` tooltip. The descriptions were already written in
  `LinkDirectoryService.kt` (positional third argument to `SiteLink`) — all 153 of them.
- 2026-09-13 — Prerendering verified: homepage 13/13 categories, 135 outbound links,
  125/125 link names as text, 124 kB of HTML (was an empty shell); /obrazovanie 6/6
  sections, 28/28 links. Not yet deployed.
- 2026-09-12 — SEO: prerendering. `links/LinksSnapshot.kt` + `exportLinks` task export
  the directory to JSON; `main.server.ts` / `app.config.server.ts` / `app.routes.server.ts`
  add the server build; `prerender-api.interceptor.ts` answers `/api/links` from the
  snapshot; `SpaForwardController` serves the prerendered file per route; Dockerfile
  restructured to 4 stages. Branch `seo-prerender`.
- 2026-09-10 — `a262e3c` routing + category pages; Образование page with Факултети
  and Универзитети; `SpaForwardController` · `500fe64` fix its unclosed comment.

## Backlog (ideas only — never a licence to build)

- Roll the visible link descriptions out to the other category pages as they are added
  (same three lines of markup, styles already in `category-page.scss`).
- **SEO, remaining after prerendering** (audit 2026-09-12, in impact order): per-route
  `<title>`/description/canonical via Angular's `Title`/`Meta` (today every route serves
  `pocetna.mk` and one description); `robots.txt` + a `/sitemap.xml` generated by Spring
  from `LinkDirectoryService`; redirect `www` → apex in the `Caddyfile` (both currently
  serve 200, no canonical); Open Graph + Twitter Card with a 1200×630 image; render link
  descriptions as visible text on each category page (the text already exists); JSON-LD (`WebSite` +
  `SearchAction`, `Organization`, `BreadcrumbList`); real 404 for unknown category ids —
  now more urgent, since prerendering means an unknown URL serves the *full homepage*
  rather than an empty shell, so arbitrary paths are indexable duplicates; rename the `pošta` category id to `posta`
  before it ever gets a page; compress the ~1 MB of PNG logos; self-host the ~150 Google
  favicon requests and the Google Fonts; `zstd` in Caddy; verify in Search Console.
- Render the icon set without `[innerHTML]` (structured path data + a template loop)
  so icons appear in the prerendered HTML instead of only after bootstrap. 40 icons,
  six element types.
- More Образование subcategories; pages for other categories.
- More shops: Ramstore, аптеки, мода. Огласи may deserve its own category.
- `panel__links--cols` is bound in `link-directory.html` for >12 links but has no CSS
  rule anywhere — dead code.
- `category-page.scss` duplicates ~60 lines of panel styling from
  `link-directory.scss` (Angular scopes component styles). Extract to a shared
  partial if more pages appear.
- The panel grid is a CSS **multi-column** layout, so expanding a panel reflows the
  other columns. Moving to a real grid would fix the jump.

## Gotchas

- **Backend changes need a restart.** 4200 hot-reloads, but `/api/**` is Spring;
  Kotlin edits only appear after `bootRun` restarts. A stale `build/classes` once
  made an edited link still show up.
- **Kotlin nests block comments.** A `/*` sequence inside a comment — as in an API
  glob written out in KDoc — opens a nested comment and breaks the file. Cost one
  failed build.
- **`ng serve` misses newly created directories** on Windows. A new component folder
  gives "Cannot find module" until the dev server is restarted.
- **Deep links need the SPA fallback.** In production Spring serves the built
  frontend from `static/`; `config/SpaForwardController` forwards extensionless
  non-API paths to `index.html`. `ng serve` hides this problem in development.
- **`<router-outlet>` renders an empty element** and `.layout` is a grid, so it is
  `display: none` in `app.scss` or it claims a grid cell.
- **Gradle cannot build from Claude's shell** — no network there, and the wrapper
  wants to download its distribution. Backend changes are unverified until Goce runs
  them. (Frontend has `ngc`, see above.)
- **Git lock files** — the mounted folder blocks deletes, and the permission resets
  whenever the connection drops, so `.git/index.lock` and friends pile up. Ask for
  delete permission again and clear them.
- **A second `main` function breaks the Spring Boot plugin.** `LinksSnapshot.kt` added
  one, and `bootJar`/`bootRun` then fail with "Unable to find a single main class from
  the following candidates". Fixed by naming it explicitly in `springBoot { mainClass }`.
  Any future standalone main needs no further change, but do not remove that block.
- **All `@angular/*` runtime packages must share one version floor, and changing them
  means regenerating `package-lock.json`.** `@angular/build` declares
  `peerOptional @angular/ssr@^21.2.20`, and `@angular/platform-server` declares *exact*
  peers on `core`, `common`, `compiler` and `platform-browser` — so ssr forces a
  framework floor and platform-server forces the whole line to move together. npm will
  not lift the framework out of an existing lockfile: caret ranges, exact pins and
  command-line versions all fail with ERESOLVE. The fix is `del package-lock.json`,
  `Remove-Item -Recurse -Force node_modules`, then `npm install`. Never reach for
  `--force` or `--legacy-peer-deps`; they produce a tree that breaks at render time.
- **Nothing may bind `[innerHTML]` during prerendering.** Angular's server DOM has no
  HTML parser, so `setProperty('innerHTML', ...)` throws `NotYetImplemented` — and the
  throw aborts the rest of the surrounding template without failing the build. The
  `Icon` component did this, which silently cost twelve of thirteen category panels and
  every nav label after the first, while the build still reported success. `icon.html`
  now only binds it in the browser. Symptom to watch for: a prerendered page that
  contains the first item of a list and empty markup for the rest.
- **`main.server.ts` must forward the `BootstrapContext`.** The prerenderer creates a
  platform per render and passes it in; `bootstrapApplication(App, config)` without the
  third argument fails the build with NG0401 "Missing Platform" during route extraction,
  after a clean compile. Signature is `(context: BootstrapContext) =>
  bootstrapApplication(App, config, context)`.
- **The frontend will not compile without `@angular/ssr` and the link snapshot.**
  `tsconfig.app.json` includes all of `src/**/*.ts`, so `main.server.ts` is type-checked
  even by `ng serve`. After a fresh clone: `npm install` then `./gradlew exportLinks`.
- **Nothing may touch `document` or `window` at construction time.** Prerendering runs the
  app in Node. `ThemeService.apply()` wrote to `document.documentElement` from its
  constructor and is now guarded with `isPlatformBrowser`. `new Audio()` in
  `radio-widget` is only safe because the widget is not currently rendered.
- **`npm`/`ng build` cannot run from Claude's shell** either: `node_modules` holds the
  Windows esbuild binary. Do not run `npm install` there — it would overwrite it.
