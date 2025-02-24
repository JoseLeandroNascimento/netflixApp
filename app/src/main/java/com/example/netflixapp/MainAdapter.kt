package com.example.netflixapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.netflixapp.model.Category

class MainAdapter(
    private val categories: List<Category>,
    private val onItemClickLister: ((Int) -> Unit)? = null
) :
    RecyclerView.Adapter<MainAdapter.CategorieHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategorieHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.category_item, parent, false)
        return CategorieHolder(view)
    }

    override fun getItemCount(): Int {
        return categories.size
    }

    override fun onBindViewHolder(holder: CategorieHolder, position: Int) {

        val category = categories[position]
        holder.bind(category)
    }

    inner class CategorieHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(category: Category) {

            val titleCategory = itemView.findViewById<TextView>(R.id.txt_title)
            val rvCategory = itemView.findViewById<RecyclerView>(R.id.rv_category)

            titleCategory.text = category.name
            rvCategory.layoutManager =
                LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
            rvCategory.adapter = MovieAdapter(category.movies, R.layout.movie_item,onItemClickLister)
        }
    }


}