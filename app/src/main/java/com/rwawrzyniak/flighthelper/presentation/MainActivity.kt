package com.rwawrzyniak.flighthelper.presentation

import android.graphics.Rect
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.afollestad.materialdialogs.MaterialDialog
import com.rwawrzyniak.flighthelper.R
import com.rwawrzyniak.flighthelper.business.domain.util.NetworkManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@FlowPreview
@AndroidEntryPoint
class MainActivity : AppCompatActivity(R.layout.activity_main) {

	@Inject
	lateinit var networkManager: NetworkManager

    @Inject
    lateinit var inputMethodManager: InputMethodManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
		// TODO this causes ui freeze
//		lifecycleScope.launch {
//			networkManager.observeConnectivity()
//				.collectLatest{ isConnected ->
//					if(isConnected.not()){
//						showNoConnectivityDialog()
//					}
//				}
//		}
    }

	private fun showNoConnectivityDialog(){
		MaterialDialog(this).apply {
			title(R.string.no_connectivity_title)
				.message(R.string.no_connectivity_explain)
		}.show()
	}

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    // This clear focus for all edit texts, when clicked outside.
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v: View? = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }
}
