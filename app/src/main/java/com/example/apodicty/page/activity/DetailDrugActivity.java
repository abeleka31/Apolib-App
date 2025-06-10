package com.example.apodicty.page.activity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.apodicty.MainActivity;
import com.example.apodicty.utils.MyApplication;
import com.example.apodicty.R;
import com.example.apodicty.data.networkApi.components.DrugRepository;
import com.example.apodicty.data.networkApi.respon.DrugResponse;
import com.example.apodicty.data.sqlitedatabase.database.favorite.Favorite;
import com.example.apodicty.data.sqlitedatabase.database.favorite.FavoriteManager;
import com.example.apodicty.databinding.ActivityDetailDrugBinding;
import com.example.apodicty.utils.DrugDetailHelper;
import com.example.apodicty.utils.ProgressBarListener; // Pastikan ini diimpor jika Anda menggunakan ProgressBarListener
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors; // Untuk fallback ExecutorService, jika MyApplication belum diatur
import java.util.concurrent.TimeUnit; // Untuk shutdown di onDestroy

public class DetailDrugActivity extends AppCompatActivity {
    private ActivityDetailDrugBinding binding;
    private DrugDetailHelper drugDetailHelper;
    private String drugIdIntent;
    private FirebaseAuth mAuth;

    private FavoriteManager favoriteManager;
    private String currentUserEmail;

    private DrugResponse.Drug currentDisplayedDrug;
    private Favorite currentFavoriteDrug;

    // ProgressBarListener ini akan null jika DetailDrugActivity tidak diluncurkan dari MainActivity
    // dan tidak ada mekanisme untuk meneruskan ProgressBarListener dari MainActivity.
    // Jika Anda ingin indikator loading, pertimbangkan ProgressBar lokal.
    private ProgressBarListener progressBarListener;
    private ExecutorService executorService;

    private static final String TAG = "DetailDrugActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailDrugBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        drugDetailHelper = new DrugDetailHelper(binding);
        mAuth = FirebaseAuth.getInstance();
        favoriteManager = new FavoriteManager(this);

        // Ambil ExecutorService dari kelas Application kustom Anda
        // Jika Anda belum membuat MyApplication, ini bisa jadi null.
        // Untuk saat ini, kita akan tambahkan fallback jika MyApplication tidak diinisialisasi
        if (getApplication() instanceof MyApplication) {
            executorService = ((MyApplication) getApplication()).getApplicationExecutorService();
        } else {
            Log.e(TAG, "MyApplication not set up or not inheriting from MyApplication. Using local single thread executor.");
            // Fallback: Inisialisasi ExecutorService lokal jika MyApplication tidak tersedia.
            // Ini akan membuat aplikasi berfungsi, tapi mungkin tidak optimal jika ada banyak thread lain.
            executorService = Executors.newSingleThreadExecutor();
        }

        // Inisialisasi ProgressBarListener HANYA JIKA Anda meluncurkan DetailDrugActivity dari MainActivity
        // DAN ada mekanisme yang tepat untuk meneruskan ProgressBarListener (misalnya, static setter di MainActivity)
        // Jika tidak, progressBarListener akan tetap null dan Anda harus mengelola ProgressBar lokal di Activity ini.
        // Untuk menjaga kode tetap berfungsi, saya akan biarkan ini opsional (bisa null).
        // Anda perlu menambahkan ProgressBar di layout ActivityDetailDrugBinding.xml jika ingin indikator loading.
        // Contoh ProgressBar lokal di layout:
        // <ProgressBar android:id="@+id/detail_progress_bar" android:layout_width="wrap_content" android:layout_height="wrap_content" android:layout_centerInParent="true" android:visibility="gone" />
        // Lalu inisialisasi di initViews jika ada.

        getIntentData();
        displayCurrentUser();
        loadDrugDetails(); // Ini akan memicu pemuatan data dan mungkin menampilkan ProgressBar
        binding.ivSaveFavorite.setOnClickListener(v -> saveDrugToFavorites());
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Buka database saat Activity resume
        if (favoriteManager != null && !favoriteManager.isDbOpen()) { // Tambahkan isDbOpen() check
            try {
                favoriteManager.open();
                Log.d(TAG, "FavoriteManager opened in onResume.");
            } catch (Exception e) {
                Log.e(TAG, "Failed to open FavoriteManager in onResume: " + e.getMessage(), e);
                Toast.makeText(this, "Gagal membuka database favorit.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Tutup database saat Activity pause
        if (favoriteManager != null && favoriteManager.isDbOpen()) { // Tambahkan isDbOpen() check
            favoriteManager.close();
            Log.d(TAG, "FavoriteManager closed in onPause.");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Jika ExecutorService adalah instance lokal (bukan dari Application),
        // maka perlu di-shutdown di sini.
        // Karena kita mengambil dari MyApplication, dia yang akan mengelola shutdown.
        // Jika Anda menggunakan fallback ExecutorService (misal Executors.newSingleThreadExecutor()),
        // maka Anda perlu me-shutdown-nya di sini:
        // if (executorService != null && !executorService.isShutdown()) {
        //     executorService.shutdown();
        //     try {
        //         if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
        //             executorService.shutdownNow();
        //         }
        //     } catch (InterruptedException e) {
        //         executorService.shutdownNow();
        //         Thread.currentThread().interrupt();
        //     }
        // }
    }

    private void getIntentData() {
        drugIdIntent = getIntent().getStringExtra("id");
        Log.d(TAG, "Received ID: " + drugIdIntent);
    }

    private void displayCurrentUser() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String currentUserName;
        String email;

        if (currentUser != null) {
            if (currentUser.isAnonymous()) {
                currentUserName = "Pembaca Saat Ini: Guest";
                email = "guest@example.com";
            } else {
                email = currentUser.getEmail();
                if (currentUser.getDisplayName() != null && !currentUser.getDisplayName().isEmpty()) {
                    currentUserName = "Pembaca Saat Ini: " + currentUser.getDisplayName();
                } else if (email != null && !email.isEmpty()) {
                    currentUserName = "Pembaca Saat Ini: " + email.substring(0, email.indexOf('@'));
                } else {
                    currentUserName = "Pembaca Saat Ini: Pengguna Tidak Dikenal";
                    email = "unknown@example.com";
                }
            }
        } else {
            currentUserName = "Pembaca Saat Ini: Tidak Login";
            email = "-";
        }

        binding.tvCurrentUser.setText(currentUserName);
        binding.tvCurrentUserEmail.setText("Email: " + email);
        this.currentUserEmail = drugDetailHelper.ensureTextValueForDb(email);
        saveUserEmailToSharedPreferences(this.currentUserEmail);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }

    private void showLoadingIndicator() {
        // Ini adalah tempat Anda menampilkan ProgressBar lokal Anda.
        // Asumsi ada ProgressBar dengan ID binding.localProgressBar di layout Anda.
        // Contoh:
        // if (binding.localProgressBar != null) {
        //     binding.localProgressBar.setVisibility(View.VISIBLE);
        // }
        // Atau, jika Anda menggunakan ProgressBarListener yang global dan di-set dari MainActivity:
        if (progressBarListener != null) {
            runOnUiThread(() -> progressBarListener.showProgressBar());
        } else {
            Log.d(TAG, "No loading indicator mechanism available.");
        }
    }

    private void hideLoadingIndicator() {
        // Ini adalah tempat Anda menyembunyikan ProgressBar lokal Anda.
        // Contoh:
        // if (binding.localProgressBar != null) {
        //     binding.localProgressBar.setVisibility(View.GONE);
        // }
        // Atau, jika Anda menggunakan ProgressBarListener yang global:
        if (progressBarListener != null) {
            runOnUiThread(() -> progressBarListener.hideProgressBar());
        }
    }


    private void loadDrugDetails() {
        if (drugIdIntent == null || drugIdIntent.isEmpty()) {
            showError("ID Obat tidak ditemukan di intent");
            return;
        }

        showLoadingIndicator(); // Tampilkan loading

        if (isNetworkAvailable()) {
            Toast.makeText(this, "Online: Mengambil data dari API...", Toast.LENGTH_SHORT).show();
            fetchDrugDataFromApi();
        } else {
            Toast.makeText(this, "Offline: Mengambil data dari favorit...", Toast.LENGTH_SHORT).show();
            loadDrugDataFromFavorites();
        }
    }

    private void fetchDrugDataFromApi() {
        new DrugRepository().fetchDrugById(drugIdIntent, new DrugRepository.SingleDrugCallback() {
            @Override
            public void onSuccess(DrugResponse.Drug drug) {
                currentDisplayedDrug = drug;
                runOnUiThread(() -> {
                    drugDetailHelper.displayDrugData(drug);
                    checkFavoriteStatus();
                    hideLoadingIndicator(); // Sembunyikan setelah sukses
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    Toast.makeText(DetailDrugActivity.this, "Gagal memuat data dari API: " + message + ". Mencoba dari favorit...", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "API Error: " + message);
                    loadDrugDataFromFavorites(); // Ini akan memicu loading lagi jika diperlukan
                });
            }
        });
    }

    private void loadDrugDataFromFavorites() {
        if (executorService == null) {
            Log.e(TAG, "ExecutorService is null, cannot load drug data from favorites in background.");
            Toast.makeText(this, "Aplikasi mengalami masalah internal (Executor).", Toast.LENGTH_LONG).show();
            hideLoadingIndicator(); // Sembunyikan loading jika executor null
            return;
        }

        executorService.execute(() -> {
            Favorite favorite = favoriteManager.getFavoriteByIdObatAndEmail(drugIdIntent, currentUserEmail);

            runOnUiThread(() -> {
                if (favorite != null) {
                    currentFavoriteDrug = favorite;
                    drugDetailHelper.displayFavoriteData(favorite);
                    checkFavoriteStatus();
                } else {
                    showError("Obat tidak ditemukan di favorit lokal dan tidak ada koneksi internet.");
                    Toast.makeText(DetailDrugActivity.this, "Data obat tidak tersedia offline.", Toast.LENGTH_LONG).show();
                }
                hideLoadingIndicator(); // Sembunyikan setelah selesai
            });
        });
    }

    private void showError(String errorText) {
        binding.tvDrugId.setText(errorText);
        drugDetailHelper.clearAllDrugTextViews();
    }

    private void saveDrugToFavorites() {
        if (currentUserEmail == null || currentUserEmail.equals("Tidak tersedia") || currentUserEmail.equals("guest@example.com")) {
            Toast.makeText(this, "Anda harus login untuk menambahkan ke favorit.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (drugIdIntent == null || drugIdIntent.isEmpty()) {
            Toast.makeText(this, "ID Obat tidak tersedia.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentDisplayedDrug == null && currentFavoriteDrug == null) {
            Toast.makeText(this, "Data obat belum dimuat sepenuhnya atau tidak tersedia.", Toast.LENGTH_SHORT).show();
            return;
        }

        Favorite favoriteToSave;
        if (currentDisplayedDrug != null) {
            favoriteToSave = drugDetailHelper.mapDrugResponseToFavorite(currentDisplayedDrug, currentUserEmail);
        } else {
            favoriteToSave = currentFavoriteDrug;
        }

        if (favoriteToSave == null) {
            Toast.makeText(this, "Kesalahan data: Obat tidak dapat disimpan.", Toast.LENGTH_SHORT).show();
            return;
        }

        showLoadingIndicator(); // Tampilkan loading

        if (executorService == null) {
            Log.e(TAG, "ExecutorService is null, cannot save/delete favorite in background.");
            Toast.makeText(this, "Aplikasi mengalami masalah internal (Executor).", Toast.LENGTH_LONG).show();
            hideLoadingIndicator(); // Sembunyikan loading jika executor null
            return;
        }

        String drugIdForDb = favoriteToSave.getIdObat();
        String userEmailForDb = favoriteToSave.getEmailFk();

        executorService.execute(() -> {
            boolean isAlreadyFavorited = favoriteManager.isDrugFavorited(drugIdForDb, userEmailForDb);

            runOnUiThread(() -> {
                boolean operationSuccess;
                String toastMessage;
                int imageResource;

                if (isAlreadyFavorited) {
                    int rowsDeleted = favoriteManager.deleteFavoriteByIdObatAndEmail(drugIdForDb, userEmailForDb);
                    operationSuccess = (rowsDeleted > 0);
                    toastMessage = operationSuccess ? "Berhasil dihapus dari favorit!" : "Gagal menghapus dari favorit.";
                    imageResource = operationSuccess ? R.drawable.ic_save : R.drawable.ic_saved;
                    if (operationSuccess) {
                        Toast.makeText(DetailDrugActivity.this, toastMessage, Toast.LENGTH_SHORT).show();
                        // Anda mungkin tidak perlu mengubah gambar ikon jika Activity akan ditutup.
                        // binding.ivSaveFavorite.setImageResource(imageResource); // Opsional, bisa dihapus
                        hideLoadingIndicator(); // Sembunyikan loading sebelum finish
                        finish(); // <<< Tambahkan ini untuk menutup Activity
                        return; // Penting: Keluar dari block ini setelah finish()
                    }
                } else {
                    operationSuccess = favoriteManager.addFavorite(favoriteToSave);
                    toastMessage = operationSuccess ? "Berhasil ditambahkan ke favorit!" : "Gagal menambahkan ke favorit.";
                    imageResource = operationSuccess ? R.drawable.ic_saved : R.drawable.ic_save;
                }

                Toast.makeText(DetailDrugActivity.this, toastMessage, Toast.LENGTH_SHORT).show();
                binding.ivSaveFavorite.setImageResource(imageResource);

                checkFavoriteStatus(); // Update ikon favorit setelah operasi
                hideLoadingIndicator(); // Sembunyikan setelah operasi selesai
            });
        });
    }

    private void checkFavoriteStatus() {
        String drugIdToCheck;
        if (currentDisplayedDrug != null) {
            drugIdToCheck = drugDetailHelper.ensureTextValueForDb(currentDisplayedDrug.getId());
        } else if (currentFavoriteDrug != null) {
            drugIdToCheck = drugDetailHelper.ensureTextValueForDb(currentFavoriteDrug.getIdObat());
        } else {
            binding.ivSaveFavorite.setImageResource(R.drawable.ic_save);
            return;
        }

        if (currentUserEmail == null || currentUserEmail.equals("Tidak tersedia") || currentUserEmail.equals("guest@example.com")) {
            binding.ivSaveFavorite.setImageResource(R.drawable.ic_save);
            return;
        }

        if (executorService == null) {
            Log.e(TAG, "ExecutorService is null, cannot check favorite status in background.");
            binding.ivSaveFavorite.setImageResource(R.drawable.ic_save);
            return;
        }

        executorService.execute(() -> {
            boolean isFavorited = favoriteManager.isDrugFavorited(drugIdToCheck, currentUserEmail);
            runOnUiThread(() -> {
                if (isFavorited) {
                    binding.ivSaveFavorite.setImageResource(R.drawable.ic_saved);
                } else {
                    binding.ivSaveFavorite.setImageResource(R.drawable.ic_save);
                }
            });
        });
    }

    private void saveUserEmailToSharedPreferences(String email) {
        getSharedPreferences("user_prefs", MODE_PRIVATE)
                .edit()
                .putString("logged_in_email", email)
                .apply();
    }
}