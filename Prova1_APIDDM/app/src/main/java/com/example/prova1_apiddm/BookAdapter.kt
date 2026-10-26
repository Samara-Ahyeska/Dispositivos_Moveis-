package com.example.prova1_apiddm

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class BookAdapter(private var bookList: List<VolumeItem>) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    class BookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivBookCover: ImageView = itemView.findViewById(R.id.ivBookCover)
        val tvBookTitle: TextView = itemView.findViewById(R.id.tvBookTitle)
        val tvBookAuthor: TextView = itemView.findViewById(R.id.tvBookAuthor)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_book, parent, false)
        return BookViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = bookList[position]
        val volumeInfo = book.volumeInfo

        holder.tvBookTitle.text = volumeInfo?.title ?: "Título desconhecido"
        holder.tvBookAuthor.text = volumeInfo?.authors?.joinToString(", ") ?: "Autor desconhecido"

        var thumbnail = volumeInfo?.imageLinks?.thumbnail ?: ""
        if (thumbnail.startsWith("http://")) {
            thumbnail = thumbnail.replace("http://", "https://")
        }

        if (thumbnail.isNotEmpty()) {
            Glide.with(holder.itemView.context)
                .load(thumbnail)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_report_image)
                .into(holder.ivBookCover)
        } else {
            holder.ivBookCover.setImageResource(android.R.drawable.ic_menu_report_image)
        }
    }

    override fun getItemCount(): Int = bookList.size

    fun updateData(newList: List<VolumeItem>) {
        bookList = newList
        notifyDataSetChanged()
    }
}