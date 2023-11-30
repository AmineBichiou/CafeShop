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
import com.example.myapplication.R;
import com.example.myapplication.eventbus.MyUpdateCartEvent;
import com.example.myapplication.listeneur.CartListeneur;
import com.example.myapplication.listeneur.IRecycleViewClickListener;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;

import java.util.*;

import de.hdodenhof.circleimageview.CircleImageView;


public class CafeAdapter extends RecyclerView.Adapter<CafeAdapter.myCafeViewHolder> {
    private FirebaseUser user;
    private List<CafeModule> cafeModuleList;
    private CartListeneur cardLoadListener;

    Context context;

    public CafeAdapter(Context context,List<CafeModule> cafeModuleList, CartListeneur cardLoadListener) {
        this.context = context;
        this.cafeModuleList = cafeModuleList;
        this.cardLoadListener = cardLoadListener;
    }
    class myCafeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CircleImageView img;
        TextView name, price;

        IRecycleViewClickListener listener;

        public void setListener(IRecycleViewClickListener listener) {
            this.listener = listener;
        }

        public myCafeViewHolder(@NonNull View itemView) {
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
    /*@Override
    protected void onBindViewHolder(@NonNull myCafeViewHolder holder, int position, @NonNull CafeModule model) {
        holder.name.setText(model.getName());
        holder.price.setText(String.valueOf(model.getPrice()));

        Glide.with(holder.img.getContext()).load(model.getImg())
                .placeholder(R.drawable.ic_launcher_background)
                .circleCrop().error(R.drawable.exit)
                .into(holder.img);
        holder.setListener((view, adapterPosition) -> {
            addToCard(cafeModuleList.get(adapterPosition));
        });

    }*/
    private void addToCard(CafeModule cafeModule) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();
            DatabaseReference userCart = FirebaseDatabase.getInstance().getReference("Cart").child(userId);
            userCart.child(cafeModule.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        CartModule cartModule = snapshot.getValue(CartModule.class);
                        Map<String, Object> updateData = new HashMap<>();
                        updateData.put("quantity", cartModule.getQuantity());
                        updateData.put("totalPrice", cartModule.getQuantity() * Float.parseFloat(String.valueOf(cartModule.getPrice())));
                        userCart.child(cafeModule.getKey()).updateChildren(updateData)
                                .addOnSuccessListener(aVoid -> cardLoadListener.onCartLoadFailed("add to Card Success"))
                                .addOnFailureListener(e -> cardLoadListener.onCartLoadFailed(e.getMessage()));
                    } else {
                        CartModule cartModule = new CartModule();
                        cartModule.setName(cafeModule.getName());
                        cartModule.setImg(cafeModule.getImg());
                        cartModule.setPrice(cafeModule.getPrice());
                        cartModule.setQuantity(1);
                        cartModule.setTotalPrice(Float.parseFloat(String.valueOf(cafeModule.getPrice())));
                        userCart.child(cafeModule.getKey()).setValue(cartModule)
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

    @NonNull
    @Override
    public myCafeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cafe_item, parent, false);
        return new myCafeViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull myCafeViewHolder holder, int position) {
        holder.name.setText(new StringBuilder(" ").append(cafeModuleList.get(position).getName()));
        holder.price.setText(String.valueOf(cafeModuleList.get(position).getPrice()));
        Glide.with(context).load(cafeModuleList.get(position).getImg())
                .placeholder(R.drawable.ic_launcher_background)
                .circleCrop().error(R.drawable.exit)
                .into(holder.img);
        holder.setListener((view, adapterPosition) -> {
            addToCard(cafeModuleList.get(adapterPosition));
        });
    }

    @Override
    public int getItemCount() {
        return cafeModuleList.size();
    }
    /*public void updateData(List<CafeModule> newData) {
        cafeModuleList.clear();
        cafeModuleList.addAll(newData);
        notifyDataSetChanged();
    }*/

    /*class myViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CircleImageView img;
        TextView name, price;

        IRecycleViewClickListener listener;

        public void setListener(IRecycleViewClickListener listener) {
            this.listener = listener;
        }
        public myViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img1);
            name = itemView.findViewById(R.id.name);
            price = itemView.findViewById(R.id.price);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onRecycleViewClick(v, getAdapterPosition());
        }
    }*/


}
