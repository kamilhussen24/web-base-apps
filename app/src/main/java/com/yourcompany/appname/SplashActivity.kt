package com.yourcompany.appname

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.yourcompany.appname.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        startAnimation()
    }

    private fun startAnimation() {
        val scaleX = ObjectAnimator.ofFloat(binding.splashLogo, "scaleX", 0.6f, 1.0f).apply {
            duration = 700; interpolator = OvershootInterpolator(1.2f)
        }
        val scaleY = ObjectAnimator.ofFloat(binding.splashLogo, "scaleY", 0.6f, 1.0f).apply {
            duration = 700; interpolator = OvershootInterpolator(1.2f)
        }
        val fadeIn = ObjectAnimator.ofFloat(binding.splashLogo, "alpha", 0f, 1f).apply {
            duration = 700
        }
        val tagline = ObjectAnimator.ofFloat(binding.splashTagline, "alpha", 0f, 1f).apply {
            duration = 500; startDelay = 500; interpolator = DecelerateInterpolator()
        }

        AnimatorSet().apply { playTogether(scaleX, scaleY, fadeIn, tagline); start() }

        Handler(Looper.getMainLooper()).postDelayed({
            val next = if (isNetworkAvailable()) MainActivity::class.java
                       else NoNetworkActivity::class.java
            startActivity(Intent(this, next))
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }, 2500)
    }

    private fun isNetworkAvailable(): Boolean {
        val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val net = cm.activeNetwork ?: return false
        return cm.getNetworkCapabilities(net)
            ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }
}
