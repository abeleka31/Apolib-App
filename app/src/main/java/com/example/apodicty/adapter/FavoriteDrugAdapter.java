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

import com.example.apodicty.R;
import com.example.apodicty.data.sqlitedatabase.database.favorite.Favorite;
import com.example.apodicty.page.activity.DetailDrugActivity;

import java.util.List;

public class FavoriteDrugAdapter extends RecyclerView.Adapter<FavoriteDrugAdapter.FavoriteViewHolder> {

    private List<Favorite> favoriteList;
    private Context context;
    private OnItemClickListener listener; // Deklarasi listener

    // Interface untuk click listener
    public interface OnItemClickListener {
        void onItemClick(Favorite favorite);
    }

    // Ubah constructor untuk menerima listener
    public FavoriteDrugAdapter(Context context, List<Favorite> favoriteList, OnItemClickListener listener) {
        this.context = context;
        this.favoriteList = favoriteList;
        this.listener = listener;
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

        // Gunakan listener yang diteruskan dari fragment
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
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