package ro.pub.cs.systems.eim.colocviu1_2;

import android.content.Intent;
import android.os.Bundle;
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

public class Colocviul1_2MainActivity extends AppCompatActivity {

    private ActivityResultLauncher<Intent> computeLauncher;
    private double lastComputedSum = 0;
    private String lastComputedTerms = "";

    private static final String KEY_LAST_SUM = "last_sum";
    private static final String KEY_LAST_TERMS = "last_terms";

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
    }
}
