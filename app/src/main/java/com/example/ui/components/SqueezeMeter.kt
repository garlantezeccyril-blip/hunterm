package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary

@Composable
fun SqueezeMeter(
    isSqueezeOn: Boolean,
    squeezeDurationBars: Int,
    bbWidthPercent: Double,
    modifier: Modifier = Modifier
) {
    val activeColor = if (isSqueezeOn) NeonCyan else Color(0xFF64748B)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSqueezeOn) NeonCyan.copy(alpha = 0.12f) else Color(0xFF1E2838))
            .border(
                1.dp,
                if (isSqueezeOn) NeonCyan.copy(alpha = 0.35f) else Color(0xFF2B374C),
                RoundedCornerShape(8.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(activeColor)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (isSqueezeOn) "SQUEEZE ON (${squeezeDurationBars}b)" else "EXPANSION",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSqueezeOn) NeonCyan else TextSecondary
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "BBw: ${String.format("%.1f", bbWidthPercent)}%",
                fontSize = 9.sp,
                color = TextMuted
            )
        }
    }
}
