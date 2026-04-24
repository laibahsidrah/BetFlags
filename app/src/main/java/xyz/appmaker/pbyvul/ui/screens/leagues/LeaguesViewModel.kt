package xyz.appmaker.pbyvul.ui.screens.leagues

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import xyz.appmaker.pbyvul.data.api.models.LeagueResponse
import xyz.appmaker.pbyvul.data.repository.FootballRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LeaguesViewModel @Inject constructor(
    private val repository: FootballRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LeaguesState())
    val state: StateFlow<LeaguesState> = _state.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private var allLeagues: List<LeagueResponse> = emptyList()

    init {
        loadLeagues()
    }

    fun loadLeagues() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            repository.getLeagues()
                .onSuccess { leagues ->
                    allLeagues = leagues
                    applyFilterAndGroup()
                }
                .onFailure {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = it.message ?: "Failed to load leagues",
                        leagues = emptyList(),
                        leaguesByCountry = emptyMap()
                    )
                }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            repository.getLeagues()
                .onSuccess { leagues ->
                    allLeagues = leagues
                    applyFilterAndGroup()
                }
                .onFailure {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = it.message ?: "Failed to load leagues",
                        leagues = emptyList(),
                        leaguesByCountry = emptyMap()
                    )
                }
            _isRefreshing.value = false
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        applyFilterAndGroup()
    }

    private fun applyFilterAndGroup() {
        val query = _searchQuery.value.lowercase()
        val filtered = if (query.isBlank()) {
            allLeagues
        } else {
            allLeagues.filter {
                it.league?.name?.lowercase()?.contains(query) == true ||
                    it.country?.name?.lowercase()?.contains(query) == true
            }
        }
        val grouped = groupByCountryWithMajorFirst(filtered)
        _state.value = LeaguesState(
            isLoading = false,
            error = null,
            searchQuery = _searchQuery.value,
            leagues = filtered,
            leaguesByCountry = grouped
        )
    }

    private fun groupByCountryWithMajorFirst(leagues: List<LeagueResponse>): Map<String, List<LeagueResponse>> {
        val raw = leagues.groupBy { it.country?.name ?: "Other" }
        return raw.mapValues { (_, list) -> list.sortedWith(compareLeaguesMajorFirst()) }
            .toSortedMap(countryComparator())
    }

    private fun countryComparator(): Comparator<String> = compareBy<String> { country ->
        val idx = CountryPriority.indexOf(country)
        if (idx >= 0) idx else Int.MAX_VALUE
    }.thenBy { it.lowercase() }

    private fun compareLeaguesMajorFirst(): Comparator<LeagueResponse> = Comparator { a, b ->
        val idA = a.league?.id
        val idB = b.league?.id
        val idxA = MajorLeagueIds.indexOf(idA ?: -1).takeIf { it >= 0 } ?: Int.MAX_VALUE
        val idxB = MajorLeagueIds.indexOf(idB ?: -1).takeIf { it >= 0 } ?: Int.MAX_VALUE
        when {
            idxA != idxB -> idxA.compareTo(idxB)
            else -> (a.league?.name ?: "").compareTo(b.league?.name ?: "", ignoreCase = true)
        }
    }

    companion object {
        private val MajorLeagueIds = listOf(39, 140, 135, 78, 61)
        private val CountryPriority = listOf("England", "Spain", "Italy", "Germany", "France")
    }
}

data class LeaguesState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val searchQuery: String = "",
    val leagues: List<LeagueResponse> = emptyList(),
    val leaguesByCountry: Map<String, List<LeagueResponse>> = emptyMap()
)
