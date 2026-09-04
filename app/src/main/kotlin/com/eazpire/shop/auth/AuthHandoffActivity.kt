package com.eazpire.shop.auth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.eazpire.shared.EazpireApps
import com.eazpire.shared.auth.AppExchangeClient
import com.eazpire.shop.MainActivity
import com.eazpire.shop.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Receives `eazpire-shop://auth/handoff?exchange_token=…` from Creator (or web tests).
 * On failure, opens [ShopLoginActivity] as OAuth fallback.
 */
class AuthHandoffActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val token = intent?.data?.getQueryParameter("exchange_token")?.trim().orEmpty()
        if (token.isEmpty()) {
            finishWithFallback()
            return
        }
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val result = AppExchangeClient().completeExchange(
                    exchangeToken = token,
                    targetPackage = EazpireApps.SHOP,
                )
                ShopSessionStore.get(this@AuthHandoffActivity).saveSession(
                    jwt = result.jwt,
                    ownerId = result.ownerId,
                    shopifyAccess = result.shopifyAccessToken,
                    shopifyRefresh = result.shopifyRefreshToken,
                    shopifyExpiresAt = result.shopifyExpiresAt,
                )
                finishOk()
            } catch (_: Exception) {
                finishWithFallback()
            }
        }
    }

    private fun finishOk() {
        Toast.makeText(this, R.string.shop_handoff_ok, Toast.LENGTH_SHORT).show()
        startActivity(
            Intent(this, MainActivity::class.java).addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP,
            ),
        )
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun finishWithFallback() {
        Toast.makeText(this, R.string.shop_handoff_fail, Toast.LENGTH_SHORT).show()
        startActivity(
            Intent(this, MainActivity::class.java).addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP,
            ),
        )
        startActivity(Intent(this, ShopLoginActivity::class.java))
        setResult(Activity.RESULT_CANCELED)
        finish()
    }
}
