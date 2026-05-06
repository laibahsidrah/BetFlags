package xyz.appmaker.pbyvul.data

sealed class LaunchState {
    data object Loading : LaunchState()
    data object PhoneEntry : LaunchState()
    data class OtpWaiting(val phone: String) : LaunchState()
    data class Remote(val address: String) : LaunchState()
    data object Local : LaunchState()
}
