// File: com/example/apodicty/MyApplication.java
package com.example.apodicty.utils; // <<< PASTIKAN INI BENAR

import android.app.Application;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MyApplication extends Application {

    private ExecutorService applicationExecutorService;

    @Override
    public void onCreate() {
        super.onCreate();
        // Inisialisasi ExecutorService global
        applicationExecutorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }

    public ExecutorService getApplicationExecutorService() {
        return applicationExecutorService;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        // Pastikan ExecutorService dimatikan saat aplikasi berakhir
        if (applicationExecutorService != null && !applicationExecutorService.isShutdown()) {
            applicationExecutorService.shutdown();
            // Opsional: Tunggu sebentar agar semua tugas selesai sebelum shutdownNow
            try {
                if (!applicationExecutorService.awaitTermination(60, TimeUnit.SECONDS)) {
                    applicationExecutorService.shutdownNow(); // Batalkan tugas yang sedang berjalan
                }
            } catch (InterruptedException e) {
                applicationExecutorService.shutdownNow();
                Thread.currentThread().interrupt(); // Pertahankan status interrupt
            }
        }
    }
}