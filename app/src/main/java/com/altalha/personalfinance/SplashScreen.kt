package com.altalha.personalfinance

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.google.firebase.database.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class SplashScreen : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val uid = getUIDFromSharedPreference()

        Executors.newSingleThreadScheduledExecutor().schedule({
            if (uid!!.isNotEmpty()) {
                gotoMain(uid)
            }
            else
                gotoFLA()
        }, 2, TimeUnit.SECONDS)

    }

    private fun getUIDFromSharedPreference() : String? {
        val sp = getSharedPreferences("SAVE_USER", Context.MODE_PRIVATE)
        return sp.getString("userUID", "")
    }

    private fun gotoMain(uid : String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("savedUID", uid)
        startActivity(intent)
        finish()
    }

    private fun gotoFLA() {
        startActivity(Intent(this, FirstLoginActivity::class.java))
        finish()
    }
}