package com.qipa.qipaimbase.image

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.qipa.qipaimbase.R
import com.qipa.qipaimbase.base.BaseActivity
import java.util.ArrayList

class ImageCheckActivity : BaseActivity() {
    var viewPager: ViewPager? = null
    var ivClose: ImageView? =  null
    private var adapter: ImageCheckFragmentAdapter? = null
    private var fragments: MutableList<Fragment?>? = null
    private var chatDataList: List<String>? = null
    private var currentPosition = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_imagecheck)
        initView()
    }

    private fun initView() {
        viewPager = findViewById(R.id.viewPager)
        ivClose = findViewById(R.id.ivClose)
        ivClose?.setOnClickListener {
            onCloseClick()
        }
        chatDataList = intent.extras!![EXTRA_CHATDATA] as List<String>?
        //        current = (ChatData) getIntent().getExtras().get(EXTRA_CHATDATA_CURRENT);
        currentPosition = intent.getIntExtra(EXTRA_CHATDATA_CURRENT, 0)
        fragments = ArrayList()
        for (i in chatDataList!!.indices) {
            val imageFragment = ImageFragment.getInstance(chatDataList!![i])
            fragments?.add(imageFragment)
        }
        adapter = ImageCheckFragmentAdapter(supportFragmentManager, fragments)
        viewPager?.adapter = adapter
        viewPager?.currentItem = currentPosition
    }

    fun onCloseClick() {
        finish()
    }

    companion object {
        private const val EXTRA_CHATDATA = "EXTRA_CHATDATA"
        private const val EXTRA_CHATDATA_CURRENT = "EXTRA_CHATDATA_CURRENT"
        fun startActivity(context: Activity, chatDatas: ArrayList<String>, currentPosition: Int) {
            val intent = Intent(
                context,
                ImageCheckActivity::class.java
            )
            intent.putExtra(EXTRA_CHATDATA, chatDatas)
            intent.putExtra(EXTRA_CHATDATA_CURRENT, currentPosition)
            context.startActivity(intent)
        }
    }
}
