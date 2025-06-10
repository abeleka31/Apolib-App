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

import com.example.apodicty.page.activity.DetailDrugActivity;
import com.example.apodicty.ui.UiHelper;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;

import com.example.apodicty.R;
import com.example.apodicty.data.networkApi.respon.DrugResponse;

import java.util.List;

public class DrugAdapter extends RecyclerView.Adapter<DrugAdapter.DrugViewHolder> {
    private List<DrugResponse.Drug> drugList; // List yang akan diisi dengan data obat
    private Context context; // Tambahkan Context untuk menggunakan resource

    public DrugAdapter(List<DrugResponse.Drug> drugList) {
        this.drugList = drugList;
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
        context = parent.getContext(); // Dapatkan context di sini
        View view = LayoutInflater.from(context).inflate(R.layout.item_drug, parent, false);
        return new DrugViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull DrugViewHolder holder, int position) {
        DrugResponse.Drug drug = drugList.get(position);

        // Generic Name
        String genericName = "Generic Name Tidak tersedia";
        if (drug.getOpenfda() != null && drug.getOpenfda().getGenericName() != null && !drug.getOpenfda().getGenericName().isEmpty()) {
            genericName = TextUtils.join(", ", drug.getOpenfda().getGenericName());
        }
        holder.tvGenericName.setText(genericName);

        // Brand Name
        String brandName = "Nama Tidak Tersedia";
        if (drug.getOpenfda() != null && drug.getOpenfda().getBrandName() != null && !drug.getOpenfda().getBrandName().isEmpty()) {
            brandName = TextUtils.join(", ", drug.getOpenfda().getBrandName());
        }
        holder.tvBrandName.setText(brandName);

        // Drug ID
        String drugId = "ID Tidak Tersedia"; // Default jika drug.getId() null/kosong
        if (drug.getId() != null && !drug.getId().isEmpty()) {
            drugId = drug.getId();
        }
        holder.tvDrugId.setText("ID: " + drugId); // Ubah dari "id: " ke "ID: "

        // Drug Type
        String drugType = "Tipe Tidak Tersedia"; // Default jika drug.getOpenfda().getProductType() null/kosong
        if (drug.getOpenfda() != null && drug.getOpenfda().getProductType() != null && !drug.getOpenfda().getProductType().isEmpty()) {
            drugType = TextUtils.join("| ", drug.getOpenfda().getProductType());
        }
        holder.tvType.setText("Tipe: " + drugType);

        // Gambar berdasarkan Tipe Obat
        if ("HUMAN PRESCRIPTION DRUG".equalsIgnoreCase(drugType)) {
            holder.tvPicType.setImageResource(R.drawable.pic_human_prescription_drug);
        } else if ("HUMAN OTC DRUG".equalsIgnoreCase(drugType)) {
            holder.tvPicType.setImageResource(R.drawable.pic_human_otc_drug);
        } else if ("CELLULAR THERAPY".equalsIgnoreCase(drugType))  {
            holder.tvPicType.setImageResource(R.drawable.pic_cellular_therapy);
        } else {
            holder.tvPicType.setImageResource(R.drawable.pic_ask); // Gambar default jika tidak ada yang cocok
        }

        // Listener untuk Item Obat (mengarahkan ke DetailDrugActivity)
        holder.tvDrugItem.setOnClickListener(v -> {
            // Pastikan context tidak null sebelum digunakan
            if (context != null) {
                UiHelper.setupAnimatedClick(holder.tvDrugItem, () -> {
                    Intent intent = new Intent(context, DetailDrugActivity.class);
                    intent.putExtra("id", drug.getId()); // Teruskan ID obat
                    context.startActivity(intent);
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
        LinearLayout tvDrugItem; // Ini adalah LinearLayout yang bisa diklik

        public DrugViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPicType = itemView.findViewById(R.id.tv_pic_type);
            tvGenericName = itemView.findViewById(R.id.tv_generic_name);
            tvBrandName = itemView.findViewById(R.id.tv_brand_name);
            tvDrugItem = itemView.findViewById(R.id.tv_drug_item); // Ini harus merujuk ke root layout item_drug
            tvDrugId = itemView.findViewById(R.id.tv_drug_id);
            tvType = itemView.findViewById(R.id.tv_type);
        }
    }
}