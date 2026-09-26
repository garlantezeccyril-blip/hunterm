package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ScreenSearchDesktop
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.MemoryEngineStats
import com.example.ui.theme.NeonAmber
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

data class NavItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val badge: String? = null
)

@Composable
fun DesktopSidebar(
    currentTab: Int,
    onTabSelected: (Int) -> Unit,
    btcContext: String,
    isDemoMode: Boolean,
    onToggleDemoMode: () -> Unit,
    isLoading: Boolean,
    onRefresh: () -> Unit,
    stats: MemoryEngineStats?,
    prePumpCount: Int,
    onToggleMobileView: () -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        NavItem(
            title = "Scanner Live",
            subtitle = "Détecteur d'anomalies",
            icon = Icons.Default.ElectricBolt,
            badge = if (prePumpCount > 0) "$prePumpCount SETUP" else null
        ),
        NavItem(
            title = "Mémoire & Suivi",
            subtitle = "Validation +5% à +50%",
            icon = Icons.Default.Psychology,
            badge = stats?.let { "${String.format("%.0f", it.successRatePercent)}% WIN" }
        ),
        NavItem(
            title = "Règles du Moteur",
            subtitle = "15 principes stricts",
            icon = Icons.AutoMirrored.Filled.MenuBook,
            badge = "ANTI-FOMO"
        )
    )

    Column(
        modifier = modifier
            .width(260.dp)
            .fillMaxHeight()
            .background(SurfaceDark)
            .border(
                width = 1.dp,
                color = SurfaceCardBorder,
                shape = RoundedCornerShape(topEnd = 0.dp, bottomEnd = 0.dp)
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            // Header: Logo + App Name + Desktop Tag
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(NeonCyan.copy(alpha = 0.15f))
                        .border(1.dp, NeonCyan.copy(alpha = 0.4f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ElectricBolt,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = "PRE-PUMP ENGINE",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary,
                        letterSpacing = 0.5.sp
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(NeonEmerald)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "VERSION ORDINATEUR",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeonEmerald,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // BTC Market Context Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(SurfaceCard)
                    .border(1.dp, SurfaceCardBorder, RoundedCornerShape(10.dp))
                    .padding(10.dp)
            ) {
                Column {
                    Text(
                        text = "CONTEXTE BTC",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMuted,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = btcContext,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Navigation Items
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items.forEachIndexed { index, nav ->
                    val isSelected = currentTab == index
                    val activeBg = if (isSelected) NeonCyan.copy(alpha = 0.14f) else Color.Transparent
                    val activeBorder = if (isSelected) NeonCyan.copy(alpha = 0.45f) else Color.Transparent
                    val activeIconTint = if (isSelected) NeonCyan else TextMuted
                    val activeTextColor = if (isSelected) NeonCyan else TextPrimary

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(activeBg)
                            .border(1.dp, activeBorder, RoundedCornerShape(12.dp))
                            .clickable { onTabSelected(index) }
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = nav.icon,
                                    contentDescription = nav.title,
                                    tint = activeIconTint,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = nav.title,
                                        fontSize = 13.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                        color = activeTextColor
                                    )
                                    Text(
                                        text = nav.subtitle,
                                        fontSize = 10.sp,
                                        color = TextMuted
                                    )
                                }
                            }

                            nav.badge?.let { b ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isSelected) NeonCyan.copy(alpha = 0.2f) else SurfaceElevated)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = b,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) NeonCyan else TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Bottom Controls: Mode switcher + Refresh + Performance stats
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // Stats Snapshot
            stats?.let { s ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(SurfaceElevated)
                        .border(1.dp, SurfaceCardBorder, RoundedCornerShape(10.dp))
                        .padding(10.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Succès", fontSize = 10.sp, color = TextMuted)
                            Text(
                                text = "${String.format("%.0f", s.successRatePercent)}%",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonEmerald
                            )
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Gain moyen", fontSize = 10.sp, color = TextMuted)
                            Text(
                                text = "+${String.format("%.1f", s.averageMaxFavorable)}%",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = NeonCyan
                            )
                        }
                    }
                }
            }

            // Mode switch
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isDemoMode) NeonAmber.copy(alpha = 0.15f) else SurfaceElevated)
                    .border(
                        1.dp,
                        if (isDemoMode) NeonAmber.copy(alpha = 0.4f) else SurfaceCardBorder,
                        RoundedCornerShape(10.dp)
                    )
                    .clickable { onToggleDemoMode() }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "SOURCE DES DONNÉES",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextMuted
                        )
                        Text(
                            text = if (isDemoMode) "Mode Étalons Benchmarks" else "Flux Live Binance",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDemoMode) NeonAmber else TextPrimary
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = null,
                        tint = if (isDemoMode) NeonAmber else TextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Refresh Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(NeonCyan.copy(alpha = 0.15f))
                    .border(1.dp, NeonCyan.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                    .clickable { onRefresh() }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(14.dp),
                            strokeWidth = 2.dp,
                            color = NeonCyan
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "ACTUALISER LE SCANNER",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = NeonCyan,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            // Mobile view switcher toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onToggleMobileView() }
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.PhoneAndroid,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Passer en vue mobile",
                    fontSize = 10.sp,
                    color = TextMuted
                )
            }
        }
    }
}
