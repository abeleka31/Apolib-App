package com.example.apodicty.data.networkApi.api;

import okhttp3.OkHttpClient; // Import ini
import okhttp3.logging.HttpLoggingInterceptor; // Import ini
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String BASE_URL = "https://api.fda.gov/";
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            // Buat interceptor untuk logging
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY); // Atur level logging ke BODY untuk melihat JSON

            // Buat OkHttpClient dengan interceptor
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client) // Set OkHttpClient ke Retrofit
                    .build();
        }
        return retrofit;
    }
}