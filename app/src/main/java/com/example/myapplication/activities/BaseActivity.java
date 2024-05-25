package com.example.myapplication.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.utilities.preference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.myapplication.utilities.constantes;

public class BaseActivity extends AppCompatActivity {

    private DocumentReference documentReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preference preferenceManager =new preference(getApplicationContext());
        FirebaseFirestore database=FirebaseFirestore.getInstance();
        documentReference=database.collection(constantes.KEY_COLLECTION_USERS)
                .document(preferenceManager.getString(constantes.KEY_USER_ID));
    }

    @Override
    protected void onPause() {
        super.onPause();
        documentReference.update(constantes.KEY_AVABILITY,0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        documentReference.update(constantes.KEY_AVABILITY,1);
    }
}
