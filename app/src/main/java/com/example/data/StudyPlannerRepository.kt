package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.engine.PlanGeneratorAlgorithm
import com.example.model.DailyPlanItem
import com.example.model.DifficultyLevel
import com.example.model.Exam
import com.example.model.PlanTaskItem
import com.example.model.StudyMethod
import com.example.model.StudyStats
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class StudyPlannerRepository(private val context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("lomedai_planner_prefs", Context.MODE_PRIVATE)

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val examListType = Types.newParameterizedType(List::class.java, Exam::class.java)
    private val examAdapter = moshi.adapter<List<Exam>>(examListType)
    private val statsAdapter = moshi.adapter(StudyStats::class.java)

    private val _examsFlow = MutableStateFlow<List<Exam>>(emptyList())
    val examsFlow: StateFlow<List<Exam>> = _examsFlow.asStateFlow()

    private val _statsFlow = MutableStateFlow(StudyStats())
    val statsFlow: StateFlow<StudyStats> = _statsFlow.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        val jsonExams = prefs.getString(KEY_EXAMS, null)
        val examsList = if (!jsonExams.isNullOrEmpty()) {
            try {
                examAdapter.fromJson(jsonExams) ?: emptyList()
            } catch (e: Exception) {
                emptyList()
            }
        } else {
            // Clean empty initial state - no mock exams
            emptyList()
        }
        _examsFlow.value = examsList

        val jsonStats = prefs.getString(KEY_STATS, null)
        val stats = if (!jsonStats.isNullOrEmpty()) {
            try {
                statsAdapter.fromJson(jsonStats) ?: StudyStats()
            } catch (e: Exception) {
                StudyStats()
            }
        } else {
            StudyStats()
        }
        _statsFlow.value = stats
    }

    private fun saveData() {
        try {
            val jsonExams = examAdapter.toJson(_examsFlow.value)
            val jsonStats = statsAdapter.toJson(_statsFlow.value)
            prefs.edit()
                .putString(KEY_EXAMS, jsonExams)
                .putString(KEY_STATS, jsonStats)
                .apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun addExam(
        subject: String,
        customTitle: String,
        examDateMillis: Long,
        difficulty: DifficultyLevel,
        dailyMinutes: Int,
        method: StudyMethod,
        rawTopicsText: String = ""
    ): Exam {
        val parsedTopics = PlanGeneratorAlgorithm.parseUserTopics(rawTopicsText)
        val plan = PlanGeneratorAlgorithm.generatePlan(
            subject = subject,
            examDateMillis = examDateMillis,
            difficulty = difficulty,
            dailyMinutesPreference = dailyMinutes,
            selectedMethod = method,
            rawTopicsText = rawTopicsText
        )

        val newExam = Exam(
            id = UUID.randomUUID().toString(),
            subject = subject,
            customTitle = customTitle,
            examDateMillis = examDateMillis,
            difficulty = difficulty,
            dailyMinutes = dailyMinutes,
            recommendedMethod = method,
            userTopics = parsedTopics,
            planDays = plan,
            createdAtMillis = System.currentTimeMillis()
        )

        val updated = listOf(newExam) + _examsFlow.value
        _examsFlow.value = updated
        saveData()
        return newExam
    }

    fun deleteExam(examId: String) {
        val updated = _examsFlow.value.filterNot { it.id == examId }
        _examsFlow.value = updated
        saveData()
    }

    fun toggleTask(examId: String, dayId: String, taskId: String) {
        val currentExams = _examsFlow.value
        val updated = currentExams.map { exam ->
            if (exam.id == examId) {
                val updatedDays = exam.planDays.map { day ->
                    if (day.id == dayId) {
                        val updatedTasks = day.tasks.map { task ->
                            if (task.id == taskId) {
                                task.copy(isDone = !task.isDone)
                            } else task
                        }
                        day.copy(
                            tasks = updatedTasks,
                            isCompleted = updatedTasks.isNotEmpty() && updatedTasks.all { it.isDone }
                        )
                    } else day
                }
                exam.copy(planDays = updatedDays)
            } else exam
        }
        _examsFlow.value = updated
        saveData()
    }

    fun recordCompletedSession(minutes: Int) {
        val current = _statsFlow.value
        val updated = current.copy(
            totalMinutesStudied = current.totalMinutesStudied + minutes,
            completedSessionsCount = current.completedSessionsCount + 1,
            streakDays = if (current.streakDays == 0) 1 else current.streakDays
        )
        _statsFlow.value = updated
        saveData()
    }

    companion object {
        private const val KEY_EXAMS = "key_exams_lomedai_v2"
        private const val KEY_STATS = "key_stats_lomedai_v2"
    }
}
