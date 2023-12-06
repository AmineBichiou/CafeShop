package com.example.myapplication.adapter;

import static java.lang.Float.parseFloat;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.Module.CartModule;
import com.example.myapplication.R;
import com.example.myapplication.eventbus.MyUpdateCartEvent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyCartAdapter extends RecyclerView.Adapter<MyCartAdapter.MyCartViewHolder>{
        private List<CartModule> cartModuleList;
        private Context context;



        public MyCartAdapter(Context context, List<CartModule> cartModuleList) {
                this.context = context;
                this.cartModuleList = cartModuleList;
        }
        public class MyCartViewHolder extends RecyclerView.ViewHolder{
                ImageView minus;
                ImageView plus;
                ImageView clear;
                CircleImageView img;
                TextView name,price,quantity;
                public MyCartViewHolder(@NonNull View itemView) {
                        super(itemView);
                        minus = itemView.findViewById(R.id.minus);
                        plus = itemView.findViewById(R.id.add);
                        clear = itemView.findViewById(R.id.clear);
                        img = itemView.findViewById(R.id.img1);
                        name = itemView.findViewById(R.id.name);
                        price = itemView.findViewById(R.id.price);
                        quantity = itemView.findViewById(R.id.quantity);


                }


        }


        @NonNull
        @Override
        public MyCartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new MyCartViewHolder(LayoutInflater.from(context)
                        .inflate(R.layout.cart_item,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull MyCartViewHolder holder, int position) {
                Glide.with(context).load(cartModuleList.get(position).getImg())
                        .placeholder(R.drawable.ic_launcher_background)
                        .circleCrop().error(R.drawable.exit)
                        .into(holder.img);
                holder.name.setText(new StringBuilder(" ").append(cartModuleList.get(position).getName()));
                holder.price.setText(String.valueOf(cartModuleList.get(position).getPrice()));
                holder.quantity.setText(String.valueOf(cartModuleList.get(position).getQuantity()));

                holder.minus.setOnClickListener(v -> {
                        minusCartItem(holder,cartModuleList.get(position));

                });

                holder.plus.setOnClickListener(v -> {
                        addCartItem(holder,cartModuleList.get(position));
                });

                holder.clear.setOnClickListener(v -> {
                        AlertDialog dialog = new AlertDialog.Builder(context)
                                .setTitle("Delete")
                                .setMessage("Do you really want to delete this item?")
                                .setNegativeButton("CANCEL", (dialog1, which) -> dialog1.dismiss())
                                .setPositiveButton("DELETE", (dialog12, which) -> {

                                        notifyItemRemoved(position);
                                        deleteFromFireBase(cartModuleList.get(position));
                                        dialog12.dismiss();
                                }).create();
                        dialog.show();

                });


        }

        private void minusCartItem(MyCartViewHolder holder, CartModule cartModule) {
                if(cartModule.getQuantity() >= 1) {
                        cartModule.setQuantity(cartModule.getQuantity()-1);
                        cartModule.setTotalPrice((float) (cartModule.getPrice() * cartModule.getQuantity()));
                        holder.quantity.setText(new StringBuilder().append(cartModule.getQuantity()));
                        updateFireBase(cartModule);

                }
                if(cartModule.getQuantity() == 0){
                        deleteFromFireBase(cartModule);
                }
        }
        private void addCartItem(MyCartViewHolder holder, CartModule cartModule) {
                if(cartModule.getQuantity() >= 1){
                        cartModule.setQuantity(cartModule.getQuantity()+1);
                        cartModule.setTotalPrice((float) (cartModule.getPrice() * cartModule.getQuantity()));
                        holder.quantity.setText(new StringBuilder().append(cartModule.getQuantity()));
                        updateFireBase(cartModule);

                }
        }
        private void deleteFromFireBase(CartModule cartModule) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String userId = user.getUid();
                FirebaseDatabase.getInstance().getReference("Cart").child(userId).child(cartModule.getKey())
                        .removeValue()
                        .addOnSuccessListener(aVoid -> EventBus.getDefault().postSticky(new MyUpdateCartEvent()));

        }

        private void updateFireBase(CartModule cartModule) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String userId = user.getUid();
                FirebaseDatabase.getInstance().getReference("Cart").child(userId).child(cartModule.getKey())
                        .setValue(cartModule)
                        .addOnSuccessListener(aVoid -> EventBus.getDefault().postSticky(new MyUpdateCartEvent()));

        }


        @Override
        public int getItemCount() {
                return cartModuleList.size();
        }



}

