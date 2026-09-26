package com.example.data.model

enum class PrePumpPhase(val label: String, val description: String) {
    WAIT("WAIT", "Aucune configuration intéressante"),
    COMPRESSION("COMPRESSION", "Volatilité comprimée (BB inside KC)"),
    SETUP("SETUP", "Compression + premiers signes d'activité"),
    PRE_PUMP("PRE-PUMP", "Volume + momentum + structure convergent"),
    ACCELERATION("ACCELERATION", "Augmentation rapide du volume et momentum"),
    TRIGGER("TRIGGER", "Breakout confirmé en cours"),
    PUMP("PUMP", "Mouvement explosif déjà engagé"),
    EXTENDED("EXTENDED", "Mouvement trop avancé (Anti-FOMO)")
}

enum class PrePumpClassification(val label: String, val minScore: Int, val maxScore: Int) {
    FAIBLE("FAIBLE", 0, 39),
    SURVEILLANCE("SURVEILLANCE", 40, 59),
    INTERESSANT("PRE-PUMP INTÉRESSANT", 60, 74),
    FORT("PRE-PUMP FORT", 75, 89),
    EXCEPTIONNEL("PRE-PUMP EXCEPTIONNEL", 90, 100)
}

data class TechnicalIndicators(
    val price: Double,
    val change1m: Double? = null,
    val change5m: Double? = null,
    val change15m: Double? = null,
    val change1h: Double? = null,
    val change4h: Double? = null,
    val change24h: Double = 0.0,
    val volume24h: Double = 0.0,
    val rvol: Double = 1.0,
    val rvolProgression: Double = 0.0, // % progression over last 3 candles
    val volatility: Double = 0.0,
    val atrPercent: Double = 0.0,
    val rsi: Double = 50.0,
    val rsiAcceleration: Double = 0.0, // delta over recent bars
    val roc: Double = 0.0,
    val ema20: Double = 0.0,
    val ema50: Double = 0.0,
    val distanceEma20Percent: Double = 0.0,
    val bbUpper: Double = 0.0,
    val bbMiddle: Double = 0.0,
    val bbLower: Double = 0.0,
    val bbWidthPercent: Double = 0.0,
    val kcUpper: Double = 0.0,
    val kcLower: Double = 0.0,
    val isSqueezeOn: Boolean = false,
    val squeezeDurationBars: Int = 0,
    val isBreakout: Boolean = false,
    val structureLabel: String = "Neutre",
    val buyerVolumePercent: Double? = null,
    val btcContext: String = "Range Neutre",
    val btcDominance: Double? = null
)

data class ScoreBreakdown(
    val volumeRvolScore: Int,        // max 25
    val volumeAccelerationScore: Int,// max 15
    val squeezeScore: Int,           // max 15
    val momentumScore: Int,          // max 15
    val structureScore: Int,         // max 10
    val atrExpansionScore: Int,      // max 5
    val breakoutScore: Int,          // max 5
    val btcContextScore: Int,        // max 5
    val extensionPenalty: Int,       // -10 to 0
    val totalScore: Int              // 0 to 100
)

data class CryptoCandidate(
    val symbol: String,
    val baseAsset: String,
    val quoteAsset: String = "USDT",
    val indicators: TechnicalIndicators,
    val scoreBreakdown: ScoreBreakdown,
    val phase: PrePumpPhase,
    val classification: PrePumpClassification,
    val confidence: String, // "Élevée", "Modérée", "Faible"
    val diagnosticPhrase: String,
    val riskPhrase: String,
    val timestamp: Long = System.currentTimeMillis()
)
