package com.qipa.newboxproject.data.push

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.hyphenate.util.EMLog


class HeadsetReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent) {
        // 耳机插入状态 0 拔出，1 插入
        val state = if (intent.getIntExtra("state", 0) === 0) false else true
        // 耳机类型
        val name: String? = intent.getStringExtra("name")
        // 耳机是否带有麦克风 0 没有，1 有
        val mic = if (intent.getIntExtra("microphone", 0) === 0) false else true
        val headsetChange = String.format("耳机插入: %b, 有麦克风: %b", state, mic)
        EMLog.d("HeadsetReceiver", headsetChange)
        Toast.makeText(context, headsetChange, Toast.LENGTH_SHORT).show()
    }
}
