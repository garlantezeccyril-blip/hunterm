package com.example.engine

import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sqrt

object IndicatorCalculator {

    fun computeEma(prices: List<Double>, period: Int): List<Double> {
        if (prices.isEmpty() || period <= 0) return emptyList()
        val ema = ArrayList<Double>(prices.size)
        val k = 2.0 / (period + 1)
        
        var currentEma = prices.take(period.coerceAtMost(prices.size)).average()
        for (i in prices.indices) {
            if (i < period - 1) {
                ema.add(prices[i])
            } else if (i == period - 1) {
                ema.add(currentEma)
            } else {
                currentEma = (prices[i] * k) + (currentEma * (1 - k))
                ema.add(currentEma)
            }
        }
        return ema
    }

    fun computeSma(values: List<Double>, period: Int): List<Double> {
        if (values.size < period || period <= 0) return emptyList()
        val result = mutableListOf<Double>()
        var windowSum = values.take(period).sum()
        result.add(windowSum / period)

        for (i in period until values.size) {
            windowSum += values[i] - values[i - period]
            result.add(windowSum / period)
        }
        return result
    }

    fun computeRsi(prices: List<Double>, period: Int = 14): List<Double> {
        if (prices.size <= period) return emptyList()
        val rsiList = mutableListOf<Double>()

        var gainSum = 0.0
        var lossSum = 0.0

        for (i in 1..period) {
            val change = prices[i] - prices[i - 1]
            if (change > 0) gainSum += change else lossSum += abs(change)
        }

        var avgGain = gainSum / period
        var avgLoss = lossSum / period

        var rs = if (avgLoss == 0.0) 100.0 else avgGain / avgLoss
        rsiList.add(100.0 - (100.0 / (1.0 + rs)))

        for (i in (period + 1) until prices.size) {
            val change = prices[i] - prices[i - 1]
            val gain = if (change > 0) change else 0.0
            val loss = if (change < 0) abs(change) else 0.0

            avgGain = (avgGain * (period - 1) + gain) / period
            avgLoss = (avgLoss * (period - 1) + loss) / period

            rs = if (avgLoss == 0.0) 100.0 else avgGain / avgLoss
            val rsiVal = 100.0 - (100.0 / (1.0 + rs))
            rsiList.add(rsiVal)
        }
        return rsiList
    }

    fun computeBollingerBands(
        prices: List<Double>,
        period: Int = 20,
        k: Double = 2.0
    ): Triple<List<Double>, List<Double>, List<Double>> {
        if (prices.size < period) return Triple(emptyList(), emptyList(), emptyList())
        val middle = mutableListOf<Double>()
        val upper = mutableListOf<Double>()
        val lower = mutableListOf<Double>()

        for (i in period - 1 until prices.size) {
            val sub = prices.subList(i - period + 1, i + 1)
            val mean = sub.average()
            val variance = sub.map { (it - mean) * (it - mean) }.average()
            val stdDev = sqrt(variance)

            middle.add(mean)
            upper.add(mean + (k * stdDev))
            lower.add(mean - (k * stdDev))
        }
        return Triple(upper, middle, lower)
    }

    fun computeAtr(
        highs: List<Double>,
        lows: List<Double>,
        closes: List<Double>,
        period: Int = 14
    ): List<Double> {
        val size = minOf(highs.size, lows.size, closes.size)
        if (size <= period) return emptyList()

        val trueRanges = mutableListOf<Double>()
        trueRanges.add(highs[0] - lows[0])

        for (i in 1 until size) {
            val tr1 = highs[i] - lows[i]
            val tr2 = abs(highs[i] - closes[i - 1])
            val tr3 = abs(lows[i] - closes[i - 1])
            trueRanges.add(max(tr1, max(tr2, tr3)))
        }

        val atrList = mutableListOf<Double>()
        var currentAtr = trueRanges.take(period).average()
        atrList.add(currentAtr)

        for (i in period until trueRanges.size) {
            currentAtr = ((currentAtr * (period - 1)) + trueRanges[i]) / period
            atrList.add(currentAtr)
        }
        return atrList
    }

    fun computeKeltnerChannels(
        emas: List<Double>,
        atrs: List<Double>,
        multiplier: Double = 1.5
    ): Pair<List<Double>, List<Double>> {
        val count = minOf(emas.size, atrs.size)
        val upper = mutableListOf<Double>()
        val lower = mutableListOf<Double>()

        val emaOffset = emas.size - count
        val atrOffset = atrs.size - count

        for (i in 0 until count) {
            val ema = emas[emaOffset + i]
            val atr = atrs[atrOffset + i]
            upper.add(ema + (multiplier * atr))
            lower.add(ema - (multiplier * atr))
        }
        return Pair(upper, lower)
    }

    fun computeRoc(prices: List<Double>, period: Int = 9): Double {
        if (prices.size <= period) return 0.0
        val current = prices.last()
        val past = prices[prices.size - 1 - period]
        if (past == 0.0) return 0.0
        return ((current - past) / past) * 100.0
    }

    fun computeRvol(volumes: List<Double>, period: Int = 20): Pair<Double, Double> {
        if (volumes.size < period) return Pair(1.0, 0.0)
        val currentVol = volumes.last()
        val baseline = volumes.dropLast(1).takeLast(period).average()
        val rvol = if (baseline > 0.0) currentVol / baseline else 1.0

        // RVOL progression over last 3 candles
        val prog = if (volumes.size >= 3 && volumes[volumes.size - 3] > 0.0) {
            ((volumes.last() - volumes[volumes.size - 3]) / volumes[volumes.size - 3]) * 100.0
        } else 0.0

        return Pair(rvol, prog)
    }
}
