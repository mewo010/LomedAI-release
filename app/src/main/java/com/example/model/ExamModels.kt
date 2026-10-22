package com.example.model

enum class DifficultyLevel(
    val titleHe: String,
    val descriptionHe: String,
    val factor: Float
) {
    EASY("קל", "חזרה ממוקדת על עיקרי החומר", 0.8f),
    MEDIUM("בינוני", "איזון מקיף בין תיאוריה לתרגול", 1.0f),
    HARD("קשה / עמוס", "מרתון אינטנסיבי וסימולציות מרובות", 1.3f)
}

enum class StudyMethod(
    val titleHe: String,
    val badgeHe: String,
    val shortDescription: String,
    val fullStrategyHe: String,
    val iconName: String
) {
    METHOD_123(
        titleHe = "שיטת 1-2-3 (מבנה תלת-שלבי)",
        badgeHe = "שיטת 1-2-3",
        shortDescription = "מעולה לתנ\"ך, ספרות ומדעי הרוח",
        fullStrategyHe = "חלוקה לשלושה שלבים: הבנת חומר ומיפוי -> שינון פעיל ותרגול מחוון -> סימולציית מבחן עצמית.",
        iconName = "menu_book"
    ),
    SPIRAL_PRACTICE(
        titleHe = "תרגול ספירלי מדורג",
        badgeHe = "תרגול ספירלי",
        shortDescription = "התאמה מושלמת למתמטיקה ופיזיקה",
        fullStrategyHe = "פתרון תרגילים מדורג מקל למאתגר, חזרה מחזורית על שאלות בגרות וניתוח טעויות שכיחות.",
        iconName = "calculate"
    ),
    ACTIVE_RECALL(
        titleHe = "Active Recall & כרטיסיות זיכרון",
        badgeHe = "Active Recall",
        shortDescription = "מנצח באנגלית, לשון, מושגים ושפות",
        fullStrategyHe = "שליפה פעילה מהזיכרון ללא פתיחת סיכומים, חזרה מרווחת (Spaced Repetition) ובדיקת שליפה עיוורת.",
        iconName = "psychology"
    ),
    FEYNMAN_TECHNIQUE(
        titleHe = "טכניקת פיינמן (הסבר ופישוט)",
        badgeHe = "טכניקת פיינמן",
        shortDescription = "להבנה מעמיקה של נושאים מורכבים",
        fullStrategyHe = "הסבר כל נושא במילים פשוטות כאילו אתה מלמד אדם אחר, איתור פערי הבנה, ופישוט השפה עד להבנה מלאה.",
        iconName = "record_voice_over"
    ),
    POMODORO_FOCUSED(
        titleHe = "פומודורו ממוקד (אינטרוולים וריכוז)",
        badgeHe = "פומודורו ממוקד",
        shortDescription = "לשמירה על ריכוז שיא ומניעת שחיקה",
        fullStrategyHe = "חלוקת כל נושא למקטעי למידה אינטנסיביים של 25 דקות עם 5 דקות הפסקה, לשמירה על ערנות וחדות.",
        iconName = "timer"
    ),
    TIMELINE_NARRATIVE(
        titleHe = "ציר זמן וסיפור נרטיבי",
        badgeHe = "ציר זמנים",
        shortDescription = "אופטימלי להיסטוריה ואזרחות",
        fullStrategyHe = "ארגון כרונולוגי של סיבה ותוצאה, חיבור בין מונחים ואירועים היסטוריים, וכתיבת תשובות מובנות.",
        iconName = "history_edu"
    ),
    HANDS_ON_CODE(
        titleHe = "קוד פעיל ומעקב יבש",
        badgeHe = "Coding & Debug",
        shortDescription = "מכוון למדעי המחשב ותכנות",
        fullStrategyHe = "כתיבת אלגוריתמים מעשית, מעקבי טבלאות יבשים על דף ופתרון שאלות מבחני עבר תחת זמן.",
        iconName = "terminal"
    )
}

data class PlanTaskItem(
    val id: String,
    val title: String,
    var isDone: Boolean = false
)

data class DailyPlanItem(
    val id: String,
    val dayNumber: Int,
    val dateDisplay: String,
    val stageTitle: String,
    val allocatedMinutes: Int,
    val tasks: List<PlanTaskItem>,
    val tipOfTheDay: String,
    val dailyTopics: List<String> = emptyList(),
    var isCompleted: Boolean = false
)

data class Exam(
    val id: String,
    val subject: String,
    val customTitle: String = "",
    val examDateMillis: Long,
    val difficulty: DifficultyLevel,
    val dailyMinutes: Int = 60,
    val recommendedMethod: StudyMethod,
    val userTopics: List<String> = emptyList(),
    val planDays: List<DailyPlanItem> = emptyList(),
    val createdAtMillis: Long = System.currentTimeMillis()
) {
    val displayName: String
        get() = if (customTitle.isNotBlank()) customTitle else "מבחן ב$subject"

    val daysLeft: Int
        get() {
            val now = System.currentTimeMillis()
            val diffMillis = examDateMillis - now
            val days = kotlin.math.ceil(diffMillis.toDouble() / (1000.0 * 60.0 * 60.0 * 24.0)).toInt()
            return if (days < 0) 0 else days
        }

    val totalTasksCount: Int
        get() = planDays.sumOf { it.tasks.size }

    val completedTasksCount: Int
        get() = planDays.sumOf { day -> day.tasks.count { it.isDone } }

    val progressPercent: Float
        get() {
            val total = totalTasksCount
            return if (total == 0) 0f else completedTasksCount.toFloat() / total.toFloat()
        }
}

data class StudyStats(
    val totalMinutesStudied: Int = 0,
    val completedSessionsCount: Int = 0,
    val streakDays: Int = 0
)
