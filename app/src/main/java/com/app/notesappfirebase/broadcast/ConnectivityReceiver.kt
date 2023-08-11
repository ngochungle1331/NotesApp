package com.app.notesappfirebase.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import com.app.notesappfirebase.authentication.LoginActivity

class ConnectivityReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val connectivityManager =
            context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        val isConnected = networkInfo != null && networkInfo.isConnected

        // Gọi hàm xử lý kết nối internet trong LoginActivity
        (context as LoginActivity).handleInternetConnection(isConnected)
    }
}
