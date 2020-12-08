package com.rwawrzyniak.flighthelper.presentation

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
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
}
