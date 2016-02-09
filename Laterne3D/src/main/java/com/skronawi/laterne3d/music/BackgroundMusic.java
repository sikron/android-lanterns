package com.skronawi.laterne3d.music;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import com.skronawi.laterne3d.Lantern3DActivity;
import com.skronawi.laterne3d.R;

import java.lang.ref.WeakReference;

public class BackgroundMusic extends AsyncTask<Void, Void, Void> {

    private WeakReference<Lantern3DActivity> lantern;
    private MediaPlayer player;

    private static int[] songIds = new int[]{
            R.raw.ich_geh_mit_meiner_laterne,
            R.raw.martinslied,
            R.raw.sankt_martin,
            R.raw.laterne_laterne};
    private int currentSong;


    class CompletionListener implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mp) {

            if (player != null) {
                player.stop();
                player.release();
                player = null;
            }

            Lantern3DActivity l = lantern.get();

            if (l != null) {
                currentSong = nextSong();
                player = MediaPlayer.create(lantern.get(), songIds[currentSong]);
                float volume = getVolume(l);
                player.setVolume(volume, volume);
                player.setOnCompletionListener(new CompletionListener());
                player.start();
            }
        }

        private int nextSong() {
            return (currentSong + 1) % songIds.length;
        }
    }

    public BackgroundMusic(Lantern3DActivity lantern) {
        this.lantern = new WeakReference<Lantern3DActivity>(lantern);
        currentSong = 0;
    }

    @Override
    protected Void doInBackground(Void... params) {

        Lantern3DActivity l = lantern.get();

        if (l != null) {
            player = MediaPlayer.create(lantern.get(), songIds[currentSong]);
            float volume = getVolume(l);
            player.setVolume(volume, volume);
            player.setOnCompletionListener(new CompletionListener());
            player.start();
        }
        return null;
    }

    private float getVolume(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Lantern3DActivity.AUDIO_SERVICE);
        float actualVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        float maxVolume = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float volume = 0;
        if (maxVolume != 0) {
            volume = actualVolume / maxVolume;
        }
        return volume;
    }

    @Override
    public void onCancelled() {
        super.onCancelled();
        if (player != null) {
            player.stop();
            player.release();
            player = null;
        }
        lantern.clear();
    }
}