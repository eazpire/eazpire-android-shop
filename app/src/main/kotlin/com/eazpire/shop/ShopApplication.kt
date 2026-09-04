package com.eazpire.shop

import android.app.Application
import com.eazpire.shared.EazpireApps
import com.eazpire.shared.security.TrustedPackages

class ShopApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        TrustedPackages.ensurePackage(EazpireApps.CREATOR)
        TrustedPackages.ensurePackage(EazpireApps.SHOP)
        TrustedPackages.ensurePackage(EazpireApps.WEAR_PLAYER)
    }
}
