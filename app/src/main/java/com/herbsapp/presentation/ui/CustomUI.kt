package com.herbsapp.presentation.ui

import android.content.Context
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.herbsapp.R
import com.herbsapp.presentation.ui.theme.Typography
import com.herbsapp.presentation.ui.theme.black
import com.herbsapp.presentation.ui.theme.border
import com.herbsapp.presentation.ui.theme.gray
import com.herbsapp.presentation.ui.theme.primary
import com.herbsapp.presentation.ui.theme.white
import es.dmoral.toasty.Toasty

@Composable
fun PrimaryButton(modifier: Modifier = Modifier, text: String, onClick: () -> Unit) {
    Text(
        modifier = modifier
            .fillMaxWidth()
            .clip(CircleShape)
            .background(primary)
            .clickable { onClick() }
            .padding(vertical = 22.dp),
        text = text,
        style = Typography.labelLarge.copy(color = white)
    )
}

@Composable
fun PrimaryButtonGray(modifier: Modifier = Modifier, text: String, onClick: () -> Unit) {
    Text(
        modifier = modifier
            .fillMaxWidth()
            .clip(CircleShape)
            .background(border)
            .clickable { onClick() }
            .padding(vertical = 22.dp),
        text = text,
        style = Typography.labelLarge.copy(color = black)
    )
}

@Composable
fun PrimaryButtonWIcon(modifier: Modifier = Modifier, text: String, icon: Painter, onClick: () -> Unit) {
    Row(
        modifier = modifier
            .fillMaxWidth()

            .shadow(10.dp, ambientColor = primary, spotColor = primary, shape = CircleShape)
            .clip(CircleShape)
            .background(primary)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Image(painter = icon, modifier = Modifier.size(30.dp), contentDescription = null, contentScale = ContentScale.Fit )
        Spacer(Modifier.size(16.dp))
        Text(
            modifier = Modifier.padding(vertical = 22.dp),
            textAlign = TextAlign.Center,
            text = text,
            style = Typography.labelLarge.copy(color = white)
        )
    }
}

@Composable
fun CustomTextField(
    modifier: Modifier = Modifier,
    label: String,
    value: MutableState<String>,
    isErrorExpression: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions(),
    visualTransformation: VisualTransformation = VisualTransformation.None,
) {
    val isFocused = value.value.isEmpty()

    val labelOffsetY by animateDpAsState(if (isFocused) (-40).dp else (-70).dp)
    val labelFontSize by animateFloatAsState(if (isFocused) 14.sp.value else 12.sp.value)
    val labelPadding by animateDpAsState(if (isFocused) 0.dp else 4.dp)

    val textFieldBorderColor by animateColorAsState(if (isErrorExpression) border else com.herbsapp.presentation.ui.theme.error)

    BasicTextField(
        modifier = modifier
            .fillMaxWidth(),
        singleLine = true,
        keyboardOptions = keyboardOptions,
        textStyle = Typography.labelMedium.copy(color = black),
        value = value.value,
        onValueChange = { value.value = it },
        visualTransformation = visualTransformation,
        decorationBox = { innerTextField ->
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(2.dp, textFieldBorderColor, CircleShape)
                        .padding(horizontal = 24.dp, vertical = 20.dp)
                ) {
                    innerTextField()
                }
                Text(
                    modifier = Modifier
                        .padding(start = 28.dp)
                        .offset(y = labelOffsetY)
                        .background(white)
                        .padding(horizontal = labelPadding),
                    text = label,
                    style = Typography.labelMedium,
                    fontSize = labelFontSize.sp
                )
            }
        },
    )
}

fun Context.CustomToast(text: String) = Toasty.custom(this, text, this.resources.getDrawable(R.drawable.ico_home), primary.toArgb(), white.toArgb(), Toasty.LENGTH_SHORT, false, true).show()