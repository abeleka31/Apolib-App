// File: com/example/apodicty/SqlDatabase/UserManager.java

package com.example.apodicty.data.sqlitedatabase.database.user;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

// Import kelas User yang baru
// import com.example.apodicty.model.User; // Sesuaikan jika package berbeda
import com.example.apodicty.data.sqlitedatabase.database.DatabaseHelper;

public class UserManager {

    private DatabaseHelper dbHelper;
    private SQLiteDatabase database;

    public UserManager(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    /**
     * Menyimpan data user baru ke database.
     * Menggunakan String untuk parameter karena ini adalah input dari UI.
     */
    public boolean insertUser(String email, String username, String namaLengkap,
                              String tanggalLahir, String instansi, String quotes) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_EMAIL, email);
        values.put(DatabaseHelper.COL_USERNAME, username);
        values.put(DatabaseHelper.COL_NAMA_LENGKAP, namaLengkap);
        values.put(DatabaseHelper.COL_TGL_LAHIR, tanggalLahir);
        values.put(DatabaseHelper.COL_INSTANSI, instansi);
        values.put(DatabaseHelper.COL_QUOTES, quotes);

        long result = database.insert(DatabaseHelper.TABLE_USER, null, values);
        return result != -1;
    }

    /**
     * Memperbarui data user yang sudah ada.
     * Menggunakan String untuk parameter karena ini adalah input dari UI.
     */
    public int updateUser(String email, String username, String namaLengkap,
                          String tanggalLahir, String instansi, String quotes) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_USERNAME, username);
        values.put(DatabaseHelper.COL_NAMA_LENGKAP, namaLengkap);
        values.put(DatabaseHelper.COL_TGL_LAHIR, tanggalLahir);
        values.put(DatabaseHelper.COL_INSTANSI, instansi);
        values.put(DatabaseHelper.COL_QUOTES, quotes);

        return database.update(DatabaseHelper.TABLE_USER, values,
                DatabaseHelper.COL_EMAIL + " = ?", new String[]{email});
    }

    /**
     * Mengambil semua data user berdasarkan email dan mengembalikannya sebagai objek User.
     * @param email Email user yang dicari
     * @return Objek User berisi data, atau null jika user tidak ditemukan.
     */
    public User getUser(String email) { // <--- Mengubah tipe return dari Map ke User
        Cursor cursor = null;
        User user = null; // <--- Mengubah dari Map ke User
        try {
            cursor = database.query(
                    DatabaseHelper.TABLE_USER,
                    null, // Semua kolom
                    DatabaseHelper.COL_EMAIL + " = ?",
                    new String[]{email},
                    null, null, null
            );

            if (cursor != null && cursor.moveToFirst()) {
                // Membuat objek User baru dengan data dari kursor
                user = new User( // <--- Membuat objek User
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_EMAIL)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USERNAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NAMA_LENGKAP)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TGL_LAHIR)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_INSTANSI)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_QUOTES))
                );
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return user; // <--- Mengembalikan objek User
    }

    /**
     * Memeriksa apakah user dengan email tertentu sudah ada di database.
     */
    public boolean isUserExists(String email) {
        Cursor cursor = null;
        boolean exists = false;
        try {
            cursor = database.query(
                    DatabaseHelper.TABLE_USER,
                    new String[]{DatabaseHelper.COL_EMAIL}, // Hanya butuh kolom email
                    DatabaseHelper.COL_EMAIL + " = ?",
                    new String[]{email},
                    null, null, null
            );
            exists = (cursor != null && cursor.getCount() > 0);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return exists;
    }
}