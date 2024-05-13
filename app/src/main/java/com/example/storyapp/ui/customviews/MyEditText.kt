package com.example.storyapp.ui.customviews

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.example.storyapp.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow

class MyEditText : AppCompatEditText, View.OnTouchListener {
    private lateinit var clearButtonImage: Drawable
    private lateinit var startDrawableImage: Drawable
    private lateinit var errorBackground: Drawable
    private lateinit var normalBackground: Drawable

    private val _isValidInput = MutableStateFlow(false)
    val isValidInput = _isValidInput.asStateFlow()

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
        clearButtonImage =
            ContextCompat.getDrawable(context, R.drawable.baseline_clear_black_24dp) as Drawable
        errorBackground =
            ContextCompat.getDrawable(context, R.drawable.edittext_error_background) as Drawable
        normalBackground =
            ContextCompat.getDrawable(context, R.drawable.edittext_normal_background) as Drawable
        setOnTouchListener(this)
        startDrawableImage = if (inputType == InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS + 1) {
            ContextCompat.getDrawable(context, R.drawable.baseline_email_black_24dp) as Drawable
        } else {
            ContextCompat.getDrawable(context, R.drawable.baseline_lock_black_24dp) as Drawable
        }

        background = normalBackground
        alpha = 0F

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val isNotEmpty =  s.toString().isNotEmpty()
                if (inputType == InputType.TYPE_TEXT_VARIATION_PASSWORD + 1  && isNotEmpty) {
                    showErrorForPassword()
                } else if (inputType == InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS + 1 && isNotEmpty) {
                    showErrorForEmail()
                } else if (inputType == InputType.TYPE_TEXT_VARIATION_PERSON_NAME + 1 && isNotEmpty) {
                    showErrorForUsername()
                }else{
                    background = normalBackground
                }
                if (s.toString().isNotEmpty()) showClearButton() else hideClearButton()
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        textSize = 12F
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
    }

    private fun showClearButton() {
        setButtonDrawables(endOfTheText = clearButtonImage)
    }

    private fun hideClearButton() {
        setButtonDrawables()
    }

    private fun setButtonDrawables(
        startOfTheText: Drawable? = null,
        topOfTheText: Drawable? = null,
        endOfTheText: Drawable? = null,
        bottomOfTheText: Drawable? = null
    ) {
        setCompoundDrawablesWithIntrinsicBounds(
            startOfTheText,
            topOfTheText,
            endOfTheText,
            bottomOfTheText
        )
    }

    private fun showErrorForPassword() {
        val isPasswordValid = text.toString().length >= 8
        if (!isPasswordValid) {
            setupErrorTask(context.getString(R.string.password_is_less_than_8_characters))
        } else {
            setupValidTask()
        }
    }

    private fun showErrorForEmail() {
        val isValidEmail =
            android.util.Patterns.EMAIL_ADDRESS.matcher(text.toString().trim()).matches();
        if (!isValidEmail) {
            setupErrorTask(context.getString(R.string.email_not_valid))
        } else {
            setupValidTask()
        }
    }

    private fun showErrorForUsername() {
        val isUsernameValid = text.toString().isNotEmpty()
        if (!isUsernameValid) {
            setupErrorTask(context.getString(R.string.username_cannot_be_empty))
        } else {
            setupValidTask()
        }
    }

    private fun setupErrorTask(errorMessage: String) {
        background = errorBackground
        _isValidInput.value = false
        error = errorMessage
        requestFocus()
    }

    private fun setupValidTask() {
        background = normalBackground
        _isValidInput.value = true
        error = null
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        if (compoundDrawables[2] != null) {
            val clearButtonStart: Float
            val clearButtonEnd: Float
            var isClearButtonClicked = false
            if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                clearButtonEnd = (clearButtonImage.intrinsicWidth + paddingStart).toFloat()
                when {
                    event.x < clearButtonEnd -> isClearButtonClicked = true
                }
            } else {
                clearButtonStart = (width - paddingEnd - clearButtonImage.intrinsicWidth).toFloat()
                when {
                    event.x > clearButtonStart -> isClearButtonClicked = true
                }
            }
            if (isClearButtonClicked) {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        clearButtonImage = ContextCompat.getDrawable(
                            context,
                            R.drawable.baseline_clear_black_24dp
                        ) as Drawable
                        showClearButton()
                        return true
                    }

                    MotionEvent.ACTION_UP -> {
                        clearButtonImage = ContextCompat.getDrawable(
                            context,
                            R.drawable.baseline_clear_black_24dp
                        ) as Drawable
                        when {
                            text != null -> text?.clear()
                        }
                        hideClearButton()
                        return true
                    }

                    else -> return false
                }
            } else return false
        }
        return false
    }


}