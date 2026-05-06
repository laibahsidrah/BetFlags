package xyz.appmaker.pbyvul.data

sealed class StartupPhase {
    data object CheckingPolicy : StartupPhase()
    data class PolicyLoaded(val isEnglishPolicy: Boolean) : StartupPhase()
}
