package com.example.apodicty;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.viewpager2.widget.ViewPager2;

import com.example.apodicty.adapter.ViewPagerAdapter;
import com.example.apodicty.ui.UiHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.DocumentSnapshot;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private BottomNavigationView bottomNavigationView;

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseFirestore db;

    private static final String PREFS_NAME = "MyPrefs";
    private static final String LAST_SELECTED_TAB_KEY = "lastSelectedTab";
    private static final String IS_FIRST_LAUNCH_KEY = "isFirstLaunch"; // <<< Kunci baru untuk flag

    @Override // Hapus @SuppressLint("MissingSuperCall") karena ini sudah ditambahkan secara default
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boolean isDarkMode = UiHelper.loadThemePref(this);
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

        viewPager = findViewById(R.id.view_pager);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);

        bottomNavigationView.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                int selectedPosition = 0; // Default ke Home

                if (itemId == R.id.navigation_home) {
                    selectedPosition = 0;
                } else if (itemId == R.id.navigation_search) {
                    selectedPosition = 1;
                } else if (itemId == R.id.navigation_favorite) {
                    selectedPosition = 2;
                } else if (itemId == R.id.navigation_profile) {
                    selectedPosition = 3;
                }
                viewPager.setCurrentItem(selectedPosition);
                saveLastSelectedTab(selectedPosition); // Simpan posisi terpilih
                return true;
            }
        });

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (mAuth.getCurrentUser() != null && !mAuth.getCurrentUser().isAnonymous()) {
                    bottomNavigationView.getMenu().getItem(position).setChecked(true);
                }
                saveLastSelectedTab(position); // Simpan posisi saat ViewPager2 berubah secara manual (swipe)
            }
        });

        bottomNavigationView.setVisibility(View.VISIBLE);
        viewPager.setUserInputEnabled(true);

        // --- Logic untuk menentukan posisi awal ViewPager ---
        if (isFirstLaunch()) { // <<< Cek apakah ini peluncuran pertama
            viewPager.setCurrentItem(0, false); // Selalu ke Home (indeks 0)
            bottomNavigationView.getMenu().getItem(0).setChecked(true);
            markAppLaunched(); // Tandai bahwa aplikasi sudah diluncurkan
        } else {
            // Jika bukan peluncuran pertama, muat posisi terakhir yang disimpan
            int lastSelectedTab = loadLastSelectedTab();
            viewPager.setCurrentItem(lastSelectedTab, false);
            bottomNavigationView.getMenu().getItem(lastSelectedTab).setChecked(true);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            signInAnonymously();
        } else {
            ensureUserProfileExists(currentUser);
        }
    }

    // --- Metode untuk menyimpan dan memuat posisi tab dan flag peluncuran ---
    private void saveLastSelectedTab(int position) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(LAST_SELECTED_TAB_KEY, position);
        editor.apply();
    }

    private int loadLastSelectedTab() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return prefs.getInt(LAST_SELECTED_TAB_KEY, 0);
    }

    // Metode baru untuk memeriksa apakah aplikasi baru saja diluncurkan
    private boolean isFirstLaunch() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return prefs.getBoolean(IS_FIRST_LAUNCH_KEY, true); // Default true jika belum ada
    }

    // Metode baru untuk menandai bahwa aplikasi sudah diluncurkan
    private void markAppLaunched() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(IS_FIRST_LAUNCH_KEY, false);
        editor.apply();
    }
    // --- Akhir metode SharedPreferences ---


    public void signInAnonymously() {
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            ensureUserProfileExists(user);
                            // Setelah sign-in anonim, selalu ke Home untuk peluncuran pertama
                            // atau ke tab terakhir jika sudah pernah dibuka
                            if (isFirstLaunch()) { // Cek lagi untuk kasus sign-in anonim setelah re-install
                                viewPager.setCurrentItem(0, false);
                                bottomNavigationView.getMenu().getItem(0).setChecked(true);
                                markAppLaunched();
                            } else {
                                int lastSelectedTab = loadLastSelectedTab();
                                viewPager.setCurrentItem(lastSelectedTab, false);
                                bottomNavigationView.getMenu().getItem(lastSelectedTab).setChecked(true);
                            }
                        } else {
                            finish();
                        }
                    }
                });
    }

    private void ensureUserProfileExists(FirebaseUser user) {
        db.collection("users").document(user.getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (!document.exists()) {
                                Map<String, Object> userData = new HashMap<>();
                                userData.put("email", user.getEmail() != null ? user.getEmail() : "anonim@apodicty.com");
                                userData.put("username", user.isAnonymous() ? "GuestUser" : (user.getDisplayName() != null ? user.getDisplayName() : (user.getEmail() != null ? user.getEmail().split("@")[0] : "User")));
                                userData.put("isAnonymous", user.isAnonymous());
                                userData.put("isGoogleLinked", user.getProviderData().stream().anyMatch(info -> info.getProviderId().equals(GoogleAuthProvider.PROVIDER_ID)));

                                db.collection("users").document(user.getUid()).set(userData);
                            } else {
                                Map<String, Object> updates = new HashMap<>();
                                updates.put("isAnonymous", user.isAnonymous());
                                updates.put("isGoogleLinked", user.getProviderData().stream().anyMatch(info -> info.getProviderId().equals(GoogleAuthProvider.PROVIDER_ID)));
                                if (!user.isAnonymous()) {
                                    updates.put("email", user.getEmail());
                                    updates.put("username", user.getDisplayName() != null ? user.getDisplayName() : (user.getEmail() != null ? user.getEmail().split("@")[0] : "User"));
                                }
                                db.collection("users").document(user.getUid()).update(updates);
                            }
                        } else {
                            // Penanganan error jika gagal memeriksa profil
                        }
                    }
                });
    }

    public void signOut() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            if (!currentUser.isAnonymous()) {
                if (mGoogleSignInClient != null) {
                    mGoogleSignInClient.signOut();
                }
            }
            mAuth.signOut();
            saveLastSelectedTab(0); // Kembali ke Home setelah logout
            signInAnonymously();
        }
    }

    public void signOutAndRevertToInitialGuest() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && !currentUser.isAnonymous()) {
            if (mGoogleSignInClient != null) {
                mGoogleSignInClient.signOut();
            }
            mAuth.signOut();
            saveLastSelectedTab(0); // Kembali ke Home setelah logout
            signInAnonymously();
        } else if (currentUser != null && currentUser.isAnonymous()) {
            viewPager.setCurrentItem(3);
        }
    }

    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    public FirebaseFirestore getFirestoreInstance() {
        return db;
    }
}