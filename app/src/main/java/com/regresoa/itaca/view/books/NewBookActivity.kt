package com.regresoa.itaca.view.books

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.regresoa.itaca.R
import com.regresoa.itaca.model.repositories.BooksRepository
import com.regresoa.itaca.viewmodel.BooksViewModel
import kotlinx.android.synthetic.main.activity_new_book.*
import java.security.MessageDigest
import java.util.*
import android.content.DialogInterface
import android.support.v7.app.AlertDialog
import android.text.InputType
import android.widget.EditText
import com.bumptech.glide.Glide
import com.regresoa.itaca.model.entities.*


/**
 * Created by just_ on 10/02/2019.
 */
class NewBookActivity : AppCompatActivity() {

    private lateinit var book: Book
    private val viewModel: BooksViewModel = BooksViewModel(BooksRepository())
    private var imageUrl = ""

    companion object {
        val RESULT_ADDED = 11
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_book)

        setSupportActionBar(toolbar)
        supportActionBar!!.title = ""
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        val hashKey = hash()
        book = Book(hashKey, hashKey)
        image_cover.setOnClickListener {
            showInputDialog()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_book_remote, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId){
            android.R.id.home -> {
                finish()
            }
            R.id.action_addnew -> {
                if(hasValidInfo()){
                    showDialogInfo(object : LocalInfoDialog.OnLocalInfoEdit {
                        override fun onLocalInfoSave(localInfo: LocalInfo) {
                            book.localInfo = localInfo
                            viewModel.addBookToMyLibrary(book)
                            setResult(RESULT_ADDED)
                            finish()
                        }
                    })
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun hasValidInfo(): Boolean{
        val volumeInfo = VolumeInfo()

        volumeInfo.imageLinks = ImageLinks(imageUrl, imageUrl)

        if(edit_title.text.toString().isNotEmpty()) {
            volumeInfo.title = edit_title.text.toString()
        } else {
            inputlayout_title.error = "Campo requerido"
            return false
        }

        if(edit_authors.text.toString().isNotEmpty()) {
            volumeInfo.authors = edit_authors.text.toString().split(",")
        } else {
            inputlayout_authors.error = "Campo requerido"
            return false
        }

        if(edit_publisher.text.toString().isNotEmpty()) {
            volumeInfo.publisher = edit_publisher.text.toString()
        }

        if(edit_publishing_date.text.toString().isNotEmpty()) {
            volumeInfo.publishedDate = edit_publishing_date.text.toString()
        }

        if(edit_categories.text.toString().isNotEmpty()) {
            volumeInfo.categories = edit_categories.text.toString().split(",")
        }

        if(edit_description.text.toString().isNotEmpty()) {
            volumeInfo.description = edit_description.text.toString()
        }

        if(edit_identifiers.text.toString().isNotEmpty()) {
            val ids = mutableListOf<IndustryIdentifier>()
            val identifiers = edit_identifiers.text.toString().split(",")
            identifiers.forEach {
                ids.add(IndustryIdentifier("ISBN", it))
            }
            volumeInfo.industryIdentifiers = ids
        }

        if(edit_laguage.text.toString().isNotEmpty()) {
            volumeInfo.language = edit_laguage.text.toString()
        }

        if(edit_pages.text.toString().isNotEmpty()) {
            volumeInfo.pageCount = edit_pages.text.toString().toInt()
        }

        book.creationDate = Calendar.getInstance().timeInMillis
        book.volumeInfo = volumeInfo
        return true
    }

    private fun showInputDialog(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("URL de imagen")
        // Set up the input
        val input = EditText(this)
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.inputType = InputType.TYPE_TEXT_VARIATION_URI
        input.setText(imageUrl)
        builder.setView(input)
        // Set up the buttons
        builder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
            imageUrl = input.text.toString()
            Glide.with(this)
                    .load(imageUrl)
                    .into(image_cover)
        })
        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })

        builder.show()
    }

    private fun showDialogInfo(listener: LocalInfoDialog.OnLocalInfoEdit){
        val dialog = if(book.localInfo == null) LocalInfoDialog.newInstance()
                     else LocalInfoDialog.newInstance(book.localInfo!!)
        dialog.listener = listener
        dialog.show(supportFragmentManager, "LoadlInfoDialog")
    }

    private fun hash(): String {
        val bytes = Calendar.getInstance().timeInMillis.toString().toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("", { str, it -> str + "%02x".format(it) })
    }
}