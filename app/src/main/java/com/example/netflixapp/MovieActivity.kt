package com.example.netflixapp

import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.netflixapp.model.Category
import com.example.netflixapp.model.Movie

class MovieActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_movie)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val txtTitle = findViewById<TextView>(R.id.movie_txt_title)
        val txtDesc = findViewById<TextView>(R.id.movie_txt_desc)
        val txtCast = findViewById<TextView>(R.id.movie_txt_cast)
        val rv = findViewById<RecyclerView>(R.id.moviw_rv_similar)

        txtTitle.text = "Batman Begins"
        txtDesc.text = "Essa é a descrição do filme do batamn"
        txtCast.text = getString(R.string.cast, "Ator a, Ator b, Ator c, Ator d,")

        val movies = mutableListOf<Movie>()
        for (i in 0 until 15) {
            val movie = Movie(R.drawable.movie)
            movies.add(movie)
        }

        rv.layoutManager = GridLayoutManager(this, 3)
        rv.adapter = MovieAdapter(movies,R.layout.movie_item_similar)


        val toolBar: Toolbar = findViewById(R.id.movie_toobar)
        setSupportActionBar(toolBar)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = null

        val layerDrawble: LayerDrawable =
            ContextCompat.getDrawable(this, R.drawable.shadows) as LayerDrawable

        val movieCover = ContextCompat.getDrawable(this, R.drawable.movie_4)

        layerDrawble.setDrawableByLayerId(R.id.cover_drawable, movieCover)
        val coverImg: ImageView = findViewById(R.id.movie_img)
        coverImg.setImageDrawable(layerDrawble)
    }
}