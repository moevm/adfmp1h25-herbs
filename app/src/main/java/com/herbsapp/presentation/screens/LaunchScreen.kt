package com.herbsapp.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.herbsapp.R
import com.herbsapp.presentation.ui.CustomToast
import com.herbsapp.presentation.ui.Routes
import com.herbsapp.presentation.ui.PrimaryButton
import com.herbsapp.presentation.ui.theme.Typography
import com.herbsapp.presentation.ui.theme.black
import com.herbsapp.presentation.ui.theme.gray
import com.herbsapp.presentation.ui.theme.primary
import com.herbsapp.presentation.viewmodels.AuthViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.androidx.compose.koinViewModel

@Preview(showBackground = true, backgroundColor = 0xFFFAF9F9)
@Composable
fun LaunchScreen(navController: NavController = rememberNavController()) {
    val vm = koinViewModel<AuthViewModel>()
    val context = LocalContext.current
    val loadingDialogState = remember { mutableStateOf(false) }

    if (vm.currentUser != null) {
        loadingDialogState.value = true
        LaunchedEffect(Unit) {
            context.CustomToast(context.getString(R.string.login_title) + " " + vm.currentUser!!.displayName)
            loadingDialogState.value = false
            navController.navigate(Routes.Main.route) {
                popUpTo(Routes.Launch.route) {
                    inclusive = true
                }
            }

        }
    }
    if (loadingDialogState.value) {
        LoadingDialog(loadingDialogState)
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

            Spacer(Modifier.size(32.dp))

            PrimaryButton(text = stringResource(R.string.login)) {
                navController.navigate(Routes.Login.route)
            }

            Spacer(Modifier.size(16.dp))

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
    }
}

