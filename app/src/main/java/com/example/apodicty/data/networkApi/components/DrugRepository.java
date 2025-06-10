package com.example.apodicty.data.networkApi.components;

import android.util.Log;

import com.example.apodicty.data.networkApi.api.ApiClient;
import com.example.apodicty.data.networkApi.api.ApiService;
import com.example.apodicty.data.networkApi.respon.DrugResponse;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DrugRepository {

    private static final String TAG = "DrugRepository";

    public interface DrugCallback {
        void onSuccess(List<DrugResponse.Drug> drugs);
        void onFailure(String message);
    }

    public interface SingleDrugCallback {
        void onSuccess(DrugResponse.Drug drug);
        void onError(String message);
    }

    // Ganti namedOnly menjadi verifiedOnly
    public void fetchDrugs(String query, String filterType, boolean verifiedOnly, int limit, int skip, DrugCallback callback) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        StringBuilder finalQueryBuilder = new StringBuilder();

        // 1. Bagian untuk pencarian (query)
        // Perluas pencarian untuk juga menyertakan field 'description' jika ada
        if (query != null && !query.trim().isEmpty()) {
            try {
                String encodedQuery = URLEncoder.encode(query.trim(), StandardCharsets.UTF_8.toString());
                // Pastikan pencarian nama juga bekerja untuk yang tidak ada openfda-nya
                finalQueryBuilder.append("(openfda.generic_name:*" + encodedQuery + "* OR openfda.brand_name:*" + encodedQuery + "*" +
                        " OR description:*" + encodedQuery + "*)"); // Tambahkan pencarian di description
            } catch (Exception e) {
                Log.e(TAG, "Error encoding query: " + e.getMessage());
                callback.onFailure("Kesalahan encoding query.");
                return;
            }
        }

        // 2. Bagian untuk filter berdasarkan product_type
        if (filterType != null && !filterType.isEmpty()) {
            if (finalQueryBuilder.length() > 0) {
                finalQueryBuilder.append(" AND ");
            }
            // Untuk filter product_type, selalu asumsikan ada di openfda.product_type
            try {
                String encodedFilterType = URLEncoder.encode(filterType, StandardCharsets.UTF_8.toString());
                finalQueryBuilder.append("openfda.product_type:\"" + encodedFilterType + "\""); // Gunakan tanda kutip untuk frasa
            } catch (Exception e) {
                Log.e(TAG, "Error encoding filter type: " + e.getMessage());
                callback.onFailure("Kesalahan encoding filter tipe.");
                return;
            }
        }

        // 3. Bagian untuk filter All/Verified
        if (verifiedOnly) {
            // Jika switch dicentang (true), artinya hanya tampilkan yang ada blok openfda-nya
            if (finalQueryBuilder.length() > 0) {
                finalQueryBuilder.append(" AND ");
            }
            finalQueryBuilder.append("_exists_:openfda"); // Filter untuk memastikan blok openfda ada
        }
        // Jika verifiedOnly adalah false, kita tidak menambahkan apa-apa,
        // sehingga semua data (termasuk yang tidak ada openfda) akan ditampilkan.

        String finalSearchQuery = finalQueryBuilder.toString();
        Log.d(TAG, "API Request - Final Query: " + (finalSearchQuery.isEmpty() ? "[NO SEARCH QUERY]" : finalSearchQuery) +
                ", Limit: " + limit + ", Skip: " + skip);

        Call<DrugResponse> call = apiService.getDrugLabels(finalSearchQuery, limit, skip);
        call.enqueue(new Callback<DrugResponse>() {
            @Override
            public void onResponse(Call<DrugResponse> call, Response<DrugResponse> response) {
                if (response.isSuccessful()) {
                    DrugResponse body = response.body();
                    if (body != null && body.getResults() != null) {
                        Log.d(TAG, "API Response - Success, drugs found: " + body.getResults().size());
                        callback.onSuccess(body.getResults());
                    } else {
                        Log.w(TAG, "API Response - Empty data from server.");
                        callback.onFailure("Data kosong dari server.");
                    }
                } else {
                    String errorMessage = "Gagal memuat data: " + response.code() + " - " + response.message();
                    Log.e(TAG, "API Response - Failed: " + errorMessage);
                    callback.onFailure(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<DrugResponse> call, Throwable t) {
                String errorMessage = "Kesalahan jaringan: " + (t.getMessage() != null ? t.getMessage() : "Unknown error");
                Log.e(TAG, "API Response - Network error: " + errorMessage, t);
                callback.onFailure(errorMessage);
            }
        });
    }

    public void fetchDrugById(String id, SingleDrugCallback callback) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        String searchQuery = "id:" + id;

        Call<DrugResponse> call = apiService.getDrugById(searchQuery);
        call.enqueue(new Callback<DrugResponse>() {
            @Override
            public void onResponse(Call<DrugResponse> call, Response<DrugResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getResults() != null) {
                    if (!response.body().getResults().isEmpty()) {
                        callback.onSuccess(response.body().getResults().get(0));
                    } else {
                        callback.onError("Data tidak ditemukan");
                    }
                } else {
                    callback.onError("Gagal memuat data. Kode: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<DrugResponse> call, Throwable t) {
                callback.onError("Kesalahan jaringan: " + t.getMessage());
            }
        });
    }
}