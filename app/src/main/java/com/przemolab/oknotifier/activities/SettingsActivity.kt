package com.przemolab.oknotifier.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.ActionBar

import com.przemolab.oknotifier.R

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setupActionBar()
    }

    private fun setupActionBar() {
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }
}
