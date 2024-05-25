package com.example.myapplication.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.myapplication.databinding.ActivitySignupBinding;
import com.example.myapplication.utilities.constantes;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

public class signup extends AppCompatActivity {
    private ActivitySignupBinding binding;
    private com.example.myapplication.utilities.preference preferenceManager;
    private String encodedimage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding =ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager =new com.example.myapplication.utilities.preference(getApplicationContext());
        setListner();
    }
    private void setListner(){
        binding.nextsignin.setOnClickListener(v -> onBackPressed());
        binding.sendsignup.setOnClickListener(v -> {
            if (isvalid()){
                signup();
            }
        });
        binding.layoutimage.setOnClickListener(v -> {
            Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickimage.launch(intent);
        });
    }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
    }
    private void signup(){
        loading(true);
        FirebaseFirestore database=FirebaseFirestore.getInstance();
        HashMap<String,Object> user=new HashMap<>();
        user.put(constantes.KEY_NAME,binding.name.getText().toString());
        user.put(constantes.KEY_EMAIL,binding.Email.getText().toString());
        user.put(constantes.KEY_PASSWORD,binding.pass.getText().toString());
        user.put(constantes.KEY_IMAGE,encodedimage);
        database.collection(constantes.KEY_COLLECTION_USERS)
                .add(user)
                .addOnSuccessListener(documentReference -> {
                    loading(false);
                    preferenceManager.putBoolean(constantes.KEY_IS_SIGNED_IN,true);
                    preferenceManager.putString(constantes.KEY_USER_ID, documentReference.getId());
                    preferenceManager.putString(constantes.KEY_NAME,binding.name.getText().toString());
                    preferenceManager.putString(constantes.KEY_IMAGE,encodedimage);
                    Intent intent=new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);

                }).addOnFailureListener(exception -> {
                    loading(false);
                    showToast(exception.getMessage());

                });

    }

    private String encodedimage(Bitmap bitmap){
        int previewWidth=150;
        int previewHeight=bitmap.getHeight()*previewWidth/bitmap.getWidth();
        Bitmap previewBitmap=Bitmap.createScaledBitmap(bitmap,previewWidth,previewHeight,false);
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
        byte[] bytes=byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes,Base64.DEFAULT);
    }
    private ActivityResultLauncher<Intent> pickimage=registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode()==RESULT_OK){
                    if (result.getData() !=null){
                        Uri Imageuri=result.getData().getData();
                        try {
                            InputStream inputStream=getContentResolver().openInputStream(Imageuri);
                            Bitmap bitmap= BitmapFactory.decodeStream(inputStream);
                            binding.imageprofile.setImageBitmap(bitmap);
                            binding.textimage.setVisibility(View.GONE);
                            encodedimage=encodedimage(bitmap);

                        }catch (FileNotFoundException e){
                            e.printStackTrace();
                        }
                    }
                }
            }
    );

    private boolean isvalid(){
        if (encodedimage==null){
            showToast("Please select your image");
            return false;
        }else if (binding.name.getText().toString().trim().isEmpty()){
            showToast("Enter your name please");
            return false;
        }else if (binding.Email.getText().toString().trim().isEmpty()){
            showToast("Enter your Email");
            return false;
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(binding.Email.getText().toString()).matches()){
            showToast("Enter valid mail");
            return false;
        }
        else if (binding.pass.getText().toString().trim().isEmpty()){
            showToast("Enter your password");
            return false;
        }else if (binding.confirmpass.getText().toString().trim().isEmpty()){
            showToast("Confirm your password");
            return false;
        }else if (!binding.pass.getText().toString().equals(binding.confirmpass.getText().toString())){
            showToast("pass and confirm pass must the same");
            return false;
        }else {
            return true;
        }

    }

    private void loading(boolean isLoading){
        if (isLoading){
            binding.sendsignup.setVisibility(View.INVISIBLE);
            binding.progress.setVisibility(View.VISIBLE);
        }else {
            binding.progress.setVisibility(View.INVISIBLE);
            binding.sendsignup.setVisibility(View.VISIBLE);
        }
    }
}