package fr.vannes.recordstore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import fr.vannes.recordstore.API.APIUtils;
import fr.vannes.recordstore.BO.Collection;
import fr.vannes.recordstore.BO.Record;

public class CollectionActivity extends AppCompatActivity {


    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private String userAgent = "Record Store/1.0 (by elemouellic)";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("RecordStore");
        String barcode = "5414939920783";


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
                    databaseReference.child("records").child(record.getTitle()).child("barcode").setValue(record.getBarcode());


                    List<Record> records = new ArrayList<>();
                    records.add(record);


                    createCollectionForUser(records, getUid());
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

        DatabaseReference userCollectionsRef = databaseReference.child("users").child(userId).child("collection");

        String barcodeToCheck = records.get(0).getBarcode();

        // Vérifier si le code-barres existe déjà dans la collection de l'utilisateur
        userCollectionsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean barcodeExists = false;

                for (DataSnapshot collectionSnapshot : dataSnapshot.getChildren()) {
                    DataSnapshot recordsSnapshot = collectionSnapshot.child("records");

                    for (DataSnapshot recordSnapshot : recordsSnapshot.getChildren()) {
                        String barcode = recordSnapshot.child("barcode").getValue(String.class);

                        if (barcode != null && barcode.equals(barcodeToCheck)) {
                            // Le code-barres existe déjà
                            barcodeExists = true;
                            break;
                        }
                    }

                    if (barcodeExists) {
                        break;
                    }
                }

                if (barcodeExists) {
                    // Le code-barres existe déjà, tu peux gérer cela comme tu le souhaites (par exemple, afficher un message d'erreur)
                    Toast.makeText(CollectionActivity.this, "Le disque existe déjà dans votre collection", Toast.LENGTH_SHORT).show();
                    Log.e("Firebase", "Le code-barres existe déjà dans la collection de l'utilisateur");
                } else {
                    // Le code-barres n'existe pas, ajout du nouvel enregistrement
                    DatabaseReference newCollectionRef = userCollectionsRef.push();
                    newCollectionRef.setValue(collection);
                    Toast.makeText(CollectionActivity.this, "Ajout du disque dans votre collection", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Erreur lors de la vérification du code-barres dans la collection de l'utilisateur");
            }
        });
    }





    public String getUid() {
        return Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    }

}