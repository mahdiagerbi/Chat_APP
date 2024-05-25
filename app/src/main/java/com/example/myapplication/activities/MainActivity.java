package com.example.myapplication.activities;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import com.example.myapplication.adapters.recentconversionadapter;
import com.example.myapplication.databinding.ActivityMainBinding;
import com.example.myapplication.listeners.conversionlistener;
import com.example.myapplication.model.chatmessage;
import com.example.myapplication.model.user;
import com.example.myapplication.utilities.constantes;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends BaseActivity implements conversionlistener {
    private ActivityMainBinding binding;
    private com.example.myapplication.utilities.preference preferenceManager;
    private List<chatmessage> conversations;
    private recentconversionadapter conversionadapter;
    private FirebaseFirestore database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        preferenceManager =new com.example.myapplication.utilities.preference(getApplicationContext());
        setContentView(binding.getRoot());
        loaduserdata();
        gettoken();
        setListner();
        init();
        listenconversation();
    }

    private void init(){
        conversations=new ArrayList<>();
        conversionadapter=new recentconversionadapter(conversations,this);
        binding.conversionrecycle.setAdapter(conversionadapter);
        database=FirebaseFirestore.getInstance();
    }

    private void setListner(){
        binding.imagelogout.setOnClickListener(view -> signout());
        binding.newchat.setOnClickListener(view ->
                startActivity(new Intent(getApplicationContext(),useractivity.class)));
    }

    private void loaduserdata(){
        binding.textname.setText(preferenceManager.getString(constantes.KEY_NAME));
        byte[] bytes= Base64.decode(preferenceManager.getString(constantes.KEY_IMAGE),Base64.DEFAULT);
        Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        binding.imageprofile.setImageBitmap(bitmap);
    }

    private void showToast(String mesaage){
        Toast.makeText(getApplicationContext(),mesaage,Toast.LENGTH_SHORT).show();
    }

    private void listenconversation(){
        database.collection(constantes.KEY_CONVERSATIONS)
                .whereEqualTo(constantes.KEY_SENDER_ID,preferenceManager.getString(constantes.KEY_USER_ID))
                .addSnapshotListener(eventListener);
        database.collection(constantes.KEY_CONVERSATIONS)
                .whereEqualTo(constantes.KEY_RECEIVER_ID,preferenceManager.getString(constantes.KEY_USER_ID))
                .addSnapshotListener(eventListener);
    }

    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if (error!=null){
            return;
        }
        if (value!=null){
            for (DocumentChange documentChange : value.getDocumentChanges()){
                if (documentChange.getType()== DocumentChange.Type.ADDED){
                    String senderid=documentChange.getDocument().getString(constantes.KEY_SENDER_ID);
                    String receiverid=documentChange.getDocument().getString(constantes.KEY_RECEIVER_ID);
                    chatmessage chatmessage=new chatmessage();
                    chatmessage.senderid=senderid;
                    chatmessage.receiverid=receiverid;
                    if (preferenceManager.getString(constantes.KEY_USER_ID).equals(senderid)){
                        chatmessage.conversionimage=documentChange.getDocument().getString(constantes.KEY_RECEIVER_IMAGE);
                        chatmessage.conversionname=documentChange.getDocument().getString(constantes.KEY_RECEIVER_NAME);
                        chatmessage.conversionid=documentChange.getDocument().getString(constantes.KEY_RECEIVER_ID);
                    }else {
                        chatmessage.conversionimage=documentChange.getDocument().getString(constantes.KEY_SENDER_IMAGE);
                        chatmessage.conversionname=documentChange.getDocument().getString(constantes.KEY_SENDER_NAME);
                        chatmessage.conversionid=documentChange.getDocument().getString(constantes.KEY_SENDER_ID);

                    }
                    chatmessage.message=documentChange.getDocument().getString(constantes.KEY_LAST_MESSAGE);
                    chatmessage.dateobject=documentChange.getDocument().getDate(constantes.KEY_TIMESTAMP);
                    conversations.add(chatmessage);
                }else if (documentChange.getType()== DocumentChange.Type.MODIFIED){
                    for (int i=0;i<conversations.size();i++){
                        String senderid=documentChange.getDocument().getString(constantes.KEY_SENDER_ID);
                        String receiverid=documentChange.getDocument().getString(constantes.KEY_RECEIVER_ID);
                        if (conversations.get(i).senderid.equals(senderid)&&conversations.get(i).receiverid.equals(receiverid)){
                            conversations.get(i).message=documentChange.getDocument().getString(constantes.KEY_LAST_MESSAGE);
                            conversations.get(i).dateobject=documentChange.getDocument().getDate(constantes.KEY_TIMESTAMP);
                            break;
                        }
                    }

                }
            }

            Collections.sort(conversations,(obj1, obj2)->obj2.dateobject.compareTo(obj1.dateobject));
            conversionadapter.notifyDataSetChanged();
            binding.conversionrecycle.smoothScrollToPosition(0);
            binding.conversionrecycle.setVisibility(View.VISIBLE);
            binding.progressbar.setVisibility(View.GONE);

        }
    };

    private void gettoken(){
        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(this::updateToken);
    }

    private void updateToken(String token){
        preferenceManager.putString(constantes.KEY_FCM_TOKEN,token);
        FirebaseFirestore database=FirebaseFirestore.getInstance();
        DocumentReference documentReference=
                database.collection(constantes.KEY_COLLECTION_USERS).document(
                        preferenceManager.getString(constantes.KEY_USER_ID)
                );
        documentReference.update(constantes.KEY_FCM_TOKEN,token)
                .addOnFailureListener(e -> showToast("la mouch mrigel"));
    }
    private void signout(){
        showToast("bye bye");
        FirebaseFirestore database=FirebaseFirestore.getInstance();
        DocumentReference documentReference=
                database.collection(constantes.KEY_COLLECTION_USERS)
                        .document(preferenceManager.getString(constantes.KEY_USER_ID));
        HashMap<String,Object> update=new HashMap<>();
        update.put(constantes.KEY_FCM_TOKEN, FieldValue.delete());
        documentReference.update(update)
                .addOnSuccessListener(unused -> {
                    preferenceManager.clear();
                    startActivity(new Intent(getApplicationContext(),signin.class));
                    finish();
                })
                .addOnFailureListener(e -> showToast("manich 5arej"));
    }

    @Override
    public void onconversionclicked(user user) {
        Intent intent=new Intent(getApplicationContext(), chatActivity.class);
        intent.putExtra(constantes.KEY_USER,user);
        startActivity(intent);
    }
}