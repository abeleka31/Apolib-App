package com.example.apodicty.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.LinearLayout;

public class UiHelper {
    private static final String PREFS_NAME = "settings";
    private static final String KEY_DARK_MODE = "dark_mode";

    // Simpan preferensi tema
    public static void saveThemePref(Context context, boolean isDarkMode) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(KEY_DARK_MODE, isDarkMode).apply();
    }

    // Baca preferensi tema
    public static boolean loadThemePref(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getBoolean(KEY_DARK_MODE, false);
    }

    // Efek animasi iOS-style (grow-shrink)
    public static void applyiOSButtonAnimation(View view, Runnable onEndAction) {
        ScaleAnimation shrink = new ScaleAnimation(
                1f, 0.9f, 1f, 0.9f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        shrink.setDuration(80);
        shrink.setFillAfter(true);

        ScaleAnimation expand = new ScaleAnimation(
                0.9f, 1f, 0.9f, 1f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        expand.setStartOffset(80);
        expand.setDuration(80);
        expand.setFillAfter(true);

        view.startAnimation(shrink);
        view.postDelayed(() -> {
            view.startAnimation(expand);
            if (onEndAction != null) onEndAction.run();
        }, 80);
    }

    // Setup tombol back agar punya animasi & aksi kembali
    public static void setupBackButton(LinearLayout btn, Runnable onBackPressed) {
        btn.setOnClickListener(v -> UiHelper.applyiOSButtonAnimation(v, onBackPressed));
    }
    // Setup tombol dengan animasi saja (tanpa logika navigasi)
    public static void setupAnimatedClick(View view, Runnable onClickAction) {
        view.setOnClickListener(v -> UiHelper.applyiOSButtonAnimation(v, onClickAction));
    }

}
