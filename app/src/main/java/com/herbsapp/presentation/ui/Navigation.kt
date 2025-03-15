package com.herbsapp.presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.herbsapp.R
import com.herbsapp.presentation.screens.AccountScreen
import com.herbsapp.presentation.screens.DescriptionScreen
import com.herbsapp.presentation.screens.DeterminerScreen
import com.herbsapp.presentation.screens.FavouritesScreen
import com.herbsapp.presentation.screens.FlowerInfoScreen
import com.herbsapp.presentation.screens.LaunchScreen
import com.herbsapp.presentation.screens.LoginScreen
import com.herbsapp.presentation.screens.MainScreen
import com.herbsapp.presentation.screens.RegisterScreen
import com.herbsapp.presentation.ui.theme.gray
import com.herbsapp.presentation.ui.theme.primary
import com.herbsapp.presentation.ui.theme.white
import com.herbsapp.presentation.viewmodels.AuthViewModel
import com.herbsapp.presentation.viewmodels.DeterminerViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun Navigation(navHostController: NavHostController) {
    val authVm = koinViewModel<AuthViewModel>()
    val determinerVm = koinViewModel<DeterminerViewModel>()
    NavHost(navController = navHostController, startDestination = Routes.Launch.route) {
        composable(Routes.Launch.route) { LaunchScreen(navController = navHostController, authVm) }
        composable(Routes.Login.route) { LoginScreen(navController = navHostController, authVm) }
        composable(Routes.Register.route) { RegisterScreen(navController = navHostController, authVm) }
        composable(Routes.Main.route) { MainScreen(navController = navHostController, authVm) }
        composable(Routes.Favourites.route) { FavouritesScreen(navController = navHostController, authVm) }
        composable(Routes.Determiner.route) { DeterminerScreen(navController = navHostController, determinerVm) }
        composable(Routes.Account.route) { AccountScreen(navController = navHostController, authVm) }
        composable(Routes.FlowerInfo.route + "/{id}") { entry ->
            FlowerInfoScreen(
                navController = navHostController,
                id = entry.arguments!!.getString("id")!!.toInt(),
                authVm
            )
        }
        composable(Routes.Description.route + "/{id}") { entry ->
            DescriptionScreen(
                navController = navHostController,
                id = entry.arguments!!.getString("id")!!.toInt()
            )
        }
    }
}

sealed class Routes(val route: String) {
    data object Launch : Routes("launch")
    data object Login : Routes("login")
    data object Register : Routes("register")
    data object Main : Routes("main")
    data object Favourites : Routes("favourites")
    data object Determiner : Routes("determiner")
    data object Account : Routes("account")
    data object FlowerInfo : Routes("flower_info")
    data object Description : Routes("description")

}

@Preview
@Composable
fun BottomNavBar(navController: NavHostController = rememberNavController()) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    NavigationBar(modifier = Modifier.shadow(20.dp), containerColor = white) {
        NavBarItems.BarItems.forEach { navItem ->
            NavigationBarItem(
                colors = NavigationBarItemColors(
                    selectedIconColor = primary,
                    selectedTextColor = primary,
                    selectedIndicatorColor = Color.Transparent,
                    unselectedIconColor = gray,
                    unselectedTextColor = gray,
                    disabledIconColor = gray,
                    disabledTextColor = gray
                ),
                selected = currentRoute == navItem.route,
                onClick = {
                    if (navItem.route != currentRoute) {
                        navController.navigate(navItem.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(navItem.image),
                        contentDescription = null,
                    )
                },
                label = {
                    if (navItem.route == currentRoute) {
                        Box(modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(primary))
                    } else {
                        Box(modifier = Modifier.size(6.dp))
                    }
                }
            )
        }
    }


}

fun String?.removeAllAfterSlash() = this?.replaceAfterLast("/", "")?.removeSuffix("/")

object NavBarItems {
    val BarItems = listOf(
        BottomBarItem(
            image = R.drawable.ico_home,
            route = Routes.Main.route
        ),
        BottomBarItem(
            image = R.drawable.ico_like_stroked,
            route = Routes.Favourites.route
        ),
        BottomBarItem(
            image = R.drawable.ico_play,
            route = Routes.Determiner.route
        ),
        BottomBarItem(
            image = R.drawable.ico_profile,
            route = Routes.Account.route
        )

    )
}

data class BottomBarItem(
    val image: Int,
    val route: String
)