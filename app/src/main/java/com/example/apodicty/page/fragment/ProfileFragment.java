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
import android.net.Uri; // <<< Tambahkan import ini untuk Uri
import android.content.ActivityNotFoundException; // <<< Tambahkan import ini
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import com.example.apodicty.data.sqlitedatabase.database.user.UserManager;
import com.example.apodicty.data.sqlitedatabase.database.user.User;
import com.example.apodicty.data.sqlitedatabase.database.favorite.FavoriteManager;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ProfileFragment extends Fragment {

    private TextView tvUsername, profileStatusText, profileNameText, profileEmailText,
            tvEmailVerifiedStatus, tvAccountType, tvName, profile_status_text,
            tvBirth, tvInstansi, tvQuotes, textFavCount;
    private Button btnAccountAction;
    private ImageButton btnDropdown;
    private boolean isSwitchListenerActive = false;
    private LinearLayout infoDetail, header_information, btn_editProfile, btnGuide, tv_favoriteCount;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private GoogleSignInClient mGoogleSignInClient;
    private Switch switchTheme;
    private UserManager userManager;
    private FavoriteManager favoriteManager;

    private ExecutorService executorService;

    private static final int RC_SIGN_IN_GOOGLE_SWITCH = 9003;
    private static final String TAG = "ProfileFragment";

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso);

        executorService = Executors.newSingleThreadExecutor();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        initViews(view);

        userManager = new UserManager(requireContext());
        favoriteManager = new FavoriteManager(requireContext());

        isSwitchListenerActive = false;
        boolean isDarkMode = UiHelper.loadThemePref(requireContext());
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

            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).recreateActivityOnThemeChange();
            }
        });

        setupListeners();
        setupFirebase();
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
        textFavCount = view.findViewById(R.id.text_fav_count);

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
        // --- Perubahan di sini: Mengatur onClickListener untuk btnGuide ---
        UiHelper.setupAnimatedClick(btnGuide, () -> {
            String googleDrivePdfUrl = "https://drive.google.com/file/d/1_epqrIT4SruRoRLCv95YvBUu9AjEgNQ3/view?usp=sharing"; // URL PDF Anda
            String viewerUrl = "https://docs.google.com/viewer?url=" + Uri.encode(googleDrivePdfUrl);
            Uri uri = Uri.parse(viewerUrl);

            Intent chromeIntent = new Intent(Intent.ACTION_VIEW);
            chromeIntent.setData(uri);
            chromeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            // Package name untuk Google Chrome
            String chromePackage = "com.android.chrome";
            chromeIntent.setPackage(chromePackage); // Coba paksa ke Chrome

            try {
                startActivity(chromeIntent);
            } catch (ActivityNotFoundException e) {
                // Fallback: Jika Chrome tidak ditemukan atau gagal dibuka, coba dengan browser default lainnya
                Log.w(TAG, "Chrome not found or failed to open. Trying with default browser. Error: " + e.getMessage());
                Intent defaultBrowserIntent = new Intent(Intent.ACTION_VIEW);
                defaultBrowserIntent.setData(uri);
                defaultBrowserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                if (defaultBrowserIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
                    startActivity(defaultBrowserIntent);
                } else {
                    // Jika tidak ada browser sama sekali yang bisa menangani
                    Log.e(TAG, "Tidak ada aplikasi browser yang ditemukan untuk membuka tautan: " + viewerUrl);
                    Toast.makeText(requireContext(), "Tidak ada aplikasi browser yang terinstal untuk membuka panduan ini.", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnDropdown.setOnClickListener(v -> toggleInfoDetail());
        UiHelper.setupAnimatedClick(header_information, () -> toggleInfoDetail());

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

    private void toggleInfoDetail() {
        if (infoDetail.getVisibility() == View.GONE) {
            infoDetail.setVisibility(View.VISIBLE);
            btnDropdown.setImageResource(R.drawable.ic_dropup);
        } else {
            infoDetail.setVisibility(View.GONE);
            btnDropdown.setImageResource(R.drawable.ic_dropdown);
        }
    }

    private void setupFirebase() {
        if (db == null) {
            MainActivity mainActivity = (MainActivity) getActivity();
            if (mainActivity != null) {
                db = mainActivity.getFirestoreInstance();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (userManager != null) {
            userManager.open();
        }
        if (favoriteManager != null) {
            favoriteManager.open();
        }
        updateUI();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (userManager != null) {
            userManager.close();
        }
        if (favoriteManager != null) {
            favoriteManager.close();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        tvUsername = null; tvEmailVerifiedStatus = null; tvAccountType = null;
        tvName = null; profile_status_text = null; tvBirth = null;
        tvInstansi = null; tvQuotes = null; profileStatusText = null;
        profileNameText = null; profileEmailText = null; textFavCount = null;
        btnAccountAction = null; btnDropdown = null; switchTheme = null;
        infoDetail = null; header_information = null; btn_editProfile = null;
        btnGuide = null; tv_favoriteCount = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (userManager != null) {
            userManager.close();
        }
        if (favoriteManager != null) {
            favoriteManager.close();
        }
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }


    private void updateUI() {
        if (!isAdded() || getContext() == null) {
            Log.w(TAG, "Fragment not attached or context is null, skipping updateUI.");
            return;
        }

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (profileStatusText == null || profileNameText == null || profileEmailText == null ||
                tvEmailVerifiedStatus == null || tvAccountType == null || tvName == null ||
                profile_status_text == null || btnAccountAction == null || btn_editProfile == null ||
                tvBirth == null || tvInstansi == null || tvQuotes == null || tvUsername == null ||
                textFavCount == null) {
            Log.e(TAG, "One or more UI views are null in updateUI. Re-check initViews.");
            return;
        }

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
            String userEmail = currentUser.isAnonymous() ? "guest@example.com" : (currentUser.getEmail() != null ? currentUser.getEmail() : "guest@example.com");

            updateFavoriteCount(userEmail);

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

                if (userEmail != null && !userEmail.isEmpty()) {
                    User userProfile = null;
                    if (userManager != null && userManager.isDbOpen()) {
                        userProfile = userManager.getUser(userEmail);
                    } else {
                        Log.w(TAG, "UserManager or database not ready, cannot fetch local user data.");
                    }

                    if (userProfile != null) {
                        Log.d(TAG, "User data from DB: " + userProfile.getNamaLengkap());
                        profileNameText.setText(" " + ((userProfile.getNamaLengkap() != null && !userProfile.getNamaLengkap().isEmpty()) ? userProfile.getNamaLengkap() : (currentUser.getDisplayName() != null ? currentUser.getDisplayName() : "Belum diatur")));
                        tvName.setText("Nama Lengkap: " + ((userProfile.getNamaLengkap() != null && !userProfile.getNamaLengkap().isEmpty()) ? userProfile.getNamaLengkap() : "Belum diatur"));
                        tvUsername.setText("Username: " + ((userProfile.getUsername() != null && !userProfile.getUsername().isEmpty()) ? userProfile.getUsername() : "Belum diatur"));
                        tvBirth.setText("Tanggal Lahir: " + ((userProfile.getTanggalLahir() != null && !userProfile.getTanggalLahir().isEmpty()) ? userProfile.getTanggalLahir() : "Belum diatur"));
                        tvInstansi.setText("Instansi: " + ((userProfile.getInstansi() != null && !userProfile.getInstansi().isEmpty()) ? userProfile.getInstansi() : "Belum diatur"));
                        tvQuotes.setText("Quotes: " + ((userProfile.getQuotes() != null && !userProfile.getQuotes().isEmpty()) ? userProfile.getQuotes() : "Belum diatur"));

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
            textFavCount.setText("0");

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

    private void updateFavoriteCount(String userEmail) {
        if (favoriteManager == null || !isAdded()) {
            Log.w(TAG, "FavoriteManager is null or fragment not added, cannot update favorite count.");
            return;
        }

        if (executorService != null) {
            executorService.execute(() -> {
                favoriteManager.open();
                int count = favoriteManager.getFavoriteCountByUser(userEmail);
                favoriteManager.close();

                if (isAdded()) {
                    requireActivity().runOnUiThread(() -> {
                        if (textFavCount != null) {
                            textFavCount.setText(String.valueOf(count));
                        }
                    });
                }
            });
        } else {
            Log.e(TAG, "ExecutorService is null, cannot update favorite count in background.");
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

                                    User existingUser = userManager.getUser(email);

                                    String tanggalLahirToSave = (existingUser != null && existingUser.getTanggalLahir() != null && !existingUser.getTanggalLahir().isEmpty()) ? existingUser.getTanggalLahir() : "";
                                    String instansiToSave = (existingUser != null && existingUser.getInstansi() != null && !existingUser.getInstansi().isEmpty()) ? existingUser.getInstansi() : "";
                                    String quotesToSave = (existingUser != null && existingUser.getQuotes() != null && !existingUser.getQuotes().isEmpty()) ? existingUser.getQuotes() : "";

                                    if (!userManager.isUserExists(email)) {
                                        userManager.insertUser(email, username, namaLengkap, tanggalLahirToSave, instansiToSave, quotesToSave);
                                        Log.d(TAG, "Inserted new user into local DB: " + email);
                                    } else {
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

        User localUser = null;
        if (userManager != null && userManager.isDbOpen()) {
            localUser = userManager.getUser(userEmail);
        } else {
            Log.w(TAG, "UserManager or database not ready, cannot fetch local user data for Firestore update.");
        }

        Map<String, Object> userData = new HashMap<>();

        if (localUser != null) {
            userData.put("email", localUser.getEmail());
            userData.put("username", localUser.getUsername());
            userData.put("namaLengkap", localUser.getNamaLengkap());
            userData.put("tanggalLahir", localUser.getTanggalLahir());
            userData.put("instansi", localUser.getInstansi());
            userData.put("quotes", localUser.getQuotes());
        } else {
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

                                User existingUser = userManager.getUser(email);

                                String tanggalLahirToSave = (existingUser != null && existingUser.getTanggalLahir() != null && !existingUser.getTanggalLahir().isEmpty()) ? existingUser.getTanggalLahir() : "";
                                String instansiToSave = (existingUser != null && existingUser.getInstansi() != null && !existingUser.getInstansi().isEmpty()) ? existingUser.getInstansi() : "";
                                String quotesToSave = (existingUser != null && existingUser.getQuotes() != null && !existingUser.getQuotes().isEmpty()) ? existingUser.getQuotes() : "";

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