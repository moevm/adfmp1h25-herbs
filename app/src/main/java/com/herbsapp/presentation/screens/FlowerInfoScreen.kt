package com.herbsapp.presentation.screens

import android.content.Context
import android.content.Intent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import com.herbsapp.R
import com.herbsapp.data.room.entity.HerbEntity
import com.herbsapp.presentation.ui.PrimaryButtonWIcon
import com.herbsapp.presentation.ui.Routes
import com.herbsapp.presentation.ui.imageLoader
import com.herbsapp.presentation.ui.theme.Typography
import com.herbsapp.presentation.ui.theme.black
import com.herbsapp.presentation.ui.theme.button
import com.herbsapp.presentation.ui.theme.gray
import com.herbsapp.presentation.ui.theme.primary
import com.herbsapp.presentation.ui.theme.white
import com.herbsapp.presentation.viewmodels.FlowerInfoViewModel
import com.herbsapp.presentation.viewmodels.MainViewModel
import org.koin.androidx.compose.koinViewModel

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun FlowerInfoScreen(navController: NavController = rememberNavController(), id: Int = 1) {
    val vm = koinViewModel<FlowerInfoViewModel>()
    vm.getHerbById(id)
    val herb by vm.herb.collectAsState()
    val context = LocalContext.current

    if (herb != null) {
        Column(Modifier.fillMaxSize()) {
            TitleInfo(herb = herb!!, navController = navController, vm)
            Spacer(Modifier.size(16.dp))
            SubtitleInfo(herb!!)
            Spacer(Modifier.size(16.dp))
            BodyInfo(herb!!)
        }
        Box(
            modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 16.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            PrimaryButtonWIcon(
                text = stringResource(R.string.share),
                icon = rememberAsyncImagePainter(R.drawable.ico_share, context.imageLoader())
            ) {
                Share(context = context, herb = herb!!)
            }
        }
    }
}

fun Share(context: Context, herb: HerbEntity) {
    val type = "text/plain"
    val subject = herb.name
    val title = context.getString(R.string.app_name)
    val extraText = """
${herb.name}
${herb.title}

${context.getText(R.string.google_play_link)}
    """.trimIndent()
    val shareWith = "ShareWith"

    val intent = Intent(Intent.ACTION_SEND)
    intent.type = type
    intent.putExtra(Intent.EXTRA_TITLE, title)
    intent.putExtra(Intent.EXTRA_SUBJECT, subject)
    intent.putExtra(Intent.EXTRA_TEXT, extraText)

    ContextCompat.startActivity(
        context,
        Intent.createChooser(intent, shareWith),
        null
    )
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ImagePager(herb: HerbEntity) {
    val context = LocalContext.current
    val state = rememberPagerState()
    val imageUrl = remember { mutableStateOf("") }

    Box(Modifier.fillMaxWidth()
        .height(300.dp).clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))){
        HorizontalPager(
            state = state,
            count = herb.imageURL.size
        ) {
            imageUrl.value = herb.imageURL[it]
            Image(
                painter = rememberAsyncImagePainter(
                    imageUrl.value,
                    imageLoader = context.imageLoader()
                ),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
        }
        PagerIndication(
            modifier = Modifier
                .fillMaxWidth().wrapContentHeight()
                .align(Alignment.BottomCenter),
            dotsCount = state.pageCount,
            selectedIndex = state.currentPage
        )

    }
}

@Composable
fun PagerIndication(modifier: Modifier = Modifier, dotsCount: Int, selectedIndex: Int) {
    Row (
        modifier = modifier.padding(bottom = 48.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(dotsCount) { index ->
            val dotSize by animateDpAsState(if (index == selectedIndex) 14.dp else 8.dp)
            val dotColor by animateColorAsState(
                if (index == selectedIndex) white else white.copy(
                    alpha = 0.7f
                )
            )
            Box(
                modifier = Modifier
                    .size(dotSize)
                    .clip(CircleShape)
                    .shadow(4.dp, clip = true)
                    .background(dotColor)
            )

            if (index != dotsCount - 1) {
                Spacer(modifier = Modifier.padding(horizontal = 2.dp))
            }
        }
    }
}

@Composable
fun SubtitleInfo(herb: HerbEntity) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 24.dp), verticalAlignment = Alignment.CenterVertically) {
        UserLocation(style = Typography.headlineLarge.copy(color = primary), iconSize = 20.dp)
        RatingViews(rating = herb.rating.toString(), views = herb.views, iconSize = 20.dp, textStyle = Typography.headlineLarge.copy(fontSize = 20.sp, color = gray))
    }
}

@Composable
fun BodyInfo(herb: HerbEntity) {
    Column(Modifier.padding(horizontal = 24.dp).verticalScroll(rememberScrollState())) {
        Text(text = herb.name, style = Typography.displayLarge)
        Text(modifier = Modifier, text = herb.title, style = Typography.bodyLarge)
        Spacer(Modifier.size(8.dp))
        Text(modifier = Modifier.padding(start = 8.dp), text = herb.description, style = Typography.bodyLarge, color = black, maxLines = 8, overflow = TextOverflow.Ellipsis)

        Spacer(Modifier.size(8.dp))
        Text(modifier = Modifier.padding(start = 8.dp), text = stringResource(R.string.sign_family) + ": " + herb.family, style = Typography.titleSmall.copy(fontSize = 18.sp))
        Text(modifier = Modifier.padding(start = 8.dp), text = stringResource(R.string.sign_taste) + ": " + herb.taste, style = Typography.titleSmall.copy(fontSize = 18.sp))
        Text(modifier = Modifier.padding(start = 8.dp), text = stringResource(R.string.sign_class) + ": " + herb.mClass, style = Typography.titleSmall.copy(fontSize = 18.sp))
        Text(modifier = Modifier.padding(start = 8.dp), text = stringResource(R.string.sign_genus) + ": " + herb.genus, style = Typography.titleSmall.copy(fontSize = 18.sp))

        Spacer(Modifier.size(90.dp))
    }
}

@Composable
fun TitleInfo(herb: HerbEntity, navController: NavController, vm: FlowerInfoViewModel) {
    val context = LocalContext.current

    Box {
        ImagePager(herb)

        BackButton(
            modifier = Modifier.padding(top = 48.dp, start = 16.dp),
            navController = navController
        )
        AboutButton(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 48.dp, end = 16.dp),
            navController = navController,
            id = herb.id
        )
        BigLikeButton(herb, Modifier.align(Alignment.BottomEnd).offset(y = 16.dp), context, vm)
    }
}

@Composable
fun BigLikeButton(herb: HerbEntity, modifier: Modifier, context: Context, vm: FlowerInfoViewModel) {
    var isLikedState by remember { mutableStateOf(herb.isLiked) }
    val likedColor by animateColorAsState(if (isLikedState) primary else gray)
    Box(
        modifier = modifier
            .padding(end = 16.dp, top = 16.dp)
            .size(60.dp)
            .shadow(10.dp, shape = CircleShape)
            .clip(
                CircleShape
            )
            .clickable {
                isLikedState = !isLikedState
                vm.updateHerb(herb.copy(isLiked = isLikedState))
            }
            .background(white)
            .padding(16.dp), contentAlignment = Alignment.Center
    ) {
        Icon(
            rememberAsyncImagePainter(R.drawable.ico_like, context.imageLoader()),
            contentDescription = "like",
            tint = likedColor,
        )
    }
}

@Composable
fun BackButton(modifier: Modifier = Modifier, navController: NavController) {
    val context = LocalContext.current
    Icon(
        rememberAsyncImagePainter(R.drawable.ico_back, context.imageLoader()),
        contentDescription = "back",
        tint = white,
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(button)
            .clickable { navController.popBackStack() }
            .padding(8.dp)
    )
}

@Composable
fun BackButtonPrimary(modifier: Modifier = Modifier, navController: NavController) {
    val context = LocalContext.current
    Icon(
        rememberAsyncImagePainter(R.drawable.ico_back, context.imageLoader()),
        contentDescription = "back",
        tint = gray,
        modifier = modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(primary.copy(alpha = 0.2f))
            .clickable { navController.popBackStack() }
            .padding(8.dp)
    )
}

@Composable
fun AboutButton(modifier: Modifier = Modifier, navController: NavController, id: Int) {
    Box(
        modifier = modifier
            .size(60.dp)
            .clip(CircleShape)
            .background(button.copy(alpha = 0.9f))
            .clickable { navController.navigate(Routes.Description.route + "/${id}") }
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.more),
            style = Typography.titleSmall,
            textAlign = TextAlign.Center,
        )

    }
}