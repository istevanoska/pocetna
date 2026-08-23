# Почетна.мк

Македонска почетна страница во стилот на startpagina.nl — пребарување поврзано со Google,
именик со категоризирани македонски сајтови, и виџети за курсна листа, време, датум/имендар
и најнови вести.

## Архитектура

- **Backend** — Spring Boot 4 (Kotlin), `src/main/kotlin/com/sorsix/pocetna`. Нема база на
  податоци; секој извор (курсна листа, време, вести) се вчитува од надворешен јавен сервис и
  се кешира во меморија (`common/TtlCache`) за да не се удира секој пат во надворешните API-ja.
- **Frontend** — Angular 21 (standalone компоненти, TypeScript, signals), во `frontend/`.

## Стартување

### 1. Backend (порт 8080)

```bash
./gradlew bootRun
```

### 2. Frontend (порт 4200, со proxy кон backend-от)

```bash
cd frontend
npm install
npm start
```

Отвори `http://localhost:4200`. `proxy.conf.json` ги проследува повиците кон `/api/**` до
`http://localhost:8080`.

> Ако портите 8080 или 4200 ти се веќе зафатени од друга апликација, стартувај со
> `./gradlew bootRun --args='--server.port=8081'` односно `npm start -- --port 4300`, и промени
> го `target` во `frontend/proxy.conf.json` соодветно.

## Поврзување со Google пребарување

Search-от работи преку **Google Programmable Search Engine (Custom Search JSON API)** и
резултатите се прикажуваат директно на страницата. За ова ти требаат бесплатен API клуч и
Search Engine ID:

1. Оди на https://programmablesearchengine.google.com/ и создади нов "Search engine" со
   опција "Search the entire web".
2. Копирај го неговиот **Search engine ID** (cx).
3. Оди на https://console.cloud.google.com/apis/credentials, овозможи "Custom Search API" и
   создади **API key**.
4. Постави ги како environment променливи пред да го стартуваш backend-от:

   ```bash
   export GOOGLE_API_KEY=твојот-клуч
   export GOOGLE_CSE_ID=твојот-cx
   ./gradlew bootRun
   ```

   (Бесплатниот план дозволува 100 пребарувања на ден.)

Ако клучевите не се поставени, search-от автоматски паѓа назад на обично отворање на
Google резултати во нов таб — сайтот работи и без нив, само без inline резултати.

## Виџети и извори на податоци

| Виџет | Извор | Забелешка |
|---|---|---|
| Курсна листа | НБРМ (`nbrm.mk/services/ExchangeRates.asmx`) | Средни курсеви, кеш 1 час |
| Време | Open-Meteo (`api.open-meteo.com`) | 12 градови, кеш 30 мин |
| Најнови вести | RSS од `makfax.com.mk` | Кеш 15 мин |
| Датум / Имендар | Локална, рачно избрана листа | Само добро познати датуми — не е целосен календар |

## Теми

Три теми (Светла / Темна / Македонска) се менуваат од копчето до лого-то и се паметат во
`localStorage` на уредот.

## „Најави се“

Тоа е чисто козметична персонализација на клиентска страна (име се памети во `localStorage`
на уредот) — нема лозинка, нема сервер, нема вистинска автентикација. Ако сакаш вистински
кориснички системи (регистрација, лозинки, сесии), тоа бара посебен backend со Spring
Security и база на податоци — кажи ми ако сакаш да го додадам.
