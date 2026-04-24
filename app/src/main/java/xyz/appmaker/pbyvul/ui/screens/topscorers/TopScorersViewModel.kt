package xyz.appmaker.pbyvul.ui.screens.topscorers

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import xyz.appmaker.pbyvul.data.api.models.PlayerResponse
import xyz.appmaker.pbyvul.data.repository.FootballRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TopScorersViewModel @Inject constructor(
    private val repository: FootballRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val leagueId: Int = checkNotNull(savedStateHandle["leagueId"]) {
        "leagueId is required"
    }

    private val season: Int = checkNotNull(savedStateHandle["season"]) {
        "season is required"
    }

    private val _uiState = MutableStateFlow(TopScorersUiState())
    val uiState: StateFlow<TopScorersUiState> = _uiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    init {
        loadAll()
    }

    fun loadAll() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val (scorers, assists) = coroutineScope {
                    val scorersDef = async { repository.getTopScorers(leagueId, season) }
                    val assistsDef = async { repository.getTopAssists(leagueId, season) }
                    scorersDef.await().getOrElse { emptyList() } to assistsDef.await()
                        .getOrElse { emptyList() }
                }
                _uiState.update {
                    it.copy(
                        topScorers = scorers,
                        topAssists = assists,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = e.message ?: "Unknown error")
                }
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                val (scorers, assists) = coroutineScope {
                    val scorersDef = async { repository.getTopScorers(leagueId, season) }
                    val assistsDef = async { repository.getTopAssists(leagueId, season) }
                    scorersDef.await().getOrElse { emptyList() } to assistsDef.await()
                        .getOrElse { emptyList() }
                }
                _uiState.update {
                    it.copy(topScorers = scorers, topAssists = assists)
                }
            } finally {
                _isRefreshing.value = false
            }
        }
    }
}

data class TopScorersUiState(
    val topScorers: List<PlayerResponse> = emptyList(),
    val topAssists: List<PlayerResponse> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)
