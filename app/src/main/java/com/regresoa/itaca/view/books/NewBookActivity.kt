package com.regresoa.itaca.view.books

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.DialogOnDeniedPermissionListener
import com.karumi.dexter.listener.single.PermissionListener
import com.mandala.lib.utils.RealPathUtil
import com.regresoa.itaca.R
import com.regresoa.itaca.model.entities.*
import com.regresoa.itaca.model.repositories.BooksRepository
import com.regresoa.itaca.viewmodel.BooksViewModel
import kotlinx.android.synthetic.main.activity_new_book.*
import java.security.MessageDigest
import java.util.*


/**
 * Created by just_ on 10/02/2019.
 */
class NewBookActivity : AppCompatActivity(), PermissionListener{

    private lateinit var book: Book
    private var imageLinks: ImageLinks? = null
    private val viewModel: BooksViewModel = BooksViewModel(BooksRepository())
    private lateinit var dialogPermissionListener: PermissionListener

    companion object {
        val EDIT_BOOK = "EDIT_BOOK"
        val UPDATE_BOOK = "UPDATE_BOOK"
        val RESULT_ADDED = 11
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_book)

        setSupportActionBar(toolbar)
        supportActionBar!!.title = ""
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)

        if(intent.hasExtra(EDIT_BOOK)) {
            book = Gson().fromJson(intent.getStringExtra(EDIT_BOOK), Book::class.java)
            imageLinks = book.volumeInfo?.imageLinks
            fillBook(book)
        }else{
            val hashKey = hash()
            book = Book(hashKey, hashKey)
        }

        dialogPermissionListener = DialogOnDeniedPermissionListener.Builder
                .withContext(this)
                .withTitle(getString(R.string.permission_read_title))
                .withMessage(getString(R.string.permission_read_message))
                .withButtonText(getString(R.string.permission_read_accept))
                .withIcon(R.drawable.ic_action_info)
                .build()!!

        image_cover.setOnClickListener {
            Dexter.withActivity(this)
                  .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                  .withListener(this)
                  .check()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(
                if(intent.hasExtra(EDIT_BOOK)) R.menu.menu_book_edit
                else R.menu.menu_book_remote, menu)
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
                            book.creationDate = Calendar.getInstance().timeInMillis
                            book.localInfo = localInfo
                            viewModel.addBookToMyLibrary(book)
                            setResult(RESULT_ADDED)
                            finish()
                        }
                    })
                }
            }
            R.id.action_save -> {
                if(hasValidInfo()){
                    val resultIntent = Intent()
                    resultIntent.putExtra(UPDATE_BOOK, Gson().toJson(book))
                    setResult(Activity.RESULT_OK, resultIntent)
                    finish()
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun fillBook(book: Book?){
        book?.let {
            Glide.with(this)
                    .load(it.volumeInfo?.imageLinks?.thumbnail)
                    .into(image_cover)

            edit_title.setText(it.volumeInfo?.title)
            edit_authors.setText(it.volumeInfo?.sAuthors)
            edit_publisher.setText(it.volumeInfo?.publisher)
            edit_publishing_date.setText(it.volumeInfo?.publishedDate)
            edit_categories.setText(it.volumeInfo?.sCategories)
            edit_description.setText(it.volumeInfo?.description)
            edit_identifiers.setText(it.volumeInfo?.sIdentifiers)
            edit_laguage.setText(it.volumeInfo?.language)
            edit_pages.setText(it.volumeInfo?.pageCount.toString())
        }
    }

    private fun hasValidInfo(): Boolean{
        val volumeInfo = VolumeInfo()

        volumeInfo.imageLinks = imageLinks

        if(edit_title.text.toString().isNotEmpty()) {
            volumeInfo.title = edit_title.text.toString()
        } else {
            inputlayout_title.error = getString(R.string.book_error_required_field)
            return false
        }

        if(edit_authors.text.toString().isNotEmpty()) {
            volumeInfo.authors = edit_authors.text.toString().split(",")
        } else {
            inputlayout_authors.error = getString(R.string.book_error_required_field)
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

        book.volumeInfo = volumeInfo
        return true
    }

    override fun onPermissionGranted(response: PermissionGrantedResponse?) {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1900)
    }

    override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest?, token: PermissionToken?) {
        dialogPermissionListener.onPermissionRationaleShouldBeShown(permission, token)
    }

    override fun onPermissionDenied(response: PermissionDeniedResponse?) {
        dialogPermissionListener.onPermissionDenied(response)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1900 && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                upload_loader.visibility = View.VISIBLE
                deleteAndUploadIfNeccessary(uri)
            }
        }
    }

    private fun deleteAndUploadIfNeccessary(data: Uri){
        imageLinks?.let { links ->
            if(links.thumbnail.contains("firebasestorage")) {
                viewModel.deleteFile(book.id).addOnCompleteListener {
                    uploadFileFromUri(data)
                }.addOnFailureListener {
                    imageLinks = ImageLinks("", "")
                    upload_loader.visibility = View.GONE
                    Toast.makeText(this, R.string.error_upload, Toast.LENGTH_SHORT).show()
                }
            }
            else uploadFileFromUri(data)
        }?: run{ uploadFileFromUri(data) }
    }

    private fun uploadFileFromUri(data: Uri){
        val path = RealPathUtil.getRealPathFromURI(this, data)
        viewModel.uploadFile(book.id, path!!).addOnCompleteListener { task ->
            upload_loader.visibility = View.GONE
            if (task.isSuccessful) {
                val downloadUri = task.result
                imageLinks = ImageLinks(downloadUri.toString(), downloadUri.toString())
                Glide.with(this)
                        .load(downloadUri)
                        .into(image_cover)
            } else {
                Toast.makeText(this, R.string.error_upload, Toast.LENGTH_SHORT).show()
            }
        }
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