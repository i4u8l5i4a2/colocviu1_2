package ro.pub.cs.systems.eim.colocviu1_2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class Colocviu1_2BroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "Colocviu1_2BR";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Broadcast received!");

        String dateTime = intent.getStringExtra("dateTime");
        double sum = intent.getDoubleExtra("sum", 0);

        String message = "Time: " + dateTime + "\nSum: " + sum;

        Log.d(TAG, message);
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}
