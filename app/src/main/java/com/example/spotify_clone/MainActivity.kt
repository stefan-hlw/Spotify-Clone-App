package com.example.spotify_clone

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.spotify_clone.ui.theme.Spotify_cloneTheme
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.protocol.types.Track


/**
 * 24.08.2023 Spotify Clone app
 */
class MainActivity : ComponentActivity() {

    private val clientId = "41b0ef3ce34a47a2aaa7b93aa0fac48d"
    private val redirectUri = "spotifyclone://callback"
    private var spotifyAppRemote: SpotifyAppRemote? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Spotify_cloneTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                    TogglePlayButton()
                }
            }
           //test
        }
    }
    override fun onStart() {
        super.onStart()
        val connectionParams = ConnectionParams.Builder(clientId)
            .setRedirectUri(redirectUri)
            .showAuthView(true)
            .build()

        SpotifyAppRemote.connect(this, connectionParams, object : Connector.ConnectionListener {
            override fun onConnected(appRemote: SpotifyAppRemote) {
                spotifyAppRemote = appRemote
                Log.d("MainActivity", "Connected! Yay!")
                // Now you can start interacting with App Remote
                connected()
            }

            override fun onFailure(throwable: Throwable) {
                Log.e("MainActivity", throwable.message, throwable)
                // Something went wrong when attempting to connect! Handle errors here
            }
        })
    }

    private fun connected() {
        spotifyAppRemote?.let { appRemote ->
            // Play a playlist
            val playlistURI = "spotify:playlist:37i9dQZF1DX2sUQwD7tbmL"
            appRemote.playerApi.play(playlistURI)
            // Subscribe to PlayerState
            appRemote.playerApi.subscribeToPlayerState().setEventCallback {
                val track: Track = it.track
                Log.d("MainActivity", track.name + " by " + track.artist.name)
            }
        }

    }

    override fun onStop() {
        super.onStop()
        spotifyAppRemote?.let {
            it.playerApi.pause()
            SpotifyAppRemote.disconnect(it)
        }

    }
    private fun togglePlay(boolean: Boolean) {
        if(!boolean) {
            spotifyAppRemote?.playerApi?.pause()
        } else {
            spotifyAppRemote?.playerApi?.resume()
        }
    }
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Composable
fun TogglePlayButton(modifier: Modifier = Modifier) {
    // Fetching the Local Context
    val mContext = LocalContext.current

    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {

        // Declaring a boolean value for storing checked state
        val mCheckedState = remember{ mutableStateOf(false)}

        // Creating a Switch, when value changes,
        // it updates mCheckedState value
        Switch(checked = mCheckedState.value, onCheckedChange = {mCheckedState.value = it}, modifier)

        Toast.makeText(mContext, mCheckedState.value.toString(), Toast.LENGTH_SHORT).show()
        // Adding a Space of 100dp height
        Spacer(modifier = Modifier.height(100.dp))
    }
}
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Spotify_cloneTheme {
        Greeting("Android")
    }
}