package com.c3ai.sourcingoptimization.common.components

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import com.c3ai.sourcingoptimization.R

@Composable
fun ContactBuyerBottomSheetContent(phoneNumber: String, email: String) {
    val context = LocalContext.current
    val callIntent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber))

    val emailIntent = Intent(Intent.ACTION_SEND)
    emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
    emailIntent.type = "message/rfc822"

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission Accepted: Do something
            ContextCompat.startActivity(context, callIntent, null)

        }
    }

    BottomSheetContent(
        BottomSheetItem(
            image = Icons.Filled.Call,
            contentDescription = stringResource(R.string.cd_make_call),
            text = stringResource(R.string.make_call),
            onClick = {
                when (PackageManager.PERMISSION_GRANTED) {
                    ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) -> {
                        ContextCompat.startActivity(context, callIntent, null)
                    }
                    else -> {
                        // Asking for permission
                        launcher.launch(Manifest.permission.CALL_PHONE)
                    }
                }
            }
        ),
        BottomSheetItem(
            image = Icons.Filled.Email,
            contentDescription = stringResource(R.string.cd_send_email),
            text = stringResource(R.string.send_email),
            onClick = {
                ContextCompat.startActivity(
                    context,
                    Intent.createChooser(emailIntent, "Choose an Email client :"),
                    null
                )
            }
        ),
    )
}