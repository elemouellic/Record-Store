package fr.vannes.recordstore;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import fr.vannes.recordstore.API.APIUtils;
import fr.vannes.recordstore.BO.Artist;
import fr.vannes.recordstore.BO.Collection;
import fr.vannes.recordstore.BO.Record;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, CollectionActivity.class);
            startActivity(intent);
        }



    }


}