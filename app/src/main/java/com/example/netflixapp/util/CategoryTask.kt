package com.example.netflixapp.util

import android.util.Log
import java.io.IOException
import java.net.URL
import java.util.concurrent.Executors
import javax.net.ssl.HttpsURLConnection

class CategoryTask {

    fun execute(url: String) {

        val executor = Executors.newSingleThreadExecutor()

        executor.execute {

            try {

                val requestURL = URL(url)
                val urlConnection = requestURL.openConnection() as HttpsURLConnection
                urlConnection.readTimeout = 2000
                urlConnection.connectTimeout = 2000
                val statusCode = urlConnection.responseCode

                if (statusCode > 400) {
                    throw IOException("Erro na comunicação com o servidor")
                }

                val stream = urlConnection.inputStream
                val jsonString = stream.bufferedReader().use {
                    it.readText()
                }


                Log.i("Teste",jsonString)

            } catch (e: IOException) {

                Log.e("Teste", e.message ?: "Erro desconhecido",e)
            }

        }

    }

}