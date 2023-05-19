package com.elkdocs.handwritter.presentation.page_edit_screen

import android.content.Context
import android.graphics.Rect
import android.text.Layout
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.AppCompatEditText

class CustomEditText(context: Context, attrs: AttributeSet) : AppCompatEditText(context, attrs) {

    override fun onTouchEvent(event: MotionEvent): Boolean {
        super.onTouchEvent(event)

        val touchPosition = getOffsetForPosition(event.x, event.y)
        setSelection(touchPosition)
        super.performClick() // Call performClick() on the view

        return true
    }

}