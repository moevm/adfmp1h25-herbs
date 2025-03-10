package com.herbsapp.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.herbsapp.R
import com.herbsapp.data.room.entity.AccountEntity
import com.herbsapp.presentation.ui.CustomTextField
import com.herbsapp.presentation.ui.CustomToast
import com.herbsapp.presentation.ui.PrimaryButton
import com.herbsapp.presentation.ui.Routes
import com.herbsapp.presentation.ui.imageLoader
import com.herbsapp.presentation.ui.theme.Typography
import com.herbsapp.presentation.ui.theme.black
import com.herbsapp.presentation.ui.theme.primary
import com.herbsapp.presentation.viewmodels.AuthViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Preview(showBackground = true, backgroundColor = 0xFFFAF9F9)
@Composable
fun AccountScreen(navController: NavController = rememberNavController()) {
    val vm = koinViewModel<AuthViewModel>()

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 16.dp).padding(top = 48.dp)) {
        val name = vm.name.collectAsState()
        AccountTitle(text = name.value ?: "загрузка...")
        Spacer(Modifier.size(32.dp))
        AccountBody(vm)
    }

    val bottomSheetState = androidx.compose.material.rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter) {
        Column {
            Text(text = stringResource(R.string.authors_title), style = Typography.titleMedium, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp).clickable {
                scope.launch {
                    bottomSheetState.show()
                }
            }, color = black)
            PrimaryButton(modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 32.dp), text = stringResource(R.string.exit)) {
                vm.logout()
                navController.navigate(Routes.Launch.route) {
                    popUpTo(Routes.Account.route) {
                        inclusive = true
                    }
                }
            }
        }
    }
    Authors(bottomSheetState)

}

@Composable
fun Authors(bottomSheetState: ModalBottomSheetState) {
    ModalBottomSheetLayout (
        modifier = Modifier.fillMaxWidth().wrapContentHeight(),
        sheetShape = RoundedCornerShape(topEnd = 24.dp, topStart = 24.dp),
        sheetState = bottomSheetState,
        sheetContent = {
            Column(Modifier.fillMaxWidth().padding(16.dp)) {
                Box(Modifier.size(width = 36.dp, height = 3.dp).clip(CircleShape).background(black).align(Alignment.CenterHorizontally))
                Spacer(Modifier.size(16.dp))
                Text(text = stringResource(R.string.authors_title), style = Typography.displaySmall, textAlign = TextAlign.Center, color = primary, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.size(8.dp))
                Text(text = stringResource(R.string.authors), style = Typography.bodyLarge, fontSize = 18.sp, color = black)
            }
        }
    ) { }
}

@Composable
fun AccountBody(vm: AuthViewModel) {
    val context = LocalContext.current
    val newName = remember { mutableStateOf("") }
    val newMail = remember { mutableStateOf("") }

    ChangeAccountSetting(value = newName, title = stringResource(R.string.change_name), titleForTextFiled = stringResource(R.string.new_name)) {
        if (!newName.value.isNullOrEmpty()) {
            vm.changeName(newName.value)
            context.CustomToast(context.getString(R.string.change_name_apply))
            vm.name.value = newName.value
            newName.value = ""
        } else {
            context.CustomToast(context.getString(R.string.empty_text_field))
        }
    }
    Spacer(Modifier.size(32.dp))

    ChangeAccountSetting(value = newMail, title = stringResource(R.string.change_mail), titleForTextFiled = stringResource(R.string.new_mail)) {
        if (!newMail.value.isNullOrEmpty() && newMail.value.contains("@") && newMail.value.substringAfter("@").contains(".")) {
            vm.changeEmail(newMail.value)
            context.CustomToast(context.getString(R.string.change_mail_apply))
            newMail.value = ""
        } else {
            context.CustomToast(context.getString(R.string.empty_text_field))
        }
    }


}

@Composable
fun ChangeAccountSetting(value: MutableState<String>, title: String, titleForTextFiled: String, onSave: () -> Unit) {
    val context = LocalContext.current
    Text(text = title, style = Typography.titleLarge, modifier = Modifier.padding(start = 16.dp))
    Spacer(Modifier.size(16.dp))

    CustomTextField(
        label = titleForTextFiled,
        value = value,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
    )
    CheckButton(onSave)
}

@Composable
fun CheckButton(onClick: () -> Unit) {
    val context = LocalContext.current
    Image(rememberAsyncImagePainter(R.drawable.ico_check, context.imageLoader()), contentDescription = "apply", modifier = Modifier.padding(start = 16.dp).size(60.dp, 50.dp).clip(
        CircleShape).background(primary).clickable{ onClick() }.padding(12.dp))

}

@Composable
fun AccountTitle(text: String) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        Column {
            Text(text = stringResource(R.string.account_title) + " " + text + "!", style = Typography.titleLarge)
            UserLocation(style = Typography.headlineMedium, iconSize = 16.dp)
        }
        AccountImage {  }
    }
}