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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
fun RulesScreen(modifier: Modifier = Modifier) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ObsidianBg)
            .padding(horizontal = 14.dp)
            .verticalScroll(scrollState)
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
                    .background(NeonCyan.copy(alpha = 0.15f))
                    .border(1.dp, NeonCyan.copy(alpha = 0.4f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.MenuBook,
                    contentDescription = null,
                    tint = NeonCyan,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "MOTEUR & CRITÈRES",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "Les 15 principes stricts du Pre-Pump Engine",
                    fontSize = 11.sp,
                    color = TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Cardinal Rule
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(SurfaceCard)
                .border(1.dp, NeonAmber.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                .padding(14.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = NeonAmber,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "RÈGLE FONDAMENTALE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonAmber,
                        letterSpacing = 0.6.sp
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Block, contentDescription = null, tint = NeonCrimson, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "NE JAMAIS dire : \"Cette crypto va pump.\"", fontSize = 12.sp, color = NeonCrimson, fontWeight = FontWeight.SemiBold)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = NeonEmerald, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Dire : \"Cette crypto présente actuellement une configuration PRE-PUMP de qualité X/100.\"",
                        fontSize = 12.sp,
                        color = NeonEmerald,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Section 2: Sequence
        RuleCard(
            title = "PHILOSOPHIE DU PRE-PUMP",
            content = "COMPRESSION  ➜  ACCUMULATION  ➜  ACCÉLÉRATION VOLUME  ➜  MOMENTUM  ➜  BREAKOUT  ➜  DÉBUT DU PUMP.\n\nLa question clé est : \"Le mouvement explosif vient-il potentiellement de commencer alors que son amplitude principale n'est pas encore consommée ?\""
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Section 8: Anti-FOMO
        RuleCard(
            title = "PÉNALITÉ ANTI-FOMO (MAX -10 PTS)",
            content = "Le moteur pénalise lourdement les cryptos qui ont déjà explosé :\n• Distance excessive à l'EMA20 (> +5% à +8%)\n• RSI déjà suracheté (> 75-80)\n• Bougie verticale démesurée\n\nObjectif : Préférer une \"configuration en accélération\" à une \"crypto qui a déjà pumpé\"."
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Section 7: Scoring
        RuleCard(
            title = "BARÈME DU SCORE DE CONFLUENCE (0 À 100)",
            content = "• VOLUME / RVOL : 25 points\n• ACCÉLÉRATION DU VOLUME : 15 points\n• SQUEEZE BB/KC : 15 points\n• MOMENTUM / ROC / RSI : 15 points\n• STRUCTURE DE PRIX : 10 points\n• ATR / EXPANSION : 5 points\n• BREAKOUT : 5 points\n• CONTEXTE BTC : 5 points\n• EXTENSION / RISQUE : jusqu'à -10 points"
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Section 10: 8 Phases
        RuleCard(
            title = "LES 8 PHASES DU MOTEUR",
            content = "1. WAIT : Aucune configuration intéressante\n2. COMPRESSION : Volatilité comprimée (BB inside KC)\n3. SETUP : Compression + premiers signes d'activité\n4. PRE-PUMP : Volume + momentum + structure convergent (Cœur de cible !)\n5. ACCELERATION : Augmentation rapide du volume\n6. TRIGGER : Breakout confirmé en cours\n7. PUMP : Mouvement explosif déjà engagé\n8. EXTENDED : Mouvement trop avancé (Rejet anti-FOMO)"
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Section 15: Priorité Absolue
        RuleCard(
            title = "PRIORITÉ ABSOLUE",
            content = "Le meilleur signal n'est PAS nécessairement celui au score brut le plus élevé.\n\nPriorité à l'actif qui présente :\n1. Faible extension préalable\n2. Compression récente\n3. Accélération du volume\n4. Momentum ascendant (RSI 48 ➜ 59)\n5. Structure proche du breakout\n\nTu recherches : JUSTE AVANT L'EXPLOSION, et non APRÈS L'EXPLOSION."
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun RuleCard(
    title: String,
    content: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceCard)
            .border(1.dp, SurfaceCardBorder, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Column {
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = NeonCyan,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = content,
                fontSize = 12.sp,
                color = TextSecondary,
                lineHeight = 18.sp
            )
        }
    }
}
