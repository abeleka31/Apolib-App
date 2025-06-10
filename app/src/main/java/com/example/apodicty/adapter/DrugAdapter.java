package com.example.apodicty.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.apodicty.MainActivity; // <<< IMPORT INI
import com.example.apodicty.page.activity.DetailDrugActivity;
import com.example.apodicty.ui.UiHelper;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;

import com.example.apodicty.R;
import com.example.apodicty.data.networkApi.respon.DrugResponse;
import com.example.apodicty.utils.ProgressBarListener; // <<< IMPORT INI

import java.util.List;
import java.util.concurrent.ExecutorService; // <<< IMPORT INI
import java.util.concurrent.TimeUnit; // <<< IMPORT INI (untuk simulasi delay)

public class DrugAdapter extends RecyclerView.Adapter<DrugAdapter.DrugViewHolder> {
    private List<DrugResponse.Drug> drugList;
    private Context context;
    private ProgressBarListener progressBarListener; // <<< DEKLARASI INI
    private ExecutorService executorService;         // <<< DEKLARASI INI

    // Constructor baru untuk menerima ProgressBarListener dan ExecutorService
    public DrugAdapter(List<DrugResponse.Drug> drugList, Context context) { // Hapus parameter context dari constructor lama
        this.drugList = drugList;
        this.context = context; // Simpan context
        // Periksa apakah context adalah instance dari MainActivity
        if (context instanceof MainActivity) {
            this.progressBarListener = (MainActivity) context; // Casting ke ProgressBarListener
            this.executorService = ((MainActivity) context).getExecutorService(); // Dapatkan ExecutorService
        } else {
            // Jika adapter digunakan di luar MainActivity yang mengimplementasikan listener,
            // Anda mungkin perlu strategi yang berbeda atau throw exception
            throw new RuntimeException(context.toString() + " must be MainActivity or implement ProgressBarListener and provide ExecutorService");
        }
    }

    public void addDrugs(List<DrugResponse.Drug> newDrugs) {
        int startPosition = drugList.size();
        drugList.addAll(newDrugs);
        notifyItemRangeInserted(startPosition, newDrugs.size());
    }

    public void clearDrugs() {
        drugList.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DrugViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // context = parent.getContext(); // Tidak perlu lagi di sini karena sudah diterima di constructor
        View view = LayoutInflater.from(context).inflate(R.layout.item_drug, parent, false);
        return new DrugViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull DrugViewHolder holder, int position) {
        DrugResponse.Drug drug = drugList.get(position);

        String genericName = "Generic Name Tidak tersedia";
        if (drug.getOpenfda() != null && drug.getOpenfda().getGenericName() != null && !drug.getOpenfda().getGenericName().isEmpty()) {
            genericName = TextUtils.join(", ", drug.getOpenfda().getGenericName());
        }
        holder.tvGenericName.setText(genericName);

        String brandName = "Nama Tidak Tersedia";
        if (drug.getOpenfda() != null && drug.getOpenfda().getBrandName() != null && !drug.getOpenfda().getBrandName().isEmpty()) {
            brandName = TextUtils.join(", ", drug.getOpenfda().getBrandName());
        }
        holder.tvBrandName.setText(brandName);

        String drugId = "ID Tidak Tersedia";
        if (drug.getId() != null && !drug.getId().isEmpty()) {
            drugId = drug.getId();
        }
        holder.tvDrugId.setText("ID: " + drugId);

        String drugType = "Tipe Tidak Tersedia";
        if (drug.getOpenfda() != null && drug.getOpenfda().getProductType() != null && !drug.getOpenfda().getProductType().isEmpty()) {
            drugType = TextUtils.join("| ", drug.getOpenfda().getProductType());
        }
        holder.tvType.setText("Tipe: " + drugType);

        if ("HUMAN PRESCRIPTION DRUG".equalsIgnoreCase(drugType)) {
            holder.tvPicType.setImageResource(R.drawable.pic_human_prescription_drug);
        } else if ("HUMAN OTC DRUG".equalsIgnoreCase(drugType)) {
            holder.tvPicType.setImageResource(R.drawable.pic_human_otc_drug);
        } else if ("CELLULAR THERAPY".equalsIgnoreCase(drugType))  {
            holder.tvPicType.setImageResource(R.drawable.pic_cellular_therapy);
        } else {
            holder.tvPicType.setImageResource(R.drawable.pic_ask);
        }

        holder.tvDrugItem.setOnClickListener(v -> {
            if (context != null && progressBarListener != null && executorService != null) {
                UiHelper.setupAnimatedClick(holder.tvDrugItem, () -> {
                    progressBarListener.showProgressBar(); // Tampilkan ProgressBar

                    // Jalankan tugas di background thread
                    executorService.execute(() -> {
                        try {
                            Thread.sleep(1500); // Simulasi delay 1.5 detik (misalnya mengambil detail data)
                            // Lakukan operasi background jika ada sebelum meluncurkan Activity

                            // Setelah tugas selesai, sembunyikan ProgressBar dan buka DetailDrugActivity
                            ((MainActivity) context).runOnUiThread(() -> { // Casting context ke MainActivity
                                progressBarListener.hideProgressBar(); // Sembunyikan ProgressBar
                                Intent intent = new Intent(context, DetailDrugActivity.class);
                                intent.putExtra("id", drug.getId());
                                context.startActivity(intent);
                            });

                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            ((MainActivity) context).runOnUiThread(() -> {
                                progressBarListener.hideProgressBar(); // Sembunyikan ProgressBar jika dibatalkan
                                // Opsional: Tampilkan Toast error
                            });
                        }
                    });
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return drugList.size();
    }

    public static class DrugViewHolder extends RecyclerView.ViewHolder {
        ImageView tvPicType;
        TextView tvGenericName, tvBrandName, tvDrugId, tvType;
        LinearLayout tvDrugItem;

        public DrugViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPicType = itemView.findViewById(R.id.tv_pic_type);
            tvGenericName = itemView.findViewById(R.id.tv_generic_name);
            tvBrandName = itemView.findViewById(R.id.tv_brand_name);
            tvDrugItem = itemView.findViewById(R.id.tv_drug_item);
            tvDrugId = itemView.findViewById(R.id.tv_drug_id);
            tvType = itemView.findViewById(R.id.tv_type);
        }
    }
}