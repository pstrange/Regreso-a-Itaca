package com.regresoa.itaca.view.settings

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*
import android.widget.CheckBox
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

        radio_my_books.setOnCheckedChangeListener { buttonView, isChecked ->
            AppPreferences.sourceMyBooks = isChecked
        }

        radio_title_az.setOnCheckedChangeListener { buttonView, isChecked ->
            AppPreferences.sortByTitleAZ = isChecked
        }

        radio_title_za.setOnCheckedChangeListener { buttonView, isChecked ->
            AppPreferences.sortByTitleZA = isChecked
        }

        radio_news.setOnCheckedChangeListener { buttonView, isChecked ->
            AppPreferences.sortByNew = isChecked
        }

        radio_older.setOnCheckedChangeListener { buttonView, isChecked ->
            AppPreferences.sortByOld = isChecked
        }

        check_isbn.setOnCheckedChangeListener { buttonView, isChecked ->
            if(!isChecked)
                check_isbn.isChecked = true
        }

        check_title.setOnCheckedChangeListener { buttonView, isChecked ->
            AppPreferences.byTitle = isChecked
            validatesEmptySelection(check_title)
        }

        check_author.setOnCheckedChangeListener { buttonView, isChecked ->
            AppPreferences.byAuthor = isChecked
            validatesEmptySelection(check_author)
        }

        check_publisher.setOnCheckedChangeListener { buttonView, isChecked ->
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
}