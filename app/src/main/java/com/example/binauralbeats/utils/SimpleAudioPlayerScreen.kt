package com.example.binauralbeats.utils


import android.content.Context
import android.content.Intent
import androidx.compose.ui.graphics.Color
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import com.example.binauralbeats.AnimateBackground
import com.example.binauralbeats.AudioPlayerService
import com.example.binauralbeats.R
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.binauralbeats.PlaybackStateBus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class AudioPlayerViewModel : ViewModel() {

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    init {
        viewModelScope.launch {
            PlaybackStateBus.playbackState.collect { state ->
                _isPlaying.value = state
            }
        }
    }

    fun setIsPlaying(playing: Boolean) {
        _isPlaying.value = playing
        PlaybackStateBus.sendState(playing)
    }
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

    // Start playing when the screen appears
    LaunchedEffect(audioResId) {
        val intent = Intent(context, AudioPlayerService::class.java).apply {
            action = AudioPlayerService.ACTION_PLAY
            putExtra("audioResId", audioResId)
            putExtra("trackTitle", title)
            putExtra("imageResId", imageResId)
        }
        context.startForegroundService(intent)
        viewModel.setIsPlaying(true)
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
                    }
                    context.startForegroundService(intent)
                    viewModel.setIsPlaying(!isPlaying)
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


