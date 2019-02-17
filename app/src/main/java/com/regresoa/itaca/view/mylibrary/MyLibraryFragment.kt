package com.regresoa.itaca.view.mylibrary

import android.Manifest
import android.app.Activity
import android.arch.lifecycle.Observer
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
        val REQUEST_CAM = 1985
        val REQUEST_BOOK = 1984

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
                .withTitle(getString(R.string.permission_title))
                .withMessage(getString(R.string.permission_message))
                .withButtonText(getString(R.string.permission_accept))
                .withIcon(R.drawable.ic_action_info)
                .build()!!
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
            startActivityForResult(intent, REQUEST_BOOK)
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
                startActivityForResult(intent, REQUEST_BOOK)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPermissionGranted(response: PermissionGrantedResponse?) {
        val intent = Intent(activity, ScannerActivity::class.java)
        startActivityForResult(intent, REQUEST_CAM)
    }

    override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest?, token: PermissionToken?) {
        dialogPermissionListener.onPermissionRationaleShouldBeShown(permission, token)
    }

    override fun onPermissionDenied(response: PermissionDeniedResponse?) {
        dialogPermissionListener.onPermissionDenied(response)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_CAM && resultCode == Activity.RESULT_OK){
            if(AppPreferences.sourceMyBooks){
                adapter.filter(data!!.getStringExtra(ScannerActivity.ISBN))
            }else{
                viewModel.removeMyBooksListener(listener)
                viewModel.searchISBN(data!!.getStringExtra(ScannerActivity.ISBN))
            }
        }else if(requestCode == REQUEST_BOOK && resultCode == BookActivity.RESULT_ADDED){
            adapter.clear()
            viewModel.getMyBooks(listener)
        }
    }

}