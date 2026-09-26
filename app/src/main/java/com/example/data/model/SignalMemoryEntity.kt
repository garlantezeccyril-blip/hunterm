package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "signals_memory")
data class SignalMemoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val crypto: String,
    val initialScore: Int,
    val initialPhase: String,
    val priceAtSignal: Double,
    val rvol: Double,
    val rsi: Double,
    val roc: Double,
    val squeezeDuration: Int,
    val isSqueezeOn: Boolean,
    val atrPercent: Double,
    val distanceEma20: Double,
    val priorChange: Double,
    val btcContext: String,
    
    // Live tracking after signal
    val currentPrice: Double,
    val maxFavorablePercent: Double = 0.0,
    val maxDrawdownPercent: Double = 0.0,
    
    // Milestones
    val hit5Percent: Boolean = false,
    val timeTo5PercentMs: Long? = null,
    val hit10Percent: Boolean = false,
    val timeTo10PercentMs: Long? = null,
    val hit20Percent: Boolean = false,
    val timeTo20PercentMs: Long? = null,
    val hit30Percent: Boolean = false,
    val timeTo30PercentMs: Long? = null,
    val hit50Percent: Boolean = false,
    val timeTo50PercentMs: Long? = null,
    
    // Final classification & review
    // Values: "ACTIF", "SUCCÈS", "ÉCHEC", "FAUX_POSITIF", "PUMP_DÉJÀ_COMMENCÉ"
    val outcomeStatus: String = "ACTIF",
    val outcomeNote: String = ""
)
