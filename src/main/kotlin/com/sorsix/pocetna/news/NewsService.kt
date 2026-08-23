package com.sorsix.pocetna.news

import com.sorsix.pocetna.common.TtlCache
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import org.springframework.web.client.body
import org.w3c.dom.Element
import java.time.Duration
import javax.xml.parsers.DocumentBuilderFactory

@Service
class NewsService(private val restClient: RestClient) {

    private val log = LoggerFactory.getLogger(javaClass)
    private val feedUrl = "https://makfax.com.mk/feed/"
    private val sourceName = "Makfax"
    private val cache = TtlCache(ttl = Duration.ofMinutes(15)) { fetchOrThrow() }

    fun getNews(): List<NewsItem> = try {
        cache.get()
    } catch (ex: Exception) {
        log.warn("Failed to fetch news feed", ex)
        emptyList()
    }

    private fun fetchOrThrow(): List<NewsItem> {
        val xml = restClient.get().uri(feedUrl).retrieve().body<String>()
        check(!xml.isNullOrBlank()) { "News feed returned an empty response" }
        return parse(xml)
    }

    private fun parse(xml: String): List<NewsItem> {
        val factory = DocumentBuilderFactory.newInstance()
        factory.isExpandEntityReferences = false
        val doc = factory.newDocumentBuilder().parse(xml.byteInputStream(Charsets.UTF_8))
        val items = doc.getElementsByTagName("item")

        return (0 until items.length).mapNotNull { i ->
            val el = items.item(i) as? Element ?: return@mapNotNull null
            val title = el.getElementsByTagName("title").item(0)?.textContent ?: return@mapNotNull null
            val link = el.getElementsByTagName("link").item(0)?.textContent ?: return@mapNotNull null
            val pubDate = el.getElementsByTagName("pubDate").item(0)?.textContent ?: ""
            val category = el.getElementsByTagName("category").item(0)?.textContent
            NewsItem(title = title.trim(), link = link.trim(), pubDate = pubDate, category = category, source = sourceName)
        }.take(12)
    }
}
