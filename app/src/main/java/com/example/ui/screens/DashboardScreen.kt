package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.CheckCircleOutline
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Exam
import com.example.ui.components.DifficultyBadge
import com.example.ui.components.GlassCard
import com.example.ui.components.MethodBadge
import com.example.ui.components.NeonGradientCard
import com.example.ui.components.NeonProgressBar
import com.example.ui.components.StatChip
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyanAccent
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonPinkAccent
import com.example.ui.theme.NeonVioletPrimary
import com.example.ui.theme.NeonVioletSecondary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.StudyPlannerViewModel

@Composable
fun DashboardScreen(
    viewModel: StudyPlannerViewModel,
    onOpenWizard: () -> Unit,
    onOpenExamPlan: (String) -> Unit,
    onOpenTimer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val exams by viewModel.exams.collectAsState()
    val stats by viewModel.stats.collectAsState()
    val todayPlanItems by viewModel.todayPlanItems.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // App Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "lomedAi",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = NeonVioletPrimary
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = NeonCyanAccent,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Text(
                        text = "מתכנן למידה חכם למבחנים",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }

                Button(
                    onClick = onOpenWizard,
                    colors = ButtonDefaults.buttonColors(containerColor = NeonVioletPrimary),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.testTag("create_exam_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "מבחן חדש",
                        tint = DarkBackground,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "מבחן חדש",
                        color = DarkBackground,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }

        // Quick Stats Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatChip(
                    icon = Icons.Default.CalendarMonth,
                    title = "מבחנים פעילים",
                    value = "${exams.size}",
                    accentColor = NeonCyanAccent,
                    modifier = Modifier.weight(1f)
                )
                StatChip(
                    icon = Icons.Default.Timer,
                    title = "שעות למידה",
                    value = "${stats.totalMinutesStudied / 60}h",
                    accentColor = NeonVioletPrimary,
                    modifier = Modifier.weight(1f)
                )
                StatChip(
                    icon = Icons.Default.LocalFireDepartment,
                    title = "ימי רצף",
                    value = "${stats.streakDays}🔥",
                    accentColor = NeonAmber,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Today's Study Plan Section
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "תוכנית הלימוד להיום",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "משימות ממוקדות",
                        fontSize = 12.sp,
                        color = NeonVioletPrimary
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))

                if (todayPlanItems.isEmpty()) {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = NeonEmerald,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "אין משימות פעילות להיום!",
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "הוסף מבחן חדש כדי לייצר תוכנית לימוד מותאמת אישית",
                                color = TextMuted,
                                fontSize = 12.sp
                            )
                        }
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        todayPlanItems.forEach { (exam, dayPlan) ->
                            TodayPlanCard(
                                exam = exam,
                                dayPlan = dayPlan,
                                onToggleTask = { taskId ->
                                    viewModel.toggleTask(exam.id, dayPlan.id, taskId)
                                },
                                onStartTimer = { taskTitle, taskId ->
                                    viewModel.startTimerForTask(
                                        examTitle = exam.displayName,
                                        taskTitle = taskTitle,
                                        examId = exam.id,
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
            }
        }

        // Upcoming Exams Section Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "המבחנים הקרובים שלך",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "${exams.size} סה\"כ",
                    fontSize = 12.sp,
                    color = TextMuted
                )
            }
        }

        // Upcoming Exams List
        if (exams.isEmpty()) {
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "עדיין לא הזנת מבחנים",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "בנה תוכנית לימוד ראשונה מותאמת אישית עם נושאי המבחן שלך!",
                            color = TextMuted,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = onOpenWizard,
                            colors = ButtonDefaults.buttonColors(containerColor = NeonVioletPrimary),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = DarkBackground,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "צור מבחן ראשון עכשיו",
                                color = DarkBackground,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        } else {
            items(exams, key = { it.id }) { exam ->
                ExamCountdownCard(
                    exam = exam,
                    onOpenPlan = { onOpenExamPlan(exam.id) },
                    onDelete = { viewModel.deleteExam(exam.id) },
                    onQuickStudy = {
                        val firstTask = exam.planDays.flatMap { it.tasks }.firstOrNull { !it.isDone }
                        val firstDay = exam.planDays.firstOrNull { it.tasks.any { t -> !t.isDone } }
                        viewModel.startTimerForTask(
                            examTitle = exam.displayName,
                            taskTitle = firstTask?.title ?: "סשן למידה ממוקד",
                            examId = exam.id,
                            dayId = firstDay?.id ?: "",
                            taskId = firstTask?.id ?: "",
                            durationMinutes = exam.dailyMinutes
                        )
                        onOpenTimer()
                    }
                )
            }
        }

        // Daily Cognitive Tip
        item {
            NeonGradientCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(NeonVioletPrimary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = NeonVioletPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "טיפ יומי: חוק 20 הדקות",
                            color = NeonVioletPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "המוח האנושי חווה ירידה בריכוז לאחר כ-25 דקות רצופות. עבודה בסבבי פומודורו עם הפסקות קצרות משמרת רעננות קוגניטיבית לאורך כל היום!",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TodayPlanCard(
    exam: Exam,
    dayPlan: com.example.model.DailyPlanItem,
    onToggleTask: (String) -> Unit,
    onStartTimer: (String, String) -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = NeonVioletPrimary.copy(alpha = 0.35f)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = exam.displayName,
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${dayPlan.stageTitle} • ${dayPlan.allocatedMinutes} דק'",
                        color = NeonCyanAccent,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0x33B388FF),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonVioletPrimary.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = "יום ${dayPlan.dayNumber}",
                        color = NeonVioletPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Tasks List
            dayPlan.tasks.forEach { task ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onToggleTask(task.id) }
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (task.isDone) NeonEmerald else Color(0x33FFFFFF)),
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
                                contentDescription = "למד עם טיימר",
                                tint = NeonCyanAccent,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ExamCountdownCard(
    exam: Exam,
    onOpenPlan: () -> Unit,
    onDelete: () -> Unit,
    onQuickStudy: () -> Unit
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("exam_card_${exam.id}"),
        onClick = onOpenPlan
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                // Countdown Days Badge
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(NeonVioletSecondary, DarkBackground)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${exam.daysLeft}",
                            color = NeonCyanAccent,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "ימים",
                            color = TextSecondary,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = exam.displayName,
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        MethodBadge(method = exam.recommendedMethod)
                        DifficultyBadge(difficulty = exam.difficulty)
                    }
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "מחק מבחן",
                        tint = TextMuted.copy(alpha = 0.7f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Progress bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "התקדמות בתוכנית (${exam.completedTasksCount}/${exam.totalTasksCount} משימות)",
                    fontSize = 11.sp,
                    color = TextMuted
                )
                Text(
                    text = "${(exam.progressPercent * 100).toInt()}%",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonVioletPrimary
                )
            }

            Spacer(modifier = Modifier.height(6.dp))
            NeonProgressBar(progress = exam.progressPercent)

            Spacer(modifier = Modifier.height(12.dp))

            // Actions row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onOpenPlan,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0x33B388FF)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "צפה בתוכנית המלאה",
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Button(
                    onClick = onQuickStudy,
                    colors = ButtonDefaults.buttonColors(containerColor = NeonVioletPrimary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = DarkBackground,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "הפעל טיימר",
                        color = DarkBackground,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
