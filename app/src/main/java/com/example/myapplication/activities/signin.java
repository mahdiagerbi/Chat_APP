package com.example.myapplication.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.myapplication.databinding.ActivitySigninBinding;
import com.example.myapplication.utilities.constantes;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;


public class signin extends AppCompatActivity {
    private ActivitySigninBinding binding;
    private com.example.myapplication.utilities.preference preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding =ActivitySigninBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager =new com.example.myapplication.utilities.preference(getApplicationContext());
        if (preferenceManager.getBoolean(constantes.KEY_IS_SIGNED_IN)){
            Intent intent=new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
        setListner();
    }
    private void setListner(){
        binding.nextsignup.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(),signup.class)));
        binding.sendlogin.setOnClickListener(v ->{
            if (isValid()){
                signIn();
            }
        });
    }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
    }
    private void signIn(){
        loading(true);
        FirebaseFirestore database =FirebaseFirestore.getInstance();
        database.collection(constantes.KEY_COLLECTION_USERS)
                .whereEqualTo(constantes.KEY_EMAIL,binding.Email.getText().toString())
                .whereEqualTo(constantes.KEY_PASSWORD,binding.pass.getText().toString())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()&&task.getResult()!=null
                    &&task.getResult().getDocuments().size()>0){
                        DocumentSnapshot documentSnapshot=task.getResult().getDocuments().get(0);
                        preferenceManager.putBoolean(constantes.KEY_IS_SIGNED_IN,true);
                        preferenceManager.putString(constantes.KEY_USER_ID,documentSnapshot.getId());
                        preferenceManager.putString(constantes.KEY_NAME,documentSnapshot.getString(constantes.KEY_NAME));
                        preferenceManager.putString(constantes.KEY_IMAGE,documentSnapshot.getString(constantes.KEY_IMAGE));
                        Intent intent=new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }else {
                        loading(false);
                        showToast("bara mrigel");
                    }
                });

    }

    private void loading(Boolean isLoading){
        if (isLoading){
            binding.sendlogin.setVisibility(View.INVISIBLE);
            binding.progbar.setVisibility(View.VISIBLE);
        }else{
            binding.progbar.setVisibility(View.INVISIBLE);
            binding.sendlogin.setVisibility(View.VISIBLE);
        }
    }
    private Boolean isValid(){
        if (binding.Email.getText().toString().trim().isEmpty()){
            showToast("Enter Email");
            return false;
        }else if (!Patterns.EMAIL_ADDRESS.matcher(binding.Email.getText().toString()).matches()){
            showToast("Enter a valid mail");
            return false;
        }else if (binding.pass.getText().toString().trim().isEmpty()){
            showToast("Enter password");
            return false;
        }else {
            return true;
        }
    }


}