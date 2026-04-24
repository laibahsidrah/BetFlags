package xyz.appmaker.pbyvul.ui.screens.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import xyz.appmaker.pbyvul.data.local.entity.FavoriteLeagueEntity
import xyz.appmaker.pbyvul.data.local.entity.FavoriteTeamEntity
import xyz.appmaker.pbyvul.data.repository.FootballRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val repository: FootballRepository
) : ViewModel() {

    val favoriteTeams: StateFlow<List<FavoriteTeamEntity>> = repository.getFavoriteTeams()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteLeagues: StateFlow<List<FavoriteLeagueEntity>> = repository.getFavoriteLeagues()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun removeTeam(teamId: Int) {
        viewModelScope.launch { repository.removeFavoriteTeam(teamId) }
    }

    fun removeLeague(leagueId: Int) {
        viewModelScope.launch { repository.removeFavoriteLeague(leagueId) }
    }
}
