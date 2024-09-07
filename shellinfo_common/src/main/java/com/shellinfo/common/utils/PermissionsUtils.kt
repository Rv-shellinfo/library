package com.shellinfo.common.utils

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PermissionsUtils @Inject constructor(){

    private val _permissionsState = MutableLiveData<PermissionsState>()
    val permissionsState: LiveData<PermissionsState> = _permissionsState

    fun checkPermissions(permissions: Array<String>, context: Context) {
        val notGrantedPermissions = permissions.filter {
            ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
        }

        if (notGrantedPermissions.isEmpty()) {
            _permissionsState.value = PermissionsState.Granted
        } else {
            val shouldShowRationale = notGrantedPermissions.any {
                ActivityCompat.shouldShowRequestPermissionRationale(context as Activity, it)
            }
            _permissionsState.value = if (shouldShowRationale) {
                PermissionsState.ShouldShowRationale(notGrantedPermissions.toTypedArray())
            } else {
                PermissionsState.RequestPermissions(notGrantedPermissions.toTypedArray())
            }
        }
    }

    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_CODE) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                _permissionsState.value = PermissionsState.Granted
            } else {
                _permissionsState.value = PermissionsState.Denied
            }
        }
    }

    companion object {
        const val REQUEST_CODE = 1
    }

    sealed class PermissionsState {
        object Granted : PermissionsState()
        object Denied : PermissionsState()
        data class ShouldShowRationale(val permissions: Array<String>) : PermissionsState()
        data class RequestPermissions(val permissions: Array<String>) : PermissionsState()
    }
}