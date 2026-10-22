package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.DailyPlanItem
import com.example.model.Exam
import com.example.ui.components.DifficultyBadge
import com.example.ui.components.GlassCard
import com.example.ui.components.MethodBadge
import com.example.ui.components.NeonProgressBar
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyanAccent
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonVioletPrimary
import com.example.ui.theme.NeonVioletSecondary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.StudyPlannerViewModel

@Composable
fun FullPlanScreen(
    viewModel: StudyPlannerViewModel,
    onNavigateBack: () -> Unit,
    onOpenTimer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val exams by viewModel.exams.collectAsState()
    val selectedExam by viewModel.selectedExam.collectAsState()

    if (exams.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(DarkBackground),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "אין מבחנים פעילים", color = TextSecondary)
        }
        return
    }

    val currentExam = selectedExam ?: exams.first()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top navigation bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "חזרה",
                        tint = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "תוכנית למידה מפורטת",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
        }

        // Exam Switcher Selector (if multiple exams exist)
        if (exams.size > 1) {
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(exams) { exam ->
                        val isSelected = exam.id == currentExam.id
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) NeonVioletPrimary else Color(0x22FFFFFF),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) NeonVioletPrimary else GlassBorder
                            ),
                            modifier = Modifier.clickable { viewModel.setSelectedExam(exam.id) }
                        ) {
                            Text(
                                text = exam.displayName,
                                color = if (isSelected) DarkBackground else TextPrimary,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                            )
                        }
                    }
                }
            }
        }

        // Exam Overview Card
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = currentExam.displayName,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                MethodBadge(method = currentExam.recommendedMethod)
                                DifficultyBadge(difficulty = currentExam.difficulty)
                            }
                        }

                        // Days countdown circle
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        listOf(NeonVioletSecondary, DarkBackground)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${currentExam.daysLeft}",
                                    color = NeonCyanAccent,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "ימים",
                                    color = TextSecondary,
                                    fontSize = 9.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Progress
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "הושלמו ${currentExam.completedTasksCount} מתוך ${currentExam.totalTasksCount} משימות",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                        Text(
                            text = "${(currentExam.progressPercent * 100).toInt()}%",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonVioletPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    NeonProgressBar(progress = currentExam.progressPercent)

                    Spacer(modifier = Modifier.height(12.dp))

                    // Strategy explanation
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0x227C4DFF),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = NeonCyanAccent,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = currentExam.recommendedMethod.fullStrategyHe,
                                fontSize = 11.sp,
                                color = TextSecondary,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }
        }

        // Section Title: Daily Roadmap
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "לוח זמנים יומי מפורט",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "${currentExam.planDays.size} ימי לימוד",
                    fontSize = 12.sp,
                    color = NeonCyanAccent
                )
            }
        }

        // Day by Day Items
        items(currentExam.planDays, key = { it.id }) { dayPlan ->
            DayPlanDetailCard(
                dayPlan = dayPlan,
                examDisplayName = currentExam.displayName,
                onToggleTask = { taskId ->
                    viewModel.toggleTask(currentExam.id, dayPlan.id, taskId)
                },
                onStartTimer = { taskTitle, taskId ->
                    viewModel.startTimerForTask(
                        examTitle = currentExam.displayName,
                        taskTitle = taskTitle,
                        examId = currentExam.id,
                        dayId = dayPlan.id,
                        taskId = taskId,
                        durationMinutes = dayPlan.allocatedMinutes.coerceAtMost(60)
                    )
                    onOpenTimer()
                }
            )
        }
    }
}

@Composable
private fun DayPlanDetailCard(
    dayPlan: DailyPlanItem,
    examDisplayName: String,
    onToggleTask: (String) -> Unit,
    onStartTimer: (String, String) -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = if (dayPlan.isCompleted) NeonEmerald.copy(alpha = 0.5f) else GlassBorder
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Day Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (dayPlan.isCompleted) NeonEmerald.copy(alpha = 0.2f) else Color(0x33B388FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${dayPlan.dayNumber}",
                            color = if (dayPlan.isCompleted) NeonEmerald else NeonVioletPrimary,
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = dayPlan.stageTitle,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = dayPlan.dateDisplay,
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                        if (dayPlan.dailyTopics.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "נושאי היום: ${dayPlan.dailyTopics.joinToString(", ")}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = NeonCyanAccent
                            )
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0x2200E5FF),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyanAccent.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = NeonCyanAccent,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${dayPlan.allocatedMinutes} דק'",
                            color = NeonCyanAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Tasks List
            dayPlan.tasks.forEach { task ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onToggleTask(task.id) }
                        .padding(horizontal = 4.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (task.isDone) NeonEmerald else Color(0x26FFFFFF)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (task.isDone) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = DarkBackground,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = task.title,
                        color = if (task.isDone) TextMuted else TextPrimary,
                        fontSize = 13.sp,
                        textDecoration = if (task.isDone) TextDecoration.LineThrough else null,
                        modifier = Modifier.weight(1f)
                    )

                    if (!task.isDone) {
                        IconButton(
                            onClick = { onStartTimer(task.title, task.id) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "התחל טיימר למשימה זו",
                                tint = NeonCyanAccent,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Daily Tip
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0x18FFFFFF))
                    .padding(10.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = null,
                    tint = NeonAmber,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = dayPlan.tipOfTheDay,
                    fontSize = 11.sp,
                    color = TextSecondary,
                    lineHeight = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Quick button to run session for today
            Button(
                onClick = {
                    val firstUnfinished = dayPlan.tasks.firstOrNull { !it.isDone }
                    onStartTimer(
                        firstUnfinished?.title ?: dayPlan.stageTitle,
                        firstUnfinished?.id ?: dayPlan.id
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0x2E9D4EDD)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Timer,
                    contentDescription = null,
                    tint = NeonVioletPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "הפעל טיימר למידה לשלב זה (${dayPlan.allocatedMinutes} דק')",
                    color = NeonVioletPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
