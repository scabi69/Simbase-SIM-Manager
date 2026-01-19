package com.xabi.simbase.ui.components

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*

@Composable
fun AutoSizeText(
    text: String,
    modifier: Modifier = Modifier,
    maxFontSize: TextUnit = 16.sp,
    minFontSize: TextUnit = 10.sp,
    style: TextStyle = TextStyle()
) {
    BoxWithConstraints(modifier = modifier) {

        val scale = remember(maxWidth, text) {
            val maxChars = (maxWidth.value / 8).toInt()
            val shrinkFactor = when {
                text.length <= maxChars -> 1f
                else -> maxChars.toFloat() / text.length.toFloat()
            }
            shrinkFactor.coerceIn(
                minFontSize.value / maxFontSize.value,
                1f
            )
        }

        Text(
            text = text,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = style.copy(
                fontSize = maxFontSize * scale
            )
        )
    }
}