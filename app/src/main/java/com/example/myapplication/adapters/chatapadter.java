package com.example.myapplication.adapters;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.databinding.ItemContainerReceiverMessageBinding;
import com.example.myapplication.databinding.ItemContainerSentMessageBinding;
import com.example.myapplication.model.chatmessage;

import java.util.List;

public class chatapadter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<chatmessage> chatmessages;
    private Bitmap receiverprofileimage;
    private final String senderid;
    public static final int view_type_sent=1;
    public static final int view_type_received=2;

    public void setReceiverprofileimage(Bitmap bitmap){
        receiverprofileimage=bitmap;
    }

    public chatapadter(List<chatmessage> chatmessages, Bitmap receiverprofileimage, String senderid) {
        this.chatmessages = chatmessages;
        this.receiverprofileimage = receiverprofileimage;
        this.senderid = senderid;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType==view_type_sent){
            return new sentmessageviewholder(
                    ItemContainerSentMessageBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false
                    )
            );
        }else {
            return new receivermessageviewholder(
                    ItemContainerReceiverMessageBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false
                    )
            );
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position)==view_type_sent){
            ((sentmessageviewholder)holder).setdata(chatmessages.get(position));
        }else {
            ((receivermessageviewholder)holder).setdata(chatmessages.get(position),receiverprofileimage);
        }

    }

    @Override
    public int getItemCount() {
        return chatmessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (chatmessages.get(position).senderid.equals(senderid)){
            return view_type_sent;
        }else {
            return view_type_received;
        }
    }

    static class sentmessageviewholder extends RecyclerView.ViewHolder {
        private ItemContainerSentMessageBinding binding;
        sentmessageviewholder(ItemContainerSentMessageBinding itemContainerSentMessageBinding){
            super(itemContainerSentMessageBinding.getRoot());
            binding=itemContainerSentMessageBinding;
        }
        void setdata(chatmessage chatmessage){
            binding.textmessage.setText(chatmessage.message);
            binding.textdatetime.setText(chatmessage.datetime);
        }
    }

    static class receivermessageviewholder extends RecyclerView.ViewHolder{
        private ItemContainerReceiverMessageBinding binding;
        receivermessageviewholder(ItemContainerReceiverMessageBinding itemContainerReceiverMessageBinding){
            super(itemContainerReceiverMessageBinding.getRoot());
            binding=itemContainerReceiverMessageBinding;
        }

        void setdata(chatmessage chatmessage,Bitmap receiverprofileimage){
            binding.textmessage.setText(chatmessage.message);
            binding.textdatetime.setText(chatmessage.datetime);
            if (receiverprofileimage!=null){
                binding.imageprofil.setImageBitmap(receiverprofileimage);
            }

        }
    }
}
