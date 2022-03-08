package cn.qpvd

import android.app.AlertDialog
import android.app.Dialog
import android.content.*
import android.graphics.Color
import android.net.ConnectivityManager
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.widget.*
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import android.widget.PopupWindow
import cn.qpvd.QPUtils.dip2px
import cn.qpvd.QPUtils.isWifiConnected
import java.lang.IllegalArgumentException

open class QPvdStd : QPvd {
    var backButton: ImageView? = null
    var bottomProgressBar: ProgressBar? = null
    var loadingProgressBar: ProgressBar? = null
    var titleTextView: TextView? = null
    var posterImageView: ImageView? = null
    var tinyBackImageView: ImageView? = null
    var batteryTimeLayout: LinearLayout? = null
    var batteryLevel: ImageView? = null
    var videoCurrentTime: TextView? = null
    var replayTextView: TextView? = null
    var clarity: TextView? = null
    var clarityPopWindow: PopupWindow? = null
    var mRetryBtn: TextView? = null
    var mRetryLayout: LinearLayout? = null
    var battertReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val action = intent.action
            if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {
                val level = intent.getIntExtra("level", 0)
                val scale = intent.getIntExtra("scale", 100)
                val percent = level * 100 / scale
                LAST_GET_BATTERYLEVEL_PERCENT = percent
                setBatteryLevel()
                try {
                    jzvdContext?.unregisterReceiver(this)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
    protected var mDismissControlViewTimerTask: DismissControlViewTimerTask? = null
    protected var mProgressDialog: Dialog? = null
    protected var mDialogProgressBar: ProgressBar? = null
    protected var mDialogSeekTime: TextView? = null
    protected var mDialogTotalTime: TextView? = null
    protected var mDialogIcon: ImageView? = null
    protected var mVolumeDialog: Dialog? = null
    protected var mDialogVolumeProgressBar: ProgressBar? = null
    protected var mDialogVolumeTextView: TextView? = null
    protected var mDialogVolumeImageView: ImageView? = null
    protected var mBrightnessDialog: Dialog? = null
    protected var mDialogBrightnessProgressBar: ProgressBar? = null
    protected var mDialogBrightnessTextView: TextView? = null
    protected var mIsWifi = false
    var wifiReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.action)) {
                val isWifi = isWifiConnected(context!!)
                if (mIsWifi == isWifi) return
                mIsWifi = isWifi
                if (!mIsWifi && !WIFI_TIP_DIALOG_SHOWED && state == STATE_PLAYING) {
                    startButton?.performClick()
                    showWifiDialog()
                }
            }
        }
    }
    protected var delayTask: ArrayDeque<Runnable> = ArrayDeque()

    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}

    override fun init(context: Context?) {
        super.init(context)
        batteryTimeLayout = findViewById(R.id.battery_time_layout)
        bottomProgressBar = findViewById(R.id.bottom_progress)
        titleTextView = findViewById(R.id.title)
        backButton = findViewById(R.id.back)
        posterImageView = findViewById(R.id.poster)
        loadingProgressBar = findViewById(R.id.loading)
        tinyBackImageView = findViewById(R.id.back_tiny)
        batteryLevel = findViewById(R.id.battery_level)
        videoCurrentTime = findViewById(R.id.video_current_time)
        replayTextView = findViewById(R.id.replay_text)
        clarity = findViewById(R.id.clarity)
        mRetryBtn = findViewById(R.id.retry_btn)
        mRetryLayout = findViewById(R.id.retry_layout)
        if (batteryTimeLayout == null) {
            batteryTimeLayout = LinearLayout(context)
        }
        if (bottomProgressBar == null) {
            bottomProgressBar = ProgressBar(context)
        }
        if (titleTextView == null) {
            titleTextView = TextView(context)
        }
        if (backButton == null) {
            backButton = ImageView(context)
        }
        if (posterImageView == null) {
            posterImageView = ImageView(context)
        }
        if (loadingProgressBar == null) {
            loadingProgressBar = ProgressBar(context)
        }
        if (tinyBackImageView == null) {
            tinyBackImageView = ImageView(context)
        }
        if (batteryLevel == null) {
            batteryLevel = ImageView(context)
        }
        if (videoCurrentTime == null) {
            videoCurrentTime = TextView(context)
        }
        if (replayTextView == null) {
            replayTextView = TextView(context)
        }
        if (clarity == null) {
            clarity = TextView(context)
        }
        if (mRetryBtn == null) {
            mRetryBtn = TextView(context)
        }
        if (mRetryLayout == null) {
            mRetryLayout = LinearLayout(context)
        }
        posterImageView?.setOnClickListener(this)
        backButton?.setOnClickListener(this)
        tinyBackImageView?.setOnClickListener(this)
        clarity?.setOnClickListener(this)
        mRetryBtn?.setOnClickListener(this)
    }

    override fun setUp(QPDataSource: QPDataSource?, screen: Int, mediaInterfaceClass: Class<*>?) {
        if (System.currentTimeMillis() - gobakFullscreenTime < 200) {
            return
        }
        if (System.currentTimeMillis() - gotoFullscreenTime < 200) {
            return
        }
        super.setUp(QPDataSource, screen, mediaInterfaceClass)
        titleTextView?.text = QPDataSource?.title
        setScreen(screen)
    }

    override fun changeUrl(QPDataSource: QPDataSource?, seekToInAdvance: Long) {
        super.changeUrl(QPDataSource, seekToInAdvance)
        titleTextView?.text = QPDataSource?.title
    }

    fun changeStartButtonSize(size: Int) {
        var lp = startButton?.layoutParams
        lp?.height = size
        lp?.width = size
        lp = loadingProgressBar?.layoutParams
        lp?.height = size
        lp?.width = size
    }

    override val layoutId: Int
        get() = R.layout.jz_layout_std

    override fun onStateNormal() {
        super.onStateNormal()
        changeUiToNormal()
    }

    override fun onStatePreparing() {
        super.onStatePreparing()
        changeUiToPreparing()
    }

    override fun onStatePreparingPlaying() {
        super.onStatePreparingPlaying()
        changeUIToPreparingPlaying()
    }

    override fun onStatePreparingChangeUrl() {
        super.onStatePreparingChangeUrl()
        changeUIToPreparingChangeUrl()
    }

    override fun onStatePlaying() {
        super.onStatePlaying()
        changeUiToPlayingClear()
    }

    override fun onStatePause() {
        super.onStatePause()
        changeUiToPauseShow()
        cancelDismissControlViewTimer()
    }

    override fun onStateError() {
        super.onStateError()
        changeUiToError()
    }

    override fun onStateAutoComplete() {
        super.onStateAutoComplete()
        changeUiToComplete()
        cancelDismissControlViewTimer()
        bottomProgressBar?.progress = 100
    }

    override fun startVideo() {
        super.startVideo()
        registerWifiListener(applicationContext)
    }

    /**
     * 双击
     */
    protected var gestureDetector: GestureDetector =
        GestureDetector(context.applicationContext, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent?): Boolean {
                if (state == STATE_PLAYING || state == STATE_PAUSE) {
                    Log.d(TAG, "doublClick [" + this.hashCode() + "] ")
                    startButton?.performClick()
                }
                return super.onDoubleTap(e)
            }

            override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
                if (!mChangePosition && !mChangeVolume) {
                    onClickUiToggle()
                }
                return super.onSingleTapConfirmed(e)
            }

            override fun onLongPress(e: MotionEvent?) {
                super.onLongPress(e)
            }
        })

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        val id = v.id
        if (id == R.id.surface_container) {
            when (event.action) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                }
                MotionEvent.ACTION_UP -> {
                    startDismissControlViewTimer()
                    if (mChangePosition) {
                        val duration = duration
                        val progress =
                            (mSeekTimePosition * 100 / if (duration == 0L) 1 else duration).toInt()
                        bottomProgressBar?.progress = progress
                    }
                }
            }
            gestureDetector.onTouchEvent(event)
        } else if (id == R.id.bottom_seek_progress) {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> cancelDismissControlViewTimer()
                MotionEvent.ACTION_UP -> startDismissControlViewTimer()
            }
        }
        return super.onTouch(v, event)
    }

    override fun onClick(v: View) {
        super.onClick(v)
        val i = v.id
        if (i == R.id.poster) {
            clickPoster()
        } else if (i == R.id.surface_container) {
            clickSurfaceContainer()
            if (clarityPopWindow != null) {
                clarityPopWindow?.dismiss()
            }
        } else if (i == R.id.back) {
            clickBack()
        } else if (i == R.id.back_tiny) {
            clickBackTiny()
        } else if (i == R.id.clarity) {
            clickClarity()
        } else if (i == R.id.retry_btn) {
            clickRetryBtn()
        }
    }

    protected fun clickRetryBtn() {
        if (QPDataSource?.urlsMap?.isEmpty()!! || QPDataSource?.currentUrl == null) {
            Toast.makeText(jzvdContext, resources.getString(R.string.no_url), Toast.LENGTH_SHORT)
                .show()
            return
        }
        if (!QPDataSource?.currentUrl.toString()
                .startsWith("file") && !QPDataSource?.currentUrl.toString().startsWith("/") &&
            !isWifiConnected(jzvdContext!!) && !WIFI_TIP_DIALOG_SHOWED
        ) {
            showWifiDialog()
            return
        }
        seekToInAdvance = mCurrentPosition
        startVideo()
    }

    protected fun clickClarity() {
        onCLickUiToggleToClear()
        val inflater = jzvdContext?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout = inflater.inflate(R.layout.jz_layout_clarity, null) as LinearLayout
        val mQualityListener = OnClickListener { v1: View ->
            val index = v1.tag as Int

//                this.seekToInAdvance = getCurrentPositionWhenPlaying();
            QPDataSource?.currentUrlIndex = index
            //                onStatePreparingChangeUrl();
            changeUrl(QPDataSource, currentPositionWhenPlaying)
            clarity?.text = QPDataSource?.currentKey.toString()
            for (j in 0 until layout.childCount) { //设置点击之后的颜色
                if (j == QPDataSource?.currentUrlIndex) {
                    (layout.getChildAt(j) as TextView).setTextColor(
                        Color.parseColor(
                            "#fff85959"
                        )
                    )
                } else {
                    (layout.getChildAt(j) as TextView).setTextColor(
                        Color.parseColor(
                            "#ffffff"
                        )
                    )
                }
            }
            if (clarityPopWindow != null) {
                clarityPopWindow?.dismiss()
            }
        }
        for (j in 0 until QPDataSource?.urlsMap?.size!!) {
            val key = QPDataSource?.getKeyFromDataSource(j)
            val clarityItem =
                inflate(jzvdContext, R.layout.jz_layout_clarity_item, null) as TextView
            clarityItem.text = key
            clarityItem.tag = j
            layout.addView(clarityItem, j)
            clarityItem.setOnClickListener(mQualityListener)
            if (j == QPDataSource?.currentUrlIndex) {
                clarityItem.setTextColor(Color.parseColor("#fff85959"))
            }
        }
        clarityPopWindow =
            PopupWindow(layout, dip2px(jzvdContext!!, 240f), LayoutParams.MATCH_PARENT, true)
        clarityPopWindow?.contentView = layout
        clarityPopWindow?.animationStyle = R.style.pop_animation
        clarityPopWindow?.showAtLocation(textureViewContainer, Gravity.END, 0, 0)
        //            int offsetX = clarity.getMeasuredWidth() / 3;
//            int offsetY = clarity.getMeasuredHeight() / 3;
//            clarityPopWindow.update(clarity, -offsetX, -offsetY, Math.round(layout.getMeasuredWidth() * 2), layout.getMeasuredHeight());
    }

    protected fun clickBackTiny() {
        clearFloatScreen()
    }

    protected fun clickBack() {
        backPress()
    }

    protected fun clickSurfaceContainer() {
        startDismissControlViewTimer()
    }

    protected fun clickPoster() {
        if (QPDataSource == null || QPDataSource?.urlsMap?.isEmpty()!! || QPDataSource?.currentUrl == null) {
            Toast.makeText(jzvdContext, resources.getString(R.string.no_url), Toast.LENGTH_SHORT)
                .show()
            return
        }
        if (state == STATE_NORMAL) {
            if (!QPDataSource?.currentUrl.toString().startsWith("file") &&
                !QPDataSource?.currentUrl.toString().startsWith("/") &&
                !isWifiConnected(jzvdContext!!) && !WIFI_TIP_DIALOG_SHOWED
            ) {
                showWifiDialog()
                return
            }
            startVideo()
        } else if (state == STATE_AUTO_COMPLETE) {
            onClickUiToggle()
        }
    }

    override fun setScreenNormal() {
        super.setScreenNormal()
        fullscreenButton?.setImageResource(R.drawable.jz_enlarge)
        backButton?.visibility = GONE
        tinyBackImageView?.visibility = INVISIBLE
        changeStartButtonSize(
            resources.getDimension(R.dimen.jz_start_button_w_h_normal)
                .toInt()
        )
        batteryTimeLayout?.visibility = GONE
        clarity?.visibility = GONE
    }

    override fun setScreenFullscreen() {
        super.setScreenFullscreen()
        //进入全屏之后要保证原来的播放状态和ui状态不变，改变个别的ui
        fullscreenButton?.setImageResource(R.drawable.jz_shrink)
        backButton?.visibility = VISIBLE
        tinyBackImageView?.visibility = INVISIBLE
        batteryTimeLayout?.visibility = VISIBLE
        if (QPDataSource?.urlsMap?.size == 1) {
            clarity?.visibility = GONE
        } else {
            clarity?.text = QPDataSource?.currentKey.toString()
            clarity?.visibility = VISIBLE
        }
        changeStartButtonSize(
            resources.getDimension(R.dimen.jz_start_button_w_h_fullscreen)
                .toInt()
        )
        setSystemTimeAndBattery()
    }

    override fun setScreenTiny() {
        super.setScreenTiny()
        tinyBackImageView?.visibility = VISIBLE
        setAllControlsVisiblity(
            INVISIBLE, INVISIBLE, INVISIBLE,
            INVISIBLE, INVISIBLE, INVISIBLE, INVISIBLE
        )
        batteryTimeLayout?.visibility = GONE
        clarity?.visibility = GONE
    }

    override fun showWifiDialog() {
        super.showWifiDialog()
        val builder = AlertDialog.Builder(jzvdContext)
        builder.setMessage(resources.getString(R.string.tips_not_wifi))
        builder.setPositiveButton(
            resources.getString(R.string.tips_not_wifi_confirm)
        ) { dialog: DialogInterface, which: Int ->
            dialog.dismiss()
            WIFI_TIP_DIALOG_SHOWED = true
            if (state == STATE_PAUSE) {
                startButton?.performClick()
            } else {
                startVideo()
            }
        }
        builder.setNegativeButton(
            resources.getString(R.string.tips_not_wifi_cancel)
        ) { dialog: DialogInterface, which: Int ->
            dialog.dismiss()
            releaseAllVideos()
            clearFloatScreen()
        }
        builder.setOnCancelListener(object : DialogInterface.OnCancelListener {
            override fun onCancel(dialog: DialogInterface) {
                dialog.dismiss()
                releaseAllVideos()
                clearFloatScreen()
            }
        })
        builder.create().show()
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {
        super.onStartTrackingTouch(seekBar)
        cancelDismissControlViewTimer()
    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {
        super.onStopTrackingTouch(seekBar)
        startDismissControlViewTimer()
    }

    open fun onClickUiToggle() { //这是事件
        if (bottomContainer?.visibility != VISIBLE) {
            setSystemTimeAndBattery()
            clarity?.text = QPDataSource?.currentKey.toString()
        }
        if (state == STATE_PREPARING) {
            changeUiToPreparing()
            if (bottomContainer?.visibility == VISIBLE) {
            } else {
                setSystemTimeAndBattery()
            }
        } else if (state == STATE_PLAYING) {
            if (bottomContainer?.visibility == VISIBLE) {
                changeUiToPlayingClear()
            } else {
                changeUiToPlayingShow()
            }
        } else if (state == STATE_PAUSE) {
            if (bottomContainer?.visibility == VISIBLE) {
                changeUiToPauseClear()
            } else {
                changeUiToPauseShow()
            }
        }
    }

    fun setSystemTimeAndBattery() {
        val dateFormater = SimpleDateFormat("HH:mm")
        val date = Date()
        videoCurrentTime?.text = dateFormater.format(date)
        if (System.currentTimeMillis() - LAST_GET_BATTERYLEVEL_TIME > 30000) {
            LAST_GET_BATTERYLEVEL_TIME = System.currentTimeMillis()
            jzvdContext?.registerReceiver(
                battertReceiver,
                IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            )
        } else {
            setBatteryLevel()
        }
    }

    fun setBatteryLevel() {
        val percent = LAST_GET_BATTERYLEVEL_PERCENT
        if (percent < 15) {
            batteryLevel?.setBackgroundResource(R.drawable.jz_battery_level_10)
        } else if (percent >= 15 && percent < 40) {
            batteryLevel?.setBackgroundResource(R.drawable.jz_battery_level_30)
        } else if (percent >= 40 && percent < 60) {
            batteryLevel?.setBackgroundResource(R.drawable.jz_battery_level_50)
        } else if (percent >= 60 && percent < 80) {
            batteryLevel?.setBackgroundResource(R.drawable.jz_battery_level_70)
        } else if (percent >= 80 && percent < 95) {
            batteryLevel?.setBackgroundResource(R.drawable.jz_battery_level_90)
        } else if (percent >= 95 && percent <= 100) {
            batteryLevel?.setBackgroundResource(R.drawable.jz_battery_level_100)
        }
    }

    //** 和onClickUiToggle重复，要干掉
    fun onCLickUiToggleToClear() {
        if (state == STATE_PREPARING) {
            if (bottomContainer?.visibility == VISIBLE) {
                changeUiToPreparing()
            } else {
            }
        } else if (state == STATE_PLAYING) {
            if (bottomContainer?.visibility == VISIBLE) {
                changeUiToPlayingClear()
            } else {
            }
        } else if (state == STATE_PAUSE) {
            if (bottomContainer?.visibility == VISIBLE) {
                changeUiToPauseClear()
            } else {
            }
        } else if (state == STATE_AUTO_COMPLETE) {
            if (bottomContainer?.visibility == VISIBLE) {
                changeUiToComplete()
            } else {
            }
        }
    }

    override fun onProgress(progress: Int, position: Long, duration: Long) {
        super.onProgress(progress, position, duration)
        bottomProgressBar?.progress = progress
    }

    override fun setBufferProgress(bufferProgress: Int) {
        super.setBufferProgress(bufferProgress)
        bottomProgressBar?.secondaryProgress = bufferProgress
    }

    override fun resetProgressAndTime() {
        super.resetProgressAndTime()
        bottomProgressBar?.progress = 0
        bottomProgressBar?.secondaryProgress = 0
    }

    open fun changeUiToNormal() {
        when (screen) {
            SCREEN_NORMAL, SCREEN_FULLSCREEN -> {
                setAllControlsVisiblity(
                    VISIBLE, INVISIBLE, VISIBLE,
                    INVISIBLE, VISIBLE, INVISIBLE, INVISIBLE
                )
                updateStartImage()
            }
            SCREEN_TINY -> {
            }
        }
    }

    open fun changeUiToPreparing() {
        when (screen) {
            SCREEN_NORMAL, SCREEN_FULLSCREEN -> {
                setAllControlsVisiblity(
                    INVISIBLE, INVISIBLE, INVISIBLE,
                    VISIBLE, VISIBLE, INVISIBLE, INVISIBLE
                )
                updateStartImage()
            }
            SCREEN_TINY -> {
            }
        }
    }

    fun changeUIToPreparingPlaying() {
        when (screen) {
            SCREEN_NORMAL, SCREEN_FULLSCREEN -> {
                setAllControlsVisiblity(
                    VISIBLE, VISIBLE, INVISIBLE,
                    VISIBLE, INVISIBLE, INVISIBLE, INVISIBLE
                )
                updateStartImage()
            }
            SCREEN_TINY -> {
            }
        }
    }

    fun changeUIToPreparingChangeUrl() {
        when (screen) {
            SCREEN_NORMAL, SCREEN_FULLSCREEN -> {
                setAllControlsVisiblity(
                    INVISIBLE, INVISIBLE, INVISIBLE,
                    VISIBLE, VISIBLE, INVISIBLE, INVISIBLE
                )
                updateStartImage()
            }
            SCREEN_TINY -> {
            }
        }
    }

    open fun changeUiToPlayingShow() {
        when (screen) {
            SCREEN_NORMAL, SCREEN_FULLSCREEN -> {
                setAllControlsVisiblity(
                    VISIBLE, VISIBLE, VISIBLE,
                    INVISIBLE, INVISIBLE, INVISIBLE, INVISIBLE
                )
                updateStartImage()
            }
            SCREEN_TINY -> {
            }
        }
    }

    open fun changeUiToPlayingClear() {
        when (screen) {
            SCREEN_NORMAL, SCREEN_FULLSCREEN -> setAllControlsVisiblity(
                INVISIBLE, INVISIBLE, INVISIBLE,
                INVISIBLE, INVISIBLE, VISIBLE, INVISIBLE
            )
            SCREEN_TINY -> {
            }
        }
    }

    open fun changeUiToPauseShow() {
        when (screen) {
            SCREEN_NORMAL, SCREEN_FULLSCREEN -> {
                setAllControlsVisiblity(
                    VISIBLE, VISIBLE, VISIBLE,
                    INVISIBLE, INVISIBLE, INVISIBLE, INVISIBLE
                )
                updateStartImage()
            }
            SCREEN_TINY -> {
            }
        }
    }

    open fun changeUiToPauseClear() {
        when (screen) {
            SCREEN_NORMAL, SCREEN_FULLSCREEN -> setAllControlsVisiblity(
                INVISIBLE, INVISIBLE, INVISIBLE,
                INVISIBLE, INVISIBLE, VISIBLE, INVISIBLE
            )
            SCREEN_TINY -> {
            }
        }
    }

    open fun changeUiToComplete() {
        when (screen) {
            SCREEN_NORMAL, SCREEN_FULLSCREEN -> {
                setAllControlsVisiblity(
                    VISIBLE, INVISIBLE, VISIBLE,
                    INVISIBLE, VISIBLE, INVISIBLE, INVISIBLE
                )
                updateStartImage()
            }
            SCREEN_TINY -> {
            }
        }
    }

    open fun changeUiToError() {
        when (screen) {
            SCREEN_NORMAL -> {
                setAllControlsVisiblity(
                    INVISIBLE, INVISIBLE, VISIBLE,
                    INVISIBLE, INVISIBLE, INVISIBLE, VISIBLE
                )
                updateStartImage()
            }
            SCREEN_FULLSCREEN -> {
                setAllControlsVisiblity(
                    VISIBLE, INVISIBLE, VISIBLE,
                    INVISIBLE, INVISIBLE, INVISIBLE, VISIBLE
                )
                updateStartImage()
            }
            SCREEN_TINY -> {
            }
        }
    }

    fun setAllControlsVisiblity(
        topCon: Int, bottomCon: Int, startBtn: Int, loadingPro: Int,
        posterImg: Int, bottomPro: Int, retryLayout: Int
    ) {
        topContainer?.visibility = topCon
        bottomContainer?.visibility = bottomCon
        startButton?.visibility = startBtn
        loadingProgressBar?.visibility = loadingPro
        posterImageView?.visibility = posterImg
        bottomProgressBar?.visibility = bottomPro
        mRetryLayout?.visibility = retryLayout
    }

    fun updateStartImage() {
        if (state == STATE_PLAYING) {
            startButton?.visibility = VISIBLE
            startButton?.setImageResource(R.drawable.jz_click_pause_selector)
            replayTextView?.visibility = GONE
        } else if (state == STATE_ERROR) {
            startButton?.visibility = INVISIBLE
            replayTextView?.visibility = GONE
        } else if (state == STATE_AUTO_COMPLETE) {
            startButton?.visibility = VISIBLE
            startButton?.setImageResource(R.drawable.jz_click_replay_selector)
            replayTextView?.visibility = VISIBLE
        } else {
            startButton?.setImageResource(R.drawable.jz_click_play_selector)
            replayTextView?.visibility = GONE
        }
    }

    override fun showProgressDialog(
        deltaX: Float,
        seekTime: String?,
        seekTimePosition: Long,
        totalTime: String?,
        totalTimeDuration: Long
    ) {
        super.showProgressDialog(deltaX, seekTime, seekTimePosition, totalTime, totalTimeDuration)
        if (mProgressDialog == null) {
            val localView: View =
                LayoutInflater.from(jzvdContext).inflate(R.layout.jz_dialog_progress, null)
            mDialogProgressBar = localView.findViewById(R.id.duration_progressbar)
            mDialogSeekTime = localView.findViewById(R.id.tv_current)
            mDialogTotalTime = localView.findViewById(R.id.tv_duration)
            mDialogIcon = localView.findViewById(R.id.duration_image_tip)
            mProgressDialog = createDialogWithView(localView)
        }
        if (!mProgressDialog?.isShowing!!) {
            mProgressDialog?.show()
        }
        mDialogSeekTime?.text = seekTime
        mDialogTotalTime?.text = " / $totalTime"
        mDialogProgressBar?.progress =
            if (totalTimeDuration <= 0) 0 else (seekTimePosition * 100 / totalTimeDuration).toInt()
        if (deltaX > 0) {
            mDialogIcon?.setBackgroundResource(R.drawable.jz_forward_icon)
        } else {
            mDialogIcon?.setBackgroundResource(R.drawable.jz_backward_icon)
        }
        onCLickUiToggleToClear()
    }

    override fun dismissProgressDialog() {
        super.dismissProgressDialog()
        if (mProgressDialog != null) {
            mProgressDialog?.dismiss()
        }
    }

    override fun showVolumeDialog(deltaY: Float, volumePercent: Int) {
        var volumePercent = volumePercent
        super.showVolumeDialog(deltaY, volumePercent)
        if (mVolumeDialog == null) {
            val localView: View =
                LayoutInflater.from(jzvdContext).inflate(R.layout.jz_dialog_volume, null)
            mDialogVolumeImageView = localView.findViewById(R.id.volume_image_tip)
            mDialogVolumeTextView = localView.findViewById(R.id.tv_volume)
            mDialogVolumeProgressBar = localView.findViewById(R.id.volume_progressbar)
            mVolumeDialog = createDialogWithView(localView)
        }
        if (!mVolumeDialog?.isShowing!!) {
            mVolumeDialog?.show()
        }
        if (volumePercent <= 0) {
            mDialogVolumeImageView?.setBackgroundResource(R.drawable.jz_close_volume)
        } else {
            mDialogVolumeImageView?.setBackgroundResource(R.drawable.jz_add_volume)
        }
        if (volumePercent > 100) {
            volumePercent = 100
        } else if (volumePercent < 0) {
            volumePercent = 0
        }
        mDialogVolumeTextView?.text = "$volumePercent%"
        mDialogVolumeProgressBar?.progress = volumePercent
        onCLickUiToggleToClear()
    }

    override fun dismissVolumeDialog() {
        super.dismissVolumeDialog()
        if (mVolumeDialog != null) {
            mVolumeDialog?.dismiss()
        }
    }

    override fun showBrightnessDialog(brightnessPercent: Int) {
        var brightnessPercent = brightnessPercent
        super.showBrightnessDialog(brightnessPercent)
        if (mBrightnessDialog == null) {
            val localView: View =
                LayoutInflater.from(jzvdContext).inflate(R.layout.jz_dialog_brightness, null)
            mDialogBrightnessTextView = localView.findViewById(R.id.tv_brightness)
            mDialogBrightnessProgressBar = localView.findViewById(R.id.brightness_progressbar)
            mBrightnessDialog = createDialogWithView(localView)
        }
        if (!mBrightnessDialog?.isShowing!!) {
            mBrightnessDialog?.show()
        }
        if (brightnessPercent > 100) {
            brightnessPercent = 100
        } else if (brightnessPercent < 0) {
            brightnessPercent = 0
        }
        mDialogBrightnessTextView?.text = "$brightnessPercent%"
        mDialogBrightnessProgressBar?.progress = brightnessPercent
        onCLickUiToggleToClear()
    }

    override fun dismissBrightnessDialog() {
        super.dismissBrightnessDialog()
        if (mBrightnessDialog != null) {
            mBrightnessDialog?.dismiss()
        }
    }

    fun createDialogWithView(localView: View?): Dialog {
        val dialog = Dialog(jzvdContext!!, R.style.jz_style_dialog_progress)
        dialog.setContentView(localView!!)
        val window = dialog.window
        window?.addFlags(Window.FEATURE_ACTION_BAR)
        window?.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL)
        window?.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        window?.setLayout(-2, -2)
        val localLayoutParams = window?.attributes
        localLayoutParams?.gravity = Gravity.CENTER
        window?.attributes = localLayoutParams
        return dialog
    }

    fun startDismissControlViewTimer() {
        cancelDismissControlViewTimer()
        DISMISS_CONTROL_VIEW_TIMER = Timer()
        mDismissControlViewTimerTask = DismissControlViewTimerTask()
        DISMISS_CONTROL_VIEW_TIMER?.schedule(mDismissControlViewTimerTask, 2500)
    }

    fun cancelDismissControlViewTimer() {
        if (DISMISS_CONTROL_VIEW_TIMER != null) {
            DISMISS_CONTROL_VIEW_TIMER?.cancel()
        }
        if (mDismissControlViewTimerTask != null) {
            mDismissControlViewTimerTask?.cancel()
        }
    }

    override fun onCompletion() {
        super.onCompletion()
        cancelDismissControlViewTimer()
    }

    override fun reset() {
        super.reset()
        cancelDismissControlViewTimer()
        unregisterWifiListener(applicationContext)
    }

    fun dissmissControlView() {
        if (state != STATE_NORMAL && state != STATE_ERROR && state != STATE_AUTO_COMPLETE) {
            post {
                bottomContainer?.visibility = INVISIBLE
                topContainer?.visibility = INVISIBLE
                startButton?.visibility = INVISIBLE
                if (screen != SCREEN_TINY) {
                    bottomProgressBar?.visibility = VISIBLE
                }
            }
        }
    }

    fun registerWifiListener(context: Context?) {
        if (context == null) return
        mIsWifi = isWifiConnected(context)
        val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        context.registerReceiver(wifiReceiver, intentFilter)
    }

    fun unregisterWifiListener(context: Context?) {
        if (context == null) return
        try {
            context.unregisterReceiver(wifiReceiver)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        }
    }

    inner class DismissControlViewTimerTask : TimerTask() {
        override fun run() {
            dissmissControlView()
        }
    }

    companion object {
        var LAST_GET_BATTERYLEVEL_TIME: Long = 0
        var LAST_GET_BATTERYLEVEL_PERCENT = 70
        protected var DISMISS_CONTROL_VIEW_TIMER: Timer? = null
    }
}


