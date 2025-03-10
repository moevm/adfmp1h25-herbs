package com.herbsapp.presentation

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.herbsapp.R
import com.herbsapp.presentation.ui.BottomNavBar
import com.herbsapp.presentation.ui.Navigation
import com.herbsapp.presentation.ui.Routes
import com.herbsapp.presentation.ui.removeAllAfterSlash
import com.herbsapp.presentation.ui.theme.FontMedium
import com.herbsapp.presentation.ui.theme.HerbsAppTheme
import com.herbsapp.presentation.ui.theme.Typography
import com.herbsapp.presentation.ui.theme.black
import com.herbsapp.presentation.ui.theme.white
import es.dmoral.toasty.Toasty
import org.koin.compose.KoinContext

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(SystemBarStyle.light(black.toArgb(), black.toArgb()))
        setContent {
            ToastyConfig()

            val navHostController = rememberNavController()
            val backStackEntry by navHostController.currentBackStackEntryAsState()
            val currentRoute = backStackEntry?.destination?.route
            val screensWithoutBottomBar = listOf(Routes.Launch.route, Routes.Login.route, Routes.Register.route, Routes.Description.route, Routes.FlowerInfo.route)

            KoinContext {
                HerbsAppTheme {
                    Scaffold(modifier = Modifier.fillMaxSize(),
                        bottomBar = {
                            if (!screensWithoutBottomBar.contains(currentRoute.removeAllAfterSlash())) {
                                BottomNavBar(navHostController)
                            }
                        }) {
                        Box(Modifier.padding(bottom = it.calculateBottomPadding())) {
                            Navigation(navHostController)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ToastyConfig() {
    val context = LocalContext.current
    Toasty.Config.getInstance()
        .setToastTypeface(context.resources.getFont(R.font.dm_sans_medium))
        .setTextSize(14)
        .apply()
}
