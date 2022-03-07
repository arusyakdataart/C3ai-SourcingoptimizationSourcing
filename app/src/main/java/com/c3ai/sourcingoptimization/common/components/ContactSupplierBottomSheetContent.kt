package com.c3ai.sourcingoptimization.common.components

import android.Manifest
import android.R.attr
import android.content.Intent
import android.net.Uri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Sms
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat.startActivity
import com.c3ai.sourcingoptimization.R

import android.content.pm.PackageManager

import android.telephony.SmsManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat.checkSelfPermission
import android.R.attr.phoneNumber
import android.content.ActivityNotFoundException
import android.provider.Telephony
import android.widget.Toast


@Composable
fun ContactSupplierBottomSheetContent(phoneNumber: String, email: String) {
    val context = LocalContext.current
    val callIntent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + 8802177690))

//    val smsUri = Uri.parse("smsto:" + phoneNumber)
//    val intent = Intent(Intent.ACTION_VIEW, smsUri)
//    intent.putExtra("sms_body", "sms text")
//    intent.type = "vnd.android-dir/mms-sms"

    val defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(context)
    val smsIntent = Intent(Intent.ACTION_VIEW)
    smsIntent.data = Uri.parse("smsto:")
    smsIntent.putExtra("address", "8802177690")
    smsIntent.type = "vnd.android-dir/mms-sms"
    if (defaultSmsPackageName != null) {
        smsIntent.setPackage(defaultSmsPackageName);
    }

    val emailIntent = Intent(Intent.ACTION_SEND)
    emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf<String>("arusyak@dataaty.com"))
    emailIntent.type = "message/rfc822"

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission Accepted: Do something
            //startActivity(context, callIntent, null)

//            try {
//                startActivity(context, smsIntent, null)
//            } catch (ex: ActivityNotFoundException) {
//                Toast.makeText(context, "No activity found to send message", Toast.LENGTH_LONG).show()
//            }

            startActivity(context, Intent.createChooser(emailIntent, "Choose an Email client :"), null)
        } else {
            // Permission Denied: Do something
        }
    }


    BottomSheetContent(
        BottomSheetItem(
            image = Icons.Filled.Call,
            contentDescription = stringResource(R.string.cd_make_call),
            text = stringResource(R.string.make_call),
            onClick = {
                when (PackageManager.PERMISSION_GRANTED) {
                    checkSelfPermission(context, Manifest.permission.CALL_PHONE) -> {
                        startActivity(context, smsIntent, null)
                    }
                    else -> {
                        // Asking for permission
                        launcher.launch(Manifest.permission.CALL_PHONE)
                    }
                }
            }
        ),
        BottomSheetItem(
            image = Icons.Filled.Sms,
            contentDescription = stringResource(R.string.cd_send_sms),
            text = stringResource(R.string.send_sms),
            onClick = {
                when (PackageManager.PERMISSION_GRANTED) {
                    checkSelfPermission(context, Manifest.permission.SEND_SMS) -> {
                        try {
                            startActivity(context, smsIntent, null)
                        } catch (ex: ActivityNotFoundException) {
                            Toast.makeText(context, "No activity found to send message", Toast.LENGTH_LONG).show()
                        }
                    }
                    else -> {
                        // Asking for permission
                        launcher.launch(Manifest.permission.SEND_SMS)
                    }
                }
            }
        ),
        BottomSheetItem(
            image = Icons.Filled.Email,
            contentDescription = stringResource(R.string.cd_send_email),
            text = stringResource(R.string.send_email),
            onClick = {
                startActivity(context, Intent.createChooser(emailIntent, "Choose an Email client :"), null)
            }
        ),
    )
}