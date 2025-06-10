package com.example.apodicty.page.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import com.example.apodicty.data.sqlitedatabase.database.user.UserManager;
import com.example.apodicty.data.sqlitedatabase.database.user.User;
import com.example.apodicty.page.activity.EditProfileActivity;
import com.example.apodicty.MainActivity;
import com.example.apodicty.R;
import com.example.apodicty.ui.UiHelper;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private TextView tvUsername, profileStatusText, profileNameText, profileEmailText,
            tvEmailVerifiedStatus, tvAccountType, tvName, profile_status_text,
            tvBirth, tvInstansi, tvQuotes;
    private Button btnAccountAction;
    private ImageButton btnDropdown;
    private boolean isSwitchListenerActive = false;
    private LinearLayout infoDetail, header_information, btn_editProfile, btnGuide, tv_favoriteCount;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private GoogleSignInClient mGoogleSignInClient;
    private Switch switchTheme;
    private UserManager userManager;
    private SharedPreferences sharedPreferences;

    private static final int RC_SIGN_IN_GOOGLE_SWITCH = 9003;
    private static final String TAG = "ProfileFragment";

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        userManager = new UserManager(requireContext());
        // --- PERBAIKAN DI SINI: BUKA DATABASE SEBELUM MELAKUKAN OPERASI DATABASE ---
        userManager.open(); // Pastikan database dibuka di onCreateView
        // --- AKHIR PERBAIKAN ---

        mAuth = FirebaseAuth.getInstance();
        initViews(view);

        boolean isDarkMode = UiHelper.loadThemePref(requireContext());

        isSwitchListenerActive = false;
        switchTheme.setChecked(isDarkMode);
        isSwitchListenerActive = true;

        switchTheme.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isSwitchListenerActive) return;

            UiHelper.saveThemePref(requireContext(), isChecked);
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
            requireActivity().recreate();
        });

        setupListeners();
        setupFirebase();
        // Langsung panggil updateUI di sini
        updateUI();

        return view;
    }

    private void initViews(View view) {
        profileStatusText = view.findViewById(R.id.profile_status_text);
        profileNameText = view.findViewById(R.id.profile_name_text);
        profileEmailText = view.findViewById(R.id.profile_email_text);
        tvEmailVerifiedStatus = view.findViewById(R.id.tv_email_verified_status);
        tvAccountType = view.findViewById(R.id.tv_account_type);
        btnGuide = view.findViewById(R.id.btn_guide);
        btnDropdown = view.findViewById(R.id.btn_dropdown);
        btnAccountAction = view.findViewById(R.id.btn_account_action);

        infoDetail = view.findViewById(R.id.info_detail);
        header_information = view.findViewById(R.id.header_information);
        tv_favoriteCount = view.findViewById(R.id.tv_favorite_count);

        tvName = view.findViewById(R.id.tvName);
        tvUsername = view.findViewById(R.id.tvUsername);
        tvBirth = view.findViewById(R.id.tvBirth);
        tvInstansi = view.findViewById(R.id.tvInstansi);
        tvQuotes = view.findViewById(R.id.tvQuotes);
        profile_status_text = view.findViewById(R.id.profile_status_text);
        switchTheme = view.findViewById(R.id.switchTheme);
        btn_editProfile = view.findViewById(R.id.btn_editProfile);

    }

    private void setupListeners() {
        UiHelper.setupAnimatedClick(btnGuide, () -> requireActivity());

        btnDropdown.setOnClickListener(v -> {
            if (infoDetail.getVisibility() == View.GONE) {
                infoDetail.setVisibility(View.VISIBLE);
                btnDropdown.setImageResource(R.drawable.ic_dropup);
            } else {
                infoDetail.setVisibility(View.GONE);
                btnDropdown.setImageResource(R.drawable.ic_dropdown);
            }
        });
        UiHelper.setupAnimatedClick(header_information, () -> {
            if (infoDetail.getVisibility() == View.GONE) {
                infoDetail.setVisibility(View.VISIBLE);
                btnDropdown.setImageResource(R.drawable.ic_dropup);
            } else {
                infoDetail.setVisibility(View.GONE);
                btnDropdown.setImageResource(R.drawable.ic_dropdown);
            }
        });

        UiHelper.setupAnimatedClick(btn_editProfile, () -> {
            Intent intent = new Intent(getActivity(), EditProfileActivity.class);
            startActivity(intent);
        });

        btnAccountAction.setOnClickListener(v -> {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null && currentUser.isAnonymous()) {
                showSwitchAccountPopup();
            } else {
                showLogoutConfirmationPopup();
            }
        });
    }

    private void setupFirebase() {
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mAuth = FirebaseAuth.getInstance();
            db = mainActivity.getFirestoreInstance();

            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
            mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Pastikan database sudah dibuka di sini juga, meskipun sudah di onCreateView.
        // onResume dipanggil setiap kali fragment kembali ke foreground,
        // jadi memastikan database terbuka di sini adalah praktik yang baik.
        if (userManager != null) {
            userManager.open();
        }
        updateUI();
    }

    @Override
    public void onPause() {
        super.onPause();
        // Tutup database saat fragment tidak lagi di foreground
        if (userManager != null) {
            userManager.close();
        }
    }

    private void updateUI() {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        profileStatusText.setVisibility(View.VISIBLE);
        profileNameText.setVisibility(View.VISIBLE);
        profileEmailText.setVisibility(View.VISIBLE);
        tvEmailVerifiedStatus.setVisibility(View.VISIBLE);
        tvAccountType.setVisibility(View.VISIBLE);
        tvName.setVisibility(View.VISIBLE);
        profile_status_text.setVisibility(View.VISIBLE);
        btnAccountAction.setVisibility(View.VISIBLE);

        tvBirth.setVisibility(View.GONE);
        tvInstansi.setVisibility(View.GONE);
        tvQuotes.setVisibility(View.GONE);
        tvUsername.setVisibility(View.GONE);

        if (currentUser != null) {
            if (currentUser.isAnonymous()) {
                profileStatusText.setText("Status Akun Anda:");
                tvAccountType.setText("Tipe Akun: Guest (Anonim)");
                tvAccountType.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
                profileNameText.setText("Guest User");
                profileEmailText.setText("Email: Tidak tersedia");
                tvEmailVerifiedStatus.setVisibility(View.GONE);

                btnAccountAction.setText("Login");
                tvName.setText("Username: Guest");
                profile_status_text.setText("Status Akun: Tidak Terverifikasi");
                profile_status_text.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                btn_editProfile.setVisibility(View.GONE);

                tvBirth.setVisibility(View.GONE);
                tvInstansi.setVisibility(View.GONE);
                tvQuotes.setVisibility(View.GONE);
                tvUsername.setVisibility(View.GONE);

            } else {
                profileStatusText.setText("Status Akun Anda:");
                tvAccountType.setText("Tipe Akun: Permanen");
                tvAccountType.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                profileEmailText.setText("Email: " + (currentUser.getEmail() != null ? currentUser.getEmail() : "Tidak tersedia"));

                if (currentUser.isEmailVerified()) {
                    tvEmailVerifiedStatus.setText("Terverifikasi");
                    tvEmailVerifiedStatus.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                    profile_status_text.setText("Status Akun: Terverifikasi");
                    profile_status_text.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                } else {
                    tvEmailVerifiedStatus.setText("Status Email: Belum Terverifikasi");
                    tvEmailVerifiedStatus.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                    profile_status_text.setText("Status Akun: Belum Terverifikasi");
                    profile_status_text.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                }
                tvEmailVerifiedStatus.setVisibility(View.VISIBLE);
                btn_editProfile.setVisibility(View.VISIBLE);
                btnAccountAction.setText("Logout");

                String userEmail = currentUser.getEmail();
                if (userEmail != null && !userEmail.isEmpty()) {
                    User userProfile = userManager.getUser(userEmail);

                    if (userProfile != null) {
                        Log.d(TAG, "User data from DB: " + userProfile.getNamaLengkap());
                        profileNameText.setText(" " + (userProfile.getNamaLengkap() != null && !userProfile.getNamaLengkap().isEmpty() ? userProfile.getNamaLengkap() : (currentUser.getDisplayName() != null ? currentUser.getDisplayName() : "Belum diatur")));
                        tvName.setText("Nama Lengkap: " + (userProfile.getNamaLengkap() != null && !userProfile.getNamaLengkap().isEmpty() ? userProfile.getNamaLengkap() : "Belum diatur"));
                        tvUsername.setText("Username: " + (userProfile.getUsername() != null && !userProfile.getUsername().isEmpty() ? userProfile.getUsername() : "Belum diatur"));
                        tvBirth.setText("Tanggal Lahir: " + (userProfile.getTanggalLahir() != null && !userProfile.getTanggalLahir().isEmpty() ? userProfile.getTanggalLahir() : "Belum diatur"));
                        tvInstansi.setText("Instansi: " + (userProfile.getInstansi() != null && !userProfile.getInstansi().isEmpty() ? userProfile.getInstansi() : "Belum diatur"));
                        tvQuotes.setText("Quotes: " + (userProfile.getQuotes() != null && !userProfile.getQuotes().isEmpty() ? userProfile.getQuotes() : "Belum diatur"));

                        tvUsername.setVisibility(View.VISIBLE);
                        tvBirth.setVisibility(View.VISIBLE);
                        tvInstansi.setVisibility(View.VISIBLE);
                        tvQuotes.setVisibility(View.VISIBLE);

                    } else {
                        Log.d(TAG, "No user data found in local DB for: " + userEmail);
                        profileNameText.setText(" " + (currentUser.getDisplayName() != null ? currentUser.getDisplayName() : "Belum diatur"));
                        tvName.setText("Nama Lengkap: " + (currentUser.getDisplayName() != null ? currentUser.getDisplayName() : "Belum diatur"));
                        tvUsername.setText("Username: " + (currentUser.getEmail() != null ? currentUser.getEmail().split("@")[0] : "Belum diatur"));
                        tvBirth.setText("Tanggal Lahir: Belum diatur");
                        tvInstansi.setText("Instansi: Belum diatur");
                        tvQuotes.setText("Quotes: Belum diatur");

                        tvUsername.setVisibility(View.VISIBLE);
                        tvBirth.setVisibility(View.VISIBLE);
                        tvInstansi.setVisibility(View.VISIBLE);
                        tvQuotes.setVisibility(View.VISIBLE);
                    }
                } else {
                    Log.w(TAG, "Current user email is null or empty for a permanent user.");
                    profileNameText.setText(" " + (currentUser.getDisplayName() != null ? currentUser.getDisplayName() : "Belum diatur"));
                    tvName.setText("Nama Lengkap: " + (currentUser.getDisplayName() != null ? currentUser.getDisplayName() : "Belum diatur"));
                    tvUsername.setText("Username: " + (currentUser.getEmail() != null ? currentUser.getEmail().split("@")[0] : "Belum diatur"));
                    tvBirth.setText("Tanggal Lahir: Belum diatur");
                    tvInstansi.setText("Instansi: Belum diatur");
                    tvQuotes.setText("Quotes: Belum diatur");
                    tvUsername.setVisibility(View.VISIBLE);
                    tvBirth.setVisibility(View.VISIBLE);
                    tvInstansi.setVisibility(View.VISIBLE);
                    tvQuotes.setVisibility(View.VISIBLE);
                }
            }
        } else {
            profileStatusText.setText("Status Akun Anda:");
            tvAccountType.setText("Tipe Akun: Guest (Anonim)");
            tvAccountType.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
            profileNameText.setText("Guest User");
            profileEmailText.setText("Email: Tidak tersedia");
            tvEmailVerifiedStatus.setVisibility(View.GONE);

            btnAccountAction.setText("Login");
            tvName.setText("Username: Guest");
            profile_status_text.setText("Status Akun: Tidak Terverifikasi");
            profile_status_text.setTextColor(getResources().getColor(android.R.color.holo_red_dark));

            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).signInAnonymously();
            }
            btn_editProfile.setVisibility(View.GONE);

            tvBirth.setVisibility(View.GONE);
            tvInstansi.setVisibility(View.GONE);
            tvQuotes.setVisibility(View.GONE);
            tvUsername.setVisibility(View.GONE);
        }
    }

    private void showLogoutConfirmationPopup() {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.popup_switch_account);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setWindowAnimations(R.style.DialogSlideAnimation);

        TextView title = dialog.findViewById(R.id.tv_popup_title);
        TextView message = dialog.findViewById(R.id.tv_popup_message);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel);
        Button btnOk = dialog.findViewById(R.id.btn_ok);

        title.setText("Konfirmasi Logout");
        message.setText("Apakah Anda yakin ingin logout dari akun ini? Anda akan kembali sebagai Guest.");
        btnOk.setText("Logout");

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnOk.setOnClickListener(v -> {
            dialog.dismiss();
            if (isNetworkAvailable()) {
                mAuth.signOut();
                if (mGoogleSignInClient != null) {
                    mGoogleSignInClient.signOut().addOnCompleteListener(task -> {
                        if (getActivity() instanceof MainActivity) {
                            ((MainActivity) getActivity()).signInAnonymously();
                        }
                        updateUI();
                    });
                } else {
                    if (getActivity() instanceof MainActivity) {
                        ((MainActivity) getActivity()).signInAnonymously();
                    }
                    updateUI();
                }
            } else {
                showNoNetworkPopup();
            }
        });
        dialog.show();
    }

    private void showSwitchAccountPopup() {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.popup_switch_account);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setWindowAnimations(R.style.DialogSlideAnimation);

        TextView title = dialog.findViewById(R.id.tv_popup_title);
        TextView message = dialog.findViewById(R.id.tv_popup_message);
        Button btnCancel = dialog.findViewById(R.id.btn_cancel);
        Button btnOk = dialog.findViewById(R.id.btn_ok);

        title.setText("Login Google");
        message.setText("Apakah Anda ingin beralih dari sesi Guest Anda ke akun Google?");
        btnOk.setText("Login");

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnOk.setOnClickListener(v -> {
            dialog.dismiss();
            if (isNetworkAvailable()) {
                signInWithGoogle();
            } else {
                showNoNetworkPopup();
            }
        });
        dialog.show();
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN_GOOGLE_SWITCH);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN_GOOGLE_SWITCH) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

                mAuth.signInWithCredential(credential)
                        .addOnCompleteListener(getActivity(), taskAuth -> {
                            if (taskAuth.isSuccessful()) {
                                FirebaseUser newPermanentUser = taskAuth.getResult().getUser();
                                Log.d(TAG, "Google Sign-In successful for: " + newPermanentUser.getEmail());

                                if (userManager != null && newPermanentUser.getEmail() != null) {
                                    String email = newPermanentUser.getEmail();
                                    String username = newPermanentUser.getDisplayName() != null ? newPermanentUser.getDisplayName() : (email.split("@")[0]);
                                    String namaLengkap = newPermanentUser.getDisplayName();

                                    // --- PERUBAHAN UTAMA DI SINI ---
                                    User existingUser = userManager.getUser(email); // Coba ambil data yang sudah ada

                                    String tanggalLahirToSave = (existingUser != null && existingUser.getTanggalLahir() != null && !existingUser.getTanggalLahir().isEmpty()) ? existingUser.getTanggalLahir() : "";
                                    String instansiToSave = (existingUser != null && existingUser.getInstansi() != null && !existingUser.getInstansi().isEmpty()) ? existingUser.getInstansi() : "";
                                    String quotesToSave = (existingUser != null && existingUser.getQuotes() != null && !existingUser.getQuotes().isEmpty()) ? existingUser.getQuotes() : "";
                                    // --- AKHIR PERUBAHAN UTAMA ---

                                    if (!userManager.isUserExists(email)) {
                                        userManager.insertUser(email, username, namaLengkap, tanggalLahirToSave, instansiToSave, quotesToSave);
                                        Log.d(TAG, "Inserted new user into local DB: " + email);
                                    } else {
                                        // Saat update, juga pertahankan data yang sudah ada jika tidak ada perubahan dari Firebase
                                        userManager.updateUser(email, username, namaLengkap, tanggalLahirToSave, instansiToSave, quotesToSave);
                                        Log.d(TAG, "Updated existing user in local DB: " + email);
                                    }
                                }

                                updateUserProfileInFirestore(newPermanentUser);
                                showAccountLinkedPopup(newPermanentUser, "Login Berhasil");
                                updateUI();
                            } else {
                                Log.e(TAG, "Firebase Auth with Google failed: " + taskAuth.getException().getMessage(), taskAuth.getException());
                                if (taskAuth.getException() instanceof FirebaseAuthUserCollisionException) {
                                    AuthCredential conflictCredential = credential;
                                    showAccountCollisionPopup(conflictCredential);
                                } else {
                                    mAuth.signOut();
                                    if (mGoogleSignInClient != null) {
                                        mGoogleSignInClient.signOut();
                                    }
                                    if (getActivity() instanceof MainActivity) {
                                        ((MainActivity) getActivity()).signInAnonymously();
                                    }
                                    updateUI();
                                }
                            }
                        });
            } catch (ApiException e) {
                Log.e(TAG, "Google Sign-In failed: " + e.getStatusCode() + " - " + e.getMessage());
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).signInAnonymously();
                }
                updateUI();
            }
        }
    }

    private void updateUserProfileInFirestore(FirebaseUser user) {
        if (db == null) {
            MainActivity mainActivity = (MainActivity) getActivity();
            if (mainActivity != null) {
                db = mainActivity.getFirestoreInstance();
            } else {
                Log.e(TAG, "Firestore instance is null, cannot update profile in Firestore.");
                return;
            }
        }

        String userEmail = user.getEmail();
        if (userEmail == null) {
            Log.w(TAG, "User email is null, cannot update Firestore profile without email.");
            return;
        }

        User localUser = userManager.getUser(userEmail);
        Map<String, Object> userData = new HashMap<>();

        if (localUser != null) {
            userData.put("email", localUser.getEmail());
            userData.put("username", localUser.getUsername());
            userData.put("namaLengkap", localUser.getNamaLengkap());
            userData.put("tanggalLahir", localUser.getTanggalLahir());
            userData.put("instansi", localUser.getInstansi());
            userData.put("quotes", localUser.getQuotes());
        } else {
            // Ini akan dipicu jika tidak ada data lokal untuk email ini sama sekali
            // Maka kita inisialisasi dengan data dari Firebase Auth dan string kosong untuk detail lainnya
            userData.put("email", user.getEmail() != null ? user.getEmail() : "N/A");
            userData.put("username", user.getDisplayName() != null ? user.getDisplayName() : (user.getEmail() != null ? user.getEmail().split("@")[0] : ""));
            userData.put("namaLengkap", user.getDisplayName() != null ? user.getDisplayName() : "");
            userData.put("tanggalLahir", "");
            userData.put("instansi", "");
            userData.put("quotes", "");
            Log.w(TAG, "No local user data found for " + userEmail + ". Initializing Firestore with Firebase Auth data.");
        }

        userData.put("isAnonymous", user.isAnonymous());
        boolean isGoogleLinked = user.getProviderData().stream().anyMatch(info -> info.getProviderId().equals(GoogleAuthProvider.PROVIDER_ID));
        userData.put("isGoogleLinked", isGoogleLinked);

        db.collection("users").document(user.getUid()).set(userData)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "User profile successfully written to Firestore for UID: " + user.getUid()))
                .addOnFailureListener(e -> Log.e(TAG, "Error writing user profile to Firestore for UID: " + user.getUid(), e));
    }


    private void showAccountLinkedPopup(FirebaseUser linkedUser, String title) {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.popup_account_linked);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setWindowAnimations(R.style.DialogSlideAnimation);

        TextView tvTitle = dialog.findViewById(R.id.tv_popup_title);
        TextView tvMessage = dialog.findViewById(R.id.tv_popup_message);
        Button btnOk = dialog.findViewById(R.id.btn_ok);

        tvTitle.setText(title);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        Date now = new Date();

        String tanggal = dateFormat.format(now);
        String waktu = timeFormat.format(now);

        String message = "Selamat, " + (linkedUser.getDisplayName() != null ? linkedUser.getDisplayName() : "Pengguna") + "!\n"
                + "Anda berhasil login.\n"
                + "Tanggal: " + tanggal + "\n"
                + "Waktu: " + waktu;

        tvMessage.setText(message);
        btnOk.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void showAccountCollisionPopup(AuthCredential conflictCredential) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Akun Google Sudah Digunakan!");
        builder.setMessage("Akun Google ini sudah terhubung dengan akun Firebase lain. Akun saat ini akan logout dan Anda akan login dengan akun ini. Apakah Anda ingin melanjutkan?");
        builder.setPositiveButton("Lanjutkan Login", (dialog, which) -> {
            mAuth.signInWithCredential(conflictCredential)
                    .addOnCompleteListener(getActivity(), task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser newPermanentUser = task.getResult().getUser();
                            Log.d(TAG, "Account collision resolved. Logged in with: " + newPermanentUser.getEmail());

                            if (userManager != null && newPermanentUser.getEmail() != null) {
                                String email = newPermanentUser.getEmail();
                                String username = newPermanentUser.getDisplayName() != null ? newPermanentUser.getDisplayName() : (email.split("@")[0]);
                                String namaLengkap = newPermanentUser.getDisplayName();

                                // --- PERUBAHAN UTAMA DI SINI JUGA ---
                                User existingUser = userManager.getUser(email); // Coba ambil data yang sudah ada

                                String tanggalLahirToSave = (existingUser != null && existingUser.getTanggalLahir() != null && !existingUser.getTanggalLahir().isEmpty()) ? existingUser.getTanggalLahir() : "";
                                String instansiToSave = (existingUser != null && existingUser.getInstansi() != null && !existingUser.getInstansi().isEmpty()) ? existingUser.getInstansi() : "";
                                String quotesToSave = (existingUser != null && existingUser.getQuotes() != null && !existingUser.getQuotes().isEmpty()) ? existingUser.getQuotes() : "";
                                // --- AKHIR PERUBAHAN UTAMA ---

                                if (!userManager.isUserExists(email)) {
                                    userManager.insertUser(email, username, namaLengkap, tanggalLahirToSave, instansiToSave, quotesToSave);
                                    Log.d(TAG, "Inserted new user (collision) into local DB: " + email);
                                } else {
                                    userManager.updateUser(email, username, namaLengkap, tanggalLahirToSave, instansiToSave, quotesToSave);
                                    Log.d(TAG, "Updated existing user (collision) in local DB: " + email);
                                }
                            }

                            updateUserProfileInFirestore(newPermanentUser);
                            showAccountLinkedPopup(newPermanentUser, "Login Berhasil");
                            updateUI();
                        } else {
                            Log.e(TAG, "Firebase Auth with Google (collision) failed: " + task.getException().getMessage(), task.getException());
                            mAuth.signOut();
                            if (mGoogleSignInClient != null) {
                                mGoogleSignInClient.signOut();
                            }
                            if (getActivity() instanceof MainActivity) {
                                ((MainActivity) getActivity()).signInAnonymously();
                            }
                            updateUI();
                        }
                    });
            dialog.dismiss();
        });
        builder.setNegativeButton("Batal", (dialog, which) -> dialog.dismiss());
        builder.setCancelable(false);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setWindowAnimations(R.style.DialogSlideAnimation);
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
        }
        return false;
    }

    private void showNoNetworkPopup() {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.popup_no_network);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.getWindow().setWindowAnimations(R.style.DialogSlideAnimation);

        TextView tvTitle = dialog.findViewById(R.id.tv_popup_title_no_network);
        TextView tvMessage = dialog.findViewById(R.id.tv_popup_message_no_network);
        Button btnOk = dialog.findViewById(R.id.btn_ok_no_network);

        tvTitle.setText("Tidak Ada Jaringan!");
        tvMessage.setText("Pastikan Anda terhubung ke internet untuk melanjutkan.");
        btnOk.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}