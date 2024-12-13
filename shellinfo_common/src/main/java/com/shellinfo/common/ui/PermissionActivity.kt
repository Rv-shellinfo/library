package com.shellinfo.common.ui

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

class PermissionActivity:AppCompatActivity() {

    companion object {
        private const val EXTRA_PERMISSIONS = "extra_permissions"
        private const val REQUEST_CODE = 1001
        private var callback: ((Boolean) -> Unit)? = null

        fun start(context: Context, permissions: Array<String>, resultCallback: (Boolean) -> Unit) {
            callback = resultCallback
            val intent = Intent(context, PermissionActivity::class.java).apply {
                putExtra(EXTRA_PERMISSIONS, permissions)
            }
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val permissions = intent.getStringArrayExtra(EXTRA_PERMISSIONS) ?: run {
            callback?.invoke(false)
            finish()
            return
        }

        ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            val allGranted = grantResults.all { it == PackageManager.PERMISSION_GRANTED }
            callback?.invoke(true)
        }
        finish()
    }
}