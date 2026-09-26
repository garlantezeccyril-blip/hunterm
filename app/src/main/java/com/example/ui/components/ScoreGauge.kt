package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PrePumpClassification
import com.example.data.model.PrePumpPhase
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCrimson
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.PhaseAccelerationColor
import com.example.ui.theme.PhaseCompressionColor
import com.example.ui.theme.PhaseExtendedColor
import com.example.ui.theme.PhasePrePumpColor
import com.example.ui.theme.PhasePumpColor
import com.example.ui.theme.PhaseSetupColor
import com.example.ui.theme.PhaseTriggerColor
import com.example.ui.theme.PhaseWaitColor
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

fun getPhaseColor(phase: PrePumpPhase): Color {
    return when (phase) {
        PrePumpPhase.WAIT -> PhaseWaitColor
        PrePumpPhase.COMPRESSION -> PhaseCompressionColor
        PrePumpPhase.SETUP -> PhaseSetupColor
        PrePumpPhase.PRE_PUMP -> PhasePrePumpColor
        PrePumpPhase.ACCELERATION -> PhaseAccelerationColor
        PrePumpPhase.TRIGGER -> PhaseTriggerColor
        PrePumpPhase.PUMP -> PhasePumpColor
        PrePumpPhase.EXTENDED -> PhaseExtendedColor
    }
}

fun getScoreColor(score: Int): Color {
    return when {
        score >= 90 -> Color(0xFF00FFCC)
        score >= 75 -> NeonEmerald
        score >= 60 -> NeonCyan
        score >= 40 -> NeonAmber
        else -> NeonCrimson
    }
}

@Composable
fun ScoreGauge(
    score: Int,
    size: Dp = 60.dp,
    strokeWidth: Dp = 5.dp,
    modifier: Modifier = Modifier
) {
    val scoreColor = getScoreColor(score)

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(size)) {
            // Track
            drawArc(
                color = Color(0xFF1E2838),
                startAngle = 135f,
                sweepAngle = 270f,
                useCenter = false,
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            )

            // Score Sweep
            val sweep = (score / 100f) * 270f
            drawArc(
                color = scoreColor,
                startAngle = 135f,
                sweepAngle = sweep,
                useCenter = false,
                style = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "$score",
                fontSize = if (size > 80.dp) 24.sp else 16.sp,
                fontWeight = FontWeight.Black,
                color = TextPrimary
            )
            Text(
                text = "/100",
                fontSize = if (size > 80.dp) 11.sp else 8.sp,
                fontWeight = FontWeight.Medium,
                color = TextMuted
            )
        }
    }
}

@Composable
fun PhaseBadge(
    phase: PrePumpPhase,
    modifier: Modifier = Modifier
) {
    val color = getPhaseColor(phase)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.15f))
            .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = phase.label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            letterSpacing = 0.5.sp
        )
    }
}
