package top.oply.opuslib

import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.util.Log
import java.lang.Exception
import java.nio.ByteBuffer
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

class OpusPlayer private constructor() {
    private val opusLib = OpusTool()

    @Volatile
    private var state = STATE_NONE
    private val libLock: Lock = ReentrantLock()
    private var audioTrack: AudioTrack? = null
    var bufferSize = 0
    private var channel = 0
    private val lastNotificationTime: Long = 0
    private var currentFileName = ""

    @Volatile
    private var playTread = Thread()
    private var mEventSender: OpusEvent? = null
    fun setEventSender(es: OpusEvent?) {
        mEventSender = es
    }

    internal inner class PlayThread : Runnable {
        override fun run() {
            readAudioDataFromFile()
        }
    }

    fun play(fileName: String) {
        //if already playing, stop current playback
        if (state != STATE_NONE) {
            stop()
        }
        state = STATE_NONE
        currentFileName = fileName
        if (!Utils.isFileExist(currentFileName) || opusLib.isOpusFile(currentFileName) === 0) {
            Log.e(TAG, "File does not exist, or it is not an opus file!")
            if (mEventSender != null) mEventSender!!.sendEvent(OpusEvent.PLAYING_FAILED)
            return
        }
        libLock.lock()
        val res = opusLib.openOpusFile(currentFileName)
        libLock.unlock()
        if (res == 0) {
            Log.e(TAG, "Open opus file error!")
            if (mEventSender != null) mEventSender!!.sendEvent(OpusEvent.PLAYING_FAILED)
            return
        }
        try {
            channel = opusLib.channelCount
            var trackChannel = 0
            trackChannel =
                if (channel == 1) AudioFormat.CHANNEL_OUT_MONO else AudioFormat.CHANNEL_OUT_STEREO
            bufferSize =
                AudioTrack.getMinBufferSize(48000, trackChannel, AudioFormat.ENCODING_PCM_16BIT)
            bufferSize = if (bufferSize > minBufferSize) bufferSize else minBufferSize
            audioTrack = AudioTrack(
                AudioManager.STREAM_MUSIC,
                48000,
                trackChannel,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize,
                AudioTrack.MODE_STREAM
            )
            audioTrack!!.play()
        } catch (e: Exception) {
            Utils.printE(TAG, e)
            destroyPlayer()
            return
        }
        state = STATE_STARTED
        playTread = Thread(PlayThread(), "OpusPlay Thrd")
        playTread.start()
        if (mEventSender != null) mEventSender!!.sendEvent(OpusEvent.PLAYING_STARTED)
    }

    protected fun readAudioDataFromFile() {
        if (state != STATE_STARTED) {
            return
        }
        val buffer = ByteBuffer.allocateDirect(bufferSize)
        var isFinished = false
        while (state != STATE_NONE) {
            if (state == STATE_PAUSED) {
                try {
                    Thread.sleep(10)
                    continue
                } catch (e: Exception) {
                    Log.e(TAG, e.toString())
                    continue
                }
            } else if (state == STATE_STARTED) {
                libLock.lock()
                opusLib.readOpusFile(buffer, bufferSize)
                val size: Int = opusLib.size
                libLock.unlock()
                if (size != 0) {
                    buffer.rewind()
                    val data = ByteArray(size)
                    buffer[data]
                    audioTrack!!.write(data, 0, size)
                }
                notifyProgress()
                isFinished = opusLib.finished !== 0
                if (isFinished) {
                    break
                }
            }
        }
        if (state != STATE_NONE) state = STATE_NONE
        if (mEventSender != null) mEventSender!!.sendEvent(OpusEvent.PLAYING_FINISHED)
    }

    fun pause() {
        if (state == STATE_STARTED) {
            audioTrack!!.pause()
            state = STATE_PAUSED
            if (mEventSender != null) mEventSender!!.sendEvent(OpusEvent.PLAYING_PAUSED)
        }
        notifyProgress()
    }

    fun resume() {
        if (state == STATE_PAUSED) {
            audioTrack!!.play()
            state = STATE_STARTED
            if (mEventSender != null) mEventSender!!.sendEvent(OpusEvent.PLAYING_STARTED)
        }
    }

    fun stop() {
        state = STATE_NONE
        while (true) {
            try {
                Thread.sleep(20)
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
            if (!playTread.isAlive) break
        }
        Thread.yield()
        destroyPlayer()
    }

    fun toggle(fileName: String): String {
        return if (state == STATE_PAUSED && currentFileName == fileName) {
            resume()
            "Pause"
        } else if (state == STATE_STARTED && currentFileName == fileName) {
            pause()
            "Resume"
        } else {
            play(fileName)
            "Pause"
        }
    }

    /**
     * Get duration, whose unit is second
     *
     * @return duration
     */
    val duration: Long
        get() = opusLib.totalDuration

    /**
     * Get Position of current palyback, whose unit is second
     *
     * @return duration
     */
    val position: Long
        get() = opusLib.currentPosition

    fun seekOpusFile(scale: Float) {
        if (state == STATE_PAUSED || state == STATE_STARTED) {
            libLock.lock()
            opusLib.seekOpusFile(scale)
            libLock.unlock()
        }
    }

    private fun notifyProgress() {
        //notify every 1 second
        if (System.currentTimeMillis() - lastNotificationTime >= 1000) {
            if (mEventSender != null) mEventSender!!.sendProgressEvent(position, duration)
        }
    }

    private fun destroyPlayer() {
        libLock.lock()
        opusLib.closeOpusFile()
        libLock.unlock()
        try {
            if (audioTrack != null) {
                audioTrack!!.pause()
                audioTrack!!.flush()
                audioTrack!!.release()
                audioTrack = null
            }
        } catch (e: Exception) {
            Utils.printE(TAG, e)
        }
    }

    val isWorking: Boolean
        get() = state != STATE_NONE

    fun release() {
        if (state != STATE_NONE) stop()
    }

    companion object {
        @Volatile
        private var oPlayer: OpusPlayer? = null
        val instance: OpusPlayer?
            get() {
                if (oPlayer == null) synchronized(OpusPlayer::class.java) {
                    if (oPlayer == null) oPlayer =
                        OpusPlayer()
                }
                return oPlayer
            }
        private val TAG = OpusPlayer::class.java.name
        private const val STATE_NONE = 0
        private const val STATE_STARTED = 1
        private const val STATE_PAUSED = 2
        private const val minBufferSize = 1024 * 8 * 8
    }
}