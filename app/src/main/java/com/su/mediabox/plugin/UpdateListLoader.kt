package com.su.mediabox.plugin

import android.graphics.Color
import android.graphics.Typeface
import android.util.Log
import com.su.mediabox.pluginapi.UI.dp
import com.su.mediabox.pluginapi.v2.action.CustomDataAction
import com.su.mediabox.pluginapi.v2.action.DetailAction
import com.su.mediabox.pluginapi.v2.been.BaseData
import com.su.mediabox.pluginapi.v2.been.TextData
import com.su.mediabox.pluginapi.v2.been.ViewPagerData
import org.jsoup.select.Elements
import java.util.*

class UpdateListLoader : CustomDataAction.Loader {

    private val days = mutableListOf<String>()
    private lateinit var updateList: Elements

    override suspend fun loadData(page: Int): List<BaseData>? {
        Log.d("抓取更新数据", "page=$page")
        if (page != 1)
            return null
        val doc = JsoupUtil.getDocument(CustomConst.host)
            .select("[class=side r]").select("[class=bg]")
            .first() ?: return null
        //星期
        days.clear()
        doc.getElementsByClass("tag").first()?.getElementsByTag("span")?.forEach {
            Log.d("星期", it.text())
            days.add(it.text())
        }
        //当前星期
        val cal: Calendar = Calendar.getInstance()
        cal.timeInMillis = System.currentTimeMillis()
        val w = cal.get(Calendar.DAY_OF_WEEK) - 2
        Log.d("当前星期", "$w ${days[w]}")
        //更新列表元素
        updateList = doc.getElementsByClass("tlist").first()?.children() ?: return null

        val updateLoader = object : ViewPagerData.PageLoader {
            override fun pageName(page: Int): String = days[page]

            override suspend fun loadData(page: Int): List<BaseData> {
                Log.d("获取更新列表", "$page ${updateList[page]}")
                //ul元素
                val target = updateList[page]
                val ups = mutableListOf<TextData>()
                for (em in target.children()) {
                    val titleEm = em.select("[title]").first()
                    val title = titleEm?.text()
                    val episode = em.select("[target=_blank]").first()?.text()
                    val url = titleEm?.attr("href")
                    if (!title.isNullOrBlank() && !episode.isNullOrBlank() && !url.isNullOrBlank()) {
                        Log.d("添加更新", "$title $episode $url")
                        ups.add(
                            TextData(
                                "${ups.size + 1}. $title $episode",
                                fontStyle = Typeface.BOLD,
                                fontColor = Color.BLACK
                            ).apply {
                                paddingTop = 24.dp
                                paddingLeft = 16.dp
                                paddingRight = 16.dp
                                paddingBottom = 0

                                action = DetailAction.obtain(url)
                            })
                    }
                }
                return ups
            }
        }

        return listOf(ViewPagerData(mutableListOf<ViewPagerData.PageLoader>().apply {
            repeat(7) {
                add(updateLoader)
            }
        }, w))
    }
}