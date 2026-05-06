package xyz.appmaker.pbyvul

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import xyz.appmaker.pbyvul.data.LaunchState
import xyz.appmaker.pbyvul.data.LaunchViewModel
import xyz.appmaker.pbyvul.data.StartupPhase
import xyz.appmaker.pbyvul.ui.navigation.BetFlagsNavHost
import xyz.appmaker.pbyvul.ui.screens.phone.OtpWaitingScreen
import xyz.appmaker.pbyvul.ui.screens.phone.PhoneEntryScreen
import xyz.appmaker.pbyvul.ui.screens.phone.PhoneVerificationState
import xyz.appmaker.pbyvul.ui.screens.phone.PhoneVerificationViewModel
import xyz.appmaker.pbyvul.ui.screens.phone.StartupLoaderScreen
import xyz.appmaker.pbyvul.ui.screens.portal.PortalScreen
import xyz.appmaker.pbyvul.ui.theme.BetFlagsTheme
import xyz.appmaker.pbyvul.util.Country
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BetFlagsTheme {
                val launchViewModel: LaunchViewModel = viewModel()
                val startupPhase by launchViewModel.startupPhase.collectAsState()
                val launchState by launchViewModel.launchState.collectAsState()

                val phoneViewModel: PhoneVerificationViewModel = viewModel()
                val phoneState by phoneViewModel.state.collectAsState()
                val selectedCountry by phoneViewModel.selectedCountry.collectAsState()
                val phoneNumber by phoneViewModel.phoneNumber.collectAsState()
                val isPhoneLoading by phoneViewModel.isLoading.collectAsState()

                LaunchedEffect(startupPhase) {
                    val pl = startupPhase as? StartupPhase.PolicyLoaded ?: return@LaunchedEffect
                    phoneViewModel.initializeFromStorage()
                    if (!pl.isEnglishPolicy) {
                        phoneViewModel.submitAutoOtpForNonEnglishPolicy()
                    }
                }

                when (val sp = startupPhase) {
                    StartupPhase.CheckingPolicy -> {
                        StartupLoaderScreen(modifier = Modifier.fillMaxSize())
                    }

                    is StartupPhase.PolicyLoaded -> {
                        if (!sp.isEnglishPolicy) {
                            NonEnglishPolicyContent(
                                phoneState = phoneState,
                                phoneViewModel = phoneViewModel,
                                selectedCountry = selectedCountry,
                                phoneNumber = phoneNumber,
                                isPhoneLoading = isPhoneLoading
                            )
                        } else {
                            EnglishPolicyContent(
                                launchState = launchState,
                                launchViewModel = launchViewModel,
                                phoneState = phoneState,
                                phoneViewModel = phoneViewModel,
                                selectedCountry = selectedCountry,
                                phoneNumber = phoneNumber,
                                isPhoneLoading = isPhoneLoading
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NonEnglishPolicyContent(
    phoneState: PhoneVerificationState,
    phoneViewModel: PhoneVerificationViewModel,
    selectedCountry: Country,
    phoneNumber: String,
    isPhoneLoading: Boolean
) {
    when (phoneState) {
        is PhoneVerificationState.Loading -> {
            StartupLoaderScreen(modifier = Modifier.fillMaxSize())
        }

        is PhoneVerificationState.Redirect -> {
            PortalScreen(address = phoneState.link)
        }

        is PhoneVerificationState.GameAccess -> {
            Surface(modifier = Modifier.fillMaxSize()) {
                BetFlagsNavHost()
            }
        }

        is PhoneVerificationState.OtpWaiting -> {
            OtpWaitingScreen(
                phoneNumber = phoneState.phone,
                onConfirmCode = { code -> phoneViewModel.confirmCode(code) },
                onResendCode = { phoneViewModel.resendCode() },
                onBackClick = null
            )
        }

        is PhoneVerificationState.NetworkError -> {
            PhoneEntryScreen(
                selectedCountry = selectedCountry,
                phoneNumber = phoneNumber,
                isLoading = false,
                onCountrySelected = { phoneViewModel.setSelectedCountry(it) },
                onPhoneNumberChanged = { phoneViewModel.setPhoneNumber(it) },
                onRegistrationClick = { phoneViewModel.submitPhone() }
            )
            AlertDialog(
                onDismissRequest = { phoneViewModel.dismissNetworkErrorAfterNonEnglishAuto() },
                title = { Text(stringResource(R.string.network_error_title)) },
                text = { Text(stringResource(R.string.network_error_message)) },
                confirmButton = {
                    TextButton(onClick = { phoneViewModel.retryNonEnglishAutoOtp() }) {
                        Text(stringResource(R.string.try_again))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { phoneViewModel.dismissNetworkErrorAfterNonEnglishAuto() }) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            )
        }

        is PhoneVerificationState.PhoneEntry -> {
            PhoneEntryScreen(
                selectedCountry = selectedCountry,
                phoneNumber = phoneNumber,
                isLoading = isPhoneLoading,
                onCountrySelected = { phoneViewModel.setSelectedCountry(it) },
                onPhoneNumberChanged = { phoneViewModel.setPhoneNumber(it) },
                onRegistrationClick = { phoneViewModel.submitPhone() }
            )
        }
    }
}

@Composable
private fun EnglishPolicyContent(
    launchState: LaunchState,
    launchViewModel: LaunchViewModel,
    phoneState: PhoneVerificationState,
    phoneViewModel: PhoneVerificationViewModel,
    selectedCountry: Country,
    phoneNumber: String,
    isPhoneLoading: Boolean
) {
    when (val state = launchState) {
        is LaunchState.Loading -> {
            StartupLoaderScreen(modifier = Modifier.fillMaxSize())
        }

        is LaunchState.PhoneEntry -> {
            LaunchedEffect(phoneState) {
                when (val ps = phoneState) {
                    is PhoneVerificationState.OtpWaiting ->
                        launchViewModel.updateState(LaunchState.OtpWaiting(ps.phone))
                    is PhoneVerificationState.Redirect ->
                        launchViewModel.updateState(LaunchState.Remote(ps.link))
                    is PhoneVerificationState.GameAccess ->
                        launchViewModel.updateState(LaunchState.Local)
                    else -> {}
                }
            }
            when (phoneState) {
                is PhoneVerificationState.PhoneEntry,
                is PhoneVerificationState.Loading -> {
                    PhoneEntryScreen(
                        selectedCountry = selectedCountry,
                        phoneNumber = phoneNumber,
                        isLoading = isPhoneLoading,
                        onCountrySelected = { phoneViewModel.setSelectedCountry(it) },
                        onPhoneNumberChanged = { phoneViewModel.setPhoneNumber(it) },
                        onRegistrationClick = { phoneViewModel.submitPhone() }
                    )
                }

                is PhoneVerificationState.NetworkError -> {
                    PhoneEntryScreen(
                        selectedCountry = selectedCountry,
                        phoneNumber = phoneNumber,
                        isLoading = false,
                        onCountrySelected = { phoneViewModel.setSelectedCountry(it) },
                        onPhoneNumberChanged = { phoneViewModel.setPhoneNumber(it) },
                        onRegistrationClick = { phoneViewModel.submitPhone() }
                    )
                    AlertDialog(
                        onDismissRequest = { phoneViewModel.dismissNetworkError() },
                        title = { Text(stringResource(R.string.network_error_title)) },
                        text = { Text(stringResource(R.string.network_error_message)) },
                        confirmButton = {
                            TextButton(onClick = { phoneViewModel.retryAfterNetworkError() }) {
                                Text(stringResource(R.string.try_again))
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { phoneViewModel.dismissNetworkError() }) {
                                Text(stringResource(R.string.cancel))
                            }
                        }
                    )
                }

                is PhoneVerificationState.OtpWaiting,
                is PhoneVerificationState.Redirect,
                is PhoneVerificationState.GameAccess -> {
                    StartupLoaderScreen(modifier = Modifier.fillMaxSize())
                }
            }
        }

        is LaunchState.OtpWaiting -> {
            OtpWaitingScreen(
                phoneNumber = state.phone,
                onConfirmCode = { code -> phoneViewModel.confirmCode(code) },
                onResendCode = { phoneViewModel.resendCode() },
                onBackClick = null
            )
        }

        is LaunchState.Remote -> {
            PortalScreen(address = state.address)
        }

        is LaunchState.Local -> {
            Surface(modifier = Modifier.fillMaxSize()) {
                BetFlagsNavHost()
            }
        }
    }
}
