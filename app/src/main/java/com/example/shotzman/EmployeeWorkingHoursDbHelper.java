package com.example.shotzman;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import java.util.ArrayList;
import java.util.List;

public class EmployeeWorkingHoursDbHelper extends SQLiteOpenHelper {
    // Database info
    private static final String DATABASE_NAME = "employee_working_hours.db";
    private static final int DATABASE_VERSION = 1;

    // Table and column names
    public static final String TABLE_EMPLOYEE_WORKING_HOURS = "employee_working_hours";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_EMPLOYEE_ID = "employee_id";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_START_TIME = "start_time";
    public static final String COLUMN_END_TIME = "end_time";

    // SQL statement to create the table
    private static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TABLE_EMPLOYEE_WORKING_HOURS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_EMPLOYEE_ID + " INTEGER NOT NULL, " +
                    COLUMN_DATE + " TEXT NOT NULL, " +
                    COLUMN_START_TIME + " TEXT, " +
                    COLUMN_END_TIME + " TEXT)";

    public EmployeeWorkingHoursDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database upgrades here if needed
    }

    // Add a new working hours entry
    public long addEmployeeWorkingHours(EmployeeWorkingHours employeeWorkingHours) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EMPLOYEE_ID, employeeWorkingHours.getEmployeeId());
        values.put(COLUMN_DATE, employeeWorkingHours.getDate());
        values.put(COLUMN_START_TIME, employeeWorkingHours.getStartTime());
        values.put(COLUMN_END_TIME, employeeWorkingHours.getEndTime());

        long newRowId = db.insert(TABLE_EMPLOYEE_WORKING_HOURS, null, values);
        db.close();
        return newRowId;
    }

    // Get all working hours entries
    public List<EmployeeWorkingHours> getAllEmployeeWorkingHours() {
        List<EmployeeWorkingHours> employeeWorkingHoursList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                COLUMN_ID,
                COLUMN_EMPLOYEE_ID,
                COLUMN_DATE,
                COLUMN_START_TIME,
                COLUMN_END_TIME
        };

        Cursor cursor = db.query(
                TABLE_EMPLOYEE_WORKING_HOURS,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
            int employeeId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_EMPLOYEE_ID));
            String date = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE));
            String startTime = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_START_TIME));
            String endTime = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_END_TIME));

            EmployeeWorkingHours employeeWorkingHours = new EmployeeWorkingHours(id, employeeId, date, startTime, endTime);
            employeeWorkingHoursList.add(employeeWorkingHours);
        }

        cursor.close();
        db.close();
        return employeeWorkingHoursList;
    }
}
