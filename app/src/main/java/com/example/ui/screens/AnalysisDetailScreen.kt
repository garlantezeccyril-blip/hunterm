package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CryptoCandidate
import com.example.ui.components.PhaseBadge
import com.example.ui.components.ScoreGauge
import com.example.ui.components.getPhaseColor
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCrimson
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardBorder
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun AnalysisDetailScreen(
    candidate: CryptoCandidate,
    aiReport: String?,
    isAiGenerating: Boolean,
    onBack: () -> Unit,
    onRefreshAi: () -> Unit,
    onRecordSignal: (CryptoCandidate) -> Unit,
    showBackButton: Boolean = true,
    modifier: Modifier = Modifier
) {
    if (showBackButton) {
        BackHandler { onBack() }
    }
    val context = LocalContext.current
    val ind = candidate.indicators
    val b = candidate.scoreBreakdown
    val phaseColor = getPhaseColor(candidate.phase)
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBg)
            .padding(horizontal = 14.dp)
            .verticalScroll(scrollState)
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Top Navigation Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showBackButton) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(SurfaceElevated)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Retour",
                        tint = TextPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(NeonCyan.copy(alpha = 0.15f))
                        .border(1.dp, NeonCyan.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "TERMINAL DÉTAILLÉ",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonCyan,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${candidate.baseAsset}/${candidate.quoteAsset}",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary
                )
                Text(
                    text = "$${if (ind.price < 1.0) String.format("%.4f", ind.price) else String.format("%.2f", ind.price)} USDT",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = NeonCyan
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(NeonAmber.copy(alpha = 0.15f))
                    .border(1.dp, NeonAmber.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                    .clickable { onRecordSignal(candidate) }
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.BookmarkAdd,
                        contentDescription = null,
                        tint = NeonAmber,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "MÉMORISER",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonAmber
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Phase & Score Banner Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(SurfaceCard)
                .border(1.dp, phaseColor.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        PhaseBadge(phase = candidate.phase)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = candidate.classification.label,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = phaseColor
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = candidate.phase.description,
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "CONFIANCE DU SCANNER : ${candidate.confidence.uppercase()}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMuted,
                        letterSpacing = 0.6.sp
                    )
                }

                ScoreGauge(score = b.totalScore, size = 74.dp, strokeWidth = 6.dp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Confluence Scoring Breakdown (Section 7)
        Text(
            text = "DÉCOMPOSITION DU SCORE PRE-PUMP (MAX 100)",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = TextMuted,
            letterSpacing = 0.6.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(SurfaceCard)
                .border(1.dp, SurfaceCardBorder, RoundedCornerShape(14.dp))
                .padding(14.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ScoreBarItem("Volume / RVOL", b.volumeRvolScore, 25, NeonCyan)
                ScoreBarItem("Accélération Volume", b.volumeAccelerationScore, 15, NeonCyan)
                ScoreBarItem("Squeeze BB/KC", b.squeezeScore, 15, NeonEmerald)
                ScoreBarItem("Momentum / ROC / RSI", b.momentumScore, 15, NeonEmerald)
                ScoreBarItem("Structure de Prix", b.structureScore, 10, NeonAmber)
                ScoreBarItem("ATR / Expansion", b.atrExpansionScore, 5, NeonAmber)
                ScoreBarItem("Breakout", b.breakoutScore, 5, NeonAmber)
                ScoreBarItem("Contexte BTC", b.btcContextScore, 5, NeonAmber)
                if (b.extensionPenalty < 0) {
                    ScoreBarItem("Pénalité Anti-FOMO", b.extensionPenalty, -10, NeonCrimson)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Technical Telemetry Grid
        Text(
            text = "TÉLÉMÉTRIE TECHNIQUE RÉELLE",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = TextMuted,
            letterSpacing = 0.6.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(SurfaceCard)
                .border(1.dp, SurfaceCardBorder, RoundedCornerShape(14.dp))
                .padding(14.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    TelemetryItem("RVOL Actuel", "${String.format("%.2f", ind.rvol)}x", Modifier.weight(1f))
                    TelemetryItem("Progression RVOL", "${if (ind.rvolProgression >= 0) "+" else ""}${String.format("%.1f", ind.rvolProgression)}%", Modifier.weight(1f))
                    TelemetryItem("RSI (14)", "${String.format("%.1f", ind.rsi)}", Modifier.weight(1f))
                }
                Row(modifier = Modifier.fillMaxWidth()) {
                    TelemetryItem("Distance EMA20", "${if (ind.distanceEma20Percent >= 0) "+" else ""}${String.format("%.2f", ind.distanceEma20Percent)}%", Modifier.weight(1f))
                    TelemetryItem("Squeeze BB/KC", if (ind.isSqueezeOn) "ON (${ind.squeezeDurationBars}b)" else "OFF", Modifier.weight(1f))
                    TelemetryItem("ROC", "${if (ind.roc >= 0) "+" else ""}${String.format("%.2f", ind.roc)}%", Modifier.weight(1f))
                }
                Row(modifier = Modifier.fillMaxWidth()) {
                    TelemetryItem("Largeur BB", "${String.format("%.2f", ind.bbWidthPercent)}%", Modifier.weight(1f))
                    TelemetryItem("ATR %", "${String.format("%.2f", ind.atrPercent)}%", Modifier.weight(1f))
                    TelemetryItem("Acheteurs", "${ind.buyerVolumePercent?.let { String.format("%.0f", it) + "%" } ?: "DONNÉE INDISPONIBLE"}", Modifier.weight(1f))
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Gemini AI Diagnostic Card (Mandatory format from Section 14)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = NeonCyan,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "RAPPORT D'ANALYSE IA (MANDAT STRICT)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeonCyan,
                    letterSpacing = 0.6.sp
                )
            }

            Row {
                IconButton(
                    onClick = onRefreshAi,
                    modifier = Modifier.size(28.dp)
                ) {
                    if (isAiGenerating) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(14.dp),
                            strokeWidth = 2.dp,
                            color = NeonCyan
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Régénérer",
                            tint = NeonCyan,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                IconButton(
                    onClick = {
                        aiReport?.let { text ->
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Pre-Pump Report", text))
                        }
                    },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copier",
                        tint = TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(SurfaceDark)
                .border(1.dp, NeonCyan.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                .padding(14.dp)
        ) {
            if (isAiGenerating && aiReport == null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(28.dp),
                        strokeWidth = 3.dp,
                        color = NeonCyan
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Examen de la confluence volume, squeeze & structure...",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            } else {
                Text(
                    text = aiReport ?: "Rapport en cours de génération...",
                    fontSize = 12.sp,
                    color = TextPrimary,
                    fontFamily = FontFamily.Monospace,
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Rule Note
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(SurfaceElevated)
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = null,
                tint = NeonAmber,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Règle absolue : L'IA ne donne jamais d'ordre d'achat ou de vente. Un score élevé représente la qualité d'une configuration, pas une certitude de hausse.",
                fontSize = 10.sp,
                color = TextSecondary
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun ScoreBarItem(
    label: String,
    score: Int,
    maxScore: Int,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = TextSecondary,
            modifier = Modifier.width(160.dp)
        )

        val progress = if (maxScore < 0) {
            (score.toFloat() / maxScore.toFloat()).coerceIn(0f, 1f)
        } else {
            (score.toFloat() / maxScore.toFloat()).coerceIn(0f, 1f)
        }

        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .weight(1f)
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = Color(0xFF1E2838)
        )

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = if (maxScore < 0) "$score pts" else "$score/$maxScore",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
fun TelemetryItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            fontSize = 10.sp,
            color = TextMuted
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
    }
}
