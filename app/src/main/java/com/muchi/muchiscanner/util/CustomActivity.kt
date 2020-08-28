/*
 * Copyright (c) 2020 - Irfanul Haq
 */

package com.muchi.muchiscanner.util

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.muchi.muchiscanner.BuildConfig
import java.io.File

open class CustomActivity : AppCompatActivity(){

    companion object {
        private const val TAG = "CustomActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
    }

    fun showToast(string: String){
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show()
    }

    fun showLog(string: String){
        Log.d("MUCHI", string)
    }

    fun openPDF(dir: String?, filename: String?){
        intent = Intent(Intent.ACTION_VIEW)
        val myPDF = File("$dir/$filename")
        val uri = FileProvider.getUriForFile(this, "${BuildConfig.APPLICATION_ID}.provider", myPDF)
        intent.setDataAndType(uri, "application/pdf")
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        startActivity(Intent.createChooser(intent, "Open with..."))
    }
}