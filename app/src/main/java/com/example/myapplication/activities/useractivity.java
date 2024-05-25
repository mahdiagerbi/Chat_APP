package com.example.myapplication.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.myapplication.adapters.useradapter;
import com.example.myapplication.databinding.ActivityUseractivityBinding;
import com.example.myapplication.listeners.userlistener;
import com.example.myapplication.model.user;
import com.example.myapplication.utilities.constantes;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class useractivity extends BaseActivity implements userlistener {
    private ActivityUseractivityBinding binding;
    private com.example.myapplication.utilities.preference preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityUseractivityBinding.inflate(getLayoutInflater());
        preferenceManager =new com.example.myapplication.utilities.preference(getApplicationContext());
        setContentView(binding.getRoot());
        setListner();
        getdata();
    }
    private void setListner(){
        binding.imageback.setOnClickListener(v -> onBackPressed());
    }
    private void getdata(){
        loading(true);
        FirebaseFirestore database=FirebaseFirestore.getInstance();
        database.collection(constantes.KEY_COLLECTION_USERS)
                .get()
                .addOnCompleteListener(task -> {
                    loading(false);
                    String currentuser =preferenceManager.getString(constantes.KEY_USER_ID);
                    if (task.isSuccessful()&&task.getResult()!=null){
                        List<user> users=new ArrayList<>();
                        for (QueryDocumentSnapshot queryDocumentSnapshot: task.getResult()){
                            if (currentuser.equals(queryDocumentSnapshot.getId())){
                                continue;
                            }
                            user user=new user();
                            user.name=queryDocumentSnapshot.getString(constantes.KEY_NAME);
                            user.email=queryDocumentSnapshot.getString(constantes.KEY_EMAIL);
                            user.image=queryDocumentSnapshot.getString(constantes.KEY_IMAGE);
                            user.token=queryDocumentSnapshot.getString(constantes.KEY_FCM_TOKEN);
                            user.id=queryDocumentSnapshot.getId();
                            users.add(user);
                        }
                        if (users.size()>0){
                            useradapter useradapter=new useradapter(users,this);
                            binding.userrecycle.setAdapter(useradapter);
                            binding.userrecycle.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }
    private void showerror(){
        binding.texterror.setText(String.format("there is no user"));
        binding.texterror.setVisibility(View.VISIBLE);
    }
    private void loading(Boolean isLoading){
        if (isLoading){
            binding.progressbar.setVisibility(View.VISIBLE);
        }else {
            binding.progressbar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onuserclicked(user user) {
        Intent intent=new Intent(getApplicationContext(), chatActivity.class);
        intent.putExtra(constantes.KEY_USER,user);
        startActivity(intent);
        finish();
    }
}