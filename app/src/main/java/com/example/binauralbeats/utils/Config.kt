package com.example.binauralbeats.utils


import androidx.annotation.DrawableRes
import com.example.binauralbeats.R

data class AudioTrack(val name: String, val resId: Int)

class Config {
    companion object {
        // Your existing audio map
        val audioTracks: HashMap<String, Int> = hashMapOf(
            "Creativity" to R.raw.creativity,
            "Focus" to R.raw.focussed,
            "Relax" to R.raw.relax,
            "Sleep" to R.raw.sleep,
            "Energy" to R.raw.love,
            "Memory" to R.raw.memory,
            "Underwater" to R.raw.underwater,
            "Reduce Anxiety" to R.raw.reduceanxiety,
            "Study" to R.raw.simply
        )

        // Your existing list
        val trackList: List<AudioTrack> = listOf(
            AudioTrack("Creativity", R.raw.creativity),
            AudioTrack("Focus", R.raw.focussed),
            AudioTrack("Relax", R.raw.relax),
            AudioTrack("Sleep", R.raw.sleep),
            AudioTrack("Energy", R.raw.love),
            AudioTrack("Memory", R.raw.memory),
            AudioTrack("Underwater", R.raw.underwater),
            AudioTrack("Reduce Anxiety", R.raw.reduceanxiety),
            AudioTrack("Study", R.raw.simply)
        )

        // NEW: image per track name (match what you already show in HomeScreen)
        private val imageByName: Map<String, Int> = mapOf(
            "Creativity" to R.drawable.creative_1,
            "Focus" to R.drawable.focus_1,
            "Relax" to R.drawable.focus,          // you used R.drawable.focus for Relax card
            "Sleep" to R.drawable.relax_1,
            "Energy" to R.drawable.love,
            "Memory" to R.drawable.memory_1,
            "Underwater" to R.drawable.underwater_1,
            "Reduce Anxiety" to R.drawable.reduce_anxiety,
            "Study" to R.drawable.study
        )

        fun getTrackByName(name: String): AudioTrack? =
            trackList.find { it.name == name }

        @DrawableRes
        fun getImageResByName(name: String): Int =
            imageByName[name] ?: R.drawable.background_3 // fallback image
    }
}
