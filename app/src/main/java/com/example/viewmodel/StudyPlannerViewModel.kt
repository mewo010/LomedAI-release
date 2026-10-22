package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.StudyPlannerRepository
import com.example.engine.PlanGeneratorAlgorithm
import com.example.model.DifficultyLevel
import com.example.model.Exam
import com.example.model.StudyMethod
import com.example.model.StudyStats
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

enum class TimerMode(val titleHe: String, val defaultMinutes: Int) {
    POMODORO("פומודורו (25 דק')", 25),
    DEEP_FOCUS("סשן עמוק (45 דק')", 45),
    EXTENDED("מרתון (60 דק')", 60),
    SHORT_BREAK("הפסקה (5 דק')", 5)
}

data class TimerUiState(
    val isRunning: Boolean = false,
    val mode: TimerMode = TimerMode.POMODORO,
    val totalSeconds: Int = 25 * 60,
    val remainingSeconds: Int = 25 * 60,
    val associatedExamTitle: String? = null,
    val associatedTaskTitle: String? = null,
    val associatedExamId: String? = null,
    val associatedDayId: String? = null,
    val associatedTaskId: String? = null,
    val sessionFinishedEvent: Boolean = false
) {
    val progress: Float
        get() = if (totalSeconds <= 0) 0f else (totalSeconds - remainingSeconds).toFloat() / totalSeconds.toFloat()

    val formattedTime: String
        get() {
            val mins = remainingSeconds / 60
            val secs = remainingSeconds % 60
            return String.format("%02d:%02d", mins, secs)
        }
}

class StudyPlannerViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = StudyPlannerRepository(application)

    val exams: StateFlow<List<Exam>> = repository.examsFlow
    val stats: StateFlow<StudyStats> = repository.statsFlow

    // Navigation and Selected Exam
    private val _selectedExamId = MutableStateFlow<String?>(null)
    val selectedExamId: StateFlow<String?> = _selectedExamId.asStateFlow()

    val selectedExam: StateFlow<Exam?> = combine(exams, selectedExamId) { list, id ->
        list.find { it.id == id } ?: list.firstOrNull()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Today's focus items across all active exams
    val todayPlanItems: StateFlow<List<Pair<Exam, com.example.model.DailyPlanItem>>> = exams.combine(_selectedExamId) { list, _ ->
        val results = mutableListOf<Pair<Exam, com.example.model.DailyPlanItem>>()
        list.forEach { exam ->
            val currentDay = exam.planDays.firstOrNull { !it.isCompleted } ?: exam.planDays.firstOrNull()
            if (currentDay != null) {
                results.add(Pair(exam, currentDay))
            }
        }
        results
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Timer State
    private val _timerState = MutableStateFlow(TimerUiState())
    val timerState: StateFlow<TimerUiState> = _timerState.asStateFlow()

    private var timerJob: Job? = null

    // Wizard Form State
    private val _wizardSubject = MutableStateFlow("מתמטיקה")
    val wizardSubject: StateFlow<String> = _wizardSubject.asStateFlow()

    private val _wizardCustomTitle = MutableStateFlow("")
    val wizardCustomTitle: StateFlow<String> = _wizardCustomTitle.asStateFlow()

    private val _wizardTopicsText = MutableStateFlow("")
    val wizardTopicsText: StateFlow<String> = _wizardTopicsText.asStateFlow()

    private val _wizardDateMillis = MutableStateFlow(getDefaultExamDate())
    val wizardDateMillis: StateFlow<Long> = _wizardDateMillis.asStateFlow()

    private val _wizardDifficulty = MutableStateFlow(DifficultyLevel.MEDIUM)
    val wizardDifficulty: StateFlow<DifficultyLevel> = _wizardDifficulty.asStateFlow()

    private val _wizardDailyMinutes = MutableStateFlow(60)
    val wizardDailyMinutes: StateFlow<Int> = _wizardDailyMinutes.asStateFlow()

    // Recommended method by the algorithm
    val wizardAutoRecommendedMethod: StateFlow<StudyMethod> = _wizardSubject.combine(_wizardDifficulty) { subj, _ ->
        PlanGeneratorAlgorithm.recommendMethod(subj)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), StudyMethod.SPIRAL_PRACTICE)

    // User's custom selected method (null if using system recommendation)
    private val _wizardUserChosenMethod = MutableStateFlow<StudyMethod?>(null)
    val wizardUserChosenMethod: StateFlow<StudyMethod?> = _wizardUserChosenMethod.asStateFlow()

    // The effective method that will actually be used to build the plan
    val wizardEffectiveMethod: StateFlow<StudyMethod> = combine(
        wizardAutoRecommendedMethod,
        _wizardUserChosenMethod
    ) { autoRec, userChoice ->
        userChoice ?: autoRec
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), StudyMethod.SPIRAL_PRACTICE)

    fun setSelectedExam(id: String?) {
        _selectedExamId.value = id
    }

    fun toggleTask(examId: String, dayId: String, taskId: String) {
        repository.toggleTask(examId, dayId, taskId)
    }

    fun deleteExam(examId: String) {
        if (_selectedExamId.value == examId) {
            _selectedExamId.value = null
        }
        repository.deleteExam(examId)
    }

    // Wizard Controls
    fun setWizardSubject(subject: String) {
        _wizardSubject.value = subject
        // Reset manual method override on subject change so recommendation updates smoothly
        _wizardUserChosenMethod.value = null
    }

    fun setWizardCustomTitle(title: String) {
        _wizardCustomTitle.value = title
    }

    fun setWizardTopicsText(topics: String) {
        _wizardTopicsText.value = topics
    }

    fun setWizardDateMillis(dateMillis: Long) {
        _wizardDateMillis.value = dateMillis
    }

    fun setWizardDifficulty(difficulty: DifficultyLevel) {
        _wizardDifficulty.value = difficulty
    }

    fun setWizardDailyMinutes(minutes: Int) {
        _wizardDailyMinutes.value = minutes
    }

    fun setWizardMethod(method: StudyMethod) {
        _wizardUserChosenMethod.value = method
    }

    fun resetWizardMethodToAuto() {
        _wizardUserChosenMethod.value = null
    }

    fun resetWizardForm() {
        _wizardSubject.value = "מתמטיקה"
        _wizardCustomTitle.value = ""
        _wizardTopicsText.value = ""
        _wizardDateMillis.value = getDefaultExamDate()
        _wizardDifficulty.value = DifficultyLevel.MEDIUM
        _wizardDailyMinutes.value = 60
        _wizardUserChosenMethod.value = null
    }

    fun createExamFromWizard(): Exam {
        val subject = _wizardSubject.value.ifBlank { "מבחן כללי" }
        val title = _wizardCustomTitle.value.trim()
        val topics = _wizardTopicsText.value.trim()
        val dateMillis = _wizardDateMillis.value
        val difficulty = _wizardDifficulty.value
        val dailyMinutes = _wizardDailyMinutes.value
        val method = wizardEffectiveMethod.value

        val newExam = repository.addExam(
            subject = subject,
            customTitle = title,
            examDateMillis = dateMillis,
            difficulty = difficulty,
            dailyMinutes = dailyMinutes,
            method = method,
            rawTopicsText = topics
        )
        _selectedExamId.value = newExam.id
        resetWizardForm()
        return newExam
    }

    // Timer Controls
    fun setTimerMode(mode: TimerMode) {
        timerJob?.cancel()
        val totalSecs = mode.defaultMinutes * 60
        _timerState.value = _timerState.value.copy(
            isRunning = false,
            mode = mode,
            totalSeconds = totalSecs,
            remainingSeconds = totalSecs,
            sessionFinishedEvent = false
        )
    }

    fun startTimerForTask(
        examTitle: String,
        taskTitle: String,
        examId: String,
        dayId: String,
        taskId: String,
        durationMinutes: Int = 25
    ) {
        timerJob?.cancel()
        val totalSecs = durationMinutes * 60
        _timerState.value = TimerUiState(
            isRunning = true,
            mode = TimerMode.POMODORO,
            totalSeconds = totalSecs,
            remainingSeconds = totalSecs,
            associatedExamTitle = examTitle,
            associatedTaskTitle = taskTitle,
            associatedExamId = examId,
            associatedDayId = dayId,
            associatedTaskId = taskId,
            sessionFinishedEvent = false
        )
        runTimerLoop()
    }

    fun togglePlayPauseTimer() {
        if (_timerState.value.isRunning) {
            pauseTimer()
        } else {
            startTimer()
        }
    }

    fun startTimer() {
        if (_timerState.value.remainingSeconds <= 0) {
            val totalSecs = _timerState.value.mode.defaultMinutes * 60
            _timerState.value = _timerState.value.copy(
                remainingSeconds = totalSecs,
                totalSeconds = totalSecs,
                sessionFinishedEvent = false
            )
        }
        _timerState.value = _timerState.value.copy(isRunning = true, sessionFinishedEvent = false)
        runTimerLoop()
    }

    fun pauseTimer() {
        timerJob?.cancel()
        _timerState.value = _timerState.value.copy(isRunning = false)
    }

    fun resetTimer() {
        timerJob?.cancel()
        val totalSecs = _timerState.value.mode.defaultMinutes * 60
        _timerState.value = _timerState.value.copy(
            isRunning = false,
            totalSeconds = totalSecs,
            remainingSeconds = totalSecs,
            sessionFinishedEvent = false
        )
    }

    fun dismissFinishedEvent() {
        _timerState.value = _timerState.value.copy(sessionFinishedEvent = false)
    }

    private fun runTimerLoop() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_timerState.value.remainingSeconds > 0 && _timerState.value.isRunning) {
                delay(1000)
                val newRemaining = _timerState.value.remainingSeconds - 1
                _timerState.value = _timerState.value.copy(remainingSeconds = newRemaining)
            }

            if (_timerState.value.remainingSeconds <= 0) {
                _timerState.value = _timerState.value.copy(
                    isRunning = false,
                    sessionFinishedEvent = true
                )
                val minutesLearned = _timerState.value.totalSeconds / 60
                repository.recordCompletedSession(minutesLearned)

                val examId = _timerState.value.associatedExamId
                val dayId = _timerState.value.associatedDayId
                val taskId = _timerState.value.associatedTaskId
                if (examId != null && dayId != null && taskId != null) {
                    repository.toggleTask(examId, dayId, taskId)
                }

                triggerCompletionFeedback()
            }
        }
    }

    private fun triggerCompletionFeedback() {
        try {
            val app = getApplication<Application>()
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = app.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                app.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 300, 200, 400), -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(500)
            }

            val toneGen = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 90)
            toneGen.startTone(ToneGenerator.TONE_PROP_BEEP2, 350)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getDefaultExamDate(): Long {
        return Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, 7)
        }.timeInMillis
    }
}
