package com.c3ai.sourcingoptimization.common.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Sms
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.c3ai.sourcingoptimization.R

@Composable
fun ContactSupplierBottomSheetContent() {
    BottomSheetContent(
        BottomSheetItem(
            image = Icons.Filled.Call,
            contentDescription = stringResource(R.string.cd_make_call),
            text = stringResource(R.string.make_call),
        ),
        BottomSheetItem(
            image = Icons.Filled.Sms,
            contentDescription = stringResource(R.string.cd_send_sms),
            text = stringResource(R.string.send_sms),
        ),
        BottomSheetItem(
            image = Icons.Filled.Email,
            contentDescription = stringResource(R.string.cd_send_email),
            text = stringResource(R.string.send_email),
        ),
    )
}