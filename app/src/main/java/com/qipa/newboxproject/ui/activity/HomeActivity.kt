package com.qipa.newboxproject.ui.activity

import android.util.SparseArray
import androidx.fragment.app.Fragment
import com.qipa.newboxproject.R

import kotlinx.android.synthetic.main.activity_home.*

import android.widget.Toast

import com.zenglb.downloadinstaller.DownloadInstaller

import com.zenglb.downloadinstaller.DownloadProgressCallBack

import android.content.Context

import android.graphics.drawable.BitmapDrawable

import android.os.Bundle
import android.view.View

import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2

import com.daimajia.numberprogressbar.NumberProgressBar
import java.lang.Exception
import com.qipa.newboxproject.app.upload.bean.CheckVersionResult


class HomeActivity : AppCompatActivity() {

    private var mDialog: UpdateDialog? = null
    private var checkVersionResult: CheckVersionResult? = null
    private val roFragments = arrayOf<Class<*>>()
    private var viewPager : ViewPager2? = null
    private var mContext : Context? = null
    private var mFragmentSparseArray: SparseArray<Fragment>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        mContext = this
        initView()
    }

    fun initView(){
        viewPager = view_pager
        checkVersionResult=getIntent().getParcelableExtra("update");
        initViewpage()
        mDialog =  UpdateDialog(this)
        mDialog?.show();


    }

    fun initViewpage(){
//        val tabLayoutNoScroll: TabLayoutNoScroll = findViewById(R.id.tablayout)
//        val fragmentPageAdapter: FragPageAdapterVp2NoScroll<TabSelectBean?> =
//            object : FragPageAdapterVp2NoScroll<TabSelectBean?>(this) {
//
//
//                override fun createFragment(bean: TabSelectBean?, position: Int): Fragment {
////                    return HomeFragment.newInstance(
////                        "",
////                        list_bean[position]!!.text
////                    )!!
//                }
//
//                override fun bindDataToTab(
//                    holder: TabNoScrollViewHolder?,
//                    position: Int,
//                    bean: TabSelectBean?,
//                    isSelected: Boolean
//                ) {
//                    val textView = holder?.getView<TextView>(R.id.tv)
//                    if (isSelected) {
//                        textView?.setTextColor(-0xff0100)
//                        bean?.let { holder?.setImageResource(R.id.iv, it.resID_selected) }
//                    } else {
//                        textView?.setTextColor(-0xbbbbbc)
//                        bean?.resID_normal?.let { holder?.setImageResource(R.id.iv, it) }
//                    }
//                    textView?.text = bean?.text
//                }
//
//                override fun getTabLayoutID(position: Int, bean: TabSelectBean?): Int {
//                    return if (position == 2) {
//                        R.layout.item_tab_main_circle
//                    } else R.layout.item_tab_main
//                }
//            }
//
//        val tabAdapter =
//            TabMediatorVp2NoScroll<TabSelectBean>(tabLayoutNoScroll, viewPager).setAdapter(
//                fragmentPageAdapter
//            )
//
//        val list: MutableList<TabSelectBean> = ArrayList()
//        list.add(TabSelectBean("??????", R.mipmap.ic_launcher, R.mipmap.ic_launcher))
//        list.add(TabSelectBean("?????????", R.mipmap.ic_launcher, R.mipmap.ic_launcher))
//        list.add(TabSelectBean("?????????",R.mipmap.ic_launcher, R.mipmap.ic_launcher))
//        list.add(TabSelectBean("???", R.mipmap.ic_launcher, R.mipmap.ic_launcher))
//        fragmentPageAdapter.add<BaseFragPageAdapterVp2<TabSelectBean, TabNoScrollViewHolder>>(list as List<TabSelectBean?>?)
//        tabAdapter.add<ITabAdapter<*, *>>(list)
    }

    /**
     * ?????????????????????Dialog
     *
     */
    inner class UpdateDialog(context: Context) : AlertDialog(context) {
        var mContext: Context
        var mUnConfirm: TextView? = null
        var mContent: TextView? = null
        var mBtnConfirm: TextView? = null
        var mVersion: TextView? = null
        var mTitle: TextView? = null
        var progressBar: NumberProgressBar? = null
        protected override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            this.getWindow()?.setBackgroundDrawable(BitmapDrawable())
            setContentView(R.layout.update_app_alert)
            this.setCancelable(false)
            mVersion = findViewById<TextView>(R.id.version)
            mUnConfirm = findViewById<TextView>(R.id.btn_close)
            mContent = findViewById<TextView>(R.id.content)
            mTitle = findViewById<TextView>(R.id.title)
            progressBar = findViewById(R.id.tips_progress)
            mBtnConfirm = findViewById<TextView>(R.id.btn_confirm)


            // mContent.setText(checkVersionResult.getDescription());
            mVersion?.setText(this@HomeActivity.checkVersionResult?.versionName)
            if (this@HomeActivity.checkVersionResult?.updateType === 1) {
                mTitle?.text = "????????????"
                mUnConfirm?.text = "????????????"
            }
            mBtnConfirm?.setOnClickListener { v: View? ->
                //????????????
                mBtnConfirm?.setBackgroundResource(R.drawable.shape_btn_grey_bg)
                if (checkVersionResult?.updateType === 1) {
                    progressBar?.setVisibility(View.VISIBLE)
                    DownloadInstaller(mContext,
                        checkVersionResult?.packageUrl,
                        true,
                        object : DownloadProgressCallBack {
                            override fun downloadProgress(progress: Int) {
                                runOnUiThread(Runnable { progressBar?.setProgress(progress) })
                                if (progress == 100) {
                                }
                            }

                            override fun downloadException(e: Exception) {
                                val a = 1
                            }

                            override fun onInstallStart() {
                                finish()
                            }
                        }).start()
                } else {
                    DownloadInstaller(
                        mContext,
                        checkVersionResult?.packageUrl
                    ).start()
                    Toast.makeText(mContext, "?????????????????????", Toast.LENGTH_LONG).show()
                }
            }
            mUnConfirm?.setOnClickListener { v: View? ->
                if (checkVersionResult?.updateType === 1) {
                    finish()
                } else {
                    dismiss()
                }
            }
        }

        init {
            mContext = context
        }
    }
}