package top.oply.opuslib

import android.content.Context
import android.content.Intent
import android.os.Bundle

class OpusEvent(context: Context?) {
    /**
     * Handler for sending status updates
     */
    private var mContext: Context? = null
    private var mAction = ACTION_OPUS_UI_RECEIVER
    fun setActionReceiver(action: String) {
        mAction = action
    }

    fun sendEvent(bundle: Bundle?) {
        val i = Intent()
        i.action = mAction
        i.putExtras(bundle!!)
        mContext!!.sendBroadcast(i)
    }

    /**
     * Send Event to UI
     *
     * @param eventType
     */
    fun sendEvent(eventType: Int) {
        val b = Bundle()
        b.putInt(EVENT_TYPE, eventType)
        val i = Intent()
        i.action = mAction
        i.putExtras(b)
        mContext!!.sendBroadcast(i)
    }

    /**
     * Send Event to UI
     *
     * @param eventType
     */
    fun sendEvent(eventType: Int, msg: String?) {
        val b = Bundle()
        b.putInt(EVENT_TYPE, eventType)
        b.putString(EVENT_MSG, msg)
        val i = Intent()
        i.action = mAction
        i.putExtras(b)
        mContext!!.sendBroadcast(i)
    }

    /**
     * Send playback progress to UI
     *
     * @param currentPosition
     * @param totalDuration
     */
    fun sendProgressEvent(currentPosition: Long, totalDuration: Long) {
        val b = Bundle()
        b.putInt(EVENT_TYPE, PLAY_PROGRESS_UPDATE)
        b.putLong(EVENT_PLAY_PROGRESS_POSITION, currentPosition)
        b.putLong(EVENT_PLAY_DURATION, totalDuration)
        val i = Intent()
        i.action = mAction
        i.putExtras(b)
        mContext!!.sendBroadcast(i)
    }

    /**
     * Send record progress(time, units: second) to UI
     */
    fun sendRecordProgressEvent(time: String?) {
        val b = Bundle()
        b.putInt(EVENT_TYPE, RECORD_PROGRESS_UPDATE)
        b.putString(EVENT_RECORD_PROGRESS, time)
        val i = Intent()
        i.action = mAction
        i.putExtras(b)
        mContext!!.sendBroadcast(i)
    }

    /**
     * Send inforList to UI
     *
     * @param inforList
     */
    fun sendTrackinforEvent(inforList: OpusTrackInfo.AudioPlayList?) {
        val b = Bundle()
        b.putInt(EVENT_TYPE, PLAY_GET_AUDIO_TRACK_INFO)
        //TODO; bug when sending the serializable inforList
        b.putSerializable(EVENT_PLAY_TRACK_INFO, inforList)
        val i = Intent()
        i.action = mAction
        i.putExtras(b)
        mContext!!.sendBroadcast(i)
    }

    companion object {
        //below are values of EventType
        const val PLAYING_FINISHED = 1001
        const val PLAYING_STARTED = 1002
        const val PLAYING_FAILED = 1003
        const val PLAYING_PAUSED = 1004
        const val PLAY_PROGRESS_UPDATE = 1011
        const val PLAY_GET_AUDIO_TRACK_INFO = 1012
        const val RECORD_FINISHED = 2001
        const val RECORD_STARTED = 2002
        const val RECORD_FAILED = 2003
        const val RECORD_PROGRESS_UPDATE = 2004
        const val CONVERT_FINISHED = 3001
        const val CONVERT_STARTED = 3002
        const val CONVERT_FAILED = 3003

        //below are types of EventType
        const val EVENT_TYPE = "EVENT_TYPE"
        const val EVENT_PLAY_PROGRESS_POSITION = "PLAY_PROGRESS_POSITION"
        const val EVENT_PLAY_DURATION = "PLAY_DURATION"
        const val EVENT_PLAY_TRACK_INFO = "PLAY_TRACK_INFO"
        const val EVENT_RECORD_PROGRESS = "RECORD_PROGRESS"
        const val EVENT_MSG = "EVENT_MSG"

        /**
         * Default UI broadcast
         */
        const val ACTION_OPUS_UI_RECEIVER = "top.oply.oplayer.action.ui_receiver"
    }

    init {
        mContext = context
    }
}
