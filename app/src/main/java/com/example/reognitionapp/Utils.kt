package com.example.reognitionapp

import android.util.Patterns
import android.view.View

fun isValidEmail(email: String?): Boolean {
    return !email.isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

fun setVisibility(view: View, visible: Boolean) {
    if (visible)
        view.visibility = View.VISIBLE
    else
        view.visibility = View.GONE
}