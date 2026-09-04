package com.japcounter.jap_counter_app.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.japcounter.jap_counter_app.R
import com.japcounter.jap_counter_app.ui.components.AppTopBar
import com.japcounter.jap_counter_app.ui.components.BottomNavBar
import com.japcounter.jap_counter_app.ui.components.NavTab
import com.japcounter.jap_counter_app.ui.theme.SaffronDark
import com.japcounter.jap_counter_app.ui.theme.SaffronLight
import com.japcounter.jap_counter_app.ui.theme.SaffronMedium
import com.japcounter.jap_counter_app.ui.theme.SandalwoodLight
import com.japcounter.jap_counter_app.ui.theme.SandalwoodText

@Composable
fun SetupScreen(
    onStartJap: (String, Int) -> Unit,
    onBack: () -> Unit
) {
    var mantraName by remember { mutableStateOf("") }
    var selectedLimitOption by remember { mutableStateOf("108") } // "108", "1008", "custom"
    var customLimitText by remember { mutableStateOf("") }

    var mantraError by remember { mutableStateOf<String?>(null) }
    var limitError by remember { mutableStateOf<String?>(null) }

    val emptyMantraMsg = stringResource(R.string.err_empty_mantra)
    val invalidLimitMsg = stringResource(R.string.err_invalid_target)

    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(R.string.setup_title),
                onBack = onBack
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth()
                ) {
                    // Mantra Name
                    Text(
                        text = stringResource(R.string.mantra_name_label),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = SandalwoodText
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = mantraName,
                        onValueChange = {
                            mantraName = it
                            if (it.isNotBlank()) mantraError = null
                        },
                        placeholder = {
                            Text(
                                text = stringResource(R.string.mantra_name_hint),
                                color = Color.LightGray,
                                fontSize = 14.sp
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SaffronDark,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        ),
                        isError = mantraError != null,
                        supportingText = {
                            if (mantraError != null) {
                                Text(text = mantraError!!, color = MaterialTheme.colorScheme.error)
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Select Limit
                    Text(
                        text = stringResource(R.string.select_limit_label),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = SandalwoodText
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val options = listOf(
                            "108" to stringResource(R.string.limit_108),
                            "1008" to stringResource(R.string.limit_1008),
                            "custom" to stringResource(R.string.limit_custom)
                        )

                        options.forEach { (key, label) ->
                            val isSelected = selectedLimitOption == key
                            val borderBrush = if (isSelected) {
                                Brush.horizontalGradient(listOf(SaffronDark, SaffronMedium))
                            } else {
                                Brush.linearGradient(listOf(MaterialTheme.colorScheme.outline, MaterialTheme.colorScheme.outline))
                            }

                            val background = if (isSelected) SaffronDark.copy(alpha = 0.08f) else Color.Transparent

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(background)
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        brush = borderBrush,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable {
                                        selectedLimitOption = key
                                        limitError = null
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) SaffronDark else SandalwoodText
                                )
                            }
                        }
                    }

                    // Custom Limit Input
                    if (selectedLimitOption == "custom") {
                        Spacer(modifier = Modifier.height(20.dp))

                        OutlinedTextField(
                            value = customLimitText,
                            onValueChange = {
                                customLimitText = it
                                if (it.toIntOrNull() != null && it.toInt() > 0) limitError = null
                            },
                            label = { Text(stringResource(R.string.custom_limit_label)) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SaffronDark,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                focusedLabelColor = SaffronDark
                            ),
                            isError = limitError != null,
                            supportingText = {
                                if (limitError != null) {
                                    Text(text = limitError!!, color = MaterialTheme.colorScheme.error)
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Start Button
                    Button(
                        onClick = {
                            var hasError = false
                            if (mantraName.trim().isEmpty()) {
                                mantraError = emptyMantraMsg
                                hasError = true
                            }

                            val target = if (selectedLimitOption == "custom") {
                                val parsedVal = customLimitText.toIntOrNull()
                                if (parsedVal == null || parsedVal <= 0) {
                                    limitError = invalidLimitMsg
                                    hasError = true
                                    0
                                } else {
                                    parsedVal
                                }
                            } else {
                                selectedLimitOption.toInt()
                            }

                            if (!hasError) {
                                onStartJap(mantraName.trim(), target)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clip(RoundedCornerShape(28.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(SaffronDark, SaffronMedium)
                                )
                            ),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.btn_start_jap),
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
