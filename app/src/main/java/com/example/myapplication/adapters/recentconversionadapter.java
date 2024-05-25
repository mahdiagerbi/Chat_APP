package com.example.myapplication.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.activities.chatActivity;
import com.example.myapplication.databinding.ItemContainerRecentConversationBinding;
import com.example.myapplication.listeners.conversionlistener;
import com.example.myapplication.model.chatmessage;
import com.example.myapplication.model.user;
import com.example.myapplication.utilities.constantes;

import java.util.List;
import java.util.Objects;

public class recentconversionadapter extends RecyclerView.Adapter<recentconversionadapter.conversionviewholder> {


    private final List<chatmessage> chatmessages;
    private final conversionlistener conversionlistener;

    public recentconversionadapter(List<chatmessage> chatmessages, conversionlistener conversionlistener) {
        this.chatmessages = chatmessages;
        this.conversionlistener=conversionlistener;
    }

    @NonNull
    @Override
    public conversionviewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new conversionviewholder(
                ItemContainerRecentConversationBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false

                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull conversionviewholder holder, int position) {
        holder.setdata(chatmessages.get(position));

    }

    @Override
    public int getItemCount() {
        return chatmessages.size();
    }

    class conversionviewholder extends RecyclerView.ViewHolder {
        ItemContainerRecentConversationBinding binding;
        conversionviewholder(ItemContainerRecentConversationBinding itemContainerRecentConversationBinding){
            super(itemContainerRecentConversationBinding.getRoot());
            binding=itemContainerRecentConversationBinding;
        }

        void setdata(chatmessage chatmessage){
            binding.imageprofile.setImageBitmap(getconversionimage(chatmessage.conversionimage));
            binding.textname.setText(chatmessage.conversionname);
            binding.textrecentmessage.setText(chatmessage.message);
            binding.getRoot().setOnClickListener(v -> {
                user user=new user();
                user.id=chatmessage.conversionid;
                user.name=chatmessage.conversionname;
                user.image=chatmessage.conversionimage;
                conversionlistener.onconversionclicked(user);
            });
        }


    }

    private Bitmap getconversionimage(String encodedimage){
        byte[] bytes= Base64.decode(encodedimage,Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }
}
