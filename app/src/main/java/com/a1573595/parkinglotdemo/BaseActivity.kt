package com.a1573595.parkinglotdemo

import android.content.Context
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {
    override fun attachBaseContext(base: Context) {
        val configuration = base.resources.configuration

        if (configuration.fontScale != 1.0f) {
            configuration.fontScale = 1.0f
            super.attachBaseContext(base.createConfigurationContext(configuration))
            return
        }

        super.attachBaseContext(base)
    }

    override fun getResources(): Resources {
        val resources = super.getResources()

        if (resources?.configuration?.fontScale != 1.0f) {
            val configuration = resources.configuration
            configuration.fontScale = 1.0f

            val context = createConfigurationContext(configuration)
            return context.resources
        }

        return resources
    }
}