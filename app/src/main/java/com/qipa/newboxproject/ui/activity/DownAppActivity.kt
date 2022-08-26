package com.qipa.newboxproject.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.ViewModelProvider
import com.arialyy.aria.core.Aria
import com.arialyy.aria.core.download.DownloadTaskListener
import com.arialyy.aria.core.task.DownloadTask
import com.qipa.jetpackmvvm.base.viewmodel.BaseViewModel
import com.qipa.jetpackmvvm.ext.download.DownLoadManager
import com.qipa.jetpackmvvm.ext.download.DownLoadManager.downLoad
import com.qipa.jetpackmvvm.ext.download.OnDownLoadListener
import com.qipa.jetpackmvvm.ext.download.ShareDownLoadUtil
import com.qipa.newboxproject.R
import com.qipa.newboxproject.app.App
import com.qipa.newboxproject.app.base.BaseActivity
import com.qipa.newboxproject.databinding.ActivityDownAppBinding
import kotlinx.android.synthetic.main.activity_down_app.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import top.niunaijun.blackbox.BlackBoxCore

import java.io.File


class DownAppActivity : BaseActivity<BaseViewModel, ActivityDownAppBinding>(), DownloadTaskListener {
    var PARENT_PATH = Environment.getExternalStorageDirectory().absolutePath + "/666Game/download/apk/256722000bec4aa397e55fd49e8903fa.apk"
    var CreatePARENT_PATH =""

    fun initData(){
        ShareDownLoadUtil.getShareDownLoadUtil(this)
        createFile(CreatePARENT_PATH,"256722000bec4aa397e55fd49e8903fa.apk");

        //https://d.ig-666.com/a/app/qpp-2.0.9-2109071615-release.apk
        btn_down_app.setOnClickListener {
//            val taskId: Long = Aria.download(this)
//                .load("https://cdn.llscdn.com/yy/files/xs8qmxn8-lls-LLS-5.8-800-20171207-111607.apk") //读取下载地址
//                .setFilePath(PARENT_PATH) //设置文件保存的完整路径
//                .create() //创建并启动下载

            GlobalScope.launch(Dispatchers.IO) {
                downLoad("2238","https://qpyx-korea-res-1300632903.cos.ap-seoul.myqcloud.com/2022/05/12/256722000bec4aa397e55fd49e8903fa.apk",CreatePARENT_PATH,"256722000bec4aa397e55fd49e8903fass.apk",false,object : DownloadTaskListener{
//                    override fun onDownLoadPrepare(key: String) {
//
//                    }
//
//                    override fun onDownLoadError(key: String, throwable: Throwable) {
//                        Log.i("Api",""+key+"path:"+throwable.message)
//                    }
//
//                    override fun onDownLoadSuccess(key: String, path: String, size: Long) {
//                        Log.i("Api",""+key+"paths:"+path+":size:"+size)
//                    }
//
//                    override fun onDownLoadPause(key: String) {
//
//                    }
//
//                    override fun onUpdate(
//                        key: String,
//                        progress: Int,
//                        read: Long,
//                        count: Long,
//                        done: Boolean
//                    ) {
//                        Log.i("Api",""+key+"progress:"+progress)
//                    }

                    override fun onWait(task: DownloadTask?) {
                        TODO("Not yet implemented")
                    }

                    override fun onPre(task: DownloadTask?) {
                        TODO("Not yet implemented")
                    }

                    override fun onTaskPre(task: DownloadTask?) {
                        TODO("Not yet implemented")
                    }

                    override fun onTaskResume(task: DownloadTask?) {
                        TODO("Not yet implemented")
                    }

                    override fun onTaskStart(task: DownloadTask?) {
                        TODO("Not yet implemented")
                    }

                    override fun onTaskStop(task: DownloadTask?) {
                        TODO("Not yet implemented")
                    }

                    override fun onTaskCancel(task: DownloadTask?) {
                        TODO("Not yet implemented")
                    }

                    override fun onTaskFail(task: DownloadTask?, e: java.lang.Exception?) {
                        TODO("Not yet implemented")
                    }

                    override fun onTaskComplete(task: DownloadTask?) {
                        TODO("Not yet implemented")
                    }

                    override fun onTaskRunning(task: DownloadTask?) {
                        TODO("Not yet implemented")
                    }

                    override fun onNoSupportBreakPoint(task: DownloadTask?) {
                        TODO("Not yet implemented")
                    }

                })
            }


//            DownloadUtils(this).downloadAPK("https://cdn.llscdn.com/yy/files/xs8qmxn8-lls-LLS-5.8-800-20171207-111607.apk","test.apk")
//            downLoad("test.apk", "https://cdn.llscdn.com/yy/files/xs8qmxn8-lls-LLS-5.8-800-20171207-111607.apk", File(PARENT_PATH))
        }



    }

    /**
     * 下载对应apk文件
     */
//    fun downLoad(filename: String?, url: String?, parentFile: File?): DownloadTask? {
//        val task = DownloadTask.Builder(url!!, parentFile!!)
//            .setFilename(filename) // the minimal interval millisecond for callback progress
//            .setMinIntervalMillisCallbackProcess(30)
//            .setAutoCallbackToUIThread(true) // do re-download even if the task has already been completed in the past.
//            .setPassIfAlreadyCompleted(false)
//            .build()
//        task.enqueue(getListener())
//        return task
//    }


    //先定义
    private val REQUEST_EXTERNAL_STORAGE = 1
    private val REQUEST_CODE_EXTERNAL_STORAGE = 20
    private val PERMISSIONS_STORAGE = arrayOf(
        "android.permission.READ_EXTERNAL_STORAGE",
        "android.permission.WRITE_EXTERNAL_STORAGE"
    )

    /**
     * 检查外部scard读写权限
     */
    private fun checkExtrnalStorage() {
        //首先判断用户手机的版本号 如果版本大于6.0就需要动态申请权限
        //如果版本小于6.0就直接去扫描二维码

        if (Build.VERSION.SDK_INT > 23) {
            //说明是android6.0之前的
            //添加动态权限申请
            //1.定义一个数组，用来装载申请的权限
            var permissons = arrayOf<String>(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )

            //2.判断这些权限有没有申请，没有申请的话，就把没有申请的权限放到一个数组里面
            val deniedPermissions = ArrayList<String>()
            for (permission in permissons) {
                val i: Int = ContextCompat.checkSelfPermission(this, permission)
                if (PackageManager.PERMISSION_DENIED == i) {
                    //说明权限没有被申请
                    deniedPermissions.add(permission)
                }
            }
            if (deniedPermissions.size === 0) {
                //说明是android6.0之前的
                return
            }
            //当你不知道数组多大的时候，就可以先创建一个集合，然后调用集合的toArray方法需要传递一个数组参数，这个数组参数的长度
            //设置成跟集合一样的长度
            val strings: Array<String> = deniedPermissions.toArray(arrayOfNulls(permissons.size))
            //3.去申请权限
            ActivityCompat.requestPermissions(this, strings, REQUEST_CODE_EXTERNAL_STORAGE)
        }
    }




    fun getPackage(context: Context, filePath: String?): String? {
        var packageName = ""
        val pm = context.packageManager
        val info = filePath?.let {
            pm.getPackageArchiveInfo(
                it,
                PackageManager.GET_ACTIVITIES
            )
        }
        if (info != null) {
            packageName = info.packageName
            val versionCode = info.versionCode
        }
        return packageName
    }

    override fun onDestroy() {
        super.onDestroy()
        Aria.download(this).unRegister();
    }

    override fun onClick(v: View?) {
        TODO("Not yet implemented")
    }

    override fun onWait(task: DownloadTask?) {
    }

    override fun onPre(task: DownloadTask?) {
    }

    override fun onTaskPre(task: DownloadTask?) {
    }

    override fun onTaskResume(task: DownloadTask?) {
    }

    override fun onTaskStart(task: DownloadTask?) {

    }

    override fun onTaskStop(task: DownloadTask?) {
    }

    override fun onTaskCancel(task: DownloadTask?) {
    }

    override fun onTaskFail(task: DownloadTask?, e: Exception?) {
    }

    override fun onTaskComplete(task: DownloadTask?) {
        Handler().post(object : Runnable{
            override fun run() {
                Toast.makeText(this@DownAppActivity,"正在加载中",Toast.LENGTH_SHORT).show()
                showLoading()
            }

        })
        Toast.makeText(this,"正在加载中",Toast.LENGTH_SHORT).show()

        Log.i("Api","1111"+task?.filePath)

        var documentFile = DocumentFile.fromFile(File(PARENT_PATH))
//
    val installResult = BlackBoxCore.get().installPackageAsUser(documentFile.getUri(),23)
    if (installResult.success) {
        Log.i("Api","sucess")

        var launch = BlackBoxCore.get().launchApk(getPackage(this@DownAppActivity,File(PARENT_PATH).absolutePath),23)
        if(launch){
            Log.i("Api","launch success")
            finish()
        }

    }else{
//        hideLoading()
        Log.i("Api","失败")
    }
    }

    override fun onTaskRunning(task: DownloadTask?) {
        task?.percent?.toFloat()?.let { horizontalProgressBar.setCurrentProgress(it) }
    }


    //
    override fun onNoSupportBreakPoint(task: DownloadTask?) {

    }

    //@Override public void onNoSupportBreakPoint(DownloadTask task) {
    //
    //}

    //@Override public void onNoSupportBreakPoint(DownloadTask task) {
    //
    //}
//



    fun toUri(context: Context, filePath: String?): Uri? {
        return Uri.fromFile(File(filePath))
    }

    @SuppressLint("Range")
    fun getImageContentUri(context: Context, imageFile: File): Uri? {
        val filePath = imageFile.absolutePath
        val cursor = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, arrayOf(
                MediaStore.Images.Media._ID
            ), MediaStore.Images.Media.DATA + "=? ", arrayOf(filePath), null
        )
        return if (cursor != null && cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID))
            val baseUri = Uri.parse("content://media/external/images/media")
            Uri.withAppendedPath(baseUri, "" + id)
        } else {
            if (imageFile.exists()) {
                val values = ContentValues()
                values.put(MediaStore.Images.Media.DATA, filePath)
                context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            } else {
                null
            }
        }
    }
    //单个权限申请
    val launcherPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()){
        if(it) {
            //同意
        }else{
            //拒绝
        }
    }
    fun createFileDir(dirFile: File?): Boolean {
        if (dirFile == null) return true
        if (dirFile.exists()) {
            return true
        }
        val parentFile = dirFile.parentFile
        return if (parentFile != null && !parentFile.exists()) {
            //父文件夹不存在，则先创建父文件夹，再创建自身文件夹
            createFileDir(parentFile) && createFileDir(dirFile)
        } else {
            val mkdirs = dirFile.mkdirs()
            val isSuccess = mkdirs || dirFile.exists()
            if (!isSuccess) {
                Log.e("FileUtil", "createFileDir fail $dirFile")
            }
            isSuccess
        }
    }


    fun createFile(dirPath: String?, fileName: String?): File? {
        return try {
            val dirFile = File(dirPath)
            if (!dirFile.exists()) {
                if (!createFileDir(dirFile)) {
                    Log.e("", "createFile dirFile.mkdirs fail")
                    return null
                }
            } else if (!dirFile.isDirectory) {
                val delete = dirFile.delete()
                return if (delete) {
                    createFile(dirPath, fileName)
                } else {
                    Log.e("",  "createFile dirFile !isDirectory and delete fail")
                    null
                }
            }
            val file = File(dirPath, fileName)
            if (!file.exists()) {
                if (!file.createNewFile()) {
                    Log.e("",  "createFile createNewFile fail")
                    return null
                }
            }
            file
        } catch (e: Exception) {
            Log.e("",  "createFile fail :" + e.message)
            e.printStackTrace()
            null
        }
    }




    /**
     * 获取手机文件路径
     */
    private fun getFile(fileName: String, parentPath: String): File {
        if (!TextUtils.isEmpty(parentPath)) {
            val parentPathFile = File(parentPath)
            if (!parentPathFile.exists()) {
                parentPathFile.mkdirs()
            }
        }
        val file = File(parentPath, fileName)
//        if (file.exists()) {
//            file.delete()
//        }
        return file
    }
    companion object{
        fun start(context: Activity){
            val intent = Intent(context, DownAppActivity::class.java)
            context.startActivityForResult(intent,RESULT_OK)
        }
    }

    override fun layoutId(): Int {
        return R.layout.activity_down_app
    }

    override fun initView(savedInstanceState: Bundle?) {
        Aria.download(this).register()
        if(Build.VERSION.SDK_INT<29){
            CreatePARENT_PATH =  Environment.getExternalStorageDirectory().getPath() + "/666Game/download/apk/"
            PARENT_PATH = Environment.getExternalStorageDirectory().getPath() + "/666Game/download/apk/xs8qmxn8-lls-LLS-5.8-800-20171207-111607.apk";
        }else{
            CreatePARENT_PATH =  ContextCompat.getExternalFilesDirs(
                App.getContext(), null)[0].getAbsolutePath() + "/666Game/download/apk/"
            PARENT_PATH = ContextCompat.getExternalFilesDirs(
                App.getContext(), null)[0].getAbsolutePath() + "/666Game/download/apk/xs8qmxn8-lls-LLS-5.8-800-20171207-111607.apk";
        }

//        launcherPermission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        checkExtrnalStorage()
        initData()
    }
}