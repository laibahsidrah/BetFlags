package xyz.appmaker.pbyvul.ui.screens.phone

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import xyz.appmaker.pbyvul.R
import kotlinx.coroutines.delay

private val WhiteBackground = Color(0xFFF5F5F5)
private val NavyAccent = Color(0xFF0B2347)
private val CodeFieldBackground = Color.White
private val CodeFieldBorder = Color(0xFFDDDDDD)
private val CodeFieldBorderFocused = Color(0xFF0B2347)
private val CodeFieldBorderError = Color(0xFFE53935)
private val TextBlack = Color(0xFF1A1A1A)
private val TextGray = Color(0xFF666666)
private val TextLightGray = Color(0xFF999999)
private val ErrorRed = Color(0xFFE53935)

private const val PRIVACY_POLICY_LINK = "https://getapisportapp.site/bf-app/privacy/"

@Composable
fun OtpWaitingScreen(
    phoneNumber: String,
    onConfirmCode: (String) -> Unit,
    onResendCode: () -> Unit,
    onBackClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var code by remember { mutableStateOf("") }
    var resendTimerSeconds by remember { mutableIntStateOf(60) }
    var canResend by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    var showPolicyViewer by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    BackHandler {
    }

    if (showPolicyViewer) {
        PolicyViewerScreen(
            destination = PRIVACY_POLICY_LINK,
            onClose = { showPolicyViewer = false }
        )
        return
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    LaunchedEffect(resendTimerSeconds) {
        if (resendTimerSeconds > 0) {
            delay(1000)
            resendTimerSeconds--
        } else {
            canResend = true
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(WhiteBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(80.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NavyAccent, RoundedCornerShape(16.dp))
                    .padding(vertical = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.enter_sms_code_title),
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = stringResource(R.string.sms_sent_to_number),
                color = TextGray,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )

            Text(
                text = phoneNumber,
                color = TextBlack,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(32.dp))

            BasicTextField(
                value = code,
                onValueChange = { newValue ->
                    if (newValue.length <= 4 && newValue.all { it.isDigit() }) {
                        code = newValue
                        showError = false
                    }
                },
                modifier = Modifier.focusRequester(focusRequester),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                cursorBrush = SolidColor(Color.Transparent),
                textStyle = TextStyle(color = Color.Transparent),
                decorationBox = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        repeat(4) { index ->
                            OtpDigitBox(
                                digit = code.getOrNull(index)?.toString() ?: "",
                                isFocused = code.length == index,
                                isError = showError
                            )
                        }
                    }
                }
            )

            if (showError) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(R.string.invalid_code),
                    color = ErrorRed,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (canResend) {
                TextButton(
                    onClick = {
                        onResendCode()
                        resendTimerSeconds = 60
                        canResend = false
                        code = ""
                        showError = false
                    }
                ) {
                    Text(
                        text = stringResource(R.string.resend_code),
                        color = NavyAccent,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            } else {
                Text(
                    text = stringResource(R.string.resend_timer, resendTimerSeconds),
                    color = TextLightGray,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    showError = true
                    onConfirmCode(code)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = code.length == 4,
                colors = ButtonDefaults.buttonColors(
                    containerColor = NavyAccent,
                    disabledContainerColor = NavyAccent.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.confirm_button),
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            if (onBackClick != null) {
                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = onBackClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE0E0E0)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.back_button),
                        color = TextBlack,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = stringResource(R.string.terms_agreement_text),
                color = TextGray,
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = stringResource(R.string.terms_link),
                color = NavyAccent,
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.clickable {
                    showPolicyViewer = true
                }
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun OtpDigitBox(
    digit: String,
    isFocused: Boolean,
    isError: Boolean = false
) {
    val borderColor = when {
        isError -> CodeFieldBorderError
        isFocused -> CodeFieldBorderFocused
        else -> CodeFieldBorder
    }

    Box(
        modifier = Modifier
            .width(60.dp)
            .height(70.dp)
            .background(CodeFieldBackground, RoundedCornerShape(12.dp))
            .border(
                width = 2.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = digit,
            color = TextBlack,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
