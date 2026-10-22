package com.example.engine

import com.example.model.DailyPlanItem
import com.example.model.DifficultyLevel
import com.example.model.PlanTaskItem
import com.example.model.StudyMethod
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

object PlanGeneratorAlgorithm {

    /**
     * Determines the recommended study methodology according to the subject characteristics.
     */
    fun recommendMethod(subject: String): StudyMethod {
        val cleanSubject = subject.trim().lowercase(Locale.ROOT)
        return when {
            cleanSubject.contains("מתמטיק") || cleanSubject.contains("פיזיק") ||
                    cleanSubject.contains("אלגבר") || cleanSubject.contains("גיאומטרי") ||
                    cleanSubject.contains("חדוא") || cleanSubject.contains("הנדס") -> {
                StudyMethod.SPIRAL_PRACTICE
            }

            cleanSubject.contains("תנ\"ך") || cleanSubject.contains("תנך") ||
                    cleanSubject.contains("ספרות") || cleanSubject.contains("תורה") ||
                    cleanSubject.contains("הלכה") || cleanSubject.contains("יהדות") -> {
                StudyMethod.METHOD_123
            }

            cleanSubject.contains("אנגלי") || cleanSubject.contains("שפה") ||
                    cleanSubject.contains("לשון") || cleanSubject.contains("ערבית") ||
                    cleanSubject.contains("צרפתית") || cleanSubject.contains("עברית") -> {
                StudyMethod.ACTIVE_RECALL
            }

            cleanSubject.contains("היסטורי") || cleanSubject.contains("אזרחות") ||
                    cleanSubject.contains("גיאוגרפי") || cleanSubject.contains("מדעי החברה") -> {
                StudyMethod.TIMELINE_NARRATIVE
            }

            cleanSubject.contains("מחשב") || cleanSubject.contains("סייבר") ||
                    cleanSubject.contains("תכנות") || cleanSubject.contains("קוד") ||
                    cleanSubject.contains("אלגוריתמ") || cleanSubject.contains("רובוטיק") -> {
                StudyMethod.HANDS_ON_CODE
            }

            else -> StudyMethod.METHOD_123
        }
    }

    /**
     * Parses raw user topics text (delimited by newlines, commas, or semicolons).
     */
    fun parseUserTopics(rawText: String): List<String> {
        if (rawText.isBlank()) return emptyList()
        return rawText.split("\n", ",", ";")
            .map { it.trim().trimStart('-', '•', '*', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '.', ' ') }
            .filter { it.isNotBlank() }
    }

    /**
     * Generates a day-by-day tailored study plan from today until the exam date,
     * intelligently distributing user-provided topics throughout the timeline.
     */
    fun generatePlan(
        subject: String,
        examDateMillis: Long,
        difficulty: DifficultyLevel,
        dailyMinutesPreference: Int,
        selectedMethod: StudyMethod = recommendMethod(subject),
        rawTopicsText: String = ""
    ): List<DailyPlanItem> {
        val now = System.currentTimeMillis()
        val calendar = Calendar.getInstance().apply { timeInMillis = now }

        val diffMillis = examDateMillis - now
        var daysRemaining = kotlin.math.ceil(diffMillis.toDouble() / (1000.0 * 60.0 * 60.0 * 24.0)).toInt()
        if (daysRemaining < 1) daysRemaining = 1
        if (daysRemaining > 21) daysRemaining = 21

        val parsedTopics = parseUserTopics(rawTopicsText)
        val baseMinutes = (dailyMinutesPreference * difficulty.factor).toInt()
        val dateFormat = SimpleDateFormat("EEEE, d בMMMM", Locale("he", "IL"))

        val planList = mutableListOf<DailyPlanItem>()

        // Distribute user topics across days
        val dailyTopicMap = distributeTopicsAcrossDays(parsedTopics, daysRemaining)

        for (dayIdx in 1..daysRemaining) {
            val dayCalendar = Calendar.getInstance().apply {
                timeInMillis = now
                add(Calendar.DAY_OF_YEAR, dayIdx - 1)
            }
            val dateDisplay = dateFormat.format(dayCalendar.time)
            val progressRatio = (dayIdx.toFloat()) / daysRemaining.toFloat()

            val topicsForToday = dailyTopicMap[dayIdx] ?: emptyList()
            val topicContext = if (topicsForToday.isNotEmpty()) {
                topicsForToday.joinToString(", ")
            } else {
                "נושאי המפתח ב$subject"
            }

            val (stageTitle, tasks, tip) = when (selectedMethod) {
                StudyMethod.METHOD_123 -> generateMethod123Day(dayIdx, daysRemaining, progressRatio, subject, topicContext, topicsForToday)
                StudyMethod.SPIRAL_PRACTICE -> generateSpiralDay(dayIdx, daysRemaining, progressRatio, subject, topicContext, topicsForToday)
                StudyMethod.ACTIVE_RECALL -> generateActiveRecallDay(dayIdx, daysRemaining, progressRatio, subject, topicContext, topicsForToday)
                StudyMethod.FEYNMAN_TECHNIQUE -> generateFeynmanDay(dayIdx, daysRemaining, progressRatio, subject, topicContext, topicsForToday)
                StudyMethod.POMODORO_FOCUSED -> generatePomodoroDay(dayIdx, daysRemaining, progressRatio, subject, topicContext, topicsForToday)
                StudyMethod.TIMELINE_NARRATIVE -> generateTimelineDay(dayIdx, daysRemaining, progressRatio, subject, topicContext, topicsForToday)
                StudyMethod.HANDS_ON_CODE -> generateCodingDay(dayIdx, daysRemaining, progressRatio, subject, topicContext, topicsForToday)
            }

            val dayMinutes = if (dayIdx == daysRemaining && daysRemaining > 1) {
                (baseMinutes * 0.65f).toInt().coerceAtLeast(30)
            } else {
                baseMinutes
            }

            planList.add(
                DailyPlanItem(
                    id = UUID.randomUUID().toString(),
                    dayNumber = dayIdx,
                    dateDisplay = dateDisplay,
                    stageTitle = stageTitle,
                    allocatedMinutes = dayMinutes,
                    tasks = tasks.map { title ->
                        PlanTaskItem(id = UUID.randomUUID().toString(), title = title, isDone = false)
                    },
                    tipOfTheDay = tip,
                    dailyTopics = topicsForToday,
                    isCompleted = false
                )
            )
        }

        return planList
    }

    /**
     * Maps days (1..totalDays) to subsets of user topics.
     */
    private fun distributeTopicsAcrossDays(
        topics: List<String>,
        totalDays: Int
    ): Map<Int, List<String>> {
        val result = mutableMapOf<Int, MutableList<String>>()
        for (d in 1..totalDays) {
            result[d] = mutableListOf()
        }

        if (topics.isEmpty()) return result

        if (topics.size <= totalDays) {
            // Allocate topics across days, keeping the final day for integrated review
            val studyDaysCount = if (totalDays > 1) totalDays - 1 else 1
            for (i in topics.indices) {
                val assignedDay = (i % studyDaysCount) + 1
                result[assignedDay]?.add(topics[i])
            }
            if (totalDays > 1) {
                // Final day reviews all topics
                result[totalDays]?.addAll(topics.take(3))
            }
        } else {
            // More topics than days: group topics proportionally
            topics.forEachIndexed { index, topic ->
                val assignedDay = ((index.toFloat() / topics.size) * totalDays).toInt() + 1
                val boundedDay = assignedDay.coerceIn(1, totalDays)
                result[boundedDay]?.add(topic)
            }
        }

        return result
    }

    private fun generateMethod123Day(
        day: Int,
        totalDays: Int,
        ratio: Float,
        subject: String,
        topicContext: String,
        topics: List<String>
    ): Triple<String, List<String>, String> {
        val topicMention = if (topics.isNotEmpty()) "בנושא: $topicContext" else "ב$subject"
        return when {
            ratio <= 0.35f -> {
                Triple(
                    "שלב 1: הבנה ומיפוי ($topicContext)",
                    listOf(
                        "קריאה סדורה וסימון מושגי מפתח $topicMention",
                        "בניית שלד סיכום ממוקד לדפי עוגן עבור $topicContext",
                        "הסבר תמציתי בעל פה של עיקרי החומר $topicMention"
                    ),
                    "טיפ הבנה: קריאה פעילה עם סימון מונחי יסוד משפרת את הזיכרון ב-50% לעומת קריאה סבילה."
                )
            }
            ratio <= 0.75f -> {
                Triple(
                    "שלב 2: שינון פעיל ותרגול מחוון ($topicContext)",
                    listOf(
                        "פתרון 3 שאלות בגרות מתוך $topicContext לפי מחוון רשמי",
                        "בדיקה עצמית עם מרקר: טיעון, נימוק ודוגמה מדויקת $topicMention",
                        "מעבר מהיר על יומן מושגים שדורשים חיזוק ב-$topicContext"
                    ),
                    "טיפ מחוון: בודקי הבחינה מחפשים מילות מפתח מפורשות - ודא ששיבצת אותן בתשובתך!"
                )
            }
            else -> {
                Triple(
                    "שלב 3: סימולציה ובדיקת פערים מסכמת",
                    listOf(
                        "סימולציה מתוזמנת של שאלות מורכבות המשלבות את $topicContext",
                        "ליטוש נקודות תורפה וסגירת פערים אחרונה ב-$subject",
                        "הכנת ציוד ומנוחה טובה לקראת יום המבחן"
                    ),
                    "טיפ לילה לפני: המוח מבסס את הידע בזמן שינה עמוקה. אל תוותר על שנת לילה מלאה!"
                )
            }
        }
    }

    private fun generateSpiralDay(
        day: Int,
        totalDays: Int,
        ratio: Float,
        subject: String,
        topicContext: String,
        topics: List<String>
    ): Triple<String, List<String>, String> {
        return when {
            ratio <= 0.35f -> {
                Triple(
                    "שלב 1: עקרונות יסוד ונוסחאות ($topicContext)",
                    listOf(
                        "מעבר ממוקד על נוסחאות ועקרונות בסיס ב-$topicContext",
                        "פתרון 3 תרגילי מבוא מדגימים בנושא $topicContext",
                        "סימון מקרים מיוחדים ומכשולים אופייניים ב-$topicContext"
                    ),
                    "טיפ ספירלי: הבנת שורש הנוסחה מקלה על זכירתה בשעת מבחן ללא צורך בשינון מכני."
                )
            }
            ratio <= 0.75f -> {
                Triple(
                    "שלב 2: תרגול ספירלי ברמת קושי עולה ($topicContext)",
                    listOf(
                        "פתרון 4 שאלות בגרות מעורבות בנושא $topicContext",
                        "יומן שגיאות: תיעוד כל טעות אלגברית או הנחה מוטעית ב-$topicContext",
                        "חזרה על תרגיל שהסתבכת איתו אתמול עד לפתרון נקי וחלק"
                    ),
                    "טיפ שגיאות: יומן שגיאות הוא הכלי היעיל ביותר למניעת נפילות חוזרות באותו סעיף."
                )
            }
            else -> {
                Triple(
                    "שלב 3: מרתון בחינות מתוזמן",
                    listOf(
                        "פתרון פרק בחינה מלא הכולל את $topicContext תחת זמן מדוד",
                        "בדיקה מדוקדקת מול פתרונות רשמיים ורישום ציונים",
                        "חזרה קלילה על דפי נוסחאות ורגיעה לקראת המבחן"
                    ),
                    "טיפ מבחן: התחל תמיד מהשאלות שאתה הכי שולט בהן כדי לצבור ביטחון מוקדם."
                )
            }
        }
    }

    private fun generateActiveRecallDay(
        day: Int,
        totalDays: Int,
        ratio: Float,
        subject: String,
        topicContext: String,
        topics: List<String>
    ): Triple<String, List<String>, String> {
        return when {
            ratio <= 0.35f -> {
                Triple(
                    "שלב 1: כרטיסיות ושליפה עיוורת ($topicContext)",
                    listOf(
                        "בניית כרטיסיות זיכרון עבור 20 מונחים/חוקים עיקריים מתוך $topicContext",
                        "אימון שליפה עיוורת: מבחן עצמי ללא מבט בתשובה",
                        "הפרדת הכרטיסיות הקשות לחזרה כפולה בשיטת לייטנר"
                    ),
                    "טיפ Active Recall: המאמץ לשלוף מידע מהזיכרון בונה קשרים עצביים חזקים פי 3 מקריאה חוזרת."
                )
            }
            ratio <= 0.75f -> {
                Triple(
                    "שלב 2: יישום וחזרה מרווחת (Spaced Repetition)",
                    listOf(
                        "פתרון תרגול יישומי / אנסין מלא הכולל את $topicContext",
                        "סבב שליפה נוסף של הכרטיסיות המסומנות בנושא $topicContext",
                        "כתיבת פסקה מסכמת תוך שימוש בלפחות 5 מושגים חדשים"
                    ),
                    "טיפ מרווח: חזרה בדיוק כשהמידע מתחיל להישכח היא שמעבירה אותו לזיכרון לטווח ארוך."
                )
            }
            else -> {
                Triple(
                    "שלב 3: סימולציית שליפה מהירה וסגירה",
                    listOf(
                        "מבחן שליפה מהיר על כלל המושגים והנושאים שנלמדו",
                        "תרגול מודל מלא בזמן אמת וסריקת תבניות תשובה",
                        "הורדת עומס, שתייה מרובה ושינה טובה"
                    ),
                    "טיפ בחינה: קרא קודם את שאלות המבחן ורק אז גש לטקסט או לשאלה המלאה."
                )
            }
        }
    }

    private fun generateFeynmanDay(
        day: Int,
        totalDays: Int,
        ratio: Float,
        subject: String,
        topicContext: String,
        topics: List<String>
    ): Triple<String, List<String>, String> {
        return when {
            ratio <= 0.35f -> {
                Triple(
                    "טכניקת פיינמן: הבנה יסודית והסבר ($topicContext)",
                    listOf(
                        "לימוד מעמיק של $topicContext וזיהוי הרעיון המרכזי",
                        "הסבר את הנושא בקול רם במילים פשוטות כאילו מול תלמיד יסודי",
                        "סימון הנקודות שבהן נתקעת או השתמשת בז'רגון מורכב"
                    ),
                    "עיקרון פיינמן: אם אינך יכול להסביר את החומר בפשטות, עדיין לא הפנמת אותו במלואו."
                )
            }
            ratio <= 0.75f -> {
                Triple(
                    "טכניקת פיינמן: סגירת פערי הבנה ופישוט",
                    listOf(
                        "חזרה לספר/סיכומים עבור הפערים שהתגלו ב-$topicContext",
                        "ניסוח אנלוגיה יומיומית מוחשית להסבר הנושא $topicContext",
                        "פתרון 3 שאלות עומק המאתגרות את ההבנה היסודית"
                    ),
                    "עיקרון אנלוגיה: שימוש במשלים ודוגמאות מהעולם האמיתי מקבע את ההבנה בזיכרון."
                )
            }
            else -> {
                Triple(
                    "טכניקת פיינמן: מבחן ההוראה וסימולציה",
                    listOf(
                        "העברת 'הרצאה' מסכמת של 10 דקות על כלל הנושאים",
                        "מענה על שאלות מבחן מורכבות תוך נימוק ברור ונהיר",
                        "הכנה מנטלית ומנוחה לקראת יום המבחן"
                    ),
                    "טיפ סיום: מי שמבין את החומר ברמת פיינמן יתמודד בהצלחה עם כל שאלת חשיבה חדשה!"
                )
            }
        }
    }

    private fun generatePomodoroDay(
        day: Int,
        totalDays: Int,
        ratio: Float,
        subject: String,
        topicContext: String,
        topics: List<String>
    ): Triple<String, List<String>, String> {
        return when {
            ratio <= 0.35f -> {
                Triple(
                    "פומודורו ממוקד: סבב ראשוני ($topicContext)",
                    listOf(
                        "סשן פומודורו 1 (25 דק'): סקירת חומר מרוכזת בנושא $topicContext",
                        "הפסקה של 5 דקות: מתיחות, שתיית מים והתרחקות ממסכים",
                        "סשן פומודורו 2 (25 דק'): פתרון תרגול ראשוני וממוקד ב-$topicContext"
                    ),
                    "חוק פומודורו: הפסקה אמיתית היא ללא סמארטפון - תן לעיניים ולמוח מנוחה מוחלטת."
                )
            }
            ratio <= 0.75f -> {
                Triple(
                    "פומודורו ממוקד: סבב אינטנסיבי ($topicContext)",
                    listOf(
                        "סשן פומודורו 1 (25 דק'): פיצוח שאלות בגרות מאתגרות ב-$topicContext",
                        "סשן פומודורו 2 (25 דק'): בדיקה מדוקדקת ורישום תובנות ביומן שגיאות",
                        "סשן פומודורו 3 (25 דק'): חזרה ממוקדת על נקודות החולשה שהתגלו"
                    ),
                    "טיפ ריכוז: סילוק הסחות דעת במהלך ה-25 דקות מכפיל את תפוקת הלמידה שלך."
                )
            }
            else -> {
                Triple(
                    "פומודורו ממוקד: סימולציה ומנוחה מבוקרת",
                    listOf(
                        "סשן כפול (50 דק'): סימולציית מבחן רציפה על כלל הנושאים",
                        "סשן קצר (20 דק'): סקירת דגשים ונוסחאות אחרונות",
                        "מנוחה מוחלטת, שחרור מתחים והכנה מנטלית"
                    ),
                    "טיפ יום לפני: אל תלמד עד שעות מאוחרות. שינה טובה חיונית לפעילות המוח מחר!"
                )
            }
        }
    }

    private fun generateTimelineDay(
        day: Int,
        totalDays: Int,
        ratio: Float,
        subject: String,
        topicContext: String,
        topics: List<String>
    ): Triple<String, List<String>, String> {
        return when {
            ratio <= 0.35f -> {
                Triple(
                    "ציר זמנים ונרטיב: ($topicContext)",
                    listOf(
                        "שרטוט ציר זמן כרונולוגי של האירועים המרכזיים ב-$topicContext",
                        "חיבור בין אירועים: רישום 2 סיבות ו-2 תוצאות לכל אירוע מפתח",
                        "הגדרת מושגי היסוד ההיסטוריים הרלוונטיים ל-$topicContext"
                    ),
                    "טיפ נרטיבי: ראיית ההיסטוריה כסיפור עלילתי מסייעת בזכירת השתלשלות האירועים."
                )
            }
            ratio <= 0.75f -> {
                Triple(
                    "שאלות עומק והשוואות ($topicContext)",
                    listOf(
                        "פתרון 3 שאלות מקור (קטע היסטורי / קריקטורה) בנושא $topicContext",
                        "בניית טבלת השוואה בין תקופות או גישות שונות ב-$topicContext",
                        "תרגול כתיבת תשובה מובנית לפי מחוון (טענה -> ביסוס מן המקור -> מסקנה)"
                    ),
                    "טיפ מקור: שלב תמיד ציטוט ישיר מהקטע לביסוס הטיעון שלך."
                )
            }
            else -> {
                Triple(
                    "סימולציה מסכמת ובדיקת מחוון",
                    listOf(
                        "סימולציה מלאה של 2 שאלות גדולות בכתב יד ובזמן מדוד",
                        "בדיקה עצמית: האם ענית במדויק על מילת ההוראה (הסבר/הצג/השווה)?",
                        "מעבר מהיר על ציר הזמנים המסכם והורדת מתחים"
                    ),
                    "טיפ בחינה: שים לב להבדל בין 'הצג' (הגדרה תמציתית) לבין 'הסבר' (הרחבה וקישור)."
                )
            }
        }
    }

    private fun generateCodingDay(
        day: Int,
        totalDays: Int,
        ratio: Float,
        subject: String,
        topicContext: String,
        topics: List<String>
    ): Triple<String, List<String>, String> {
        return when {
            ratio <= 0.35f -> {
                Triple(
                    "מבני נתונים ואלגוריתמיקה ($topicContext)",
                    listOf(
                        "חזרה על מחלקות, מערכים ומבני נתונים רלוונטיים ל-$topicContext",
                        "כתיבת אלגוריתמי בסיס (סריקה, חיפוש, מניפולציה) מאפס",
                        "ניתוח סיבוכיות זמן וזיכרון עבור הפונקציות הנפוצות ב-$topicContext"
                    ),
                    "טיפ קוד: תמיד חשוב על מקרי קצה (מערך ריק, איבר יחיד, ערכים קיצוניים)."
                )
            }
            ratio <= 0.75f -> {
                Triple(
                    "מעקבים יבשים וכתיבה על דף ($topicContext)",
                    listOf(
                        "ביצוע 2 טבלאות מעקב יבש על קוד מורכב עם רקורסיה בנושא $topicContext",
                        "כתיבת פתרון שלם של שאלת בגרות על דף שורות (ללא מחשב ו-IDE!)",
                        "בדיקת הקוד שנכתב ביד ואימות תקינותו"
                    ),
                    "טיפ מעקב: בבחינה בכתב יד היכולת לעקוב בדיוק אחרי משתנים בראש היא המפתח לציון 100."
                )
            }
            else -> {
                Triple(
                    "סימולציית בחינה וסדר כתיבה",
                    listOf(
                        "פתרון בגרות מלאה משנה שעברה תחת הגבלת זמן מדויקת",
                        "הקפדה על מתן שמות משמעותיים למשתנים והערות ברורות בקוד",
                        "בדיקה של כללי תחביר ומעבר קל על דפי עזר"
                    ),
                    "טיפ בודק: כתיבה נקייה ומסודרת של קוד מעניקה נקודות רבות בבדיקת הבגרות."
                )
            }
        }
    }
}
