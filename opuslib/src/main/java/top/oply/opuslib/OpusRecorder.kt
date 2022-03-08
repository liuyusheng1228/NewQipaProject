package top.oply.opuslib

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import java.io.File
import java.lang.Exception
import java.nio.ByteBuffer
import java.util.*

class OpusRecorder private constructor() {
    @Volatile
    private var state = STATE_NONE
    private var recorder: AudioRecord? = null
    private var recordingThread: Thread? = Thread()
    private val opusTool = OpusTool()
    private var bufferSize = 0
    private var filePath: String? = null
    private val fileBuffer =
        ByteBuffer.allocateDirect(1920) // Should be 1920, to accord with function writeFreme()
    private var mEventSender: OpusEvent? = null
    private val mRecordTime: Utils.AudioTime = Utils.AudioTime()
    private var onRecordStatusListener: OnRecordStatusListener? = null
    fun setEventSender(es: OpusEvent?) {
        mEventSender = es
    }

    internal inner class RecordThread : Runnable {
        override fun run() {
            mRecordTime.setTimeInSecond(0)
            recorder!!.startRecording()
            /**
             * 根据开始录音判断是否有录音权限
             */
            if (recorder!!.recordingState != AudioRecord.RECORDSTATE_RECORDING) {
                if (mEventSender != null) mEventSender!!.sendEvent(OpusEvent.RECORD_FAILED)
                return
            }
            val rst = opusTool.startRecording(filePath)
            if (rst != 1) {
                if (mEventSender != null) mEventSender!!.sendEvent(OpusEvent.RECORD_FAILED)
                Log.e(TAG, "recorder initially error")
                return
            }
            if (mEventSender != null) mEventSender!!.sendEvent(OpusEvent.RECORD_STARTED)
            if (onRecordStatusListener != null) {
                onRecordStatusListener!!.onRecordStart()
            }
            state = STATE_STARTED
            writeAudioDataToFile()
        }
    }

    @SuppressLint("MissingPermission")
    fun startRecording(file: String, onRecordStatusListener: OnRecordStatusListener?) {
        if (state == STATE_STARTED) return
        this.onRecordStatusListener = onRecordStatusListener
        val minBufferSize = AudioRecord.getMinBufferSize(
            RECORDER_SAMPLERATE,
            RECORDER_CHANNELS,
            RECORDER_AUDIO_ENCODING
        )
        bufferSize = (minBufferSize / 1920 + 1) * 1920
        recorder = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            RECORDER_SAMPLERATE, RECORDER_CHANNELS,
            RECORDER_AUDIO_ENCODING, bufferSize
        )
        filePath = if (file.isEmpty()) {
            OpusTrackInfo.instance?.getAValidFileName("OpusRecord")
        } else {
            file
        }
        //        filePath = file.isEmpty() ? initRecordFileName() : file;
        recordingThread = Thread(RecordThread(), "OpusRecord Thrd")
        recordingThread!!.start()
    }

    private fun writeAudioDataToOpus(buffer: ByteBuffer, size: Int) {
        val finalBuffer = ByteBuffer.allocateDirect(size)
        finalBuffer.put(buffer)
        finalBuffer.rewind()
        val flush = false

        //write data to Opus file
        while (state == STATE_STARTED && finalBuffer.hasRemaining()) {
            var oldLimit = -1
            if (finalBuffer.remaining() > fileBuffer.remaining()) {
                oldLimit = finalBuffer.limit()
                finalBuffer.limit(fileBuffer.remaining() + finalBuffer.position())
            }
            fileBuffer.put(finalBuffer)
            if (fileBuffer.position() == fileBuffer.limit() || flush) {
                val length = if (!flush) fileBuffer.limit() else finalBuffer.position()
                val rst = opusTool.writeFrame(fileBuffer, length)
                if (rst != 0) {
                    fileBuffer.rewind()
                }
            }
            if (oldLimit != -1) {
                finalBuffer.limit(oldLimit)
            }
        }
    }

    private fun writeAudioDataToFile() {
        if (state != STATE_STARTED) return
        val buffer = ByteBuffer.allocateDirect(bufferSize)
        while (state == STATE_STARTED) {
            buffer.rewind()
            val len = recorder!!.read(buffer, bufferSize)
            Log.d(TAG, "\n lengh of buffersize is $len")
            if (len != AudioRecord.ERROR_INVALID_OPERATION) {
                try {
                    writeAudioDataToOpus(buffer, len)
                } catch (e: Exception) {
                    if (mEventSender != null) mEventSender!!.sendEvent(OpusEvent.RECORD_FAILED)
                    Utils.printE(TAG, e)
                }
            }
        }
    }

    private fun updateTrackInfo() {
        val info: OpusTrackInfo? = OpusTrackInfo.instance
        info?.addOpusFile(filePath!!)
        if (mEventSender != null) {
            val f = File(filePath)
            mEventSender!!.sendEvent(OpusEvent.RECORD_FINISHED, f.name)
        }
    }

    fun stopRecording() {
        if (state != STATE_STARTED) return
        state = STATE_NONE
        try {
            Thread.sleep(200)
        } catch (e: Exception) {
            Utils.printE(TAG, e)
        }
        if (null != recorder) {
            opusTool.stopRecording()
            recordingThread = null
            recorder!!.stop()
            recorder!!.release()
            recorder = null
        }
        updateTrackInfo()
    }

    val isWorking: Boolean
        get() = state != STATE_NONE

    fun release() {
        if (state != STATE_NONE) {
            stopRecording()
        }
    }

    private inner class MyTimerTask : TimerTask() {
        override fun run() {
            if (state != STATE_STARTED) {
            } else {
                mRecordTime.add(1)
                val progress: String = mRecordTime.time
                if (mEventSender != null) mEventSender!!.sendRecordProgressEvent(progress)
            }
        }
    }

    interface OnRecordStatusListener {
        fun onRecordStart()
    }

    companion object {
        @Volatile
        private var oRecorder: OpusRecorder? = null
        val instance: OpusRecorder?
            get() {
                if (oRecorder == null) synchronized(OpusRecorder::class.java) {
                    if (oRecorder == null) oRecorder =
                        OpusRecorder()
                }
                return oRecorder
            }
        private const val STATE_NONE = 0
        private const val STATE_STARTED = 1
        private val TAG = OpusRecorder::class.java.name
        private const val RECORDER_SAMPLERATE = 16000
        private const val RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO
        private const val RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT
    }
}

