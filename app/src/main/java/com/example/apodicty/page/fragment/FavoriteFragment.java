package com.example.apodicty.page.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable; // Import ini
import android.text.TextWatcher; // Import ini
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apodicty.R;
import com.example.apodicty.data.sqlitedatabase.database.favorite.Favorite;
import com.example.apodicty.data.sqlitedatabase.database.favorite.FavoriteManager;
import com.example.apodicty.data.sqlitedatabase.database.DatabaseHelper; // Import ini
import com.example.apodicty.adapter.FavoriteDrugAdapter;
import com.example.apodicty.page.activity.DetailDrugActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class FavoriteFragment extends Fragment {

    private static final int REQUEST_CODE_DETAIL_DRUG = 1001;

    private RecyclerView rvFavorites;
    private TextView tvNoFavorites;
    private FavoriteManager favoriteManager;
    private FavoriteDrugAdapter adapter;
    private String currentUserEmail;

    // Elemen UI
    private EditText etSearchQuery;
    private ImageButton btnSearch; // Tombol ini bisa dihilangkan jika pencarian sepenuhnya real-time
    private ImageButton btnSort;

    // Variabel untuk pencarian dan pengurutan
    private String currentSearchQuery = null;
    private String currentSortBy = DatabaseHelper.COL_CREATED_AT; // Default sort by creation time
    private String currentSortOrder = "DESC"; // Default descending (terbaru ke terlama)

    // Opsi pengurutan untuk AlertDialog
    private final String[] sortOptions = {
            "Tanggal Ditambahkan (Terbaru)",
            "Tanggal Ditambahkan (Terlama)",
            "Nama Generic (A-Z)",
            "Nama Generic (Z-A)",
            "Nama Brand (A-Z)", // Tambahkan ini jika ingin sort by brand
            "Nama Brand (Z-A)" // Tambahkan ini jika ingin sort by brand
    };
    // Mapping opsi ke kolom DB dan urutan
    private final String[][] sortMapping = {
            {DatabaseHelper.COL_CREATED_AT, "DESC"},
            {DatabaseHelper.COL_CREATED_AT, "ASC"},
            {DatabaseHelper.COL_GENERICNAME, "ASC"},
            {DatabaseHelper.COL_GENERICNAME, "DESC"},
            {DatabaseHelper.COL_BRANDNAME, "ASC"}, // Mapping untuk brand A-Z
            {DatabaseHelper.COL_BRANDNAME, "DESC"} // Mapping untuk brand Z-A
    };


    public FavoriteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);
        rvFavorites = view.findViewById(R.id.rv_favorites);
        tvNoFavorites = view.findViewById(R.id.tv_no_favorites);
        etSearchQuery = view.findViewById(R.id.et_search_query);
        btnSearch = view.findViewById(R.id.btn_search);
        btnSort = view.findViewById(R.id.btn_sort);

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

        // --- Perubahan untuk Real-time Search ---
        etSearchQuery.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Tidak perlu implementasi di sini
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Pemicu pencarian setiap kali teks berubah
                currentSearchQuery = s.toString();
                loadFavorites(); // Muat ulang dengan query baru
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Tidak perlu implementasi di sini
            }
        });

        // Jika pencarian real-time, tombol search mungkin tidak diperlukan, bisa dihilangkan dari XML dan kode.
        // Jika tetap ingin tombol search untuk "submit" pencarian secara manual (bukan real-time),
        // maka listener tombol search tetap diperlukan, dan onTextChanged hanya akan mengupdate currentSearchQuery.
        // Untuk real-time, kita hapus onClickListener dari btnSearch:
        // btnSearch.setOnClickListener(v -> {
        //     currentSearchQuery = etSearchQuery.getText().toString();
        //     loadFavorites();
        // });
        // Atau biarkan tombol search untuk "submit" jika user tidak mau real time update setiap ketikan.
        // Untuk contoh ini, saya akan menyarankan menghapus onClicListener dari btnSearch jika real-time.
        // Jika Anda ingin tombol search tetap berfungsi untuk pencarian MANUAL,
        // maka jangan tambahkan TextWatcher dan pertahankan onClickListener.
        // Jika ingin real-time, hapus onClickListener untuk btnSearch.
        btnSearch.setOnClickListener(v -> {
            // Ini akan memicu pencarian sekali lagi ketika tombol search diklik,
            // yang mungkin berguna jika user mengetik dan tidak menunggu real-time.
            // Atau Anda bisa menghilangkan tombol ini jika real-time sudah cukup.
            currentSearchQuery = etSearchQuery.getText().toString();
            loadFavorites();
        });


        btnSort.setOnClickListener(v -> showSortOptionsDialog());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        currentUserEmail = getCurrentUserEmail();
        // Set teks pencarian saat ini ke EditText jika sudah ada (misal setelah kembali dari Detail)
        etSearchQuery.setText(currentSearchQuery);
        // Panggil loadFavorites() setelah etSearchQuery di-set, agar memuat dengan query yang ada.
        loadFavorites();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_DETAIL_DRUG) {
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
            return;
        }

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
}