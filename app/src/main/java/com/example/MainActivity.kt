package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.outlined.ElectricBolt
import androidx.compose.material.icons.outlined.Psychology
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.MainViewModel
import com.example.ui.components.DesktopSidebar
import com.example.ui.screens.AnalysisDetailScreen
import com.example.ui.screens.MemoryScreen
import com.example.ui.screens.RulesScreen
import com.example.ui.screens.ScannerScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.SurfaceCardBorder
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary

enum class AppTab(val title: String, val selectedIcon: ImageVector, val unselectedIcon: ImageVector) {
    SCANNER("Scanner", Icons.Filled.ElectricBolt, Icons.Outlined.ElectricBolt),
    MEMORY("Mémoire", Icons.Filled.Psychology, Icons.Outlined.Psychology),
    RULES("Règles IA", Icons.AutoMirrored.Filled.MenuBook, Icons.AutoMirrored.Outlined.MenuBook)
}

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                val context = LocalContext.current
                val snackbarHostState = remember { SnackbarHostState() }

                val candidates by viewModel.candidates.collectAsStateWithLifecycle()
                val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
                val selectedCandidate by viewModel.selectedCandidate.collectAsStateWithLifecycle()
                val aiReport by viewModel.aiDiagnosticReport.collectAsStateWithLifecycle()
                val isAiGenerating by viewModel.isAiGenerating.collectAsStateWithLifecycle()
                val activeFilter by viewModel.activeFilter.collectAsStateWithLifecycle()
                val isDemoMode by viewModel.isDemoMode.collectAsStateWithLifecycle()
                val trackedSignals by viewModel.trackedSignals.collectAsStateWithLifecycle()
                val stats by viewModel.stats.collectAsStateWithLifecycle()
                val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()

                var currentTab by remember { mutableIntStateOf(0) }
                // Manual override: null means auto-detect by screen width (>= 720dp)
                var forceDesktopMode by remember { mutableStateOf<Boolean?>(null) }

                LaunchedEffect(toastMessage) {
                    toastMessage?.let { msg ->
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        viewModel.clearToast()
                    }
                }

                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    val isDesktop = forceDesktopMode ?: (maxWidth >= 720.dp)
                    val btcContext = candidates.firstOrNull()?.indicators?.btcContext ?: "Stable (+0.4%)"
                    val prePumpCount = candidates.count { it.scoreBreakdown.totalScore >= 60 }

                    // Auto-select candidate in Desktop mode if none is explicitly picked yet
                    val activeDesktopCandidate = selectedCandidate ?: candidates.firstOrNull()
                    LaunchedEffect(isDesktop, candidates) {
                        if (isDesktop && selectedCandidate == null && candidates.isNotEmpty()) {
                            candidates.firstOrNull()?.let { first ->
                                viewModel.selectCandidate(first)
                            }
                        }
                    }

                    if (isDesktop) {
                        // ==================== VERSION ORDINATEUR / DESKTOP LAYOUT ====================
                        Scaffold(
                            modifier = Modifier.fillMaxSize(),
                            contentWindowInsets = WindowInsets.safeDrawing,
                            containerColor = ObsidianBg,
                            snackbarHost = { SnackbarHost(snackbarHostState) }
                        ) { innerPadding ->
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(innerPadding)
                            ) {
                                // 1. Sidebar Navigation Rail
                                DesktopSidebar(
                                    currentTab = currentTab,
                                    onTabSelected = { currentTab = it },
                                    btcContext = btcContext,
                                    isDemoMode = isDemoMode,
                                    onToggleDemoMode = { viewModel.toggleDemoMode() },
                                    isLoading = isLoading,
                                    onRefresh = { viewModel.refreshScanner() },
                                    stats = stats,
                                    prePumpCount = prePumpCount,
                                    onToggleMobileView = { forceDesktopMode = false }
                                )

                                VerticalDivider(
                                    thickness = 1.dp,
                                    color = SurfaceCardBorder
                                )

                                // 2. Main Desktop View Area
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                ) {
                                    when (currentTab) {
                                        0 -> {
                                            // Desktop Canonical List-Detail Split View
                                            Row(modifier = Modifier.fillMaxSize()) {
                                                // Left: Candidate list pane
                                                Box(
                                                    modifier = Modifier
                                                        .width(420.dp)
                                                        .fillMaxHeight()
                                                ) {
                                                    ScannerScreen(
                                                        candidates = candidates,
                                                        isLoading = isLoading,
                                                        activeFilter = activeFilter,
                                                        isDemoMode = isDemoMode,
                                                        onFilterChange = { viewModel.setFilter(it) },
                                                        onToggleDemoMode = { viewModel.toggleDemoMode() },
                                                        onRefresh = { viewModel.refreshScanner() },
                                                        onSelectCandidate = { viewModel.selectCandidate(it) },
                                                        onRecordSignal = { viewModel.recordSignalToMemory(it) },
                                                        selectedSymbol = activeDesktopCandidate?.symbol,
                                                        showTopHeader = false
                                                    )
                                                }

                                                VerticalDivider(
                                                    thickness = 1.dp,
                                                    color = SurfaceCardBorder
                                                )

                                                // Right: Detailed Analysis & Telemetry pane
                                                Box(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .fillMaxHeight()
                                                ) {
                                                    if (activeDesktopCandidate != null) {
                                                        AnalysisDetailScreen(
                                                            candidate = activeDesktopCandidate,
                                                            aiReport = aiReport,
                                                            isAiGenerating = isAiGenerating,
                                                            onBack = { },
                                                            onRefreshAi = { viewModel.runAiDiagnostic(activeDesktopCandidate) },
                                                            onRecordSignal = { viewModel.recordSignalToMemory(it) },
                                                            showBackButton = false
                                                        )
                                                    } else {
                                                        Box(
                                                            modifier = Modifier
                                                                .fillMaxSize()
                                                                .padding(32.dp),
                                                            contentAlignment = androidx.compose.ui.Alignment.Center
                                                        ) {
                                                            Text(
                                                                text = "Sélectionnez un actif dans la liste pour afficher l'analyse détaillée",
                                                                fontSize = 14.sp,
                                                                color = TextMuted
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        1 -> {
                                            // Desktop Mémoire & Apprentissage
                                            MemoryScreen(
                                                signals = trackedSignals,
                                                stats = stats,
                                                onDeleteSignal = { viewModel.deleteTrackedSignal(it) }
                                            )
                                        }
                                        2 -> {
                                            // Desktop Règles & Algorithme
                                            RulesScreen()
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        // ==================== VERSION MOBILE / SMARTPHONE LAYOUT ====================
                        Scaffold(
                            modifier = Modifier.fillMaxSize(),
                            contentWindowInsets = WindowInsets.safeDrawing,
                            containerColor = ObsidianBg,
                            snackbarHost = { SnackbarHost(snackbarHostState) },
                            bottomBar = {
                                if (selectedCandidate == null) {
                                    NavigationBar(
                                        containerColor = SurfaceDark,
                                        contentColor = TextPrimary,
                                        tonalElevation = 0.dp
                                    ) {
                                        AppTab.values().forEachIndexed { index, tab ->
                                            val isSelected = currentTab == index
                                            NavigationBarItem(
                                                selected = isSelected,
                                                onClick = { currentTab = index },
                                                icon = {
                                                    Icon(
                                                        imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                                        contentDescription = tab.title,
                                                        modifier = Modifier.size(20.dp)
                                                    )
                                                },
                                                label = {
                                                    Text(
                                                        text = tab.title,
                                                        fontSize = 11.sp,
                                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                                    )
                                                },
                                                colors = NavigationBarItemDefaults.colors(
                                                    selectedIconColor = NeonCyan,
                                                    selectedTextColor = NeonCyan,
                                                    unselectedIconColor = TextMuted,
                                                    unselectedTextColor = TextMuted,
                                                    indicatorColor = NeonCyan.copy(alpha = 0.12f)
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        ) { innerPadding ->
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(innerPadding)
                            ) {
                                if (selectedCandidate != null) {
                                    AnalysisDetailScreen(
                                        candidate = selectedCandidate!!,
                                        aiReport = aiReport,
                                        isAiGenerating = isAiGenerating,
                                        onBack = { viewModel.clearSelectedCandidate() },
                                        onRefreshAi = { viewModel.runAiDiagnostic(selectedCandidate!!) },
                                        onRecordSignal = { viewModel.recordSignalToMemory(it) },
                                        showBackButton = true
                                    )
                                } else {
                                    when (currentTab) {
                                        0 -> ScannerScreen(
                                            candidates = candidates,
                                            isLoading = isLoading,
                                            activeFilter = activeFilter,
                                            isDemoMode = isDemoMode,
                                            onFilterChange = { viewModel.setFilter(it) },
                                            onToggleDemoMode = { viewModel.toggleDemoMode() },
                                            onRefresh = { viewModel.refreshScanner() },
                                            onSelectCandidate = { viewModel.selectCandidate(it) },
                                            onRecordSignal = { viewModel.recordSignalToMemory(it) },
                                            onToggleDesktopMode = { forceDesktopMode = true },
                                            isDesktopForced = false
                                        )
                                        1 -> MemoryScreen(
                                            signals = trackedSignals,
                                            stats = stats,
                                            onDeleteSignal = { viewModel.deleteTrackedSignal(it) }
                                        )
                                        2 -> RulesScreen()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
