package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlassCard
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassSurfaceElevated
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyanAccent
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonVioletPrimary
import com.example.ui.theme.NeonVioletSecondary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.StudyPlannerViewModel
import com.example.viewmodel.TimerMode

@Composable
fun TimerScreen(
    viewModel: StudyPlannerViewModel,
    modifier: Modifier = Modifier
) {
    val timerState by viewModel.timerState.collectAsState()

    // Pulsing animation when running
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (timerState.isRunning) 1.03f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Header
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Timer,
                    contentDescription = null,
                    tint = NeonCyanAccent,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "מצב תרגול וטיימר מובנה",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "מיקוד מקסימלי עם התראת סיום ומשוב רטט",
                fontSize = 12.sp,
                color = TextSecondary
            )

            // Associated Task info (if active)
            if (timerState.associatedExamTitle != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0x337C4DFF),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonVioletPrimary.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = timerState.associatedExamTitle ?: "",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonCyanAccent
                        )
                        if (!timerState.associatedTaskTitle.isNullOrBlank()) {
                            Text(
                                text = timerState.associatedTaskTitle ?: "",
                                fontSize = 11.sp,
                                color = TextPrimary
                            )
                        }
                    }
                }
            }
        }

        // Circular Timer Visual
        Box(
            modifier = Modifier
                .size(260.dp)
                .scale(pulseScale),
            contentAlignment = Alignment.Center
        ) {
            val progress = timerState.progress
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokeWidth = 14.dp.toPx()
                // Track
                drawCircle(
                    color = Color(0x229D4EDD),
                    style = Stroke(width = strokeWidth)
                )
                // Active Neon Sweep
                val sweepAngle = 360f * (1f - progress)
                drawArc(
                    brush = Brush.sweepGradient(
                        listOf(
                            NeonVioletPrimary,
                            NeonCyanAccent,
                            NeonVioletSecondary,
                            NeonVioletPrimary
                        )
                    ),
                    startAngle = -90f,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }

            // Inner Time Text
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = timerState.formattedTime,
                    fontSize = 54.sp,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (timerState.isRunning) NeonEmerald.copy(alpha = 0.2f) else Color(0x22FFFFFF)
                ) {
                    Text(
                        text = if (timerState.isRunning) "בלמידה ממוקדת 🎯" else "מושהה",
                        color = if (timerState.isRunning) NeonEmerald else TextMuted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // Mode Selector Chips
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "בחר מצב למידה:",
                fontSize = 12.sp,
                color = TextMuted,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TimerMode.values().forEach { mode ->
                    val isSelected = timerState.mode == mode
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) NeonVioletPrimary else Color(0x1AFFFFFF),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) NeonVioletPrimary else GlassBorder
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { viewModel.setTimerMode(mode) }
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(vertical = 10.dp)
                        ) {
                            Text(
                                text = mode.titleHe,
                                color = if (isSelected) DarkBackground else TextPrimary,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        // Controls (Play / Pause / Reset)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Reset button
            IconButton(
                onClick = { viewModel.resetTimer() },
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(Color(0x22FFFFFF))
            ) {
                Icon(
                    imageVector = Icons.Default.Replay,
                    contentDescription = "איפוס",
                    tint = TextPrimary,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(28.dp))

            // Main Play/Pause Button
            Button(
                onClick = { viewModel.togglePlayPauseTimer() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (timerState.isRunning) NeonCyanAccent else NeonVioletPrimary
                ),
                shape = CircleShape,
                modifier = Modifier
                    .size(80.dp)
                    .testTag("timer_play_pause_button")
            ) {
                Icon(
                    imageVector = if (timerState.isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (timerState.isRunning) "השהה" else "התחל",
                    tint = DarkBackground,
                    modifier = Modifier.size(42.dp)
                )
            }

            Spacer(modifier = Modifier.width(28.dp))

            // Volume/Sound indicator
            IconButton(
                onClick = { },
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(Color(0x22FFFFFF))
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                    contentDescription = "צליל מופעל",
                    tint = NeonEmerald,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }

    // Session Finished Celebration Dialog
    if (timerState.sessionFinishedEvent) {
        AlertDialog(
            onDismissRequest = { viewModel.dismissFinishedEvent() },
            containerColor = DarkSurface,
            titleContentColor = NeonVioletPrimary,
            textContentColor = TextPrimary,
            icon = {
                Icon(
                    imageVector = Icons.Default.Celebration,
                    contentDescription = null,
                    tint = NeonAmber,
                    modifier = Modifier.size(48.dp)
                )
            },
            title = {
                Text(
                    text = "כל הכבוד! סיימת סשן למידה! 🏆",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Text(
                    text = "השלמת סשן של ${timerState.totalSeconds / 60} דקות בהצלחה. הזמן נרשם בסטטיסטיקות שלך והמשימה סומנה כהושלמה! המשך כך לקראת ציון מושלם במבחן.",
                    fontSize = 14.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.dismissFinishedEvent() },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonVioletPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("מעולה, המשך", color = DarkBackground, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}
