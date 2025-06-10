package com.example.apodicty.page.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apodicty.MainActivity;
import com.example.apodicty.R;
import com.example.apodicty.data.sqlitedatabase.database.favorite.Favorite;
import com.example.apodicty.data.sqlitedatabase.database.favorite.FavoriteManager;
import com.example.apodicty.data.sqlitedatabase.database.DatabaseHelper;
import com.example.apodicty.adapter.FavoriteDrugAdapter;
import com.example.apodicty.utils.ProgressBarListener;
import com.example.apodicty.page.activity.DetailDrugActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class FavoriteFragment extends Fragment {

    private static final int REQUEST_CODE_DETAIL_DRUG = 1001;

    private RecyclerView rvFavorites;
    private TextView tvNoFavorites;
    private FavoriteManager favoriteManager;
    private FavoriteDrugAdapter adapter;
    private String currentUserEmail;

    private EditText etSearchQuery;
    private ImageButton btnSort; // btnSearch sudah dihapus, jadi ini tinggal btnSort

    private String currentSearchQuery = null;
    // Mengubah default sort menjadi Nama Generic (A-Z) atau sesuai preferensi baru Anda
    private String currentSortBy = DatabaseHelper.COL_GENERICNAME;
    private String currentSortOrder = "ASC"; // Default ascending

    // --- PERUBAHAN DI SINI: Hapus opsi "Tanggal Ditambahkan" ---
    private final String[] sortOptions = {
            // "Tanggal Ditambahkan (Terbaru)", // <<< HAPUS BARIS INI
            // "Tanggal Ditambahkan (Terlama)", // <<< HAPUS BARIS INI
            "Nama Generic (A-Z)",
            "Nama Generic (Z-A)",
            "Nama Brand (A-Z)",
            "Nama Brand (Z-A)"
    };
    private final String[][] sortMapping = {
            // {DatabaseHelper.COL_CREATED_AT, "DESC"}, // <<< HAPUS BARIS INI
            // {DatabaseHelper.COL_CREATED_AT, "ASC"},  // <<< HAPUS BARIS INI
            {DatabaseHelper.COL_GENERICNAME, "ASC"},
            {DatabaseHelper.COL_GENERICNAME, "DESC"},
            {DatabaseHelper.COL_BRANDNAME, "ASC"},
            {DatabaseHelper.COL_BRANDNAME, "DESC"}
    };
    // --- AKHIR PERUBAHAN ---

    private ProgressBarListener progressBarListener;
    private ExecutorService executorService;

    private ProgressBar progressBarSearch;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;
    private final long DEBOUNCE_DELAY = 300;

    public FavoriteFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ProgressBarListener) {
            progressBarListener = (ProgressBarListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement ProgressBarListener");
        }
        if (context instanceof MainActivity) {
            executorService = ((MainActivity) context).getExecutorService();
        } else {
            throw new RuntimeException(context.toString() + " must be MainActivity to provide ExecutorService");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);
        rvFavorites = view.findViewById(R.id.rv_favorites);
        tvNoFavorites = view.findViewById(R.id.tv_no_favorites);
        etSearchQuery = view.findViewById(R.id.et_search_query);
        btnSort = view.findViewById(R.id.btn_sort); // Inisialisasi btnSort
        progressBarSearch = view.findViewById(R.id.progress_bar_search);

        rvFavorites.setLayoutManager(new LinearLayoutManager(getContext()));
        favoriteManager = new FavoriteManager(requireContext());

        adapter = new FavoriteDrugAdapter(requireContext(), new ArrayList<>(), new FavoriteDrugAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Favorite favorite) {
                Intent intent = new Intent(requireContext(), DetailDrugActivity.class);
                intent.putExtra("id", favorite.getIdObat());
                FavoriteFragment.this.startActivityForResult(intent, REQUEST_CODE_DETAIL_DRUG);
            }
        });
        rvFavorites.setAdapter(adapter);

        etSearchQuery.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeCallbacks(searchRunnable);
                final String query = s.toString();
                progressBarSearch.setVisibility(View.VISIBLE);

                searchRunnable = () -> {
                    currentSearchQuery = query;
                    loadFavorites();
                };
                handler.postDelayed(searchRunnable, DEBOUNCE_DELAY);
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        btnSort.setOnClickListener(v -> showSortOptionsDialog());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        currentUserEmail = getCurrentUserEmail();
        etSearchQuery.setText(currentSearchQuery);

        if (adapter.getItemCount() == 0 && (currentSearchQuery == null || currentSearchQuery.isEmpty())) {
            loadFavorites();
        }
        // Pastikan loadFavorites juga dipanggil saat resume jika ada query yang sudah ada
        // agar tampilan diperbarui setelah kembali dari DetailDrugActivity, dll.
        // loadFavorites(); // Ini bisa memicu tampilan ProgressBar singkat, tapi mungkin diperlukan
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_DETAIL_DRUG) {
            // Setelah kembali dari DetailDrugActivity, muat ulang favorit (karena mungkin ada perubahan)
            loadFavorites();
        }
    }

    private String getCurrentUserEmail() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            if (!firebaseUser.isAnonymous() && firebaseUser.getEmail() != null) {
                return firebaseUser.getEmail();
            } else {
                return "guest@example.com";
            }
        }
        return getLoggedInUserEmailFromSharedPreferences();
    }

    private void loadFavorites() {
        if (currentUserEmail == null || "-".equals(currentUserEmail) || "Tidak tersedia".equals(currentUserEmail)) {
            tvNoFavorites.setText("Silakan login untuk melihat daftar favorit Anda.");
            tvNoFavorites.setVisibility(View.VISIBLE);
            rvFavorites.setVisibility(View.GONE);
            adapter.setFavoriteList(new ArrayList<>());
            progressBarSearch.setVisibility(View.GONE);
            return;
        }

        if (executorService != null) {
            executorService.execute(() -> {
                favoriteManager.open();
                List<Favorite> favorites = favoriteManager.getFavoritesByUser(currentUserEmail, currentSearchQuery, currentSortBy, currentSortOrder);
                favoriteManager.close();

                if (isAdded()) {
                    requireActivity().runOnUiThread(() -> {
                        if (favorites.isEmpty()) {
                            tvNoFavorites.setText("Tidak ada obat favorit yang ditemukan dengan kriteria ini.");
                            tvNoFavorites.setVisibility(View.VISIBLE);
                            rvFavorites.setVisibility(View.GONE);
                        } else {
                            tvNoFavorites.setVisibility(View.GONE);
                            rvFavorites.setVisibility(View.VISIBLE);
                            adapter.setFavoriteList(favorites);
                        }
                        progressBarSearch.setVisibility(View.GONE);
                    });
                } else {
                    progressBarSearch.setVisibility(View.GONE);
                }
            });
        } else {
            favoriteManager.open();
            List<Favorite> favorites = favoriteManager.getFavoritesByUser(currentUserEmail, currentSearchQuery, currentSortBy, currentSortOrder);
            favoriteManager.close();

            if (favorites.isEmpty()) {
                tvNoFavorites.setText("Tidak ada obat favorit yang ditemukan dengan kriteria ini.");
                tvNoFavorites.setVisibility(View.VISIBLE);
                rvFavorites.setVisibility(View.GONE);
            } else {
                tvNoFavorites.setVisibility(View.GONE);
                rvFavorites.setVisibility(View.VISIBLE);
                adapter.setFavoriteList(favorites);
            }
            progressBarSearch.setVisibility(View.GONE);
        }
    }

    private String getLoggedInUserEmailFromSharedPreferences() {
        if (getContext() != null) {
            return getContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                    .getString("logged_in_email", "-");
        }
        return "-";
    }

    private void showSortOptionsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Urutkan berdasarkan");
        builder.setItems(sortOptions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String[] selectedSort = sortMapping[which];
                currentSortBy = selectedSort[0];
                currentSortOrder = selectedSort[1];
                loadFavorites();
            }
        });
        builder.show();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        progressBarListener = null;
        executorService = null;
        handler.removeCallbacksAndMessages(null);
    }
}