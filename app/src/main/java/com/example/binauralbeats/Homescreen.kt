package com.example.binauralbeats

import com.example.binauralbeats.study.creativity.relax.memory.love.underwater.meditation.sleep.focus.study.reduceanxiety.R
import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.binauralbeats.utils.Config
import com.example.binauralbeats.utils.SimpleAudioPlayerScreen
import kotlinx.coroutines.delay
import com.google.android.gms.ads.MobileAds
import android.widget.LinearLayout
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import androidx.compose.ui.viewinterop.AndroidView


@Composable
fun AppNavigation(modifier: Modifier,showAdThen: (action: () -> Unit) -> Unit
)
{
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(

                onStudyClick         = { showAdThen { navController.navigate(Screen.Player.create("Study")) }},
                onFocusClick         = { showAdThen { navController.navigate(Screen.Player.create("Focus")) }},
                onCreativityClick    = { showAdThen { navController.navigate(Screen.Player.create("Creativity")) }},
                onMemoryClick        = { showAdThen { navController.navigate(Screen.Player.create("Memory")) }},
                onLoveClick          = { showAdThen { navController.navigate(Screen.Player.create("Energy")) }},
                onRelaxClick         = { showAdThen { navController.navigate(Screen.Player.create("Relax")) }},
                onSleepClick         = { showAdThen { navController.navigate(Screen.Player.create("Sleep")) }},
                onUnderwaterClick    = { showAdThen { navController.navigate(Screen.Player.create("Underwater")) }},
                onReduceAnxietyClick = { showAdThen { navController.navigate(Screen.Player.create("Reduce Anxiety"))} }
            )
        }

        composable(
            route = Screen.Player.route,
            arguments = listOf(navArgument("name") { type = NavType.StringType })
        ) { backStackEntry ->
            val name = backStackEntry.arguments?.getString("name") ?: return@composable

            // Look up audio + image by name from Config
            val track = remember(name) { Config.getTrackByName(name) }
            if (track != null) {
                val imageRes = remember(name) { Config.getImageResByName(name) }
                SimpleAudioPlayerScreen(
                    imageResId = imageRes,
                    audioResId = track.resId,
                    title = name
                )
            } else {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Track not found: $name", color = Color.White)
                }
            }
        }
    }
}
/*
@Composable//for ads purpose
fun BannerAdView(adUnitId: String) {
    AndroidView(
        factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                this.adUnitId = adUnitId
                loadAd(AdRequest.Builder().build())
            }
        },
        update = { adView ->
            adView.loadAd(AdRequest.Builder().build())
        }
    )
}*/
@Composable
fun BannerAdView(adUnitId: String) {
    val context = LocalContext.current

    AndroidView(
        factory = {
            AdView(it).apply {
                val metrics = DisplayMetrics().also { m ->
                    (it.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.getMetrics(m)
                }
                val adWidth = (metrics.widthPixels / metrics.density).toInt()

                setAdSize(AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(it, adWidth))
                this.adUnitId = adUnitId
                loadAd(AdRequest.Builder().build())
            }
        },
        update = { it.loadAd(AdRequest.Builder().build()) }
    )
}


@Composable
fun HomeScreen(
    onStudyClick: () -> Unit,
    onFocusClick: () -> Unit,
    onCreativityClick: () -> Unit,
    onMemoryClick: () -> Unit,
    onLoveClick: () -> Unit,
    onRelaxClick: () -> Unit,
    onSleepClick: () -> Unit,
    onUnderwaterClick: () -> Unit,
    onReduceAnxietyClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Background image from drawable
        AnimateBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 95.dp, bottom = 65.dp, start = 20.dp, end = 20.dp),
        ) {


            Spacer(modifier = Modifier.height(16.dp))

            val cards = listOf(
                Triple("Study", R.drawable.study, onStudyClick),
                Triple("Focus", R.drawable.focus_1, onFocusClick),
                Triple("Creativity", R.drawable.creative_1, onCreativityClick),
                Triple("Memory", R.drawable.memory_1, onMemoryClick),
                Triple("Love", R.drawable.love, onLoveClick),
                Triple("Relax", R.drawable.focus, onRelaxClick),
                Triple("Sleep", R.drawable.relax_1, onSleepClick),
                Triple("Underwater", R.drawable.underwater_1, onUnderwaterClick),
                Triple("Reduce Anxiety", R.drawable.reduce_anxiety, onReduceAnxietyClick)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(25.dp),
                horizontalArrangement = Arrangement.spacedBy(25.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(cards) { (label, iconRes, clickAction) ->
                    CategoryCard(label = label, iconRes = iconRes, onClick = clickAction)
                }
            }


        }

    }
}

@Composable
fun CategoryCard(
    label: String,
    iconRes: Int,
    onClick: () -> Unit
) {
    var clicked by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (clicked) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "cardScale"
    )

    val alpha by animateFloatAsState(
        targetValue = if (clicked) 0.8f else 1f,
        animationSpec = tween(300),
        label = "cardAlpha"
    )

    // Delayed navigation after animation
    LaunchedEffect(clicked) {
        if (clicked) {
            delay(250) // Wait for animation to feel complete
            onClick()
            clicked = false
        }
    }


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.alpha = alpha
            }
            .clickable {
                clicked = true
            },
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF7198AF),
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,

            ) {
            Box(modifier = Modifier.fillMaxSize()
                /*.border(
                    width = 2.dp,
                    color = Color(0xFFCFD2CB),
                    shape = RoundedCornerShape(20.dp) // Match card shape
                )*/) {
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = label,
                    modifier = Modifier
                        .align(Alignment.Center)

                        .size(100.dp)
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                        },
                    contentScale = ContentScale.Crop,
                )

                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(Color(0xFF153A50)) // semi-transparent black
                        .padding(vertical = 8.dp)


                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.displayLarge,
                        color = Color.White,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }

    }
}
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Player : Screen("player/{name}") {
        fun create(name: String) = "player/$name"  // avoid naming clash with 'route' prop
    }
}

@Composable
fun AnimateBackground(){
    val infiniteTransition = rememberInfiniteTransition()

    val color1 = infiniteTransition.animateColor(
        initialValue = Color(0xFF7198AF),
        targetValue = Color(0xFF153A50),
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    val color2 = infiniteTransition.animateColor(
        initialValue = Color(0xFF5B8094),
        targetValue = Color(0xFF7198AF),
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val color3 = infiniteTransition.animateColor(
        initialValue = Color(0xFF87B1C8),
        targetValue = Color(0xFF153A50),
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )
    Box(modifier = Modifier.fillMaxSize()
        .background(brush = Brush.verticalGradient(
            colors = listOf(color1.value, color2.value, color3.value),
            startY = 0f,
            endY = Float.POSITIVE_INFINITY
        )))
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen(onStudyClick = {}, onFocusClick = {}, onCreativityClick = {}, onMemoryClick = {}, onLoveClick = {}, onRelaxClick = {}, onSleepClick = {}, onUnderwaterClick = {}, onReduceAnxietyClick = {})
}
