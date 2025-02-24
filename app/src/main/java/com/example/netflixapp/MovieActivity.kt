package com.example.netflixapp

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.netflixapp.model.Movie
import com.example.netflixapp.model.MovieDetails
import com.example.netflixapp.util.MovieTask
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target

class MovieActivity : AppCompatActivity(), MovieTask.Callback {

    private lateinit var txtTitle: TextView
    private lateinit var txtDesc: TextView
    private lateinit var txtCast: TextView
    private lateinit var rv: RecyclerView
    private lateinit var progress: ProgressBar
    private lateinit var adapter: MovieAdapter
    private val movies = mutableListOf<Movie>()
    private lateinit var imgMovie: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_movie)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        txtTitle = findViewById<TextView>(R.id.movie_txt_title)
        txtDesc = findViewById<TextView>(R.id.movie_txt_desc)
        txtCast = findViewById<TextView>(R.id.movie_txt_cast)
        progress = findViewById(R.id.progress_movie_details)
        rv = findViewById<RecyclerView>(R.id.moviw_rv_similar)
        imgMovie = findViewById(R.id.movie_img)

        val id =
            intent?.getIntExtra("id", 0)

        Log.i("Teste", id.toString())


        val url =
            "https://atway.tiagoaguiar.co/fenix/netflixapp/movie/${id}?apiKey=43c81700-f6da-4b67-8c08-1725e84fadf5"
//        https://atway.tiagoaguiar.co/fenix/netflixapp/movie/1?apiKey=43c81700-f6da-4b67-8c08-1725e84fadf5
        txtTitle.text = "Batman Begins"
        txtDesc.text = "Essa é a descrição do filme do batamn"
        txtCast.text = getString(R.string.cast, "Ator a, Ator b, Ator c, Ator d,")

        MovieTask(this).execute(url)


        adapter = MovieAdapter(movies, R.layout.movie_item_similar)

        rv.layoutManager = GridLayoutManager(this, 3)
        rv.adapter = adapter


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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPreExecute() {
        progress.visibility = View.VISIBLE
    }

    override fun onResult(movieDetails: MovieDetails) {
        progress.visibility = View.GONE

        txtTitle.text = movieDetails.movie.title
        txtDesc.text = movieDetails.movie.desc
        txtCast.text = movieDetails.movie.cast

        movies.clear()
        movies.addAll(movieDetails.similares)
        Picasso.get().load(movieDetails.movie.coverUrl).into(
            (object : Target {

                override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                    val layerDrawable: LayerDrawable = ContextCompat.getDrawable(
                        this@MovieActivity,
                        R.drawable.shadows
                    ) as LayerDrawable
                    val movieCover = BitmapDrawable(resources, bitmap)
                    layerDrawable.setDrawableByLayerId(R.id.cover_drawable, movieCover)
                    val coverImg: ImageView = findViewById(R.id.movie_img)
                    coverImg.setImageDrawable(layerDrawable)
                }

                override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                }

                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                }

            })
        )

        adapter.notifyDataSetChanged()


    }

    override fun onFailure(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        progress.visibility = View.GONE

    }
}