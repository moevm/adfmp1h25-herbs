package com.herbsapp.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.herbsapp.R
import com.herbsapp.presentation.ui.CustomTextField
import com.herbsapp.presentation.ui.CustomToast
import com.herbsapp.presentation.ui.PrimaryButton
import com.herbsapp.presentation.ui.Resource
import com.herbsapp.presentation.ui.Routes
import com.herbsapp.presentation.ui.theme.Typography
import com.herbsapp.presentation.ui.theme.gray
import com.herbsapp.presentation.ui.theme.primary
import com.herbsapp.presentation.ui.theme.white
import com.herbsapp.presentation.viewmodels.AuthViewModel
import org.koin.androidx.compose.koinViewModel

@Preview(showBackground = true, backgroundColor = 0xFFFAF9F9)
@Composable
fun RegisterScreen(navController: NavController = rememberNavController()) {
    val context = LocalContext.current
    val vm = koinViewModel<AuthViewModel>()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp)
    ) {
        Spacer(Modifier.size(16.dp))
        Text(text = stringResource(R.string.register), style = Typography.displayMedium)
        Spacer(Modifier.size(8.dp))
        Text(
            text = stringResource(R.string.register_subtitle),
            style = Typography.bodyMedium.copy(color = gray)
        )

        Spacer(Modifier.size(32.dp))

        val inputName = remember { mutableStateOf("") }
        val inputMail = remember { mutableStateOf("") }
        val inputPassword = remember { mutableStateOf("") }
        val inputPassword2 = remember { mutableStateOf("") }

        CustomTextField(
            label = stringResource(R.string.name),
            value = inputName,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )

        CustomTextField(
            label = stringResource(R.string.email),
            value = inputMail,
            isErrorExpression = (inputMail.value.contains('@') || inputMail.value.isEmpty()),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )
        )

        CustomTextField(
            label = stringResource(R.string.password),
            value = inputPassword,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            isErrorExpression = (inputPassword.value.length >= 6 || inputPassword.value.isEmpty()),
            visualTransformation = PasswordVisualTransformation()
        )

        CustomTextField(
            label = stringResource(R.string.repeat_password),
            value = inputPassword2,
            isErrorExpression = (inputPassword.value == inputPassword2.value),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            visualTransformation = PasswordVisualTransformation()
        )

        val register = vm.register.collectAsState()
        val emptyFieldsText = stringResource(R.string.empty_text_field)
        val passwordsDontMatchText = stringResource(R.string.passwords_dont_match)
        PrimaryButton(text = stringResource(R.string.register)) {
            if (inputName.value.isNotEmpty() && inputMail.value.isNotEmpty() && inputPassword.value.isNotEmpty() && inputPassword2.value.isNotEmpty() && (inputMail.value.contains(
                    '@'
                ) && inputMail.value.isNotEmpty()) && inputMail.value.substringAfter("@").contains(".")
            ) {
                if (inputPassword.value == inputPassword2.value) {
                    if (inputPassword.value.length >= 6) {
                        vm.registerUser(
                            name = inputName.value,
                            email = inputMail.value,
                            password = inputPassword.value
                        )
                    } else {
                        context.CustomToast(context.getString(R.string.pass_length))
                    }
                } else {
                    context.CustomToast(passwordsDontMatchText)
                }
            } else {
                context.CustomToast(emptyFieldsText)
            }
        }

        val dialogState = remember { mutableStateOf(false) }
        when (register.value) {
            is Resource.Success -> {
                LaunchedEffect(Unit) {
                    dialogState.value = false
                    context.CustomToast(text = context.getString(R.string.register_done))
                    navController.navigate(Routes.Login.route) {
                        popUpTo(Routes.Login.route) {
                            inclusive = true
                        }
                        popUpTo(Routes.Launch.route) {
                            inclusive = true
                        }
                    }
                }
            }
            is Resource.Failure -> {
                dialogState.value = false
                context.CustomToast(text = (register.value as Resource.Failure).exception.message.toString())
            }

            is Resource.Loading -> {
                dialogState.value = true
            }
            null -> {}

        }
        if (dialogState.value) {
            LoadingDialog(dialogState)
        }

        Box(modifier = Modifier
            .fillMaxSize()
            .clickable {
                navController.navigate(Routes.Login.route)
            }, contentAlignment = Alignment.BottomCenter) {
            Row {
                Text(
                    text = stringResource(R.string.already_have_account),
                    style = Typography.bodyMedium.copy(
                        gray
                    )
                )
                Spacer(Modifier.size(4.dp))
                Text(
                    text = stringResource(R.string.login),
                    style = Typography.titleSmall
                )
            }
        }

    }

}

@Composable
fun LoadingDialog(dialogState: MutableState<Boolean>) {
    Dialog(
        onDismissRequest = {
            dialogState.value = false
        },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Box(modifier = Modifier.fillMaxWidth().clip(CircleShape).background(
            primary).padding(16.dp), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(color = white)
                Spacer(Modifier.size(8.dp))
                Text(text = stringResource(R.string.loading), style = Typography.headlineLarge.copy(color = white), textAlign = TextAlign.Center)
            }
        }
    }
}