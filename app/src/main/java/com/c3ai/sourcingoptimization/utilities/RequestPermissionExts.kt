package com.c3ai.sourcingoptimization.utilities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.contract.ActivityResultContracts.RequestMultiplePermissions
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

typealias RequestPermissionLaunchAction = () -> Unit

fun ComponentActivity.createPermissionRequest(
    permission: String,
    maxSdkVersion: Int? = null,
    onResult: (Boolean) -> Unit = {}
): RequestPermissionLaunchAction {
    val launcher = createPermissionLauncher(permission, onResult)
    return {
        when {
            shouldSkipBySdkVersion(maxSdkVersion) || isPermissionGranted(this, permission) -> {
                onResult(true)
            }
            else -> launcher()
        }
    }
}

fun Fragment.createPermissionRequest(
    permission: String,
    maxSdkVersion: Int? = null,
    onResult: (Boolean) -> Unit = {}
): RequestPermissionLaunchAction {
    val launcher = createPermissionLauncher(permission, onResult)
    return {
        when {
            shouldSkipBySdkVersion(maxSdkVersion) || isPermissionGranted(context, permission) -> {
                onResult(true)
            }
            else -> launcher()
        }
    }
}

fun ActivityResultCaller.createPermissionLauncher(
    permission: String,
    onResult: (Boolean) -> Unit
): RequestPermissionLaunchAction {
    return createMultiplePermissionsRequest(
        arrayOf(permission)
    ) { result -> onResult(result[permission] ?: false) }
}

fun ActivityResultCaller.createMultiplePermissionsRequest(
    permissions: Array<String>,
    onResult: (Map<String, Boolean>) -> Unit
): RequestPermissionLaunchAction {
    val launcher = registerForActivityResult(RequestMultiplePermissions(), onResult)
    return { launcher.launch(permissions) }
}

fun Activity.createPermissionSettingsIntent(): Intent {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    val uri: Uri = Uri.fromParts("package", packageName, null)
    intent.data = uri
    return intent
}

fun isPermissionGranted(context: Context?, permission: String): Boolean {
    return context != null
            && PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(
        context,
        permission
    )
}

private fun shouldSkipBySdkVersion(maxSdkVersion: Int? = null): Boolean {
    return maxSdkVersion != null && Build.VERSION.SDK_INT > maxSdkVersion
}
