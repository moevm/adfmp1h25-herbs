package com.herbsapp.presentation.screens

import android.content.Context
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.herbsapp.R
import com.herbsapp.data.room.entity.HerbEntity
import com.herbsapp.presentation.ui.CustomToast
import com.herbsapp.presentation.ui.Determiner
import com.herbsapp.presentation.ui.PrimaryButton
import com.herbsapp.presentation.ui.PrimaryButtonGray
import com.herbsapp.presentation.ui.Routes
import com.herbsapp.presentation.ui.imageLoader
import com.herbsapp.presentation.ui.theme.Typography
import com.herbsapp.presentation.ui.theme.black
import com.herbsapp.presentation.ui.theme.primary
import com.herbsapp.presentation.viewmodels.DeterminerViewModel
import org.koin.androidx.compose.koinViewModel

@Preview(showBackground = true, backgroundColor = 0xFFFAF9F9)
@Composable
fun DeterminerScreen(navController: NavController = rememberNavController()) {
    val vm = koinViewModel<DeterminerViewModel>()
    val determiner = vm.determinerList.collectAsStateWithLifecycle()
    val isComplete = remember { mutableStateOf(false) }
    val result = vm.resultHerbs.collectAsStateWithLifecycle()

    Column(
        Modifier
            .fillMaxSize()
    ) {
        Spacer(Modifier.size(48.dp))
        TitileWithBackButton(stringResource(R.string.determiner), navController)

        if (!isComplete.value) {
            BodyQuiz(determiner.value, vm)
        } else {
            BodyResult(result.value, vm, isComplete)
        }

    }
    Box(Modifier.fillMaxSize().padding(horizontal = 16.dp).padding(bottom = 16.dp), contentAlignment = Alignment.BottomCenter) {
        if (!isComplete.value) {
            ContinueButton(determiner.value, vm, isComplete)
        } else {
            if (!result.value.isNullOrEmpty()) {
                YesOrNotButtons(result.value, vm, isComplete, navController)
            }
        }
    }

}

@Composable
fun BodyResult(result: List<HerbEntity>, vm: DeterminerViewModel, isComplete: MutableState<Boolean>) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp).verticalScroll(
        rememberScrollState()
    )) {
        Spacer(Modifier.size(16.dp))
        val context = LocalContext.current

        if (!result.isNullOrEmpty()) {
            val currentId = vm.currentHerbID.collectAsState()
            val currentHerb = result[currentId.value]

            Text(
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontStyle = Typography.titleLarge.fontStyle, color = black, fontSize = 20.sp)) {
                        append(context.getString(R.string.determiner_find) + " ")
                    }
                    withStyle(style = SpanStyle(fontStyle = Typography.titleLarge.fontStyle, color = black, fontSize = 20.sp, fontWeight = FontWeight.Bold)) {
                        append(currentHerb.name + " ?")
                    }
                },
            )
            Spacer(Modifier.size(16.dp))
            Image(
                rememberAsyncImagePainter(currentHerb.imageURL.first(), context.imageLoader()),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().height(400.dp).clip(
                    RoundedCornerShape(20.dp)
                )
            )
        } else {
            notFindHerb(vm, isComplete, context)
        }
    }
}

@Composable
fun BodyQuiz(determiner: List<Determiner>, vm: DeterminerViewModel) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .nestedScroll(
                rememberNestedScrollInteropConnection()
            )
    ) {
        items(determiner) {
            QuizField(it, vm)
        }
    }
}

@Composable
fun ContinueButton(determinerList: List<Determiner>, vm: DeterminerViewModel, isComplete: MutableState<Boolean>) {
    val context = LocalContext.current
    PrimaryButton(text = stringResource(R.string.continuee)) {
        if (determinerList.filter { it.selectVariant != null } == determinerList) {
            vm.getHerbByParams()
            isComplete.value = true
        } else {
            context.CustomToast(text = context.getString(R.string.determiner_choose_all))
        }
    }
}

@Composable
fun YesOrNotButtons(herbsEntity: List<HerbEntity>, vm: DeterminerViewModel, isComplete: MutableState<Boolean>, navController: NavController) {
    val currentId = vm.currentHerbID.collectAsState()
    val currentHerb = remember { mutableStateOf(herbsEntity[currentId.value]) }
    val context = LocalContext.current
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {

        PrimaryButton(modifier = Modifier.weight(1f), text = stringResource(R.string.yes)) {
            navController.navigate(Routes.FlowerInfo.route + "/${currentHerb.value.id}")
        }
        Spacer(Modifier.size(16.dp))
        PrimaryButtonGray(modifier = Modifier.weight(1f), text = stringResource(R.string.no)) {
            notFindHerb(vm, isComplete, context)
        }
    }
}

fun notFindHerb(vm: DeterminerViewModel, isComplete: MutableState<Boolean>, context: Context) {
    vm.nextHerb {
        context.CustomToast(context.getString(R.string.nothing_find))
        isComplete.value = false
    }
}

@Composable
fun QuizField(determiner: Determiner, vm: DeterminerViewModel) {
    Spacer(Modifier.size(16.dp))
    Text(text = determiner.title, style = Typography.headlineLarge)
    Spacer(Modifier.size(16.dp))
    LazyRow(Modifier.fillMaxWidth(),horizontalArrangement = Arrangement.Center) {
        items(determiner.variants) {
            QuizVariant(variant = it, isChoose = (it == determiner.selectVariant), determiner = determiner, vm)
        }
    }
}

@Composable
fun QuizVariant(variant: String, isChoose: Boolean, determiner: Determiner, vm: DeterminerViewModel) {

    val borderColor by animateColorAsState(if (isChoose) primary else black)

    Text(
        text = variant,
        style = Typography.bodyLarge.copy(textAlign = TextAlign.Center),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier
            .width(120.dp)
            .padding(8.dp)
            .clickable {
                vm.choose(determiner = determiner, variant = if (!isChoose) variant else null)
            }
            .border(1.dp, borderColor, CircleShape)
            .padding(horizontal = 12.dp, vertical = 10.dp)
    )
}
