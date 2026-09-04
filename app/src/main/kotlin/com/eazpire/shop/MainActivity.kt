package com.eazpire.shop

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.eazpire.shared.EazpireApps
import com.eazpire.shared.switcher.AppSwitchHelper
import com.eazpire.shop.auth.ShopSessionStore

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val ownerView = findViewById<TextView>(R.id.shop_owner)
        val ownerId = ShopSessionStore.get(this).getOwnerId()
        if (!ownerId.isNullOrBlank()) {
            ownerView.text = "Signed in as $ownerId"
            ownerView.visibility = android.view.View.VISIBLE
        }

        val openCreator = {
            AppSwitchHelper.openSiblingOrStore(this, EazpireApps.Target.CREATOR)
        }
        findViewById<Button>(R.id.btn_creator_switch).setOnClickListener { openCreator() }
        findViewById<Button>(R.id.btn_open_creator).setOnClickListener { openCreator() }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }
}
