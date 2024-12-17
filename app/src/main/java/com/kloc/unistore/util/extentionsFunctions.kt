package com.kloc.unistore.util

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast

class extentionsFunctions {
    companion object {
        fun print(e: Exception) = Log.e("exception", e.stackTraceToString())

        fun showMessage(
            context: Context,
            message: String?
        ) = Handler(Looper.getMainLooper()).post {Toast.makeText(context, message, Toast.LENGTH_SHORT).show()}
    }
}

