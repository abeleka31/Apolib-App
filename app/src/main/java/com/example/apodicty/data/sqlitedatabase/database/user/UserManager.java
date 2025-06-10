// File: com/example/apodicty/SqlDatabase/UserManager.java

package com.example.apodicty.data.sqlitedatabase.database.user;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException; // Tambahkan import ini
import android.database.sqlite.SQLiteDatabase;
import android.util.Log; // Tambahkan import ini untuk logging

// Import kelas User
import com.example.apodicty.data.sqlitedatabase.database.DatabaseHelper;
import com.example.apodicty.data.sqlitedatabase.database.user.User; // Pastikan ini mengarah ke kelas User Anda

public class UserManager {

    private DatabaseHelper dbHelper;
    private SQLiteDatabase database;
    private static final String TAG = "UserManager"; // Untuk logging

    public UserManager(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    // --- PERBAIKAN: Pastikan database dibuka dengan benar dan kelola statusnya ---
    public void open() throws SQLException {
        try {
            if (database == null || !database.isOpen()) {
                database = dbHelper.getWritableDatabase();
                Log.d(TAG, "Database opened successfully.");
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error opening database: " + e.getMessage());
            throw e; // Lemparkan kembali exception agar ditangani pemanggil
        }
    }

    // --- PERBAIKAN: Tutup database secara eksplisit dan juga dbHelper ---
    public void close() {
        if (database != null && database.isOpen()) {
            database.close();
            Log.d(TAG, "Database closed successfully.");
        }
        // Jangan menutup dbHelper di sini karena itu akan menutup database juga.
        // dbHelper.close() sebaiknya hanya dipanggil saat aplikasi benar-benar akan keluar
        // atau Anda yakin tidak ada lagi operasi database.
        // Untuk fragment onPause/onResume, hanya kelola database.close()/open()
        // dbHelper.close(); // Hapus baris ini dari sini
    }

    // --- METODE YANG HILANG (Penyebab Error) ---
    public boolean isDbOpen() {
        return database != null && database.isOpen();
    }
    // ------------------------------------------

    /**
     * Menyimpan data user baru ke database.
     * Menggunakan String untuk parameter karena ini adalah input dari UI.
     * @return true jika berhasil, false jika gagal atau database tidak terbuka.
     */
    public boolean insertUser(String email, String username, String namaLengkap,
                              String tanggalLahir, String instansi, String quotes) {
        if (!isDbOpen()) {
            Log.e(TAG, "Database is not open. Cannot insert user: " + email);
            return false;
        }

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_EMAIL, email);
        values.put(DatabaseHelper.COL_USERNAME, username);
        values.put(DatabaseHelper.COL_NAMA_LENGKAP, namaLengkap);
        values.put(DatabaseHelper.COL_TGL_LAHIR, tanggalLahir);
        values.put(DatabaseHelper.COL_INSTANSI, instansi);
        values.put(DatabaseHelper.COL_QUOTES, quotes);

        long result = database.insert(DatabaseHelper.TABLE_USER, null, values);
        if (result == -1) {
            Log.e(TAG, "Failed to insert user: " + email);
        } else {
            Log.d(TAG, "User inserted: " + email + " (ID: " + result + ")");
        }
        return result != -1;
    }

    /**
     * Memperbarui data user yang sudah ada.
     * Menggunakan String untuk parameter karena ini adalah input dari UI.
     * @return Jumlah baris yang terpengaruh.
     */
    public int updateUser(String email, String username, String namaLengkap,
                          String tanggalLahir, String instansi, String quotes) {
        if (!isDbOpen()) {
            Log.e(TAG, "Database is not open. Cannot update user: " + email);
            return 0;
        }

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COL_USERNAME, username);
        values.put(DatabaseHelper.COL_NAMA_LENGKAP, namaLengkap);
        values.put(DatabaseHelper.COL_TGL_LAHIR, tanggalLahir);
        values.put(DatabaseHelper.COL_INSTANSI, instansi);
        values.put(DatabaseHelper.COL_QUOTES, quotes);

        int rowsAffected = database.update(DatabaseHelper.TABLE_USER, values,
                DatabaseHelper.COL_EMAIL + " = ?", new String[]{email});
        if (rowsAffected == 0) {
            Log.w(TAG, "No user found or updated for email: " + email);
        } else {
            Log.d(TAG, "User updated: " + email + ", rows affected: " + rowsAffected);
        }
        return rowsAffected;
    }

    /**
     * Mengambil semua data user berdasarkan email dan mengembalikannya sebagai objek User.
     * @param email Email user yang dicari
     * @return Objek User berisi data, atau null jika user tidak ditemukan atau database tidak terbuka.
     */
    public User getUser(String email) {
        if (!isDbOpen()) {
            Log.e(TAG, "Database is not open. Cannot get user: " + email);
            return null;
        }
        Cursor cursor = null;
        User user = null;
        try {
            cursor = database.query(
                    DatabaseHelper.TABLE_USER,
                    null, // Semua kolom
                    DatabaseHelper.COL_EMAIL + " = ?",
                    new String[]{email},
                    null, null, null
            );

            if (cursor != null && cursor.moveToFirst()) {
                user = new User(
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_EMAIL)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_USERNAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_NAMA_LENGKAP)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_TGL_LAHIR)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_INSTANSI)),
                        cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_QUOTES))
                );
                Log.d(TAG, "User found: " + email);
            } else {
                Log.d(TAG, "User not found for email: " + email);
            }
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Error getting column index for user data: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return user;
    }

    /**
     * Memeriksa apakah user dengan email tertentu sudah ada di database.
     * @return true jika user ada, false jika tidak ada atau database tidak terbuka.
     */
    public boolean isUserExists(String email) {
        if (!isDbOpen()) {
            Log.e(TAG, "Database is not open. Cannot check user existence: " + email);
            return false;
        }
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
            Log.d(TAG, "User existence check for " + email + ": " + exists);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return exists;
    }

    // Metode lain yang mungkin Anda miliki, misalnya deleteUser jika diperlukan
    // public int deleteUser(String email) { ... }
}