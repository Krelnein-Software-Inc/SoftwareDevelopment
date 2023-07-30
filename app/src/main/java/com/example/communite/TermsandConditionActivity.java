package com.example.communite;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class TermsandConditionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_termsand_condition);

        WebView webView = findViewById(R.id.webView);

        // Load the HTML content from the string resource
        String htmlContent = getString(R.string.terms_and_conditions_html);

        // Load the HTML content into the WebView
        webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null);
    }

    @Override
    public void onBackPressed() {
        // Handle the back button press here
        // In this case, redirect to RegisterActivity
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
        finish(); // Optional, depending on whether you want to keep TermsandConditionActivity in the back stack or not
    }
}
