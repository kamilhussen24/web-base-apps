package com.yourcompany.appname

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.yourcompany.appname.databinding.ActivityNoNetworkBinding

class NoNetworkActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNoNetworkBinding

    private val networkReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (isNetworkAvailable()) {
                startActivity(Intent(this@NoNetworkActivity, MainActivity::class.java))
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoNetworkBinding.inflate(layoutInflater)
        setContentView(binding.root)
        registerReceiver(networkReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    override fun onBackPressed() { finishAffinity() }

    override fun onDestroy() {
        super.onDestroy()
        try { unregisterReceiver(networkReceiver) } catch (e: Exception) {}
    }

    private fun isNetworkAvailable(): Boolean {
        val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val net = cm.activeNetwork ?: return false
        return cm.getNetworkCapabilities(net)
            ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }
}
