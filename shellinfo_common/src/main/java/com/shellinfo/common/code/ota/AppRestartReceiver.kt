package com.shellinfo.common.code.ota

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AppRestartReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val restartIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        restartIntent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        context.startActivity(restartIntent)
    }
}