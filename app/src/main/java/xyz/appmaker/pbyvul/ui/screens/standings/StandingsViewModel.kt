package xyz.appmaker.pbyvul.ui.screens.standings

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import xyz.appmaker.pbyvul.data.api.models.StandingEntry
import xyz.appmaker.pbyvul.data.repository.FootballRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StandingsViewModel @Inject constructor(
    private val repository: FootballRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val leagueId: Int = checkNotNull(savedStateHandle["leagueId"]) {
        "leagueId is required"
    }

    private val season: Int = checkNotNull(savedStateHandle["season"]) {
        "season is required"
    }

    private val _uiState = MutableStateFlow(StandingsUiState())
    val uiState: StateFlow<StandingsUiState> = _uiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    init {
        loadStandings()
    }

    fun loadStandings() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            repository.getStandings(leagueId, season)
                .onSuccess { list ->
                    val league = list.firstOrNull()?.league
                    val table = league?.standings?.firstOrNull().orEmpty()
                    _uiState.update {
                        it.copy(
                            standings = table,
                            leagueName = league?.name,
                            isLoading = false,
                            error = null
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(isLoading = false, error = e.message ?: "Unknown error")
                    }
                }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            repository.getStandings(leagueId, season)
                .onSuccess { list ->
                    val league = list.firstOrNull()?.league
                    val table = league?.standings?.firstOrNull().orEmpty()
                    _uiState.update {
                        it.copy(
                            standings = table,
                            leagueName = league?.name
                        )
                    }
                }
            _isRefreshing.value = false
        }
    }
}

data class StandingsUiState(
    val standings: List<StandingEntry> = emptyList(),
    val leagueName: String? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)
