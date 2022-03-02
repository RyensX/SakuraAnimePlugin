package com.su.mediabox.plugin

import android.view.Gravity
import com.su.mediabox.pluginapi.Constant
import com.su.mediabox.pluginapi.Text.buildRouteActionUrl
import com.su.mediabox.pluginapi.Text.urlEncode
import com.su.mediabox.pluginapi.UI.dp
import com.su.mediabox.pluginapi.v2.been.*
import com.su.mediabox.pluginapi.v2.components.IVideoDetailDataComponent
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

class CustomVideoDetailDataComponent : IVideoDetailDataComponent {
    override suspend fun getAnimeDetailData(
        partUrl: String
    ): Triple<String, String, List<BaseData>> {
        var cover = ""
        var title = ""
        var desc = ""
        var score = -1F
        var upState = ""
        val url = CustomConst.host + partUrl
        val document = JsoupUtil.getDocument(url)
        val tags = mutableListOf<TagData>()

        val details = mutableListOf<BaseData>()

        //番剧头部信息
        val area: Elements = document.getElementsByClass("area")
        for (i in area.indices) {
            val areaChildren = area[i].children()
            for (j in areaChildren.indices) {
                when (areaChildren[j].className()) {
                    "fire l" -> {
                        val fireLChildren =
                            areaChildren[j].select("[class=fire l]")[0].children()
                        for (k in fireLChildren.indices) {
                            when (fireLChildren[k].className()) {
                                "thumb l" -> {
                                    cover = fireLChildren[k]
                                        .select("img").attr("src")
                                }
                                "rate r" -> {   //其他信息，如标签、地区等
                                    val rateR = fireLChildren[k]
                                    title = rateR.select("h1").text()
                                    val sinfo: Elements = rateR.select("[class=sinfo]")
                                    val span: Elements = sinfo.select("span")
                                    //更新状况
                                    upState = sinfo.select("p")
                                        .run { if (size == 1) get(0) else get(1) }
                                        .text()
                                    //年份
                                    val yearEm = span[0].select("a")[0]
                                    val year = Regex("\\d+").find(yearEm.text())?.value
                                    if (year != null)
                                        tags.add(TagData(year).apply {
                                            actionUrl = buildRouteActionUrl(
                                                Constant.ActionUrl.ANIME_CLASSIFY,
                                                yearEm.attr("href"),
                                                "",
                                                year
                                            )
                                        })
                                    //地区
                                    val animeArea = span[1].select("a")
                                    tags.add(TagData(animeArea.text()).apply {
                                        actionUrl = buildRouteActionUrl(
                                            Constant.ActionUrl.ANIME_CLASSIFY,
                                            animeArea.attr("href"),
                                            "",
                                            animeArea.text()
                                        )
                                    })

                                    //类型
                                    val typeElements: Elements = span[2].select("a")
                                    for (l in typeElements.indices) {
                                        tags.add(TagData(typeElements[l].text()).apply {
                                            actionUrl = buildRouteActionUrl(
                                                Constant.ActionUrl.ANIME_CLASSIFY,
                                                typeElements[l].attr("href"),
                                                "",
                                                typeElements[l].text()
                                            )
                                        })
                                    }
                                    //标签
                                    val tagElements: Elements = span[4].select("a")
                                    for (l in tagElements.indices) {
                                        tags.add(TagData(tagElements[l].text()).apply {
                                            actionUrl = buildRouteActionUrl(
                                                Constant.ActionUrl.ANIME_CLASSIFY,
                                                tagElements[l].attr("href"),
                                                "",
                                                tagElements[l].text()
                                            )
                                        })
                                    }

                                    //评分
                                    score = fireLChildren[k].select(".score").select("em")[0].text()
                                        .toFloatOrNull()
                                        ?: -1F
                                }
                                "tabs", "tabs noshow" -> {     //播放列表+header
                                    details.add(
                                        TextData(
                                            fireLChildren[k].select("[class=menu0]")
                                                .select("li").text() + "($upState)",
                                            fontSize = 16F
                                        )
                                    )

                                    details.add(
                                        VideoPlayListData(
                                            parseEpisodes(
                                                fireLChildren[k].select("[class=main0]")
                                                    .select("[class=movurl]")[0]
                                            )
                                        )
                                    )
                                }
                                "info" -> {         //动漫介绍
                                    desc = fireLChildren[k].select("[class=info]").text()
                                }
                                "img" -> {         //系列动漫推荐
                                    val series = parseSeries(fireLChildren[k])
                                    if (series.isNotEmpty()) {
                                        details.add(TextData("系列作品", fontSize = 16F))
                                        details.add(VideoGridData(series))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return Triple(cover, title, mutableListOf<BaseData>().apply {
            add(VideoCover1Data(cover, score = score))
            add(
                TextData(
                    title,
                    fontSize = 20F,
                    gravity = Gravity.CENTER,
                    fontStyle = 1,
                    paddingBottom = 12.dp
                )
            )
            add(TagFlowData(tags))
            add(LongTextData(desc.addDouBanSearch(title), paddingTop = 16.dp))
            addAll(details)
        })
    }

    private fun parseEpisodes(element: Element): List<EpisodeData> {
        val episodeList = mutableListOf<EpisodeData>()
        val elements: Elements = element.select("ul").select("li")
        for (k in elements.indices) {
            val episodeUrl = elements[k].select("a").attr("href")
            episodeList.add(
                EpisodeData(elements[k].select("a").text(), episodeUrl).apply {
                    actionUrl = buildRouteActionUrl(
                        Constant.ActionUrl.ANIME_PLAY,
                        episodeUrl
                    )
                }
            )
        }
        return episodeList
    }

    private fun parseSeries(element: Element): List<VideoGridItemData> {
        val videos = mutableListOf<VideoGridItemData>()
        val elements: Elements = element.select("ul").select("li")
        for (i in elements.indices) {
            val url = elements[i].select("a").attr("href")
            val cover = elements[i].select("a").select("img").attr("src")
            val title = elements[i].select("[class=tname]").select("a").text()
            var episode = ""
            if (elements[i].select("p").size > 1) {
                episode = elements[i].select("p")[1].select("a").text()
            }
            videos.add(VideoGridItemData(title, cover, CustomConst.host + url, episode).apply {
                actionUrl = buildRouteActionUrl(Constant.ActionUrl.ANIME_DETAIL, url)
            })
        }
        return videos
    }

    private fun String.addDouBanSearch(name: String) =
        this + "\n🎞 豆瓣评分 https://m.douban.com/search/?query=${name.urlEncode()}"
}