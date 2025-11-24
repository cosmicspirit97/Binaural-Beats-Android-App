@file:OptIn(UnstableApi::class)
package com.example.binauralbeats

import com.example.binauralbeats.study.creativity.relax.memory.love.underwater.meditation.sleep.focus.study.reduceanxiety.R
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.IBinder
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import android.net.Uri
import android.content.ContentResolver
import androidx.annotation.OptIn
import androidx.media3.datasource.RawResourceDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerNotificationManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow



@UnstableApi
class AudioPlayerService : Service() {

    companion object {
        const val ACTION_PLAY = "ACTION_PLAY"
        const val ACTION_STOP = "ACTION_STOP"
        const val ACTION_PAUSE = "ACTION_PAUSE"
        const val CHANNEL_ID = "audio_channel"
        const val NOTIFICATION_ID = 1
    }

    private lateinit var player: ExoPlayer
    private lateinit var playerNotificationManager: PlayerNotificationManager
    private var currentTrackTitle: String = "Playing Music"
    private var currentImageResId: Int = R.drawable.ic_launcher_foreground

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

        player = ExoPlayer.Builder(this).build()

        player.addListener(object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                PlaybackStateBus.sendState(isPlaying)
            }
        })

        playerNotificationManager = PlayerNotificationManager.Builder(
            this,
            NOTIFICATION_ID,
            CHANNEL_ID
        ).apply {
            setMediaDescriptionAdapter(object : PlayerNotificationManager.MediaDescriptionAdapter {
                override fun getCurrentContentTitle(player: Player): CharSequence {
                    return currentTrackTitle // always updated from playTrack()
                }

                override fun createCurrentContentIntent(player: Player): PendingIntent? {
                    return null
                }

                override fun getCurrentContentText(player: Player): CharSequence? {
                    return "Binaural Beat"
                }

                override fun getCurrentLargeIcon(
                    player: Player,
                    callback: PlayerNotificationManager.BitmapCallback
                ): Bitmap? {
                    return BitmapFactory.decodeResource(resources, currentImageResId)
                }
            })

            setNotificationListener(object : PlayerNotificationManager.NotificationListener {
                override fun onNotificationPosted(
                    notificationId: Int,
                    notification: Notification,
                    ongoing: Boolean
                ) {
                    startForeground(notificationId, notification)
                }

                override fun onNotificationCancelled(
                    notificationId: Int,
                    dismissedByUser: Boolean
                ) {
                    stopForeground(STOP_FOREGROUND_REMOVE)
                    stopSelf()
                }
            })
        }.build()

        // Only show play/pause buttons
        playerNotificationManager.setUseNextAction(false)
        playerNotificationManager.setUsePreviousAction(false)
        playerNotificationManager.setUseFastForwardAction(false)
        playerNotificationManager.setUseRewindAction(false)
        playerNotificationManager.setUseStopAction(false)

        // Attach player to notification manager
        playerNotificationManager.setPlayer(player)
    }

    /**
     * Call this method whenever you want to load a new track
     */
    fun playTrack(audioResId: Int, title: String, imageResId: Int) {
        currentTrackTitle = title
        currentImageResId = imageResId

        val uri = RawResourceDataSource.buildRawResourceUri(audioResId)
        player.setMediaItem(MediaItem.fromUri(uri))
        player.prepare()
        player.play()

        // 🔄 Force update notification immediately
        playerNotificationManager.invalidate()
    }

override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    val action = intent?.action
    val resId = intent?.getIntExtra("audioResId", R.raw.love) ?: R.raw.love
    val title = intent?.getStringExtra("trackTitle") ?: "Playing Music"
    currentTrackTitle = title

    val imageId = intent?.getIntExtra("imageResId", R.drawable.ic_launcher_foreground)
    currentImageResId = imageId ?: R.drawable.ic_launcher_foreground

    // ✅ Correct URI for raw resources
    val uri = Uri.parse("android.resource://$packageName/$resId")

    when (action) {

        ACTION_PLAY -> {
            // ✅ Always set new track if it's different from current
            player.setMediaItem(MediaItem.fromUri(uri))
            player.repeatMode = Player.REPEAT_MODE_ONE
            player.prepare()
            player.play()
            PlaybackStateBus.sendState(true)
        }

        ACTION_PAUSE -> {
            if (player.isPlaying) {
                player.pause()
                PlaybackStateBus.sendState(false)
            }
        }

        ACTION_STOP -> {
            if (player.isPlaying) {
                player.stop()
                PlaybackStateBus.sendState(false)
                stopSelf()
            }
        }

        else -> {
            // Default: start playing new track
            player.setMediaItem(MediaItem.fromUri(uri))
            player.repeatMode = Player.REPEAT_MODE_ONE
            player.prepare()
            player.play()
            PlaybackStateBus.sendState(true)
        }
    }

    return START_NOT_STICKY
}

    override fun onDestroy() {
        player.release()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Audio Playback",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Used for background audio playback"
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
}

object PlaybackStateBus {
    private val _playbackState = MutableSharedFlow<Boolean>(replay = 1)
    val playbackState: SharedFlow<Boolean> = _playbackState

    fun sendState(isPlaying: Boolean) {
        _playbackState.tryEmit(isPlaying)
    }
}
