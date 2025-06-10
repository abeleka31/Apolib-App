package com.example.apodicty.utils; // Atau di package yang lebih umum, misal com.example.apodicty.helper

import android.text.TextUtils;
import android.util.Log;
import java.util.List;

/**
 * Kelas utilitas untuk metode-metode helper terkait pemrosesan data obat.
 * Ini termasuk metode untuk menangani string null/kosong dan list string.
 */
public class DrugUtils {

    private static final String TAG = "DrugUtils"; // Untuk logging

    /**
     * Mengembalikan teks default "Tidak diketahui" jika string yang diberikan null atau kosong,
     * jika tidak, mengembalikan string itu sendiri.
     * @param text String yang akan diperiksa.
     * @return String yang sudah diproses.
     */
    public static String defaultText(String text) {
        return text != null && !text.isEmpty() ? text : "Tidak diketahui";
    }

    /**
     * Mengonversi List<String> menjadi satu string yang digabungkan dengan dua baris baru ("\n\n").
     * Mengembalikan "Tidak diketahui" jika list null atau kosong.
     * @param list List<String> yang akan digabungkan.
     * @return String yang sudah digabungkan.
     */
    public static String listToString(List<String> list) {
        return list != null && !list.isEmpty() ? TextUtils.join("\n\n", list) : "Tidak diketahui";
    }

    /**
     * Metode safe getter generik yang menangani NullPointerException saat mengakses properti berantai.
     * Berguna untuk menghindari crash saat mengakses objek yang mungkin null.
     * @param supplier Sebuah Supplier functional interface yang menyediakan nilai.
     * @param <T> Tipe data yang diharapkan.
     * @return Nilai yang diambil, atau null jika terjadi exception.
     */
    public static <T> T safeGet(Supplier<T> supplier) {
        try {
            return supplier.get();
        } catch (Exception e) {
            // Log error jika terjadi exception, tapi jangan sampai crash
            Log.e(TAG, "SafeGet Exception: " + e.getMessage());
            return null;
        }
    }

    /**
     * Functional interface sederhana untuk Supplier.
     * Mirip dengan java.util.function.Supplier di Java 8+.
     */
    public interface Supplier<T> {
        T get();
    }
}