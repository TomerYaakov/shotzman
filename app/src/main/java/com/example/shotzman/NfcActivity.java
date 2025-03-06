package com.example.shotzman;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class NfcActivity extends AppCompatActivity {

    private EmployeeWorkingHoursDbHelper dbHelper;
    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private IntentFilter[] intentFiltersArray;
    private boolean isTimerRunning = false;
    private String startTime;
    private int employeeId; // Get the employee ID from the intent

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfc);

        dbHelper = new EmployeeWorkingHoursDbHelper(this);

        // Get the employee ID from the intent
        employeeId = getIntent().getIntExtra("employeeId", -1); // -1 is a default value if not found
        if (employeeId == -2) {
            Toast.makeText(this, "Employee ID not provided", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity if employee ID is missing
            return;
        }

        // NFC setup
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (nfcAdapter == null) {
            Toast.makeText(this, "NFC is not available", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        pendingIntent = PendingIntent.getActivity(
                this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), PendingIntent.FLAG_MUTABLE);

        // Change the intent filter to ACTION_NDEF_DISCOVERED
        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndef.addDataType("text/plain");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("fail", e);
        }
        intentFiltersArray = new IntentFilter[]{ndef};
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (nfcAdapter != null) {
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, null);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // Check for ACTION_NDEF_DISCOVERED
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String tagText = readTag(tag);
            if (tagText != null && tagText.equals("MY BUSINESS")) {
                processTag();
            } else {
                Toast.makeText(this, "Invalid Tag", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String readTag(Tag tag) {
        Ndef ndef = Ndef.get(tag);
        if (ndef == null) {
            return null;
        }

        NdefMessage ndefMessage = ndef.getCachedNdefMessage();
        if (ndefMessage == null) {
            return null;
        }

        NdefRecord[] records = ndefMessage.getRecords();
        if (records == null || records.length == 0) {
            return null;
        }

        NdefRecord ndefRecord = records[0];
        if (ndefRecord.getTnf() != NdefRecord.TNF_WELL_KNOWN || !Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
            return null;
        }

        try {
            byte[] payload = ndefRecord.getPayload();
            String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
            int languageCodeLength = payload[0] & 0063;
            return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
        } catch (UnsupportedEncodingException e) {
            Log.e("NfcActivity", "Unsupported Encoding", e);
            return null;
        }
    }

    private void processTag() {
        // Get the current date
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String date = dateFormat.format(currentDate);

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String currentTime = timeFormat.format(currentDate);

        if (!isTimerRunning) {
            // Start the timer
            startTime = currentTime;
            isTimerRunning = true;
            Toast.makeText(this, "Timer started at: " + startTime, Toast.LENGTH_SHORT).show();
            Log.d("NfcActivity", "Timer started at: " + startTime);
        } else {
            // Stop the timer
            String endTime = currentTime;
            isTimerRunning = false;
            Toast.makeText(this, "Timer stopped at: " + endTime, Toast.LENGTH_SHORT).show();
            Log.d("NfcActivity", "Timer stopped at: " + endTime);

            // Create and insert the entry into the database
            EmployeeWorkingHours entry = new EmployeeWorkingHours(0, employeeId, date, startTime, endTime);
            long newRowId = dbHelper.addEmployeeWorkingHours(entry);

            if (newRowId != -1) {
                Toast.makeText(this, "Entry added with ID: " + newRowId, Toast.LENGTH_SHORT).show();
                Log.d("NfcActivity", "Entry added with ID: " + newRowId);
            } else {
                Toast.makeText(this, "Error adding entry", Toast.LENGTH_SHORT).show();
                Log.e("NfcActivity", "Error adding entry");
            }
        }
    }
}