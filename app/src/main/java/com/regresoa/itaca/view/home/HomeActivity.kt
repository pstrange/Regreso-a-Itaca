package com.regresoa.itaca.view.home

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.regresoa.itaca.R
import com.regresoa.itaca.view.mylibrary.MyLibraryFragment
import com.regresoa.itaca.view.settings.SettingsFragment
import kotlinx.android.synthetic.main.activity_main.*

class HomeActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = ""

        navigation_view.setOnNavigationItemSelectedListener(this)
        onNavigationItemSelected(navigation_view.menu.findItem(R.id.itm_mylibrary))
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.itm_mylibrary -> {
                supportFragmentManager.beginTransaction()
                        .replace(R.id.content_frame, MyLibraryFragment.newInstance())
                        .commit()
            }
            R.id.itm_settings -> {
                supportFragmentManager.beginTransaction()
                        .replace(R.id.content_frame, SettingsFragment.newInstance())
                        .commit()
            }
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }
}
