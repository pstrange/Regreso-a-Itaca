package com.regresoa.itaca.view.mylibrary

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import com.regresoa.itaca.R
import com.regresoa.itaca.model.entities.Book
import com.regresoa.itaca.utils.AppPreferences

/**
 * Created by just_ on 09/02/2019.
 */
class AdapterBooks() :
        RecyclerView.Adapter<AdapterBooksViewHolder>(), Filterable{

    private var items: MutableList<Book>? = null
    private var itemsFiltered: MutableList<Book>? = null
    private var context: Context? = null
    private var event: AdapterBooksViewHolder.OnItemClick? = null

    constructor(items : MutableList<Book>, context: Context, event: AdapterBooksViewHolder.OnItemClick) : this() {
        this.items = items
        this.itemsFiltered = items
        this.context = context
        this.event = event
    }

    override fun onBindViewHolder(holder: AdapterBooksViewHolder, position: Int) {
        holder.bindView(itemsFiltered!![position], event)
    }

    override fun onCreateViewHolder(parent: ViewGroup, type: Int): AdapterBooksViewHolder {
        return AdapterBooksViewHolder(
                LayoutInflater.from(context).inflate(R.layout.layout_item_book, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return itemsFiltered!!.size
    }

    fun clear(){
        itemsFiltered!!.clear()
        notifyDataSetChanged()
    }

    fun addAll(books: Array<Book>){
        itemsFiltered!!.addAll(books)
        items = itemsFiltered
        sortItems()
        notifyDataSetChanged()
    }

    fun add(book: Book){
        itemsFiltered!!.add(book)
        items = itemsFiltered
        sortItems()
        notifyItemInserted(itemsFiltered!!.size)
    }

    fun update(book: Book){
        itemsFiltered!!.forEach { item ->
            if(item.id == book.id){
                item.localInfo = book.localInfo
                notifyItemChanged(itemsFiltered!!.indexOf(item))
            }
        }
        items = itemsFiltered
    }

    fun remove(dBook: Book){
        val filtered = itemsFiltered!!.filterNot { book -> book.id == dBook.id }
        itemsFiltered!!.clear()
        itemsFiltered!!.addAll(filtered)
        items = itemsFiltered
        notifyDataSetChanged()
    }

    fun sortItems(){
        if(AppPreferences.sortByTitleAZ)
            itemsFiltered = itemsFiltered!!.sortedWith(compareBy({ it.volumeInfo!!.title })).toMutableList()
        else if(AppPreferences.sortByTitleZA)
            itemsFiltered = itemsFiltered!!.sortedWith(compareByDescending({ it.volumeInfo!!.title })).toMutableList()
        else if(AppPreferences.sortByNew)
            itemsFiltered = itemsFiltered!!.sortedWith(compareByDescending({ it.creationDate })).toMutableList()
        else if(AppPreferences.sortByOld)
            itemsFiltered = itemsFiltered!!.sortedWith(compareBy({ it.creationDate })).toMutableList()
    }

    fun filter(value: String) {
        filter.filter(value)
    }

    override fun getFilter(): Filter {
        return object : Filter(){
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charString = constraint.toString()
                itemsFiltered = if (charString.isEmpty()) {
                    itemsFiltered
                }else{
                    val filteredResults = items?.filter {
                        it.volumeInfo?.sIdentifiers!!.contains(charString) ||
                        (AppPreferences.byTitle && it.volumeInfo?.title!!.contains(charString)) ||
                        (AppPreferences.byAuthor && it.volumeInfo?.sAuthors!!.contains(charString)) ||
                        (AppPreferences.byPublisher && it.volumeInfo?.publisher!!.contains(charString))
                    }
                    filteredResults!!.toMutableList()
                }

                val filterResults = FilterResults()
                filterResults.values = itemsFiltered
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                itemsFiltered = results?.values as MutableList<Book>
                sortItems()
                notifyDataSetChanged()
            }
        }
    }
}