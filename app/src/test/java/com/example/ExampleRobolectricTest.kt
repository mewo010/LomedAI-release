package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.engine.PlanGeneratorAlgorithm
import com.example.model.DifficultyLevel
import com.example.model.StudyMethod
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.Calendar

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context matches lomedAi`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("lomedAi", appName)
  }

  @Test
  fun `plan generator recommends spiral practice for math`() {
    val method = PlanGeneratorAlgorithm.recommendMethod("מתמטיקה 5 יח\"ל")
    assertEquals(StudyMethod.SPIRAL_PRACTICE, method)
  }

  @Test
  fun `plan generator recommends 1-2-3 method for tanakh`() {
    val method = PlanGeneratorAlgorithm.recommendMethod("תנ\"ך")
    assertEquals(StudyMethod.METHOD_123, method)
  }

  @Test
  fun `parse user topics correctly from raw text`() {
    val raw = "פרקים י\"ב-ט\"ו\nטריגונומטריה במרחב, חקירת פונקציות; וקטורים"
    val parsed = PlanGeneratorAlgorithm.parseUserTopics(raw)
    assertEquals(4, parsed.size)
    assertTrue(parsed.contains("פרקים י\"ב-ט\"ו"))
    assertTrue(parsed.contains("טריגונומטריה במרחב"))
    assertTrue(parsed.contains("חקירת פונקציות"))
    assertTrue(parsed.contains("וקטורים"))
  }

  @Test
  fun `plan generator distributes user topics across days`() {
    val targetDate = Calendar.getInstance().apply {
      add(Calendar.DAY_OF_YEAR, 4)
    }.timeInMillis

    val plan = PlanGeneratorAlgorithm.generatePlan(
      subject = "תנ\"ך",
      examDateMillis = targetDate,
      difficulty = DifficultyLevel.MEDIUM,
      dailyMinutesPreference = 60,
      selectedMethod = StudyMethod.METHOD_123,
      rawTopicsText = "מלכים א' פרק א'-ד'\nמלכים א' פרק ה'-י'\nמלכים ב' פרק י\"ז"
    )

    assertTrue("Plan should contain days", plan.isNotEmpty())
    // At least one day has the specified topic assigned
    val hasTopics = plan.any { it.dailyTopics.isNotEmpty() }
    assertTrue("Should distribute topics into days", hasTopics)
  }
}
