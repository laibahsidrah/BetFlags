package xyz.appmaker.pbyvul.ui.screens.matches

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import xyz.appmaker.pbyvul.data.api.models.FixtureResponse
import xyz.appmaker.pbyvul.data.repository.FootballRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class MatchesViewModel @Inject constructor(
    private val repository: FootballRepository
) : ViewModel() {

    private val apiDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
        isLenient = false
    }

    private val _uiState = MutableStateFlow(
        MatchesUiState(
            isLoading = true,
            selectedDate = todayApiString()
        )
    )
    val uiState: StateFlow<MatchesUiState> = _uiState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    init {
        loadFixtures()
    }

    fun selectDate(date: String) {
        _uiState.update { it.copy(selectedDate = date) }
        loadFixtures()
    }

    fun loadFixtures() {
        viewModelScope.launch {
            val dateStr = _uiState.value.selectedDate
            _uiState.update { it.copy(isLoading = true, error = null) }
            repository.getFixturesByDate(dateStr)
                .onSuccess { fixtures ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            fixtures = fixtures,
                            error = null
                        )
                    }
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
            val dateStr = _uiState.value.selectedDate
            repository.getFixturesByDate(dateStr)
                .onSuccess { fixtures ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            fixtures = fixtures,
                            error = null
                        )
                    }
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

    private fun todayApiString(): String {
        return apiDateFormat.format(Calendar.getInstance().time)
    }
}

data class MatchesUiState(
    val isLoading: Boolean = false,
    val fixtures: List<FixtureResponse> = emptyList(),
    val error: String? = null,
    val selectedDate: String
)
