package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.FullPlanScreen
import com.example.ui.screens.NewExamWizardSheet
import com.example.ui.screens.TimerScreen
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NeonCyanAccent
import com.example.ui.theme.NeonVioletPrimary
import com.example.ui.theme.NeonVioletSecondary
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.viewmodel.StudyPlannerViewModel

enum class AppScreen {
    DASHBOARD,
    FULL_PLAN,
    TIMER
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                // Enforce 100% full Hebrew RTL layout direction across all screens
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    StudyPlannerApp()
                }
            }
        }
    }
}

@Composable
fun StudyPlannerApp(viewModel: StudyPlannerViewModel = viewModel()) {
    var currentScreen by remember { mutableStateOf(AppScreen.DASHBOARD) }
    var showWizardSheet by remember { mutableStateOf(false) }

    // Handle back button for sub-screens
    BackHandler(enabled = currentScreen != AppScreen.DASHBOARD) {
        currentScreen = AppScreen.DASHBOARD
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = DarkBackground,
        floatingActionButton = {
            if (currentScreen != AppScreen.TIMER) {
                FloatingActionButton(
                    onClick = { showWizardSheet = true },
                    containerColor = NeonVioletPrimary,
                    contentColor = DarkBackground,
                    shape = CircleShape,
                    elevation = FloatingActionButtonDefaults.elevation(8.dp),
                    modifier = Modifier.testTag("fab_add_exam")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "הוסף מבחן חדש",
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = DarkSurface,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = currentScreen == AppScreen.DASHBOARD,
                    onClick = { currentScreen = AppScreen.DASHBOARD },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Dashboard,
                            contentDescription = "מסך הבית",
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = {
                        Text(
                            text = "לוח בקרה",
                            fontSize = 11.sp,
                            fontWeight = if (currentScreen == AppScreen.DASHBOARD) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = DarkBackground,
                        selectedTextColor = NeonVioletPrimary,
                        indicatorColor = NeonVioletPrimary,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                    )
                )

                NavigationBarItem(
                    selected = currentScreen == AppScreen.FULL_PLAN,
                    onClick = { currentScreen = AppScreen.FULL_PLAN },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "תוכנית מפורטת",
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = {
                        Text(
                            text = "תוכנית לימוד",
                            fontSize = 11.sp,
                            fontWeight = if (currentScreen == AppScreen.FULL_PLAN) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = DarkBackground,
                        selectedTextColor = NeonVioletPrimary,
                        indicatorColor = NeonVioletPrimary,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                    )
                )

                NavigationBarItem(
                    selected = currentScreen == AppScreen.TIMER,
                    onClick = { currentScreen = AppScreen.TIMER },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = "טיימר למידה",
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = {
                        Text(
                            text = "טיימר פוקוס",
                            fontSize = 11.sp,
                            fontWeight = if (currentScreen == AppScreen.TIMER) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = DarkBackground,
                        selectedTextColor = NeonCyanAccent,
                        indicatorColor = NeonCyanAccent,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                    )
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                AppScreen.DASHBOARD -> {
                    DashboardScreen(
                        viewModel = viewModel,
                        onOpenWizard = { showWizardSheet = true },
                        onOpenExamPlan = { examId ->
                            viewModel.setSelectedExam(examId)
                            currentScreen = AppScreen.FULL_PLAN
                        },
                        onOpenTimer = {
                            currentScreen = AppScreen.TIMER
                        }
                    )
                }

                AppScreen.FULL_PLAN -> {
                    FullPlanScreen(
                        viewModel = viewModel,
                        onNavigateBack = { currentScreen = AppScreen.DASHBOARD },
                        onOpenTimer = { currentScreen = AppScreen.TIMER }
                    )
                }

                AppScreen.TIMER -> {
                    TimerScreen(
                        viewModel = viewModel
                    )
                }
            }
        }

        // New Exam Wizard Modal Bottom Sheet
        if (showWizardSheet) {
            NewExamWizardSheet(
                viewModel = viewModel,
                onDismiss = { showWizardSheet = false },
                onExamCreated = {
                    showWizardSheet = false
                    currentScreen = AppScreen.FULL_PLAN
                }
            )
        }
    }
}
