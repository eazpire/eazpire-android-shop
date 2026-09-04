package com.eazpire.shop.auth

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.browser.customtabs.CustomTabsIntent
import com.eazpire.shop.R

/**
 * Fallback sign-in when dual-app exchange handoff fails (IDEA-093 Phase 4).
 * Opens the store account login in a Custom Tab until Shop OAuth is wired.
 */
class ShopLoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Toast.makeText(this, R.string.shop_login_fallback, Toast.LENGTH_SHORT).show()
        try {
            CustomTabsIntent.Builder().setShowTitle(true).build()
                .launchUrl(this, Uri.parse(LOGIN_URL))
        } catch (_: Exception) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(LOGIN_URL)))
        }
        finish()
    }

    companion object {
        const val LOGIN_URL = "https://www.eazpire.com/account/login"
    }
}
