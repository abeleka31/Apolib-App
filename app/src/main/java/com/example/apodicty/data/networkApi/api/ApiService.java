package com.example.apodicty.data.networkApi.api;

import com.example.apodicty.data.networkApi.respon.DrugResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    @GET("drug/label.json")
    Call<DrugResponse> getDrugLabels(
            @Query("search") String search,
            @Query("limit") int limit,
            @Query("skip") int skip
    );

    @GET("drug/label.json")
    Call<DrugResponse> getDrugById(
            @Query("search") String search
    );

    @GET("drug/label.json?limit=1")
    Call<DrugResponse> getTotalDrugCount();

    // --- PERBAIKAN DI SINI UNTUK MENDAPATKAN COUNT YANG LEBIH BANYAK ---
    // Pastikan product_type di-encode untuk URL.
    @GET("drug/label.json?limit=1&search=openfda.product_type:HUMAN%20OTC%20DRUG")
    Call<DrugResponse> getOtcCount();

    @GET("drug/label.json?limit=1&search=openfda.product_type:HUMAN%20PRESCRIPTION%20DRUG")
    Call<DrugResponse> getPrescriptionCount();

    @GET("drug/label.json?limit=1&search=openfda.product_type:CELLULAR%20THERAPY")
    Call<DrugResponse> getCellularCount();
}