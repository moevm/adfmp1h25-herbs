package com.herbsapp.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.herbsapp.R
import com.herbsapp.data.room.entity.HerbEntity
import com.herbsapp.presentation.ui.Routes
import com.herbsapp.presentation.ui.imageLoader
import com.herbsapp.presentation.ui.theme.Typography
import com.herbsapp.presentation.viewmodels.MainViewModel
import org.koin.androidx.compose.koinViewModel

@Preview(showBackground = true)
@Composable
fun FavouritesScreen(navController: NavController = rememberNavController()) {
    val vm = koinViewModel<MainViewModel>()
    val likedHerbs = vm.herbsList.collectAsState()

    Column(Modifier.fillMaxWidth()) {
        Spacer(Modifier.size(48.dp))

        TitileWithBackButton(title = stringResource(R.string.favourites), navController)
        Spacer(Modifier.size(16.dp))

        if (!likedHerbs.value.isNullOrEmpty()) {
            if (!likedHerbs.value.filter { it.isLiked }.isNullOrEmpty()) {
                BodyFavourites(likedHerbs.value, navController, vm)
            } else {
                EmptyFavourites()
            }
        }
    }
}

@Composable
fun BodyFavourites(herbs: List<HerbEntity>, navController: NavController, vm: MainViewModel) {
    LazyVerticalGrid(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(rememberNestedScrollInteropConnection()),
        columns = GridCells.Fixed(2)
    ) {
        items(herbs.filter { it.isLiked }) {
            CardHerbFav(it, navController, vm)
        }

    }
}

@Composable
fun EmptyFavourites() {
    Box(Modifier.fillMaxSize()) {
        Text(
            text = stringResource(R.string.nothing_find),
            style = Typography.displaySmall.copy(textAlign = TextAlign.Center),
            modifier = Modifier.align(
                Alignment.Center
            )
        )
    }
}

@Composable
fun CardHerbFav(herbEntity: HerbEntity, navController: NavController, vm: MainViewModel) {
    Box (Modifier.wrapContentWidth().padding(16.dp)) {
        HerbCard(herbEntity, navController, vm, isLikable = false)
        CloseButton(Modifier.align(Alignment.TopEnd), herbEntity, vm)
    }
}

@Composable
fun CloseButton(modifier: Modifier, herbEntity: HerbEntity, vm: MainViewModel) {
    val context = LocalContext.current
    Image(
        rememberAsyncImagePainter(R.drawable.ico_close, context.imageLoader()),
        contentDescription = "close",
        modifier = modifier
            .size(30.dp)
            .offset(x = 4.dp, y = (-8).dp).clickable {
                vm.updateHerb(herbEntity = herbEntity.copy(isLiked = false))
            }
    )
}