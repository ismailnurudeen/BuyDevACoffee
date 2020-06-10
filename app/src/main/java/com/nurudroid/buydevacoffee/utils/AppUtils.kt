package com.nurudroid.buydevacoffee.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import java.text.SimpleDateFormat
import java.util.*

class AppUtils(private val context: Context) {
    fun formatDate(date: Date?, format: String = "dd/MM/yyyy hh:MM a"): String {
        return if (date != null) SimpleDateFormat(format, Locale.getDefault()).format(date)
        else ""

    }

    fun shareText(txt: String, title: String) {
        val otherIntent = Intent(Intent.ACTION_SEND)
        otherIntent.type = "text/plain"
        otherIntent.putExtra(Intent.EXTRA_TEXT, txt)
        context.startActivity(Intent.createChooser(otherIntent, title))
    }

    fun sendEmail(subject: String, message: String, emails: Array<String>) {
        val emailIntent = Intent(Intent.ACTION_SENDTO)
        emailIntent.data = Uri.parse("mailto:")
        emailIntent.putExtra(Intent.EXTRA_EMAIL, emails)
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
        emailIntent.putExtra(Intent.EXTRA_TEXT, message)
        try {
            context.startActivity(Intent.createChooser(emailIntent, "Select Email app..."))
        } catch (anf: ActivityNotFoundException) {
            shareText(message, "Send Email With?")
        }
    }
}