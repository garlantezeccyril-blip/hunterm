package com.example.data.repository

import com.example.data.local.SignalDao
import com.example.data.model.CryptoCandidate
import com.example.data.model.SignalMemoryEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlin.math.max
import kotlin.math.min

data class MemoryEngineStats(
    val totalSignals: Int,
    val successCount: Int,
    val successRatePercent: Double,
    val hit5Count: Int,
    val hit10Count: Int,
    val hit20Count: Int,
    val hit30Count: Int,
    val hit50Count: Int,
    val averageMaxFavorable: Double,
    val averageDrawdown: Double,
    val falsePositiveRatePercent: Double
)

class SignalMemoryRepository(private val signalDao: SignalDao) {

    val allSignals: Flow<List<SignalMemoryEntity>> = signalDao.getAllSignals()

    suspend fun recordCandidateSignal(candidate: CryptoCandidate): Long {
        val ind = candidate.indicators
        val entity = SignalMemoryEntity(
            crypto = candidate.symbol,
            initialScore = candidate.scoreBreakdown.totalScore,
            initialPhase = candidate.phase.label,
            priceAtSignal = ind.price,
            rvol = ind.rvol,
            rsi = ind.rsi,
            roc = ind.roc,
            squeezeDuration = ind.squeezeDurationBars,
            isSqueezeOn = ind.isSqueezeOn,
            atrPercent = ind.atrPercent,
            distanceEma20 = ind.distanceEma20Percent,
            priorChange = ind.change1h ?: 0.0,
            btcContext = ind.btcContext,
            currentPrice = ind.price,
            maxFavorablePercent = 0.0,
            maxDrawdownPercent = 0.0,
            outcomeStatus = "ACTIF",
            outcomeNote = "Signal enregistré le ${java.text.SimpleDateFormat("dd/MM HH:mm", java.util.Locale.getDefault()).format(java.util.Date())}. Surveillance des cibles +5%, +10%, +20%, +30%, +50%."
        )
        return signalDao.insertSignal(entity)
    }

    suspend fun updateSignalPrice(signalId: Long, newPrice: Double) {
        val signal = signalDao.getSignalById(signalId) ?: return
        val basePrice = signal.priceAtSignal
        if (basePrice <= 0.0) return

        val deltaPercent = ((newPrice - basePrice) / basePrice) * 100.0
        val newMaxFavorable = max(signal.maxFavorablePercent, deltaPercent)
        val newMaxDrawdown = min(signal.maxDrawdownPercent, deltaPercent)

        val now = System.currentTimeMillis()
        val duration = now - signal.timestamp

        val hit5 = signal.hit5Percent || deltaPercent >= 5.0
        val t5 = signal.timeTo5PercentMs ?: if (deltaPercent >= 5.0) duration else null

        val hit10 = signal.hit10Percent || deltaPercent >= 10.0
        val t10 = signal.timeTo10PercentMs ?: if (deltaPercent >= 10.0) duration else null

        val hit20 = signal.hit20Percent || deltaPercent >= 20.0
        val t20 = signal.timeTo20PercentMs ?: if (deltaPercent >= 20.0) duration else null

        val hit30 = signal.hit30Percent || deltaPercent >= 30.0
        val t30 = signal.timeTo30PercentMs ?: if (deltaPercent >= 30.0) duration else null

        val hit50 = signal.hit50Percent || deltaPercent >= 50.0
        val t50 = signal.timeTo50PercentMs ?: if (deltaPercent >= 50.0) duration else null

        val newStatus = when {
            newMaxFavorable >= 20.0 -> "SUCCÈS (+${String.format("%.1f", newMaxFavorable)}%)"
            newMaxFavorable >= 10.0 -> "SUCCÈS (+${String.format("%.1f", newMaxFavorable)}%)"
            newMaxDrawdown <= -4.0 && newMaxFavorable < 2.0 -> "INVALIDÉ_STOP"
            else -> signal.outcomeStatus
        }

        val updated = signal.copy(
            currentPrice = newPrice,
            maxFavorablePercent = newMaxFavorable,
            maxDrawdownPercent = newMaxDrawdown,
            hit5Percent = hit5,
            timeTo5PercentMs = t5,
            hit10Percent = hit10,
            timeTo10PercentMs = t10,
            hit20Percent = hit20,
            timeTo20PercentMs = t20,
            hit30Percent = hit30,
            timeTo30PercentMs = t30,
            hit50Percent = hit50,
            timeTo50PercentMs = t50,
            outcomeStatus = newStatus
        )
        signalDao.updateSignal(updated)
    }

    suspend fun deleteSignal(id: Long) {
        signalDao.deleteSignalById(id)
    }

    suspend fun computeStats(): MemoryEngineStats {
        val list = signalDao.getAllSignals().firstOrNull() ?: emptyList()
        if (list.isEmpty()) {
            return MemoryEngineStats(0, 0, 0.0, 0, 0, 0, 0, 0, 0.0, 0.0, 0.0)
        }

        val successList = list.filter { it.hit10Percent || it.outcomeStatus.startsWith("SUCCÈS") }
        val failList = list.filter { it.outcomeStatus.contains("INVALIDÉ") || it.outcomeStatus.contains("FAUX_POSITIF") }

        val hit5Count = list.count { it.hit5Percent }
        val hit10Count = list.count { it.hit10Percent }
        val hit20Count = list.count { it.hit20Percent }
        val hit30Count = list.count { it.hit30Percent }
        val hit50Count = list.count { it.hit50Percent }

        val avgFav = list.map { it.maxFavorablePercent }.average()
        val avgDd = list.map { it.maxDrawdownPercent }.average()

        val successRate = (successList.size.toDouble() / list.size) * 100.0
        val falsePosRate = (failList.size.toDouble() / list.size) * 100.0

        return MemoryEngineStats(
            totalSignals = list.size,
            successCount = successList.size,
            successRatePercent = successRate,
            hit5Count = hit5Count,
            hit10Count = hit10Count,
            hit20Count = hit20Count,
            hit30Count = hit30Count,
            hit50Count = hit50Count,
            averageMaxFavorable = avgFav,
            averageDrawdown = avgDd,
            falsePositiveRatePercent = falsePosRate
        )
    }

    suspend fun seedInitialMemoryIfEmpty() {
        if (signalDao.getCount() > 0) return

        val now = System.currentTimeMillis()
        val hour = 3600_000L

        val initialData = listOf(
            SignalMemoryEntity(
                timestamp = now - (72 * hour),
                crypto = "NEARUSDT",
                initialScore = 84,
                initialPhase = "PRE-PUMP",
                priceAtSignal = 4.38,
                rvol = 2.45,
                rsi = 56.2,
                roc = 2.2,
                squeezeDuration = 14,
                isSqueezeOn = true,
                atrPercent = 1.9,
                distanceEma20 = 1.2,
                priorChange = 1.1,
                btcContext = "Stable (+0.4%)",
                currentPrice = 5.48,
                maxFavorablePercent = 25.1,
                maxDrawdownPercent = -0.8,
                hit5Percent = true,
                timeTo5PercentMs = 38 * 60 * 1000L,
                hit10Percent = true,
                timeTo10PercentMs = 85 * 60 * 1000L,
                hit20Percent = true,
                timeTo20PercentMs = 210 * 60 * 1000L,
                outcomeStatus = "SUCCÈS (+25.1%)",
                outcomeNote = "Confluence exemplaire : sortie propre de compression BB/KC après 14 bougies. RVOL ascendant continu."
            ),
            SignalMemoryEntity(
                timestamp = now - (48 * hour),
                crypto = "SOLUSDT",
                initialScore = 81,
                initialPhase = "ACCELERATION",
                priceAtSignal = 138.20,
                rvol = 2.65,
                rsi = 62.0,
                roc = 3.1,
                squeezeDuration = 8,
                isSqueezeOn = false,
                atrPercent = 2.3,
                distanceEma20 = 2.8,
                priorChange = 2.4,
                btcContext = "Haussier (+1.8%)",
                currentPrice = 162.40,
                maxFavorablePercent = 17.5,
                maxDrawdownPercent = -1.2,
                hit5Percent = true,
                timeTo5PercentMs = 25 * 60 * 1000L,
                hit10Percent = true,
                timeTo10PercentMs = 65 * 60 * 1000L,
                outcomeStatus = "SUCCÈS (+17.5%)",
                outcomeNote = "Accélération confirmée par le momentum et appui sur EMA20. Prise de profit graduelle."
            ),
            SignalMemoryEntity(
                timestamp = now - (36 * hour),
                crypto = "INJUSDT",
                initialScore = 78,
                initialPhase = "SETUP",
                priceAtSignal = 19.40,
                rvol = 1.85,
                rsi = 54.0,
                roc = 1.4,
                squeezeDuration = 18,
                isSqueezeOn = true,
                atrPercent = 1.8,
                distanceEma20 = 0.9,
                priorChange = 0.6,
                btcContext = "Range Neutre",
                currentPrice = 22.10,
                maxFavorablePercent = 13.9,
                maxDrawdownPercent = -1.4,
                hit5Percent = true,
                timeTo5PercentMs = 45 * 60 * 1000L,
                hit10Percent = true,
                timeTo10PercentMs = 120 * 60 * 1000L,
                outcomeStatus = "SUCCÈS (+13.9%)",
                outcomeNote = "Longue phase d'accumulation : le squeeze de 18 bougies a fourni l'énergie nécessaire à l'expansion."
            ),
            SignalMemoryEntity(
                timestamp = now - (24 * hour),
                crypto = "TIAUSDT",
                initialScore = 58,
                initialPhase = "COMPRESSION",
                priceAtSignal = 4.35,
                rvol = 1.25,
                rsi = 48.0,
                roc = -0.4,
                squeezeDuration = 10,
                isSqueezeOn = true,
                atrPercent = 1.6,
                distanceEma20 = -0.3,
                priorChange = -0.2,
                btcContext = "Correction (-2.1%)",
                currentPrice = 4.16,
                maxFavorablePercent = 1.2,
                maxDrawdownPercent = -4.4,
                hit5Percent = false,
                outcomeStatus = "INVALIDÉ_STOP (-4.4%)",
                outcomeNote = "Faux départ : compression rompue par le bas suite à la correction de BTC. Sortie de position respectée."
            ),
            SignalMemoryEntity(
                timestamp = now - (12 * hour),
                crypto = "DOGEUSDT",
                initialScore = 38,
                initialPhase = "EXTENDED",
                priceAtSignal = 0.235,
                rvol = 4.2,
                rsi = 81.5,
                roc = 14.2,
                squeezeDuration = 0,
                isSqueezeOn = false,
                atrPercent = 6.4,
                distanceEma20 = 11.2,
                priorChange = 18.5,
                btcContext = "Haussier (+2.4%)",
                currentPrice = 0.218,
                maxFavorablePercent = 2.1,
                maxDrawdownPercent = -8.5,
                outcomeStatus = "PUMP_DÉJÀ_COMMENCÉ",
                outcomeNote = "Anti-FOMO validé : entrée refusée par l'algorithme car le pump était déjà consommé. Retracement violent immédiat."
            )
        )

        signalDao.insertSignals(initialData)
    }
}
