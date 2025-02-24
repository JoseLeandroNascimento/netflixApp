package com.example.netflixapp.util

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.netflixapp.model.Movie
import com.example.netflixapp.model.MovieDetails
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors
import javax.net.ssl.HttpsURLConnection

class MovieTask(private val callback: Callback) {

    private val handle = Handler(Looper.getMainLooper())

    interface Callback {
        fun onPreExecute()
        fun onResult(movieDetails: MovieDetails)
        fun onFailure(message: String)
    }

    fun execute(url: String) {

        callback.onPreExecute()
        val executor = Executors.newSingleThreadExecutor()

        executor.execute {

            var urlConnection: HttpURLConnection? = null
            var buffer: BufferedInputStream? = null
            var stream: InputStream? = null

            try {

                val requestURL = URL(url)
                urlConnection = requestURL.openConnection() as HttpsURLConnection
                urlConnection.readTimeout = 2000
                urlConnection.connectTimeout = 2000
                val statusCode = urlConnection.responseCode

                if (statusCode == 400) {
                    stream = urlConnection.errorStream
                    buffer = BufferedInputStream(stream)

                    val jsonAsString = toString(buffer)
                    val json = JSONObject(jsonAsString)

                    val message = json.getString("message")
                    throw IOException(message)


                } else if (statusCode > 400) {
                    throw IOException("Erro na comunicação com o servidor")
                }

                stream = urlConnection.inputStream

                buffer = BufferedInputStream(stream)
                val jsonString = toString(buffer)

                val movieDetaisl = toMovieDetails(jsonString)
                Log.i("Teste", movieDetaisl.toString())

                handle.post {
                    callback.onResult(movieDetaisl)
                }


            } catch (e: IOException) {

                Log.e("Teste", e.message ?: "Erro desconhecido", e)
                handle.post {
                    callback.onFailure(e.message ?: "Erro desconhecido")
                }
            } finally {
                urlConnection?.disconnect()
                buffer?.close()
                stream?.close()
            }

        }

    }

    private fun  toMovieDetails(jsonAsString:String):MovieDetails{
        val json = JSONObject(jsonAsString)

        val id = json.getInt("id")
        val title = json.getString("title")
        val desc = json.getString("cast")
        val coverUrl = json.getString("cover_url")

        val jsonMovies = json.getJSONArray("movie")

        val similares = mutableListOf<Movie>()

        for( i in 0 until jsonMovies.length()){
            val jsonMovie = jsonMovies.getJSONObject(i)
            val similarId = jsonMovie.getInt("id")
            val similarCoverUrl = jsonMovie.getString("cover_url")

            val m = Movie(similarId,similarCoverUrl)

            similares.add(m)
        }
        val movie = Movie(id,coverUrl,title,desc)
        return MovieDetails(movie,similares)

    }

    private fun toString(stream: InputStream): String {
        val bytes = ByteArray(1024)
        val baos = ByteArrayOutputStream()
        var read: Int
        while (true) {
            read = stream.read(bytes)
            if (read <= 0) {
                break
            }
            baos.write(bytes, 0, read)
        }

        return String(baos.toByteArray())
    }

}