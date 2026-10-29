package com.adspro.app.receiver

import android.content.*
import com.adspro.app.AdsProApplication

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action == Intent.ACTION_BOOT_COMPLETED ||
            action == Intent.ACTION_MY_PACKAGE_REPLACED ||
            action == "com.adspro.app.ACTION_RESTART_SERVICE"
        ) {
            val app = context.applicationContext as? AdsProApplication
            app?.startCoreServices()
        }
    }
}
