package ro.pub.cs.systems.eim.colocviu1_2;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class Colocviul1_2SecondaryActivity extends AppCompatActivity {

    public static final String EXTRA_ALL_TERMS = "all_terms";
    public static final String EXTRA_RESULT = "result";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String allTerms = intent.getStringExtra(EXTRA_ALL_TERMS);

        double sum = 0;
        if (allTerms != null && !allTerms.isEmpty()) {
            String[] terms = allTerms.split("\\+");
            for (String term : terms) {
                try {
                    sum += Double.parseDouble(term.trim());
                } catch (NumberFormatException e) {
                    // ignore invalid numbers
                }
            }
        }

        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_RESULT, sum);
        setResult(RESULT_OK, resultIntent);
        finish();
    }
}