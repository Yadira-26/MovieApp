package com.example.movieapp.network

import android.util.Log
import com.example.movieapp.model.MovieResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.bodyAsText
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json


object MovieService {
    // RECUERDA: Reemplaza "TU_CLAVE_DE_API" con tu clave real
    private const val API_KEY = "ac25f48cd14030e28f537256074457cd"
    private const val BASE_URL = "https://api.themoviedb.org/3/"
    private const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500" // Para construir la URL completa del póster

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {

                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
    }

    suspend fun getPopularMovies(language: String = "es-ES", page: Int = 1): MovieResponse? {
        return try {
            client.get("${BASE_URL}movie/popular") {
                parameter("api_key", API_KEY)
                parameter("language", language)
                parameter("page", page)
            }.body()
        } catch (e: Exception) {
            // Maneja errores de red o deserialización aquí
            e.printStackTrace()
            null
        }
    }

    suspend fun searchMovies(query: String, page: Int = 1): MovieResponse {
        return try { // Añade un try-catch aquí para más detalles del error de deserialización
            val response = client.get("${BASE_URL}search/movie") {
                parameter("api_key", API_KEY)
                parameter("query", query)
                parameter("page", page)
                parameter("language", "es-ES")
            }
            Log.d("MovieService", "Raw search response: ${response.bodyAsText()}")
            response.body() // Aquí ocurre la deserialización
        } catch (e: Exception) {
            Log.e("MovieService", "Error deserializing search response: ${e.message}", e)
            // Lanza una excepción personalizada o devuelve un estado de error si prefieres
            // en lugar de dejar que la app crashee directamente.
            // Por ahora, para depurar, relanzar puede estar bien o devolver un MovieResponse vacío/nulo.
            throw e // O maneja el error de forma más elegante
        }
    }

    fun getPosterUrl(posterPath: String?): String? {
        return posterPath?.let { "$IMAGE_BASE_URL$it" }
    }
}