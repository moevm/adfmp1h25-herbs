package com.herbsapp.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.herbsapp.R
import com.herbsapp.data.room.entity.HerbEntity
import com.herbsapp.presentation.ui.Routes
import com.herbsapp.presentation.ui.imageLoader
import com.herbsapp.presentation.ui.theme.Typography
import com.herbsapp.presentation.ui.theme.gray
import com.herbsapp.presentation.ui.theme.primary
import com.herbsapp.presentation.viewmodels.FlowerInfoViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun DescriptionScreen(navController: NavController, id: Int) {
    val vm = koinViewModel<FlowerInfoViewModel>()
    vm.getHerbById(id)
    val herb by vm.herb.collectAsState()

    if (herb != null) {
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
            Spacer(Modifier.size(48.dp))
            
            TitileWithBackButton(herb!!.name, navController)
            Spacer(Modifier.size(16.dp))
            Text(
                text = stringResource(R.string.description),
                style = Typography.titleSmall.copy(fontSize = 16.sp, lineHeight = 22.sp),
                modifier = Modifier.padding(horizontal = 32.dp)
            )
            Text(
                text = herb!!.description,
                style = Typography.bodyMedium.copy(fontSize = 16.sp, lineHeight = 22.sp),
                modifier = Modifier.padding(horizontal = 32.dp)
            )

        }
    }


}

@Composable
fun TitileWithBackButton(title: String, navController: NavController) {
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
        BackButtonPrimary(
            modifier = Modifier.padding(end = 16.dp),
            navController = navController
        )
    }
}
