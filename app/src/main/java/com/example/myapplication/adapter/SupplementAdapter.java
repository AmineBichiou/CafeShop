package com.example.myapplication.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.Module.CafeModule;
import com.example.myapplication.Module.CartModule;
import com.example.myapplication.Module.SupplementModule;
import com.example.myapplication.R;
import com.example.myapplication.Supplement;
import com.example.myapplication.eventbus.MyUpdateCartEvent;
import com.example.myapplication.listeneur.CartListeneur;
import com.example.myapplication.listeneur.IRecycleViewClickListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class SupplementAdapter extends RecyclerView.Adapter<SupplementAdapter.mySuppViewHolder> {
    private FirebaseUser user;
    private List<SupplementModule> supplementModuleList;
    private CartListeneur cardLoadListener;

    Context context;
    public SupplementAdapter(Context context, List<SupplementModule> supplementModuleList, CartListeneur cardLoadListener) {
        this.context = context;
        this.supplementModuleList = supplementModuleList;
        this.cardLoadListener = cardLoadListener;
    }

    class mySuppViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CircleImageView img;
        TextView name, price;

        IRecycleViewClickListener listener;

        public void setListener(IRecycleViewClickListener listener) {
            this.listener = listener;
        }

        public mySuppViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img1);
            name = itemView.findViewById(R.id.name);
            price = itemView.findViewById(R.id.price);
            itemView.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            int absolutePosition = getAbsoluteAdapterPosition();
            Log.e("difference", "getAbsoluteAdapterPosition = "+getAbsoluteAdapterPosition()+" getBindingAdapterPosition = "+getBindingAdapterPosition()+" getAbsoluteAdapterPosition = "+getAbsoluteAdapterPosition());
            if (absolutePosition != RecyclerView.NO_POSITION && listener != null) {
                listener.onRecycleViewClick(v, absolutePosition);
            }
        }
    }
    @NonNull
    @Override
    public SupplementAdapter.mySuppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.supplement_item, parent, false);
        return new SupplementAdapter.mySuppViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SupplementAdapter.mySuppViewHolder holder, int position) {
        holder.name.setText(new StringBuilder(" ").append(supplementModuleList.get(position).getName()));
        holder.price.setText(String.valueOf(supplementModuleList.get(position).getPrice()));
        Glide.with(context).load(supplementModuleList.get(position).getImg())
                .placeholder(R.drawable.ic_launcher_background)
                .circleCrop().error(R.drawable.exit)
                .into(holder.img);
        holder.setListener((view, adapterPosition) -> {
            addToCard(supplementModuleList.get(adapterPosition));
        });

    }
    private void addToCard(SupplementModule supplementModule) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();
        DatabaseReference userCart = FirebaseDatabase.getInstance().getReference("Cart").child(userId);
        userCart.child(supplementModule.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    CartModule cartModule = snapshot.getValue(CartModule.class);
                    Map<String, Object> updateData = new HashMap<>();
                    updateData.put("quantity", cartModule.getQuantity());
                    updateData.put("totalPrice", cartModule.getQuantity() * Float.parseFloat(String.valueOf(cartModule.getPrice())));
                    userCart.child(supplementModule.getKey()).updateChildren(updateData)
                            .addOnSuccessListener(aVoid -> cardLoadListener.onCartLoadFailed("add to Card Success"))
                            .addOnFailureListener(e -> cardLoadListener.onCartLoadFailed(e.getMessage()));
                } else {
                    CartModule cartModule = new CartModule();
                    cartModule.setName(supplementModule.getName());
                    cartModule.setImg(supplementModule.getImg());
                    cartModule.setPrice(supplementModule.getPrice());
                    cartModule.setQuantity(1);
                    cartModule.setTotalPrice(Float.parseFloat(String.valueOf(supplementModule.getPrice())));
                    userCart.child(supplementModule.getKey()).setValue(cartModule)
                            .addOnSuccessListener(aVoid -> cardLoadListener.onCartLoadFailed("add to Card Success"))
                            .addOnFailureListener(e -> cardLoadListener.onCartLoadFailed(e.getMessage()));
                }
                EventBus.getDefault().postSticky(new MyUpdateCartEvent());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                cardLoadListener.onCartLoadFailed(error.getMessage());
            }
        });


    }

    @Override
    public int getItemCount() {
        return supplementModuleList.size();
    }
}
