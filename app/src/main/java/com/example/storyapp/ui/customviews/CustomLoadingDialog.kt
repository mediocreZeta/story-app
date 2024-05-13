package com.example.storyapp.ui.customviews

import android.app.Activity
import android.app.AlertDialog
import com.example.storyapp.R

class CustomLoadingDialog(private val activity: Activity) {
    private var dialog: AlertDialog? = null

    fun startLoadingDialog() {
        val builder = AlertDialog.Builder(activity)
        val inflater = activity.layoutInflater

        builder.setView(inflater.inflate(R.layout.custom_dialog_layout, null))

        dialog = builder.create()
        dialog?.setCancelable(false)
        dialog?.setCanceledOnTouchOutside(false)
        dialog?.show()
    }

    fun closeLoadingDialog() {
        dialog?.dismiss()
        dialog = null
    }
}