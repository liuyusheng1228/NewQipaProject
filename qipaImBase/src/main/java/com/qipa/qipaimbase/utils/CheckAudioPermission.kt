package com.qipa.qipaimbase.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.MediaRecorder
import android.os.Build
import android.widget.Toast

object CheckAudioPermission {
    // 音频获取源
    var audioSource = MediaRecorder.AudioSource.MIC

    // 设置音频采样率，44100是目前的标准，但是某些设备仍然支持22050，16000，11025
    var sampleRateInHz = 44100

    // 设置音频的录制的声道CHANNEL_IN_STEREO为双声道，CHANNEL_CONFIGURATION_MONO为单声道
    var channelConfig = AudioFormat.CHANNEL_IN_STEREO

    // 音频数据格式:PCM 16位每个样本。保证设备支持。PCM 8位每个样本。不一定能得到设备支持。
    var audioFormat = AudioFormat.ENCODING_PCM_16BIT

    // 缓冲区字节大小
    var bufferSizeInBytes = 0

    /**
     * 判断是是否有录音权限
     */
    fun isHasPermission(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(context, "没有麦克风权限，好难啊！",Toast.LENGTH_SHORT).show()
                return false
            }
            return true
        }
        //        bufferSizeInBytes = 0;
//        bufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRateInHz,
//                channelConfig, audioFormat);
//        AudioRecord audioRecord =  new AudioRecord(audioSource, sampleRateInHz,
//                channelConfig, audioFormat, bufferSizeInBytes);
//        //开始录制音频
//        try{
//            // 防止某些手机崩溃，例如联想
//            audioRecord.startRecording();
//        }catch (IllegalStateException e){
//            e.printStackTrace();
//        }
//        /**
//         * 根据开始录音判断是否有录音权限
//         */
//        if (audioRecord.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING) {
//            return false;
//        }
//        audioRecord.stop();
//        audioRecord.release();
//        audioRecord = null;
        return true
    }
}
