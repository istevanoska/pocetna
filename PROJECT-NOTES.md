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

## Changelog

- 2026-09-08 — `3f5dd45` LF normalisation · `34f6019` drop dead fudbal24.mk.
- 2026-09-08 — `488c53e` refresh Технологија (out: meta.mk/tehnologija, php.mk,
  Netokracija; in: USB.mk, Smartportal, Иновативност, Конект.мк, Емитер).
- 2026-09-08 — `b5e3ebc` +18 travel agencies, category sorted A–Z (26 links).
- 2026-09-08 — `8994dcc` 10-link cap with the `повеќе...` expander.
- 2026-09-08 — `0597188` new categories Шопинг and Социјални медиуми, with two new icons.
- 2026-09-10 — `8eadf5b` fill Шопинг to 10 · `b10421a` sort links in every category.
- 2026-09-10 — `a262e3c` routing + category pages; Образование page with Факултети
  and Универзитети; `SpaForwardController` · `500fe64` fix its unclosed comment.

## Backlog (ideas only — never a licence to build)

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
- **`npm`/`ng build` cannot run from Claude's shell** either: `node_modules` holds the
  Windows esbuild binary. Do not run `npm install` there — it would overwrite it.
