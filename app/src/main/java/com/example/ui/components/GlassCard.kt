package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassBorderBright
import com.example.ui.theme.GlassSurface
import com.example.ui.theme.GlassSurfaceElevated
import com.example.ui.theme.NeonCyanAccent
import com.example.ui.theme.NeonVioletPrimary

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(20.dp),
    backgroundColor: Color = GlassSurface,
    borderColor: Color = GlassBorder,
    borderWidth: Dp = 1.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val clickModifier = if (onClick != null) {
        Modifier.clickable(onClick = onClick)
    } else {
        Modifier
    }

    val gradientBorder = Brush.linearGradient(
        colors = listOf(
            borderColor,
            borderColor.copy(alpha = 0.15f),
            GlassBorderBright.copy(alpha = 0.4f)
        )
    )

    Surface(
        modifier = modifier
            .clip(shape)
            .then(clickModifier),
        shape = shape,
        color = backgroundColor,
        border = BorderStroke(borderWidth, gradientBorder),
        tonalElevation = 4.dp
    ) {
        Box(modifier = Modifier.padding(16.dp)) {
            content()
        }
    }
}

@Composable
fun NeonGradientCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(24.dp),
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val clickModifier = if (onClick != null) {
        Modifier.clickable(onClick = onClick)
    } else {
        Modifier
    }

    val cardBrush = Brush.linearGradient(
        colors = listOf(
            GlassSurfaceElevated,
            DarkSurface.copy(alpha = 0.9f)
        )
    )

    val borderBrush = Brush.linearGradient(
        colors = listOf(
            NeonVioletPrimary.copy(alpha = 0.7f),
            NeonCyanAccent.copy(alpha = 0.5f),
            GlassBorder
        )
    )

    Surface(
        modifier = modifier
            .clip(shape)
            .then(clickModifier),
        shape = shape,
        color = Color.Transparent,
        border = BorderStroke(1.2.dp, borderBrush)
    ) {
        Box(
            modifier = Modifier
                .background(cardBrush)
                .padding(20.dp)
        ) {
            content()
        }
    }
}
