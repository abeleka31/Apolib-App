package com.example.apodicty.page.fragment;

import android.annotation.SuppressLint;
import android.os.*;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.*;
import android.widget.*;
import androidx.annotation.*;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.apodicty.R;
import com.example.apodicty.adapter.DrugAdapter;
import com.example.apodicty.data.networkApi.api.ApiClient;
import com.example.apodicty.data.networkApi.api.ApiService;
import com.example.apodicty.data.networkApi.components.DrugRepository;
import com.example.apodicty.data.networkApi.respon.DrugResponse;
import java.util.*;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private RecyclerView rvDrugs;
    private DrugAdapter drugAdapter;
    private ProgressBar progressBarInitial, progressBarLoadMore;
    private TextView textSwitchLabel, total_drugs,  tv_type_otc_count, tv_type_prescription_count, tv_type_cellular_count;
    private Button btnLoadMore;
    private EditText editTextSearch;
    private ImageView iconFilter;
    private Switch switchAllOrVerified; // Ganti nama switch

    private int offset = 0;
    private final int limit = 15;
    private boolean isLoading = false, allLoaded = false;
    private String currentSearchQuery = "";
    private String currentFilterType = "";
    private boolean isVerifiedOnly = true; // Ganti nama variabel boolean

    private final DrugRepository repository = new DrugRepository();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;
    private final long DEBOUNCE_DELAY = 500;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        rvDrugs = view.findViewById(R.id.rv_drugs);
        progressBarInitial = view.findViewById(R.id.progress_bar_initial);
        progressBarLoadMore = view.findViewById(R.id.progress_bar_load_more);
        btnLoadMore = view.findViewById(R.id.btn_load_more);
        editTextSearch = view.findViewById(R.id.edit_text_search);
        iconFilter = view.findViewById(R.id.icon_filter);
        switchAllOrVerified = view.findViewById(R.id.switch_filter); // Tetap gunakan ID yang sama jika tidak mengubah layout XML
        textSwitchLabel = view.findViewById(R.id.text_switch_label);

        // Inisialisasi TextView count
        total_drugs = view.findViewById(R.id.total_drugs);
        tv_type_otc_count = view.findViewById(R.id.tv_type_otc_count);
        tv_type_prescription_count = view.findViewById(R.id.tv_type_prescription_count);
        tv_type_cellular_count = view.findViewById(R.id.tv_type_cellular_count);

        // Inisialisasi awal untuk "Verified"
        isVerifiedOnly = true; // Defaultnya tampilkan yang "Verified"
        switchAllOrVerified.setChecked(true); // Set switch ke posisi "Verified"
        updateSwitchLabel(); // Update label sesuai status awal

        // Ganti semua listener switch dengan satu ini saja
        switchAllOrVerified.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isVerifiedOnly = isChecked; // True = Verified, False = All
            updateSwitchLabel();
            // Panggil applyFilter untuk memperbarui pencarian dengan filter yang baru
            applyFilter(currentFilterType);
            showToast("Tipe filter: " + (isChecked ? "Verified" : "All")); // Update pesan Toast
        });

        rvDrugs.setLayoutManager(new LinearLayoutManager(getContext()));
        drugAdapter = new DrugAdapter(new ArrayList<>());
        rvDrugs.setAdapter(drugAdapter);

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeCallbacks(searchRunnable);
                final String query = s.toString().trim();
                searchRunnable = () -> {
                    // Panggil performSearch dengan variabel isVerifiedOnly yang baru
                    if (!query.equals(currentSearchQuery)) performSearch(query, currentFilterType, isVerifiedOnly);
                };
                handler.postDelayed(searchRunnable, DEBOUNCE_DELAY);
            }
        });

        iconFilter.setOnClickListener(this::showFilterDropdown);
        btnLoadListener();

        // Panggilan awal fetchDrugs dengan isVerifiedOnly
        fetchDrugs("", "", isVerifiedOnly);
        fetchAllDrugCounts(); // Tetap panggil untuk mengambil jumlah total

        return view;
    }

    private void btnLoadListener() {
        btnLoadMore.setOnClickListener(v -> {
            if (!isLoading && !allLoaded) {
                // Panggil fetchDrugs dengan isVerifiedOnly
                fetchDrugs(currentSearchQuery, currentFilterType, isVerifiedOnly);
            } else if (allLoaded) {
                showToast("Semua data sudah dimuat.");
            }
        });
    }

    private void showFilterDropdown(View view) {
        PopupMenu popupMenu = new PopupMenu(requireContext(), view);
        popupMenu.getMenu().add(Menu.NONE, 1, Menu.NONE, "Semua Tipe");
        popupMenu.getMenu().add(Menu.NONE, 2, Menu.NONE, "HUMAN OTC DRUG");
        popupMenu.getMenu().add(Menu.NONE, 3, Menu.NONE, "HUMAN PRESCRIPTION DRUG");
        popupMenu.getMenu().add(Menu.NONE, 4, Menu.NONE, "CELLULAR THERAPY");

        popupMenu.setOnMenuItemClickListener(menuItem -> {
            String selectedType = "";
            switch (menuItem.getItemId()) {
                case 1: selectedType = ""; break;
                case 2: selectedType = "HUMAN OTC DRUG"; break;
                case 3: selectedType = "HUMAN PRESCRIPTION DRUG"; break;
                case 4: selectedType = "CELLULAR THERAPY"; break;
            }
            // Panggil applyFilter dengan isVerifiedOnly
            applyFilter(selectedType);
            return true;
        });
        popupMenu.show();
    }

    private void applyFilter(String filterType) {
        if (!filterType.equals(currentFilterType)) {
            currentFilterType = filterType;
        }
        updateSwitchLabel();
        // Panggil performSearch dengan isVerifiedOnly
        performSearch(currentSearchQuery, currentFilterType, isVerifiedOnly);
        showToast("Filter diterapkan: " + (filterType.isEmpty() ? "Semua Tipe" : filterType) +
                " & Status: " + (isVerifiedOnly ? "Verified" : "All")); // Update pesan Toast
    }

    private void performSearch(String query, String filterType, boolean verifiedOnly) { // Ganti parameter namedOnly ke verifiedOnly
        currentSearchQuery = query;
        isVerifiedOnly = verifiedOnly; // Pastikan variabel global diperbarui
        offset = 0;
        allLoaded = false;
        drugAdapter.clearDrugs();
        fetchDrugs(query, filterType, verifiedOnly); // Gunakan verifiedOnly
    }

    private void fetchDrugs(String query, String filterType, boolean verifiedOnly) { // Ganti parameter namedOnly ke verifiedOnly
        if (isLoading) return;
        isLoading = true;

        if (offset == 0) showInitialLoading();
        else showLoadMoreLoading();

        // Panggil repository dengan parameter verifiedOnly
        repository.fetchDrugs(query, filterType, verifiedOnly, limit, offset, new DrugRepository.DrugCallback() {
            @Override
            public void onSuccess(List<DrugResponse.Drug> drugs) {
                if (isAdded()) requireActivity().runOnUiThread(() -> updateUIOnSuccess(drugs));
            }

            @Override
            public void onFailure(String message) {
                if (isAdded()) requireActivity().runOnUiThread(() -> showError(message));
            }
        });
    }

    private void showInitialLoading() {
        progressBarInitial.setVisibility(View.VISIBLE);
        rvDrugs.setVisibility(View.GONE);
        btnLoadMore.setVisibility(View.GONE);
    }

    private void showLoadMoreLoading() {
        progressBarLoadMore.setVisibility(View.VISIBLE);
        btnLoadMore.setEnabled(false);
    }

    private void updateUIOnSuccess(List<DrugResponse.Drug> drugs) {
        isLoading = false;
        progressBarInitial.setVisibility(View.GONE);
        progressBarLoadMore.setVisibility(View.GONE);
        btnLoadMore.setEnabled(true);
        rvDrugs.setVisibility(View.VISIBLE);

        if (drugs != null && !drugs.isEmpty()) {
            drugAdapter.addDrugs(drugs);
            offset += drugs.size();

            if (drugs.size() < limit) {
                allLoaded = true;
                btnLoadMore.setVisibility(View.GONE);
                showToast("Semua data berhasil dimuat.");
            } else {
                btnLoadMore.setVisibility(View.VISIBLE);
            }
        } else {
            allLoaded = true;
            btnLoadMore.setVisibility(View.GONE);
            if (offset == 0) {
                showToast("Tidak ada data obat ditemukan untuk pencarian ini.");
                rvDrugs.setVisibility(View.GONE);
            } else {
                showToast("Tidak ada data lagi.");
            }
        }
    }

    private void showError(String message) {
        isLoading = false;
        progressBarInitial.setVisibility(View.GONE);
        progressBarLoadMore.setVisibility(View.GONE);
        btnLoadMore.setEnabled(true);
        btnLoadMore.setVisibility(View.VISIBLE);
        rvDrugs.setVisibility(View.VISIBLE);
        showToast("Error: " + message);
    }

    private void updateSwitchLabel() {
        // Update label sesuai status isVerifiedOnly
        textSwitchLabel.setText(isVerifiedOnly ? "Verified" : "All");
    }

    private void showToast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }
    // ... kode lainnya di dalam kelas HomeFragment

    private void fetchAllDrugCounts() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        // Ambil Total Obat
        apiService.getTotalDrugCount().enqueue(new Callback<DrugResponse>() {
            @Override
            public void onResponse(@NonNull Call<DrugResponse> call, @NonNull Response<DrugResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getMeta() != null && response.body().getMeta().getResults() != null) {
                    int total = response.body().getMeta().getResults().getTotal();
                    total_drugs.setText("Total Obat: " + total);
                } else {
                    total_drugs.setText("Total Obat: N/A");
                }
            }

            @Override
            public void onFailure(@NonNull Call<DrugResponse> call, @NonNull Throwable t) {
                total_drugs.setText("Total Obat: Error");
                Log.e("HomeFragment", "Gagal memuat total obat: " + t.getMessage());
            }
        });

        // Ambil Jumlah OTC
        apiService.getOtcCount().enqueue(new Callback<DrugResponse>() {
            @Override
            public void onResponse(@NonNull Call<DrugResponse> call, @NonNull Response<DrugResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getMeta() != null && response.body().getMeta().getResults() != null) {
                    int total = response.body().getMeta().getResults().getTotal();
                    tv_type_otc_count.setText("OTC: " + total);
                } else {
                    tv_type_otc_count.setText("OTC: N/A");
                }
            }

            @Override
            public void onFailure(@NonNull Call<DrugResponse> call, @NonNull Throwable t) {
                tv_type_otc_count.setText("OTC: Error");
                Log.e("HomeFragment", "Gagal memuat jumlah OTC: " + t.getMessage());
            }
        });

        // Ambil Jumlah Resep
        apiService.getPrescriptionCount().enqueue(new Callback<DrugResponse>() {
            @Override
            public void onResponse(@NonNull Call<DrugResponse> call, @NonNull Response<DrugResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getMeta() != null && response.body().getMeta().getResults() != null) {
                    int total = response.body().getMeta().getResults().getTotal();
                    tv_type_prescription_count.setText("Resep: " + total);
                } else {
                    tv_type_prescription_count.setText("Resep: N/A");
                }
            }

            @Override
            public void onFailure(@NonNull Call<DrugResponse> call, @NonNull Throwable t) {
                tv_type_prescription_count.setText("Resep: Error");
                Log.e("HomeFragment", "Gagal memuat jumlah Resep: " + t.getMessage());
            }
        });

        // Ambil Jumlah Seluler
        apiService.getCellularCount().enqueue(new Callback<DrugResponse>() {
            @Override
            public void onResponse(@NonNull Call<DrugResponse> call, @NonNull Response<DrugResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getMeta() != null && response.body().getMeta().getResults() != null) {
                    int total = response.body().getMeta().getResults().getTotal();
                    tv_type_cellular_count.setText("Seluler: " + total);
                } else {
                    tv_type_cellular_count.setText("Seluler: N/A");
                }
            }

            @Override
            public void onFailure(@NonNull Call<DrugResponse> call, @NonNull Throwable t) {
                tv_type_cellular_count.setText("Seluler: Error");
                Log.e("HomeFragment", "Gagal memuat jumlah Seluler: " + t.getMessage());
            }
        });
    }
}