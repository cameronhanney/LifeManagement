package com.example.lifemanagement


import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import android.os.Bundle
import android.graphics.BitmapFactory
import android.view.View
import android.widget.ImageView

class DisplayActivity : AppCompatActivity() {
    var mTvName: TextView? = null
    var mTvLastName: TextView? = null
    var mIvThumbnail: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display)

        //Get the text views
        mTvName = findViewById<View>(R.id.tv_n_data) as TextView

        //Get the starter intent
        val receivedIntent = intent

        //Set the text views
        mTvName!!.text = receivedIntent.getStringExtra("N_DATA")

    }
}