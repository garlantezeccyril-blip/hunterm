package com.example.data.repository

import com.example.BuildConfig
import com.example.data.model.CryptoCandidate
import com.example.data.remote.ContentPayload
import com.example.data.remote.GeminiClient
import com.example.data.remote.GeminiRequest
import com.example.data.remote.PartPayload
import com.example.engine.PrePumpCalculator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AiDiagnosticRepository {

    private val systemPrompt = """
Tu es l'IA d'analyse du PRE-PUMP ENGINE.
TON RÔLE :
Tu n'es PAS un robot de trading.
Tu ne donnes PAS d'ordre d'achat ou de vente.
Tu analyses exclusivement les données réelles fournies par le scanner et tu identifies les configurations pouvant précéder une forte accélération du prix.

OBJECTIF PRINCIPAL :
Détecter une crypto AVANT ou au tout début d'un mouvement explosif, et non après que le pump soit déjà largement réalisé.

RÈGLE FONDAMENTALE :
NE JAMAIS dire : "Cette crypto va pump."
Dire : "Cette crypto présente actuellement une configuration PRE-PUMP de qualité X/100."

FORMAT DE RÉPONSE OBLIGATOIRE :
ACTIF : [Symbole]
PHASE : [WAIT | COMPRESSION | SETUP | PRE-PUMP | ACCELERATION | TRIGGER | PUMP | EXTENDED]
SCORE : [Score]/100 ([Classification])
CONFIANCE : [Élevée | Modérée | Faible]

VOLUME : [RVOL et analyse]
MOMENTUM : [RSI, ROC et analyse]
SQUEEZE : [État BB/KC et durée]
STRUCTURE : [Description de la structure]
EXTENSION : [Distance EMA20 et risque FOMO]
BTC : [Contexte de marché]

DIAGNOSTIC :
[Une phrase courte expliquant la confluence principale.]

RISQUE :
[Une phrase courte expliquant ce qui pourrait invalider le signal.]
""".trimIndent()

    suspend fun generateDiagnostic(
        candidate: CryptoCandidate,
        historicalSummary: String? = null
    ): String = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Throwable) {
            ""
        }

        // If API key is present and not default placeholder, attempt real Gemini call
        if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val userPrompt = buildString {
                    append("Analyse la configuration technique suivante fournie par le scanner :\n\n")
                    append("Crypto : ${candidate.symbol}\n")
                    append("Prix actuel : ${candidate.indicators.price} USDT\n")
                    append("Variations : 15m: ${candidate.indicators.change15m}%, 1h: ${candidate.indicators.change1h}%, 4h: ${candidate.indicators.change4h}%, 24h: ${candidate.indicators.change24h}%\n")
                    append("RVOL : ${candidate.indicators.rvol} (Progression : ${candidate.indicators.rvolProgression}%)\n")
                    append("RSI : ${candidate.indicators.rsi} (Accélération : ${candidate.indicators.rsiAcceleration} pts)\n")
                    append("ROC : ${candidate.indicators.roc}%\n")
                    append("Distance à l'EMA20 : ${candidate.indicators.distanceEma20Percent}%\n")
                    append("Squeeze BB/KC : ${if (candidate.indicators.isSqueezeOn) "Actif" else "Inactif"} (Durée : ${candidate.indicators.squeezeDurationBars} bougies, Largeur BB : ${candidate.indicators.bbWidthPercent}%)\n")
                    append("Structure de prix : ${candidate.indicators.structureLabel}\n")
                    append("Contexte BTC : ${candidate.indicators.btcContext}\n")
                    append("Score calculé par le moteur : ${candidate.scoreBreakdown.totalScore}/100\n")
                    if (!historicalSummary.isNullOrBlank()) {
                        append("Mémoire historique du moteur : $historicalSummary\n")
                    }
                    append("\nGénère le rapport strict au format demandé.")
                }

                val request = GeminiRequest(
                    contents = listOf(
                        ContentPayload(parts = listOf(PartPayload(text = userPrompt)))
                    ),
                    systemInstruction = ContentPayload(parts = listOf(PartPayload(text = systemPrompt)))
                )

                val response = GeminiClient.service.generateContent(apiKey, request)
                val responseText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                if (!responseText.isNullOrBlank()) {
                    return@withContext responseText.trim()
                }
            } catch (e: Exception) {
                // If API call encounters network error, fallback gracefully to algorithmic report
            }
        }

        // Deterministic engine diagnostic (implements identical format and strict guidelines)
        return@withContext PrePumpCalculator.formatDiagnosticReport(candidate)
    }
}
