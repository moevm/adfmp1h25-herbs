package com.herbsapp.presentation.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.herbsapp.R
import com.herbsapp.presentation.ui.CustomTextField
import com.herbsapp.presentation.ui.CustomToast
import com.herbsapp.presentation.ui.PrimaryButton
import com.herbsapp.presentation.ui.Resource
import com.herbsapp.presentation.ui.Routes
import com.herbsapp.presentation.ui.theme.Typography
import com.herbsapp.presentation.ui.theme.black
import com.herbsapp.presentation.ui.theme.border
import com.herbsapp.presentation.ui.theme.gray
import com.herbsapp.presentation.ui.theme.white
import com.herbsapp.presentation.viewmodels.AuthViewModel
import org.koin.androidx.compose.koinViewModel

@Preview(showBackground = true, backgroundColor = 0xFFFAF9F9)
@Composable
fun LoginScreen(navController: NavController = rememberNavController()) {
    val vm = koinViewModel<AuthViewModel>()
    val login = vm.login.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
    ) {
        Spacer(Modifier.size(16.dp))
        Text(text = stringResource(R.string.login_title), style = Typography.displayMedium)
        Spacer(Modifier.size(8.dp))
        Text(
            text = stringResource(R.string.login_subtitle),
            style = Typography.bodyMedium.copy(color = gray)
        )

        Spacer(Modifier.size(32.dp))

        val inputMail = remember { mutableStateOf("") }
        val inputPassword = remember { mutableStateOf("") }
        val isValuesNotEmpty = remember { mutableStateOf(inputMail.value.isNotEmpty() && inputPassword.value.isNotEmpty()) }

        CustomTextField(
            label = stringResource(R.string.email),
            value = inputMail,
            isErrorExpression = (inputMail.value.contains('@') || inputMail.value.isEmpty()),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next)
        )

        CustomTextField(
            label = stringResource(R.string.password),
            value = inputPassword,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done), visualTransformation = PasswordVisualTransformation()
        )

        Spacer(Modifier.size(16.dp))

        val context = LocalContext.current
        val emptyText = stringResource(R.string.empty_text_field)
        val wrongText = stringResource(R.string.wrong_login)

        PrimaryButton(text = stringResource(R.string.login)) {
            if (inputMail.value.isNotEmpty() && inputMail.value.contains('@') && inputPassword.value.isNotEmpty()) {
                vm.loginUser(inputMail.value, inputPassword.value)
            } else {
                context.CustomToast(emptyText)
            }
        }

        when(login.value) {
            is Resource.Loading -> {}
            is Resource.Success -> {
                LaunchedEffect(Unit) {
                    context.CustomToast(context.getString(R.string.login_title) + " " + vm.currentUser!!.displayName)
                    navController.navigate(Routes.Main.route) {
                        popUpTo(Routes.Main.route) {
                            inclusive = true
                        }
                        popUpTo(Routes.Launch.route) {
                            inclusive = true
                        }
                    }
                }
            }
            is Resource.Failure -> {
                context.CustomToast(wrongText)
            }
            null -> {}
        }
    }
}
