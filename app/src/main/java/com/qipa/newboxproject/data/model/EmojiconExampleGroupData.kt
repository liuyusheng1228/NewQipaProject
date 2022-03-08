package com.qipa.newboxproject.data.model

import com.hyphenate.easeui.domain.EaseEmojiconGroupEntity
import com.hyphenate.easeui.domain.EaseEmojicon
import com.qipa.newboxproject.R
import com.hyphenate.easeui.domain.EaseEmojicon.Type;
import com.qipa.newboxproject.app.App

object EmojiconExampleGroupData {
    private val icons = intArrayOf(
//        R.drawable.icon_002_cover,
//        R.drawable.icon_007_cover,
//        R.drawable.icon_010_cover,
//        R.drawable.icon_012_cover,
//        R.drawable.icon_013_cover,
//        R.drawable.icon_018_cover,
//        R.drawable.icon_019_cover,
//        R.drawable.icon_020_cover,
//        R.drawable.icon_021_cover,
//        R.drawable.icon_022_cover,
//        R.drawable.icon_024_cover,
//        R.drawable.icon_027_cover,
//        R.drawable.icon_029_cover,
//        R.drawable.icon_030_cover,
//        R.drawable.icon_035_cover,
//        R.drawable.icon_040_cover
    )
    private val bigIcons = intArrayOf(
//        R.drawable.icon_002,
//        R.drawable.icon_007,
//        R.drawable.icon_010,
//        R.drawable.icon_012,
//        R.drawable.icon_013,
//        R.drawable.icon_018,
//        R.drawable.icon_019,
//        R.drawable.icon_020,
//        R.drawable.icon_021,
//        R.drawable.icon_022,
//        R.drawable.icon_024,
//        R.drawable.icon_027,
//        R.drawable.icon_029,
//        R.drawable.icon_030,
//        R.drawable.icon_035,
//        R.drawable.icon_040
    )
    val data = createData()

    private fun createData(): EaseEmojiconGroupEntity {
        val emojiconGroupEntity = EaseEmojiconGroupEntity()
        val datas = arrayOfNulls<EaseEmojicon>(icons.size)
        for (i in icons.indices) {
            datas[i] = EaseEmojicon(icons[i], null, Type.BIG_EXPRESSION)
            datas[i]?.bigIcon = bigIcons[i]
            //you can replace this to any you want
            datas[i]!!.name = App.instance.applicationContext
                .getString(R.string.cancel) + (i + 1)
            datas[i]!!.identityCode = "em" + (1000 + i + 1)
        }
        emojiconGroupEntity.emojiconList = datas.toMutableList()
//        emojiconGroupEntity.icon = R.drawable.icon_002_cover
        emojiconGroupEntity.type = Type.BIG_EXPRESSION
        return emojiconGroupEntity
    }
}
