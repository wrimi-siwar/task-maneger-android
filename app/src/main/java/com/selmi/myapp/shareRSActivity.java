package com.selmi.myapp;

import androidx.appcompat.app.AppCompatActivity;



import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class shareRSActivity extends AppCompatActivity {
    private Button mShareButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_rsactivity);
        mShareButton = findViewById(R.id.share_button);

        mShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shareContent();
            }
        });
    }

    private void shareContent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Titre du contenu à partager");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Contenu à partager");

        startActivity(Intent.createChooser(shareIntent, "Partager via"));
    }
}
