package fr.vannes.recordstore;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import static fr.vannes.recordstore.R.*;
import static fr.vannes.recordstore.R.id.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import fr.vannes.recordstore.API.APIUtils;
import fr.vannes.recordstore.BO.Collection;
import fr.vannes.recordstore.BO.Record;

public class AddFragment extends Fragment {
    private Activity activity;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private String userAgent = "Record Store/1.0 (by elemouellic)";

    private static final int BARCODE_READER_REQUEST_CODE = 1;

    private EditText editTextBarcode;
    private Button buttonSearch;
    private Button buttonScan;
    private TextView textViewAlbum;
    private Button buttonAdd;
    private ProgressBar progressBar;
    private ImageView imageViewAlbumCover;

    private String barcode;

    public AddFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof Activity) {
            activity = (Activity) context;
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(layout.fragment_add, container, false);

        buttonScan = view.findViewById(R.id.btn_scan);

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("RecordStore");

        editTextBarcode = view.findViewById(id.editTextBarcode);
        buttonSearch = view.findViewById(btn_search);
        textViewAlbum = view.findViewById(textViewAlbumInfo);
        buttonAdd = view.findViewById(btn_add);
        progressBar = view.findViewById(id.progressBar);
        imageViewAlbumCover = view.findViewById(R.id.imageViewAlbumCover);

        buttonScan.setOnClickListener(v -> {
            IntentIntegrator.forSupportFragment(this).initiateScan();
        });

        buttonSearch.setOnClickListener(v -> {
            barcode = editTextBarcode.getText().toString();
            handleBarcode(barcode);
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(getActivity(), "Scan annulé", Toast.LENGTH_SHORT).show();
            } else {
                String barcode = result.getContents();
                editTextBarcode.setText(barcode);
                handleBarcode(barcode);

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * This method handles the barcode entered by the user.
     *
     * @param barcode The barcode entered by the user.
     */
    private void handleBarcode(String barcode) {
        // Add a check to make sure the barcode is at least 5 characters long
        if (barcode.length() < 5) {
            Toast.makeText(getActivity(), "Veuillez entrer un code-barres valide", Toast.LENGTH_SHORT).show();
        } else {
            progressBar.setVisibility(View.VISIBLE);
            try {
                // Run the API call in a separate thread
                APIUtils.runAsync(barcode, userAgent, new APIUtils.OnRecordFetchedListener() {
                    @Override
                    public void onRecordFetched(Record record) {
                        requireActivity().runOnUiThread(() -> {
                            progressBar.setVisibility(View.GONE);
                            if (record != null) {
                                textViewAlbum.setText(record.toString());

                                String pictureURL = record.getPictureURL();
                                if (pictureURL != null) {
                                    Glide.with(requireActivity())
                                            .load(record.getPictureURL())
                                            .into(imageViewAlbumCover);
                                } else {
                                    pictureURL = "https://simplyahouse.elemouellic.tech/public/img/emptycover.png";
                                    Toast.makeText(activity, "Image non disponible", Toast.LENGTH_SHORT).show();
                                }

                                buttonAdd.setOnClickListener(v -> {
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
                                });
                            } else {
                                Toast.makeText(activity, "Aucun disque trouvé pour ce code-barres", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onError(Exception e) {
                        if (isAdded() && activity != null) { // Check if the fragment is added to the activity and activity is not null
                            activity.runOnUiThread(() -> {
                                Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Create a collection for the user
     *
     * @param records The list of records to add to the collection
     * @param userId  The user ID
     */
    public void createCollectionForUser(List<Record> records, String userId) {
        // Instantiate a new Collection object
        Collection collection = new Collection(userId, records);

        DatabaseReference userCollectionsRef = databaseReference.child("users").child(userId).child("collection");

        String barcodeToCheck = records.get(0).getBarcode();

        // Verify if the barcode already exists in the user's collection
        userCollectionsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean barcodeExists = false;

                for (DataSnapshot collectionSnapshot : dataSnapshot.getChildren()) {
                    DataSnapshot recordsSnapshot = collectionSnapshot.child("records");

                    for (DataSnapshot recordSnapshot : recordsSnapshot.getChildren()) {
                        String barcode = recordSnapshot.child("barcode").getValue(String.class);

                        if (barcode != null && barcode.equals(barcodeToCheck)) {
                            // Barcode exists in the user's collection already
                            barcodeExists = true;
                            break;
                        }
                    }

                    if (barcodeExists) {
                        break;
                    }
                }

                if (barcodeExists) {
                    // Barcode exists in the user's collection already - show error message
                    Toast.makeText(getContext(), "Le disque existe déjà dans votre collection", Toast.LENGTH_SHORT).show();
                    Log.e("Firebase", "Le code-barres existe déjà dans la collection de l'utilisateur");
                } else {
                    // Barcode does not exist in the user's collection - add it
                    DatabaseReference newCollectionRef = userCollectionsRef.push();
                    newCollectionRef.setValue(collection);
                    Toast.makeText(getContext(), "Ajout du disque dans votre collection", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Erreur lors de la vérification du code-barres dans la collection de l'utilisateur");
            }
        });
    }

    /**
     * Get the user ID
     *
     * @return The user ID
     */
    public static String getUid() {
        return Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    }
}