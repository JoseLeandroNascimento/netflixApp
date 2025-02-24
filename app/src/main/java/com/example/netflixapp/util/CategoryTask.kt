package com.example.netflixapp.util

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.netflixapp.model.Category
import com.example.netflixapp.model.Movie
import org.json.JSONObject
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors
import javax.net.ssl.HttpsURLConnection

class CategoryTask(private val callback:Callback) {

    private val handle = Handler(Looper.getMainLooper())
    interface Callback{
        fun onResult(categories: List<Category>)
    }

    fun execute(url: String) {

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

                if (statusCode > 400) {
                    throw IOException("Erro na comunicação com o servidor")
                }

                stream = urlConnection.inputStream
//                val jsonString = stream.bufferedReader().use {
//                    it.readText()
//                }

                buffer = BufferedInputStream(stream)
                val jsonString = toString(buffer)

                val categories = toCategory(jsonString)
                Log.i("Teste",categories.toString())

                handle.post{
                    callback.onResult(categories)
                }


            } catch (e: IOException) {

                Log.e("Teste", e.message ?: "Erro desconhecido", e)
            }finally {
                urlConnection?.disconnect()
                buffer?.close()
                stream?.close()
            }

        }

    }

    private fun toCategory(jsonAsString: String): List<Category> {
        val categories = mutableListOf<Category>()

        val jsonRoot = JSONObject(jsonAsString)
        val jsonCategories = jsonRoot.getJSONArray("category")

        for (i in 0 until  jsonCategories.length()) {
            val jsonCategory = jsonCategories.getJSONObject(i)

            val title = jsonCategory.getString("title")
            val jsonMovies = jsonCategory.getJSONArray("movie")

            val movies = mutableListOf<Movie>()

            for (j in 0 until jsonMovies.length()) {
                val jsonMovie = jsonMovies.getJSONObject(j)
                val id = jsonMovie.getInt("id")
                val coverUrl = jsonMovie.getString("cover_url")

                movies.add(Movie(id, coverUrl))
            }

            categories.add(Category(title, movies))
        }

        return categories
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