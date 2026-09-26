package com.example.engine

import com.example.data.model.CryptoCandidate
import com.example.data.model.PrePumpClassification
import com.example.data.model.PrePumpPhase
import com.example.data.model.ScoreBreakdown
import com.example.data.model.TechnicalIndicators
import kotlin.math.abs

object PrePumpCalculator {

    fun evaluateCandidate(
        symbol: String,
        baseAsset: String,
        quoteAsset: String = "USDT",
        indicators: TechnicalIndicators
    ): CryptoCandidate {
        val breakdown = computeScoreBreakdown(indicators)
        val phase = determinePhase(indicators, breakdown)
        val classification = determineClassification(breakdown.totalScore)
        val confidence = determineConfidence(indicators, breakdown, phase)
        val diagnostic = buildDiagnosticPhrase(indicators, breakdown, phase)
        val risk = buildRiskPhrase(indicators, breakdown)

        return CryptoCandidate(
            symbol = symbol,
            baseAsset = baseAsset,
            quoteAsset = quoteAsset,
            indicators = indicators,
            scoreBreakdown = breakdown,
            phase = phase,
            classification = classification,
            confidence = confidence,
            diagnosticPhrase = diagnostic,
            riskPhrase = risk
        )
    }

    private fun computeScoreBreakdown(ind: TechnicalIndicators): ScoreBreakdown {
        // 1. VOLUME / RVOL (max 25 points)
        val volumeRvolScore = when {
            ind.rvol >= 2.5 -> 25
            ind.rvol >= 2.0 -> 22
            ind.rvol >= 1.6 -> 18
            ind.rvol >= 1.3 -> 13
            ind.rvol >= 1.0 -> 8
            ind.rvol >= 0.8 -> 4
            else -> 1
        }

        // 2. ACCÉLÉRATION DU VOLUME (max 15 points)
        val volumeAccScore = when {
            ind.rvolProgression >= 40.0 -> 15
            ind.rvolProgression >= 25.0 -> 12
            ind.rvolProgression >= 15.0 -> 9
            ind.rvolProgression >= 5.0 -> 6
            ind.rvolProgression > -5.0 -> 2
            else -> 0
        }

        // 3. SQUEEZE BB/KC (max 15 points)
        val squeezeScore = when {
            ind.isSqueezeOn && ind.squeezeDurationBars >= 10 -> 15
            ind.isSqueezeOn && ind.squeezeDurationBars >= 5 -> 13
            ind.isSqueezeOn -> 10
            // Recent squeeze breakout (BB expanding outside KC)
            ind.bbWidthPercent in 2.0..5.5 && ind.rvol > 1.2 -> 12
            ind.bbWidthPercent < 4.0 -> 8
            ind.bbWidthPercent < 8.0 -> 4
            else -> 0 // Bands already blown wide open
        }

        // 4. MOMENTUM / ROC / RSI (max 15 points)
        // Rule: RSI 48 -> 53 -> 59 is much more interesting than an already overbought RSI 82!
        val momentumScore = when {
            ind.rsi in 50.0..64.0 && ind.rsiAcceleration > 4.0 && ind.roc > 1.0 -> 15
            ind.rsi in 46.0..68.0 && ind.rsiAcceleration > 1.5 -> 12
            ind.rsi in 42.0..70.0 && ind.roc > 0.0 -> 8
            ind.rsi > 75.0 -> 2 // Overbought FOMO warning
            ind.rsi < 35.0 -> 3 // Strong downtrend
            else -> 5
        }

        // 5. STRUCTURE DU PRIX (max 10 points)
        val structureScore = when {
            ind.structureLabel.contains("Higher Lows", ignoreCase = true) ||
            ind.structureLabel.contains("Compression Résistance", ignoreCase = true) -> 10
            ind.structureLabel.contains("Range Serré", ignoreCase = true) ||
            ind.structureLabel.contains("Consolidation", ignoreCase = true) -> 8
            ind.structureLabel.contains("Neutre", ignoreCase = true) -> 4
            ind.structureLabel.contains("Verticale", ignoreCase = true) -> 1
            else -> 4
        }

        // 6. ATR / EXPANSION (max 5 points)
        // Rule: Compression ATR that starts expanding with volume
        val atrScore = when {
            ind.atrPercent in 1.0..3.5 && ind.rvol > 1.2 -> 5
            ind.atrPercent < 2.5 -> 4
            ind.atrPercent in 3.5..6.0 -> 3
            ind.atrPercent > 9.0 -> 1 // High erratic volatility
            else -> 2
        }

        // 7. BREAKOUT (max 5 points)
        val breakoutScore = when {
            ind.isBreakout && ind.distanceEma20Percent in 0.5..3.5 -> 5
            ind.isBreakout -> 3
            ind.structureLabel.contains("Résistance", ignoreCase = true) -> 4
            else -> 1
        }

        // 8. CONTEXTE BTC (max 5 points)
        val btcScore = when {
            ind.btcContext.contains("Haussier", ignoreCase = true) ||
            ind.btcContext.contains("Stable", ignoreCase = true) -> 5
            ind.btcContext.contains("Range", ignoreCase = true) -> 4
            ind.btcContext.contains("Neutre", ignoreCase = true) -> 3
            ind.btcContext.contains("Correction", ignoreCase = true) -> 1
            else -> 2
        }

        // 9. PÉNALITÉ ANTI-FOMO (EXTENSION / RISQUE: -10 points max)
        var penalty = 0
        if (ind.distanceEma20Percent > 8.0) penalty -= 6
        else if (ind.distanceEma20Percent > 5.0) penalty -= 3
        else if (ind.distanceEma20Percent > 3.5) penalty -= 1

        if (ind.rsi > 78.0) penalty -= 5
        else if (ind.rsi > 73.0) penalty -= 2

        val change4h = ind.change4h ?: ind.change24h
        if (change4h > 20.0) penalty -= 6
        else if (change4h > 12.0) penalty -= 3

        if (ind.structureLabel.contains("Verticale", ignoreCase = true)) penalty -= 4

        val extensionPenalty = penalty.coerceIn(-10, 0)

        val rawTotal = volumeRvolScore + volumeAccScore + squeezeScore +
                momentumScore + structureScore + atrScore + breakoutScore +
                btcScore + extensionPenalty

        val totalScore = rawTotal.coerceIn(0, 100)

        return ScoreBreakdown(
            volumeRvolScore = volumeRvolScore,
            volumeAccelerationScore = volumeAccScore,
            squeezeScore = squeezeScore,
            momentumScore = momentumScore,
            structureScore = structureScore,
            atrExpansionScore = atrScore,
            breakoutScore = breakoutScore,
            btcContextScore = btcScore,
            extensionPenalty = extensionPenalty,
            totalScore = totalScore
        )
    }

    private fun determinePhase(ind: TechnicalIndicators, breakdown: ScoreBreakdown): PrePumpPhase {
        // Priority anti-FOMO check: if movement is already over-extended, it's EXTENDED or PUMP
        if (ind.distanceEma20Percent > 7.5 || ind.rsi > 77.0 || (ind.change1h ?: 0.0) > 12.0) {
            return if (ind.rvol > 2.5 && (ind.change1h ?: 0.0) > 10.0) PrePumpPhase.PUMP else PrePumpPhase.EXTENDED
        }

        // Pre-Pump: Volume + momentum + structure converging while still in compression/coiled spring (core target)
        if (breakdown.totalScore >= 60 && ind.isSqueezeOn && ind.distanceEma20Percent <= 4.0) {
            return PrePumpPhase.PRE_PUMP
        }

        // Trigger: Breakout confirmed on current bar with volume
        if (ind.isBreakout && ind.rvol >= 1.4 && ind.distanceEma20Percent in 0.5..4.5) {
            return PrePumpPhase.TRIGGER
        }

        // Acceleration: Rapid volume & momentum surge
        if (ind.rvol >= 1.8 && ind.rvolProgression > 15.0 && ind.rsi in 54.0..72.0) {
            return PrePumpPhase.ACCELERATION
        }

        // Setup: Compression + early signs of activity
        if (ind.isSqueezeOn && (ind.rvol > 1.1 || ind.rsiAcceleration > 1.5)) {
            return PrePumpPhase.SETUP
        }

        // Compression: Pure tight volatility squeeze
        if (ind.isSqueezeOn || ind.bbWidthPercent < 3.8) {
            return PrePumpPhase.COMPRESSION
        }

        // Wait: no setup
        return PrePumpPhase.WAIT
    }

    private fun determineClassification(score: Int): PrePumpClassification {
        return when {
            score >= 90 -> PrePumpClassification.EXCEPTIONNEL
            score >= 75 -> PrePumpClassification.FORT
            score >= 60 -> PrePumpClassification.INTERESSANT
            score >= 40 -> PrePumpClassification.SURVEILLANCE
            else -> PrePumpClassification.FAIBLE
        }
    }

    private fun determineConfidence(
        ind: TechnicalIndicators,
        breakdown: ScoreBreakdown,
        phase: PrePumpPhase
    ): String {
        return when {
            breakdown.totalScore >= 75 && (phase == PrePumpPhase.PRE_PUMP || phase == PrePumpPhase.ACCELERATION) -> "Élevée"
            breakdown.totalScore >= 60 -> "Modérée"
            else -> "Faible"
        }
    }

    private fun buildDiagnosticPhrase(
        ind: TechnicalIndicators,
        breakdown: ScoreBreakdown,
        phase: PrePumpPhase
    ): String {
        return when (phase) {
            PrePumpPhase.PRE_PUMP ->
                "Confluence optimale : compression BB/KC (${ind.squeezeDurationBars} bougies) couplée à une accélération de RVOL (${String.format("%.1f", ind.rvol)}x) et un RSI ascendant (${String.format("%.0f", ind.rsi)}) sans extension préalable."
            PrePumpPhase.ACCELERATION ->
                "Accélération volume/momentum confirmée (+${String.format("%.0f", ind.rvolProgression)}% RVOL) avec sortie ordonnée du range et maintien au-dessus de l'EMA20."
            PrePumpPhase.TRIGGER ->
                "Breakout de résistance en cours soutenu par un afflux d'acheteurs (${ind.buyerVolumePercent?.let { String.format("%.0f", it) } ?: "60"}%) et impulsion ROC positive."
            PrePumpPhase.SETUP ->
                "Phase d'accumulation détectée : compression de volatilité active avec début de divergence haussière sur le volume."
            PrePumpPhase.COMPRESSION ->
                "Forte compression des bandes de Bollinger à l'intérieur des canaux de Keltner ; énergie emmagasinée en attente de catalyseur volume."
            PrePumpPhase.PUMP ->
                "Mouvement impulsif déjà largement engagé (+${String.format("%.1f", ind.change24h)}%) : risque de FOMO élevé, attendre un retracement sain."
            PrePumpPhase.EXTENDED ->
                "Actif sur-étendu par rapport à l'EMA20 (+${String.format("%.1f", ind.distanceEma20Percent)}%) avec RSI élevé (${String.format("%.0f", ind.rsi)}) : pénalité anti-FOMO appliquée."
            PrePumpPhase.WAIT ->
                "Aucune confluence significative détectée : volume plat et dynamique de consolidation neutre."
        }
    }

    private fun buildRiskPhrase(ind: TechnicalIndicators, breakdown: ScoreBreakdown): String {
        return when {
            ind.distanceEma20Percent > 5.0 ->
                "Risque d'épuisement immédiat ou de pullback vers l'EMA20 suite à une extension trop rapide."
            ind.btcContext.contains("Correction", ignoreCase = true) ->
                "Fragilité du contexte BTC susceptible de rejeter le breakout des altcoins en cas de baisse soudaine."
            ind.rvol < 1.3 && ind.isBreakout ->
                "Volume insuffisant pour valider le franchissement de résistance ; risque élevé de bull-trap (faux breakout)."
            ind.isSqueezeOn && ind.roc < 0.0 ->
                "Sortie potentielle par le bas si les vendeurs reprennent la main sous le support clé."
            else ->
                "Perte du support EMA20 ou réintégration sous la borne basse de la compression invalidant la structure."
        }
    }

    fun formatDiagnosticReport(candidate: CryptoCandidate): String {
        val ind = candidate.indicators
        val b = candidate.scoreBreakdown
        val sb = StringBuilder()
        sb.append("ACTIF : ${candidate.symbol}\n")
        sb.append("PHASE : ${candidate.phase.label}\n")
        sb.append("SCORE : ${b.totalScore}/100 (${candidate.classification.label})\n")
        sb.append("CONFIANCE : ${candidate.confidence}\n\n")

        val buyerTxt = ind.buyerVolumePercent?.let { " (${String.format("%.0f", it)}% acheteurs)" } ?: ""
        sb.append("VOLUME : RVOL ${String.format("%.2f", ind.rvol)} (progression ${if (ind.rvolProgression >= 0) "+" else ""}${String.format("%.1f", ind.rvolProgression)}%)$buyerTxt\n")
        sb.append("MOMENTUM : RSI ${String.format("%.1f", ind.rsi)} (accél. ${if (ind.rsiAcceleration >= 0) "+" else ""}${String.format("%.1f", ind.rsiAcceleration)} pts), ROC ${if (ind.roc >= 0) "+" else ""}${String.format("%.2f", ind.roc)}%\n")

        val squeezeTxt = if (ind.isSqueezeOn) "Actif (${ind.squeezeDurationBars} bougies, largeur BB ${String.format("%.1f", ind.bbWidthPercent)}%)" else "Inactif (largeur BB ${String.format("%.1f", ind.bbWidthPercent)}%)"
        sb.append("SQUEEZE : $squeezeTxt\n")
        sb.append("STRUCTURE : ${ind.structureLabel}\n")
        sb.append("EXTENSION : Distance EMA20 ${if (ind.distanceEma20Percent >= 0) "+" else ""}${String.format("%.2f", ind.distanceEma20Percent)}% (Pénalité: ${b.extensionPenalty} pts)\n")
        sb.append("BTC : ${ind.btcContext}\n\n")
        sb.append("DIAGNOSTIC :\n")
        sb.append("${candidate.diagnosticPhrase}\n\n")
        sb.append("RISQUE :\n")
        sb.append("${candidate.riskPhrase}")

        return sb.toString()
    }
}
