package com.example.assignment_1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.Hashtable;

public class Database {
    Database_handler DataB;

    public Database(Context context) {
        DataB = new Database_handler(context);
    }

    public String retrievedata() {
        SQLiteDatabase db = DataB.getWritableDatabase();
        String[] columns = {Database_handler.UID, Database_handler.HEART_RATE, Database_handler.RESPIRATORY_RATE, Database_handler.FEVER, Database_handler.NAUSEA, Database_handler.HEADACHE, Database_handler.DIARRHEA, Database_handler.SOAR_THROAT, Database_handler.MUSCLE_ACHE, Database_handler.LOSS_OF_SMELL_OR_TASTE, Database_handler.COUGH, Database_handler.SHORTNESS_OF_BREATH, Database_handler.FEELING_TIRED};
        Cursor cursor = db.query(Database_handler.TABLE_NAME, columns, null, null, null, null, null);
        StringBuffer buffer = new StringBuffer();
        while (cursor.moveToNext()) {
            int cid = cursor.getInt(cursor.getColumnIndex(Database_handler.UID));
            String heart_rate = cursor.getString(cursor.getColumnIndex(Database_handler.HEART_RATE));
            String resp_rate = cursor.getString(cursor.getColumnIndex(Database_handler.RESPIRATORY_RATE));
            String fever = cursor.getString(cursor.getColumnIndex(Database_handler.FEVER));
            String nausea = cursor.getString(cursor.getColumnIndex(Database_handler.NAUSEA));
            String headache = cursor.getString(cursor.getColumnIndex(Database_handler.HEADACHE));
            String diarrhea = cursor.getString(cursor.getColumnIndex(Database_handler.DIARRHEA));
            String soar_throat = cursor.getString(cursor.getColumnIndex(Database_handler.SOAR_THROAT));
            String muscle_ache = cursor.getString(cursor.getColumnIndex(Database_handler.MUSCLE_ACHE));
            String loss_smell_taste = cursor.getString(cursor.getColumnIndex(Database_handler.LOSS_OF_SMELL_OR_TASTE));
            String cough = cursor.getString(cursor.getColumnIndex(Database_handler.COUGH));
            String shortness_breath = cursor.getString(cursor.getColumnIndex(Database_handler.SHORTNESS_OF_BREATH));
            String feeling_tired = cursor.getString(cursor.getColumnIndex(Database_handler.FEELING_TIRED));
            buffer.append(cid + "   " + heart_rate + "   " + resp_rate + " \n" + "   " + fever + " \n" + "   " + nausea + " \n" + "   " + headache + " \n" + "   " + diarrhea + " \n" + "   " + soar_throat + " \n" + "   " + muscle_ache + " \n" + "   " + loss_smell_taste + " \n" + "   " + cough + " \n" + "   " + shortness_breath + " \n" + "   " + feeling_tired + " \n");
        }
        return buffer.toString();
    }



    public long insert_Sym(Hashtable symptomRating) {
        SQLiteDatabase dbb = DataB.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(Database_handler.HEART_RATE, (Float) symptomRating.get("Heart Rate"));
        contentValues.put(Database_handler.RESPIRATORY_RATE, (Float) symptomRating.get("Respiratory Rate"));
        contentValues.put(Database_handler.FEVER, (Float) symptomRating.get("Fever"));
        contentValues.put(Database_handler.NAUSEA, (Float) symptomRating.get("Nausea"));
        contentValues.put(Database_handler.HEADACHE, (Float) symptomRating.get("Headache"));
        contentValues.put(Database_handler.DIARRHEA, (Float) symptomRating.get("Diarrhea"));
        contentValues.put(Database_handler.SOAR_THROAT, (Float) symptomRating.get("Soar Throat"));
        contentValues.put(Database_handler.MUSCLE_ACHE, (Float) symptomRating.get("Muscle Ache"));
        contentValues.put(Database_handler.LOSS_OF_SMELL_OR_TASTE, (Float) symptomRating.get("Loss Of Smell Or Taste"));
        contentValues.put(Database_handler.COUGH, (Float) symptomRating.get("Cough"));
        contentValues.put(Database_handler.SHORTNESS_OF_BREATH, (Float) symptomRating.get("Shortness Of Breath"));
        contentValues.put(Database_handler.FEELING_TIRED, (Float) symptomRating.get("Feeling Tired"));
        contentValues.put(Database_handler.LATITUDE, (Float) symptomRating.get("Latitude"));
        contentValues.put(Database_handler.LONGITUDE, (Float) symptomRating.get("Longitude"));
        long id = dbb.insert(Database_handler.TABLE_NAME, String.valueOf(0), contentValues);
        return id;
    }

    static class Database_handler extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "CovidSymptoms.db";    // Database Name
        private static final String TABLE_NAME = "symptomTable";   // Table Name
        private static final int DATABASE_Version = 1;    // Database Version
        private static final String UID = "_id";     // Column I (Primary Key)
        private static final String FEVER = "Fever";    //Column II
        private static final String NAUSEA = "Nausea";
        private static final String HEADACHE = "Headache";
        private static final String DIARRHEA = "Diarrhea";
        private static final String SOAR_THROAT = "SoarThroat";
        private static final String MUSCLE_ACHE = "MuscleAche";
        private static final String LOSS_OF_SMELL_OR_TASTE = "LossofSmellOrTaste";
        private static final String COUGH = "Cough";
        private static final String SHORTNESS_OF_BREATH = "ShortnessOfBreath";
        private static final String FEELING_TIRED = "FeelingTired";
        private static final String HEART_RATE = "HeartRate";
        private static final String RESPIRATORY_RATE = "RespiratoryRate";
        private static final String LATITUDE = "Latitude";
        private static final String LONGITUDE = "Longitude";
        private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME +
                " (" + UID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + HEART_RATE + " REAL, " + RESPIRATORY_RATE + " REAL, " + FEVER + " REAL ," + NAUSEA + " REAL, " + HEADACHE + " REAL, " + DIARRHEA + " REAL," + SOAR_THROAT + " REAL, " + MUSCLE_ACHE + " REAL, " + LOSS_OF_SMELL_OR_TASTE + " REAL, " + COUGH + " REAL, " + SHORTNESS_OF_BREATH + " REAL, " + FEELING_TIRED + " REAL , " + LATITUDE + " REAL, " + LONGITUDE + " REAL );";
        private static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
        private Context context;

        public Database_handler(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_Version);
            this.context = context;
        }

        public void onCreate(SQLiteDatabase db) {

            try {
                db.execSQL(CREATE_TABLE);
            } catch (Exception e) {
                Log.d("Table","Not created");
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}