package com.qipa.qipaimbase.chat.emoji

import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.ImageSpan
import com.google.gson.Gson
import com.qipa.qipaimbase.ImBaseBridge
import com.qipa.qipaimbase.utils.Utils
import com.qipa.qipaimbase.utils.task.AsycTaskUtil
import com.qipa.qipaimbase.utils.task.TaskExecutor
import java.util.HashMap
import java.util.regex.Pattern

class EmojiUtils private constructor() {


    fun init() {
        if (init) {
            return
        }
        TaskExecutor.instance.createAsycTask({
            val emojiJson = Gson().fromJson(emojiJson, EmojiJson::class.java)
            emojiJson
        }, object : AsycTaskUtil.OnTaskListener{
            override fun onTaskFinished(result: Any?) {
                val emojiJson = result as EmojiJson
                EmojiUtils.emojiMap = HashMap()
                EmojiUtils.emojiBeans = (result as EmojiJson).getEmojis()
                for (emojiBean in EmojiUtils.emojiBeans!!) {
                    (EmojiUtils.emojiMap as HashMap<String?, String?>)[emojiBean?.credentialName] = emojiBean?.resId
                }
                init = true
            }

        })
    }


    val emojiMap: Map<String?, String?>?
        get() = Companion.emojiMap
    val emojiBeans: List<EmojiJson.EmojiBean?>?
        get() = Companion.emojiBeans

    companion object {
        private const val TAG = "EmojiUtils"
        const val EMOJI_MATCH = "\\[.{1,3}\\]"
        var instance: EmojiUtils? = null
            get() {
                if (field == null) {
                    field = EmojiUtils()
                }
                return field
            }
        @Synchronized
        fun get(): EmojiUtils{
            return instance!!
        }
        @Volatile
        private var emojiMap: MutableMap<String?, String?>? = null
        private var emojiBeans: List<EmojiJson.EmojiBean?>? = null

        @Volatile
        private var init = false
        private const val emojiJson = "{\"emojis\":[\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_ciya\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_tiaopi\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_liuhan\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_touxiao\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_zaijian\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_qiaoda\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_cahan\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_zhutou\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_meigui\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_liulei\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_daku\"},\n" +
                "        { \"credentialName\" : \"[???]\" ,\"resId\":\"emoji_xu\"},\n" +
                "        { \"credentialName\" : \"[???]\" ,\"resId\":\"emoji_ku\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_zhuakuang\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_weiqu\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_bianbian\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_zhadan\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_caidao\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_keai\"},\n" +
                "        { \"credentialName\" : \"[???]\" ,\"resId\":\"emoji_se\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_haixiu\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_deyi\"},\n" +
                "        { \"credentialName\" : \"[???]\" ,\"resId\":\"emoji_tu\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_weixiao\"},\n" +
                "        { \"credentialName\" : \"[???]\" ,\"resId\":\"emoji_nu\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_ganga\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_jingkong\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_lenghan\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_aixin\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_shiai\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_baiyan\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_aoman\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_nanguo\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_jingya\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_yiwen\"},\n" +
                "        { \"credentialName\" : \"[???]\" ,\"resId\":\"emoji_kun\"},\n" +
                "        { \"credentialName\" : \"[?????????]\" ,\"resId\":\"emoji_memeda\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_hanxiao\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_aiqing\"},\n" +
                "        { \"credentialName\" : \"[???]\" ,\"resId\":\"emoji_shuai\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_biezui\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_yinxian\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_fendou\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_fadai\"},\n" +
                "        { \"credentialName\" : \"[?????????]\" ,\"resId\":\"emoji_youhengheng\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_baobao\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_huaixiao\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_feiwen\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_bishi\"},\n" +
                "        { \"credentialName\" : \"[???]\" ,\"resId\":\"emoji_yun\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_dabing\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_kelian\"},\n" +
                "        { \"credentialName\" : \"[???]\" ,\"resId\":\"emoji_qiang\"},\n" +
                "        { \"credentialName\" : \"[???]\" ,\"resId\":\"emoji_ruo\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_woshou\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_shengli\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_baoquan\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_diaoxie\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_mifan\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_dangao\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_xigua\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_pijiu\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_piaochong\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_gouyin\"},\n" +
                "        { \"credentialName\" : \"[OK]\" ,\"resId\":\"emoji_ok\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_aini\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_kafei\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_yueliang\"},\n" +
                "        { \"credentialName\" : \"[???]\" ,\"resId\":\"emoji_dao\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_fadou\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_chajin\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_quantou\"},\n" +
                "        { \"credentialName\" : \"[?????????]\" ,\"resId\":\"emoji_xinsuile\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_taiyang\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_liwu\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_piqiu\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_kulou\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_huishou\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_shandian\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_jie\"},\n" +
                "        { \"credentialName\" : \"[???]\" ,\"resId\":\"emoji_kun\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_zhouma\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_zhemo\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_koubi\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_guzhang\"},\n" +
                "        { \"credentialName\" : \"[?????????]\" ,\"resId\":\"emoji_qiudale\"},\n" +
                "        { \"credentialName\" : \"[?????????]\" ,\"resId\":\"emoji_zuohengheng\"},\n" +
                "        { \"credentialName\" : \"[?????????]\" ,\"resId\":\"emoji_dahaqian\"},\n" +
                "        { \"credentialName\" : \"[?????????]\" ,\"resId\":\"emoji_kuaikule\"},\n" +
                "        { \"credentialName\" : \"[???]\" ,\"resId\":\"emoji_xia\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_lanqiu\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_pingpang\"},\n" +
                "        { \"credentialName\" : \"[NO]\" ,\"resId\":\"emoji_no\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_tiaotiao\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_ouhuo\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_zhuanquan\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_ketou\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_huitou\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_tiaosheng\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_jidong\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_jiewu\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_xianwen\"},\n" +
                "        { \"credentialName\" : \"[?????????]\" ,\"resId\":\"emoji_zuotaiji\"},\n" +
                "        { \"credentialName\" : \"[?????????]\" ,\"resId\":\"emoji_youtaiji\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_bizui\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_maomi\"},\n" +
                "        { \"credentialName\" : \"[?????????]\" ,\"resId\":\"emoji_hongshuangxi\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_bianpao\"},\n" +
                "        { \"credentialName\" : \"[?????????]\" ,\"resId\":\"emoji_hongdenglong\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_majiang\"},\n" +
                "        { \"credentialName\" : \"[?????????]\" ,\"resId\":\"emoji_maikefeng\"},\n" +
                "        { \"credentialName\" : \"[?????????]\" ,\"resId\":\"emoji_lipindai\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_xinfeng\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_xiangqi\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_caidai\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_lazhu\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_baojin\"},\n" +
                "        { \"credentialName\" : \"[?????????]\" ,\"resId\":\"emoji_bangbangtang\"},\n" +
                "        { \"credentialName\" : \"[??????]\",\"resId\":\"emoji_neiping\" },\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_miantiao\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_xiangjiao\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_feiji\"},\n" +
                "        { \"credentialName\" : \"[?????????]\" ,\"resId\":\"emoji_zuochetou\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_chexiang\"},\n" +
                "        { \"credentialName\" : \"[?????????]\" ,\"resId\":\"emoji_youchetou\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_duoyun\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_xiayu\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_chaopiao\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_xiongmao\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_dengpao\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_fengche\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_naozhong\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_caiqiu\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_zuanjie\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_shafa\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_zhijin\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_shouqiang\"},\n" +
                "        { \"credentialName\" : \"[??????]\" ,\"resId\":\"emoji_qingwa\"}\n" +
                " ]}"

        fun generateEmojiSpan(content: String?): SpannableString? {
            if (TextUtils.isEmpty(content)) {
                return null
            }
            val time = System.currentTimeMillis()
            val contentShow = SpannableString(content)
            val pattern = Pattern.compile(EMOJI_MATCH)
            val matcher = pattern.matcher(content)
            val emojiMap: MutableMap<String?, String?>? = emojiMap
            var start: Int
            var end: Int
            var resId: String?
            var imageSpan: ImageSpan?
            while (matcher.find()) {
                start = matcher.start()
                end = matcher.end()
                resId = emojiMap?.get(matcher.group())
                if (resId != null) {
                    imageSpan = Utils.getDrawableByName(resId)?.let {
                        ImBaseBridge.instance?.application?.let { it1 ->
                            ImageSpan(
                                it1,
                                it
                            )
                        }
                    }
                    contentShow.setSpan(imageSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            }
//            LogUtils.log(
//                TAG,
//                String.format("generateEmojiSpan time:%d", System.currentTimeMillis() - time)
//            )
            return contentShow
        }
    }
}