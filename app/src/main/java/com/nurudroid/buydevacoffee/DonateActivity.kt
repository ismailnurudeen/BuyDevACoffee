package com.nurudroid.buydevacoffee

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.anjlab.android.iab.v3.BillingProcessor
import com.anjlab.android.iab.v3.TransactionDetails
import com.nurudroid.buydevacoffee.utils.AppUtils
import com.nurudroid.buydevacoffee.utils.Constants
import com.nurudroid.buydevacoffee.utils.PrefsManager
import kotlinx.android.synthetic.main.activity_donate.*
import kotlinx.android.synthetic.main.dialog_donation_successful.view.*

class DonateActivity : AppCompatActivity(), BillingProcessor.IBillingHandler {
    private lateinit var appUtils: AppUtils
    private lateinit var prefsManager: PrefsManager
    lateinit var bp: BillingProcessor
    private var productName: String = "Espresso"
    private var clickedProductId = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_donate)
        appUtils = AppUtils(this)
        prefsManager = PrefsManager(this)
        donate_nav_back.setOnClickListener {
            finish()
        }
        bp = BillingProcessor(this, Constants.LICENSE_KEY, this)
        bp.initialize()
    }

    fun donate(v: View) {
        clickedProductId = when (v.id) {
            R.id.donate_coffe_1 -> {
                productName = getString(R.string.espresso)
                getString(R.string.small_coffee)
            }
            R.id.donate_coffee_2 -> {
                productName = getString(R.string.cafe_latte)
                getString(R.string.medium_coffee)
            }
            R.id.donate_coffe_3 -> {
                productName = getString(R.string.cappuccino)
                getString(R.string.big_coffee)
            }
            else -> {
                getString(R.string.small_coffee)
            }
        }

        if (bp.isInitialized) {
            if (bp.isOneTimePurchaseSupported) {
                bp.purchase(this, clickedProductId, null, null)
            } else {
                println("One time purchase not supported")
            }
        } else {
            Toast.makeText(this, "Initializing... click again.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBillingInitialized() {
    }

    override fun onPurchaseHistoryRestored() {
    }

    override fun onProductPurchased(productId: String, details: TransactionDetails?) {
        val purchaseData = details!!.purchaseInfo.purchaseData
        val payload = purchaseData.developerPayload
        val orderId = purchaseData.orderId
        val purchaseTime = appUtils.formatDate(purchaseData.purchaseTime)

        val receipt = "PRODUCT NAME:$productName\n" +
                "ORDER ID:$orderId\n" +
                "PURCHASE TIME:${purchaseTime}"
        bp.consumePurchase(productId)
        showDonationSuccessDialog(receipt, productId)
        prefsManager.userHasDonated = true
    }

    override fun onBillingError(errorCode: Int, error: Throwable?) {
        Log.d("BILLING ERROR", "${error?.message}")
        Toast.makeText(this, "${error?.localizedMessage}", Toast.LENGTH_LONG).show()

        if (errorCode == 102) {
            bp.purchase(null, clickedProductId)
        } else {
            Toast.makeText(this, "${error?.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }

    private fun showDonationSuccessDialog(receipt: String, productId: String = "") {
        val donateDialogView =
            LayoutInflater.from(this).inflate(R.layout.dialog_donation_successful, null)
        val builder = AlertDialog.Builder(this)
        builder.setView(donateDialogView)
        val dialog = builder.create()
        donateDialogView.donate_dialog_receipt.text = receipt
        donateDialogView.donate_dialog_close.setOnClickListener {
            dialog.dismiss()
        }

        donateDialogView.donatation_save_receipt.setOnClickListener {
            appUtils.shareText(receipt, "Save Receipt With")
        }
        donateDialogView.donatation_make_request.setOnClickListener {
            appUtils.sendEmail(
                "${getString(R.string.app_name)} Special Request",
                "$receipt\n\n<b>What's your Request?</b>",
                arrayOf(getString(R.string.my_email))
            )
        }
        dialog.setOnDismissListener {
            Toast.makeText(this, "Thanks for your support :)", Toast.LENGTH_SHORT).show()
        }
        dialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (!bp.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        bp.release()
    }
}