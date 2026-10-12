package org.project.workapp.utils

actual fun sendSms(phoneNumber: String, message: String) {
    println("Web (JS): SMS ignored")
}