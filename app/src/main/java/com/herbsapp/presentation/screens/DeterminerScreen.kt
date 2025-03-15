package com.herbsapp.presentation.screens

import android.content.Context
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
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
import com.herbsapp.presentation.ui.DeterminerImaged
import com.herbsapp.presentation.ui.PrimaryButton
import com.herbsapp.presentation.ui.PrimaryButtonGray
import com.herbsapp.presentation.ui.ResultHerb
import com.herbsapp.presentation.ui.Routes
import com.herbsapp.presentation.ui.imageLoader
import com.herbsapp.presentation.ui.theme.Typography
import com.herbsapp.presentation.ui.theme.black
import com.herbsapp.presentation.ui.theme.border
import com.herbsapp.presentation.ui.theme.gray
import com.herbsapp.presentation.ui.theme.primary
import com.herbsapp.presentation.ui.theme.white
import com.herbsapp.presentation.viewmodels.DeterminerViewModel

@Composable
fun DeterminerScreen(
    navController: NavController = rememberNavController(),
    vm: DeterminerViewModel
) {
    LaunchedEffect(Unit) {
        vm.updateData()
    }
    val determiner = vm.determinerList.collectAsStateWithLifecycle()
    val determinerImaged = vm.determinerListWithImages.collectAsStateWithLifecycle()
    val isComplete = remember { mutableStateOf(false) }
    val result = vm.resultHerbs.collectAsStateWithLifecycle()

    Column(
        Modifier
            .fillMaxSize()
    ) {
        Spacer(Modifier.size(48.dp))
        TitileWithBackButtonDeterminer(stringResource(R.string.determiner), navController, vm)

        if (!isComplete.value) {
            BodyQuiz(determiner.value, determinerImaged.value, vm)
        } else {
            BodyResult(result.value, vm, isComplete)
        }

    }
    Box(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        if (!isComplete.value) {
            ContinueButton(determiner.value, determinerImaged.value, vm, isComplete)
        } else {
            if (!result.value.isNullOrEmpty()) {
                YesOrNotButtons(result.value, vm, isComplete, navController)
            }
        }
    }

}

@Composable
fun TitileWithBackButtonDeterminer(title: String, navController: NavController, vm: DeterminerViewModel) {
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = Typography.titleLarge,
            modifier = Modifier.padding(start = 16.dp)
        )
        BackButtonPrimaryDeterminer(
            modifier = Modifier.padding(end = 16.dp),
            navController = navController,
            vm
        )
    }
}

@Composable
fun BackButtonPrimaryDeterminer(modifier: Modifier = Modifier, navController: NavController, vm: DeterminerViewModel) {
    val context = LocalContext.current
    val result = vm.resultHerbs.collectAsState()
    Icon(
        rememberAsyncImagePainter(R.drawable.ico_back, context.imageLoader()),
        contentDescription = "back",
        tint = gray,
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(primary.copy(alpha = 0.2f))
            .clickable {
                if (result.value.isNullOrEmpty()) {
                    navController.navigate(Routes.Main.route)
                } else {
                    vm.clearSelect()
                    navController.navigate(Routes.Determiner.route)
                }
            }
            .padding(8.dp)
    )
}

@Composable
fun BodyResult(
    result: MutableList<ResultHerb>,
    vm: DeterminerViewModel,
    isComplete: MutableState<Boolean>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp)
            .verticalScroll(
                rememberScrollState()
            )
    ) {
        Spacer(Modifier.size(16.dp))
        val context = LocalContext.current

        if (!result.isNullOrEmpty()) {
            val currentId = vm.currentHerbID.collectAsState()
            val currentHerb = result[currentId.value]

            Text(text = stringResource(R.string.find_herbs).replace("_", result.size.toString()), style = Typography.titleLarge, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.size(16.dp))

            Text(
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            fontStyle = Typography.titleLarge.fontStyle,
                            color = black,
                            fontSize = 20.sp
                        )
                    ) {
                        append(context.getString(R.string.determiner_find) + " ")
                    }
                    withStyle(
                        style = SpanStyle(
                            fontStyle = Typography.titleLarge.fontStyle,
                            color = black,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append(currentHerb.herb.name + " ?")
                    }
                },
            )
            Spacer(Modifier.size(16.dp))
            Image(
                rememberAsyncImagePainter(currentHerb.herb.imageURL.first(), context.imageLoader()),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
                    .clip(
                        RoundedCornerShape(20.dp)
                    )
            )
            Spacer(Modifier.size(16.dp))
            Text(text = stringResource(R.string.similar) + " ${currentHerb.similarPerc}%", style = Typography.headlineLarge, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
        } else {
            notFindHerb(vm, isComplete, context)
        }
    }
}

@Composable
fun BodyQuiz(
    determiner: List<Determiner>,
    determinerListImg: List<DeterminerImaged>,
    vm: DeterminerViewModel
) {
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
        items(determinerListImg) {
            ImagedQuiz(it, vm)
        }
        item {
            Spacer(Modifier.size(128.dp))
        }
    }
}

@Composable
fun ImagedQuiz(determinerImaged: DeterminerImaged, vm: DeterminerViewModel) {
    Spacer(Modifier.size(16.dp))
    Text(text = determinerImaged.title, style = Typography.headlineLarge)
    Spacer(Modifier.size(16.dp))
    Row (modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        determinerImaged.variants.take(2).forEachIndexed() { index, it ->
            QuizVariantImaged(
                modifier = Modifier.weight(1f),
                variant = it,
                isChoose = (it == determinerImaged.selectVariant),
                determinerImaged,
                determinerImaged.images.take(2)[index],
                vm
            )
        }
    }
    Spacer(Modifier.size(16.dp))
    Row (modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        determinerImaged.variants.takeLast(2).forEachIndexed() { index, it ->
            QuizVariantImaged(
                modifier = Modifier.weight(1f),
                variant = it,
                isChoose = (it == determinerImaged.selectVariant),
                determinerImaged,
                determinerImaged.images.takeLast(2)[index],
                vm
            )
        }
    }

    Spacer(Modifier.size(16.dp))
    var choosed by rememberSaveable { mutableStateOf(determinerImaged.selectVariant == "") }
    SideEffect() {
        choosed = determinerImaged.selectVariant == ""
    }
    val buttonColor by animateColorAsState(if (choosed) primary else border)
    val textColor by animateColorAsState(if (choosed) white else black)
    Text(text = stringResource(R.string.idk), style = Typography.labelLarge.copy(color = textColor), modifier = Modifier.fillMaxWidth().clip(
        CircleShape).background(buttonColor).clickable {
        vm.choose(determinerImaged, variant = if (!choosed) "" else null)
    }.padding(horizontal = 12.dp, vertical = 10.dp))

}

@Composable
fun QuizVariantImaged(
    modifier: Modifier,
    variant: String,
    isChoose: Boolean,
    determinerImaged: DeterminerImaged,
    image: Int,
    vm: DeterminerViewModel
) {
    var choosed by rememberSaveable { mutableStateOf(isChoose) }
    SideEffect() {
        choosed = isChoose
    }
    val textColor by animateColorAsState(if (choosed) primary else gray)

    Column(modifier = modifier
        .fillMaxWidth()
        .clickable {
            vm.choose(determiner = determinerImaged, variant = if (!choosed) variant else null)
        }
        .padding(horizontal = 12.dp, vertical = 10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = painterResource(image),
            contentDescription = null,
            contentScale = ContentScale.Fit
        )
        Spacer(Modifier.size(8.dp))
        Text(
            text = variant,
            style = Typography.bodyLarge.copy(textAlign = TextAlign.Center),
            color = textColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
fun ContinueButton(
    determinerList: List<Determiner>,
    determinerListImg: List<DeterminerImaged>,
    vm: DeterminerViewModel,
    isComplete: MutableState<Boolean>
) {
    val context = LocalContext.current
    PrimaryButton(text = stringResource(R.string.continuee)) {
        if (determinerList.filter { it.selectVariant != null } == determinerList && determinerListImg.filter { it.selectVariant != null } == determinerListImg) {
            if (determinerList.filter { it.selectVariant!! != "" }.isEmpty() && determinerListImg.filter { it.selectVariant!! != "" }.isEmpty()) {
                context.CustomToast(text = context.getString(R.string.no_choose_any))
            } else {
                vm.getHerbByParams(context.applicationContext)
                isComplete.value = true
            }
        } else {
            context.CustomToast(text = context.getString(R.string.determiner_choose_all))
        }
    }
}

@Composable
fun YesOrNotButtons(
    herbsEntity: List<ResultHerb>,
    vm: DeterminerViewModel,
    isComplete: MutableState<Boolean>,
    navController: NavController
) {
    val currentId = vm.currentHerbID.collectAsState()
    val context = LocalContext.current
    Row(
        Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {

        PrimaryButton(modifier = Modifier.weight(1f), text = stringResource(R.string.yes)) {
            navController.navigate(Routes.FlowerInfo.route + "/${herbsEntity[currentId.value].herb.id}")
            vm.clearSelect()
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
        vm.resultHerbs.value = mutableListOf()
        vm.currentHerbID.value = 0
//        vm.clearSelect()
    }
}

@Composable
fun QuizField(determiner: Determiner, vm: DeterminerViewModel) {
    Spacer(Modifier.size(16.dp))
    Text(text = determiner.title, style = Typography.headlineLarge)
    Spacer(Modifier.size(16.dp))
    LazyRow(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        items(determiner.variants) {
            QuizVariant(
                variant = it,
                isChoose = (it == determiner.selectVariant) || (determiner.selectVariant == "" && it == determiner.variants.last()),
                determiner = determiner,
                vm
            )
        }
    }
}

@Composable
fun QuizVariant(
    variant: String,
    isChoose: Boolean,
    determiner: Determiner,
    vm: DeterminerViewModel
) {
    val context = LocalContext.current
    var choosed by rememberSaveable { mutableStateOf(isChoose) }
    SideEffect() {
        choosed = isChoose
    }
    val borderColor by animateColorAsState(if (choosed) primary else black)

    Text(
        text = variant,
        style = Typography.bodyLarge.copy(textAlign = TextAlign.Center),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier.defaultMinSize(minWidth = 100.dp)
            .padding(8.dp)
            .clickable {
                vm.choose(determiner = determiner, variant = if (!choosed) if (variant == context.getString(R.string.idk)) "" else  variant else null)
            }
            .border(1.dp, borderColor, CircleShape)
            .padding(horizontal = 12.dp, vertical = 10.dp)
    )
}
