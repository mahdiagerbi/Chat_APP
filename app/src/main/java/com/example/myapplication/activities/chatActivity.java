package com.example.myapplication.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import com.example.myapplication.adapters.chatapadter;
import com.example.myapplication.adapters.recentconversionadapter;
import com.example.myapplication.databinding.ActivityChatBinding;
import com.example.myapplication.databinding.ItemContainerRecentConversationBinding;
import com.example.myapplication.model.chatmessage;
import com.example.myapplication.model.user;
import com.example.myapplication.network.ApiClient;
import com.example.myapplication.network.ApiServices;
import com.example.myapplication.utilities.constantes;
import com.example.myapplication.utilities.preference;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import javax.annotation.Nonnull;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class chatActivity extends BaseActivity {
    private ActivityChatBinding binding;
    private user receiveruser;
    private List<chatmessage> chatmessages;
    private chatapadter chatapadter;
    private com.example.myapplication.utilities.preference preferenceManager;
    private FirebaseFirestore database;
    private String conversionid=null;
    private Boolean isreceiveravailable =false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        loaduserreciverdetail();
        setListner();
        init();
        listenmessage();
    }
    private void init(){
        preferenceManager=new preference(getApplicationContext());
        chatmessages=new ArrayList<>();
        chatapadter=new chatapadter(
                chatmessages,
                getBitmapfromencoded(receiveruser.image),
                preferenceManager.getString(constantes.KEY_USER_ID)
        );
        binding.chatrecycle.setAdapter(chatapadter);
        database=FirebaseFirestore.getInstance();
    }
    private void sendmessage(){
        HashMap<String,Object> message=new HashMap<>();
        message.put(constantes.KEY_SENDER_ID,preferenceManager.getString(constantes.KEY_USER_ID));
        message.put(constantes.KEY_RECEIVER_ID,receiveruser.id);
        message.put(constantes.KEY_MESSAGE,binding.inputmessage.getText().toString());
        message.put(constantes.KEY_TIMESTAMP,new Date());
        database.collection(constantes.KEY_COLLECTION_CHAT).add(message);
        if (conversionid!=null){
            updateconversion(binding.inputmessage.getText().toString());
        }else {
            HashMap<String,Object> conversion=new HashMap<>();
            conversion.put(constantes.KEY_SENDER_ID,preferenceManager.getString(constantes.KEY_USER_ID));
            conversion.put(constantes.KEY_SENDER_NAME,preferenceManager.getString(constantes.KEY_NAME));
            conversion.put(constantes.KEY_SENDER_IMAGE,preferenceManager.getString(constantes.KEY_IMAGE));
            conversion.put(constantes.KEY_RECEIVER_ID,receiveruser.id);
            conversion.put(constantes.KEY_RECEIVER_NAME,receiveruser.name);
            conversion.put(constantes.KEY_RECEIVER_IMAGE,receiveruser.image);
            conversion.put(constantes.KEY_LAST_MESSAGE,binding.inputmessage.getText().toString());
            conversion.put(constantes.KEY_TIMESTAMP,new Date());
            addconversion(conversion);
        }

        if (!isreceiveravailable){
            try {
                JSONArray tokens=new JSONArray();
                tokens.put(receiveruser.token);
                JSONObject data=new JSONObject();
                data.put(constantes.KEY_USER_ID,preferenceManager.getString(constantes.KEY_USER_ID));
                data.put(constantes.KEY_NAME,preferenceManager.getString(constantes.KEY_NAME));
                data.put(constantes.KEY_FCM_TOKEN,preferenceManager.getString(constantes.KEY_FCM_TOKEN));
                data.put(constantes.KEY_MESSAGE,binding.inputmessage.getText().toString());
                JSONObject body =new JSONObject();
                body.put(constantes.REMOTE_MSG_DATA,data);
                body.put(constantes.REMOTE_MSG_REGISTRATION_IDS,tokens);

                sendnotification(body.toString());

            }catch (Exception exception){
                showtoast(exception.getMessage());
            }
        }
        binding.inputmessage.setText(null);
    }

    private void listeneravailablereceiver(){
        database.collection(constantes.KEY_COLLECTION_USERS).document(
                receiveruser.id
        ).addSnapshotListener(chatActivity.this,(value, error) -> {
            if (error!=null){
                return;
            }
            if (value!=null){
                if (value.getLong(constantes.KEY_AVABILITY)!=null){
                    int avaibalability = Objects.requireNonNull(
                            value.getLong(constantes.KEY_AVABILITY)
                    ).intValue();
                    isreceiveravailable=avaibalability==1;
                }
                receiveruser.token=value.getString(constantes.KEY_FCM_TOKEN);
                if (receiveruser.image==null){
                    receiveruser.image=value.getString(constantes.KEY_IMAGE);
                    chatapadter.setReceiverprofileimage(getBitmapfromencoded(receiveruser.image));
                    chatapadter.notifyItemRangeChanged(0,chatmessages.size());
                }
            }
            if (isreceiveravailable){
                binding.onlinestatue.setVisibility(View.VISIBLE);
            }else {
                binding.onlinestatue.setVisibility(View.GONE);
            }

        });
    }

    private void showtoast(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
    }

    private void sendnotification(String messageBody){
        ApiClient.getClient().create(ApiServices.class).sendmessage(
                constantes.getRemoteheaders(),
                messageBody
        ).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@Nonnull Call<String> call, @Nonnull Response<String> response) {
                if (response.isSuccessful()){
                    try {
                        if (response.body()!=null){
                            JSONObject responseobject=new JSONObject(response.body());
                            JSONArray result=responseobject.getJSONArray("results");
                            if (responseobject.getInt("failure")==1){
                                JSONObject error=(JSONObject) result.get(0);
                                showtoast(error.getString("error"));
                                return;
                            }
                        }

                    }catch (JSONException e){
                        e.printStackTrace();
                    }

                    showtoast("notification sent");

                }else {
                    showtoast("error"+response.code());
                }

            }

            @Override
            public void onFailure(@Nonnull Call<String> call,@Nonnull Throwable t) {
                showtoast(t.getMessage());

            }
        });
    }

    private void listenmessage(){
        database.collection(constantes.KEY_COLLECTION_CHAT)
                .whereEqualTo(constantes.KEY_SENDER_ID,preferenceManager.getString(constantes.KEY_USER_ID))
                .whereEqualTo(constantes.KEY_RECEIVER_ID,receiveruser.id)
                .addSnapshotListener(eventListener);
        database.collection(constantes.KEY_COLLECTION_CHAT)
                .whereEqualTo(constantes.KEY_SENDER_ID,receiveruser.id)
                .whereEqualTo(constantes.KEY_RECEIVER_ID,preferenceManager.getString(constantes.KEY_USER_ID))
                .addSnapshotListener(eventListener);
    }

    private final EventListener<QuerySnapshot> eventListener =(value, error) ->{
        if (error !=null){
            return;
        }
        if (value !=null){
            int count=chatmessages.size();
            for (DocumentChange documentChange : value.getDocumentChanges()){
                if (documentChange.getType()==DocumentChange.Type.ADDED){
                    chatmessage chatmessage=new chatmessage();
                    chatmessage.senderid=documentChange.getDocument().getString(constantes.KEY_SENDER_ID);
                    chatmessage.receiverid=documentChange.getDocument().getString(constantes.KEY_RECEIVER_ID);
                    chatmessage.message=documentChange.getDocument().getString(constantes.KEY_MESSAGE);
                    chatmessage.datetime=getReadabledatetime(documentChange.getDocument().getDate(constantes.KEY_TIMESTAMP));
                    chatmessage.dateobject=documentChange.getDocument().getDate(constantes.KEY_TIMESTAMP);
                    chatmessages.add(chatmessage);
                }

            }

            Collections.sort(chatmessages,(obj1,obj2)->obj1.dateobject.compareTo(obj2.dateobject));
            if (count==0){
                chatapadter.notifyDataSetChanged();
            }else {
                chatapadter.notifyItemRangeInserted(chatmessages.size(),chatmessages.size());
                binding.chatrecycle.smoothScrollToPosition(chatmessages.size()-1);
            }
            binding.chatrecycle.setVisibility(View.VISIBLE);
        }
        binding.progressbar.setVisibility(View.GONE);
        if (conversionid==null){
            checkforconversion();
        }
    };

    private Bitmap getBitmapfromencoded(String encodedimage){
        if (encodedimage!=null){
            byte[] bytes= Base64.decode(encodedimage,Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        }else {
            return null;
        }

    }
    private void loaduserreciverdetail(){
        receiveruser=(user) getIntent().getSerializableExtra(constantes.KEY_USER);
        binding.textname.setText(receiveruser.name);

    }
    private void setListner(){
        binding.imagebak.setOnClickListener(v -> onBackPressed());
        binding.layoutsend.setOnClickListener(v -> sendmessage());
    }

    private String getReadabledatetime(Date date){
        return new SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date);
    }

    private void addconversion(HashMap<String,Object> conversion){
        database.collection(constantes.KEY_CONVERSATIONS)
                .add(conversion)
                .addOnSuccessListener(documentReference -> conversionid=documentReference.getId());
    }

    private void updateconversion(String message){
        DocumentReference documentReference =
                database.collection(constantes.KEY_CONVERSATIONS).document(conversionid);
        documentReference.update(
                constantes.KEY_LAST_MESSAGE,message,
                constantes.KEY_TIMESTAMP,new Date()
        );
    }

    private void checkforconversion(){
        if (chatmessages.size()!=0){
            checkforconversionremotly(
                    preferenceManager.getString(constantes.KEY_USER_ID),
                    receiveruser.id
            );
            checkforconversionremotly(
                    receiveruser.id,
                    preferenceManager.getString(constantes.KEY_USER_ID)
            );
        }
    }

    private void checkforconversionremotly(String senderid,String receiverid){
        database.collection(constantes.KEY_CONVERSATIONS)
                .whereEqualTo(constantes.KEY_SENDER_ID,senderid)
                .whereEqualTo(constantes.KEY_RECEIVER_ID,receiverid)
                .get()
                .addOnCompleteListener(conversiononcompletelistner);
    }

    private final OnCompleteListener<QuerySnapshot> conversiononcompletelistner = task -> {
        if (task.isSuccessful() && task.getResult()!=null&&task.getResult().getDocuments().size()>0){
            DocumentSnapshot documentSnapshot=task.getResult().getDocuments().get(0);
            conversionid= documentSnapshot.getId();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        listeneravailablereceiver();
    }
}