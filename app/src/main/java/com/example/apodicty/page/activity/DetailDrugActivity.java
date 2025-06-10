// com.example.apodicty.page.activity.DetailDrugActivity.java
package com.example.apodicty.page.activity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.apodicty.R;
import com.example.apodicty.data.networkApi.components.DrugRepository;
import com.example.apodicty.data.networkApi.respon.DrugResponse;
import com.example.apodicty.data.sqlitedatabase.database.favorite.Favorite;
import com.example.apodicty.data.sqlitedatabase.database.favorite.FavoriteManager;
import com.example.apodicty.databinding.ActivityDetailDrugBinding;
import com.example.apodicty.utils.DrugDetailHelper; // Import the new helper class
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class DetailDrugActivity extends AppCompatActivity {
    private ActivityDetailDrugBinding binding;
    private DrugDetailHelper drugDetailHelper; // Instance of the helper
    private String drugIdIntent;
    private FirebaseAuth mAuth;

    private FavoriteManager favoriteManager;
    private String currentUserEmail;

    private DrugResponse.Drug currentDisplayedDrug; // Drug object from API
    private Favorite currentFavoriteDrug; // Drug object from SQLite (if offline)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailDrugBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        drugDetailHelper = new DrugDetailHelper(binding); // Initialize the helper
        mAuth = FirebaseAuth.getInstance();
        favoriteManager = new FavoriteManager(this);

        getIntentData();
        displayCurrentUser();
        loadDrugDetails();
        binding.ivSaveFavorite.setOnClickListener(v -> saveDrugToFavorites());
    }

    private void getIntentData() {
        drugIdIntent = getIntent().getStringExtra("id");
        Log.d("DetailDrugActivity", "Received ID: " + drugIdIntent);
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
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void loadDrugDetails() {
        if (drugIdIntent == null || drugIdIntent.isEmpty()) {
            showError("ID Obat tidak ditemukan di intent");
            return;
        }

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
                drugDetailHelper.displayDrugData(drug); // Use helper to display
                checkFavoriteStatus();
            }

            @Override
            public void onError(String message) {
                Toast.makeText(DetailDrugActivity.this, "Gagal memuat data dari API: " + message + ". Mencoba dari favorit...", Toast.LENGTH_LONG).show();
                Log.e("DetailDrugActivity", "API Error: " + message);
                loadDrugDataFromFavorites();
            }
        });
    }

    private void loadDrugDataFromFavorites() {
        favoriteManager.open();
        Favorite favorite = favoriteManager.getFavoriteByIdObatAndEmail(drugIdIntent, currentUserEmail);
        favoriteManager.close();

        if (favorite != null) {
            currentFavoriteDrug = favorite;
            drugDetailHelper.displayFavoriteData(favorite); // Use helper to display
            checkFavoriteStatus();
        } else {
            showError("Obat tidak ditemukan di favorit lokal dan tidak ada koneksi internet.");
            Toast.makeText(this, "Data obat tidak tersedia offline.", Toast.LENGTH_LONG).show();
        }
    }

    private void showError(String errorText) {
        binding.tvDrugId.setText(errorText);
        drugDetailHelper.clearAllDrugTextViews(); // Use helper to clear
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
            // Use the helper to map API data to a Favorite object
            favoriteToSave = drugDetailHelper.mapDrugResponseToFavorite(currentDisplayedDrug, currentUserEmail);
        } else {
            // If API data is not available, use the currently loaded favorite data
            favoriteToSave = currentFavoriteDrug; // No need to map, it's already a Favorite object
        }

        if (favoriteToSave == null) {
            Toast.makeText(this, "Kesalahan data: Obat tidak dapat disimpan.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Ensure ID and email are set for favorite operations, even if derived from currentFavoriteDrug
        // This is important because currentFavoriteDrug might not have all fields populated if it came from an older, incomplete save.
        // However, mapDrugResponseToFavorite will populate all fields if currentDisplayedDrug is available.
        // For currentFavoriteDrug, we assume it's already valid.
        String drugIdForDb = favoriteToSave.getIdObat(); // This should be already set by mapDrugResponseToFavorite or from currentFavoriteDrug
        String userEmailForDb = favoriteToSave.getEmailFk(); // This should be already set

        if (isDrugFavorited(drugIdForDb, userEmailForDb)) {
            int rowsDeleted = favoriteManager.deleteFavoriteByIdObatAndEmail(drugIdForDb, userEmailForDb);
            if (rowsDeleted > 0) {
                Toast.makeText(this, "Berhasil dihapus dari favorit!", Toast.LENGTH_SHORT).show();
                binding.ivSaveFavorite.setImageResource(R.drawable.ic_save);
                // Optionally, you might want to refresh the UI or finish the activity
                // finish(); // Consider if finishing is the desired behavior
            } else {
                Toast.makeText(this, "Gagal menghapus dari favorit.", Toast.LENGTH_SHORT).show();
            }
        } else {
            boolean success = favoriteManager.addFavorite(favoriteToSave);
            if (success) {
                Toast.makeText(this, "Berhasil ditambahkan ke favorit!", Toast.LENGTH_SHORT).show();
                binding.ivSaveFavorite.setImageResource(R.drawable.ic_saved);
            } else {
                Toast.makeText(this, "Gagal menambahkan ke favorit.", Toast.LENGTH_SHORT).show();
            }
        }
        checkFavoriteStatus(); // Always update UI after save/delete
    }

    private boolean isDrugFavorited(String drugId, String userEmail) {
        favoriteManager.open();
        boolean isFavorited = favoriteManager.isDrugFavorited(drugId, userEmail);
        favoriteManager.close();
        return isFavorited;
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

        if (isDrugFavorited(drugIdToCheck, currentUserEmail)) {
            binding.ivSaveFavorite.setImageResource(R.drawable.ic_saved);
        } else {
            binding.ivSaveFavorite.setImageResource(R.drawable.ic_save);
        }
    }

    private void saveUserEmailToSharedPreferences(String email) {
        getSharedPreferences("user_prefs", MODE_PRIVATE)
                .edit()
                .putString("logged_in_email", email)
                .apply();
    }


}