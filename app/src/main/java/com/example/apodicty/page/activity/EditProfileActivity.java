package com.example.apodicty.page.activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log; // Tambahkan untuk logging
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.apodicty.R;
import com.example.apodicty.data.sqlitedatabase.database.user.User;
import com.example.apodicty.data.sqlitedatabase.database.user.UserManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;
import java.util.Locale;

public class EditProfileActivity extends AppCompatActivity {

    private EditText etUsername, etNama, etEmail, etTanggalLahir, etInstansi, etQuotes;
    private Button btnBatal, btnSimpan;

    private FirebaseAuth mAuth;
    private UserManager userManager;

    private static final String TAG = "EditProfileActivity"; // Tag untuk Log

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        mAuth = FirebaseAuth.getInstance();
        userManager = new UserManager(this);

        // --- PENTING: Panggil open() di sini agar database siap sebelum loadUserData() ---
        // Jika kamu tidak melakukan ini, maka loadUserData() akan NullPointerException
        // karena userManager.database masih null saat getUser() dipanggil.
        try {
            userManager.open();
            Log.d(TAG, "Database opened in onCreate.");
        } catch (Exception e) {
            Log.e(TAG, "Error opening database in onCreate: " + e.getMessage());
            Toast.makeText(this, "Gagal membuka database lokal.", Toast.LENGTH_LONG).show();
            finish(); // Tutup aktivitas jika database tidak bisa dibuka
            return; // Penting untuk menghentikan eksekusi lebih lanjut
        }


        initViews();
        setupListeners();
        loadUserData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Cek apakah database sudah terbuka dari onCreate().
        // Ini adalah praktik yang baik jika kamu ingin memastikan database terbuka kembali
        // setelah aktivitas dijeda dan dilanjutkan.
        // Jika sudah terbuka dari onCreate dan belum ditutup, open() akan hanya mengembalikan instance yang sama.
        // Tambahkan try-catch untuk robustness.
        try {
            if (userManager != null) {
                userManager.open();
                Log.d(TAG, "Database opened in onResume.");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error opening database in onResume: " + e.getMessage());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Tutup database saat aktivitas dijeda (tidak lagi di foreground)
        try {
            if (userManager != null) {
                userManager.close();
                Log.d(TAG, "Database closed in onPause.");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error closing database in onPause: " + e.getMessage());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Jika kamu memastikan close() di onPause(), onDestroy() tidak perlu close() lagi.
        // Tapi, sebagai langkah pencegahan terakhir jika onPause tidak selalu terpanggil (jarang terjadi),
        // bisa juga menempatkan close() di sini, tapi itu duplikasi.
        // Pilih salah satu (onPause lebih disarankan untuk close).
    }


    private void initViews() {
        etUsername = findViewById(R.id.et_username);
        etNama = findViewById(R.id.et_nama);
        etEmail = findViewById(R.id.et_email);
        etTanggalLahir = findViewById(R.id.et_tanggal_lahir);
        etInstansi = findViewById(R.id.et_instansi);
        etQuotes = findViewById(R.id.et_quotes);
        btnBatal = findViewById(R.id.btn_batal);
        btnSimpan = findViewById(R.id.btn_simpan);

        etEmail.setEnabled(false);
        etEmail.setFocusable(false);
        etEmail.setClickable(false);
    }

    private void setupListeners() {
        etTanggalLahir.setOnClickListener(v -> showDatePickerDialog());
        btnBatal.setOnClickListener(v -> finish());
        btnSimpan.setOnClickListener(v -> saveUserData());
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String date = String.format(Locale.getDefault(), "%02d-%02d-%d", selectedDay, selectedMonth + 1, selectedYear);
                    etTanggalLahir.setText(date);
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    private void loadUserData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && currentUser.getEmail() != null && !currentUser.isAnonymous()) {
            String email = currentUser.getEmail();
            etEmail.setText(email);

            // Database sudah dibuka di onCreate() atau onResume()
            User userData = userManager.getUser(email);

            if (userData != null) {
                etUsername.setText(userData.getUsername());
                etNama.setText(userData.getNamaLengkap());
                etTanggalLahir.setText(userData.getTanggalLahir());
                etInstansi.setText(userData.getInstansi());
                etQuotes.setText(userData.getQuotes());
                Toast.makeText(this, "Data profil dimuat.", Toast.LENGTH_SHORT).show();
            } else {
                if (currentUser.getDisplayName() != null) {
                    etUsername.setText(currentUser.getDisplayName());
                    etNama.setText(currentUser.getDisplayName());
                } else if (currentUser.getEmail() != null) {
                    etUsername.setText(currentUser.getEmail().split("@")[0]);
                    etNama.setText(currentUser.getEmail().split("@")[0]);
                }
                Toast.makeText(this, "Silakan lengkapi profil Anda.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Akses ditolak. Silakan login dengan akun permanen.", Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private void saveUserData() {
        String email = etEmail.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String namaLengkap = etNama.getText().toString().trim();
        String tanggalLahir = etTanggalLahir.getText().toString().trim();
        String instansi = etInstansi.getText().toString().trim();
        String quotes = etQuotes.getText().toString().trim();

        if (email.isEmpty() || username.isEmpty() || namaLengkap.isEmpty()) {
            Toast.makeText(this, "Email, Username, dan Nama Lengkap tidak boleh kosong.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Pastikan database terbuka sebelum melakukan operasi tulis
        // Meskipun onResume() sudah memanggil open(), panggil lagi di sini sebagai jaga-jaga
        // jika ada skenario di mana activity di-kill dan di-restart tanpa onResume yang benar-benar fresh.
        // Atau, lebih baik pastikan userManager.open() berhasil sebelum melakukan save.
        try {
            if (userManager.isUserExists(email)) {
                int rowsAffected = userManager.updateUser(email, username, namaLengkap, tanggalLahir, instansi, quotes);
                if (rowsAffected > 0) {
                    Toast.makeText(this, "Profil berhasil diperbarui!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Gagal memperbarui profil.", Toast.LENGTH_SHORT).show();
                }
            } else {
                boolean success = userManager.insertUser(email, username, namaLengkap, tanggalLahir, instansi, quotes);
                if (success) {
                    Toast.makeText(this, "Profil baru berhasil disimpan!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Gagal menyimpan profil baru. (Email mungkin sudah terdaftar)", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error saving user data: " + e.getMessage(), e);
            Toast.makeText(this, "Terjadi kesalahan saat menyimpan profil.", Toast.LENGTH_LONG).show();
        }

        finish();
    }
}