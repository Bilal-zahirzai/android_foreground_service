package com.example.task1;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class DownloadService extends Service {

    public static final String ACTION_START_DOWNLOAD = "com.example.task1.START_DOWNLOAD";

    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "Download_Channel";

    private NotificationCompat.Builder notificationBuilder;
    private NotificationManager notificationManager;

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        createNotification();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && ACTION_START_DOWNLOAD.equals(intent.getAction())) {
            String downloadUrl = intent.getStringExtra("download_url");
            startDownload(downloadUrl);
        }
        return START_STICKY;
    }

    private void startDownload(String downloadUrl) {
        new DownloadTask().execute(downloadUrl);
    }

    private void updateNotification(int progress) {
        notificationBuilder.setProgress(100, progress, false);
        notificationBuilder.setContentText("Downloading: " + progress + "%");
        Notification notification = notificationBuilder.build();
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    private void showDownloadCompleteNotification() {
        NotificationCompat.Builder completeNotificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Download Complete")
                .setContentText("Download has been completed successfully.")
                .setSmallIcon(R.drawable.ic_baseline_done_24)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        Notification completeNotification = completeNotificationBuilder.build();
        notificationManager.notify(NOTIFICATION_ID + 1, completeNotification);
    }

    private void createNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Download Channel",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Download Service")
                .setContentText("Downloading...")
                .setSmallIcon(R.drawable.ic_notification_placeholder)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        Notification notification = notificationBuilder.build();
        startForeground(NOTIFICATION_ID, notification);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class DownloadTask extends AsyncTask<String, Integer, Void> {

        @Override
        protected Void doInBackground(String... params) {
            String downloadUrl = params[0];
            // Simulate download progress for demonstration purposes
            for (int progress = 0; progress <= 100; progress += 1) {
                publishProgress(progress);
                try {
                    Thread.sleep(1000); // Simulate download delay
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            updateNotification(progress[0]);
        }

        @Override
        protected void onPostExecute(Void result) {
            showDownloadCompleteNotification();
            stopForeground(true);
            stopSelf();
        }
    }
}
