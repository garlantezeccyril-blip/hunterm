package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.PrePumpDatabase
import com.example.data.model.CryptoCandidate
import com.example.data.model.PrePumpPhase
import com.example.data.model.SignalMemoryEntity
import com.example.data.repository.AiDiagnosticRepository
import com.example.data.repository.MarketRepository
import com.example.data.repository.MemoryEngineStats
import com.example.data.repository.SignalMemoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = PrePumpDatabase.getDatabase(application)
    private val marketRepo = MarketRepository()
    private val signalMemoryRepo = SignalMemoryRepository(db.signalDao())
    private val aiRepo = AiDiagnosticRepository()

    private val _candidates = MutableStateFlow<List<CryptoCandidate>>(emptyList())
    val candidates: StateFlow<List<CryptoCandidate>> = _candidates.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _selectedCandidate = MutableStateFlow<CryptoCandidate?>(null)
    val selectedCandidate: StateFlow<CryptoCandidate?> = _selectedCandidate.asStateFlow()

    private val _aiDiagnosticReport = MutableStateFlow<String?>(null)
    val aiDiagnosticReport: StateFlow<String?> = _aiDiagnosticReport.asStateFlow()

    private val _isAiGenerating = MutableStateFlow(false)
    val isAiGenerating: StateFlow<Boolean> = _isAiGenerating.asStateFlow()

    private val _activeFilter = MutableStateFlow("TOUS")
    val activeFilter: StateFlow<String> = _activeFilter.asStateFlow()

    private val _isDemoMode = MutableStateFlow(false)
    val isDemoMode: StateFlow<Boolean> = _isDemoMode.asStateFlow()

    private val _stats = MutableStateFlow<MemoryEngineStats?>(null)
    val stats: StateFlow<MemoryEngineStats?> = _stats.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    val trackedSignals: StateFlow<List<SignalMemoryEntity>> = signalMemoryRepo.allSignals
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        viewModelScope.launch {
            signalMemoryRepo.seedInitialMemoryIfEmpty()
            refreshStats()
            refreshScanner()
        }
    }

    fun refreshScanner() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = if (_isDemoMode.value) {
                Result.success(marketRepo.getCuratedBenchmarkCandidates("Haussier Stable (+1.2%)"))
            } else {
                marketRepo.fetchMarketCandidates()
            }

            val list = result.getOrElse {
                marketRepo.getCuratedBenchmarkCandidates("Stable (+0.5%)")
            }
            _candidates.value = list

            // If selected candidate is in list, update it
            _selectedCandidate.value?.let { current ->
                list.find { it.symbol == current.symbol }?.let { updated ->
                    _selectedCandidate.value = updated
                }
            }

            // Also check and update prices for tracked signals
            updateTrackedSignalsWithCurrentPrices(list)
            refreshStats()

            _isLoading.value = false
        }
    }

    fun selectCandidate(candidate: CryptoCandidate) {
        _selectedCandidate.value = candidate
        _aiDiagnosticReport.value = null
        runAiDiagnostic(candidate)
    }

    fun clearSelectedCandidate() {
        _selectedCandidate.value = null
        _aiDiagnosticReport.value = null
    }

    fun runAiDiagnostic(candidate: CryptoCandidate) {
        viewModelScope.launch {
            _isAiGenerating.value = true
            val currentStats = _stats.value
            val historySummary = currentStats?.let {
                "Taux de succès historique des signaux : ${String.format("%.0f", it.successRatePercent)}%, " +
                "Gain moyen favorable : +${String.format("%.1f", it.averageMaxFavorable)}%"
            }
            val report = aiRepo.generateDiagnostic(candidate, historySummary)
            _aiDiagnosticReport.value = report
            _isAiGenerating.value = false
        }
    }

    fun recordSignalToMemory(candidate: CryptoCandidate) {
        viewModelScope.launch {
            val id = signalMemoryRepo.recordCandidateSignal(candidate)
            refreshStats()
            _toastMessage.value = "Signal ${candidate.symbol} enregistré dans la Mémoire (ID #$id)"
        }
    }

    fun deleteTrackedSignal(id: Long) {
        viewModelScope.launch {
            signalMemoryRepo.deleteSignal(id)
            refreshStats()
            _toastMessage.value = "Signal retiré de la mémoire"
        }
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    fun setFilter(filter: String) {
        _activeFilter.value = filter
    }

    fun toggleDemoMode() {
        _isDemoMode.value = !_isDemoMode.value
        refreshScanner()
    }

    private suspend fun refreshStats() {
        _stats.value = signalMemoryRepo.computeStats()
    }

    private suspend fun updateTrackedSignalsWithCurrentPrices(currentList: List<CryptoCandidate>) {
        val priceMap = currentList.associate { it.symbol to it.indicators.price }
        val signals = trackedSignals.value
        for (signal in signals) {
            priceMap[signal.crypto]?.let { newPrice ->
                signalMemoryRepo.updateSignalPrice(signal.id, newPrice)
            }
        }
    }
}
