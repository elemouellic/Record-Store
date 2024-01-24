package fr.vannes.recordstore;

import androidx.appcompat.app.AppCompatActivity;

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

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private String userAgent = "Record Store/1.0 (by elemouellic)";



//    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("RecordStore");
        String barcode = "3700187665388";


        try {
            APIUtils.runAsync(barcode, userAgent, new APIUtils.OnRecordFetchedListener() {
                @Override
                public void onRecordFetched(Record record) {
                    Log.e("Firebase", "Record fetched: " + record.getTitle());
                    databaseReference.child("records").child(record.getTitle());
                    databaseReference.child("records").child(record.getTitle()).child("id").setValue(record.getId());
                    databaseReference.child("records").child(record.getTitle()).child("title").setValue(record.getTitle());
                    databaseReference.child("records").child(record.getTitle()).child("cover").setValue(record.getPictureURL());
                    databaseReference.child("records").child(record.getTitle()).child("artist").setValue(record.getArtist());
                    databaseReference.child("records").child(record.getTitle()).child("type").setValue(record.getType());
                    List<Record> records = new ArrayList<>();
                    records.add(record);
                    createCollectionForUser(records, "kposseme");
                }

                @Override
                public void onError(Exception e) {
                    Log.e("Firebase", "Error: " + e.getMessage());
                }



            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void createCollectionForUser(List<Record> records, String userId) {
        // Créer une nouvelle collection avec les enregistrements
        Collection collection = new Collection(userId, records);

        // Utiliser push() pour générer une nouvelle clé unique pour chaque enregistrement
        DatabaseReference userCollectionsRef = databaseReference.child("users").child(userId).child("collection");
        DatabaseReference newCollectionRef = userCollectionsRef.push();

        // Set the value under the new key
        newCollectionRef.setValue(collection);
    }


}