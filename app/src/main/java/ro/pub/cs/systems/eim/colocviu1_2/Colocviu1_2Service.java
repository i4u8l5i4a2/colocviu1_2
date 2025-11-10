package ro.pub.cs.systems.eim.colocviu1_2;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import androidx.annotation.Nullable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Colocviu1_2Service extends Service {

    public static final String EXTRA_SUM = "sum";
    public static final String ACTION_BROADCAST = "ro.pub.cs.systems.eim.colocviu1_2.BROADCAST_SUM";
    private static final String TAG = "Colocviu1_2Service";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Service started");

        if (intent != null) {
            double sum = intent.getDoubleExtra(EXTRA_SUM, 0);
            Log.d(TAG, "Received sum: " + sum);

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                String dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                        .format(new Date());

                Log.d(TAG, "Sending broadcast with sum: " + sum + " at " + dateTime);

                Intent broadcastIntent = new Intent(ACTION_BROADCAST);
                broadcastIntent.putExtra("dateTime", dateTime);
                broadcastIntent.putExtra("sum", sum);
                sendBroadcast(broadcastIntent);

                Log.d(TAG, "Broadcast sent, stopping service");
                stopSelf();
            }, 2000);
        }
        return START_NOT_STICKY;
    }
}
