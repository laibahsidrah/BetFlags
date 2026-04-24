package xyz.appmaker.pbyvul.ui.screens.matchdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import xyz.appmaker.pbyvul.data.api.models.FixtureEvent
import xyz.appmaker.pbyvul.data.api.models.FixtureResponse
import xyz.appmaker.pbyvul.data.api.models.FixtureStatisticsResponse
import xyz.appmaker.pbyvul.data.api.models.LineupResponse
import xyz.appmaker.pbyvul.data.api.models.OddsResponse
import xyz.appmaker.pbyvul.data.api.models.PredictionResponse
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
class MatchDetailsViewModel @Inject constructor(
    private val repository: FootballRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val fixtureId: Int = checkNotNull(savedStateHandle["fixtureId"]) {
        "fixtureId is required"
    }

    private val _uiState = MutableStateFlow(MatchDetailsUiState())
    val uiState: StateFlow<MatchDetailsUiState> = _uiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    init {
        loadAll()
    }

    fun loadAll() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val bundle = loadBundle()
                val firstError = listOfNotNull(
                    bundle.fixtureError,
                    bundle.eventsError,
                    bundle.lineupsError,
                    bundle.statsError,
                    bundle.predError,
                    bundle.oddsError
                ).firstOrNull()
                _uiState.update {
                    it.copy(
                        fixture = bundle.fixture,
                        events = bundle.events,
                        lineups = bundle.lineups,
                        statistics = bundle.statistics,
                        prediction = bundle.prediction,
                        odds = bundle.odds,
                        headToHead = bundle.headToHead,
                        isLoading = false,
                        error = firstError
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
                val bundle = loadBundle()
                _uiState.update {
                    it.copy(
                        fixture = bundle.fixture,
                        events = bundle.events,
                        lineups = bundle.lineups,
                        statistics = bundle.statistics,
                        prediction = bundle.prediction,
                        odds = bundle.odds,
                        headToHead = bundle.headToHead
                    )
                }
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    private suspend fun loadBundle(): MatchLoadBundle = coroutineScope {
        val fixtureDef = async { repository.getFixtureById(fixtureId) }
        val eventsDef = async { repository.getFixtureEvents(fixtureId) }
        val lineupsDef = async { repository.getFixtureLineups(fixtureId) }
        val statsDef = async { repository.getFixtureStatistics(fixtureId) }
        val predDef = async { repository.getPredictions(fixtureId) }
        val oddsDef = async { repository.getOdds(fixtureId) }

        val fixtureResult = fixtureDef.await()
        val fixture = fixtureResult.getOrNull()

        val h2h = if (fixture?.teams?.home?.id != null && fixture.teams?.away?.id != null) {
            repository.getHeadToHead(
                fixture.teams.home.id!!,
                fixture.teams.away.id!!
            ).getOrElse { emptyList() }
        } else {
            emptyList()
        }

        val eventsResult = eventsDef.await()
        val lineupsResult = lineupsDef.await()
        val statsResult = statsDef.await()
        val predResult = predDef.await()
        val oddsResult = oddsDef.await()

        MatchLoadBundle(
            fixture = fixture,
            fixtureError = fixtureResult.exceptionOrNull()?.message,
            events = eventsResult.getOrElse { emptyList() },
            eventsError = eventsResult.exceptionOrNull()?.message,
            lineups = lineupsResult.getOrElse { emptyList() },
            lineupsError = lineupsResult.exceptionOrNull()?.message,
            statistics = statsResult.getOrElse { emptyList() },
            statsError = statsResult.exceptionOrNull()?.message,
            prediction = predResult.getOrNull(),
            predError = predResult.exceptionOrNull()?.message,
            odds = oddsResult.getOrNull(),
            oddsError = oddsResult.exceptionOrNull()?.message,
            headToHead = h2h
        )
    }
}

private data class MatchLoadBundle(
    val fixture: FixtureResponse?,
    val fixtureError: String?,
    val events: List<FixtureEvent>,
    val eventsError: String?,
    val lineups: List<LineupResponse>,
    val lineupsError: String?,
    val statistics: List<FixtureStatisticsResponse>,
    val statsError: String?,
    val prediction: PredictionResponse?,
    val predError: String?,
    val odds: OddsResponse?,
    val oddsError: String?,
    val headToHead: List<FixtureResponse>
)

data class MatchDetailsUiState(
    val fixture: FixtureResponse? = null,
    val events: List<FixtureEvent> = emptyList(),
    val lineups: List<LineupResponse> = emptyList(),
    val statistics: List<FixtureStatisticsResponse> = emptyList(),
    val prediction: PredictionResponse? = null,
    val odds: OddsResponse? = null,
    val headToHead: List<FixtureResponse> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)
