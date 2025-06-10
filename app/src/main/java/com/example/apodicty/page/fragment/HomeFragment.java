package com.example.apodicty.page.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
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

import com.example.apodicty.MainActivity;
import com.example.apodicty.R;
import com.example.apodicty.adapter.DrugAdapter;
import com.example.apodicty.data.networkApi.api.ApiClient;
import com.example.apodicty.data.networkApi.api.ApiService;
import com.example.apodicty.data.networkApi.components.DrugRepository;
import com.example.apodicty.data.networkApi.respon.DrugResponse;
import com.example.apodicty.utils.ProgressBarListener; // <<< IMPORT YANG BENAR UNTUK TOP-LEVEL INTERFACE

import java.util.*;
import java.util.concurrent.ExecutorService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private RecyclerView rvDrugs;
    private DrugAdapter drugAdapter;
    private ProgressBar progressBarInitial, progressBarLoadMore;
    private TextView textSwitchLabel, total_drugs,  tv_type_otc_count, tv_type_prescription_count, tv_type_cellular_count;
    private TextView btnLoadMore;
    private EditText editTextSearch;
    private ImageView iconFilter;
    private Switch switchAllOrVerified;

    private int offset = 0;
    private final int limit = 15;
    private boolean isLoading = false, allLoaded = false;
    private String currentSearchQuery = "";
    private String currentFilterType = "";
    private boolean isVerifiedOnly = true;

    private final DrugRepository repository = new DrugRepository();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;
    private final long DEBOUNCE_DELAY = 500;

    // Tipe sekarang adalah ProgressBarListener dari package utils
    private ProgressBarListener progressBarListener; // <<< KEMBALI KE TIPE INI
    private ExecutorService executorService;

    private static final String TAG = "HomeFragment";

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Casting ke ProgressBarListener (top-level interface)
        if (context instanceof ProgressBarListener) { // Cek apakah Context mengimplementasikan interface ini
            progressBarListener = (ProgressBarListener) context; // Lakukan casting ke interface
        } else {
            throw new RuntimeException(context.toString() + " must implement ProgressBarListener");
        }
        // Dapatkan ExecutorService dari MainActivity (karena itu yang menyediakannya)
        if (context instanceof MainActivity) {
            executorService = ((MainActivity) context).getExecutorService();
        } else {
            throw new RuntimeException(context.toString() + " must be MainActivity to provide ExecutorService");
        }
    }

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
        switchAllOrVerified = view.findViewById(R.id.switch_filter);
        textSwitchLabel = view.findViewById(R.id.text_switch_label);

        total_drugs = view.findViewById(R.id.total_drugs);
        tv_type_otc_count = view.findViewById(R.id.tv_type_otc_count);
        tv_type_prescription_count = view.findViewById(R.id.tv_type_prescription_count);
        tv_type_cellular_count = view.findViewById(R.id.tv_type_cellular_count);

        isVerifiedOnly = true;
        switchAllOrVerified.setChecked(true);
        updateSwitchLabel();

        switchAllOrVerified.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isVerifiedOnly = isChecked;
            updateSwitchLabel();
            applyFilter(currentFilterType);
            showToast("Tipe filter: " + (isChecked ? "Verified" : "All"));
        });

        rvDrugs.setLayoutManager(new LinearLayoutManager(getContext()));
        drugAdapter = new DrugAdapter(new ArrayList<>(), requireActivity());
        rvDrugs.setAdapter(drugAdapter);

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeCallbacks(searchRunnable);
                final String query = s.toString().trim();
                searchRunnable = () -> {
                    if (!query.equals(currentSearchQuery)) performSearch(query, currentFilterType, isVerifiedOnly);
                };
                handler.postDelayed(searchRunnable, DEBOUNCE_DELAY);
            }
        });

        iconFilter.setOnClickListener(this::showFilterDropdown);
        btnLoadListener();

        fetchDrugs("", "", isVerifiedOnly);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (executorService != null) {
            fetchAllDrugCounts();
        } else {
            Log.e(TAG, "ExecutorService is null in onViewCreated. This should not happen. Check MainActivity's ExecutorService init.");
            Toast.makeText(getContext(), "Error: Gagal memuat data penghitung.", Toast.LENGTH_LONG).show();
        }
    }


    private void btnLoadListener() {
        btnLoadMore.setOnClickListener(v -> {
            if (!isLoading && !allLoaded) {
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
        performSearch(currentSearchQuery, currentFilterType, isVerifiedOnly);
        showToast("Filter diterapkan: " + (filterType.isEmpty() ? "Semua Tipe" : filterType) +
                " & Status: " + (isVerifiedOnly ? "Verified" : "All"));
    }

    private void performSearch(String query, String filterType, boolean verifiedOnly) {
        currentSearchQuery = query;
        isVerifiedOnly = verifiedOnly;
        offset = 0;
        allLoaded = false;
        drugAdapter.clearDrugs();
        fetchDrugs(query, filterType, verifiedOnly);
    }

    private void fetchDrugs(String query, String filterType, boolean verifiedOnly) {
        if (isLoading) return;
        isLoading = true;

        if (offset == 0) showInitialLoading();
        else showLoadMoreLoading();

        repository.fetchDrugs(query, filterType, verifiedOnly, limit, offset, new DrugRepository.DrugCallback() {
            @Override
            public void onSuccess(List<DrugResponse.Drug> drugs) {
                if (isAdded()) {
                    requireActivity().runOnUiThread(() -> updateUIOnSuccess(drugs));
                }
            }

            @Override
            public void onFailure(String message) {
                if (isAdded()) {
                    requireActivity().runOnUiThread(() -> showError(message));
                }
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
        textSwitchLabel.setText(isVerifiedOnly ? "Verified" : "All");
    }

    private void showToast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        progressBarListener = null;
        executorService = null;
        handler.removeCallbacksAndMessages(null);
    }

    private void fetchAllDrugCounts() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        if (executorService == null) {
            Log.e(TAG, "ExecutorService is null in fetchAllDrugCounts. Cannot execute API calls in background.");
            return;
        }

        // Dalam fetchAllDrugCounts() method:

        executorService.execute(() -> { // Jalankan panggilan API di background thread
            // Ambil Total Obat
            apiService.getTotalDrugCount().enqueue(new Callback<DrugResponse>() {
                @Override
                public void onResponse(@NonNull Call<DrugResponse> call, @NonNull Response<DrugResponse> response) {
                    if (isAdded()) {
                        requireActivity().runOnUiThread(() -> {
                            if (response.isSuccessful() && response.body() != null && response.body().getMeta() != null && response.body().getMeta().getResults() != null) {
                                int total = response.body().getMeta().getResults().getTotal();
                                // --- PERBAIKAN DI SINI: Tambahkan keterangan "Total Obat: " ---
                                total_drugs.setText("Total Obat: " + total);
                            } else {
                                total_drugs.setText("Total Obat: N/A");
                                Log.w(TAG, "Failed to get total count: Response not successful or meta/results are null. Code: " + response.code());
                            }
                        });
                    }
                }

                @Override
                public void onFailure(@NonNull Call<DrugResponse> call, @NonNull Throwable t) {
                    if (isAdded()) {
                        requireActivity().runOnUiThread(() -> {
                            total_drugs.setText("Total Obat: Error");
                            Log.e(TAG, "Gagal memuat total obat: " + t.getMessage(), t);
                        });
                    }
                }
            });

            apiService.getOtcCount().enqueue(new Callback<DrugResponse>() {
                @Override
                public void onResponse(@NonNull Call<DrugResponse> call, @NonNull Response<DrugResponse> response) {
                    if (isAdded()) {
                        requireActivity().runOnUiThread(() -> {
                            if (response.isSuccessful() && response.body() != null && response.body().getMeta() != null && response.body().getMeta().getResults() != null) {
                                int total = response.body().getMeta().getResults().getTotal();
                                // --- PERBAIKAN DI SINI: Tambahkan keterangan "OTC: " ---
                                tv_type_otc_count.setText("HUMAN OTC DRUG: " + total); // Menggunakan String.valueOf(total) secara implisit
                            } else {
                                tv_type_otc_count.setText("HUMAN OTC DRUG: N/A"); // <<< PERBAIKAN DI SINI
                                Log.w(TAG, "Failed to get OTC count: Response not successful or meta/results are null. Code: " + response.code());
                            }
                        });
                    }
                }

                @Override
                public void onFailure(@NonNull Call<DrugResponse> call, @NonNull Throwable t) {
                    if (isAdded()) {
                        requireActivity().runOnUiThread(() -> {
                            tv_type_otc_count.setText("OTC: Error"); // <<< PERBAIKAN DI SINI
                            Log.e(TAG, "Gagal memuat jumlah OTC: " + t.getMessage(), t);
                        });
                    }
                }
            });

            apiService.getPrescriptionCount().enqueue(new Callback<DrugResponse>() {
                @Override
                public void onResponse(@NonNull Call<DrugResponse> call, @NonNull Response<DrugResponse> response) {
                    if (isAdded()) {
                        requireActivity().runOnUiThread(() -> {
                            if (response.isSuccessful() && response.body() != null && response.body().getMeta() != null && response.body().getMeta().getResults() != null) {
                                int total = response.body().getMeta().getResults().getTotal();
                                // --- PERBAIKAN DI SINI: Tambahkan keterangan "Resep: " ---
                                tv_type_prescription_count.setText("HUMAN PRESCRIPTION DRUG: " + total);
                            } else {
                                tv_type_prescription_count.setText("HUMAN PRESCRIPTION DRUG: N/A"); // <<< PERBAIKAN DI SINI
                                Log.w(TAG, "Failed to get Prescription count: Response not successful or meta/results are null. Code: " + response.code());
                            }
                        });
                    }
                }

                @Override
                public void onFailure(@NonNull Call<DrugResponse> call, @NonNull Throwable t) {
                    if (isAdded()) {
                        requireActivity().runOnUiThread(() -> {
                            tv_type_prescription_count.setText("HUMAN PRESCRIPTION DRUG: Error"); // <<< PERBAIKAN DI SINI
                            Log.e(TAG, "Gagal memuat jumlah Resep: " + t.getMessage(), t);
                        });
                    }
                }
            });

            apiService.getCellularCount().enqueue(new Callback<DrugResponse>() {
                @Override
                public void onResponse(@NonNull Call<DrugResponse> call, @NonNull Response<DrugResponse> response) {
                    if (isAdded()) {
                        requireActivity().runOnUiThread(() -> {
                            if (response.isSuccessful() && response.body() != null && response.body().getMeta() != null && response.body().getMeta().getResults() != null) {
                                int total = response.body().getMeta().getResults().getTotal();
                                tv_type_cellular_count.setText("Cellular Therapy: " + total); // Sudah benar, tetap seperti ini
                            } else {
                                tv_type_cellular_count.setText("Cellular Therapy: N/A");
                                Log.w(TAG, "Failed to get Cellular count: Response not successful or meta/results are null. Code: " + response.code());
                            }
                        });
                    }
                }

                @Override
                public void onFailure(@NonNull Call<DrugResponse> call, @NonNull Throwable t) {
                    if (isAdded()) {
                        requireActivity().runOnUiThread(() -> {
                            tv_type_cellular_count.setText("Seluler: Error");
                            Log.e(TAG, "Gagal memuat jumlah Seluler: " + t.getMessage(), t);
                        });
                    }
                }
            });
        }); // End of executorService.execute()
    }
}