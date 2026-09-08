# PROJECT-NOTES — pocetna.mk

Working memory for Claude sessions. Keep under ~2 pages; edit rather than append.

## Stack & commands

- **Backend** — Spring Boot 4 (Kotlin), `src/main/kotlin/com/sorsix/pocetna`, port 8080.
  Run: `./gradlew bootRun`
- **Frontend** — Angular 21 (standalone components, signals), `frontend/`, port 4200.
  Run: `cd frontend && npm start`. `proxy.conf.json` forwards `/api/**` to :8080.
- **Deploy** — `deploy.bat`: commits, pushes to GitHub (`main` and `main:master`),
  then SSHes to `root@178.105.182.242` and runs `docker compose up -d --build`.
  Live via Caddy (HTTPS for pocetna.mk). Goce runs this himself, at the end of a
  round of changes. Never run it without being asked.

## Layout

- `src/main/kotlin/com/sorsix/pocetna/` — `links/`, `news/`, `weather/`, `exchange/`,
  `nameday/`, `search/`, `account/`, `common/TtlCache.kt`.
- `frontend/src/app/components/` — `header`, `footer`, `sidebar`, `link-directory`,
  `widgets/{clock,exchange,news,weather}-widget`.
- `frontend/src/app/core/` — services, models, icon/colour maps, theme service.

## Conventions

- The link directory is a **hardcoded Kotlin list** in
  `links/LinkDirectoryService.kt` — no database, no CMS. Editing a link means
  editing that file.
- External data (курсна листа, време, вести) is fetched from public APIs and held in
  `common/TtlCache` (1h / 30min / 15min). No DB for content.
- User-facing copy is Macedonian Cyrillic, UTF-8. Do not translate or rewrite
  existing copy unless asked.

## Decisions

- 2026-09-08 — Line endings normalised to LF via `* text=auto eol=lf` in
  `.gitattributes`. The working tree had drifted to CRLF while the repo stored LF,
  making all 96 files show as modified. `.bat` files stay CRLF deliberately.

## Changelog

- 2026-09-08 — `3f5dd45` normalise line endings to LF (`.gitattributes`).
- 2026-09-08 — `34f6019` remove dead `fudbal24.mk` link from the Спорт category;
  the site no longer exists. 9 sport links → 8.

## Backlog (ideas only — not approved)

- Health-check the remaining links in the directory for other dead sites.

## Gotchas

- **Backend changes need a restart.** Angular on 4200 hot-reloads, but `/api/**` is
  served by Spring Boot; Kotlin edits only appear after `bootRun` is restarted.
  A stale `build/classes` is what made an edited link still show up.
- **Gradle cannot build from Claude's shell** on the laptop — that shell has no
  network and the Gradle wrapper wants to download its distribution. Kotlin changes
  cannot be compile-verified from a session; Goce runs the build.
- **Git lock files** — the mounted folder blocks deletes by default, so a stale
  `.git/index.lock` can jam Git. Ask for delete permission on the project folder.
