package com.regresoa.itaca.view.settings

import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.view.*
import android.widget.CheckBox
import com.regresoa.itaca.BuildConfig
import com.regresoa.itaca.R
import com.regresoa.itaca.utils.AppPreferences
import kotlinx.android.synthetic.main.fragment_settings.*

/**
 * Created by just_ on 09/02/2019.
 */
class SettingsFragment : Fragment(){

    companion object {

        fun newInstance(): SettingsFragment {
            return SettingsFragment()
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        radio_my_books.isChecked = AppPreferences.sourceMyBooks
        radio_google.isChecked = !AppPreferences.sourceMyBooks

        radio_title_az.isChecked = AppPreferences.sortByTitleAZ
        radio_title_za.isChecked = AppPreferences.sortByTitleZA
        radio_news.isChecked = AppPreferences.sortByNew
        radio_older.isChecked = AppPreferences.sortByOld

        check_isbn.isChecked = AppPreferences.byISBN
        check_title.isChecked = AppPreferences.byTitle
        check_author.isChecked = AppPreferences.byAuthor
        check_publisher.isChecked = AppPreferences.byPublisher

        radio_my_books.setOnCheckedChangeListener { _, isChecked ->
            AppPreferences.sourceMyBooks = isChecked
        }

        radio_title_az.setOnCheckedChangeListener { _, isChecked ->
            AppPreferences.sortByTitleAZ = isChecked
        }

        radio_title_za.setOnCheckedChangeListener { _, isChecked ->
            AppPreferences.sortByTitleZA = isChecked
        }

        radio_news.setOnCheckedChangeListener { _, isChecked ->
            AppPreferences.sortByNew = isChecked
        }

        radio_older.setOnCheckedChangeListener { _, isChecked ->
            AppPreferences.sortByOld = isChecked
        }

        check_isbn.setOnCheckedChangeListener { _, isChecked ->
            if(!isChecked)
                check_isbn.isChecked = true
        }

        check_title.setOnCheckedChangeListener { _, isChecked ->
            AppPreferences.byTitle = isChecked
            validatesEmptySelection(check_title)
        }

        check_author.setOnCheckedChangeListener { _, isChecked ->
            AppPreferences.byAuthor = isChecked
            validatesEmptySelection(check_author)
        }

        check_publisher.setOnCheckedChangeListener { _, isChecked ->
            AppPreferences.byPublisher = isChecked
            validatesEmptySelection(check_publisher)
        }
    }

    private fun validatesEmptySelection(check: CheckBox){
        if (!AppPreferences.byISBN && !AppPreferences.byTitle && !AppPreferences.byAuthor && !AppPreferences.byPublisher)
            check.isChecked = true
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.menu_settings, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item?.itemId == R.id.action_appinfo){
            val builder = AlertDialog.Builder(context!!)
            builder.setTitle(R.string.app_name)
            builder.setMessage(
                    "Version: "+BuildConfig.VERSION_NAME+"-"+BuildConfig.VERSION_CODE+"\n"+
                    "Type: "+BuildConfig.BUILD_TYPE+"\n"+
                    "Database: "+BuildConfig.FIREBASE_DBNAME+"\n"+
                    "Files: "+BuildConfig.FIREBASE_MEDIA)
            builder.setPositiveButton(android.R.string.ok) { dialog, which ->
                dialog.dismiss()
            }
            builder.create().show()
        }
        return super.onOptionsItemSelected(item)
    }
}