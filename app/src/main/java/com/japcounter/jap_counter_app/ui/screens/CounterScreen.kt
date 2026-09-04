package com.japcounter.jap_counter_app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.japcounter.jap_counter_app.R
import com.japcounter.jap_counter_app.ui.components.AppTopBar
import com.japcounter.jap_counter_app.ui.theme.CardOutline
import com.japcounter.jap_counter_app.ui.theme.ProgressTrack
import com.japcounter.jap_counter_app.ui.theme.SaffronDark
import com.japcounter.jap_counter_app.ui.theme.SaffronLight
import com.japcounter.jap_counter_app.ui.theme.SaffronMedium
import com.japcounter.jap_counter_app.ui.theme.SandalwoodLight
import com.japcounter.jap_counter_app.ui.theme.SandalwoodText
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CounterScreen(
    userName: String,
    mantraName: String,
    currentCount: Int,
    targetCount: Int,
    previousJapNames: List<String>,
    onTap: () -> Unit,
    onReset: () -> Unit = {},
    onNavigateToCompletion: () -> Unit,
    onUpdateSessionSettings: (String, Int) -> Unit,
    onHistoryTab: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()

    // Completion trigger
    LaunchedEffect(currentCount, targetCount) {
        if (currentCount >= targetCount && targetCount > 0) {
            onNavigateToCompletion()
        }
    }

    val progress = if (targetCount > 0) (currentCount.toFloat() / targetCount.toFloat()).coerceIn(0f, 1f) else 0f
    val percentage = (progress * 100).toInt()

    // State for Reset Confirmation Dialog
    var showResetDialog by remember { mutableStateOf(false) }

    // State for Edit Settings Modal Bottom Sheet
    var showSettingsSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Temp state for editing inside bottom sheet
    var editMantraName by remember(mantraName) { mutableStateOf(mantraName) }
    var editTargetCountStr by remember(targetCount) { mutableStateOf(targetCount.toString()) }
    var editDropdownExpanded by remember { mutableStateOf(false) }

    // Main Tap Button Pressed Animation
    val tapInteractionSource = remember { MutableInteractionSource() }
    val isPressed by tapInteractionSource.collectIsPressedAsState()
    val tapButtonScale by animateFloatAsState(
        targetValue = if (isPressed) 0.90f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "TapButtonScale"
    )

    // Animated ripple effect on tap
    val tapRippleScale = remember { Animatable(1f) }
    val tapRippleAlpha = remember { Animatable(0f) }

    // Animated count text punch
    val countScale = remember { Animatable(1f) }

    // Quick Tap Handler with rich haptic and micro-animations
    val handleTap = {
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        coroutineScope.launch {
            launch {
                countScale.snapTo(1.15f)
                countScale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
            }
            launch {
                tapRippleScale.snapTo(1f)
                tapRippleAlpha.snapTo(0.6f)
                tapRippleScale.animateTo(1.45f, tween(350, easing = FastOutSlowInEasing))
            }
            launch {
                tapRippleAlpha.animateTo(0f, tween(350))
            }
        }
        onTap()
    }

    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(R.string.app_name),
                actions = {
                    IconButton(onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onHistoryTab()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = stringResource(R.string.history_title),
                            tint = Color.White
                        )
                    }
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFFFFFDF9),
                            Color(0xFFFBF4ED),
                            Color(0xFFF7E8D8)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // ==========================================
                // 1. TOP CARD: Active Counter Session Header (Universal & Modern)
                // ==========================================
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(elevation = 2.dp, shape = RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            // Universal & Modern Circular Pulse Focus Emblem
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(listOf(SaffronMedium, SaffronDark))
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                // Concentric minimal rings & center focus dot
                                Canvas(modifier = Modifier.size(24.dp)) {
                                    val strokeWidth = 2.dp.toPx()
                                    drawCircle(
                                        color = Color.White.copy(alpha = 0.4f),
                                        style = Stroke(width = strokeWidth)
                                    )
                                    drawCircle(
                                        color = Color.White,
                                        radius = 4.dp.toPx()
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = mantraName.ifEmpty { "Daily Counter" },
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = SaffronDark,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Target: ",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = SandalwoodText.copy(alpha = 0.7f)
                                    )
                                    Text(
                                        text = "$targetCount counts",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = SandalwoodText
                                    )
                                }
                            }
                        }

                        // Compact Edit button
                        IconButton(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                editMantraName = mantraName
                                editTargetCountStr = targetCount.toString()
                                showSettingsSheet = true
                            },
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(SaffronLight)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Session",
                                tint = SaffronDark,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // ==========================================
                // 2. CENTER PIECE: Progress Arc & Counter Display
                // ==========================================
                Box(
                    modifier = Modifier
                        .size(240.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Smooth animated sweep angle for the circular progress
                    val animatedProgress by animateFloatAsState(
                        targetValue = progress,
                        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
                        label = "ArcProgress"
                    )

                    Canvas(modifier = Modifier.size(230.dp)) {
                        val strokeWidth = 14.dp.toPx()
                        val diameter = size.minDimension - strokeWidth
                        val radius = diameter / 2
                        val centerOffset = Offset(size.width / 2, size.height / 2)

                        // Subtle outer soft glow circle
                        drawCircle(
                            color = SaffronMedium.copy(alpha = 0.08f),
                            radius = radius + strokeWidth / 2 + 10.dp.toPx()
                        )

                        // Progress Track Ring
                        drawCircle(
                            color = ProgressTrack,
                            center = centerOffset,
                            radius = radius,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )

                        // Active Progress Arc with Rich Saffron Gradient
                        if (animatedProgress > 0f) {
                            drawArc(
                                brush = Brush.sweepGradient(
                                    colors = listOf(
                                        SaffronMedium,
                                        SaffronDark,
                                        SaffronMedium
                                    )
                                ),
                                startAngle = -90f,
                                sweepAngle = 360f * animatedProgress,
                                useCenter = false,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                            )
                        }
                    }

                    // Inner Floating Counter Card with Large Typography
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .scale(countScale.value)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "$currentCount",
                            fontSize = 58.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = SaffronDark,
                            lineHeight = 58.sp
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "of $targetCount",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium,
                            color = SandalwoodText.copy(alpha = 0.65f)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Percentage Tag Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(SaffronLight)
                                .padding(horizontal = 10.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "$percentage%",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = SaffronDark
                            )
                        }
                    }
                }

                // ==========================================
                // 3. BOTTOM SECTION: Primary One-Handed TAP Button & Reset Action
                // ==========================================
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Giant Tap Button with Breathing Ripple Aura
                    Box(
                        modifier = Modifier
                            .size(170.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Expanding Ripple Halo on Tap
                        Box(
                            modifier = Modifier
                                .size(170.dp)
                                .scale(tapRippleScale.value)
                                .clip(CircleShape)
                                .background(
                                    SaffronMedium.copy(alpha = tapRippleAlpha.value)
                                )
                        )

                        // Main Touch Target
                        Box(
                            modifier = Modifier
                                .size(148.dp)
                                .scale(tapButtonScale)
                                .shadow(
                                    elevation = 12.dp,
                                    shape = CircleShape,
                                    spotColor = SaffronDark.copy(alpha = 0.45f)
                                )
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            SaffronMedium,
                                            SaffronDark
                                        )
                                    )
                                )
                                .clickable(
                                    interactionSource = tapInteractionSource,
                                    indication = null
                                ) {
                                    handleTap()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = stringResource(R.string.btn_tap),
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 2.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Touch to Count",
                                    fontSize = 10.sp,
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Secondary Reset Button (Clean pill button with icon)
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f))
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                showResetDialog = true
                            }
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = stringResource(R.string.btn_reset),
                            tint = SandalwoodText.copy(alpha = 0.75f),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(R.string.btn_reset),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = SandalwoodText.copy(alpha = 0.85f)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(6.dp))
                }
            }
        }
    }

    // ==========================================
    // 4. DIALOG: Reset Counter Confirmation
    // ==========================================
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = {
                Text(
                    text = stringResource(R.string.dialog_reset_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = SaffronDark
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.dialog_reset_msg),
                    style = MaterialTheme.typography.bodyMedium,
                    color = SandalwoodText
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onReset()
                        showResetDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SaffronDark)
                ) {
                    Text(stringResource(R.string.btn_reset), color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text(stringResource(R.string.btn_cancel), color = SandalwoodText)
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(20.dp)
        )
    }

    // ==========================================
    // 5. MODAL BOTTOM SHEET: Edit Jap & Target Count
    // ==========================================
    if (showSettingsSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSettingsSheet = false },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 36.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.edit_session_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = SaffronDark
                    )
                    IconButton(onClick = { showSettingsSheet = false }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = SandalwoodText
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Jap Name TextField with suggestions dropdown
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = editMantraName,
                        onValueChange = { editMantraName = it },
                        label = { Text(stringResource(R.string.mantra_name_label)) },
                        placeholder = { Text(stringResource(R.string.mantra_name_hint)) },
                        trailingIcon = {
                            if (previousJapNames.isNotEmpty()) {
                                IconButton(onClick = { editDropdownExpanded = !editDropdownExpanded }) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = "Suggestions",
                                        tint = SaffronDark
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SaffronDark,
                            focusedLabelColor = SaffronDark
                        )
                    )

                    DropdownMenu(
                        expanded = editDropdownExpanded,
                        onDismissRequest = { editDropdownExpanded = false }
                    ) {
                        previousJapNames.forEach { name ->
                            DropdownMenuItem(
                                text = { Text(name, color = SandalwoodText) },
                                onClick = {
                                    editMantraName = name
                                    editDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Target Count Quick Select Buttons (108, 1008)
                Text(
                    text = stringResource(R.string.select_limit_label),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = SandalwoodText
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    listOf(108, 1008).forEach { limit ->
                        val isSelected = editTargetCountStr == limit.toString()
                        OutlinedButton(
                            onClick = { editTargetCountStr = limit.toString() },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (isSelected) SaffronLight else Color.Transparent
                            ),
                            border = BorderStroke(
                                width = 1.dp,
                                color = if (isSelected) SaffronDark else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                            )
                        ) {
                            Text(
                                text = "$limit",
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) SaffronDark else SandalwoodText
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Custom Target Count
                OutlinedTextField(
                    value = editTargetCountStr,
                    onValueChange = { editTargetCountStr = it },
                    label = { Text(stringResource(R.string.custom_limit_label)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SaffronDark,
                        focusedLabelColor = SaffronDark
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Save Changes Button
                Button(
                    onClick = {
                        val parsed = editTargetCountStr.toIntOrNull() ?: targetCount
                        val finalTarget = if (parsed > 0) parsed else 108
                        val finalMantra = editMantraName.trim().ifEmpty { "Daily Counter" }
                        onUpdateSessionSettings(finalMantra, finalTarget)
                        showSettingsSheet = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SaffronDark)
                ) {
                    Text(
                        text = stringResource(R.string.btn_save),
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
