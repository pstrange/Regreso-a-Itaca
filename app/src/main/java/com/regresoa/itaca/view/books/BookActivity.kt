package com.regresoa.itaca.view.books

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.regresoa.itaca.R
import com.regresoa.itaca.model.entities.Book
import com.regresoa.itaca.model.entities.LocalInfo
import com.regresoa.itaca.model.repositories.BooksRepository
import com.regresoa.itaca.view.widgets.LayoutFieldView
import com.regresoa.itaca.viewmodel.BooksViewModel
import kotlinx.android.synthetic.main.activity_book.*
import java.util.*

/**
 * Created by just_ on 10/02/2019.
 */
class BookActivity : AppCompatActivity() {

    private lateinit var book: Book
    private val viewModel: BooksViewModel = BooksViewModel(BooksRepository())

    companion object {
        val EXTRA_BOOK = "EXTRA_BOOK"
        val RESULT_ADDED = 11
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book)

        setSupportActionBar(toolbar)
        supportActionBar!!.title = ""
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)


        book = Gson().fromJson(intent.getStringExtra(EXTRA_BOOK), Book::class.java)
        fillBook(book)
    }

    fun fillBook(book: Book){
        book.volumeInfo?.let {

            it.imageLinks?.let {
                val imageUrl = it.thumbnail.replace("[\\\\]".toRegex(), "")
                Glide.with(this)
                        .load(imageUrl)
                        .into(image_cover)
            }

            it.title?.let { text_title.text = it }
            it.sAuthors?.let { text_author.text = it }

            var viewTemp = LayoutFieldView(this, "Editorial", "Sin información")
            container_extra.addView(viewTemp)
            it.publisher?.let { viewTemp.setValue(it) }

            viewTemp = LayoutFieldView(this, "Fecha", "Sin información")
            container_extra.addView(viewTemp)
            it.publishedDate?.let { viewTemp.setValue(it) }

            viewTemp = LayoutFieldView(this, "Categorias", "Sin información")
            container_extra.addView(viewTemp)
            it.sCategories?.let { viewTemp.setValue(it) }

            viewTemp = LayoutFieldView(this, "Descripcion", "Sin información")
            container_extra.addView(viewTemp)
            it.description?.let { viewTemp.setValue(it) }

            viewTemp = LayoutFieldView(this, "Identificadores", "Sin información")
            container_extra.addView(viewTemp)
            it.industryIdentifiers?.let {
                val sb = StringBuilder()
                it.map { it.identifier }
                        .forEach { sb.append(if(sb.isNotEmpty()) "\n"+ it else it) }
                viewTemp.setValue(sb.toString())
            }

            viewTemp = LayoutFieldView(this, "Lenguaje", "Sin información")
            container_extra.addView(viewTemp)
            it.language?.let { viewTemp.setValue(it) }

            viewTemp = LayoutFieldView(this, "Paginas", "Sin información")
            container_extra.addView(viewTemp)
            it.pageCount?.let { viewTemp.setValue(it.toString()) }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(
                if(book.localInfo == null) R.menu.menu_book_remote
                else R.menu.menu_book_local, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId){
            android.R.id.home -> {
                finish()
            }
            R.id.action_addnew -> {
                book.creationDate = Calendar.getInstance().timeInMillis
                showDialogInfo(object : LocalInfoDialog.OnLocalInfoEdit{
                    override fun onLocalInfoSave(localInfo: LocalInfo) {
                        book.localInfo = localInfo
                        viewModel.addBookToMyLibrary(book)
                        setResult(RESULT_ADDED)
                        finish()
                    }
                })
            }
            R.id.action_info -> {
                showDialogInfo(object : LocalInfoDialog.OnLocalInfoEdit{
                    override fun onLocalInfoSave(localInfo: LocalInfo) {
                        book.localInfo = localInfo
                        viewModel.updateBookFromMyLibrary(book)
                        finish()
                    }
                })
            }
            R.id.action_edit -> {
                val intent = Intent(this, NewBookActivity::class.java)
                intent.putExtra(NewBookActivity.EDIT_BOOK, Gson().toJson(book))
                startActivityForResult(intent, 1980)
            }
            R.id.action_remove -> {
                viewModel.removeBookFromMyLibrary(book)
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 1980 && resultCode == Activity.RESULT_OK){
            book = Gson().fromJson(data?.getStringExtra(EXTRA_BOOK), Book::class.java)
            fillBook(book)
            viewModel.updateBookFromMyLibrary(book)
        }
    }

    private fun showDialogInfo(listener: LocalInfoDialog.OnLocalInfoEdit){
        val dialog = if(book.localInfo == null) LocalInfoDialog.newInstance()
                     else LocalInfoDialog.newInstance(book.localInfo!!)
        dialog.listener = listener
        dialog.show(supportFragmentManager, "LoadlInfoDialog")
    }

}