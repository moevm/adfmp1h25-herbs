package com.herbsapp.presentation.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.herbsapp.R
import com.herbsapp.data.room.entity.ElementEntity
import com.herbsapp.data.room.entity.HerbEntity
import com.herbsapp.presentation.ui.PrimaryButton
import com.herbsapp.presentation.ui.Routes
import com.herbsapp.presentation.ui.Sign
import com.herbsapp.presentation.ui.imageLoader
import com.herbsapp.presentation.ui.theme.Typography
import com.herbsapp.presentation.ui.theme.black
import com.herbsapp.presentation.ui.theme.border
import com.herbsapp.presentation.ui.theme.button
import com.herbsapp.presentation.ui.theme.gray
import com.herbsapp.presentation.ui.theme.primary
import com.herbsapp.presentation.ui.theme.white
import com.herbsapp.presentation.viewmodels.AuthViewModel
import com.herbsapp.presentation.viewmodels.MainViewModel
import org.koin.androidx.compose.koinViewModel

@Preview(showBackground = true, backgroundColor = 0xFFFAF9F9)
@Composable
fun MainScreen(navController: NavController = rememberNavController()) {
    val vm = koinViewModel<MainViewModel>()
    val vmAuth = koinViewModel<AuthViewModel>()
    val list = vm.herbsList.collectAsState()

    LazyColumn(
        Modifier
            .fillMaxSize()
            .nestedScroll(rememberNestedScrollInteropConnection())
    ) {
        item {
            Spacer(Modifier.size(16.dp))
            MainTitle(navController, vmAuth, vm)

            MainSearch(vm)

            SignsChooseList(vm)

            Spacer(Modifier.size(16.dp))
            Filter(vm)

            Spacer(Modifier.size(16.dp))
            SearchResult(navController, vm)

            Text(
                text = stringResource(R.string.watch_more),
                modifier = Modifier.padding(16.dp),
                style = Typography.titleLarge
            )
        }

        if (!list.value.isNullOrEmpty()) {
            items(list.value) {
                HerbHorizontalCard(it, navController)
                androidx.compose.material.Divider(
                    color = border,
                    thickness = 1.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            }
        }
    }

}

@Composable
fun Filter(vm: MainViewModel) {
    val expanded = remember { mutableStateOf(false) }
    val context = LocalContext.current

    Row(
        Modifier
            .padding(horizontal = 16.dp)
            .clickable { expanded.value = !expanded.value },
        verticalAlignment = Alignment.CenterVertically
    ) {
        val title = remember { mutableStateOf(context.getString(R.string.sort))}
        Image(
            rememberAsyncImagePainter(R.drawable.ico_filter, context.imageLoader()),
            contentScale = ContentScale.Fit,
            modifier = Modifier.size(20.dp),
            contentDescription = null
        )
        Spacer(Modifier.size(4.dp))
        Text(text = title.value, style = Typography.titleSmall)
        DropdownMenu(expanded = expanded.value,
            containerColor = white,
            shape = RoundedCornerShape(20.dp),
            onDismissRequest = { expanded.value = false }) {
            DropdownMenuItem(text = {
                Text(
                    stringResource(R.string.sort_default),
                    style = Typography.bodyMedium
                )
            }, onClick = {
                expanded.value = false
                title.value = context.getString(R.string.sort)
                vm.sort(context.getString(R.string.sort_default))
            })
            DropdownMenuItem(text = {
                Text(
                    stringResource(R.string.sort_name),
                    style = Typography.bodyMedium
                )
            }, onClick = {
                expanded.value = false
                title.value = "${context.getString(R.string.sort)} ${context.getString(R.string.sort_name).lowercase()}"
                vm.sort(context.getString(R.string.sort_name))
            })
            DropdownMenuItem(text = {
                Text(
                    stringResource(R.string.sort_views),
                    style = Typography.bodyMedium
                )
            }, onClick = {
                expanded.value = false
                title.value = "${context.getString(R.string.sort)} ${context.getString(R.string.sort_views).lowercase()}"
                vm.sort(context.getString(R.string.sort_views))
            })
            DropdownMenuItem(text = {
                Text(
                    stringResource(R.string.sort_rating),
                    style = Typography.bodyMedium
                )
            }, onClick = {
                expanded.value = false
                title.value = "${context.getString(R.string.sort)} ${context.getString(R.string.sort_rating).lowercase()}"
                vm.sort(context.getString(R.string.sort_rating))
            })
        }
    }


}

@Composable
fun HerbHorizontalCard(herbEntity: HerbEntity, navController: NavController) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navController.navigate(Routes.FlowerInfo.route + "/${herbEntity.id}")
            }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                rememberAsyncImagePainter(herbEntity.imageURL.first(), context.imageLoader()),
                modifier = Modifier
                    .size(70.dp)
                    .clip(
                        RoundedCornerShape(16.dp)
                    ),
                contentScale = ContentScale.Crop,
                contentDescription = null
            )
            Column(
                Modifier
                    .height(60.dp)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = herbEntity.name,
                    style = Typography.titleSmall,
                    softWrap = false,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.size(2.dp))
                UserLocation(Typography.headlineSmall, 15.dp)
                Spacer(Modifier.size(2.dp))
                RatingViews(
                    herbEntity.rating.toString(),
                    views = herbEntity.views,
                    15.dp,
                    Typography.headlineSmall
                )
            }
        }
        Icon(
            rememberAsyncImagePainter(R.drawable.ico_back, context.imageLoader()),
            tint = primary,
            contentDescription = null,
            modifier = Modifier
                .size(width = 28.dp, height = 60.dp)
                .rotate(180f)
                .border(1.dp, border, CircleShape)
                .padding(horizontal = 4.dp)
        )
    }

}

@Composable
fun SearchResult(navController: NavController, viewModel: MainViewModel) {
    val list = viewModel.searchList.collectAsState()
    if (!list.value.isNullOrEmpty()) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 14.dp)
        ) {
            items(list.value) {
                HerbCard(it, navController, viewModel)
            }
        }
    } else {
        Text(
            text = stringResource(R.string.nothing_find),
            style = Typography.titleLarge.copy(textAlign = TextAlign.Center),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }

}

@Composable
fun HerbCard(
    herbEntity: HerbEntity,
    navController: NavController,
    vm: MainViewModel,
    isLikable: Boolean = true
) {
    var isLiked by remember { mutableStateOf(herbEntity.isLiked) }

    val context = LocalContext.current
    val likedColor by animateColorAsState(if (isLiked) primary else gray)
    Box(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .size(height = 200.dp, width = 150.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable { navController.navigate(Routes.FlowerInfo.route + "/${herbEntity.id}") }) {
        Image(
            rememberAsyncImagePainter(herbEntity.imageURL.first(), context.imageLoader()),
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            contentDescription = null
        )
        if (isLikable) {
            Icon(
                rememberAsyncImagePainter(R.drawable.ico_like),
                tint = likedColor,
                contentDescription = "like",
                modifier = Modifier
                    .padding(8.dp)
                    .clickable {
                        isLiked = !isLiked
                        vm.updateHerb(herbEntity.copy(isLiked = isLiked))
                    }
                    .size(24.dp)
            )
        }

        Column(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(4.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    white
                )
                .padding(8.dp)
        ) {
            Text(
                text = herbEntity.name,
                style = Typography.titleSmall,
                softWrap = false,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.size(2.dp))
            UserLocation(Typography.headlineSmall, 15.dp)
            Spacer(Modifier.size(2.dp))
            RatingViews(
                herbEntity.rating.toString(),
                views = herbEntity.views,
                15.dp,
                Typography.headlineSmall
            )
        }
    }
}

@Composable
fun RatingViews(rating: String, views: Int, iconSize: Dp, textStyle: TextStyle) {
    Row(modifier = Modifier.wrapContentHeight(), verticalAlignment = Alignment.CenterVertically) {
        Image(
            rememberAsyncImagePainter(R.drawable.ico_star),
            contentDescription = null,
            modifier = Modifier.size(iconSize)
        )
        Spacer(Modifier.size(2.dp))
        Text(rating, style = textStyle.copy(color = black))
        Text(
            " | " + views.toString() + " " + stringResource(R.string.views),
            style = textStyle,
            softWrap = false,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun SignsChooseList(vm: MainViewModel) {
    val signsList by vm.signsList.collectAsState()
    val elements by vm.elementsList.collectAsState()
    val expandedAnimation by animateDpAsState(if (!signsList.first().isChoose) 270.dp else 0.dp)

    Text(
        text = stringResource(R.string.sign_choose_title),
        style = Typography.titleLarge,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    )
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 14.dp)
    ) {
        items(signsList) {
            SignItem(it, vm)
        }
    }

    if (!signsList.first().isChoose) {
        Spacer(Modifier.size(16.dp))
        LazyHorizontalGrid(
            modifier = Modifier
                .fillMaxWidth()
                .height(expandedAnimation)
                .nestedScroll(rememberNestedScrollInteropConnection()),
            rows = GridCells.Fixed(5),
        ) {
            items(elements) {
                ElementItem(it, vm)
            }
        }
        Spacer(Modifier.size(16.dp))

        PrimaryButton(
            text = stringResource(R.string.search),
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            vm.search()
        }
    }

}

@Composable
fun ElementItem(element: ElementEntity, vm: MainViewModel) {
    val borderColor by animateColorAsState(if (element.isChoose) primary else black)
    Text(
        text = element.title,
        maxLines = 1,
        softWrap = false,
        overflow = TextOverflow.Ellipsis,
        style = Typography.bodyMedium,
        modifier = Modifier
            .width(180.dp)
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .border(1.5.dp, borderColor, CircleShape)
            .clickable {
                vm.chooseElement(element.copy(isChoose = !element.isChoose))
            }
            .padding(12.dp),
        textAlign = TextAlign.Center
    )
}

@Composable
fun SignItem(item: Sign, vm: MainViewModel) {
    val context = LocalContext.current
    val backgroundColor by animateColorAsState(if (item.isChoose) primary else white)
    val borderColor by animateColorAsState(
        if (item.selectedElement != null && !item.isChoose) primary else if (item.isChoose) Color.Transparent else border
    )
    val itemColor by animateColorAsState(if (item.isChoose) white else if (item.selectedElement != null) primary else gray)
    Row(
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .border(1.dp, borderColor, CircleShape)
            .clickable {
                vm.chooseSign(item)
                if (item.title == context.getString(R.string.sign_all)) {
                    vm.search()
                }
            }
            .padding(vertical = 10.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            rememberAsyncImagePainter(item.icon, context.imageLoader()),
            contentDescription = null,
            tint = itemColor,
            modifier = Modifier.size(16.dp)
        )
        Spacer(Modifier.size(6.dp))
        Text(
            text = if (item.selectedElement == null) item.title else item.selectedElement!!.title,
            style = Typography.bodyLarge,
            color = itemColor
        )
    }
}

@Composable
fun MainSearch(vm: MainViewModel) {
    val searchVM = vm.searchEntry.collectAsState()

    var searchInput by remember { mutableStateOf(searchVM.value) }
    val context = LocalContext.current
    val localFocusManager = LocalFocusManager.current
    if (searchVM.value.isEmpty()) {
        searchInput = ""
    }

    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .shadow(10.dp, shape = CircleShape),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        value = searchInput,
        onValueChange = {
            searchInput = it
            vm.searchEntry.value = it
            vm.search()
        },
        shape = CircleShape,
        textStyle = Typography.labelMedium,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedTextColor = gray,
            unfocusedTextColor = gray,
            focusedPlaceholderColor = gray,
            unfocusedPlaceholderColor = gray,
            unfocusedContainerColor = Color.White,
            focusedContainerColor = Color.White,
        ),
        singleLine = true,
        leadingIcon = {
            Image(
                rememberAsyncImagePainter(
                    R.drawable.ico_search,
                    context.imageLoader()
                ), contentDescription = null, modifier = Modifier.size(20.dp)
            )
        },
        trailingIcon = {
            if (searchInput.isNotEmpty()) {
                Image(
                    rememberAsyncImagePainter(R.drawable.ico_close, context.imageLoader()),
                    contentDescription = null,
                    modifier = Modifier
                        .size(25.dp)
                        .clickable {
                            searchInput = ""
                            localFocusManager.clearFocus()
                            vm.searchEntry.value = ""
                        }
                )
            }
        },
        placeholder = {
            Text(stringResource(R.string.search_placeholder), style = Typography.labelMedium)
        }
    )
}

@Composable
fun MainTitle(navController: NavController, viewModel: AuthViewModel, mainVM: MainViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 32.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column() {
            Text(
                text = stringResource(R.string.hello) + " ${viewModel.currentUser?.displayName ?: "Загрузка..."}!",
                style = Typography.titleLarge
            )
            Spacer(Modifier.size(8.dp))
            UserLocation(style = Typography.headlineMedium, 20.dp)
        }
        val searchList = mainVM.searchList.collectAsState()
        if (!searchList.value.isNullOrEmpty()) {
            AccountImage {
                navController.navigate(Routes.Account.route)
            }
        } else {
            val context = LocalContext.current
            Image(
                rememberAsyncImagePainter(R.drawable.ico_back, context.imageLoader()),
                contentDescription = "back",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(button)
                    .clickable {
                        mainVM.clearSearch()
                    }
                    .padding(10.dp)
            )
        }
    }
}

@Composable
fun AccountImage(onClick: () -> Unit) {
    Image(
        painter = painterResource(R.drawable.img_user_photo),
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .size(60.dp)
            .clickable { onClick() },
        contentDescription = null
    )
}

@Composable
fun UserLocation(style: TextStyle, iconSize: Dp) {
    val context = LocalContext.current
    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            rememberAsyncImagePainter(
                if (style.color == primary) R.drawable.ico_location else R.drawable.ico_location_gray,
                context.imageLoader()
            ),
            modifier = Modifier.size(iconSize),
            contentDescription = null,
            contentScale = ContentScale.Fit
        )
        Spacer(Modifier.size(4.dp))
        Text(text = stringResource(R.string.spb), style = style)
    }

}