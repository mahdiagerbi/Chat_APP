package com.example.myapplication.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.databinding.ItemContainerUserBinding;
import com.example.myapplication.listeners.userlistener;
import com.example.myapplication.model.user;

import java.util.List;

public class useradapter extends RecyclerView.Adapter<useradapter.userviewholder>{
    private final List<user> users;
    private final userlistener userlistener;

    public useradapter(List<user> users,userlistener userlistener) {
        this.users = users;
        this.userlistener=userlistener;
    }

    @NonNull
    @Override
    public userviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContainerUserBinding itemContainerUserBinding=ItemContainerUserBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new userviewholder(itemContainerUserBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull userviewholder holder, int position) {
        holder.setuserdata(users.get(position));

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class userviewholder extends RecyclerView.ViewHolder{
        ItemContainerUserBinding binding;
        userviewholder(ItemContainerUserBinding itemContainerUserBinding){
            super(itemContainerUserBinding.getRoot());
            binding=itemContainerUserBinding;

        }
        void setuserdata(user user){
            binding.textname.setText(user.name);
            binding.textemail.setText(user.email);
            binding.imageprofile.setImageBitmap(getuserimage(user.image));
            binding.getRoot().setOnClickListener(v -> userlistener.onuserclicked(user));
        }
    }

    private Bitmap getuserimage(String encodedimage){
        byte[] bytes= Base64.decode(encodedimage,Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }
}
