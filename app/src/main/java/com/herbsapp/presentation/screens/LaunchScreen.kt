package com.herbsapp.presentation.screens

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.herbsapp.R
import com.herbsapp.presentation.MainActivity
import com.herbsapp.presentation.ui.PrimaryButton
import com.herbsapp.presentation.ui.Routes
import com.herbsapp.presentation.ui.theme.Typography
import com.herbsapp.presentation.ui.theme.black
import com.herbsapp.presentation.ui.theme.border
import com.herbsapp.presentation.ui.theme.gray
import com.herbsapp.presentation.ui.theme.primary
import com.herbsapp.presentation.ui.theme.white
import com.herbsapp.presentation.viewmodels.AuthViewModel


@Composable
fun LaunchScreen(navController: NavController = rememberNavController(), vm: AuthViewModel) {
    val context = LocalContext.current
    val loadingDialogState = remember { mutableStateOf(false) }

    val isExpandDisclaimer = remember { mutableStateOf(false) }
    Disclaimer(isExpandDisclaimer)


    if (!isExpandDisclaimer.value) {
        if (vm.currentUser != null) {
            loadingDialogState.value = true
        }
        val login = vm.login.collectAsState()
        LoginWait(login, loadingDialogState, context, navController, vm)
        if (loadingDialogState.value) {
            LoadingDialog(loadingDialogState)
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(R.drawable.img_launch_image),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
                .clip(
                    RoundedCornerShape(bottomEnd = 26.dp, bottomStart = 26.dp)
                ),
            contentDescription = null
        )

        Spacer(Modifier.size(16.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = stringResource(R.string.app_name),
                style = Typography.displayMedium,
                color = primary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
            Text(
                text = stringResource(R.string.launch_title),
                style = Typography.displayMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            Spacer(Modifier.size(24.dp))

            PrimaryButton(text = stringResource(R.string.login)) {
                navController.navigate(Routes.Login.route)
            }

            Spacer(Modifier.size(16.dp))

            GuestButton(vm, context)

            Spacer(Modifier.size(16.dp))

            RegisterButton(navController)

            Spacer(Modifier.size(32.dp))

        }
    }
}

@Composable
fun Disclaimer(isExpandDisclaimer: MutableState<Boolean>) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("APP", Context.MODE_PRIVATE)
    if (!sharedPreferences.getBoolean("IS_LIST_POPULATED", false)) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("IS_LIST_POPULATED", true)
        editor.apply()
        editor.commit()
        isExpandDisclaimer.value = true
    }

    DisclaimerDialog(isExpandDisclaimer)

}

@Composable
fun DisclaimerDialog(isExpand: MutableState<Boolean>) {
    if (isExpand.value) {
        Dialog(
            onDismissRequest = { isExpand.value = false },
            properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
        ) {
            Column(Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(white)
                .padding(32.dp)
                .verticalScroll(rememberScrollState())) {
                Text(
                    text = stringResource(R.string.warning),
                    style = Typography.displaySmall,
                    textAlign = TextAlign.Center,
                    color = primary,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.size(8.dp))
                Text(
                    text = stringResource(R.string.disclaimer_title),
                    style = Typography.bodyLarge,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    color = black
                )
                Spacer(Modifier.size(24.dp))
                PrimaryButton(text = stringResource(R.string.disclaimer_button)) {
                    isExpand.value = false
                }
            }
        }
    }
}

@Composable
fun RegisterButton(navController: NavController) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, gray, CircleShape)
            .clickable {
                navController.navigate(Routes.Register.route)
            }
            .padding(vertical = 22.dp)
        ,
        text = stringResource(R.string.register),
        style = Typography.labelLarge.copy(color = black)
    )
}

@Composable
fun GuestButton(vm: AuthViewModel, context: Context) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CircleShape)
            .background(border)
            .clickable {
                vm.loginByGuest(context.applicationContext)
            }
            .padding(vertical = 22.dp)
        ,
        text = stringResource(R.string.guest),
        style = Typography.labelLarge.copy(color = black)
    )
}

