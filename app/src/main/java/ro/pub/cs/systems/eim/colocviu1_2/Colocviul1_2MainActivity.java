package ro.pub.cs.systems.eim.colocviu1_2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Context;
import android.content.IntentFilter;


public class Colocviul1_2MainActivity extends AppCompatActivity {

    private ActivityResultLauncher<Intent> computeLauncher;
    private double lastComputedSum = 0;
    private String lastComputedTerms = "";
    private boolean serviceStarted = false;

    private static final String KEY_LAST_SUM = "last_sum";
    private static final String KEY_LAST_TERMS = "last_terms";
    private static final String KEY_SERVICE_STARTED = "service_started";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_practical_test01_2_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (savedInstanceState != null) {
            lastComputedSum = savedInstanceState.getDouble(KEY_LAST_SUM, 0);
            lastComputedTerms = savedInstanceState.getString(KEY_LAST_TERMS, "");
//            serviceStarted = savedInstanceState.getBoolean(KEY_SERVICE_STARTED, false);
        }

        EditText nextTerm = findViewById(R.id.nextTrem);
        TextView allTerms = findViewById(R.id.allTerms);
        Button addButton = findViewById(R.id.add);
        Button computeButton = findViewById(R.id.compute);

        computeLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        lastComputedSum = result.getData().getDoubleExtra(
                                Colocviul1_2SecondaryActivity.EXTRA_RESULT, 0);
                        lastComputedTerms = allTerms.getText().toString();
                        Toast.makeText(this, "Sum: " + lastComputedSum, Toast.LENGTH_LONG).show();

                        Log.d("MainActivity", "Sum: " + lastComputedSum + ", serviceStarted: " + serviceStarted);


                        if (lastComputedSum > 10 && !serviceStarted) {
                            Log.d("MainActivity", "Starting service...");
                            Intent serviceIntent = new Intent(this, Colocviu1_2Service.class);
                            serviceIntent.putExtra(Colocviu1_2Service.EXTRA_SUM, lastComputedSum);
                            startService(serviceIntent);
                            serviceStarted = true;
                        } else {
                            Log.d("MainActivity", "Service NOT started. Sum: " + lastComputedSum + ", serviceStarted: " + serviceStarted);
                        }
                    }
                });


        addButton.setOnClickListener((View v) -> {
            String input = nextTerm.getText().toString().trim();
            if (input.isEmpty()) {
                return;
            }
            try {
                Double.parseDouble(input);
            } catch (NumberFormatException e) {
                return;
            }
            String current = allTerms.getText().toString();
            if (current.isEmpty()) {
                allTerms.setText(input);
            } else {
                allTerms.setText(current + "+" + input);
            }
            nextTerm.setText("");
        });

        computeButton.setOnClickListener((View v) -> {
            String terms = allTerms.getText().toString();
            if (terms.isEmpty()) {
                Toast.makeText(this, "No terms to compute", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!terms.equals(lastComputedTerms)) {
                serviceStarted = false;
                Log.d("MainActivity", "Terms changed, reset serviceStarted to false");
            }

            if (terms.equals(lastComputedTerms)) {
                Toast.makeText(this, "Sum: " + lastComputedSum, Toast.LENGTH_LONG).show();
                return;
            }

            Intent intent = new Intent(this, Colocviul1_2SecondaryActivity.class);
            intent.putExtra(Colocviul1_2SecondaryActivity.EXTRA_ALL_TERMS, terms);
            computeLauncher.launch(intent);
        });

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putDouble(KEY_LAST_SUM, lastComputedSum);
        outState.putString(KEY_LAST_TERMS, lastComputedTerms);
//        outState.putBoolean(KEY_SERVICE_STARTED, serviceStarted);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serviceStarted) {
            Intent serviceIntent = new Intent(this, Colocviu1_2Service.class);
            stopService(serviceIntent);
        }
    }

    private Colocviu1_2BroadcastReceiver broadcastReceiver;

    @Override
    protected void onResume() {
        super.onResume();
        broadcastReceiver = new Colocviu1_2BroadcastReceiver();
        IntentFilter filter = new IntentFilter(Colocviu1_2Service.ACTION_BROADCAST);
        registerReceiver(broadcastReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        Log.d("MainActivity", "Receiver registered");
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }
    }

}
