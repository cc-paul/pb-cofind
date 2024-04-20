package com.pegp.eservicio.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.pegp.eservicio.ValidID.validIDData;

import java.util.ArrayList;

public class DBHandler extends SQLiteOpenHelper {

    public DBHandler(Context context) {
        super(context, "dbGallery", null, 1);
    }

    public void createDatabase() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS tblGallery");
        db.execSQL("CREATE TABLE tblGallery (id INTEGER PRIMARY KEY AUTOINCREMENT,firebaseLocation TEXT)");
    }

    public void addImage(String imageLink) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("firebaseLocation", imageLink);
        db.insert("tblGallery", null, values);
    }

    public ArrayList<validIDData> getImages() {
        ArrayList images = new ArrayList<validIDData>();
        Cursor c = getReadableDatabase().rawQuery("Select id,firebaseLocation from tblGallery", null);
        if (c.moveToFirst()) {
            do {
                String id = c.getString(0);
                String firebaseLocation = c.getString(1);

                images.add(new validIDData(id, firebaseLocation));
            } while (c.moveToNext());
        }
        return images;
    }

    public void deleteImage(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM tblGallery WHERE id = " + id);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
