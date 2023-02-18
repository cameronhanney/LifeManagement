package com.example.lifemanagement

import android.content.ActivityNotFoundException
import androidx.appcompat.app.AppCompatActivity
import android.graphics.Bitmap
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.os.Environment
import android.os.PersistableBundle
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.drawToBitmap
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), View.OnClickListener {
    //Create variables to hold the three strings
    private var mFirstName: String? = null
    private var mMiddleName: String? = null
    private var mLastName: String? = null
    private var mFullName: String? = null
    private var mFilepath: String? = null

    //Create variables for the UI elements that we need to control
    private var mButtonSubmit: Button? = null
    private var mButtonCamera: ImageButton? = null
    private var mEtFirstName: EditText? = null
    private var mEtMiddleName: EditText? = null
    private var mEtLastName: EditText? = null

    //Define a bitmap
    private var mThumbnailImage: Bitmap? = null

    //Define tbe global intent variables
    private var mDisplayIntent: Intent? = null
    private var mRestoreIntent: Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Get the buttons
        mButtonSubmit = findViewById<View>(R.id.button_submit) as Button
        mButtonCamera = findViewById<View>(R.id.profile_pic) as ImageButton

        //Say that this class itself contains the listener.
        mButtonSubmit!!.setOnClickListener(this)
        mButtonCamera!!.setOnClickListener(this)

        //Create the intent but don't start the activity yet
        mDisplayIntent = Intent(this, DisplayActivity::class.java)

    }

    //Handle clicks for ALL buttons here
    override fun onClick(view: View) {
        when (view.id) {
            R.id.button_submit -> {

                //First, get the string from the EditText
                mEtFirstName = findViewById<View>(R.id.first_name) as EditText
                mEtMiddleName = findViewById<View>(R.id.middle_name) as EditText
                mEtLastName = findViewById<View>(R.id.last_name) as EditText

                mFirstName = mEtFirstName!!.text.toString()
                mLastName = mEtFirstName!!.text.toString()
                mFullName = mEtFirstName!!.text.toString() + " " + mEtLastName!!.text.toString()

                //Check if the EditText string is empty
                if (mFirstName.isNullOrBlank() || mLastName.isNullOrBlank()) {
                    //Complain that there's no text
                    Toast.makeText(this@MainActivity, "Please enter both first and last name", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    //Reward them for submitting their names
                    Toast.makeText(this@MainActivity, "You slayed it!", Toast.LENGTH_SHORT).show()

                    //Remove any leading spaces or tabs
                    mFullName = mFullName!!.replace("^\\s+".toRegex(), "")
                    val mNameMessage = "$mFullName is logged in!"

                    //Start a new activity and pass the strings to them
                    mDisplayIntent!!.putExtra("N_DATA", mNameMessage)
                    startActivity(mDisplayIntent) //explicit intent

                }
            }
            R.id.profile_pic -> {

                //The button press should open a camera
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                try {
                    cameraLauncher.launch(cameraIntent)

                } catch (ex: ActivityNotFoundException) {
                    //Do something here
                }
            }
        }
    }

    private var cameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val extras = result.data!!.extras
                mThumbnailImage = extras!!["data"] as Bitmap?

                //Open a file and write to it
                if (isExternalStorageWritable) {
                    val filePathString = saveImage(mThumbnailImage)
                    mFilepath = filePathString
                    mDisplayIntent!!.putExtra("imagePath", filePathString)
                } else {
                    Toast.makeText(this, "External storage not writable.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        savedInstanceState.putString("PROFILE_PIC", mFilepath)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        mFilepath = savedInstanceState!!.getString("PROFILE_PIC")
        val buttonImage = BitmapFactory.decodeFile(mFilepath)
        mButtonCamera!!.setImageBitmap(buttonImage)
    }

    private fun saveImage(finalBitmap: Bitmap?): String {
        val root = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val myDir = File("$root/saved_images")
        myDir.mkdirs()
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val fname = "Thumbnail_$timeStamp.jpg"
        val file = File(myDir, fname)
        if (file.exists()) file.delete()
        mButtonCamera = findViewById<ImageButton>(R.id.profile_pic) as ImageButton
        try {
            val out = FileOutputStream(file)
            finalBitmap!!.compress(Bitmap.CompressFormat.JPEG, 90, out)
            mButtonCamera!!.setImageBitmap(finalBitmap)
            out.flush()
            out.close()
            Toast.makeText(this, "file saved!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return file.absolutePath
    }

    private val isExternalStorageWritable: Boolean
        get() {
            val state = Environment.getExternalStorageState()
            return Environment.MEDIA_MOUNTED == state
        }
}