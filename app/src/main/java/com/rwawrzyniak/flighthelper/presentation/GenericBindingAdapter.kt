package com.rwawrzyniak.flighthelper.presentation

import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import androidx.databinding.BindingAdapter

@BindingAdapter("hideOnLoading")
fun Group.hideOnLoading(responseState: UIState<Any>) {
	visibility = if (responseState is UIState.Loading)
		View.GONE
	else
		View.VISIBLE
}

@BindingAdapter("showOnLoading")
fun ProgressBar.showOnLoading(responseState: UIState<Any>) {
	visibility = if (responseState is UIState.Loading)
		View.VISIBLE
	else
		View.GONE
}

@BindingAdapter("showOnError")
fun TextView.showError(responseState: UIState<Any>) {
	visibility = if (responseState is UIState.Error) {
		text = responseState.errorMessage
		View.VISIBLE
	} else
		View.GONE
}
