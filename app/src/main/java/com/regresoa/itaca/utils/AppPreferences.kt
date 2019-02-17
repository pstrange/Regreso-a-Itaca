package com.regresoa.itaca.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson

/**
 * Created by just_ on 16/02/2019.
 */
object AppPreferences {

    private const val NAME = "ItacaAppPreferences"
    private lateinit var preferences: SharedPreferences
    private lateinit var jsonConverter: Gson

    /**
     * Initializes the shared preferences.
     *
     * @param context the application context
     */

    fun init(context: Context) {
        jsonConverter = Gson()
        preferences = context.getSharedPreferences(NAME, Context.MODE_PRIVATE)
    }

    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }

    var sourceMyBooks: Boolean
        get() = preferences.getBoolean(SharedKeys.SOURCE_KEY, true)
        set(value) = preferences.edit {
            it.putBoolean(SharedKeys.SOURCE_KEY, value)
        }

    var byTitle: Boolean
        get() = preferences.getBoolean(SharedKeys.TITLE_KEY, true)
        set(value) = preferences.edit {
            it.putBoolean(SharedKeys.TITLE_KEY, value)
        }

    var byAuthor: Boolean
        get() = preferences.getBoolean(SharedKeys.AUTHOR_KEY, true)
        set(value) = preferences.edit {
            it.putBoolean(SharedKeys.AUTHOR_KEY, value)
        }

    var byPublisher: Boolean
        get() = preferences.getBoolean(SharedKeys.PUBLISHER_KEY, true
        )
        set(value) = preferences.edit {
            it.putBoolean(SharedKeys.PUBLISHER_KEY, value)
        }

    var byISBN: Boolean
        get() = preferences.getBoolean(SharedKeys.ISBN_KEY, true)
        set(value) = preferences.edit {
            it.putBoolean(SharedKeys.ISBN_KEY, value)
        }

    var sortByTitleAZ: Boolean
        get() = preferences.getBoolean(SharedKeys.BY_TITLE_KEY, false)
        set(value) = preferences.edit {
            it.putBoolean(SharedKeys.BY_TITLE_KEY, value)
        }

    var sortByTitleZA: Boolean
        get() = preferences.getBoolean(SharedKeys.BY_TITLE_ZA_KEY, false)
        set(value) = preferences.edit {
            it.putBoolean(SharedKeys.BY_TITLE_ZA_KEY, value)
        }

    var sortByNew: Boolean
        get() = preferences.getBoolean(SharedKeys.BY_NEW_KEY, true)
        set(value) = preferences.edit {
            it.putBoolean(SharedKeys.BY_NEW_KEY, value)
        }

    var sortByOld: Boolean
        get() = preferences.getBoolean(SharedKeys.BY_OLD_KEY, false)
        set(value) = preferences.edit {
            it.putBoolean(SharedKeys.BY_OLD_KEY, value)
        }
}