package top.oply.opuslib

import android.os.Environment
import java.io.File
import java.io.Serializable
import java.lang.Exception
import java.util.ArrayList
import java.util.HashMap
import java.util.HashSet

class OpusTrackInfo private constructor() {
    private val TAG = OpusTrackInfo::class.java.name
    private var mEventSender: OpusEvent? = null
    private val mTool = OpusTool()
    var appExtDir: String = ""
    private var requestDirFile: File? = null
    private var mThread = Thread()
    val trackInfor = AudioPlayList()
    private val mAudioTime: Utils.AudioTime = Utils.AudioTime()
    fun setEvenSender(opusEven: OpusEvent?) {
        mEventSender = opusEven
    }

    fun addOpusFile(file: String) {
        try {
            Thread.sleep(10)
        } catch (e: Exception) {
            Utils.printE(TAG, e)
        }
        val f = File(file)
        if (f.exists() && "opus".equals(Utils.getExtention(file), ignoreCase = true)
            && mTool.openOpusFile(file) !== 0
        ) {
            val map: MutableMap<String, Any> = HashMap()
            map[TITLE_TITLE] = f.name
            map[TITLE_ABS_PATH] = file
            mAudioTime.setTimeInSecond(mTool.totalDuration)
            map[TITLE_DURATION] = mAudioTime.time
            map[TITLE_IS_CHECKED] = false
            //TODO: get imagin from opus files
            map[TITLE_IMG] = 0
            trackInfor.add(map)
            mTool.closeOpusFile()
            if (mEventSender != null) mEventSender!!.sendTrackinforEvent(trackInfor)
        }
    }

    fun sendTrackInforToUi() {
        if (mEventSender != null) mEventSender!!.sendTrackinforEvent(trackInfor)
    }

    private fun getTrackInfor(Dir: String) {
        var Dir = Dir
        if (Dir.length == 0) Dir = appExtDir
        val file = File(Dir)
        if (file.exists() && file.isDirectory) requestDirFile = file
        mThread = Thread(MyThread(), "Opus Trc Trd")
        mThread.start()
    }

    fun getAValidFileName(prefix: String): String {
        val extention = ".opus"
        val set = HashSet<String>(100)
        val lst = trackInfor.list
        for (map in lst) {
            set.add(map[TITLE_TITLE].toString())
        }
        var i = 0
        while (true) {
            i++
            if (!set.contains(prefix + i + extention)) break
        }
        return appExtDir + prefix + i + extention
    }

    private fun prepareTrackInfor(file: File?) {
        try {
            val files = file!!.listFiles()
            for (f in files) {
                if (f.isFile) {
                    val name = f.name
                    val absPath = f.absolutePath
                    if ("opus".equals(Utils.getExtention(name), ignoreCase = true)
                        && mTool.openOpusFile(absPath) !== 0
                    ) {
                        val map: MutableMap<String, Any> = HashMap()
                        map[TITLE_TITLE] = f.name
                        map[TITLE_ABS_PATH] = absPath
                        mAudioTime.setTimeInSecond(mTool.totalDuration)
                        map[TITLE_DURATION] = mAudioTime.time
                        //TODO: get imagin from opus files
                        map[TITLE_IS_CHECKED] = false
                        map[TITLE_IMG] = 0
                        trackInfor.add(map)
                        mTool.closeOpusFile()
                    }
                } else if (f.isDirectory) {
                    prepareTrackInfor(f)
                }
            }
        } catch (e: Exception) {
            Utils.printE(TAG, e)
        }
    }

    class AudioPlayList : Serializable {
        private val mAudioInforList: MutableList<Map<String, Any>> = ArrayList(32)
        fun add(map: Map<String, Any>) {
            mAudioInforList.add(map)
        }

        val list: List<Map<String, Any>>
            get() = mAudioInforList
        val isEmpty: Boolean
            get() = mAudioInforList.isEmpty()

        fun size(): Int {
            return mAudioInforList.size
        }

        fun clear() {
            mAudioInforList.clear()
        }

        companion object {
            const val serialVersionUID = 1234567890987654321L
        }
    }

    internal inner class MyThread : Runnable {
        override fun run() {
            prepareTrackInfor(requestDirFile)
            sendTrackInforToUi()
        }
    }

    fun release() {
        try {
            if (mThread.isAlive) mThread.interrupt()
        } catch (e: Exception) {
            Utils.printE(TAG, e)
        }
    }

    companion object {
        @Volatile
        private var oTrackInfo: OpusTrackInfo? = null
        val instance: OpusTrackInfo?
            get() {
                if (oTrackInfo == null) synchronized(OpusTrackInfo::class.java) {
                    if (oTrackInfo == null) oTrackInfo =
                        OpusTrackInfo()
                }
                return oTrackInfo
            }
        const val TITLE_TITLE = "TITLE"
        const val TITLE_ABS_PATH = "ABS_PATH"
        const val TITLE_DURATION = "DURATION"
        const val TITLE_IMG = "TITLE_IMG"
        const val TITLE_IS_CHECKED = "TITLE_IS_CHECKED"
    }

    init {

        //create OPlayer directory if it does not exist.
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            val sdcardPath = Environment.getExternalStorageDirectory().absolutePath
            appExtDir = "$sdcardPath/OPlayer/"
            val fp = File(appExtDir)
            if (!fp.exists()) fp.mkdir()
            getTrackInfor(appExtDir)
        }

    }
}
