package com.skronawi.laterne;

import android.os.AsyncTask;

import java.lang.ref.WeakReference;

class RefreshTask extends AsyncTask<Void, Void, Void> {

    private WeakReference<Lantern> lantern;
    boolean isGlowing;
    boolean running;

    public RefreshTask(Lantern lantern) {
        this.lantern = new WeakReference<Lantern>(lantern);
        running = true;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
        Lantern l = lantern.get();
        if (l != null) {
            if (isGlowing) {
                l.switchGlow(false);
            } else {
                l.switchGlow(true);
            }
            isGlowing = !isGlowing;
        }
    }

    @Override
    protected Void doInBackground(Void... params) {
        while (running) {
            try {
                Thread.sleep(2000);
                publishProgress();
            } catch (InterruptedException e) {
                e.printStackTrace();

            }
        }
        return null;
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        lantern.clear();
        running = false;
    }
}
