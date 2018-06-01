package io.objectbox.example.kotlin

import android.app.Application
import android.os.Environment
import com.synd.kotlin.db.entity.MyObjectBox

import java.io.File

import io.objectbox.BoxStore

class App : Application() {

    companion object Constants {
        const val TAG = "ObjectBoxDemo"
        const val EXTERNAL_DIR = false
    }

    lateinit var boxStore: BoxStore
        private set

    override fun onCreate() {
        super.onCreate()

        if (EXTERNAL_DIR) {
            // Example how you could use a custom dir in "external storage"
            // (Android 6+ note: give the app storage permission in app info settings)
            val directory = File(Environment.getExternalStorageDirectory(), "objectbox-notes");
            boxStore = MyObjectBox.builder().androidContext(this).directory(directory).build();
        } else {
            // This is the minimal setup required on Android
            boxStore = MyObjectBox.builder().androidContext(this).build()
        }
    }

}
