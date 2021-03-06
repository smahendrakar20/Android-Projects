package com.example.lrmah.hospitalmanagement.Data;

import android.content.ContentProvider;



import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.widget.Toast;

import static android.R.attr.id;


public class HospitalProvider extends ContentProvider
{


    private static final int doctors = 100;


    private static final int doctors_ID = 101;


    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
//here we assing doctors and doctors_id to specific uri

        sUriMatcher.addURI(HospitalContract.CONTENT_AUTHORITY1, HospitalContract.PATH_DOCTORS, doctors);
        sUriMatcher.addURI(HospitalContract.CONTENT_AUTHORITY1, HospitalContract.PATH_DOCTORS + "/#", doctors_ID);

    }


    private doctorDbHelper helper;

    public static final String LOG_TAG = HospitalProvider.class.getSimpleName();

    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {
        helper = new doctorDbHelper(getContext());
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase database = helper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            //THIS IS TO SELECT ALL THE IDS
            case doctors:
                cursor = database.query(HospitalContract.doctorsEntry.table_name, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            //THIS IS TO SELECT PARTICULAR ID
            case doctors_ID:
                // selection, we have 1 String in the selection arguments' String array.
                selection = HospitalContract.doctorsEntry.column_id + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};//THIS FETCHES THE LAST NUMBER IN THE COMPLETE URI AND PLACES IT IN THE ABOVE "=?"

                cursor = database.query(HospitalContract.doctorsEntry.table_name, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;

            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        //NOTIFIES IF THERE IS A CHANGE IN THE TABLE OR SINGLE ROW BASED ON THE URI RECEIVED
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override

    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case doctors:
                return insertDoctor(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }}


        @Override
        public int update (Uri uri, ContentValues contentValues, String selection, String[] selectionArgs)
        {

            final int match = sUriMatcher.match(uri);
            switch (match) {
                case doctors:
                    return updatePet(uri, contentValues, selection, selectionArgs);
                case doctors_ID:
                    // For the PET_ID code, extract out the ID from the URI,
                    // so we know which row to update. Selection will be "_id=?" and selection
                    // arguments will be a String array containing the actual ID.
                    selection = HospitalContract.doctorsEntry._ID + "=?";
                    selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                    return updatePet(uri, contentValues, selection, selectionArgs);
                default:
                    throw new IllegalArgumentException("Update is not supported for " + uri);
            }



        }


        @Override
        public int delete (Uri uri, String selection, String[]selectionArgs)
        {
            // Get writeable database
            SQLiteDatabase database = helper.getWritableDatabase();

            // Track the number of rows that were deleted
            int rowsDeleted;

            final int match = sUriMatcher.match(uri);
            switch (match) {
                case doctors:
                    // Delete all rows that match the selection and selection args
                    rowsDeleted = database.delete(HospitalContract.doctorsEntry.table_name, null,null);
                    break;
                case doctors_ID:
                    // Delete a single row given by the ID in the URI
                    selection = HospitalContract.doctorsEntry._ID + "=?";
                    selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                    rowsDeleted = database.delete(HospitalContract.doctorsEntry.table_name, selection, selectionArgs);
                    break;
                default:
                    throw new IllegalArgumentException("Deletion is not supported for " + uri);
            }

            // If 1 or more rows were deleted, then notify all listeners that the data at the
            // given URI has changed
            if (rowsDeleted != 0) {
                getContext().getContentResolver().notifyChange(uri, null);
            }

            // Return the number of rows deleted
            return rowsDeleted;
        }


        @Override
        public String getType (Uri uri){
            return null;
        }

    private int updatePet(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if(values.size() == 0) {
            return 0;
        }

        // Otherwise, get writeable database to update the data
        SQLiteDatabase database = helper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(HospitalContract.doctorsEntry.table_name, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows updated
        return rowsUpdated;


    }



    private Uri insertDoctor(Uri uri, ContentValues values) {

        SQLiteDatabase db = helper.getWritableDatabase();

        //Inserting
        long x = db.insert(HospitalContract.doctorsEntry.table_name, null, values);

        //toast if inserted
        if (x == (-1)) {
            Toast.makeText(getContext(), "not added", Toast.LENGTH_LONG).show();
        } else if (x >= 1) {
            Toast.makeText(getContext(), "added", Toast.LENGTH_LONG).show();
        }

        //return uri
        return ContentUris.withAppendedId(uri, id);
    }
}
