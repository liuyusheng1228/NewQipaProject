package top.oply.opuslib

import java.lang.Exception

class OpusConverter private constructor() {
    @Volatile
    private var state = STATE_NONE
    private var convertType = false
    private var inputFile: String? = null
    private var outputFile: String? = null
    private var option: String? = null
    private val mTool = OpusTool()
    private var mThread = Thread()
    private var mEventSender: OpusEvent? = null
    fun setEventSender(es: OpusEvent?) {
        mEventSender = es
    }

    internal inner class ConvertThread : Runnable {
        override fun run() {
            if (mEventSender != null) mEventSender!!.sendEvent(OpusEvent.CONVERT_STARTED)
            if (convertType == TYPE_ENC) mTool.encode(
                inputFile,
                outputFile,
                option
            ) else if (convertType == TYPE_DEC) mTool.decode(inputFile, outputFile, option)
            state = STATE_NONE
            outputFile?.let { OpusTrackInfo.instance?.addOpusFile(it) }
            if (mEventSender != null) mEventSender!!.sendEvent(
                OpusEvent.CONVERT_FINISHED,
                outputFile
            )
        }
    }

    fun encode(fileNameIn: String?, fileNameOut: String?, opt: String?) {
        if (!Utils.isWAVFile(fileNameIn!!)) {
            if (mEventSender != null) mEventSender!!.sendEvent(OpusEvent.CONVERT_FAILED)
            return
        }
        state = STATE_CONVERTING
        convertType = TYPE_ENC
        inputFile = fileNameIn
        outputFile = fileNameOut
        option = opt
        mThread = Thread(ConvertThread(), "Opus Enc Thrd")
        mThread.start()
    }

    fun decode(fileNameIn: String?, fileNameOut: String?, opt: String?) {
        if (!Utils.isFileExist(fileNameIn) || mTool.isOpusFile(fileNameIn) === 0) {
            if (mEventSender != null) mEventSender!!.sendEvent(OpusEvent.CONVERT_FAILED)
            return
        }
        state = STATE_CONVERTING
        convertType = TYPE_DEC
        inputFile = fileNameIn
        outputFile = fileNameOut
        option = opt
        mThread = Thread(ConvertThread(), "Opus Dec Thrd")
        mThread.start()
    }

    val isWorking: Boolean
        get() = state != STATE_NONE

    fun release() {
        try {
            if (state == STATE_CONVERTING && mThread.isAlive) mThread.interrupt()
        } catch (e: Exception) {
            Utils.printE(TAG, e)
        } finally {
            state = STATE_NONE
            if (mEventSender != null) mEventSender!!.sendEvent(OpusEvent.CONVERT_FAILED)
        }
    }

    companion object {
        @Volatile
        private var singleton: OpusConverter? = null
        val instance: OpusConverter?
            get() {
                if (singleton == null) synchronized(OpusConverter::class.java) {
                    if (singleton == null) singleton =
                        OpusConverter()
                }
                return singleton
            }
        private val TAG = OpusConverter::class.java.name
        private const val STATE_NONE = 0
        private const val STATE_CONVERTING = 1
        private const val TYPE_ENC = true
        private const val TYPE_DEC = false
    }
}