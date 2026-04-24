package xyz.appmaker.pbyvul.ui.screens.playerdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import xyz.appmaker.pbyvul.data.api.models.PlayerResponse
import xyz.appmaker.pbyvul.data.repository.FootballRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerDetailsViewModel @Inject constructor(
    private val repository: FootballRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val playerId: Int = checkNotNull(savedStateHandle["playerId"]) {
        "playerId is required"
    }

    private val season: Int = checkNotNull(savedStateHandle["season"]) {
        "season is required"
    }

    private val _uiState = MutableStateFlow(PlayerDetailsUiState())
    val uiState: StateFlow<PlayerDetailsUiState> = _uiState.asStateFlow()

    init {
        loadPlayer()
    }

    fun loadPlayer() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = repository.getPlayerById(playerId, season)
            result.fold(
                onSuccess = { player ->
                    _uiState.update {
                        it.copy(
                            player = player,
                            isLoading = false,
                            error = if (player == null) "Player not found" else null
                        )
                    }
                },
                onFailure = { e ->
                    _uiState.update {
                        it.copy(isLoading = false, error = e.message ?: "Unknown error")
                    }
                }
            )
        }
    }
}

data class PlayerDetailsUiState(
    val player: PlayerResponse? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)
