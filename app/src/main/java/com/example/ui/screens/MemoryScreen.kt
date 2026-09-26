package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.HistoryEdu
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SignalMemoryEntity
import com.example.data.repository.MemoryEngineStats
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCrimson
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardBorder
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MemoryScreen(
    signals: List<SignalMemoryEntity>,
    stats: MemoryEngineStats?,
    onDeleteSignal: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBg)
            .padding(horizontal = 14.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(NeonAmber.copy(alpha = 0.15f))
                    .border(1.dp, NeonAmber.copy(alpha = 0.4f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Psychology,
                    contentDescription = null,
                    tint = NeonAmber,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "MÉMOIRE & APPRENTISSAGE",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "Suivi post-signal (+5% à +50%) & métriques réelles",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Engine Analytics Dashboard Card
        stats?.let { s ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(SurfaceCard)
                    .border(1.dp, SurfaceCardBorder, RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "PERFORMANCE DU SCANNER",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextMuted,
                            letterSpacing = 0.6.sp
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(NeonEmerald.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${s.totalSignals} SIGNAUX ENREGISTRÉS",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonEmerald
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatBox("Taux de Succès", "${String.format("%.0f", s.successRatePercent)}%", NeonEmerald, Modifier.weight(1f))
                        Spacer(modifier = Modifier.width(8.dp))
                        StatBox("Gain Max Moyen", "+${String.format("%.1f", s.averageMaxFavorable)}%", NeonCyan, Modifier.weight(1f))
                        Spacer(modifier = Modifier.width(8.dp))
                        StatBox("Drawdown Moyen", "${String.format("%.1f", s.averageDrawdown)}%", NeonCrimson, Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Milestones Progression
                    Text(
                        text = "Cibles franchies post-signal :",
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        MilestoneHitPill("+5%", s.hit5Count, s.totalSignals)
                        MilestoneHitPill("+10%", s.hit10Count, s.totalSignals)
                        MilestoneHitPill("+20%", s.hit20Count, s.totalSignals)
                        MilestoneHitPill("+30%", s.hit30Count, s.totalSignals)
                        MilestoneHitPill("+50%", s.hit50Count, s.totalSignals)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "HISTORIQUE D'APPRENTISSAGE DU MOTEUR",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = TextMuted,
            letterSpacing = 0.6.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        if (signals.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.HistoryEdu,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Aucun signal enregistré pour le moment",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }
            }
        } else {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                val isWide = maxWidth >= 720.dp
                if (isWide) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(signals, key = { it.id }) { item ->
                            SignalMemoryCard(signal = item, onDelete = { onDeleteSignal(item.id) })
                        }
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(1),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(signals, key = { it.id }) { item ->
                            SignalMemoryCard(signal = item, onDelete = { onDeleteSignal(item.id) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatBox(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(SurfaceElevated)
            .padding(vertical = 8.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = title, fontSize = 9.sp, color = TextMuted)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Black, color = color)
        }
    }
}

@Composable
fun MilestoneHitPill(
    label: String,
    count: Int,
    total: Int
) {
    val pct = if (total > 0) (count * 100) / total else 0
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (count > 0) NeonEmerald.copy(alpha = 0.15f) else Color(0xFF1E2838))
            .padding(horizontal = 6.dp, vertical = 3.dp)
    ) {
        Text(
            text = "$label ($pct%)",
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = if (count > 0) NeonEmerald else TextMuted
        )
    }
}

@Composable
fun SignalMemoryCard(
    signal: SignalMemoryEntity,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isSuccess = signal.outcomeStatus.contains("SUCCÈS")
    val isInvalid = signal.outcomeStatus.contains("INVALIDÉ") || signal.outcomeStatus.contains("PUMP_DÉJÀ")
    val statusColor = when {
        isSuccess -> NeonEmerald
        isInvalid -> NeonCrimson
        else -> NeonCyan
    }

    val dateFormatted = SimpleDateFormat("dd/MM HH:mm", Locale.getDefault()).format(Date(signal.timestamp))

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceCard)
            .border(1.dp, SurfaceCardBorder, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Column {
            // Header Row: Crypto, Date, Status, Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = signal.crypto,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(SurfaceElevated)
                            .padding(horizontal = 5.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = signal.initialPhase,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonCyan
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Score : ${signal.initialScore}/100",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(statusColor.copy(alpha = 0.15f))
                            .border(1.dp, statusColor.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 7.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = signal.outcomeStatus,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = statusColor
                        )
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Supprimer",
                            tint = TextMuted,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Price Telemetry Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "Prix au signal", fontSize = 9.sp, color = TextMuted)
                    Text(
                        text = "$${String.format("%.4f", signal.priceAtSignal)}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                }
                Column {
                    Text(text = "RVOL Initial", fontSize = 9.sp, color = TextMuted)
                    Text(
                        text = "${String.format("%.2f", signal.rvol)}x",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                }
                Column {
                    Text(text = "Max Favorable", fontSize = 9.sp, color = TextMuted)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                            contentDescription = null,
                            tint = NeonEmerald,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "+${String.format("%.1f", signal.maxFavorablePercent)}%",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonEmerald
                        )
                    }
                }
                Column {
                    Text(text = "Max Drawdown", fontSize = 9.sp, color = TextMuted)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.TrendingDown,
                            contentDescription = null,
                            tint = NeonCrimson,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "${String.format("%.1f", signal.maxDrawdownPercent)}%",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonCrimson
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Milestones checklist (+5%, +10%, +20%, +30%, +50%)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                TargetPill("+5%", signal.hit5Percent, signal.timeTo5PercentMs)
                TargetPill("+10%", signal.hit10Percent, signal.timeTo10PercentMs)
                TargetPill("+20%", signal.hit20Percent, signal.timeTo20PercentMs)
                TargetPill("+30%", signal.hit30Percent, signal.timeTo30PercentMs)
                TargetPill("+50%", signal.hit50Percent, signal.timeTo50PercentMs)
            }

            if (signal.outcomeNote.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(SurfaceElevated)
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Apprentissage : ${signal.outcomeNote}",
                        fontSize = 10.sp,
                        color = TextSecondary,
                        lineHeight = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun TargetPill(label: String, hit: Boolean, timeMs: Long?) {
    val timeTxt = timeMs?.let {
        val mins = (it / 60000).coerceAtLeast(1)
        if (mins >= 60) "${mins / 60}h" else "${mins}m"
    } ?: ""

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(if (hit) NeonEmerald.copy(alpha = 0.15f) else Color(0xFF1E2838))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (hit) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = NeonEmerald,
                    modifier = Modifier.size(10.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
            }
            Text(
                text = if (hit && timeTxt.isNotBlank()) "$label ($timeTxt)" else label,
                fontSize = 9.sp,
                fontWeight = if (hit) FontWeight.Bold else FontWeight.Normal,
                color = if (hit) NeonEmerald else TextMuted
            )
        }
    }
}
