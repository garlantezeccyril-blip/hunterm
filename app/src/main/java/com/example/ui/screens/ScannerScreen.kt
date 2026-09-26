package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.CryptoCandidate
import com.example.data.model.PrePumpPhase
import com.example.ui.components.BtcContextBar
import com.example.ui.components.PhaseBadge
import com.example.ui.components.ScoreGauge
import com.example.ui.components.SqueezeMeter
import com.example.ui.components.getPhaseColor
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

@Composable
fun ScannerScreen(
    candidates: List<CryptoCandidate>,
    isLoading: Boolean,
    activeFilter: String,
    isDemoMode: Boolean,
    onFilterChange: (String) -> Unit,
    onToggleDemoMode: () -> Unit,
    onRefresh: () -> Unit,
    onSelectCandidate: (CryptoCandidate) -> Unit,
    onRecordSignal: (CryptoCandidate) -> Unit,
    selectedSymbol: String? = null,
    showTopHeader: Boolean = true,
    onToggleDesktopMode: (() -> Unit)? = null,
    isDesktopForced: Boolean = false,
    modifier: Modifier = Modifier
) {
    val btcContext = candidates.firstOrNull()?.indicators?.btcContext ?: "Stable (+0.4%)"

    val filteredList = when (activeFilter) {
        "PRE_PUMP" -> candidates.filter { it.scoreBreakdown.totalScore >= 60 }
        "SQUEEZE" -> candidates.filter { it.indicators.isSqueezeOn }
        "ACCELERATION" -> candidates.filter { it.phase == PrePumpPhase.ACCELERATION || it.phase == PrePumpPhase.TRIGGER }
        "ANTI_FOMO" -> candidates.filter { it.scoreBreakdown.extensionPenalty < 0 || it.phase == PrePumpPhase.EXTENDED }
        else -> candidates
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBg)
            .padding(horizontal = 14.dp)
    ) {
        if (showTopHeader) {
            Spacer(modifier = Modifier.height(10.dp))

            // Top App Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(NeonCyan.copy(alpha = 0.15f))
                            .border(1.dp, NeonCyan.copy(alpha = 0.4f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ElectricBolt,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "PRE-PUMP ENGINE",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Black,
                                color = TextPrimary,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(NeonEmerald.copy(alpha = 0.15f))
                                    .padding(horizontal = 5.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "IA v3.5",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NeonEmerald
                                )
                            }
                        }
                        Text(
                            text = if (isDemoMode) "Mode Étalons Benchmarks" else "Live Binance Scanner",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (onToggleDesktopMode != null) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isDesktopForced) NeonCyan.copy(alpha = 0.2f) else SurfaceElevated)
                                .border(1.dp, if (isDesktopForced) NeonCyan else SurfaceCardBorder, RoundedCornerShape(8.dp))
                                .clickable { onToggleDesktopMode() }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = if (isDesktopForced) "BUREAU ACTIF" else "MODE BUREAU",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDesktopForced) NeonCyan else TextSecondary
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                    }

                    // Toggle mode button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isDemoMode) NeonAmber.copy(alpha = 0.15f) else SurfaceElevated)
                            .border(1.dp, if (isDemoMode) NeonAmber.copy(alpha = 0.4f) else SurfaceCardBorder, RoundedCornerShape(8.dp))
                            .clickable { onToggleDemoMode() }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (isDemoMode) "MODE ÉTALON" else "LIVE BINANCE",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDemoMode) NeonAmber else TextSecondary
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = onRefresh,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(SurfaceElevated)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp,
                                color = NeonCyan
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Actualiser",
                                tint = NeonCyan,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
        } else {
            Spacer(modifier = Modifier.height(6.dp))
        }

        // BTC Context Bar
        BtcContextBar(btcContext = btcContext)

        Spacer(modifier = Modifier.height(10.dp))

        // Filters row
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val filters = listOf(
                Pair("TOUS", "Tous (${candidates.size})"),
                Pair("PRE_PUMP", "Pre-Pump 60+"),
                Pair("SQUEEZE", "Squeeze ON"),
                Pair("ACCELERATION", "Accélération"),
                Pair("ANTI_FOMO", "Anti-FOMO")
            )
            items(filters) { (key, label) ->
                val selected = activeFilter == key
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (selected) NeonCyan.copy(alpha = 0.2f) else SurfaceCard)
                        .border(
                            1.dp,
                            if (selected) NeonCyan else SurfaceCardBorder,
                            RoundedCornerShape(20.dp)
                        )
                        .clickable { onFilterChange(key) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = label,
                        fontSize = 11.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                        color = if (selected) NeonCyan else TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Candidate List
        if (filteredList.isEmpty() && !isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Aucun actif ne correspond au filtre actif",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredList, key = { it.symbol }) { candidate ->
                    val isSelected = candidate.symbol == selectedSymbol
                    CandidateCard(
                        candidate = candidate,
                        isSelected = isSelected,
                        onAnalyze = { onSelectCandidate(candidate) },
                        onRecord = { onRecordSignal(candidate) }
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun CandidateCard(
    candidate: CryptoCandidate,
    onAnalyze: () -> Unit,
    onRecord: () -> Unit,
    isSelected: Boolean = false,
    modifier: Modifier = Modifier
) {
    val ind = candidate.indicators
    val b = candidate.scoreBreakdown
    val changeColor = if (ind.change24h >= 0) NeonEmerald else NeonCrimson

    val borderColor = when {
        isSelected -> NeonCyan
        candidate.phase == PrePumpPhase.PRE_PUMP -> NeonEmerald.copy(alpha = 0.5f)
        else -> SurfaceCardBorder
    }

    val backgroundColor = when {
        isSelected -> SurfaceElevated
        else -> SurfaceCard
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(backgroundColor)
            .border(
                if (isSelected) 1.5.dp else 1.dp,
                borderColor,
                RoundedCornerShape(14.dp)
            )
            .clickable { onAnalyze() }
            .padding(14.dp)
    ) {
        Column {
            // Header Row: Symbol, Price, Score Gauge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = candidate.baseAsset,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "/${candidate.quoteAsset}",
                            fontSize = 12.sp,
                            color = TextMuted,
                            modifier = Modifier.padding(start = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        PhaseBadge(phase = candidate.phase)
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "$${if (ind.price < 1.0) String.format("%.4f", ind.price) else String.format("%.2f", ind.price)}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${if (ind.change24h >= 0) "+" else ""}${String.format("%.1f", ind.change24h)}%",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = changeColor
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    ScoreGauge(score = b.totalScore, size = 52.dp, strokeWidth = 4.dp)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Indicators Telemetry Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // RVOL badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (ind.rvol >= 2.0) NeonEmerald.copy(alpha = 0.15f) else Color(0xFF1E2838))
                        .padding(horizontal = 7.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "RVOL ${String.format("%.1f", ind.rvol)}x",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (ind.rvol >= 2.0) NeonEmerald else TextSecondary
                    )
                }

                // Squeeze meter
                SqueezeMeter(
                    isSqueezeOn = ind.isSqueezeOn,
                    squeezeDurationBars = ind.squeezeDurationBars,
                    bbWidthPercent = ind.bbWidthPercent
                )

                // RSI
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (ind.rsi > 75) NeonCrimson.copy(alpha = 0.15f) else Color(0xFF1E2838))
                        .padding(horizontal = 7.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "RSI ${String.format("%.0f", ind.rsi)}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (ind.rsi > 75) NeonCrimson else TextSecondary
                    )
                }

                // Dist EMA20
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (ind.distanceEma20Percent > 6.0) NeonCrimson.copy(alpha = 0.15f) else Color(0xFF1E2838))
                        .padding(horizontal = 7.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "EMA20 ${if (ind.distanceEma20Percent >= 0) "+" else ""}${String.format("%.1f", ind.distanceEma20Percent)}%",
                        fontSize = 10.sp,
                        color = if (ind.distanceEma20Percent > 6.0) NeonCrimson else TextMuted
                    )
                }
            }

            // Anti-FOMO Warning if penalty active
            if (b.extensionPenalty < 0) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(NeonCrimson.copy(alpha = 0.1f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = NeonCrimson,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "PÉNALITÉ ANTI-FOMO (${b.extensionPenalty} pts) : Extension ou surachat excessif",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = NeonCrimson
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Diagnostic teaser & Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = candidate.indicators.structureLabel,
                    fontSize = 10.sp,
                    color = TextMuted,
                    maxLines = 1,
                    modifier = Modifier.weight(1f)
                )

                Row {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(SurfaceElevated)
                            .clickable { onRecord() }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.BookmarkAdd,
                                contentDescription = "Mémoriser",
                                tint = NeonAmber,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Mémoriser",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonAmber
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(NeonCyan.copy(alpha = 0.15f))
                            .border(1.dp, NeonCyan.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                            .clickable { onAnalyze() }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Analyser IA",
                                tint = NeonCyan,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Analyse IA",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonCyan
                            )
                        }
                    }
                }
            }
        }
    }
}
