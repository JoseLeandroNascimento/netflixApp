package com.example.netflixapp.model

data class MovieDetails(
    val movie:Movie,
    val similares: List<Movie>
)
