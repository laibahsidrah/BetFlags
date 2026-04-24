package xyz.appmaker.pbyvul.ui.screens.teamdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import xyz.appmaker.pbyvul.data.api.models.CoachResponse
import xyz.appmaker.pbyvul.data.api.models.FixtureResponse
import xyz.appmaker.pbyvul.data.api.models.InjuryResponse
import xyz.appmaker.pbyvul.data.api.models.SquadResponse
import xyz.appmaker.pbyvul.data.api.models.TeamResponse
import xyz.appmaker.pbyvul.data.repository.FootballRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TeamDetailsViewModel @Inject constructor(
    private val repository: FootballRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val teamId: Int = checkNotNull(savedStateHandle["teamId"]) {
        "teamId is required"
    }

    val isFavorite: StateFlow<Boolean> = repository.isTeamFavorite(teamId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    private val _uiState = MutableStateFlow(TeamDetailsUiState())
    val uiState: StateFlow<TeamDetailsUiState> = _uiState.asStateFlow()

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
                _uiState.update {
                    it.copy(
                        teamInfo = bundle.teamInfo,
                        squad = bundle.squad,
                        coach = bundle.coach,
                        injuries = bundle.injuries,
                        recentFixtures = bundle.recentFixtures,
                        isLoading = false,
                        error = bundle.firstError()
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
                        teamInfo = bundle.teamInfo,
                        squad = bundle.squad,
                        coach = bundle.coach,
                        injuries = bundle.injuries,
                        recentFixtures = bundle.recentFixtures
                    )
                }
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            val favorite = isFavorite.value
            if (favorite) {
                repository.removeFavoriteTeam(teamId)
            } else {
                val team = _uiState.value.teamInfo?.team
                repository.addFavoriteTeam(
                    teamId = teamId,
                    name = team?.name ?: "",
                    logo = team?.logo ?: ""
                )
            }
        }
    }

    private suspend fun loadBundle(): TeamLoadBundle = coroutineScope {
        val teamDef = async { repository.getTeamInfo(teamId) }
        val squadDef = async { repository.getSquad(teamId) }
        val coachDef = async { repository.getCoach(teamId) }
        val injDef = async { repository.getInjuries(teamId, SEASON) }
        val fixDef = async { repository.getFixturesByTeam(teamId, SEASON) }

        val teamResult = teamDef.await()
        val squadResult = squadDef.await()
        val coachResult = coachDef.await()
        val injResult = injDef.await()
        val fixResult = fixDef.await()

        val recent = fixResult.getOrElse { emptyList() }
            .sortedByDescending { it.fixture?.timestamp ?: 0L }
            .take(5)

        TeamLoadBundle(
            teamInfo = teamResult.getOrNull(),
            teamError = teamResult.exceptionOrNull()?.message,
            squad = squadResult.getOrNull(),
            squadError = squadResult.exceptionOrNull()?.message,
            coach = coachResult.getOrNull(),
            coachError = coachResult.exceptionOrNull()?.message,
            injuries = injResult.getOrElse { emptyList() },
            injError = injResult.exceptionOrNull()?.message,
            recentFixtures = recent,
            fixturesError = fixResult.exceptionOrNull()?.message
        )
    }

    companion object {
        const val SEASON = 2024
    }
}

private data class TeamLoadBundle(
    val teamInfo: TeamResponse?,
    val teamError: String?,
    val squad: SquadResponse?,
    val squadError: String?,
    val coach: CoachResponse?,
    val coachError: String?,
    val injuries: List<InjuryResponse>,
    val injError: String?,
    val recentFixtures: List<FixtureResponse>,
    val fixturesError: String?
) {
    fun firstError(): String? = listOfNotNull(
        teamError,
        squadError,
        coachError,
        injError,
        fixturesError
    ).firstOrNull()
}

data class TeamDetailsUiState(
    val teamInfo: TeamResponse? = null,
    val squad: SquadResponse? = null,
    val coach: CoachResponse? = null,
    val injuries: List<InjuryResponse> = emptyList(),
    val recentFixtures: List<FixtureResponse> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)
