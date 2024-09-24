package com.example.mapstest.nativemap

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.cardview.widget.CardView

class CustomMarkerView(private val context: Context): FrameLayout(context) {
    init {
        addView(CardView(context).apply {
            id = View.generateViewId()
            //setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindow)
//            setContent {
//                Text("MarkerView")
//            }
            addView(TextView(context).apply { text = "This is a marker" })
        })
    }
}