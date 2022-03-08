package com.qipa.qipaimbase.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import top.oply.opuslib.OpusEvent
import top.oply.opuslib.OpusPlayer
import top.oply.opuslib.OpusRecorder
import top.oply.opuslib.OpusService

class VoiceHelper(private val context: Context, private val onVoiceListener: OnVoiceListener?) {
    private val mReceiver: OpusReceiver
    private var fileName: String? = null
    private var cancel = false
    fun destory() {
        context.unregisterReceiver(mReceiver)
    }

    fun record(fileName: String?, onRecordStatus: OpusRecorder.OnRecordStatusListener?) {
        cancel = false
        this.fileName = fileName
        //        OpusService.record(context, fileName);
        fileName?.let { OpusRecorder.instance?.startRecording(it, onRecordStatus) }
    }

    fun stopRecord() {
//        OpusService.stopRecording(context);
        OpusRecorder.instance?.stopRecording()
    }

    fun play(fileName: String?) {
        fileName?.let { OpusPlayer.instance?.play(it) }
        //        OpusService.play(context, fileName);
    }

    fun cancelPlay() {
        OpusPlayer.instance?.stop()
    }

    fun stopPlay() {
        OpusService.stopPlaying(context)
    }

    fun cancelRecord() {
//        OpusService.stopPlaying(context);
        cancel = true
        OpusRecorder.instance?.stopRecording()
        FileUtils.removeFile(fileName)
    }

    internal inner class OpusReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val bundle = intent.extras
            val type = bundle!!.getInt(OpusEvent.EVENT_TYPE, 0)
            when (type) {
                OpusEvent.CONVERT_FINISHED -> {
                }
                OpusEvent.CONVERT_FAILED -> {
                }
                OpusEvent.CONVERT_STARTED -> {
                }
                OpusEvent.RECORD_FAILED -> onVoiceListener?.onRecordFailed()
                OpusEvent.RECORD_FINISHED -> if (onVoiceListener != null && !cancel) {
                    onVoiceListener.onRecordFinish()
                }
                OpusEvent.RECORD_STARTED -> {
                }
                OpusEvent.RECORD_PROGRESS_UPDATE -> {
                }
                OpusEvent.PLAY_PROGRESS_UPDATE -> {
                }
                OpusEvent.PLAY_GET_AUDIO_TRACK_INFO -> {
                }
                OpusEvent.PLAYING_FAILED -> {
                }
                OpusEvent.PLAYING_FINISHED -> {
                }
                OpusEvent.PLAYING_PAUSED -> {
                }
                OpusEvent.PLAYING_STARTED -> {
                }
                else -> {
                }
            }
        }
    }

    interface OnVoiceListener {
        fun onRecordFinish()
        fun onRecordFailed()
    }

    init {
        OpusRecorder.instance?.setEventSender(OpusEvent(context))
        mReceiver = OpusReceiver()
        val filter = IntentFilter()
        filter.addAction(OpusEvent.ACTION_OPUS_UI_RECEIVER)
        context.registerReceiver(mReceiver, filter)
    }
}
