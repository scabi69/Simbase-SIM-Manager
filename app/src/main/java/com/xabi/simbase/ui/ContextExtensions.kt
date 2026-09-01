package com.xabi.simbase.ui

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper

/**
 * Safely unwrap Context to find the enclosing Activity.
 * Returns null if the context is not associated with an Activity.
 */
tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
