package com.example.shotzman;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private EmployeeWorkingHoursDbHelper dbHelper;
    private String formattedStartTime;
    private String formattedEndTime;

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
        Button btStartNfc = findViewById(R.id.btStartNfc);

        arrive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calander = Calendar.getInstance();
                Date currentTime = calander.getTime();

                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                formattedStartTime = timeFormat.format(currentTime);

                arrive.setVisibility(View.GONE);
                leave.setVisibility(View.VISIBLE);

                // Code to execute when the button is clicked
                Toast.makeText(MainActivity.this, "שעת התחלה שהוגדרה: " + formattedStartTime, Toast.LENGTH_SHORT).show();
            }
        });
        leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calander = Calendar.getInstance();
                Date currentTime = calander.getTime();
                Date date = calander.getTime();


                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                formattedEndTime = timeFormat.format(currentTime);

                // Create a SimpleDateFormat object to format the date
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String formattedDate = dateFormat.format(date);

                leave.setVisibility(View.GONE);
                arrive.setVisibility(View.VISIBLE);

                // Code to execute when the button is clicked
                Toast.makeText(MainActivity.this, "שעת סיום שהוגדרה: " + formattedEndTime, Toast.LENGTH_SHORT).show();


                EmployeeWorkingHours entry = new EmployeeWorkingHours(0, 1, formattedDate, formattedStartTime, formattedEndTime);

                long newRowId = dbHelper.addEmployeeWorkingHours(entry);

                // Check if the insertion was successful
                if (newRowId != -1) {
                    Log.d("MainActivity", "New entry added with ID: " + newRowId);
                } else {
                    Log.e("MainActivity", "Error adding new entry");
                }
            }

        });
        dbHelper = new EmployeeWorkingHoursDbHelper(this);

        btStartNfc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNfcActivity();
            }
        });
    }
    private void startNfcActivity() {



        // Create an intent to start NfcActivity
        Intent intent = new Intent(this, NfcActivity.class);


        // Start the NfcActivity
        startActivity(intent);
    }

}