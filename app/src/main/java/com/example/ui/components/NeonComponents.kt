package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DifficultyLevel
import com.example.model.StudyMethod
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyanAccent
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonPinkAccent
import com.example.ui.theme.NeonVioletPrimary
import com.example.ui.theme.NeonVioletSecondary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary

@Composable
fun NeonProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    height: Dp = 8.dp,
    trackColor: Color = Color(0x33C77DFF),
    gradientColors: List<Color> = listOf(NeonVioletPrimary, NeonCyanAccent)
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 600),
        label = "progress"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(height / 2))
            .background(trackColor)
    ) {
        if (animatedProgress > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(fraction = animatedProgress)
                    .clip(RoundedCornerShape(height / 2))
                    .background(Brush.horizontalGradient(gradientColors))
            )
        }
    }
}

@Composable
fun DifficultyBadge(
    difficulty: DifficultyLevel,
    modifier: Modifier = Modifier
) {
    val (color, bg) = when (difficulty) {
        DifficultyLevel.EASY -> Pair(NeonEmerald, Color(0x2200E676))
        DifficultyLevel.MEDIUM -> Pair(NeonAmber, Color(0x22FFB300))
        DifficultyLevel.HARD -> Pair(NeonPinkAccent, Color(0x22FF4081))
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = bg,
        border = BorderStroke(1.dp, color.copy(alpha = 0.5f))
    ) {
        Text(
            text = difficulty.titleHe,
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

@Composable
fun MethodBadge(
    method: StudyMethod,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = Color(0x269D4EDD),
        border = BorderStroke(1.dp, NeonVioletPrimary.copy(alpha = 0.5f))
    ) {
        Text(
            text = method.badgeHe,
            color = NeonVioletPrimary,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

@Composable
fun StatChip(
    icon: ImageVector,
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    accentColor: Color = NeonVioletPrimary
) {
    GlassCard(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        borderColor = accentColor.copy(alpha = 0.35f)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            androidx.compose.foundation.layout.Column {
                Text(
                    text = value,
                    color = TextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = title,
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }
}
