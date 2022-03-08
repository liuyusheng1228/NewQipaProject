package top.oply.opuslib

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.*
import android.util.Log

class OpusService : Service() {
    private val TAG = OpusService::class.java.name

    //Looper
    @Volatile
    private var mServiceLooper: Looper? = null

    @Volatile
    private var mServiceHandler: ServiceHandler? = null
    private var mPlayer: OpusPlayer? = null
    private var mRecorder: OpusRecorder? = null
    private var mConverter: OpusConverter? = null
    private var mTrackInfo: OpusTrackInfo? = null
    private var mEvent: OpusEvent? = null
    override fun onBind(intent: Intent): IBinder? {
        // TODO: Return the communication channel to the service.
        return null
    }

    override fun onCreate() {
        super.onCreate()
        mEvent = OpusEvent(applicationContext)
        mPlayer = OpusPlayer.instance
        mRecorder = OpusRecorder.instance
        mConverter = OpusConverter.instance
        mTrackInfo = OpusTrackInfo.instance
        mTrackInfo?.setEvenSender(mEvent)
        mPlayer!!.setEventSender(mEvent)
        mRecorder!!.setEventSender(mEvent)
        mConverter!!.setEventSender(mEvent)

        //start looper in onCreate() instead of onStartCommand()
        val thread = HandlerThread("OpusServiceHander")
        thread.start()
        mServiceLooper = thread.looper
        mServiceHandler = ServiceHandler(mServiceLooper)
    }

    override fun onDestroy() {
        //quit looper
        mServiceLooper!!.quit()
        mPlayer?.release()
        mRecorder?.release()
        mConverter?.release()
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        val msg = mServiceHandler!!.obtainMessage()
        msg.arg1 = startId
        msg.obj = intent
        mServiceHandler!!.sendMessage(msg)
        return START_NOT_STICKY
    }

    private fun onHandleIntent(intent: Intent?) {
        if (intent != null) {
            val action = intent.action
            if (ACTION_OPUSSERVICE == action) {
                val request = intent.getIntExtra(EXTRA_CMD, 0)
                val fileName: String?
                val fileNameOut: String?
                val option: String?
                when (request) {
                    CMD_PLAY -> {
                        fileName = intent.getStringExtra(EXTRA_FILE_NAME)
                        handleActionPlay(fileName)
                    }
                    CMD_PAUSE -> handleActionPause()
                    CMD_TOGGLE -> {
                        fileName = intent.getStringExtra(EXTRA_FILE_NAME)
                        handleActionToggle(fileName)
                    }
                    CMD_STOP_PLAYING -> handleActionStopPlaying()
                    CMD_RECORD -> {
                        fileName = intent.getStringExtra(EXTRA_FILE_NAME)
                        handleActionRecord(fileName)
                    }
                    CMD_STOP_REOCRDING -> handleActionStopRecording()
                    CMD_ENCODE -> {
                        fileName = intent.getStringExtra(EXTRA_FILE_NAME)
                        fileNameOut = intent.getStringExtra(EXTRA_FILE_NAME_OUT)
                        option = intent.getStringExtra(EXTRA_OPUS_CODING_OPTION)
                        handleActionEncode(fileName, fileNameOut, option)
                    }
                    CMD_DECODE -> {
                        fileName = intent.getStringExtra(EXTRA_FILE_NAME)
                        fileNameOut = intent.getStringExtra(EXTRA_FILE_NAME_OUT)
                        option = intent.getStringExtra(EXTRA_OPUS_CODING_OPTION)
                        handleActionDecode(fileName, fileNameOut, option)
                    }
                    CMD_RECORD_TOGGLE -> if (mRecorder!!.isWorking) {
                        handleActionStopRecording()
                    } else {
                        fileName = intent.getStringExtra(EXTRA_FILE_NAME)
                        handleActionRecord(fileName)
                    }
                    CMD_SEEK_FILE -> {
                        val scale = intent.getFloatExtra(EXTRA_SEEKFILE_SCALE, 0f)
                        handleActionSeekFile(scale)
                    }
                    CMD_GET_TRACK_INFO -> mTrackInfo?.sendTrackInforToUi()
                    else -> Log.e(TAG, "Unknown intent CMD,discarded!")
                }
            } else {
                Log.e(TAG, "Unknown intent action,discarded!")
            }
        }
    }

    private fun handleActionPlay(fileName: String?) {
        mPlayer!!.play(fileName!!)
    }

    private fun handleActionStopPlaying() {
        mPlayer!!.stop()
    }

    private fun handleActionPause() {
        mPlayer!!.pause()
    }

    private fun handleActionToggle(fileName: String?) {
        mPlayer!!.toggle(fileName!!)
    }

    private fun handleActionSeekFile(scale: Float) {
        mPlayer!!.seekOpusFile(scale)
    }

    private fun handleActionRecord(fileName: String?) {
        mRecorder!!.startRecording(fileName!!, null)
    }

    private fun handleActionStopRecording() {
        mRecorder!!.stopRecording()
    }

    private fun handleActionEncode(fileNameIn: String?, fileNameOut: String?, option: String?) {
        mConverter!!.encode(fileNameIn, fileNameOut, option)
    }

    private fun handleActionDecode(fileNameIn: String?, fileNameOut: String?, option: String?) {
        mConverter!!.decode(fileNameIn, fileNameOut, option)
    }

    private inner class ServiceHandler(looper: Looper?) : Handler(
        looper!!
    ) {
        override fun handleMessage(msg: Message) {
            onHandleIntent(msg.obj as Intent)
            //stopSelf()
        }
    }

    companion object {
        //This server
        private const val ACTION_OPUSSERVICE = "top.oply.opuslib.action.OPUSSERVICE"
        private const val EXTRA_FILE_NAME = "FILE_NAME"
        private const val EXTRA_FILE_NAME_OUT = "FILE_NAME_OUT"
        private const val EXTRA_OPUS_CODING_OPTION = "OPUS_CODING_OPTION"
        private const val EXTRA_CMD = "CMD"
        private const val EXTRA_SEEKFILE_SCALE = "SEEKFILE_SCALE"
        private const val CMD_PLAY = 10001
        private const val CMD_PAUSE = 10002
        private const val CMD_STOP_PLAYING = 10003
        private const val CMD_TOGGLE = 10004
        private const val CMD_SEEK_FILE = 10005
        private const val CMD_GET_TRACK_INFO = 10006
        private const val CMD_ENCODE = 20001
        private const val CMD_DECODE = 20002
        private const val CMD_RECORD = 30001
        private const val CMD_STOP_REOCRDING = 30002
        private const val CMD_RECORD_TOGGLE = 30003
        fun play(context: Context, fileName: String?) {
            val intent = Intent(context, OpusService::class.java)
            intent.action = ACTION_OPUSSERVICE
            intent.putExtra(EXTRA_CMD, CMD_PLAY)
            intent.putExtra(EXTRA_FILE_NAME, fileName)
            context.startService(intent)
        }

        fun record(context: Context, fileName: String?) {
            val intent = Intent(context, OpusService::class.java)
            intent.action = ACTION_OPUSSERVICE
            intent.putExtra(EXTRA_CMD, CMD_RECORD)
            intent.putExtra(EXTRA_FILE_NAME, fileName)
            context.startService(intent)
        }

        fun toggle(context: Context, fileName: String?) {
            val intent = Intent(context, OpusService::class.java)
            intent.action = ACTION_OPUSSERVICE
            intent.putExtra(EXTRA_CMD, CMD_TOGGLE)
            intent.putExtra(EXTRA_FILE_NAME, fileName)
            context.startService(intent)
        }

        fun seekFile(context: Context, scale: Float) {
            val intent = Intent(context, OpusService::class.java)
            intent.action = ACTION_OPUSSERVICE
            intent.putExtra(EXTRA_CMD, CMD_SEEK_FILE)
            intent.putExtra(EXTRA_SEEKFILE_SCALE, scale)
            context.startService(intent)
        }

        /**
         * Request the Track info of all the opus files in the directory of this app
         *
         * @param context
         */
        fun getTrackInfo(context: Context) {
            val intent = Intent(context, OpusService::class.java)
            intent.action = ACTION_OPUSSERVICE
            intent.putExtra(EXTRA_CMD, CMD_GET_TRACK_INFO)
            context.startService(intent)
        }

        fun recordToggle(context: Context, fileName: String?) {
            val intent = Intent(context, OpusService::class.java)
            intent.action = ACTION_OPUSSERVICE
            intent.putExtra(EXTRA_CMD, CMD_RECORD_TOGGLE)
            intent.putExtra(EXTRA_FILE_NAME, fileName)
            context.startService(intent)
        }

        fun pause(context: Context) {
            val intent = Intent(context, OpusService::class.java)
            intent.action = ACTION_OPUSSERVICE
            intent.putExtra(EXTRA_CMD, CMD_PAUSE)
            context.startService(intent)
        }

        fun stopRecording(context: Context) {
            val intent = Intent(context, OpusService::class.java)
            intent.action = ACTION_OPUSSERVICE
            intent.putExtra(EXTRA_CMD, CMD_STOP_REOCRDING)
            context.startService(intent)
        }

        fun stopPlaying(context: Context) {
            val intent = Intent(context, OpusService::class.java)
            intent.action = ACTION_OPUSSERVICE
            intent.putExtra(EXTRA_CMD, CMD_STOP_PLAYING)
            context.startService(intent)
        }

        fun encode(context: Context, fileName: String?, fileNameOut: String?, option: String?) {
            val intent = Intent(context, OpusService::class.java)
            intent.action = ACTION_OPUSSERVICE
            intent.putExtra(EXTRA_CMD, CMD_ENCODE)
            intent.putExtra(EXTRA_FILE_NAME, fileName)
            intent.putExtra(EXTRA_FILE_NAME_OUT, fileNameOut)
            intent.putExtra(EXTRA_OPUS_CODING_OPTION, option)
            context.startService(intent)
        }

        fun decode(context: Context, fileName: String?, fileNameOut: String?, option: String?) {
            val intent = Intent(context, OpusService::class.java)
            intent.action = ACTION_OPUSSERVICE
            intent.putExtra(EXTRA_CMD, CMD_DECODE)
            intent.putExtra(EXTRA_FILE_NAME, fileName)
            intent.putExtra(EXTRA_FILE_NAME_OUT, fileNameOut)
            intent.putExtra(EXTRA_OPUS_CODING_OPTION, option)
            context.startService(intent)
        }
    }
}
