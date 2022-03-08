package cn.qpvd

import android.graphics.SurfaceTexture
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.view.Surface
import java.lang.Exception
import java.lang.IllegalStateException

class QPMediaSystem(QPvd: QPvd?) : QPMediaInterface(QPvd!!), MediaPlayer.OnPreparedListener,
    MediaPlayer.OnCompletionListener, MediaPlayer.OnBufferingUpdateListener,
    MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnErrorListener,
    MediaPlayer.OnInfoListener, MediaPlayer.OnVideoSizeChangedListener {
    var mediaPlayer: MediaPlayer? = null
    override fun prepare() {
        release()
        mMediaHandlerThread = HandlerThread("JZVD")
        mMediaHandlerThread?.start()
        mMediaHandler = Handler(mMediaHandlerThread!!.looper) //主线程还是非主线程，就在这里
        handler = Handler()
        mMediaHandler?.post {
            try {
                mediaPlayer = MediaPlayer()
                mediaPlayer?.setAudioStreamType(AudioManager.STREAM_MUSIC)
                mediaPlayer?.isLooping = QPvd.QPDataSource!!.looping
                mediaPlayer?.setOnPreparedListener(this@QPMediaSystem)
                mediaPlayer?.setOnCompletionListener(this@QPMediaSystem)
                mediaPlayer?.setOnBufferingUpdateListener(this@QPMediaSystem)
                mediaPlayer?.setScreenOnWhilePlaying(true)
                mediaPlayer?.setOnSeekCompleteListener(this@QPMediaSystem)
                mediaPlayer?.setOnErrorListener(this@QPMediaSystem)
                mediaPlayer?.setOnInfoListener(this@QPMediaSystem)
                mediaPlayer?.setOnVideoSizeChangedListener(this@QPMediaSystem)
                val clazz =
                    MediaPlayer::class.java
                //如果不用反射，没有url和header参数的setDataSource函数
                val method = clazz.getDeclaredMethod(
                    "setDataSource",
                    String::class.java,
                    MutableMap::class.java
                )
                method.invoke(
                    mediaPlayer,
                    QPvd.QPDataSource?.currentUrl.toString(),
                    QPvd.QPDataSource?.headerMap
                )
                mediaPlayer?.prepareAsync()
                mediaPlayer?.setSurface(Surface(SAVED_SURFACE))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun start() {
        mMediaHandler?.post { mediaPlayer?.start() }
    }

    override fun pause() {
        mMediaHandler?.post { mediaPlayer?.pause() }
    }

    override val isPlaying: Boolean
        get() = mediaPlayer!!.isPlaying

    override fun seekTo(time: Long) {
        mMediaHandler?.post {
            try {
                mediaPlayer?.seekTo(time.toInt())
            } catch (e: IllegalStateException) {
                e.printStackTrace()
            }
        }
    }

    override fun release() { //not perfect change you later
        if (mMediaHandler != null && mMediaHandlerThread != null && mediaPlayer != null) { //不知道有没有妖孽
            val tmpHandlerThread: HandlerThread = mMediaHandlerThread as HandlerThread
            val tmpMediaPlayer: MediaPlayer = mediaPlayer as MediaPlayer
            SAVED_SURFACE = null
            mMediaHandler?.post {
                tmpMediaPlayer.setSurface(null)
                tmpMediaPlayer.release()
                tmpHandlerThread.quit()
            }
            mediaPlayer = null
        }
    }

    //TODO 测试这种问题是否在threadHandler中是否正常，所有的操作mediaplayer是否不需要thread，挨个测试，是否有问题
    override val currentPosition: Long
        get() = if (mediaPlayer != null) {
            mediaPlayer!!.currentPosition.toLong()
        } else {
            0
        }

    override val duration: Long
        get() = if (mediaPlayer != null) {
            mediaPlayer!!.duration.toLong()
        } else {
            0
        }

    override fun setVolume(leftVolume: Float, rightVolume: Float) {
        if (mMediaHandler == null) return
        mMediaHandler?.post {
            if (mediaPlayer != null) mediaPlayer?.setVolume(
                leftVolume,
                rightVolume
            )
        }
    }

    override fun setSpeed(speed: Float) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val pp = mediaPlayer!!.playbackParams
            pp.speed = speed
            mediaPlayer?.playbackParams = pp
        }
    }

    override fun onPrepared(mediaPlayer: MediaPlayer) {
        handler?.post { QPvd.onPrepared() } //如果是mp3音频，走这里
    }

    override fun onCompletion(mediaPlayer: MediaPlayer) {
        handler?.post { QPvd.onCompletion() }
    }

    override fun onBufferingUpdate(mediaPlayer: MediaPlayer, percent: Int) {
        handler?.post { QPvd.setBufferProgress(percent) }
    }

    override fun onSeekComplete(mediaPlayer: MediaPlayer) {
        handler?.post { QPvd.onSeekComplete() }
    }

    override fun onError(mediaPlayer: MediaPlayer, what: Int, extra: Int): Boolean {
        handler?.post { QPvd.onError(what, extra) }
        return true
    }

    override fun onInfo(mediaPlayer: MediaPlayer, what: Int, extra: Int): Boolean {
        handler?.post { QPvd.onInfo(what, extra) }
        return false
    }

    override fun onVideoSizeChanged(mediaPlayer: MediaPlayer, width: Int, height: Int) {
        handler?.post { QPvd.onVideoSizeChanged(width, height) }
    }

    override fun setSurface(surface: Surface?) {
        mediaPlayer?.setSurface(surface)
    }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
        if (SAVED_SURFACE == null) {
            SAVED_SURFACE = surface
            prepare()
        } else {
            QPvd.textureView?.setSurfaceTexture(SAVED_SURFACE!!)
        }
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {}
    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
        return false
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}
}
