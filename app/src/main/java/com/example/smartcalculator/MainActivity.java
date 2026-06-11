package com.example.smartcalculator;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class MainActivity extends AppCompatActivity {

    private EditText etNumber1, etNumber2;
    private TextView tvResult, tvHistory;
    private Switch switchDarkMode;
    private StringBuilder historyLog = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences("CalcPrefs", MODE_PRIVATE);
        boolean isDark = prefs.getBoolean("dark_mode", false);
        AppCompatDelegate.setDefaultNightMode(
                isDark ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );

        setContentView(R.layout.activity_main);

        etNumber1      = findViewById(R.id.etNumber1);
        etNumber2      = findViewById(R.id.etNumber2);
        tvResult       = findViewById(R.id.tvResult);
        tvHistory      = findViewById(R.id.tvHistory);
        switchDarkMode = findViewById(R.id.switchDarkMode);

        Button btnAdd      = findViewById(R.id.btnAdd);
        Button btnSubtract = findViewById(R.id.btnSubtract);
        Button btnMultiply = findViewById(R.id.btnMultiply);
        Button btnDivide   = findViewById(R.id.btnDivide);
        Button btnSqrt     = findViewById(R.id.btnSqrt);
        Button btnSquare   = findViewById(R.id.btnSquare);
        Button btnModulo   = findViewById(R.id.btnModulo);
        Button btnClear    = findViewById(R.id.btnClear);

        switchDarkMode.setChecked(isDark);

        btnAdd.setOnClickListener(v -> calculate("+"));
        btnSubtract.setOnClickListener(v -> calculate("-"));
        btnMultiply.setOnClickListener(v -> calculate("*"));
        btnDivide.setOnClickListener(v -> calculate("/"));
        btnSqrt.setOnClickListener(v -> calculateScientific("sqrt"));
        btnSquare.setOnClickListener(v -> calculateScientific("square"));
        btnModulo.setOnClickListener(v -> calculate("%"));

        btnClear.setOnClickListener(v -> {
            etNumber1.setText("");
            etNumber2.setText("");
            tvResult.setText(getString(R.string.result_placeholder));
            historyLog.setLength(0);
            tvHistory.setText(getString(R.string.history_placeholder));
        });

        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor =
                    getSharedPreferences("CalcPrefs", MODE_PRIVATE).edit();
            editor.putBoolean("dark_mode", isChecked);
            editor.apply();
            AppCompatDelegate.setDefaultNightMode(
                    isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
            );
        });
    }

    private void calculate(String operator) {
        String input1 = etNumber1.getText().toString().trim();
        String input2 = etNumber2.getText().toString().trim();

        if (input1.isEmpty() || input2.isEmpty()) {
            tvResult.setText(getString(R.string.error_empty_fields));
            return;
        }

        double num1, num2;
        try {
            num1 = Double.parseDouble(input1);
            num2 = Double.parseDouble(input2);
        } catch (NumberFormatException e) {
            tvResult.setText(getString(R.string.error_invalid_number));
            return;
        }

        double result;
        String symbol;

        switch (operator) {
            case "+": result = num1 + num2;  symbol = "+"; break;
            case "-": result = num1 - num2;  symbol = "−"; break;
            case "*": result = num1 * num2;  symbol = "×"; break;
            case "/":
                if (num2 == 0) {
                    tvResult.setText(getString(R.string.error_divide_zero));
                    return;
                }
                result = num1 / num2;
                symbol = "÷";
                break;
            case "%":
                if (num2 == 0) {
                    tvResult.setText(getString(R.string.error_divide_zero));
                    return;
                }
                result = num1 % num2;
                symbol = "%";
                break;
            default: return;
        }
        String formatted = formatResult(result);
        tvResult.setText(formatted);
        appendHistory(num1 + " " + symbol + " " + num2 + " = " + formatted);

// Automatically put result into first number field for chaining
        etNumber1.setText(formatted);
        etNumber2.setText("");
    }

    private void calculateScientific(String operation) {
        String input1 = etNumber1.getText().toString().trim();

        if (input1.isEmpty()) {
            tvResult.setText(getString(R.string.error_empty_fields));
            return;
        }

        double num1;
        try {
            num1 = Double.parseDouble(input1);
        } catch (NumberFormatException e) {
            tvResult.setText(getString(R.string.error_invalid_number));
            return;
        }

        double result;
        String entry;

        switch (operation) {
            case "sqrt":
                if (num1 < 0) {
                    tvResult.setText(getString(R.string.error_negative_sqrt));
                    return;
                }
                result = Math.sqrt(num1);
                entry = "√(" + num1 + ") = " + formatResult(result);
                break;
            case "square":
                result = num1 * num1;
                entry = "(" + num1 + ")² = " + formatResult(result);
                break;
            default: return;
        }

        tvResult.setText(formatResult(result));
        appendHistory(entry);
    }

    private String formatResult(double value) {
        if (value == Math.floor(value) && !Double.isInfinite(value)) {
            return String.valueOf((long) value);
        }
        return String.format("%.6f", value)
                .replaceAll("0*$", "")
                .replaceAll("\\.$", "");
    }

    private void appendHistory(String entry) {
        if (historyLog.length() == 0) {
            historyLog.append(entry);
        } else {
            historyLog.insert(0, entry + "\n");
        }
        tvHistory.setText(historyLog.toString());
    }
}