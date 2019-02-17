package com.regresoa.itaca.view.mylibrary

import android.Manifest
import android.app.Activity
import android.app.SearchManager
import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.view.*
import com.google.gson.Gson
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.DialogOnDeniedPermissionListener
import com.karumi.dexter.listener.single.PermissionListener
import com.regresoa.itaca.R
import com.regresoa.itaca.model.entities.Book
import com.regresoa.itaca.model.repositories.BooksRepository
import com.regresoa.itaca.utils.AppPreferences
import com.regresoa.itaca.view.books.BookActivity
import com.regresoa.itaca.view.books.NewBookActivity
import com.regresoa.itaca.viewmodel.BooksViewModel
import kotlinx.android.synthetic.main.fragment_mylibrary.*
import java.util.*


/**
 * Created by just_ on 09/02/2019.
 */
class MyLibraryFragment : Fragment(), PermissionListener, SearchView.OnQueryTextListener {

    companion object {
        fun newInstance(): MyLibraryFragment {
            return MyLibraryFragment()
        }
    }

    private val viewModel: BooksViewModel = BooksViewModel(BooksRepository())
    private lateinit var adapter: AdapterBooks
    private lateinit var dialogPermissionListener: PermissionListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        dialogPermissionListener = DialogOnDeniedPermissionListener.Builder
                .withContext(activity)
                .withTitle("Permiso de camara")
                .withMessage("El permiso de camara es necesario para utilizar el escaner de ISBN")
                .withButtonText("Aceptar")
                .withIcon(R.drawable.ic_action_info)
                .build()
        return inflater.inflate(R.layout.fragment_mylibrary, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recycler_view.layoutManager = LinearLayoutManager(activity)
        adapter = AdapterBooks(ArrayList(), context!!, eventClick)
        recycler_view.adapter = adapter

        viewModel.getMyBooks(listener)

        viewModel.isLoading.observe(this, Observer {
            progress_lib.visibility = if(it!!) View.VISIBLE else View.GONE
        })
        viewModel.searchResults.observe(this, Observer {
            adapter.clear()
            adapter.addAll(it!!.items)
        })
    }

    private val eventClick = object : AdapterBooksViewHolder.OnItemClick(){
        override fun onClickItem(book: Book?) {
            val intent = Intent(activity, BookActivity::class.java)
            intent.putExtra(BookActivity.EXTRA_BOOK, Gson().toJson(book))
            startActivityForResult(intent, 1984)
        }
    }

    private val listener = object : BooksViewModel.ChildEvents(){
        override fun onChildLoaded(book: Book) {
            viewModel.isLoading.postValue(false)
            adapter.add(book)
        }

        override fun onChildDeleted(book: Book) {
            adapter.remove(book)
        }

        override fun onChildUpdate(book: Book) {
            adapter.update(book)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(
                if(AppPreferences.sourceMyBooks) R.menu.menu_mylibrary_searchable
                else R.menu.menu_mylibrary, menu)

        val menuItem = menu!!.findItem(R.id.action_search)
        menuItem?.let {
            val searchView = it.actionView as SearchView
            searchView.maxWidth = Integer.MAX_VALUE
            searchView.setOnQueryTextListener(this)
        }

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        adapter.filter(query!!)
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {return false}

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId){
            R.id.action_search -> {
                return true
            }
            R.id.action_scan -> {
                Dexter.withActivity(activity)
                        .withPermission(Manifest.permission.CAMERA)
                        .withListener(this)
                        .check()

            }
            R.id.action_add -> {
                val intent = Intent(activity, NewBookActivity::class.java)
                startActivityForResult(intent, 1984)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPermissionGranted(response: PermissionGrantedResponse?) {
        val intent = Intent(activity, ScannerActivity::class.java)
        startActivityForResult(intent, 1985)
    }

    override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest?, token: PermissionToken?) {
        dialogPermissionListener.onPermissionRationaleShouldBeShown(permission, token)
    }

    override fun onPermissionDenied(response: PermissionDeniedResponse?) {
        dialogPermissionListener.onPermissionDenied(response)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 1985 && resultCode == Activity.RESULT_OK){
            if(AppPreferences.sourceMyBooks){
                adapter.filter(data!!.getStringExtra("ISBN"))
            }else{
                viewModel.removeMyBooksListener(listener)
                viewModel.searchISBN(data!!.getStringExtra("ISBN"))
            }
        }else if(requestCode == 1984 && resultCode == BookActivity.RESULT_ADDED){
            adapter.clear()
            viewModel.getMyBooks(listener)
        }
    }

}