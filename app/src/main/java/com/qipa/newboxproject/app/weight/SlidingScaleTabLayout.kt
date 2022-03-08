//package com.qipa.newboxproject.app.weight
//
//import android.content.Context
//import android.graphics.*
//import android.graphics.drawable.GradientDrawable
//import android.os.Bundle
//import android.os.Parcelable
//import android.text.TextUtils
//import android.util.AttributeSet
//import android.util.SparseBooleanArray
//import android.util.TypedValue
//import android.view.Gravity
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.*
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.FragmentActivity
//import androidx.fragment.app.FragmentManager
//import androidx.fragment.app.FragmentPagerAdapter
//import androidx.viewpager.widget.PagerAdapter
//import androidx.viewpager.widget.ViewPager
//import androidx.viewpager.widget.ViewPager.OnPageChangeListener
//import com.qipa.newboxproject.R
//import java.util.*
//
///**
// * 滑动切换TabLayout,tab的文字大小会发生变化
// */
//class SlidingScaleTabLayout @JvmOverloads constructor(
//    context: Context,
//    attrs: AttributeSet? = null,
//    defStyleAttr: Int = 0
//) :
//    HorizontalScrollView(context, attrs, defStyleAttr), OnPageChangeListener {
//    private val mContext: Context
//    private var mViewPager: ViewPager? = null
//    private var mTitles: ArrayList<String>? = null
//    private val mTabsContainer: LinearLayout
//    private var mCurrentTab = 0
//    private var mCurrentPositionOffset = 0f
//    var tabCount = 0
//        private set
//
//    /**
//     * 用于绘制显示器
//     */
//    private val mIndicatorRect = Rect()
//
//    /**
//     * 用于实现滚动居中
//     */
//    private val mTabRect = Rect()
//    private val mIndicatorDrawable = GradientDrawable()
//    private val mRectPaint = Paint(Paint.ANTI_ALIAS_FLAG)
//    private val mDividerPaint = Paint(Paint.ANTI_ALIAS_FLAG)
//    private val mTrianglePaint = Paint(Paint.ANTI_ALIAS_FLAG)
//    private val mTrianglePath = Path()
//    private var mIndicatorStyle = STYLE_NORMAL
//    private var mTabPadding = 0f
//    private var mTabSpaceEqual = false
//    private var mTabWidth = 0f
//
//    /**
//     * indicator
//     */
//    private var mIndicatorColor = 0
//    private var mIndicatorHeight = 0f
//    private var mIndicatorWidth = 0f
//    private var mIndicatorCornerRadius = 0f
//    var indicatorMarginLeft = 0f
//        private set
//    var indicatorMarginTop = 0f
//        private set
//    var indicatorMarginRight = 0f
//        private set
//    var indicatorMarginBottom = 0f
//        private set
//    private var mIndicatorGravity = 0
//    private var mIndicatorWidthEqualTitle = false
//
//    /**
//     * underline
//     */
//    private var mUnderlineColor = 0
//    private var mUnderlineHeight = 0f
//    private var mUnderlineGravity = 0
//
//    /**
//     * divider
//     */
//    private var mDividerColor = 0
//    private var mDividerWidth = 0f
//    private var mDividerPadding = 0f
//    var textSelectSize = 0f
//        private set
//    var textUnselectSize = 0f
//        private set
//    private var mTextSelectColor = 0
//    private var mTextUnSelectColor = 0
//    private var mTextBold = 0
//    private var mTextAllCaps = false
//    private var mLastScrollX = 0
//    private var mHeight = 0
//    private var mSnapOnTabClick = false
//
//    /**
//     * tab的上下间距
//     */
//    private var mTabMarginTop = 0
//    private var mTabMarginBottom = 0
//    private var mTabMsgMarginTop = 0
//    private var mTabMsgMarginRight = 0
//    private var mTabDotMarginTop = 0
//    private var mTabDotMarginRight = 0
//    private var mTabBackgroundId = 0
//    private var openDmg = true
//
//    /**
//     * tab中的内容的位置
//     */
//    private var mTabHorizontalGravity = 0
//    private var mTabVerticalGravity = 0
//    private var iTabScaleTransformer: ITabScaleTransformer? = null
//    private var extendTransformer: ExtendTransformer? = null
//    private fun obtainAttributes(context: Context, attrs: AttributeSet?) {
//        val ta = context.obtainStyledAttributes(attrs, R.styleable.SlidingScaleTabLayout)
//        mIndicatorStyle =
//            ta.getInt(R.styleable.SlidingScaleTabLayout_tl_indicator_style, STYLE_NORMAL)
//        mIndicatorColor = ta.getColor(
//            R.styleable.SlidingScaleTabLayout_tl_indicator_color,
//            Color.parseColor(if (mIndicatorStyle == STYLE_BLOCK) "#4B6A87" else "#ffffff")
//        )
//        mIndicatorHeight = ta.getDimension(
//            R.styleable.SlidingScaleTabLayout_tl_indicator_height,
//            dp2px(if (mIndicatorStyle == STYLE_TRIANGLE) 4 else (if (mIndicatorStyle == STYLE_BLOCK) -1 else 2).toFloat()).toFloat()
//        )
//        mIndicatorWidth = ta.getDimension(
//            R.styleable.SlidingScaleTabLayout_tl_indicator_width,
//            dp2px(if (mIndicatorStyle == STYLE_TRIANGLE) 10 else -1.toFloat()).toFloat()
//        )
//        mIndicatorCornerRadius = ta.getDimension(
//            R.styleable.SlidingScaleTabLayout_tl_indicator_corner_radius,
//            dp2px(if (mIndicatorStyle == STYLE_BLOCK) -1 else 0.toFloat()).toFloat()
//        )
//        indicatorMarginLeft = ta.getDimension(
//            R.styleable.SlidingScaleTabLayout_tl_indicator_margin_left,
//            dp2px(0f).toFloat()
//        )
//        indicatorMarginTop = ta.getDimension(
//            R.styleable.SlidingScaleTabLayout_tl_indicator_margin_top,
//            dp2px(if (mIndicatorStyle == STYLE_BLOCK) 7 else 0.toFloat()).toFloat()
//        )
//        indicatorMarginRight = ta.getDimension(
//            R.styleable.SlidingScaleTabLayout_tl_indicator_margin_right,
//            dp2px(0f).toFloat()
//        )
//        indicatorMarginBottom = ta.getDimension(
//            R.styleable.SlidingScaleTabLayout_tl_indicator_margin_bottom,
//            dp2px(if (mIndicatorStyle == STYLE_BLOCK) 7 else 0.toFloat()).toFloat()
//        )
//        mIndicatorGravity =
//            ta.getInt(R.styleable.SlidingScaleTabLayout_tl_indicator_gravity, Gravity.BOTTOM)
//        mIndicatorWidthEqualTitle =
//            ta.getBoolean(R.styleable.SlidingScaleTabLayout_tl_indicator_width_equal_title, false)
//        mUnderlineColor = ta.getColor(
//            R.styleable.SlidingScaleTabLayout_tl_underline_color,
//            Color.parseColor("#ffffff")
//        )
//        mUnderlineHeight = ta.getDimension(
//            R.styleable.SlidingScaleTabLayout_tl_underline_height,
//            dp2px(0f).toFloat()
//        )
//        mUnderlineGravity =
//            ta.getInt(R.styleable.SlidingScaleTabLayout_tl_underline_gravity, Gravity.BOTTOM)
//        mDividerColor = ta.getColor(
//            R.styleable.SlidingScaleTabLayout_tl_divider_color,
//            Color.parseColor("#ffffff")
//        )
//        mDividerWidth =
//            ta.getDimension(R.styleable.SlidingScaleTabLayout_tl_divider_width, dp2px(0f).toFloat())
//        mDividerPadding = ta.getDimension(
//            R.styleable.SlidingScaleTabLayout_tl_divider_padding,
//            dp2px(12f).toFloat()
//        )
//        textUnselectSize = ta.getDimension(
//            R.styleable.SlidingScaleTabLayout_tl_textUnSelectSize,
//            sp2px(14f).toFloat()
//        )
//        // 被选中的文字大小，默认额未选中的大小一样
//        textSelectSize = ta.getDimension(
//            R.styleable.SlidingScaleTabLayout_tl_textSelectSize,
//            textUnselectSize
//        )
//        mTextSelectColor = ta.getColor(
//            R.styleable.SlidingScaleTabLayout_tl_textSelectColor,
//            Color.parseColor("#ffffff")
//        )
//        mTextUnSelectColor = ta.getColor(
//            R.styleable.SlidingScaleTabLayout_tl_textUnSelectColor,
//            Color.parseColor("#AAffffff")
//        )
//        mTextBold = ta.getInt(R.styleable.SlidingScaleTabLayout_tl_textBold, TEXT_BOLD_NONE)
//        mTextAllCaps = ta.getBoolean(R.styleable.SlidingScaleTabLayout_tl_textAllCaps, false)
//        mTabSpaceEqual = ta.getBoolean(R.styleable.SlidingScaleTabLayout_tl_tab_space_equal, false)
//        mTabWidth =
//            ta.getDimension(R.styleable.SlidingScaleTabLayout_tl_tab_width, dp2px(-1f).toFloat())
//        mTabPadding = ta.getDimension(
//            R.styleable.SlidingScaleTabLayout_tl_tab_padding,
//            if (mTabSpaceEqual || mTabWidth > 0) dp2px(0f) else dp2px(20f).toFloat()
//        )
//        // 得到设置的上下间距和gravity
//        mTabMarginTop =
//            ta.getDimensionPixelSize(R.styleable.SlidingScaleTabLayout_tl_tab_marginTop, 0)
//        mTabMarginBottom =
//            ta.getDimensionPixelSize(R.styleable.SlidingScaleTabLayout_tl_tab_marginBottom, 0)
//        mTabHorizontalGravity =
//            ta.getInt(R.styleable.SlidingScaleTabLayout_tl_tab_horizontal_gravity, CENTER)
//        mTabVerticalGravity =
//            ta.getInt(R.styleable.SlidingScaleTabLayout_tl_tab_vertical_gravity, CENTER)
//        mTabMsgMarginTop =
//            ta.getDimension(R.styleable.SlidingScaleTabLayout_tl_tab_msg_marginTop, 0f).toInt()
//        mTabMsgMarginRight =
//            ta.getDimension(R.styleable.SlidingScaleTabLayout_tl_tab_msg_marginRight, 0f).toInt()
//        mTabDotMarginTop =
//            ta.getDimension(R.styleable.SlidingScaleTabLayout_tl_tab_dot_marginTop, 0f).toInt()
//        mTabDotMarginRight =
//            ta.getDimension(R.styleable.SlidingScaleTabLayout_tl_tab_dot_marginRight, 0f).toInt()
//        mTabBackgroundId = ta.getResourceId(R.styleable.SlidingScaleTabLayout_tl_tab_background, 0)
//        openDmg = ta.getBoolean(R.styleable.SlidingScaleTabLayout_tl_openTextDmg, false)
//        ta.recycle()
//        iTabScaleTransformer = TabScaleTransformer(
//            this,
//            textSelectSize,
//            textUnselectSize, openDmg
//        )
//    }
//
//    /**
//     * 关联ViewPager
//     */
//    fun setViewPager(vp: ViewPager?) {
//        check(!(vp == null || vp.adapter == null)) { "ViewPager or ViewPager adapter can not be NULL !" }
//        mViewPager = vp
//        initViewPagerListener()
//    }
//
//    /**
//     * 设置标题，不关联ViewPager
//     */
//    fun setTitle(titles: Array<String?>) {
//        mTitles = ArrayList()
//        Collections.addAll(mTitles, *titles)
//        initViewPagerListener()
//    }
//
//    /**
//     * 关联ViewPager,用于不想在ViewPager适配器中设置titles数据的情况
//     */
//    fun setViewPager(vp: ViewPager?, titles: Array<String?>?) {
//        check(!(vp == null || vp.adapter == null)) { "ViewPager or ViewPager adapter can not be NULL !" }
//        check(!(titles == null || titles.size == 0)) { "Titles can not be EMPTY !" }
//        check(titles.size == vp.adapter!!.count) { "Titles length must be the same as the page count !" }
//        mViewPager = vp
//        mTitles = ArrayList()
//        Collections.addAll(mTitles, *titles)
//        initViewPagerListener()
//    }
//
//    /**
//     * 关联ViewPager,用于连适配器都不想自己实例化的情况
//     */
//    fun setViewPager(
//        vp: ViewPager?,
//        titles: Array<String?>?,
//        fa: FragmentActivity,
//        fragments: ArrayList<Fragment?>?
//    ) {
//        checkNotNull(vp) { "ViewPager can not be NULL !" }
//        check(!(titles == null || titles.size == 0)) { "Titles can not be EMPTY !" }
//        mViewPager = vp
//        mViewPager!!.adapter =
//            InnerPagerAdapter(fa.supportFragmentManager, fragments, titles)
//        initViewPagerListener()
//    }
//
//    private fun initViewPagerListener() {
//        if (mViewPager != null) {
//            mViewPager!!.removeOnPageChangeListener(this)
//            mViewPager!!.addOnPageChangeListener(this)
//            initTransformer()
//        }
//        notifyDataSetChanged()
//    }
//
//    private fun initTransformer() {
//        // 如果选中状态的文字大小和未选中状态的文字大小是不同的，开启缩放
//        if (textUnselectSize != textSelectSize) {
//            extendTransformer = ExtendTransformer()
//            mViewPager!!.setPageTransformer(true, extendTransformer)
//        }
//    }
//
//    /**
//     * 更新数据
//     */
//    fun notifyDataSetChanged() {
//        mTabsContainer.removeAllViews()
//        tabCount = if (mTitles == null) mViewPager!!.adapter!!.count else mTitles!!.size
//        var tabView: View
//        for (i in 0 until tabCount) {
//            tabView = LayoutInflater.from(mContext)
//                .inflate(R.layout.layout_scale_tab, mTabsContainer, false)
//            val title = tabView.findViewById<TextView>(R.id.tv_tab_title)
//            // 设置tab的位置信息
//            setTabLayoutParams(title)
//            val pageTitle = if (mTitles == null) mViewPager!!.adapter!!
//                .getPageTitle(i) else mTitles!![i]
//            addTab(i, pageTitle.toString(), tabView)
//        }
//        updateTabStyles()
//    }
//
//    private fun setTabLayoutParams(title: TextView) {
//        var params = title.layoutParams as RelativeLayout.LayoutParams
//        params.topMargin = mTabMarginTop
//        params.bottomMargin = mTabMarginBottom
//        if (mTabVerticalGravity == TOP) {
//            params.addRule(RelativeLayout.ALIGN_PARENT_TOP)
//        } else if (mTabVerticalGravity == BOTTOM) {
//            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
//        } else {
//            params.addRule(RelativeLayout.CENTER_VERTICAL)
//        }
//        if (mTabHorizontalGravity == LEFT) {
//            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
//        } else if (mTabHorizontalGravity == RIGHT) {
//            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
//        } else {
//            params.addRule(RelativeLayout.CENTER_HORIZONTAL)
//        }
//        title.layoutParams = params
//        if (isDmgOpen) {
//            val imageView = ViewUtils.findBrotherView(title, R.id.tv_tab_title_dmg, 3) as ImageView
//                ?: return
//            params = imageView.layoutParams as RelativeLayout.LayoutParams
//            params.topMargin = mTabMarginTop
//            params.bottomMargin = mTabMarginBottom
//            // 调整镜像的问题
//            if (mTabVerticalGravity == TOP) {
//                params.addRule(RelativeLayout.ALIGN_PARENT_TOP)
//                imageView.scaleType = ImageView.ScaleType.FIT_START
//            } else if (mTabVerticalGravity == BOTTOM) {
//                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
//                imageView.scaleType = ImageView.ScaleType.FIT_END
//            } else {
//                params.addRule(RelativeLayout.CENTER_VERTICAL)
//                imageView.scaleType = ImageView.ScaleType.FIT_CENTER
//            }
//            if (mTabHorizontalGravity == LEFT) {
//                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT)
//            } else if (mTabHorizontalGravity == RIGHT) {
//                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
//            } else {
//                params.addRule(RelativeLayout.CENTER_HORIZONTAL)
//            }
//            imageView.layoutParams = params
//        }
//    }
//
//    /**
//     * 如果文字的大小没有变化，不需要开启镜像，请注意
//     */
//    private val isDmgOpen: Boolean
//        private get() = openDmg && textSelectSize != textUnselectSize
//
//    /**
//     * 创建并添加tab
//     */
//    private fun addTab(position: Int, title: String, tabView: View) {
//        val tv_tab_title = tabView.findViewById<View>(R.id.tv_tab_title) as TextView
//        if (tv_tab_title != null) {
////            tv_tab_title.setTextSize(TypedValue.COMPLEX_UNIT_PX, position == mCurrentTab ? mTextSelectSize : mTextUnSelectSize);
//            tv_tab_title.text = title
//            // 设置tab背景
//            if (mTabBackgroundId != 0) {
//                tv_tab_title.setBackgroundResource(mTabBackgroundId)
//            }
//            //            if (TextUtils.isEmpty(title)) {
////                tabView.setVisibility(View.GONE);
////            } else {
////                tabView.setVisibility(View.VISIBLE);
////            }
//        }
//        tabView.setOnClickListener { v ->
//            val position = mTabsContainer.indexOfChild(v)
//            if (position != -1) {
//                currentTab = position
//            }
//        }
//        /** 每一个Tab的布局参数  */
//        var lp_tab = if (mTabSpaceEqual) LinearLayout.LayoutParams(
//            0,
//            LayoutParams.MATCH_PARENT,
//            1.0f
//        ) else LinearLayout.LayoutParams(
//            LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT
//        )
//        if (mTabWidth > 0) {
//            lp_tab = LinearLayout.LayoutParams(mTabWidth.toInt(), LayoutParams.MATCH_PARENT)
//        }
//        mTabsContainer.addView(tabView, position, lp_tab)
//    }
//
//    private fun updateTabStyles() {
//        for (i in 0 until tabCount) {
//            val v = mTabsContainer.getChildAt(i)
//            //            v.setPadding((int) mTabPadding, v.getPaddingTop(), (int) mTabPadding, v.getPaddingBottom());
//            val tv_tab_title = v.findViewById<View>(R.id.tv_tab_title) as TextView
//            if (tv_tab_title != null) {
//                v.setPadding(mTabPadding.toInt(), 0, mTabPadding.toInt(), 0)
//                tv_tab_title.setTextSize(
//                    TypedValue.COMPLEX_UNIT_PX,
//                    if (i == mCurrentTab) textSelectSize else textUnselectSize
//                )
//                tv_tab_title.setTextColor(if (i == mCurrentTab) mTextSelectColor else mTextUnSelectColor)
//                // 设置选中状态
//                tv_tab_title.isSelected = i == mCurrentTab
//                if (mTextAllCaps) {
//                    tv_tab_title.text = tv_tab_title.text.toString().toUpperCase()
//                }
//                if (mTextBold == TEXT_BOLD_BOTH) {
//                    tv_tab_title.paint.isFakeBoldText = true
//                } else if (mTextBold == TEXT_BOLD_WHEN_SELECT) {
//                    tv_tab_title.paint.isFakeBoldText = i == mCurrentTab
//                } else if (mTextBold == TEXT_BOLD_NONE) {
//                    tv_tab_title.paint.isFakeBoldText = false
//                }
//                if (isDmgOpen) {
//                    generateTitleDmg(v, tv_tab_title, i)
//                }
//            }
//        }
//    }
//
//    private fun generateTitleDmg(tabView: View, textView: TextView, position: Int) {
//        // 空字符串不能做镜像，否则会引发空指针
//        if (TextUtils.isEmpty(textView.text)) {
//            return
//        }
//
//        // 如果需要开启镜像，需要把所有的字设置为选中的字体
////        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextUnSelectSize);
////        ImageView imageView = tabView.findViewById(R.id.tv_tab_title_dmg);
////        imageView.setImageBitmap(ViewUtils.generateViewCacheBitmap(textView));
////        imageView.setMaxWidth(imageView.getDrawable().getIntrinsicWidth());
//        val imageView = tabView.findViewById<ImageView>(R.id.tv_tab_title_dmg)
//        // 如果需要开启镜像，需要把所有的字设置为选中的字体
//        if (textSelectSize >= textUnselectSize) {
//            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSelectSize)
//            imageView.setImageBitmap(ViewUtils.generateViewCacheBitmap(textView))
//            val drawableWidth = imageView.drawable.intrinsicWidth
//            imageView.minimumWidth = (drawableWidth * textUnselectSize / textSelectSize).toInt()
//            imageView.maxWidth = drawableWidth
//        } else {
//            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textUnselectSize)
//            imageView.setImageBitmap(ViewUtils.generateViewCacheBitmap(textView))
//            val drawableWidth = imageView.drawable.intrinsicWidth
//            imageView.minimumWidth = (drawableWidth * textSelectSize / textUnselectSize).toInt()
//            imageView.maxWidth = drawableWidth
//        }
//
////        iTabScaleTransformer.setNormalWidth(position, imageView.getDrawable().getIntrinsicWidth(), position == mViewPager.getCurrentItem());
//        textView.visibility = GONE
//    }
//
//    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
//        /**
//         * position:当前View的位置
//         * mCurrentPositionOffset:当前View的偏移量比例.[0,1)
//         */
//        mCurrentTab = position
//        mCurrentPositionOffset = positionOffset
//        iTabScaleTransformer.onPageScrolled(position, positionOffset, positionOffsetPixels)
//        scrollToCurrentTab()
//        invalidate()
//        //        Log.i("onPageScrolled", "mCurrentTab：" + mCurrentTab + " positionOffset:" + positionOffset);
//        if (mCurrentPositionOffset == 0f) {
//            updateTabSelection(mCurrentTab)
//        }
//    }
//
//    override fun onPageSelected(position: Int) {}
//    override fun onPageScrollStateChanged(state: Int) {
////        if (state == ViewPager.SCROLL_STATE_IDLE) {
////            updateTabSelection(mCurrentTab);
////        }
//    }
//
//    /**
//     * HorizontalScrollView滚到当前tab,并且居中显示
//     */
//    private fun scrollToCurrentTab() {
//        if (tabCount <= 0) {
//            return
//        }
//        val offset = (mCurrentPositionOffset * mTabsContainer.getChildAt(mCurrentTab).width).toInt()
//        /**当前Tab的left+当前Tab的Width乘以positionOffset */
//        var newScrollX = mTabsContainer.getChildAt(mCurrentTab).left + offset
//        if (mCurrentTab > 0 || offset > 0) {
//            /**HorizontalScrollView移动到当前tab,并居中 */
//            newScrollX -= width / 2 - paddingLeft
//            calcIndicatorRect()
//            newScrollX += (mTabRect.right - mTabRect.left) / 2
//        }
//        if (newScrollX != mLastScrollX) {
//            mLastScrollX = newScrollX
//            /** scrollTo（int x,int y）:x,y代表的不是坐标点,而是偏移量
//             * x:表示离起始位置的x水平方向的偏移量
//             * y:表示离起始位置的y垂直方向的偏移量
//             */
//            scrollTo(newScrollX, 0)
//        }
//    }
//
//    private fun updateTabSelection(position: Int) {
//        for (i in 0 until tabCount) {
//            val tabView = mTabsContainer.getChildAt(i)
//            val isSelect = i == position
//            val tab_title = tabView.findViewById<View>(R.id.tv_tab_title) as TextView
//            if (tab_title != null) {
//                tab_title.setTextColor(if (isSelect) mTextSelectColor else mTextUnSelectColor)
//                // 设置选中状态
//                tab_title.isSelected = isSelect
//                if (mTextBold == TEXT_BOLD_BOTH) {
//                    tab_title.paint.isFakeBoldText = true
//                } else if (mTextBold == TEXT_BOLD_WHEN_SELECT && i == position) {
//                    tab_title.paint.isFakeBoldText = true
//                } else {
//                    tab_title.paint.isFakeBoldText = false
//                }
//                if (isDmgOpen && (mTextSelectColor != mTextUnSelectColor || mTextBold == TEXT_BOLD_WHEN_SELECT)) {
//                    tab_title.visibility = VISIBLE
//                    generateTitleDmg(tabView, tab_title, i)
//                } else {
//                    tab_title.post(object : Runnable {
//                        override fun run() {
//                            tab_title.setTextSize(
//                                TypedValue.COMPLEX_UNIT_PX,
//                                if (i == mCurrentTab) textSelectSize else textUnselectSize
//                            )
//                            tab_title.requestLayout()
//                        }
//                    })
//                }
//            }
//        }
//    }
//
//    private var margin = 0f
//    private fun calcIndicatorRect() {
//        val currentTabView = mTabsContainer.getChildAt(mCurrentTab)
//        var left = currentTabView.left.toFloat()
//        var right = currentTabView.right.toFloat()
//
//        //for mIndicatorWidthEqualTitle
//        if (mIndicatorStyle == STYLE_NORMAL && mIndicatorWidthEqualTitle) {
//            val tab_title = currentTabView.findViewById<TextView>(R.id.tv_tab_title)
//            val textWidth = mTextPaint.measureText(tab_title.text.toString())
//            margin = (right - left - textWidth) / 2
//        }
//        if (mCurrentTab < tabCount - 1) {
//            val nextTabView = mTabsContainer.getChildAt(mCurrentTab + 1)
//            val nextTabLeft = nextTabView.left.toFloat()
//            val nextTabRight = nextTabView.right.toFloat()
//            left = left + mCurrentPositionOffset * (nextTabLeft - left)
//            right = right + mCurrentPositionOffset * (nextTabRight - right)
//
//            //for mIndicatorWidthEqualTitle
//            if (mIndicatorStyle == STYLE_NORMAL && mIndicatorWidthEqualTitle) {
//                val next_tab_title = nextTabView.findViewById<TextView>(R.id.tv_tab_title)
//                val nextTextWidth = mTextPaint.measureText(next_tab_title.text.toString())
//                val nextMargin = (nextTabRight - nextTabLeft - nextTextWidth) / 2
//                margin = margin + mCurrentPositionOffset * (nextMargin - margin)
//            }
//        }
//        mIndicatorRect.left = left.toInt()
//        mIndicatorRect.right = right.toInt()
//        //for mIndicatorWidthEqualTitle
//        if (mIndicatorStyle == STYLE_NORMAL && mIndicatorWidthEqualTitle) {
//            mIndicatorRect.left = (left + margin - 1).toInt()
//            mIndicatorRect.right = (right - margin - 1).toInt()
//        }
//        mTabRect.left = left.toInt()
//        mTabRect.right = right.toInt()
//        if (mIndicatorWidth < 0) {   //indicatorWidth小于0时,原jpardogo's PagerSlidingTabStrip
//        } else { //indicatorWidth大于0时,圆角矩形以及三角形
//            var indicatorLeft = currentTabView.left + (currentTabView.width - mIndicatorWidth) / 2
//            if (mCurrentTab < tabCount - 1) {
//                val nextTab = mTabsContainer.getChildAt(mCurrentTab + 1)
//                indicatorLeft =
//                    indicatorLeft + mCurrentPositionOffset * (currentTabView.width / 2 + nextTab.width / 2)
//            }
//            mIndicatorRect.left = indicatorLeft.toInt()
//            mIndicatorRect.right = (mIndicatorRect.left + mIndicatorWidth).toInt()
//        }
//    }
//
//    override fun onDraw(canvas: Canvas) {
//        super.onDraw(canvas)
//        if (isInEditMode || tabCount <= 0) {
//            return
//        }
//        val height = height
//        val paddingLeft = paddingLeft
//        // draw divider
//        if (mDividerWidth > 0) {
//            mDividerPaint.strokeWidth = mDividerWidth
//            mDividerPaint.color = mDividerColor
//            for (i in 0 until tabCount - 1) {
//                val tab = mTabsContainer.getChildAt(i)
//                canvas.drawLine(
//                    (paddingLeft + tab.right).toFloat(),
//                    mDividerPadding,
//                    (paddingLeft + tab.right).toFloat(),
//                    height - mDividerPadding,
//                    mDividerPaint
//                )
//            }
//        }
//
//        // draw underline
//        if (mUnderlineHeight > 0) {
//            mRectPaint.color = mUnderlineColor
//            if (mUnderlineGravity == Gravity.BOTTOM) {
//                canvas.drawRect(
//                    paddingLeft.toFloat(),
//                    height - mUnderlineHeight,
//                    (mTabsContainer.width + paddingLeft).toFloat(),
//                    height.toFloat(),
//                    mRectPaint
//                )
//            } else {
//                canvas.drawRect(
//                    paddingLeft.toFloat(),
//                    0f,
//                    (mTabsContainer.width + paddingLeft).toFloat(),
//                    mUnderlineHeight,
//                    mRectPaint
//                )
//            }
//        }
//
//        //draw indicator line
//        calcIndicatorRect()
//        if (mIndicatorStyle == STYLE_TRIANGLE) {
//            if (mIndicatorHeight > 0) {
//                mTrianglePaint.color = mIndicatorColor
//                mTrianglePath.reset()
//                mTrianglePath.moveTo(
//                    (paddingLeft + mIndicatorRect.left).toFloat(),
//                    height.toFloat()
//                )
//                mTrianglePath.lineTo(
//                    (paddingLeft + mIndicatorRect.left / 2 + mIndicatorRect.right / 2).toFloat(),
//                    height - mIndicatorHeight
//                )
//                mTrianglePath.lineTo(
//                    (paddingLeft + mIndicatorRect.right).toFloat(),
//                    height.toFloat()
//                )
//                mTrianglePath.close()
//                canvas.drawPath(mTrianglePath, mTrianglePaint)
//            }
//        } else if (mIndicatorStyle == STYLE_BLOCK) {
//            if (mIndicatorHeight < 0) {
//                mIndicatorHeight = height - indicatorMarginTop - indicatorMarginBottom
//            } else {
//            }
//            if (mIndicatorHeight > 0) {
//                if (mIndicatorCornerRadius < 0 || mIndicatorCornerRadius > mIndicatorHeight / 2) {
//                    mIndicatorCornerRadius = mIndicatorHeight / 2
//                }
//                mIndicatorDrawable.setColor(mIndicatorColor)
//                mIndicatorDrawable.setBounds(
//                    paddingLeft + indicatorMarginLeft.toInt() + mIndicatorRect.left,
//                    indicatorMarginTop.toInt(),
//                    (paddingLeft + mIndicatorRect.right - indicatorMarginRight).toInt(),
//                    (indicatorMarginTop + mIndicatorHeight).toInt()
//                )
//                mIndicatorDrawable.cornerRadius = mIndicatorCornerRadius
//                mIndicatorDrawable.draw(canvas)
//            }
//        } else {
//            /* mRectPaint.setColor(mIndicatorColor);
//                calcIndicatorRect();
//                canvas.drawRect(getPaddingLeft() + mIndicatorRect.left, getHeight() - mIndicatorHeight,
//                        mIndicatorRect.right + getPaddingLeft(), getHeight(), mRectPaint);*/
//            if (mIndicatorHeight > 0) {
//                mIndicatorDrawable.setColor(mIndicatorColor)
//                if (mIndicatorGravity == Gravity.BOTTOM) {
//                    mIndicatorDrawable.setBounds(
//                        paddingLeft + indicatorMarginLeft.toInt() + mIndicatorRect.left,
//                        height - mIndicatorHeight.toInt() - indicatorMarginBottom.toInt(),
//                        paddingLeft + mIndicatorRect.right - indicatorMarginRight.toInt(),
//                        height - indicatorMarginBottom.toInt()
//                    )
//                } else {
//                    mIndicatorDrawable.setBounds(
//                        paddingLeft + indicatorMarginLeft.toInt() + mIndicatorRect.left,
//                        indicatorMarginTop.toInt(),
//                        paddingLeft + mIndicatorRect.right - indicatorMarginRight.toInt(),
//                        mIndicatorHeight.toInt() + indicatorMarginTop.toInt()
//                    )
//                }
//                mIndicatorDrawable.cornerRadius = mIndicatorCornerRadius
//                mIndicatorDrawable.draw(canvas)
//            }
//        }
//    }
//
//    fun setCurrentTab(currentTab: Int, smoothScroll: Boolean) {
//        if (mCurrentTab != currentTab) {
//            mCurrentTab = currentTab
//            if (mViewPager != null) {
//                mViewPager!!.setCurrentItem(currentTab, smoothScroll)
//            }
//            if (mListener != null) {
//                mListener.onTabSelect(currentTab)
//            }
//        } else {
//            if (mListener != null) {
//                mListener.onTabReselect(currentTab)
//            }
//        }
//    }
//
//    fun setIndicatorGravity(indicatorGravity: Int) {
//        mIndicatorGravity = indicatorGravity
//        invalidate()
//    }
//
//    fun setIndicatorMargin(
//        indicatorMarginLeft: Float, indicatorMarginTop: Float,
//        indicatorMarginRight: Float, indicatorMarginBottom: Float
//    ) {
//        this.indicatorMarginLeft = dp2px(indicatorMarginLeft).toFloat()
//        this.indicatorMarginTop = dp2px(indicatorMarginTop).toFloat()
//        this.indicatorMarginRight = dp2px(indicatorMarginRight).toFloat()
//        this.indicatorMarginBottom = dp2px(indicatorMarginBottom).toFloat()
//        invalidate()
//    }
//
//    fun setIndicatorWidthEqualTitle(indicatorWidthEqualTitle: Boolean) {
//        mIndicatorWidthEqualTitle = indicatorWidthEqualTitle
//        invalidate()
//    }
//
//    fun setUnderlineGravity(underlineGravity: Int) {
//        mUnderlineGravity = underlineGravity
//        invalidate()
//    }
//
//    fun setTextSelectsize(textsize: Float) {
//        textSelectSize = sp2px(textsize).toFloat()
//        initTransformer()
//        updateTabStyles()
//    }
//
//    fun setTextUnselectSize(textSize: Int) {
//        textUnselectSize = textSize.toFloat()
//        initTransformer()
//        updateTabStyles()
//    }
//
//    fun setSnapOnTabClick(snapOnTabClick: Boolean) {
//        mSnapOnTabClick = snapOnTabClick
//    }
//
//    //setter and getter
//    var currentTab: Int
//        get() = mCurrentTab
//        set(currentTab) {
//            setCurrentTab(currentTab, !mSnapOnTabClick)
//        }
//
//    var indicatorStyle: Int
//        get() = mIndicatorStyle
//        set(indicatorStyle) {
//            mIndicatorStyle = indicatorStyle
//            invalidate()
//        }
//    var tabPadding: Float
//        get() = mTabPadding
//        set(tabPadding) {
//            mTabPadding = dp2px(tabPadding).toFloat()
//            updateTabStyles()
//        }
//    var isTabSpaceEqual: Boolean
//        get() = mTabSpaceEqual
//        set(tabSpaceEqual) {
//            mTabSpaceEqual = tabSpaceEqual
//            updateTabStyles()
//        }
//    var tabWidth: Float
//        get() = mTabWidth
//        set(tabWidth) {
//            mTabWidth = dp2px(tabWidth).toFloat()
//            updateTabStyles()
//        }
//    var indicatorColor: Int
//        get() = mIndicatorColor
//        set(indicatorColor) {
//            mIndicatorColor = indicatorColor
//            invalidate()
//        }
//    var indicatorHeight: Float
//        get() = mIndicatorHeight
//        set(indicatorHeight) {
//            mIndicatorHeight = dp2px(indicatorHeight).toFloat()
//            invalidate()
//        }
//    var indicatorWidth: Float
//        get() = mIndicatorWidth
//        set(indicatorWidth) {
//            mIndicatorWidth = dp2px(indicatorWidth).toFloat()
//            invalidate()
//        }
//    var indicatorCornerRadius: Float
//        get() = mIndicatorCornerRadius
//        set(indicatorCornerRadius) {
//            mIndicatorCornerRadius = dp2px(indicatorCornerRadius).toFloat()
//            invalidate()
//        }
//    var underlineColor: Int
//        get() = mUnderlineColor
//        set(underlineColor) {
//            mUnderlineColor = underlineColor
//            invalidate()
//        }
//    var underlineHeight: Float
//        get() = mUnderlineHeight
//        set(underlineHeight) {
//            mUnderlineHeight = dp2px(underlineHeight).toFloat()
//            invalidate()
//        }
//    var dividerColor: Int
//        get() = mDividerColor
//        set(dividerColor) {
//            mDividerColor = dividerColor
//            invalidate()
//        }
//    var dividerWidth: Float
//        get() = mDividerWidth
//        set(dividerWidth) {
//            mDividerWidth = dp2px(dividerWidth).toFloat()
//            invalidate()
//        }
//    var dividerPadding: Float
//        get() = mDividerPadding
//        set(dividerPadding) {
//            mDividerPadding = dp2px(dividerPadding).toFloat()
//            invalidate()
//        }
//    var textSelectColor: Int
//        get() = mTextSelectColor
//        set(textSelectColor) {
//            mTextSelectColor = textSelectColor
//            updateTabStyles()
//        }
//    var textUnselectColor: Int
//        get() = mTextUnSelectColor
//        set(textUnselectColor) {
//            mTextUnSelectColor = textUnselectColor
//            updateTabStyles()
//        }
//    var textBold: Int
//        get() = mTextBold
//        set(textBold) {
//            mTextBold = textBold
//            updateTabStyles()
//        }
//    var isTextAllCaps: Boolean
//        get() = mTextAllCaps
//        set(textAllCaps) {
//            mTextAllCaps = textAllCaps
//            updateTabStyles()
//        }
//
//    fun addViewPagerTransformer(transformer: IViewPagerTransformer?) {
//        extendTransformer.addViewPagerTransformer(transformer)
//    }
//
//    fun removeViewPagerTransformer(transformer: IViewPagerTransformer?) {
//        extendTransformer.removeViewPagerTransformer(transformer)
//    }
//
//    var transformers: List<Any?>?
//        get() = extendTransformer.getTransformers()
//        set(transformers) {
//            extendTransformer.setTransformers(transformers)
//        }
//
//    fun getTitleView(tab: Int): TextView {
//        val tabView = mTabsContainer.getChildAt(tab)
//        return tabView.findViewById<View>(R.id.tv_tab_title) as TextView
//    }
//
//    //setter and getter
//    // show MsgTipView
//    private val mTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
//    private val mInitSetMap = SparseBooleanArray()
//
//    /**
//     * 显示未读消息
//     *
//     * @param position 显示tab位置
//     * @param num      num小于等于0显示红点,num大于0显示数字
//     */
//    fun showMsg(position: Int, num: Int) {
//        var position = position
//        if (position >= tabCount) {
//            position = tabCount - 1
//        }
//        val tabView = mTabsContainer.getChildAt(position)
//        val tipView: MsgView = tabView.findViewById<View>(R.id.rtv_msg_tip) as MsgView
//        if (tipView != null) {
//            UnreadMsgUtils.show(tipView, num)
//            val params = tipView.getLayoutParams() as RelativeLayout.LayoutParams
//            if (openDmg) {
//                params.addRule(RelativeLayout.ALIGN_END, R.id.tv_tab_title_dmg)
//                params.addRule(RelativeLayout.ALIGN_TOP, R.id.tv_tab_title_dmg)
//            } else {
//                params.addRule(RelativeLayout.ALIGN_END, R.id.tv_tab_title)
//                params.addRule(RelativeLayout.ALIGN_TOP, R.id.tv_tab_title)
//            }
//
//            // 红点的位置
//            if (num <= 0) {
//                params.topMargin = mTabDotMarginTop
//                params.rightMargin = mTabDotMarginRight
//            } else {
//                params.topMargin = mTabMsgMarginTop
//                params.rightMargin = mTabMsgMarginRight
//            }
//            tipView.setLayoutParams(params)
//            if (mInitSetMap[position]) {
//                return
//            }
//            mInitSetMap.put(position, true)
//        }
//    }
//
//    /**
//     * 显示未读红点
//     *
//     * @param position 显示tab位置
//     */
//    fun showDot(position: Int) {
//        var position = position
//        if (position >= tabCount) {
//            position = tabCount - 1
//        }
//        showMsg(position, 0)
//    }
//
//    /**
//     * 隐藏未读消息
//     */
//    fun hideMsg(position: Int) {
//        var position = position
//        if (position >= tabCount) {
//            position = tabCount - 1
//        }
//        val tabView = mTabsContainer.getChildAt(position)
//        val tipView: MsgView = tabView.findViewById<View>(R.id.rtv_msg_tip) as MsgView
//        if (tipView != null) {
//            tipView.setVisibility(GONE)
//        }
//    }
//
//    /**
//     * 当前类只提供了少许设置未读消息属性的方法,可以通过该方法获取MsgView对象从而各种设置
//     */
//    fun getMsgView(position: Int): MsgView {
//        var position = position
//        if (position >= tabCount) {
//            position = tabCount - 1
//        }
//        val tabView = mTabsContainer.getChildAt(position)
//        return tabView.findViewById<View>(R.id.rtv_msg_tip) as MsgView
//    }
//
//    /**
//     * 当前类只提供了少许设置未读消息属性的方法,可以通过该方法获取MsgView对象从而各种设置
//     */
//    fun getTitle(position: Int): TextView? {
//        var position = position
//        if (position >= tabCount) {
//            position = tabCount - 1
//        }
//        val tabView = mTabsContainer.getChildAt(position) ?: return null
//        return tabView.findViewById<View>(R.id.tv_tab_title) as TextView
//    }
//
//    fun getDmgView(position: Int): ImageView? {
//        var position = position
//        if (position >= tabCount) {
//            position = tabCount - 1
//        }
//        val tabView = mTabsContainer.getChildAt(position) ?: return null
//
////        if (tabView.getVisibility() != View.GONE) {
////            return null;
////        }
//        return tabView.findViewById<View>(R.id.tv_tab_title_dmg) as ImageView
//    }
//
//    private var mListener: OnTabSelectListener? = null
//    fun setOnTabSelectListener(listener: OnTabSelectListener?) {
//        mListener = listener
//    }
//
//    internal inner class InnerPagerAdapter(
//        fm: FragmentManager?,
//        private val fragments: ArrayList<Fragment>,
//        private val titles: Array<String>
//    ) :
//        FragmentPagerAdapter(fm!!) {
//        override fun getCount(): Int {
//            return fragments.size
//        }
//
//        override fun getPageTitle(position: Int): CharSequence? {
//            return titles[position]
//        }
//
//        override fun getItem(position: Int): Fragment {
//            return fragments[position]
//        }
//
//        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
//            // 覆写destroyItem并且空实现,这样每个Fragment中的视图就不会被销毁
//            // super.destroyItem(container, position, object);
//        }
//
//        override fun getItemPosition(`object`: Any): Int {
//            return POSITION_NONE
//        }
//    }
//
//    override fun onSaveInstanceState(): Parcelable? {
//        val bundle = Bundle()
//        bundle.putParcelable("instanceState", super.onSaveInstanceState())
//        bundle.putInt("mCurrentTab", mCurrentTab)
//        return bundle
//    }
//
//    override fun onRestoreInstanceState(state: Parcelable) {
//        var state: Parcelable? = state
//        if (state is Bundle) {
//            val bundle = state
//            mCurrentTab = bundle.getInt("mCurrentTab")
//            state = bundle.getParcelable("instanceState")
//            if (mCurrentTab != 0 && mTabsContainer.childCount > 0) {
//                updateTabSelection(mCurrentTab)
//                scrollToCurrentTab()
//            }
//        }
//        super.onRestoreInstanceState(state)
//    }
//
//    protected fun dp2px(dp: Float): Int {
//        val scale = mContext.resources.displayMetrics.density
//        return (dp * scale + 0.5f).toInt()
//    }
//
//    protected fun sp2px(sp: Float): Int {
//        val scale = mContext.resources.displayMetrics.scaledDensity
//        return (sp * scale + 0.5f).toInt()
//    }
//
//    companion object {
//        private const val TOP = 0
//        private const val BOTTOM = 1
//        private const val CENTER = 2
//        private const val LEFT = 0
//        private const val RIGHT = 1
//        private const val STYLE_NORMAL = 0
//        private const val STYLE_TRIANGLE = 1
//        private const val STYLE_BLOCK = 2
//
//        /**
//         * title
//         */
//        private const val TEXT_BOLD_NONE = 0
//        private const val TEXT_BOLD_WHEN_SELECT = 1
//        private const val TEXT_BOLD_BOTH = 2
//    }
//
//    init {
//        isFillViewport = true //设置滚动视图是否可以伸缩其内容以填充视口
//        setWillNotDraw(false) //重写onDraw方法,需要调用这个方法来清除flag
//        clipChildren = false
//        clipToPadding = false
//        mContext = context
//        mTabsContainer = LinearLayout(context)
//        addView(mTabsContainer)
//        obtainAttributes(context, attrs)
//
//        //get layout_height
//        val height =
//            attrs!!.getAttributeValue("http://schemas.android.com/apk/res/android", "layout_height")
//        if (height == ViewGroup.LayoutParams.MATCH_PARENT.toString() + "") {
//        } else if (height == ViewGroup.LayoutParams.WRAP_CONTENT.toString() + "") {
//        } else {
//            val systemAttrs = intArrayOf(R.attr.layout_height)
//            val a = context.obtainStyledAttributes(attrs, systemAttrs)
//            mHeight = a.getDimensionPixelSize(0, ViewGroup.LayoutParams.WRAP_CONTENT)
//            a.recycle()
//        }
//    }
//}
