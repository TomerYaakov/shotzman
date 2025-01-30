package com.example.shotzman;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button arrive = findViewById(R.id.arrive);
        Button leave = findViewById(R.id.leave);

        arrive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calander = Calendar.getInstance();
                Date currentTime = calander.getTime();

                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                String formattedTime = timeFormat.format(currentTime);

                arrive.setVisibility(View.GONE);
                leave.setVisibility(View.VISIBLE);

                // Code to execute when the button is clicked
                Toast.makeText(MainActivity.this, "שעת התחלה שהוגדרה: " + formattedTime, Toast.LENGTH_SHORT).show();
            }
        });
        leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calander = Calendar.getInstance();
                Date currentTime = calander.getTime();

                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                String formattedTime = timeFormat.format(currentTime);

                leave.setVisibility(View.GONE);
                arrive.setVisibility(View.VISIBLE);

                // Code to execute when the button is clicked
                Toast.makeText(MainActivity.this, "שעת סיום שהוגדרה: " + formattedTime, Toast.LENGTH_SHORT).show();
            }
        });
    }

}