package com.example.storyappproject.ui.customview

import android.content.Context
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.method.MovementMethod
import android.text.style.ClickableSpan
import android.util.AttributeSet
import android.view.View
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.storyapp.R

class CustomText : androidx.appcompat.widget.AppCompatTextView {
    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        val textTobeAdded = resources.getString(R.string.create_account)
        val spanString = SpannableString(textTobeAdded)
        val loginText = object : ClickableSpan() {
            override fun onClick(widget: View) {
                findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
            }
        }

        var count = 0
        for (value in textTobeAdded) {
            if (value == '?') {
                break
            }
            count++
        }
        spanString.setSpan(loginText, count+1, textTobeAdded.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        text = spanString
        movementMethod = LinkMovementMethod.getInstance()
    }


}