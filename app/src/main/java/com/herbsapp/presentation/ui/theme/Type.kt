package com.herbsapp.presentation.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.herbsapp.R

val FontRegular = FontFamily(Font(R.font.dm_sans))
val FontMedium = FontFamily(Font(R.font.dm_sans_medium))
val FontBold = FontFamily(Font(R.font.dm_sans_bold))

// Set of Material typography styles to start with
val Typography = Typography(

    displayLarge = TextStyle( // flower about
        fontFamily = FontBold,
        fontSize = 32.sp,
        color = black,
    ),
    displayMedium = TextStyle( // first screen
        fontFamily = FontBold,
        fontSize = 30.sp,
        color = black,
    ),
    displaySmall = TextStyle( // flower about scrolled down
        fontSize = 25.sp,
        fontFamily = FontBold,
        color = black
    ),

    titleLarge = TextStyle(
        fontSize = 20.sp,
        fontFamily = FontBold,
        color = black,

    ),
    titleSmall = TextStyle( // title for card
        fontFamily = FontBold,
        fontSize = 14.sp,
        color = black
    ),

    headlineLarge = TextStyle(
        fontSize = 18.sp,
        fontFamily = FontMedium,
        color = black
    ),
    headlineMedium = TextStyle( // location on title
        fontFamily = FontMedium,
        fontSize = 14.sp,
        color = primary,
    ),
    headlineSmall = TextStyle( // location on card
        fontFamily = FontMedium,
        fontSize = 10.sp,
        color = gray
    ),

    bodyLarge = TextStyle(
        fontFamily = FontMedium,
        fontSize = 14.sp,
        color = gray,
    ),
    bodyMedium = TextStyle(
        fontFamily = FontRegular,
        fontSize = 14.sp,
        color = black
    ),
    bodySmall = TextStyle( // views
        fontFamily = FontMedium,
        fontSize = 10.sp,
        color = gray,
    ),

    labelLarge = TextStyle( // button
        fontSize = 16.sp,
        fontFamily = FontBold,
        color = white,
        textAlign = TextAlign.Center
    ),
    labelMedium = TextStyle( // text for text field
        fontFamily = FontMedium,
        fontSize = 16.sp,
        color = gray,
    ),
    labelSmall = TextStyle( // label for text field
        fontFamily = FontMedium,
        fontSize = 12.sp,
        color = gray,
    ),

)