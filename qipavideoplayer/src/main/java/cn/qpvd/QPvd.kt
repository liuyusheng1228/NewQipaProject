package cn.qpvd

import android.content.Context
import android.content.pm.ActivityInfo
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.provider.Settings
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.widget.*
import cn.qpvd.QPUtils.getSavedProgress
import cn.qpvd.QPUtils.getScreenWidth
import cn.qpvd.QPUtils.getStatusBarHeight
import cn.qpvd.QPUtils.getWindow
import cn.qpvd.QPUtils.hideStatusBar
import cn.qpvd.QPUtils.hideSystemUI
import cn.qpvd.QPUtils.isWifiConnected
import cn.qpvd.QPUtils.saveProgress
import cn.qpvd.QPUtils.scanForActivity
import cn.qpvd.QPUtils.setRequestedOrientation
import cn.qpvd.QPUtils.showStatusBar
import cn.qpvd.QPUtils.showSystemUI
import cn.qpvd.QPUtils.stringForTime
import java.lang.Exception
import java.lang.IllegalStateException
import java.lang.reflect.Constructor
import java.lang.reflect.InvocationTargetException
import java.util.*

/**
 * Created by Nathen on 16/7/30.
 */
abstract class QPvd : FrameLayout, View.OnClickListener, SeekBar.OnSeekBarChangeListener,
    View.OnTouchListener {
    var state = -1
    var screen = -1
    var QPDataSource: QPDataSource? = null
    var widthRatio = 0
    var heightRatio = 0
    var mediaInterfaceClass: Class<*>? = null
    var mediaInterface: QPMediaInterface? = null
    var positionInList = -1 //很想干掉它
    var videoRotation = 0
    var seekToManulPosition = -1
    var seekToInAdvance: Long = 0
    var startButton: ImageView? = null
    var progressBar: SeekBar? = null
    var fullscreenButton: ImageView? = null
    var currentTimeTextView: TextView? = null
    var totalTimeTextView: TextView? = null
    var textureViewContainer: ViewGroup? = null
    var topContainer: ViewGroup? = null
    var bottomContainer: ViewGroup? = null
    var textureView: QPTextureView? = null
    var preloading = false
    protected var gobakFullscreenTime: Long = 0 //这个应该重写一下，刷新列表，新增列表的刷新，不打断播放，应该是个flag
    protected var gotoFullscreenTime: Long = 0
    protected var UPDATE_PROGRESS_TIMER: Timer? = null
    protected var mScreenWidth = 0
    protected var mScreenHeight = 0
    protected var mAudioManager: AudioManager? = null
    protected var mProgressTimerTask: ProgressTimerTask? = null
    protected var mTouchingProgressBar = false
    protected var mDownX = 0f
    protected var mDownY = 0f
    protected var mChangeVolume = false
    protected var mChangePosition = false
    protected var mChangeBrightness = false
    protected var mGestureDownPosition: Long = 0
    protected var mGestureDownVolume = 0
    protected var mGestureDownBrightness = 0f
    protected var mSeekTimePosition: Long = 0
    protected var jzvdContext: Context? = null
    protected var mCurrentPosition: Long = 0

    /**
     * 如果不在列表中可以不加block
     */
    protected var blockLayoutParams: ViewGroup.LayoutParams? = null
    protected var blockIndex = 0
    protected var blockWidth = 0
    protected var blockHeight = 0

    constructor(context: Context?) : super(context!!) {
        init(context)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    ) {
        init(context!!)
    }

    abstract val layoutId: Int

    open fun init(context: Context?) {
        inflate(context, layoutId, this)
        jzvdContext = context
        startButton = findViewById(R.id.start)
        fullscreenButton = findViewById(R.id.fullscreen)
        progressBar = findViewById(R.id.bottom_seek_progress)
        currentTimeTextView = findViewById(R.id.current)
        totalTimeTextView = findViewById(R.id.total)
        bottomContainer = findViewById(R.id.layout_bottom)
        textureViewContainer = findViewById(R.id.surface_container)
        topContainer = findViewById(R.id.layout_top)
        if (startButton == null) {
            startButton = ImageView(context)
        }
        if (fullscreenButton == null) {
            fullscreenButton = ImageView(context)
        }
        if (progressBar == null) {
            progressBar = SeekBar(context)
        }
        if (currentTimeTextView == null) {
            currentTimeTextView = TextView(context)
        }
        if (totalTimeTextView == null) {
            totalTimeTextView = TextView(context)
        }
        if (bottomContainer == null) {
            bottomContainer = LinearLayout(context)
        }
        if (textureViewContainer == null) {
            textureViewContainer = FrameLayout(context!!)
        }
        if (topContainer == null) {
            topContainer = RelativeLayout(context)
        }
        startButton?.setOnClickListener(this)
        fullscreenButton?.setOnClickListener(this)
        progressBar?.setOnSeekBarChangeListener(this)
        bottomContainer?.setOnClickListener(this)
        textureViewContainer?.setOnClickListener(this)
        textureViewContainer?.setOnTouchListener(this)
        mScreenWidth = getContext().resources.displayMetrics.widthPixels
        mScreenHeight = getContext().resources.displayMetrics.heightPixels
        state = STATE_IDLE
    }

    fun setUp(url: String?, title: String?) {
        setUp(QPDataSource(url, title), SCREEN_NORMAL)
    }

    fun setUp(url: String?, title: String?, screen: Int) {
        setUp(QPDataSource(url, title), screen)
    }

    fun setUp(QPDataSource: QPDataSource?, screen: Int) {
        setUp(QPDataSource, screen, QPMediaSystem::class.java)
    }

    fun setUp(url: String?, title: String?, screen: Int, mediaInterfaceClass: Class<*>?) {
        setUp(QPDataSource(url, title), screen, mediaInterfaceClass)
    }

    open fun setUp(QPDataSource: QPDataSource?, screen: Int, mediaInterfaceClass: Class<*>?) {
        this.QPDataSource = QPDataSource
        this.screen = screen
        onStateNormal()
        this.mediaInterfaceClass = mediaInterfaceClass
    }

    fun setMediaInterface(mediaInterfaceClass: Class<*>?) {
        reset()
        this.mediaInterfaceClass = mediaInterfaceClass
    }

    override fun onClick(v: View) {
        val i = v.id
        if (i == R.id.start) {
            clickStart()
        } else if (i == R.id.fullscreen) {
            clickFullscreen()
        }
    }

    protected fun clickFullscreen() {
        Log.i(TAG, "onClick fullscreen [" + this.hashCode() + "] ")
        if (state == STATE_AUTO_COMPLETE) return
        if (screen == SCREEN_FULLSCREEN) {
            //quit fullscreen
            backPress()
        } else {
            Log.d(TAG, "toFullscreenActivity [" + this.hashCode() + "] ")
            gotoFullscreen()
        }
    }

    protected fun clickStart() {
        Log.i(TAG, "onClick start [" + this.hashCode() + "] ")
        if (QPDataSource == null || QPDataSource!!.urlsMap.isEmpty() || QPDataSource?.currentUrl == null) {
            Toast.makeText(context, resources.getString(R.string.no_url), Toast.LENGTH_SHORT).show()
            return
        }
        if (state == STATE_NORMAL) {
            if (!QPDataSource?.currentUrl.toString()
                    .startsWith("file") && !QPDataSource?.currentUrl.toString().startsWith("/") &&
                !isWifiConnected(context) && !WIFI_TIP_DIALOG_SHOWED
            ) { //这个可以放到std中
                showWifiDialog()
                return
            }
            startVideo()
        } else if (state == STATE_PLAYING) {
            Log.d(TAG, "pauseVideo [" + this.hashCode() + "] ")
            mediaInterface?.pause()
            onStatePause()
        } else if (state == STATE_PAUSE) {
            mediaInterface?.start()
            onStatePlaying()
        } else if (state == STATE_AUTO_COMPLETE) {
            startVideo()
        }
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        val id = v.id
        if (id == R.id.surface_container) {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> touchActionDown(x, y)
                MotionEvent.ACTION_MOVE -> touchActionMove(x, y)
                MotionEvent.ACTION_UP -> touchActionUp()
            }
        }
        return false
    }

    protected fun touchActionUp() {
        Log.i(TAG, "onTouch surfaceContainer actionUp [" + this.hashCode() + "] ")
        mTouchingProgressBar = false
        dismissProgressDialog()
        dismissVolumeDialog()
        dismissBrightnessDialog()
        if (mChangePosition) {
            mediaInterface?.seekTo(mSeekTimePosition)
            val duration = duration
            val progress = (mSeekTimePosition * 100 / if (duration == 0L) 1 else duration).toInt()
            progressBar?.progress = progress
        }
        if (mChangeVolume) {
            //change volume event
        }
        startProgressTimer()
    }

    protected fun touchActionMove(x: Float, y: Float) {
        Log.i(TAG, "onTouch surfaceContainer actionMove [" + this.hashCode() + "] ")
        val deltaX = x - mDownX
        var deltaY = y - mDownY
        val absDeltaX = Math.abs(deltaX)
        val absDeltaY = Math.abs(deltaY)
        if (screen == SCREEN_FULLSCREEN) {
            //拖动的是NavigationBar和状态栏
            if (mDownX > getScreenWidth(context) || mDownY < getStatusBarHeight(context)) {
                return
            }
            if (!mChangePosition && !mChangeVolume && !mChangeBrightness) {
                if (absDeltaX > THRESHOLD || absDeltaY > THRESHOLD) {
                    cancelProgressTimer()
                    if (absDeltaX >= THRESHOLD) {
                        // 全屏模式下的CURRENT_STATE_ERROR状态下,不响应进度拖动事件.
                        // 否则会因为mediaplayer的状态非法导致App Crash
                        if (state != STATE_ERROR) {
                            mChangePosition = true
                            mGestureDownPosition = currentPositionWhenPlaying
                        }
                    } else {
                        //如果y轴滑动距离超过设置的处理范围，那么进行滑动事件处理
                        if (mDownX < mScreenHeight * 0.5f) { //左侧改变亮度
                            mChangeBrightness = true
                            val lp = getWindow(
                                context
                            ).attributes
                            if (lp.screenBrightness < 0) {
                                try {
                                    mGestureDownBrightness = Settings.System.getInt(
                                        context.contentResolver, Settings.System.SCREEN_BRIGHTNESS
                                    ).toFloat()
                                    Log.i(
                                        TAG,
                                        "current system brightness: $mGestureDownBrightness"
                                    )
                                } catch (e: Settings.SettingNotFoundException) {
                                    e.printStackTrace()
                                }
                            } else {
                                mGestureDownBrightness = lp.screenBrightness * 255
                                Log.i(
                                    TAG,
                                    "current activity brightness: $mGestureDownBrightness"
                                )
                            }
                        } else { //右侧改变声音
                            mChangeVolume = true
                            mGestureDownVolume =
                                mAudioManager?.getStreamVolume(AudioManager.STREAM_MUSIC)!!
                        }
                    }
                }
            }
        }
        if (mChangePosition) {
            val totalTimeDuration = duration
            if (PROGRESS_DRAG_RATE <= 0) {
                Log.d(TAG, "error PROGRESS_DRAG_RATE value")
                PROGRESS_DRAG_RATE = 1f
            }
            mSeekTimePosition =
                ((mGestureDownPosition + deltaX * totalTimeDuration / (mScreenWidth * PROGRESS_DRAG_RATE)) as Int).toLong()
            if (mSeekTimePosition > totalTimeDuration) mSeekTimePosition = totalTimeDuration
            val seekTime = stringForTime(mSeekTimePosition)
            val totalTime = stringForTime(totalTimeDuration)
            showProgressDialog(deltaX, seekTime, mSeekTimePosition, totalTime, totalTimeDuration)
        }
        if (mChangeVolume) {
            deltaY = -deltaY
            val max = mAudioManager?.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            val deltaV = (max!! * deltaY * 3 / mScreenHeight).toInt()
            mAudioManager?.setStreamVolume(
                AudioManager.STREAM_MUSIC,
                mGestureDownVolume + deltaV,
                0
            )
            //dialog中显示百分比
            val volumePercent =
                (mGestureDownVolume * 100 / max!! + deltaY * 3 * 100 / mScreenHeight).toInt()
            showVolumeDialog(-deltaY, volumePercent)
        }
        if (mChangeBrightness) {
            deltaY = -deltaY
            val deltaV = (255 * deltaY * 3 / mScreenHeight).toInt()
            val params = getWindow(context).attributes
            if ((mGestureDownBrightness + deltaV) / 255 >= 1) { //这和声音有区别，必须自己过滤一下负值
                params.screenBrightness = 1f
            } else if ((mGestureDownBrightness + deltaV) / 255 <= 0) {
                params.screenBrightness = 0.01f
            } else {
                params.screenBrightness = (mGestureDownBrightness + deltaV) / 255
            }
            getWindow(context).attributes = params
            //dialog中显示百分比
            val brightnessPercent =
                (mGestureDownBrightness * 100 / 255 + deltaY * 3 * 100 / mScreenHeight).toInt()
            showBrightnessDialog(brightnessPercent)
            //                        mDownY = y;
        }
    }

    protected fun touchActionDown(x: Float, y: Float) {
        Log.i(TAG, "onTouch surfaceContainer actionDown [" + this.hashCode() + "] ")
        mTouchingProgressBar = true
        mDownX = x
        mDownY = y
        mChangeVolume = false
        mChangePosition = false
        mChangeBrightness = false
    }

    open fun onStateNormal() {
        Log.i(TAG, "onStateNormal " + " [" + this.hashCode() + "] ")
        state = STATE_NORMAL
        cancelProgressTimer()
        if (mediaInterface != null) mediaInterface?.release()
    }

    open fun onStatePreparing() {
        Log.i(TAG, "onStatePreparing " + " [" + this.hashCode() + "] ")
        state = STATE_PREPARING
        resetProgressAndTime()
    }

    open fun onStatePreparingPlaying() {
        Log.i(TAG, "onStatePreparingPlaying " + " [" + this.hashCode() + "] ")
        state = STATE_PREPARING_PLAYING
    }

    open fun onStatePreparingChangeUrl() {
        Log.i(TAG, "onStatePreparingChangeUrl " + " [" + this.hashCode() + "] ")
        state = STATE_PREPARING_CHANGE_URL
        releaseAllVideos()
        startVideo()

//        mediaInterface.prepare();
    }

    open fun changeUrl(QPDataSource: QPDataSource?, seekToInAdvance: Long) {
        this.QPDataSource = QPDataSource
        this.seekToInAdvance = seekToInAdvance
        onStatePreparingChangeUrl()
    }

    fun onPrepared() {
        Log.i(TAG, "onPrepared " + " [" + this.hashCode() + "] ")
        state = STATE_PREPARED
        if (!preloading) {
            mediaInterface?.start() //这里原来是非县城
            preloading = false
        }
        if (QPDataSource?.currentUrl.toString().toLowerCase().contains("mp3") ||
            QPDataSource?.currentUrl.toString().toLowerCase().contains("wma") ||
            QPDataSource?.currentUrl.toString().toLowerCase().contains("aac") ||
            QPDataSource?.currentUrl.toString().toLowerCase().contains("m4a") ||
            QPDataSource?.currentUrl.toString().toLowerCase().contains("wav")
        ) {
            onStatePlaying()
        }
    }

    fun startPreloading() {
        preloading = true
        startVideo()
    }

    /**
     * 如果STATE_PREPARED就播放，如果没准备完成就走正常的播放函数startVideo();
     */
    fun startVideoAfterPreloading() {
        if (state == STATE_PREPARED) {
            mediaInterface?.start()
        } else {
            preloading = false
            startVideo()
        }
    }

    open fun onStatePlaying() {
        Log.i(TAG, "onStatePlaying " + " [" + this.hashCode() + "] ")
        if (state == STATE_PREPARED) { //如果是准备完成视频后第一次播放，先判断是否需要跳转进度。
            Log.d(TAG, "onStatePlaying:STATE_PREPARED ")
            mAudioManager =
                applicationContext?.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            mAudioManager?.requestAudioFocus(
                onAudioFocusChangeListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT
            )
            if (seekToInAdvance != 0L) {
                mediaInterface?.seekTo(seekToInAdvance)
                seekToInAdvance = 0
            } else {
                val position = QPDataSource?.currentUrl?.let { getSavedProgress(context, it) }
                if (position != 0L) {
                    position?.let { mediaInterface?.seekTo(it) } //这里为什么区分开呢，第一次的播放和resume播放是不一样的。 这里怎么区分是一个问题。然后
                }
            }
        }
        state = STATE_PLAYING
        startProgressTimer()
    }

    open fun onStatePause() {
        Log.i(TAG, "onStatePause " + " [" + this.hashCode() + "] ")
        state = STATE_PAUSE
        startProgressTimer()
    }

    open fun onStateError() {
        Log.i(TAG, "onStateError " + " [" + this.hashCode() + "] ")
        state = STATE_ERROR
        cancelProgressTimer()
    }

    open fun onStateAutoComplete() {
        Log.i(TAG, "onStateAutoComplete " + " [" + this.hashCode() + "] ")
        state = STATE_AUTO_COMPLETE
        cancelProgressTimer()
        progressBar?.progress = 100
        currentTimeTextView?.text = totalTimeTextView?.text
    }

    open fun onInfo(what: Int, extra: Int) {
        Log.d(TAG, "onInfo what - $what extra - $extra")
        if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
            Log.d(TAG, "MEDIA_INFO_VIDEO_RENDERING_START")
            if (state == STATE_PREPARED || state == STATE_PREPARING_CHANGE_URL || state == STATE_PREPARING_PLAYING) {
                onStatePlaying() //开始渲染图像，真正进入playing状态
            }
        } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
            Log.d(TAG, "MEDIA_INFO_BUFFERING_START")
            backUpBufferState = state
            setState(STATE_PREPARING_PLAYING)
        } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
            Log.d(TAG, "MEDIA_INFO_BUFFERING_END")
            if (backUpBufferState != -1) {
                setState(backUpBufferState)
                backUpBufferState = -1
            }
        }
    }

    open fun onError(what: Int, extra: Int) {
        Log.e(TAG, "onError " + what + " - " + extra + " [" + this.hashCode() + "] ")
        if (what != 38 && extra != -38 && what != -38 && extra != 38 && extra != -19) {
            onStateError()
            mediaInterface?.release()
        }
    }

    open fun onCompletion() {
        Runtime.getRuntime().gc()
        Log.i(TAG, "onAutoCompletion " + " [" + this.hashCode() + "] ")
        cancelProgressTimer()
        dismissBrightnessDialog()
        dismissProgressDialog()
        dismissVolumeDialog()
        onStateAutoComplete()
        mediaInterface?.release()
        scanForActivity(context)?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        QPDataSource?.currentUrl?.let { saveProgress(context, it, 0) }
        if (screen == SCREEN_FULLSCREEN) {
            if (CONTAINER_LIST.size == 0) {
                clearFloatScreen() //直接进入全屏
            } else {
                gotoNormalCompletion()
            }
        }
    }

    fun gotoNormalCompletion() {
        gobakFullscreenTime = System.currentTimeMillis() //退出全屏
        val vg = scanForActivity(jzvdContext)?.window?.decorView as ViewGroup
        vg.removeView(this)
        textureViewContainer?.removeView(textureView)
        CONTAINER_LIST.last.removeViewAt(blockIndex) //remove block
        CONTAINER_LIST.last.addView(this, blockIndex, blockLayoutParams)
        CONTAINER_LIST.pop()
        setScreenNormal()
        showStatusBar(jzvdContext)
        setRequestedOrientation(jzvdContext, NORMAL_ORIENTATION)
        showSystemUI(jzvdContext)
    }

    /**
     * 多数表现为中断当前播放
     */
    open fun reset() {
        Log.i(TAG, "reset " + " [" + this.hashCode() + "] ")
        if (state == STATE_PLAYING || state == STATE_PAUSE) {
            val position = currentPositionWhenPlaying
            QPDataSource?.currentUrl?.let { saveProgress(context, it, position) }
        }
        cancelProgressTimer()
        dismissBrightnessDialog()
        dismissProgressDialog()
        dismissVolumeDialog()
        onStateNormal()
        textureViewContainer?.removeAllViews()
        val mAudioManager =
            applicationContext?.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        mAudioManager.abandonAudioFocus(onAudioFocusChangeListener)
        scanForActivity(context)?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        if (mediaInterface != null) mediaInterface?.release()
    }

    /**
     * 里面的的onState...()其实就是setState...()，因为要可以被复写，所以参考Activity的onCreate(),onState..()的方式看着舒服一些，老铁们有何高见。
     *
     * @param state stateId
     */
    fun setState(state: Int?) {
        when (state) {
            STATE_NORMAL -> onStateNormal()
            STATE_PREPARING -> onStatePreparing()
            STATE_PREPARING_PLAYING -> onStatePreparingPlaying()
            STATE_PREPARING_CHANGE_URL -> onStatePreparingChangeUrl()
            STATE_PLAYING -> onStatePlaying()
            STATE_PAUSE -> onStatePause()
            STATE_ERROR -> onStateError()
            STATE_AUTO_COMPLETE -> onStateAutoComplete()
        }
    }

    fun setScreen(screen: Int?) { //特殊的个别的进入全屏的按钮在这里设置  只有setup的时候能用上
        when (screen) {
            SCREEN_NORMAL -> setScreenNormal()
            SCREEN_FULLSCREEN -> setScreenFullscreen()
            SCREEN_TINY -> setScreenTiny()
        }
    }

    open fun startVideo() {
        Log.d(TAG, "startVideo [" + this.hashCode() + "] ")
        setCurrentJzvd(this)
        try {
            val constructor: Constructor<QPMediaInterface> = mediaInterfaceClass?.getConstructor(
                QPvd::class.java
            ) as Constructor<QPMediaInterface>
            mediaInterface = constructor.newInstance(this)
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InstantiationException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        }
        addTextureView()
        scanForActivity(context)?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        onStatePreparing()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (screen == SCREEN_FULLSCREEN || screen == SCREEN_TINY) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            return
        }
        if (widthRatio != 0 && heightRatio != 0) {
            val specWidth = MeasureSpec.getSize(widthMeasureSpec)
            val specHeight = (specWidth * heightRatio.toFloat() / widthRatio).toInt()
            setMeasuredDimension(specWidth, specHeight)
            val childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(specWidth, MeasureSpec.EXACTLY)
            val childHeightMeasureSpec =
                MeasureSpec.makeMeasureSpec(specHeight, MeasureSpec.EXACTLY)
            getChildAt(0).measure(childWidthMeasureSpec, childHeightMeasureSpec)
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }

    fun addTextureView() {
        Log.d(TAG, "addTextureView [" + this.hashCode() + "] ")
        if (textureView != null) textureViewContainer?.removeView(textureView)
        textureView = QPTextureView(context.applicationContext)
        textureView?.surfaceTextureListener = mediaInterface
        val layoutParams = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
            Gravity.CENTER
        )
        textureViewContainer?.addView(textureView, layoutParams)
    }

    fun clearFloatScreen() {
        showStatusBar(context)
        setRequestedOrientation(context, NORMAL_ORIENTATION)
        showSystemUI(context)
        val vg = scanForActivity(context)?.window?.decorView as ViewGroup
        vg.removeView(this)
        if (mediaInterface != null) mediaInterface?.release()
        currentQPvd = null
    }

    fun onVideoSizeChanged(width: Int, height: Int) {
        Log.i(TAG, "onVideoSizeChanged " + " [" + this.hashCode() + "] ")
        if (textureView != null) {
            if (videoRotation != 0) {
                textureView?.rotation = videoRotation.toFloat()
            }
            textureView?.setVideoSize(width, height)
        }
    }

    fun startProgressTimer() {
        Log.i(TAG, "startProgressTimer: " + " [" + this.hashCode() + "] ")
        cancelProgressTimer()
        UPDATE_PROGRESS_TIMER = Timer()
        mProgressTimerTask = ProgressTimerTask()
        UPDATE_PROGRESS_TIMER?.schedule(mProgressTimerTask, 0, 300)
    }

    fun cancelProgressTimer() {
        if (UPDATE_PROGRESS_TIMER != null) {
            UPDATE_PROGRESS_TIMER?.cancel()
        }
        if (mProgressTimerTask != null) {
            mProgressTimerTask?.cancel()
        }
    }

    open fun onProgress(progress: Int, position: Long, duration: Long) {
//        Log.d(TAG, "onProgress: progress=" + progress + " position=" + position + " duration=" + duration);
        mCurrentPosition = position
        if (!mTouchingProgressBar) {
            if (seekToManulPosition != -1) {
                seekToManulPosition = if (seekToManulPosition > progress) {
                    return
                } else {
                    -1 //这个关键帧有没有必要做
                }
            } else {
                progressBar?.progress = progress
            }
        }
        if (position != 0L) currentTimeTextView?.text = stringForTime(position)
        totalTimeTextView?.text = stringForTime(duration)
    }

    open fun setBufferProgress(bufferProgress: Int) {
        progressBar?.secondaryProgress = bufferProgress
    }

    open fun resetProgressAndTime() {
        mCurrentPosition = 0
        progressBar?.progress = 0
        progressBar?.secondaryProgress = 0
        currentTimeTextView?.text = stringForTime(0)
        totalTimeTextView?.text = stringForTime(0)
    }

    val currentPositionWhenPlaying: Long
        get() {
            var position: Long = 0
            if (state == STATE_PLAYING || state == STATE_PAUSE || state == STATE_PREPARING_PLAYING) {
                position = try {
                    mediaInterface!!.currentPosition
                } catch (e: IllegalStateException) {
                    e.printStackTrace()
                    return position
                }
            }
            return position
        }
    val duration: Long
        get() {
            var duration: Long = 0
            duration = try {
                mediaInterface!!.duration
            } catch (e: Exception) {
                e.printStackTrace()
                return duration
            }
            return duration
        }

    override fun onStartTrackingTouch(seekBar: SeekBar) {
        Log.i(TAG, "bottomProgress onStartTrackingTouch [" + this.hashCode() + "] ")
        cancelProgressTimer()
        var vpdown = parent
        while (vpdown != null) {
            vpdown.requestDisallowInterceptTouchEvent(true)
            vpdown = vpdown.parent
        }
    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {
        Log.i(TAG, "bottomProgress onStopTrackingTouch [" + this.hashCode() + "] ")
        startProgressTimer()
        var vpup = parent
        while (vpup != null) {
            vpup.requestDisallowInterceptTouchEvent(false)
            vpup = vpup.parent
        }
        if (state != STATE_PLAYING &&
            state != STATE_PAUSE
        ) return
        val time = seekBar.progress * duration / 100
        seekToManulPosition = seekBar.progress
        mediaInterface?.seekTo(time)
        Log.i(TAG, "seekTo " + time + " [" + this.hashCode() + "] ")
    }

    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        if (fromUser) {
            //设置这个progres对应的时间，给textview
            val duration = duration
            currentTimeTextView?.text = stringForTime(progress * duration / 100)
        }
    }

    fun cloneAJzvd(vg: ViewGroup) {
        try {
            val constructor = this@QPvd.javaClass.getConstructor(
                Context::class.java
            ) as Constructor<QPvd>
            val jzvd = constructor.newInstance(context)
            jzvd.id = id
            jzvd.minimumWidth = blockWidth
            jzvd.minimumHeight = blockHeight
            vg.addView(jzvd, blockIndex, blockLayoutParams)
            jzvd.setUp(QPDataSource?.cloneMe(), SCREEN_NORMAL, mediaInterfaceClass)
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        } catch (e: InstantiationException) {
            e.printStackTrace()
        } catch (e: InvocationTargetException) {
            e.printStackTrace()
        } catch (e: NoSuchMethodException) {
            e.printStackTrace()
        }
    }

    /**
     * 如果全屏或者返回全屏的视图有问题，复写这两个函数gotoScreenNormal(),根据自己布局的情况重新布局。
     */
    open fun gotoFullscreen() {
        gotoFullscreenTime = System.currentTimeMillis()
        var vg = parent as ViewGroup
        jzvdContext = vg.context
        blockLayoutParams = layoutParams
        blockIndex = vg.indexOfChild(this)
        blockWidth = width
        blockHeight = height
        vg.removeView(this)
        cloneAJzvd(vg)
        CONTAINER_LIST.add(vg)
        vg = scanForActivity(jzvdContext)?.window?.decorView as ViewGroup
        val fullLayout: ViewGroup.LayoutParams = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        )
        vg.addView(this, fullLayout)
        setScreenFullscreen()
        hideStatusBar(jzvdContext)
        setRequestedOrientation(jzvdContext, FULLSCREEN_ORIENTATION)
        hideSystemUI(jzvdContext) //华为手机和有虚拟键的手机全屏时可隐藏虚拟键 issue:1326
    }

    open fun gotoNormalScreen() { //goback本质上是goto
        gobakFullscreenTime = System.currentTimeMillis() //退出全屏
        val vg = scanForActivity(jzvdContext)?.window?.decorView as ViewGroup
        vg.removeView(this)
        //        CONTAINER_LIST.getLast().removeAllViews();
        CONTAINER_LIST.last.removeViewAt(blockIndex) //remove block
        CONTAINER_LIST.last.addView(this, blockIndex, blockLayoutParams)
        CONTAINER_LIST.pop()
        setScreenNormal() //这块可以放到jzvd中
        showStatusBar(jzvdContext)
        setRequestedOrientation(jzvdContext, NORMAL_ORIENTATION)
        showSystemUI(jzvdContext)
    }

    open fun setScreenNormal() { //TODO 这块不对呀，还需要改进，设置flag之后要设置ui，不设置ui这么写没意义呀
        screen = SCREEN_NORMAL
    }

    open fun setScreenFullscreen() {
        screen = SCREEN_FULLSCREEN
    }

    open fun setScreenTiny() {
        screen = SCREEN_TINY
    }

    //    //重力感应的时候调用的函数，、、这里有重力感应的参数，暂时不能删除
    open fun autoFullscreen(x: Float) { //TODO写道demo中
        if (currentQPvd != null && (state == STATE_PLAYING || state == STATE_PAUSE)
            && screen != SCREEN_FULLSCREEN && screen != SCREEN_TINY
        ) {
            if (x > 0) {
                setRequestedOrientation(context, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
            } else {
                setRequestedOrientation(context, ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE)
            }
            gotoFullscreen()
        }
    }

    fun autoQuitFullscreen() {
        if (System.currentTimeMillis() - lastAutoFullscreenTime > 2000 //                && CURRENT_JZVD != null
            && state == STATE_PLAYING && screen == SCREEN_FULLSCREEN
        ) {
            lastAutoFullscreenTime = System.currentTimeMillis()
            backPress()
        }
    }

    fun onSeekComplete() {}
    open fun showWifiDialog() {}
    open fun showProgressDialog(
        deltaX: Float,
        seekTime: String?, seekTimePosition: Long,
        totalTime: String?, totalTimeDuration: Long
    ) {
    }

    open fun dismissProgressDialog() {}
    open fun showVolumeDialog(deltaY: Float, volumePercent: Int) {}
    open fun dismissVolumeDialog() {}
    open fun showBrightnessDialog(brightnessPercent: Int) {}
    open fun dismissBrightnessDialog() {}

    //这个函数必要吗
    val applicationContext: Context?
        get() { //这个函数必要吗
            val context = context
            if (context != null) {
                val applicationContext = context.applicationContext
                if (applicationContext != null) {
                    return applicationContext
                }
            }
            return context
        }

    class JZAutoFullscreenListener : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) { //可以得到传感器实时测量出来的变化值
            val x = event.values[SensorManager.DATA_X]
            val y = event.values[SensorManager.DATA_Y]
            val z = event.values[SensorManager.DATA_Z]
            //过滤掉用力过猛会有一个反向的大数值
            if (x < -12 || x > 12) {
                if (System.currentTimeMillis() - lastAutoFullscreenTime > 2000) {
                    if (currentQPvd != null) currentQPvd?.autoFullscreen(x)
                    lastAutoFullscreenTime = System.currentTimeMillis()
                }
            }
        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
    }

    inner class ProgressTimerTask : TimerTask() {
        override fun run() {
            if (state == STATE_PLAYING || state == STATE_PAUSE || state == STATE_PREPARING_PLAYING) {
//                Log.v(TAG, "onProgressUpdate " + "[" + this.hashCode() + "] ");
                post {
                    val position = currentPositionWhenPlaying
                    val duration = duration
                    val progress =
                        (position * 100 / if (duration == 0L) 1 else duration).toInt()
                    onProgress(progress, position, duration)
                }
            }
        }
    }

    companion object {
        const val TAG = "QPVD"
        const val SCREEN_NORMAL = 0
        const val SCREEN_FULLSCREEN = 1
        const val SCREEN_TINY = 2
        const val STATE_IDLE = -1
        const val STATE_NORMAL = 0
        const val STATE_PREPARING = 1
        const val STATE_PREPARING_CHANGE_URL = 2
        const val STATE_PREPARING_PLAYING = 3
        const val STATE_PREPARED = 4
        const val STATE_PLAYING = 5
        const val STATE_PAUSE = 6
        const val STATE_AUTO_COMPLETE = 7
        const val STATE_ERROR = 8
        const val VIDEO_IMAGE_DISPLAY_TYPE_ADAPTER = 0 //DEFAULT
        const val VIDEO_IMAGE_DISPLAY_TYPE_FILL_PARENT = 1
        const val VIDEO_IMAGE_DISPLAY_TYPE_FILL_SCROP = 2
        const val VIDEO_IMAGE_DISPLAY_TYPE_ORIGINAL = 3
        const val THRESHOLD = 80
        var currentQPvd: QPvd? = null
        var CONTAINER_LIST = LinkedList<ViewGroup>()
        var TOOL_BAR_EXIST = true
        var FULLSCREEN_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        var NORMAL_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        var SAVE_PROGRESS = true
        var WIFI_TIP_DIALOG_SHOWED = false
        var VIDEO_IMAGE_DISPLAY_TYPE = 0
        var lastAutoFullscreenTime: Long = 0
        var ON_PLAY_PAUSE_TMP_STATE = 0 //这个考虑不放到库里，去自定义
        var backUpBufferState = -1
        var PROGRESS_DRAG_RATE = 1f //进度条滑动阻尼系数 越大播放进度条滑动越慢
        var onAudioFocusChangeListener: AudioManager.OnAudioFocusChangeListener =
            object : AudioManager.OnAudioFocusChangeListener {
                //是否新建个class，代码更规矩，并且变量的位置也很尴尬
                override fun onAudioFocusChange(focusChange: Int) {
                    when (focusChange) {
                        AudioManager.AUDIOFOCUS_GAIN -> {
                        }
                        AudioManager.AUDIOFOCUS_LOSS -> {
                            releaseAllVideos()
                            Log.d(TAG, "AUDIOFOCUS_LOSS [" + this.hashCode() + "]")
                        }
                        AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                            try {
                                val player = currentQPvd
                                if (player != null && player.state == STATE_PLAYING) {
                                    player.startButton?.performClick()
                                }
                            } catch (e: IllegalStateException) {
                                e.printStackTrace()
                            }
                            Log.d(TAG, "AUDIOFOCUS_LOSS_TRANSIENT [" + this.hashCode() + "]")
                        }
                        AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                        }
                    }
                }
            }

        /**
         * 增加准备状态逻辑
         */
        fun goOnPlayOnResume() {
            if (currentQPvd != null) {
                if (currentQPvd?.state == STATE_PAUSE) {
                    if (ON_PLAY_PAUSE_TMP_STATE == STATE_PAUSE) {
                        currentQPvd?.onStatePause()
                        currentQPvd?.mediaInterface?.pause()
                    } else {
                        currentQPvd?.onStatePlaying()
                        currentQPvd?.mediaInterface?.start()
                    }
                    ON_PLAY_PAUSE_TMP_STATE = 0
                } else if (currentQPvd?.state == STATE_PREPARING) {
                    //准备状态暂停后的
                    currentQPvd?.startVideo()
                }
                if (currentQPvd?.screen == SCREEN_FULLSCREEN) {
                    hideStatusBar(currentQPvd?.jzvdContext)
                    hideSystemUI(currentQPvd?.jzvdContext)
                }
            }
        }

        /**
         * 增加准备状态逻辑
         */
        fun goOnPlayOnPause() {
            if (currentQPvd != null) {
                if (currentQPvd?.state == STATE_AUTO_COMPLETE || currentQPvd?.state == STATE_NORMAL || currentQPvd?.state == STATE_ERROR) {
                    releaseAllVideos()
                } else if (currentQPvd?.state == STATE_PREPARING) {
                    //准备状态暂停的逻辑
                    setCurrentJzvd(currentQPvd)
                    currentQPvd?.state = STATE_PREPARING
                } else {
                    ON_PLAY_PAUSE_TMP_STATE = currentQPvd!!.state
                    currentQPvd?.onStatePause()
                    currentQPvd?.mediaInterface?.pause()
                }
            }
        }

        fun startFullscreenDirectly(
            context: Context?,
            _class: Class<*>,
            url: String?,
            title: String?
        ) {
            startFullscreenDirectly(context, _class,
                QPDataSource(url, title)
            )
        }

        fun startFullscreenDirectly(
            context: Context?,
            _class: Class<*>,
            QPDataSource: QPDataSource?
        ) {
            hideStatusBar(context)
            setRequestedOrientation(context, FULLSCREEN_ORIENTATION)
            hideSystemUI(context)
            val vp = scanForActivity(context)?.window?.decorView as ViewGroup
            try {
                val constructor: Constructor<QPvd> = _class.getConstructor(
                    Context::class.java
                ) as Constructor<QPvd>
                val jzvd = constructor.newInstance(context)
                val lp = LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
                )
                vp.addView(jzvd, lp)
                jzvd.setUp(QPDataSource, SCREEN_FULLSCREEN)
                jzvd.startVideo()
            } catch (e: InstantiationException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        fun releaseAllVideos() {
            Log.d(TAG, "releaseAllVideos")
            if (currentQPvd != null) {
                currentQPvd?.reset()
                currentQPvd = null
            }
            CONTAINER_LIST.clear()
        }

        fun backPress(): Boolean {
            Log.i(TAG, "backPress")
            if (CONTAINER_LIST.size != 0 && currentQPvd != null) { //判断条件，因为当前所有goBack都是回到普通窗口
                currentQPvd?.gotoNormalScreen()
                return true
            } else if (CONTAINER_LIST.size == 0 && currentQPvd != null && currentQPvd?.screen != SCREEN_NORMAL) { //退出直接进入的全屏
                currentQPvd?.clearFloatScreen()
                return true
            }
            return false
        }

        fun setCurrentJzvd(QPvd: QPvd?) {
            if (currentQPvd != null) currentQPvd?.reset()
            currentQPvd = QPvd
        }

        fun setTextureViewRotation(rotation: Int) {
            if (currentQPvd != null && currentQPvd?.textureView != null) {
                currentQPvd?.textureView?.rotation = rotation.toFloat()
            }
        }

        fun setVideoImageDisplayType(type: Int) {
            VIDEO_IMAGE_DISPLAY_TYPE = type
            if (currentQPvd != null && currentQPvd?.textureView != null) {
                currentQPvd?.textureView?.requestLayout()
            }
        }
    }
}