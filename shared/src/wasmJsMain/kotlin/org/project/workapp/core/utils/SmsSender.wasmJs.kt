package org.project.workapp.core.utils

actual fun sendSms(phoneNumber: String, message: String) {
    println("Wasm (Web): SMS ignored")
}