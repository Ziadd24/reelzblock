package com.zoksh.reelblock

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        render()
    }

    override fun onResume() {
        super.onResume()
        render()
    }

    private fun render() {
        val enabled = ReelBlockAccessibilityService.isEnabled(this)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 64, 48, 48)
        }

        val title = TextView(this).apply {
            text = "ReelBlock"
            textSize = 30f
        }

        val status = TextView(this).apply {
            text = if (enabled)
                "● Blocker is ON"
            else
                "○ Blocker is OFF"
            textSize = 20f
            setPadding(0, 24, 0, 32)
        }

        val explanation = TextView(this).apply {
            text = "MVP: when Instagram opens a Reels surface or you tap the Reels navigation, ReelBlock sends you back."
            textSize = 16f
            setPadding(0, 0, 0, 32)
        }

        val button = Button(this).apply {
            text = if (enabled) "Open accessibility settings" else "Enable ReelBlock"
            setOnClickListener {
                startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            }
        }

        layout.addView(title)
        layout.addView(status)
        layout.addView(explanation)
        layout.addView(button, ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ))

        setContentView(layout)
    }
}
