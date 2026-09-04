package com.eazpire.shop.auth

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

/**
 * Minimal session store for the Shop shell until full SecureTokenStore is shared.
 */
class ShopSessionStore private constructor(context: Context) {
    private val prefs = EncryptedSharedPreferences.create(
        context,
        "eazpire_shop_session",
        MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
    )

    fun getJwt(): String? = prefs.getString(KEY_JWT, null)?.takeIf { it.isNotBlank() }
    fun getOwnerId(): String? = prefs.getString(KEY_OWNER, null)?.takeIf { it.isNotBlank() }
    fun getShopifyAccess(): String? = prefs.getString(KEY_SHOPIFY_ACCESS, null)?.takeIf { it.isNotBlank() }
    fun getShopifyRefresh(): String? = prefs.getString(KEY_SHOPIFY_REFRESH, null)?.takeIf { it.isNotBlank() }
    fun getShopifyExpiresAt(): Long = prefs.getLong(KEY_SHOPIFY_EXPIRES, 0L)

    fun saveSession(
        jwt: String,
        ownerId: String,
        shopifyAccess: String? = null,
        shopifyRefresh: String? = null,
        shopifyExpiresAt: Long? = null,
    ) {
        prefs.edit()
            .putString(KEY_JWT, jwt)
            .putString(KEY_OWNER, ownerId)
            .putString(KEY_SHOPIFY_ACCESS, shopifyAccess)
            .putString(KEY_SHOPIFY_REFRESH, shopifyRefresh)
            .putLong(KEY_SHOPIFY_EXPIRES, shopifyExpiresAt ?: 0L)
            .apply()
    }

    fun clear() {
        prefs.edit().clear().apply()
    }

    companion object {
        private const val KEY_JWT = "jwt"
        private const val KEY_OWNER = "owner_id"
        private const val KEY_SHOPIFY_ACCESS = "shopify_access"
        private const val KEY_SHOPIFY_REFRESH = "shopify_refresh"
        private const val KEY_SHOPIFY_EXPIRES = "shopify_expires"

        @Volatile private var instance: ShopSessionStore? = null

        fun get(context: Context): ShopSessionStore {
            val app = context.applicationContext
            return instance ?: synchronized(this) {
                instance ?: ShopSessionStore(app).also { instance = it }
            }
        }
    }
}
