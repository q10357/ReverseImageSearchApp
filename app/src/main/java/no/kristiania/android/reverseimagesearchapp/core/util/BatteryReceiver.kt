package no.kristiania.android.reverseimagesearchapp.core.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

class BatteryReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        //it will give a toast if the user ever gets below 5%
       val action  = intent.action
        Toast.makeText(context,action,Toast.LENGTH_LONG).show()

    }
}