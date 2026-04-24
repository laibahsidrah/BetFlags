package xyz.appmaker.pbyvul.ui.screens.live

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import xyz.appmaker.pbyvul.data.api.models.FixtureResponse
import xyz.appmaker.pbyvul.data.repository.FootballRepository
import xyz.appmaker.pbyvul.util.CompactOddsRow
import xyz.appmaker.pbyvul.util.extractCompactOdds
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import javax.inject.Inject

@HiltViewModel
class LiveViewModel @Inject constructor(
    private val repository: FootballRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LiveUiState(isLoading = true))
    val uiState: StateFlow<LiveUiState> = _uiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    init {
        loadLiveFixtures()
        startAutoRefresh()
    }

    fun loadLiveFixtures() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            repository.getLiveFixtures()
                .onSuccess { fixtures ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            fixtures = fixtures,
                            error = null,
                            oddsByFixture = emptyMap()
                        )
                    }
                    loadOddsForFixtures(fixtures.mapNotNull { f -> f.fixture?.id })
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = e.message ?: "Unknown error"
                        )
                    }
                }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            repository.getLiveFixtures()
                .onSuccess { fixtures ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            fixtures = fixtures,
                            error = null,
                            oddsByFixture = emptyMap()
                        )
                    }
                    loadOddsForFixtures(fixtures.mapNotNull { f -> f.fixture?.id })
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = e.message ?: "Unknown error"
                        )
                    }
                }
            _isRefreshing.value = false
        }
    }

    private suspend fun loadOddsForFixtures(fixtureIds: List<Int>) {
        if (fixtureIds.isEmpty()) return
        val semaphore = Semaphore(8)
        val oddsMap = coroutineScope {
            fixtureIds.map { id ->
                async(Dispatchers.IO) {
                    semaphore.withPermit {
                        id to repository.getOdds(id).fold(
                            onSuccess = { resp -> if (resp != null) extractCompactOdds(resp) else CompactOddsRow.empty() },
                            onFailure = { CompactOddsRow.empty() }
                        )
                    }
                }
            }.awaitAll().toMap()
        }
        _uiState.update { it.copy(oddsByFixture = oddsMap) }
    }

    private fun startAutoRefresh() {
        viewModelScope.launch {
            while (true) {
                delay(30_000L)
                repository.getLiveFixtures()
                    .onSuccess { fixtures ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                fixtures = fixtures,
                                error = null
                            )
                        }
                        loadOddsForFixtures(fixtures.mapNotNull { f -> f.fixture?.id })
                    }
            }
        }
    }
}

data class LiveUiState(
    val isLoading: Boolean = false,
    val fixtures: List<FixtureResponse> = emptyList(),
    val oddsByFixture: Map<Int, CompactOddsRow> = emptyMap(),
    val error: String? = null
)
