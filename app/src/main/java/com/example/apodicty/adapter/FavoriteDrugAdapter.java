package com.example.apodicty.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apodicty.MainActivity; // <<< IMPORT INI
import com.example.apodicty.R;
import com.example.apodicty.data.sqlitedatabase.database.favorite.Favorite;
import com.example.apodicty.utils.ProgressBarListener; // <<< IMPORT INI
import com.example.apodicty.page.activity.DetailDrugActivity;
import com.example.apodicty.ui.UiHelper; // Jika Anda menggunakan UiHelper.setupAnimatedClick

import java.util.List;
import java.util.concurrent.ExecutorService; // <<< IMPORT INI
import java.util.concurrent.TimeUnit; // <<< IMPORT INI (untuk simulasi delay)

public class FavoriteDrugAdapter extends RecyclerView.Adapter<FavoriteDrugAdapter.FavoriteViewHolder> {

    private List<Favorite> favoriteList;
    private Context context;
    private OnItemClickListener listener; // Deklarasi listener

    // Variabel baru untuk ProgressBarListener dan ExecutorService
    private ProgressBarListener progressBarListener; // <<< DEKLARASI INI
    private ExecutorService executorService;         // <<< DEKLARASI INI

    // Interface untuk click listener
    public interface OnItemClickListener {
        void onItemClick(Favorite favorite);
    }

    // Ubah constructor untuk menerima listener, ProgressBarListener, dan ExecutorService
    public FavoriteDrugAdapter(Context context, List<Favorite> favoriteList, OnItemClickListener listener) {
        this.context = context;
        this.favoriteList = favoriteList;
        this.listener = listener;

        // Inisialisasi ProgressBarListener dan ExecutorService dari Context (MainActivity)
        if (context instanceof MainActivity) {
            this.progressBarListener = (MainActivity) context; // Casting ke ProgressBarListener
            this.executorService = ((MainActivity) context).getExecutorService(); // Dapatkan ExecutorService
        } else {
            // Jika adapter digunakan di luar MainActivity yang mengimplementasikan listener,
            // Anda mungkin perlu strategi yang berbeda atau throw exception
            throw new RuntimeException(context.toString() + " must be MainActivity or implement ProgressBarListener and provide ExecutorService");
        }
    }

    public void setFavoriteList(List<Favorite> newFavoriteList) {
        this.favoriteList = newFavoriteList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorite_drug, parent, false);
        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        Favorite favorite = favoriteList.get(position);

        holder.tvGenericName.setText("Nama Generic: " + favorite.getGenericName());
        holder.tvDrugId.setText("ID: " + favorite.getIdObat());
        holder.tvBrandName.setText("Brand: " + favorite.getBrandName());
        holder.tvEffectiveTime.setText("Effective Time: " + favorite.getEffectiveTime());

        setProductTypeImage(holder.ivProductTypeIcon, favorite.getProductType());

        // Gunakan listener yang diteruskan dari fragment, dan tambahkan Executor
        holder.itemView.setOnClickListener(v -> {
            if (listener != null && progressBarListener != null && executorService != null) {
                // Tampilkan ProgressBar sebelum memulai tugas background
                progressBarListener.showProgressBar();

                // Jalankan tugas di background thread
                executorService.execute(() -> {
                    try {
                        Thread.sleep(1500); // Simulasi delay 1.5 detik (misalnya mengambil detail data)

                        // Setelah tugas selesai, sembunyikan ProgressBar dan panggil listener
                        if (context instanceof MainActivity) { // Pastikan context adalah Activity untuk runOnUiThread
                            ((MainActivity) context).runOnUiThread(() -> {
                                progressBarListener.hideProgressBar(); // Sembunyikan ProgressBar
                                listener.onItemClick(favorite); // Panggil listener untuk membuka DetailDrugActivity
                            });
                        }

                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        if (context instanceof MainActivity) {
                            ((MainActivity) context).runOnUiThread(() -> {
                                progressBarListener.hideProgressBar(); // Sembunyikan ProgressBar jika dibatalkan
                                // Opsional: Tampilkan Toast error
                            });
                        }
                    }
                });
            } else if (listener != null) {
                // Fallback jika ProgressBar atau ExecutorService tidak tersedia
                listener.onItemClick(favorite);
            }
        });
    }

    @Override
    public int getItemCount() {
        return favoriteList.size();
    }

    private void setProductTypeImage(ImageView imageView, String productType) {
        if (productType != null) {
            if ("HUMAN PRESCRIPTION DRUG".equalsIgnoreCase(productType)) {
                imageView.setImageResource(R.drawable.pic_human_prescription_drug);
            } else if ("HUMAN OTC DRUG".equalsIgnoreCase(productType)) {
                imageView.setImageResource(R.drawable.pic_human_otc_drug);
            } else if ("CELLULAR THERAPY".equalsIgnoreCase(productType)) {
                imageView.setImageResource(R.drawable.pic_cellular_therapy);
            } else {
                imageView.setImageResource(R.drawable.pic_ask);
            }
        } else {
            imageView.setImageResource(R.drawable.pic_ask);
        }
    }

    static class FavoriteViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProductTypeIcon;
        TextView tvGenericName, tvDrugId, tvBrandName, tvEffectiveTime;

        public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductTypeIcon = itemView.findViewById(R.id.iv_product_type_icon);
            tvGenericName = itemView.findViewById(R.id.tv_generic_name);
            tvDrugId = itemView.findViewById(R.id.tv_drug_id);
            tvBrandName = itemView.findViewById(R.id.tv_brand_name);
            tvEffectiveTime = itemView.findViewById(R.id.tv_effective_time);
        }
    }
}