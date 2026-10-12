package org.project.workapp.feature.employee

import org.project.workapp.core.utils.sendSms

fun handleAlarmAction(storeName: String, isArming: Boolean): String {
    val message = if (isArming) "Объект взят под охрану" else "Объект снят с охраны"
    sendSms("STUB_NUMBER", message)
    return message
}