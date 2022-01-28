package com.c3ai.sourcingoptimization.presentation.search

import android.content.Intent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.c3ai.sourcingoptimization.R
import com.c3ai.sourcingoptimization.common.components.MButton
import com.c3ai.sourcingoptimization.presentation.item_details.ItemDetailsActivity

@ExperimentalAnimationApi
@Composable
fun SearchScreen(
    navController: NavController,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_app_logo),
            contentDescription = null
        )
        OutlinedTextField(
            value = "",
            onValueChange = {},
            placeholder = {
                Text(
                    text = stringResource(R.string.search_supplier_item_po),
                    color = MaterialTheme.colors.secondary
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp),
            singleLine = true,
            textStyle = TextStyle(fontSize = 16.sp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                unfocusedBorderColor = MaterialTheme.colors.secondary
            )
        )
        MButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            text = "Item Details",
            onClick = {
                context.startActivity(Intent(context, ItemDetailsActivity::class.java))
            }
        )
    }
}