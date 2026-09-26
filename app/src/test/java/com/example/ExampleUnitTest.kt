package com.example

import com.example.data.model.PrePumpPhase
import com.example.data.model.TechnicalIndicators
import com.example.engine.IndicatorCalculator
import com.example.engine.PrePumpCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testIndicatorCalculator_RSI_and_EMA() {
        val prices = listOf(
            10.0, 10.2, 10.5, 10.4, 10.6, 10.8, 11.0, 10.9, 11.2, 11.5,
            11.8, 12.0, 12.2, 12.5, 12.8, 13.0, 13.2, 13.5, 13.8, 14.0
        )
        val ema = IndicatorCalculator.computeEma(prices, 10)
        assertTrue(ema.isNotEmpty())
        assertTrue(ema.last() in 10.0..14.0)

        val rsi = IndicatorCalculator.computeRsi(prices, 14)
        assertTrue(rsi.isNotEmpty())
        assertTrue(rsi.last() in 0.0..100.0)
    }

    @Test
    fun testPrePumpCalculator_PrePumpConfluence() {
        val ind = TechnicalIndicators(
            price = 5.0,
            rvol = 2.4,
            rvolProgression = 35.0,
            rsi = 56.0,
            rsiAcceleration = 8.0,
            roc = 2.2,
            distanceEma20Percent = 1.2,
            isSqueezeOn = true,
            squeezeDurationBars = 12,
            bbWidthPercent = 3.2,
            structureLabel = "Higher Lows + Compression",
            isBreakout = true,
            buyerVolumePercent = 65.0
        )

        val candidate = PrePumpCalculator.evaluateCandidate("TESTUSDT", "TEST", "USDT", ind)
        assertTrue("Score should be high for textbook pre-pump", candidate.scoreBreakdown.totalScore >= 70)
        assertEquals(PrePumpPhase.PRE_PUMP, candidate.phase)
        assertTrue("Diagnostic should follow rules", candidate.diagnosticPhrase.isNotBlank())
    }

    @Test
    fun testPrePumpCalculator_AntiFomoPenalty() {
        val ind = TechnicalIndicators(
            price = 100.0,
            rvol = 4.5,
            rvolProgression = 90.0,
            rsi = 84.0,
            roc = 18.0,
            distanceEma20Percent = 14.0,
            change1h = 16.0,
            isSqueezeOn = false,
            bbWidthPercent = 35.0,
            structureLabel = "Hausse Verticale (FOMO)"
        )

        val candidate = PrePumpCalculator.evaluateCandidate("FOMOUSDT", "FOMO", "USDT", ind)
        assertTrue("Anti-FOMO penalty must be applied", candidate.scoreBreakdown.extensionPenalty < 0)
        assertEquals(PrePumpPhase.PUMP, candidate.phase)
    }
}
