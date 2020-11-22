package com.canberkozcelik.paperwall

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var mInterstitialAd: InterstitialAd
    private val navController by lazy { findNavController(R.id.nav_host_fragment) }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initAds()
    }

    private fun initAds() {
        MobileAds.initialize(this) {}

        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        mInterstitialAd = InterstitialAd(this)
        mInterstitialAd.adUnitId = "<ENTER_YOUR_UNIT_ID>"
        mInterstitialAd.loadAd(AdRequest.Builder().build())
        mInterstitialAd.adListener = object : AdListener() {

            override fun onAdFailedToLoad(p0: Int) {
                super.onAdFailedToLoad(p0)
                Timber.d("%d", p0)
            }
        }
    }

    fun showAd() {
        mInterstitialAd.isLoaded.let { mInterstitialAd.show() }
    }

    override fun onSupportNavigateUp() = navController.navigateUp()
}
