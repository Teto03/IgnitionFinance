package com.unimib.ignitionfinance.presentation.ui.components.intro

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.draw.clip
import com.unimib.ignitionfinance.R

@Composable
fun BackgroundImage() {
    Image(
        painter = painterResource(id = R.drawable.intro_screen_image),
        contentDescription = stringResource(id = R.string.intro_bg_description),
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)),
        contentScale = ContentScale.Crop
    )
}
