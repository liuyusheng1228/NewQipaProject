package com.qipa.newboxproject.ui.fragment.me.set

import android.app.TaskStackBuilder
import android.content.Intent
import android.os.Bundle
import com.qipa.jetpackmvvm.ext.nav
import com.qipa.newboxproject.R
import com.qipa.newboxproject.app.appViewModel
import com.qipa.newboxproject.app.base.BaseFragment
import com.qipa.newboxproject.app.util.LangUtils
import com.qipa.newboxproject.databinding.FragmentChangelanguageBinding
import com.qipa.newboxproject.ui.activity.MainActivity
import com.qipa.newboxproject.viewmodel.state.ChangeLanguageModel
import kotlinx.android.synthetic.main.fragment_changelanguage.*
import kotlinx.android.synthetic.main.include_back.*

class ChangeLanguageFragment : BaseFragment<ChangeLanguageModel,FragmentChangelanguageBinding>() {
    override fun layoutId() = R.layout.fragment_changelanguage

    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.vm = mViewModel
        mDatabind.click = ProClick()
        rel_detail_back.setOnClickListener {
            nav().navigateUp()
        }
        toolbar_titletv.text = resources.getString(R.string.more_language)
        // 当前app 语音
        appViewModel.getLanguageLocal()?.observe(this, { rcLocale ->
            if (rcLocale === LangUtils.RCLocale.LOCALE_US) {
                siv_chinese.setSelected(false)
                siv_english.setSelected(true)
                siv_twchinese.setSelected(false)
                siv_arab.setSelected(false)
            } else if (rcLocale === LangUtils.RCLocale.LOCALE_CHINA) {
                siv_chinese.setSelected(true)
                siv_english.setSelected(false)
                siv_arab.setSelected(false)
                siv_twchinese.setSelected(false)
            } else if (rcLocale === LangUtils.RCLocale.LOCALE_ARAB) {
                siv_chinese.setSelected(false)
                siv_english.setSelected(false)
                siv_arab.setSelected(true)
                siv_twchinese.setSelected(false)
            }else if (rcLocale === LangUtils.RCLocale.LOCALE_CHINA_TW) {
                siv_chinese.setSelected(false)
                siv_english.setSelected(false)
                siv_arab.setSelected(false)
                siv_twchinese.setSelected(true)
            }
        })
    }

    override fun onBackPressed(): Boolean {
        return false
    }

    inner class ProClick{
        fun siv_chinese(){

            //中文
            siv_chinese.setSelected(true)
            siv_english.setSelected(false)
            siv_twchinese.setSelected(false)
            siv_arab.setSelected(false)
            changeLanguage(LangUtils.RCLocale.LOCALE_CHINA)
            backToSettingActivity()
        }

        fun siv_english(){
            //英文
            siv_chinese.setSelected(false)
            siv_twchinese.setSelected(false)
            siv_english.setSelected(true)
            siv_arab.setSelected(false)
            changeLanguage(LangUtils.RCLocale.LOCALE_US)
            backToSettingActivity()
        }

        fun siv_arab(){
            siv_twchinese.setSelected(false)
            siv_chinese.setSelected(false)
            siv_english.setSelected(false)
            siv_arab.setSelected(true)
            changeLanguage(LangUtils.RCLocale.LOCALE_ARAB)
            backToSettingActivity()
        }

        fun siv_twchinese(){
            siv_twchinese.setSelected(true)
            siv_chinese.setSelected(false)
            siv_english.setSelected(false)
            siv_arab.setSelected(false)
            changeLanguage(LangUtils.RCLocale.LOCALE_CHINA_TW)
            backToSettingActivity()
        }
    }

    /**
     * 切换语言
     * @param selectedLocale
     */
    private fun changeLanguage(selectedLocale: LangUtils.RCLocale) {
            appViewModel.changeLanguage(selectedLocale)
    }

    private fun backToSettingActivity() {
        val mainActivity: Intent = Intent(
            mActivity,
            MainActivity::class.java
        )
        val taskStackBuilder = TaskStackBuilder.create(mActivity)
        taskStackBuilder.addNextIntent(mainActivity)
        taskStackBuilder.startActivities()
    }


}