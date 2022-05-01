package com.su.sakuraanimeplugin.plugin.components

import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import com.su.mediabox.pluginapi.UI.dp
import com.su.mediabox.pluginapi.v2.been.BaseData
import com.su.mediabox.pluginapi.v2.been.SimpleTextData
import com.su.mediabox.pluginapi.v2.been.TagData
import com.su.mediabox.pluginapi.v2.been.ViewPagerData
import com.su.mediabox.pluginapi.v2.components.ICustomPageComponent
import com.su.sakuraanimeplugin.plugin.actions.CustomAction
import com.su.sakuraanimeplugin.plugin.util.JsoupUtil
import com.su.sakuraanimeplugin.plugin.util.ParseHtmlUtil
import org.jsoup.select.Elements

class RankPageDataComponent : ICustomPageComponent {

    override val pageName = "排行榜"
    override fun menus() = mutableListOf(CustomAction())

    override suspend fun getData(page: Int): List<BaseData>? {
        if (page != 1)
            return null
        val url = CustomConst.host
        val doc = JsoupUtil.getDocument(url)

        //排行榜，包含两项
        //一周排行榜
        val weekRank =
            doc.getElementsByClass("pics")
                .first()?.let {
                    object : ViewPagerData.PageLoader {
                        override fun pageName(page: Int): String {
                            return "一周排行"
                        }

                        override suspend fun loadData(page: Int): List<BaseData> {
                            return ParseHtmlUtil.parseSearchEm(it, url)
                        }
                    }
                }
        //动漫排行榜
        val totalRank = object : ViewPagerData.PageLoader {
            override fun pageName(page: Int): String {
                return "总排行"
            }

            override suspend fun loadData(page: Int): List<BaseData> {
                return getTotalRankData()
            }
        }
        return listOf(ViewPagerData(mutableListOf(weekRank!!, totalRank)).apply {
            layoutConfig = BaseData.LayoutConfig(
                itemSpacing = 0,
                listLeftEdge = 0,
                listRightEdge = 0
            )
        })
    }

    private val rankTop3Color = intArrayOf(
        Color.parseColor("#E4CD01"),
        Color.parseColor("#9E9E9E"),
        Color.parseColor("#B77231")
    )

    private suspend fun getTotalRankData(): List<BaseData> {
        val const = CustomConst
        val document = JsoupUtil.getDocument(CustomConst.host + CustomConst.ANIME_RANK)
        val areaChildren: Elements = document.select("[class=area]")[0].children()
        val rankList = mutableListOf<SimpleTextData>()
        for (i in areaChildren.indices) {
            when (areaChildren[i].className()) {
                "topli" -> {
                    rankList.addAll(ParseHtmlUtil.parseTopli(areaChildren[i]))
                }
            }
        }
        val rankViewList = mutableListOf<BaseData>()
        rankList.forEachIndexed { index, rank ->
            rankViewList.add(TagData("${index + 1}", rankTop3Color.getOrNull(index)).apply {
                spanSize = 1
                paddingLeft = 6.dp
            })
            rankViewList.add(rank.apply {
                spanSize = 7
                gravity = Gravity.CENTER_VERTICAL
                fontStyle = Typeface.BOLD
                fontColor = Color.BLACK

                paddingTop = 6.dp
                paddingBottom = 6.dp
                paddingLeft = 0.dp
                paddingRight = 0.dp
            })
        }
        //  rankViewList[0].layoutConfig = BaseData.LayoutConfig(listLeftEdge = 14.dp)
        return rankViewList
    }
}