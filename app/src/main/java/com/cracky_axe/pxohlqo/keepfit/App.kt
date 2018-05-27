package com.cracky_axe.pxohlqo.keepfit

import android.app.Application
import com.cracky_axe.pxohlqo.keepfit.model.MyObjectBox
import io.objectbox.BoxStore
import io.objectbox.android.AndroidObjectBrowser
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class App : Application(), AnkoLogger {
    lateinit var boxStore: BoxStore

    override fun onCreate() {
        super.onCreate()

        boxStore = MyObjectBox.builder().androidContext(this).build()

        if (BuildConfig.DEBUG) {
            val started = AndroidObjectBrowser(boxStore).start(this)
            info { "ObjectBrowser Started: $started" }
        }
    }
}