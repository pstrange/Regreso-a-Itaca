package com.regresoa.itaca.view.mylibrary

import android.support.v7.widget.RecyclerView
import android.view.View
import com.bumptech.glide.Glide
import com.regresoa.itaca.model.entities.Book
import kotlinx.android.synthetic.main.layout_item_book.view.*

/**
 * Created by just_ on 09/02/2019.
 */
class AdapterBooksViewHolder(val view: View) : RecyclerView.ViewHolder(view){

    private var bookItem: Book? = null
    private var listener: OnItemClick? = null

    fun bindView(book: Book?, event: OnItemClick?){
        bookItem = book
        listener = event
        book?.volumeInfo?.let {
            view.text_title.text = it.title
            view.text_editorial.text = it.publisher
            view.text_year.text = it.publishedDate
            it.authors?.let {
                val sb = StringBuilder()
                for (author in it)
                    sb.append(if(sb.isNotEmpty()) ", "+author else author)
                view.text_author.text = sb.toString()
            }
            it.imageLinks?.let {
                val imageUrl = it.thumbnail.replace("[\\\\]".toRegex(), "")
                Glide.with(view.context)
                        .load(imageUrl)
                        .into(view.image_thumb)
            }
        }
        view.layout_local_info.visibility = View.GONE
        book?.localInfo?.let{
            view.layout_local_info.visibility = View.VISIBLE
            it.stock?.let { view.text_stock.text = "Cantidad: "+it }
            it.price?.let { view.text_price.text = "$"+it }
        }
        view.setOnClickListener{
            listener?.let {
                listener?.onClickItem(bookItem)
            }
        }
    }

    abstract class OnItemClick{
        abstract fun onClickItem(book: Book?)
    }

}