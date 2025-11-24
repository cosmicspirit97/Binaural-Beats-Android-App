package com.example.binauralbeats.utils


import android.content.Context
import android.content.Intent
import androidx.annotation.DrawableRes
import androidx.annotation.OptIn
import androidx.annotation.RawRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.util.UnstableApi
import com.example.binauralbeats.AnimateBackground
import com.example.binauralbeats.AudioPlayerService
import com.example.binauralbeats.PlaybackStateBus
import com.example.binauralbeats.study.creativity.relax.memory.love.underwater.meditation.sleep.focus.study.reduceanxiety.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class AudioPlayerViewModel : ViewModel() {

    // Private mutable state
    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying


    /*init {
        // Collect playback state changes from the bus
        viewModelScope.launch {
            PlaybackStateBus.playbackState.collect { state ->
                // Only update if value actually changes
                if (_isPlaying.value != state) {
                    _isPlaying.value = state
                }
            }
        }
    }*/
    init { //new code
        // Listen for playback state updates from service
        viewModelScope.launch {
            PlaybackStateBus.playbackState.collect { state ->
                _isPlaying.value = state
            }
        }
    }
    /**
     * Send a playback state change request to the bus.
     * The bus will then update the ViewModel via the collector above.
     */
    fun setIsPlaying(playing: Boolean) {
        PlaybackStateBus.sendState(playing)
    }

    // Helper methods for better readability in UI
    fun play() = setIsPlaying(true)
    fun pause() = setIsPlaying(false)
}
@OptIn(UnstableApi::class)
@Composable
fun SimpleAudioPlayerScreen(
    @DrawableRes imageResId: Int,
    @RawRes audioResId: Int,
    title: String = "",
    context: Context = LocalContext.current,
    viewModel: AudioPlayerViewModel = viewModel()
) {
    val isPlaying by viewModel.isPlaying.collectAsState()

    // Play the selected track when screen loads (only once for given track)
    LaunchedEffect(audioResId) {
        val intent = Intent(context, AudioPlayerService::class.java).apply {
            action = AudioPlayerService.ACTION_PLAY
            putExtra("audioResId", audioResId)
            putExtra("trackTitle", title)
            putExtra("imageResId", imageResId)
        }
        context.startForegroundService(intent)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1B263B)),
        contentAlignment = Alignment.Center
    ) {
        AnimateBackground()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (title.isNotEmpty()) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White
                )
                Spacer(Modifier.height(24.dp))
            }

            Image(
                painter = painterResource(id = imageResId),
                contentDescription = null,
                modifier = Modifier
                    .size(300.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = {
                    val action = if (isPlaying)
                        AudioPlayerService.ACTION_PAUSE
                    else
                        AudioPlayerService.ACTION_PLAY

                    val intent = Intent(context, AudioPlayerService::class.java).apply {
                        this.action = action
                        putExtra("audioResId", audioResId)
                        putExtra("trackTitle", title)
                        putExtra("imageResId", imageResId)
                    }
                    context.startForegroundService(intent)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Cyan),
                modifier = Modifier.size(80.dp)
            ) {
                val icon = if (isPlaying) R.drawable.pause else R.drawable.play
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    modifier = Modifier.size(40.dp),
                    tint = Color.Black
                )
            }
        }
    }
}


