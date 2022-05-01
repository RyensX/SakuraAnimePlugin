package com.su.sakuraanimeplugin.plugin.deprecateds

import com.su.mediabox.pluginapi.Constant
import com.su.mediabox.pluginapi.been.*
import com.su.mediabox.pluginapi.components.IPlayComponent
import com.su.sakuraanimeplugin.plugin.components.CustomConst
import com.su.sakuraanimeplugin.plugin.util.JsoupUtil
import com.su.sakuraanimeplugin.plugin.util.ParseHtmlUtil
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

@Deprecated("将在下一个Release重新整理实现")
class CustomPlayModel : IPlayComponent {

    private suspend fun getVideoRawUrl(e: Element): String {
        val div = e.select("[class=area]").select("[class=bofang]")[0].children()
        val rawUrl = div.attr("data-vid")
        return when {
            rawUrl.endsWith("\$mp4", true) -> rawUrl.replace("\$mp4", "").replace("\\", "/")
            rawUrl.endsWith("\$url", true) -> rawUrl.replace("\$url", "")
            rawUrl.endsWith("\$hp", true) -> {
                JsoupUtil.getDocument("http://tup.yhdm.so/hp.php?url=${rawUrl.substringBefore("\$hp")}")
                    .body().select("script")[0].toString()
                    .substringAfter("video: {")
                    .substringBefore("}")
                    .split(",")[0]
                    .substringAfter("url: \"")
                    .substringBefore("\"")
            }
            rawUrl.endsWith("\$qzz", true) -> rawUrl
            else -> ""
        }
    }

    override suspend fun getPlayData(
        partUrl: String,
        animeEpisodeDataBean: AnimeEpisodeDataBean
    ): Triple<ArrayList<IAnimeDetailBean>, ArrayList<AnimeEpisodeDataBean>, PlayBean> {
        val playBeanDataList: ArrayList<IAnimeDetailBean> = ArrayList()
        val episodesList: ArrayList<AnimeEpisodeDataBean> = ArrayList()
        val title = AnimeTitleBean("", "", "")
        val episode = AnimeEpisodeDataBean("", "", "")
        val url = CustomConst.host + partUrl
        val document = JsoupUtil.getDocument(url)
        val children: Elements = document.allElements

        for (i in children.indices) {
            when (children[i].className()) {
                "play" -> {
                    animeEpisodeDataBean.videoUrl = getVideoRawUrl(children[i])
                }
                "area" -> {
                    val areaChildren = children[i].children()
                    for (j in areaChildren.indices) {
                        when (areaChildren[j].className()) {
                            "gohome l" -> {        //标题
                                title.title = areaChildren[j].select("h1")
                                    .select("a").text()
                                title.actionUrl = areaChildren[j].select("h1")
                                    .select("a").attr("href")
                                episode.title = areaChildren[j].select("h1")
                                    .select("span").text().replace("：", "")
                                animeEpisodeDataBean.title = episode.title
                            }
                            "botit" -> {
                                playBeanDataList.add(
                                    AnimeDetailBean(
                                        Constant.ViewHolderTypeString.HEADER_1,
                                        "",
                                        ParseHtmlUtil.parseBotit(areaChildren[j]),
                                        ""
                                    )
                                )
                            }
                            "movurls" -> {      //集数列表
                                episodesList.addAll(
                                    ParseHtmlUtil.parsePlayMovurls(
                                        areaChildren[j],
                                        animeEpisodeDataBean
                                    )
                                )
                                playBeanDataList.add(
                                    AnimeDetailBean(
                                        Constant.ViewHolderTypeString.HORIZONTAL_RECYCLER_VIEW_1,
                                        "",
                                        "",
                                        "",
                                        episodesList
                                    )
                                )
                            }
                            "imgs" -> {
                                playBeanDataList.addAll(
                                    ParseHtmlUtil.parseImg(areaChildren[j], url)
                                )
                            }
                        }
                    }
                }
            }
        }
        val playBean = PlayBean("", "", title, episode, playBeanDataList)
        return Triple(playBeanDataList, episodesList, playBean)
    }

    override suspend fun refreshAnimeEpisodeData(
        partUrl: String,
        animeEpisodeDataBean: AnimeEpisodeDataBean
    ): Boolean {
        val document = JsoupUtil.getDocument(CustomConst.host + partUrl)
        val children: Elements = document.select("body")[0].children()
        for (i in children.indices) {
            when (children[i].className()) {
                "play" -> {
                    animeEpisodeDataBean.actionUrl = partUrl
                    animeEpisodeDataBean.videoUrl = getVideoRawUrl(children[i])
                    return true
                }
            }
        }
        return false
    }

    override suspend fun getAnimeEpisodeUrlData(partUrl: String): String? {
        val document = JsoupUtil.getDocument(CustomConst.host + partUrl)
        val children: Elements = document.select("body")[0].children()
        for (i in children.indices) {
            when (children[i].className()) {
                "play" -> {
                    return getVideoRawUrl(children[i])
                }
            }
        }
        return null
    }

}