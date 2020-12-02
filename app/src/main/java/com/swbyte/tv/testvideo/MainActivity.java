package com.swbyte.tv.testvideo;


import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.EventLogger;
import com.google.android.exoplayer2.util.Util;
import com.google.gson.Gson;
import com.swbyte.tv.testvideo.view.MyPlayerView;

import static com.google.android.exoplayer2.C.TYPE_HLS;
import static com.google.android.exoplayer2.C.TYPE_OTHER;

public final class MainActivity extends Activity {

    private static final String TAG = "MainActivity";
    private MyPlayerView playerView;
    private final String url = "https://blz-videos.nosdn.127.net/1/World%20of%20Warcraft/WOW_LaunchCinematic_zhCN_HRZ_PK.mp4";
    private SimpleExoPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_activity);
        initView();
        initData();
        initEvent();
    }


    private final void initView() {
        playerView = findViewById(R.id.player_view);
    }

    private final void initData() {
        Uri uri = Uri.parse(url);
        int i = Util.inferContentType(uri, null);
        DefaultDataSourceFactory defaultDataSourceFactory = new DefaultDataSourceFactory(this, "test");
        MediaSource source = null;

        switch (i) {
            case TYPE_HLS:
                // 创建资源(m3u8)
                HlsMediaSource.Factory factory = new HlsMediaSource.Factory(defaultDataSourceFactory);
                source = factory.createMediaSource(uri);
                break;
            case TYPE_OTHER:
                //创建资源(mp4)
                ProgressiveMediaSource.Factory factory1 = new ProgressiveMediaSource.Factory(defaultDataSourceFactory);
                source = factory1.createMediaSource(uri);
                break;
        }

        // 创建带宽
        AdaptiveTrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory();
        // 创建播放器实例
        DefaultRenderersFactory defaultRenderersFactory = new DefaultRenderersFactory(this);
        player = new SimpleExoPlayer.Builder(this, defaultRenderersFactory).build();
        player.addAnalyticsListener(new EventLogger(new DefaultTrackSelector(videoTrackSelectionFactory)));
        playerView.setPlayer(player);
        // 准备播放
        player.prepare(source);
        // 开始播放
        player.setPlayWhenReady(true);

    }

    private final void initEvent() {
        player.addListener(new Player.EventListener() {

            @Override
            public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {
                Log.e(TAG, "onTimelineChanged Timeline:" + new Gson().toJson(timeline) + "\tmanifest:" + manifest + "\treason:" + reason);
            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
                Log.e(TAG, "onTracksChanged trackGroups:" + trackGroups + "\ttrackSelections:" + trackSelections);
            }

            @Override
            public void onLoadingChanged(boolean isLoading) {
                Log.e(TAG, "onLoadingChanged isLoading:" + isLoading);
            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                Log.e(TAG, "onPlayerStateChanged playWhenReady:" + playWhenReady + "\tplaybackState:" + playbackState);
                switch (playbackState) {
                    case Player.STATE_IDLE:
                    case Player.STATE_BUFFERING:
                        Toast.makeText(MainActivity.this, "当前视频卡顿", Toast.LENGTH_LONG).show();
                        break;
                    case Player.STATE_READY:
                        Toast.makeText(MainActivity.this, "当前视频流畅", Toast.LENGTH_LONG).show();
                        break;
                    case Player.STATE_ENDED:
                        Toast.makeText(MainActivity.this, "当前视频播放完成", Toast.LENGTH_LONG).show();
                        break;
                }
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {
                Log.e(TAG, "onRepeatModeChanged repeatMode:" + repeatMode);
            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
                Log.e(TAG, "onShuffleModeEnabledChanged shuffleModeEnabled:" + shuffleModeEnabled);
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                Log.e(TAG, "onPlayerError error:" + new Gson().toJson(error));
            }

            @Override
            public void onPositionDiscontinuity(int reason) {
                Log.e(TAG, "onPositionDiscontinuity reason:" + reason);
            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
                Log.e(TAG, "onPlaybackParametersChanged playbackParameters:" + playbackParameters);
            }

            @Override
            public void onSeekProcessed() {
                Log.e(TAG, "onSeekProcessed");
            }
        });
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        playerView.showController();
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

}