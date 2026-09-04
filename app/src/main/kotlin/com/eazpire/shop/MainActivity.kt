package com.eazpire.shop

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.eazpire.shared.EazpireApps
import com.eazpire.shared.switcher.AppSwitchHelper
import com.eazpire.shared.switcher.AppSwitchSession
import com.eazpire.shared.switcher.SiblingAppPromo
import com.eazpire.shop.auth.ShopLoginActivity
import com.eazpire.shop.auth.ShopSessionStore
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val session = ShopSessionStore.get(this)
        val ownerView = findViewById<TextView>(R.id.shop_owner)
        val ownerId = session.getOwnerId()
        if (!ownerId.isNullOrBlank()) {
            ownerView.text = getString(R.string.shop_signed_in_as, ownerId)
            ownerView.visibility = View.VISIBLE
        }

        val promo = findViewById<LinearLayout>(R.id.creator_promo_banner)
        val promoTitle = findViewById<TextView>(R.id.creator_promo_title)
        val promoBody = findViewById<TextView>(R.id.creator_promo_body)
        val promoCta = findViewById<Button>(R.id.creator_promo_cta)
        val promoDismiss = findViewById<Button>(R.id.creator_promo_dismiss)
        val prefs = getSharedPreferences("shop_ui", MODE_PRIVATE)
        val promoDismissed = prefs.getBoolean("creator_promo_dismissed", false)
        val creatorInstalled = AppSwitchHelper.isInstalled(this, EazpireApps.Target.CREATOR)
        if (!promoDismissed && !creatorInstalled) {
            promo.visibility = View.VISIBLE
            promoTitle.text = SiblingAppPromo.title(EazpireApps.Target.CREATOR)
            promoBody.text = SiblingAppPromo.body(EazpireApps.Target.CREATOR)
            promoCta.text = SiblingAppPromo.cta(EazpireApps.Target.CREATOR)
            promoCta.setOnClickListener {
                AppSwitchHelper.openSiblingOrStore(this, EazpireApps.Target.CREATOR)
            }
            promoDismiss.setOnClickListener {
                prefs.edit().putBoolean("creator_promo_dismissed", true).apply()
                promo.visibility = View.GONE
            }
        }

        val openCreator = {
            lifecycleScope.launch {
                AppSwitchSession.openSiblingWithOptionalExchange(
                    context = this@MainActivity,
                    target = EazpireApps.Target.CREATOR,
                    session = AppSwitchSession.SessionSnapshot(
                        jwt = session.getJwt(),
                        sourcePackage = EazpireApps.SHOP,
                        shopifyAccessToken = session.getShopifyAccess(),
                        shopifyRefreshToken = session.getShopifyRefresh(),
                        shopifyExpiresAt = session.getShopifyExpiresAt().takeIf { it > 0L },
                    ),
                )
            }
        }
        findViewById<Button>(R.id.btn_creator_switch).setOnClickListener { openCreator() }
        findViewById<Button>(R.id.btn_open_creator).setOnClickListener { openCreator() }
        findViewById<Button>(R.id.btn_sign_in).setOnClickListener {
            startActivity(Intent(this, ShopLoginActivity::class.java))
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }
}
