package org.project.workapp.core.utils

import android.util.Log

actual fun sendSms(phoneNumber: String, message: String) {
    Log.d("SMS_STUB", "Sending to $phoneNumber: $message")
}