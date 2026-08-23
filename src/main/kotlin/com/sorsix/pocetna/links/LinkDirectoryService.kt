package com.sorsix.pocetna.links

import org.springframework.stereotype.Service

@Service
class LinkDirectoryService {

    private val categories: List<LinkCategory> = listOf(
        LinkCategory(
            id = "vesti", title = "Вести", icon = "newspaper",
            links = listOf(
                SiteLink("МИА", "https://mia.mk", "Македонска информативна агенција"),
                SiteLink("Телма", "https://telma.com.mk", "Национална телевизија"),
                SiteLink("Канал 5", "https://kanal5.com.mk", "Национална телевизија"),
                SiteLink("МТВ", "https://mrt.com.mk", "Македонска радио телевизија"),
                SiteLink("Сител", "https://sitel.com.mk", "Национална телевизија"),
                SiteLink("Makfax", "https://makfax.com.mk", "Информативна агенција"),
                SiteLink("Мета.мк", "https://meta.mk", "Онлајн медиум"),
                SiteLink("Слободен печат", "https://slobodenpecat.mk", "Дневен весник"),
                SiteLink("Нова Македонија", "https://novamakedonija.com.mk", "Дневен весник"),
                SiteLink("24Вести", "https://24vesti.mk", "Онлајн медиум"),
                SiteLink("А1он", "https://a1on.mk", "Информативен портал"),
                SiteLink("Плусинфо", "https://plusinfo.mk", "Информативен портал"),
                SiteLink("Прес24", "https://press24.mk", "Информативен портал"),
                SiteLink("Фокус", "https://fokus.mk", "Неделен весник"),
                SiteLink("Република", "https://republika.mk", "Информативен портал"),
                SiteLink("МКД", "https://mkd.mk", "Информативен портал"),
                SiteLink("Утрински весник", "https://utrinskivesnik.mk", "Дневен весник"),
                SiteLink("Сакам да кажам", "https://sdk.mk", "Информативен портал"),
            ),
        ),
        LinkCategory(
            id = "sport", title = "Спорт", icon = "trophy",
            links = listOf(
                SiteLink("Сител Спорт", "https://sitel.com.mk/sport", "Спортски вести"),
                SiteLink("Фудбал 24", "https://fudbal24.mk", "Фудбалски вести"),
                SiteLink("Сакамфудбал", "https://sakamfudbal.mk", "Фудбалски портал"),
                SiteLink("ФФМ", "https://ffm.mk", "Фудбалска федерација на Македонија"),
                SiteLink("МОК", "https://mok.org.mk", "Македонски олимписки комитет"),
                SiteLink("Гол.мк", "https://gol.mk", "Спортски портал"),
                SiteLink("SportSport.mk", "https://sportsport.mk", "Спортски вести"),
                SiteLink("Спортска Мрежа", "https://sportskamreza.mk", "Спортски портал"),
                SiteLink("КФСМ", "https://kfsm.mk", "Кошаркарска федерација на Северна Македонија"),
            ),
        ),
        LinkCategory(
            id = "vlada", title = "Власт и институции", icon = "building-columns",
            links = listOf(
                SiteLink("Влада на РСМ", "https://vlada.mk", "Влада на Република Северна Македонија"),
                SiteLink("Собрание", "https://sobranie.mk", "Собрание на РСМ"),
                SiteLink("Претседател", "https://pretsedatel.mk", "Кабинет на претседателот"),
                SiteLink("УЈП", "https://ujp.gov.mk", "Управа за јавни приходи"),
                SiteLink("МВР", "https://mvr.gov.mk", "Министерство за внатрешни работи"),
                SiteLink("е-Управа", "https://uslugi.gov.mk", "Државни е-услуги"),
                SiteLink("Агенција за вработување", "https://av.gov.mk", "АВРСМ"),
                SiteLink("Централен регистар", "https://crm.com.mk", "Централен регистар на РСМ"),
                SiteLink("Државен завод за статистика", "https://stat.gov.mk", "Статистички податоци"),
                SiteLink("Народен правобранител", "https://ombudsman.mk", "Омбудсман на РСМ"),
                SiteLink("Уставен суд", "https://ustavensud.mk", "Уставен суд на РСМ"),
            ),
        ),
        LinkCategory(
            id = "banki", title = "Банки и финансии", icon = "landmark",
            links = listOf(
                SiteLink("НБРМ", "https://nbrm.mk", "Народна банка на РСМ"),
                SiteLink("Комерцијална банка", "https://kb.mk", "Комерцијална банка АД Скопје"),
                SiteLink("Стопанска банка", "https://stb.com.mk", "Стопанска банка АД Скопје"),
                SiteLink("НЛБ Банка", "https://nlb.mk", "НЛБ Банка АД Скопје"),
                SiteLink("Халк Банка", "https://halkbank.mk", "Халк Банка АД Скопје"),
                SiteLink("Македонска берза", "https://mse.mk", "Македонска берза на хартии од вредност"),
                SiteLink("ПроКредит Банка", "https://procreditbank.com.mk", "ПроКредит Банка АД Скопје"),
                SiteLink("Шпаркасе Банка", "https://sparkasse.mk", "Шпаркасе Банка Македонија"),
                SiteLink("УНИБанка", "https://unibank.com.mk", "УНИБанка АД Скопје"),
            ),
        ),
        LinkCategory(
            id = "obrazovanie", title = "Образование", icon = "graduation-cap",
            links = listOf(
                SiteLink("УКИМ", "https://ukim.edu.mk", "Универзитет Св. Кирил и Методиј - Скопје"),
                SiteLink("УКЛО", "https://uklo.edu.mk", "Универзитет Св. Климент Охридски - Битола"),
                SiteLink("УГД", "https://ugd.edu.mk", "Универзитет Гоце Делчев - Штип"),
                SiteLink("Министерство за образование", "https://mon.gov.mk", "МОН"),
                SiteLink("Еду.мк", "https://edu.mk", "Образовен портал"),
                SiteLink("МСУ", "https://msu.edu.mk", "Меѓународен славјански универзитет"),
                SiteLink("Американ колеџ Скопје", "https://aac.edu.mk", "American College Skopje"),
                SiteLink("Биро за развој на образованието", "https://bro.gov.mk", "БРО"),
            ),
        ),
        LinkCategory(
            id = "biznis", title = "Бизнис и економија", icon = "briefcase",
            links = listOf(
                SiteLink("Капитал", "https://kapital.mk", "Бизнис портал"),
                SiteLink("Бизнис Инфо", "https://biznisinfo.mk", "Бизнис портал"),
                SiteLink("Економија и бизнис", "https://ebizmagazine.com", "Магазин"),
                SiteLink("Стопанска комора", "https://mchamber.mk", "Стопанска комора на Македонија"),
                SiteLink("Фактор", "https://faktor.mk", "Портал за економија и бизнис"),
                SiteLink("Лидер", "https://lider.mk", "Бизнис портал"),
                SiteLink("БизнисМрежа", "https://biznismreza.mk", "База на бизнис информации"),
            ),
        ),
        LinkCategory(
            id = "tehnologija", title = "Технологија", icon = "microchip",
            links = listOf(
                SiteLink("Мета.мк Технологија", "https://meta.mk/tehnologija", "Технолошки вести"),
                SiteLink("ИТ.мк", "https://it.mk", "IT портал"),
                SiteLink("PHP.mk", "https://php.mk", "Програмерска заедница"),
                SiteLink("Netokracija Balkans", "https://netokracija.rs", "Технолошки медиум за регионот"),
            ),
        ),
        LinkCategory(
            id = "zabava", title = "Забава и ТВ", icon = "tv",
            links = listOf(
                SiteLink("ТВ Програма", "https://tvprogram.mk", "Телевизиски програми"),
                SiteLink("Star.mk", "https://star.mk", "Забавен портал"),
                SiteLink("Sakam.mk", "https://sakam.mk", "Лајфстајл портал"),
                SiteLink("Fakulteti.mk", "https://fakulteti.mk", "Магазин за млади"),
            ),
        ),
        LinkCategory(
            id = "mapi", title = "Карти, превоз и услуги", icon = "map-location-dot",
            links = listOf(
                SiteLink("Google Maps", "https://maps.google.com", "Карти и навигација"),
                SiteLink("АМСМ", "https://amsm.mk", "Автомото сојуз на Македонија"),
                SiteLink("ЈСП Скопје", "https://jsp.com.mk", "Јавен градски превоз - Скопје"),
                SiteLink("Македонски железници", "https://mztransport.mk", "Железнички превоз"),
                SiteLink("Скопски аеродроми", "https://airports.com.mk", "Аеродроми во РСМ"),
            ),
        ),
        LinkCategory(
            id = "turizam", title = "Туристички агенции", icon = "plane",
            links = listOf(
                SiteLink("Fibula Air Travel", "https://www.fibula.com.mk/", "Туристичка агенција"),
                SiteLink("Aurora", "https://aurora.mk/", "Туристичка агенција"),
                SiteLink("Магелан Травел", "https://magelantravel.mk/", "Туристичка агенција"),
                SiteLink("JK Travel", "https://jk.mk/", "Туристичка агенција"),
                SiteLink("Savana Travel", "https://www.savana.travel/", "Туристичка агенција"),
                SiteLink("Jungle Tribe", "https://www.jungletribe.mk/", "Туристичка агенција"),
                SiteLink("Puzzle Travel", "https://puzzlegroup.mk/", "Туристичка агенција"),
                SiteLink("Balkan Fun", "https://balkanfun.mk/", "Туристичка агенција"),
            ),
        ),
        LinkCategory(
            id = "pošta", title = "Пошта, комунални и е-услуги", icon = "envelope",
            links = listOf(
                SiteLink("Пошта на Северна Македонија", "https://posta.com.mk", "Јавно претпријатие"),
                SiteLink("ЕВН", "https://evn.mk", "Дистрибуција на електрична енергија"),
                SiteLink("АД ЕСМ", "https://esm.com.mk", "Електрани на Северна Македонија"),
                SiteLink("Македонски телеком", "https://telekom.mk", "Телекомуникации"),
                SiteLink("А1 Македонија", "https://a1.mk", "Телекомуникации"),
            ),
        ),
    )

    fun getCategories(): List<LinkCategory> = categories
}
