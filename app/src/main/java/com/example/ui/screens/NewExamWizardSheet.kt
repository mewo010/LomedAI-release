package com.example.ui.screens

import android.app.DatePickerDialog
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.engine.PlanGeneratorAlgorithm
import com.example.model.DifficultyLevel
import com.example.model.StudyMethod
import com.example.ui.components.GlassCard
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyanAccent
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonPinkAccent
import com.example.ui.theme.NeonVioletPrimary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.StudyPlannerViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun NewExamWizardSheet(
    viewModel: StudyPlannerViewModel,
    onDismiss: () -> Unit,
    onExamCreated: (String) -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val currentSubject by viewModel.wizardSubject.collectAsState()
    val customTitle by viewModel.wizardCustomTitle.collectAsState()
    val topicsText by viewModel.wizardTopicsText.collectAsState()
    val dateMillis by viewModel.wizardDateMillis.collectAsState()
    val difficulty by viewModel.wizardDifficulty.collectAsState()
    val dailyMinutes by viewModel.wizardDailyMinutes.collectAsState()
    val autoRecommendedMethod by viewModel.wizardAutoRecommendedMethod.collectAsState()
    val userChosenMethod by viewModel.wizardUserChosenMethod.collectAsState()
    val effectiveMethod by viewModel.wizardEffectiveMethod.collectAsState()

    var isMethodPickerExpanded by remember { mutableStateOf(false) }

    val popularSubjects = listOf(
        "מתמטיקה", "תנ\"ך", "אנגלית", "היסטוריה", "פיזיקה",
        "מדעי המחשב", "ספרות", "אזרחות", "כימיה", "ביולוגיה", "לשון"
    )

    val quickDayOffsets = listOf(3, 5, 7, 10, 14)
    val dateFormat = remember { SimpleDateFormat("EEEE, d בMMMM yyyy", Locale("he", "IL")) }

    // Number of parsed topics
    val parsedTopics = remember(topicsText) {
        PlanGeneratorAlgorithm.parseUserTopics(topicsText)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = DarkSurface,
        scrimColor = Color.Black.copy(alpha = 0.65f),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 36.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "אשף יצירת מבחן חדש",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
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
                        text = "הגדר את נושאי הלימוד ושיטת הלמידה המתאימה לך",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "סגור",
                        tint = TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Step 1: Subject Selection
            Text(
                text = "1. שם המקצוע",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = NeonVioletPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                popularSubjects.forEach { subj ->
                    val isSelected = currentSubject.equals(subj, ignoreCase = true)
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) NeonVioletPrimary else Color(0x22FFFFFF),
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) NeonVioletPrimary else GlassBorder
                        ),
                        modifier = Modifier.clickable { viewModel.setWizardSubject(subj) }
                    ) {
                        Text(
                            text = subj,
                            color = if (isSelected) DarkBackground else TextPrimary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Custom Subject Input
            OutlinedTextField(
                value = currentSubject,
                onValueChange = { viewModel.setWizardSubject(it) },
                label = { Text("או הקלד מקצוע אחר", color = TextSecondary) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonVioletPrimary,
                    unfocusedBorderColor = GlassBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedContainerColor = DarkBackground,
                    unfocusedContainerColor = DarkBackground
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("wizard_subject_input")
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Custom Exam Title
            OutlinedTextField(
                value = customTitle,
                onValueChange = { viewModel.setWizardCustomTitle(it) },
                label = { Text("כותרת ספציפית למבחן (למשל: בגרות חורף - שאלון 581)", color = TextMuted) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCyanAccent,
                    unfocusedBorderColor = GlassBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedContainerColor = DarkBackground,
                    unfocusedContainerColor = DarkBackground
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Step 2: Content / Topics to Study (USER REQUEST)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.FormatListBulleted,
                        contentDescription = null,
                        tint = NeonVioletPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "2. מה נכלל במבחן? / נושאי הלימוד והפרקים",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonVioletPrimary
                    )
                }

                if (parsedTopics.isNotEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = NeonEmerald.copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, NeonEmerald.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = "${parsedTopics.size} נושאים",
                            color = NeonEmerald,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "הזן פרקים, נושאים או מושגים (מופרדים בשורות חדשות או פסיקים). האלגוריתם יחלק אותם באופן חכם על פני ימי הלימוד!",
                fontSize = 12.sp,
                color = TextSecondary,
                lineHeight = 16.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = topicsText,
                onValueChange = { viewModel.setWizardTopicsText(it) },
                placeholder = {
                    Text(
                        "לדוגמה:\nפרקים י\"ב-ט\"ו (ספר מלכים)\nטריגונומטריה במרחב\nחקירת פונקציות וחדו\"א\nאוצר מילים יחידה 3",
                        color = TextMuted,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )
                },
                minLines = 3,
                maxLines = 5,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NeonCyanAccent,
                    unfocusedBorderColor = GlassBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedContainerColor = DarkBackground,
                    unfocusedContainerColor = DarkBackground
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("wizard_topics_input")
            )

            // Topics preview chips if entered
            if (parsedTopics.isNotEmpty()) {
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    parsedTopics.forEach { topic ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0x2200E5FF),
                            border = BorderStroke(1.dp, NeonCyanAccent.copy(alpha = 0.3f))
                        ) {
                            Text(
                                text = "• $topic",
                                color = TextPrimary,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Step 3: Study Method Selector (USER REQUEST - Choose/Change Method)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = null,
                        tint = NeonVioletPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "3. שיטת הלמידה",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonVioletPrimary
                    )
                }

                Text(
                    text = if (isMethodPickerExpanded) "סגור רשימה 🔼" else "שנה שיטה 🔄",
                    fontSize = 12.sp,
                    color = NeonCyanAccent,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .clickable { isMethodPickerExpanded = !isMethodPickerExpanded }
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Currently Active Method Card
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = Color(0x337C4DFF),
                borderColor = NeonVioletPrimary
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .background(NeonVioletPrimary.copy(alpha = 0.25f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = NeonCyanAccent,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = effectiveMethod.titleHe,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextPrimary
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (userChosenMethod == null) NeonCyanAccent.copy(alpha = 0.2f) else NeonAmber.copy(alpha = 0.2f),
                            border = BorderStroke(
                                1.dp,
                                if (userChosenMethod == null) NeonCyanAccent.copy(alpha = 0.5f) else NeonAmber.copy(alpha = 0.5f)
                            )
                        ) {
                            Text(
                                text = if (userChosenMethod == null) "מומלץ עבורך 💡" else "נבחר ידנית ✍️",
                                color = if (userChosenMethod == null) NeonCyanAccent else NeonAmber,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = effectiveMethod.fullStrategyHe,
                        fontSize = 12.sp,
                        color = TextSecondary,
                        lineHeight = 16.sp
                    )

                    if (userChosenMethod != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "חזור להמלצה האוטומטית (${autoRecommendedMethod.badgeHe})",
                            fontSize = 11.sp,
                            color = NeonCyanAccent,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .clickable { viewModel.resetWizardMethodToAuto() }
                                .padding(vertical = 2.dp)
                        )
                    }
                }
            }

            // Expandable List of All Study Methods
            AnimatedVisibility(
                visible = isMethodPickerExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "בחר מתוך מאגר שיטות הלמידה המובילות:",
                        fontSize = 13.sp,
                        color = TextMuted,
                        fontWeight = FontWeight.Medium
                    )

                    StudyMethod.values().forEach { method ->
                        val isCurrent = effectiveMethod == method
                        val isRecommended = autoRecommendedMethod == method

                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = if (isCurrent) Color(0x389D4EDD) else Color(0x18FFFFFF),
                            border = BorderStroke(
                                if (isCurrent) 1.5.dp else 1.dp,
                                if (isCurrent) NeonVioletPrimary else GlassBorder
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setWizardMethod(method)
                                    isMethodPickerExpanded = false
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = method.titleHe,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isCurrent) NeonVioletPrimary else TextPrimary
                                        )
                                        if (isRecommended) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = Color(0x3300E5FF)
                                            ) {
                                                Text(
                                                    text = "מומלץ למקצוע",
                                                    color = NeonCyanAccent,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(
                                        text = method.shortDescription,
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }

                                if (isCurrent) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "נבחר",
                                        tint = NeonEmerald,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Step 4: Exam Date Selection
            Text(
                text = "4. תאריך ויום המבחן",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = NeonVioletPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                quickDayOffsets.forEach { days ->
                    val isSelected = remember(dateMillis, days) {
                        val diff = (dateMillis - System.currentTimeMillis()) / (1000 * 60 * 60 * 24)
                        diff.toInt() == days
                    }

                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) NeonCyanAccent else Color(0x22FFFFFF),
                        border = BorderStroke(1.dp, if (isSelected) NeonCyanAccent else GlassBorder),
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                val cal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, days) }
                                viewModel.setWizardDateMillis(cal.timeInMillis)
                            }
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            Text(
                                text = "$days ימים",
                                color = if (isSelected) DarkBackground else TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Date Picker Card
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    val cal = Calendar.getInstance().apply { timeInMillis = dateMillis }
                    DatePickerDialog(
                        context,
                        { _, year, month, dayOfMonth ->
                            val selectedCal = Calendar.getInstance().apply {
                                set(year, month, dayOfMonth, 12, 0, 0)
                            }
                            viewModel.setWizardDateMillis(selectedCal.timeInMillis)
                        },
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = NeonCyanAccent,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "תאריך המבחן שנבחר:",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                            Text(
                                text = dateFormat.format(dateMillis),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                    }
                    Text(
                        text = "שנה ✏️",
                        fontSize = 12.sp,
                        color = NeonVioletPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Step 5: Difficulty & Daily Minutes
            Text(
                text = "5. רמת עומס וזמן למידה יומי",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = NeonVioletPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DifficultyLevel.values().forEach { level ->
                    val isSelected = difficulty == level
                    val activeColor = when (level) {
                        DifficultyLevel.EASY -> NeonEmerald
                        DifficultyLevel.MEDIUM -> NeonCyanAccent
                        DifficultyLevel.HARD -> NeonPinkAccent
                    }

                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) activeColor.copy(alpha = 0.2f) else Color(0x18FFFFFF),
                        border = BorderStroke(
                            if (isSelected) 1.5.dp else 1.dp,
                            if (isSelected) activeColor else GlassBorder
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { viewModel.setWizardDifficulty(level) }
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 12.dp, horizontal = 6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = level.titleHe,
                                color = if (isSelected) activeColor else TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = level.descriptionHe,
                                color = TextMuted,
                                fontSize = 10.sp,
                                lineHeight = 13.sp,
                                maxLines = 2
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "זמן למידה מוקצה בכל יום:",
                    fontSize = 13.sp,
                    color = TextPrimary
                )
                Text(
                    text = "$dailyMinutes דקות",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonCyanAccent
                )
            }
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(30, 45, 60, 90, 120).forEach { mins ->
                    val isSelected = dailyMinutes == mins
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) NeonVioletPrimary else Color(0x22FFFFFF),
                        border = BorderStroke(1.dp, if (isSelected) NeonVioletPrimary else GlassBorder),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { viewModel.setWizardDailyMinutes(mins) }
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(vertical = 7.dp)
                        ) {
                            Text(
                                text = "$mins'",
                                color = if (isSelected) DarkBackground else TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Action: Generate Plan Button
            Button(
                onClick = {
                    val newExam = viewModel.createExamFromWizard()
                    onExamCreated(newExam.id)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("generate_plan_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NeonVioletPrimary)
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = DarkBackground,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "ייצר תוכנית למידה חכמה ב-lomedAi 🚀",
                    color = DarkBackground,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}
