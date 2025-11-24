package com.example.binauralbeats.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationManagerCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.ui.PlayerNotificationManager
import com.example.binauralapp.R
import com.google.android.exoplayer2.util.Util // if using exo util; not required in Media3

@UnstableApi
class AudioService : Service() {

    companion object {
        const val ACTION_PLAY_NAME = "PLAY_NAME"   // set & play a track by "name"
        const val ACTION_TOGGLE    = "TOGGLE"      // play/pause toggle
        const val EXTRA_NAME       = "name"

        private const val NOTIF_ID = 1001
        private const val CHANNEL_ID = "playback_channel"
    }

    private lateinit var player: ExoPlayer
    private lateinit var mediaSession: MediaSession
    private var notificationManager: PlayerNotificationManager? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()

        player = ExoPlayer.Builder(this).build()
        mediaSession = MediaSession.Builder(this, player)
            .setId("BinauralSession")
            .build()

        // Build a media-style notification with actions
        notificationManager = PlayerNotificationManager.Builder(this, NOTIF_ID, CHANNEL_ID)
            .setMediaDescriptionAdapter(object : PlayerNotificationManager.MediaDescriptionAdapter {
                override fun getCurrentContentTitle(player: androidx.media3.common.Player): CharSequence {
                    return player.mediaMetadata.title ?: "Playing"
                }
                override fun createCurrentContentIntent(player: androidx.media3.common.Player) = null
                override fun getCurrentContentText(player: androidx.media3.common.Player) = null
                override fun getCurrentLargeIcon(
                    player: androidx.media3.common.Player,
                    callback: PlayerNotificationManager.BitmapCallback
                ) = null
            })
            .setNotificationListener(object : PlayerNotificationManager.NotificationListener {
                override fun onNotificationPosted(
                    notificationId: Int,
                    notification: Notification,
                    ongoing: Boolean
                ) {
                    if (ongoing) {
                        // Foreground while playing/buffering
                        startForeground(notificationId, notification)
                    } else {
                        // Not playing: keep the notification but not foreground
                        stopForeground(STOP_FOREGROUND_DETACH)
                        NotificationManagerCompat.from(this@AudioService)
                            .notify(notificationId, notification)
                    }
                }

                override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
                    stopSelf()
                }
            })
            .build().apply {
                setUseNextAction(false)
                setUsePreviousAction(false)
                setMediaSessionToken(mediaSession.sessionCompatToken)
                setPlayer(player)
            }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_PLAY_NAME -> {
                val name = intent.getStringExtra(EXTRA_NAME) ?: return START_STICKY
                // Resolve your raw + title from Config using `name`
                val track = com.example.binauralapp.util.Config.getTrackByName(name) ?: return START_STICKY
                val uri = com.google.android.exoplayer2.upstream.RawResourceDataSource.buildRawResourceUri(track.resId)

                val mediaItem = MediaItem.Builder()
                    .setUri(uri)
                    .setMediaId(name)
                    .setTag(name)
                    .setMediaMetadata(
                        androidx.media3.common.MediaMetadata.Builder()
                            .setTitle(name)
                            .build()
                    )
                    .build()

                player.setMediaItem(mediaItem)
                player.prepare()
                player.play()
            }
            ACTION_TOGGLE -> {
                if (player.isPlaying) player.pause() else player.play()
            }
        }
        return START_STICKY
    }

    override fun onDestroy() {
        notificationManager?.setPlayer(null)
        mediaSession.release()
        player.release()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val ch = NotificationChannel(
                CHANNEL_ID,
                "Playback",
                NotificationManager.IMPORTANCE_LOW
            )
            ch.description = "Binaural beats playback"
            (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(ch)
        }
    }
}
