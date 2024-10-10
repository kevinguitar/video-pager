package com.example.videopager

import android.os.Bundle
import android.view.SurfaceView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.VideoSize
import com.example.videopager.ui.theme.VideoPagerTheme
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val videoPlayer = VideoPlayer(this)
        val videoInteractors = (0..<videoPlayer.videoCount)
            .map { index -> VideoInteractor(videoPlayer, index) }

        enableEdgeToEdge()
        setContent {
            VideoPagerTheme {
                val pagerState = rememberPagerState { videoPlayer.videoCount }
                val size by videoPlayer.videoSize.collectAsStateWithLifecycle()

                VerticalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { index ->
                    VideoCard(
                        videoInteractor = videoInteractors[index],
                        modifier = Modifier
                            .fillMaxSize()
                            .aspectRatio(
                                if (size == VideoSize.UNKNOWN) {
                                    1f
                                } else {
                                    size.width / size.height.toFloat()
                                }
                            )
                    )
                }

                LaunchedEffect(pagerState) {
                    snapshotFlow { pagerState.currentPage }.collect { index ->
                        videoInteractors[index].playbackTrigger.sendEvent(Unit)
                    }
                }
            }
        }
    }
}

@Composable
fun VideoCard(
    videoInteractor: VideoInteractor,
    modifier: Modifier,
) {
    val context = LocalContext.current
    val surfaceView = remember { SurfaceView(context) }

    AndroidView(
        factory = { surfaceView },
        modifier = modifier,
    )

    LaunchedEffect(key1 = videoInteractor.playbackTrigger) {
        videoInteractor.playbackTrigger
            .onEach {
                if (it.consume() != null) {
                    videoInteractor.play(surfaceView)
                }
            }
            .collect()
    }
}
