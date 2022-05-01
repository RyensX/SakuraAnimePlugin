package com.su.mediabox.plugin

import android.graphics.Color
import android.graphics.Typeface
import android.util.Log
import android.view.Gravity
import com.su.mediabox.pluginapi.UI.dp
import com.su.mediabox.pluginapi.v2.action.Action
import com.su.mediabox.pluginapi.v2.action.DetailAction
import com.su.mediabox.pluginapi.v2.been.*
import com.su.mediabox.pluginapi.v2.components.ICustomPageComponent
import org.jsoup.select.Elements
import java.util.*

class UpdateTablePageDataComponent : ICustomPageComponent {

    override val pageName = "更新表"
    override fun isShowBack() = false

    private val days = mutableListOf<String>()
    private lateinit var updateList: Elements

    override suspend fun getData(page: Int): List<BaseData>? {
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
        val cal: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
        }
        val w = cal.get(Calendar.DAY_OF_WEEK).let {
            if (it == Calendar.SUNDAY) 6 else it - 2
        }
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
                var index = 0
                for (em in target.children()) {
                    index++
                    val titleEm = em.select("[title]").first()
                    val title = titleEm?.text()
                    val episode = em.select("[target=_blank]").first()?.text()
                    val url = titleEm?.attr("href")
                    if (!title.isNullOrBlank() && !episode.isNullOrBlank() && !url.isNullOrBlank()) {
                        Log.d("添加更新", "$title $episode $url")
                        ups.add(TagData("$index").apply {
                            spanSize = 1
                            paddingLeft = 6.dp
                        })

                        ups.add(
                            SimpleTextData(title).apply {
                                spanSize = 5
                                fontStyle = Typeface.BOLD
                                fontColor = Color.BLACK
                                paddingTop = 6.dp
                                paddingBottom = 6.dp
                                paddingLeft = 0.dp
                                paddingRight = 0.dp
                                action = DetailAction.obtain(url)
                            })

                        ups.add(SimpleTextData(episode).apply {
                            spanSize = 2
                            fontStyle = Typeface.BOLD
                            paddingRight = 6.dp
                            gravity = Gravity.RIGHT or Gravity.CENTER_VERTICAL
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
        }, w).apply {
            layoutConfig = BaseData.LayoutConfig(
                itemSpacing = 0,
                listLeftEdge = 0,
                listRightEdge = 0
            )
        })
    }
}