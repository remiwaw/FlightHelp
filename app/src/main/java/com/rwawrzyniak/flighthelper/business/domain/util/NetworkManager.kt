package com.rwawrzyniak.flighthelper.business.domain.util

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

private const val LOG_TAG = "NetworkManager"

@ExperimentalCoroutinesApi
@FlowPreview
class NetworkManager @Inject constructor(application: Application) {

	private val connectionState = ConflatedBroadcastChannel(true)

	private val networkCallback: ConnectivityManager.NetworkCallback = object : ConnectivityManager.NetworkCallback() {
		// TODO FIX blocking

		override fun onAvailable(network: Network?) {
			runBlocking {
				Log.i(LOG_TAG, "NetworkCallback#onAvailable()")
				connectionState.send(true)
			}
        }

        override fun onLost(network: Network?) {
			Dispatchers.IO.run {
				runBlocking {
					Log.i(LOG_TAG, "NetworkCallback#onLost()")
					connectionState.send(false)
				}
			}
        }
    }

    private val networkRequest = NetworkRequest.Builder().run {

        // we have internet access
        addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // ...and this was validated by the system
            addCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // ...and the connection is actually usable by foreground apps
            addCapability(NetworkCapabilities.NET_CAPABILITY_FOREGROUND)
            // ...and we don't drive through a tunnel
            addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_SUSPENDED)
        }

        build()
    }

    private val connectivityManager: ConnectivityManager by lazy {
        (application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).apply {
            registerNetworkCallback(networkRequest, networkCallback)
        }
    }

    suspend fun observeConnectivity() = connectionState.run {
		send(determineCurrentState())
		asFlow().distinctUntilChanged()
	}


    fun isOnline(): Boolean = determineCurrentState()
    // endregion

    private fun determineCurrentState(): Boolean {
        val isOnline = connectivityManager.activeNetwork != null
		Log.i(LOG_TAG, "#determineCurrentState(). Online: $isOnline")
        return isOnline
    }
}
