package com.example.storyapp.ui.customviews

import android.app.Activity
import android.app.AlertDialog

class CustomAlertDialog(private val activity: Activity) {
    private var dialog: AlertDialog? = null

    fun startDialog(message: String, buttonName: String, unit: () -> Unit = {}) {
        val builder = AlertDialog.Builder(activity)

        builder.setMessage(message)
        builder.setPositiveButton(buttonName) { dialog, which ->
            closeDialog()
            unit()
        }
        dialog = builder.create()
        dialog?.setCancelable(false)
        dialog?.setCanceledOnTouchOutside(false)
        dialog?.show()
    }

    private fun closeDialog() {
        dialog?.dismiss()
        dialog = null
    }
}