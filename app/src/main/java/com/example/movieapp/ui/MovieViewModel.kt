package com.example.movieapp.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
//import androidx.compose.ui.test.cancel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieapp.model.Movie
import com.example.movieapp.network.MovieService
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job

class MovieViewModel : ViewModel() {
    // Estados y lógica para películas populares existentes
    var popularMovies by mutableStateOf<List<Movie>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    // Nuevos estados para la búsqueda
    var searchQuery by mutableStateOf("")
        private set // Solo el ViewModel puede modificar directamente

    var searchedMovies by mutableStateOf<List<Movie>>(emptyList())
    var isLoadingSearch by mutableStateOf(false)
    var errorMessageSearch by mutableStateOf<String?>(null)

    // Declare searchJob here
    private var searchJob: Job? = null

    init {
        fetchPopularMovies()
    }

    fun fetchPopularMovies() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val response = MovieService.getPopularMovies(language = "es-ES")
                popularMovies = response?.results ?: emptyList()
            } catch (e: Exception) {
                errorMessage = "Error al cargar las películas: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    // Nueva función para actualizar el query de búsqueda
    fun onSearchQueryChange(newQuery: String) {
        searchQuery = newQuery
        searchJob?.cancel() // Cancela la búsqueda anterior si el usuario sigue escribiendo
        if (newQuery.length > 2) { // O la longitud que consideres para empezar a buscar
            searchJob = viewModelScope.launch {
                delay(500) // Debounce: espera 500ms después de que el usuario deja de escribir
                executeSearch(newQuery)
            }
        } else {
            searchedMovies = emptyList() // Limpia los resultados si el query es muy corto
            errorMessageSearch = null
        }
    }

    private fun executeSearch(query: String) {
        isLoadingSearch = true
        errorMessageSearch = null
        viewModelScope.launch {
            try {
                val response = MovieService.searchMovies(query)
                searchedMovies = response.results
                if (searchedMovies.isEmpty()) {
                    errorMessageSearch = "No se encontraron películas para '$query'."
                }
            } catch (e: Exception) {
                errorMessageSearch = "Error al buscar películas: ${e.message}"
            } finally {
                isLoadingSearch = false
            }
        }
    }
}