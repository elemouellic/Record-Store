package fr.vannes.recordstore;

import static fr.vannes.recordstore.R.*;
import static fr.vannes.recordstore.R.id.*;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import fr.vannes.recordstore.API.APIUtils;
import fr.vannes.recordstore.BO.Collection;
import fr.vannes.recordstore.BO.Record;


public class CollectionFragment extends Fragment {


    public CollectionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(layout.fragment_collection, container, false);

        ScrollView scrollView = new ScrollView(getContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, // Largeur
                LinearLayout.LayoutParams.MATCH_PARENT); // Hauteur
        scrollView.setLayoutParams(layoutParams);
        Collection collection = new Collection("", new ArrayList<>());

        GridLayout gridLayout = new GridLayout(getContext());
        gridLayout.setColumnCount(2);
        scrollView.addView(gridLayout);
        ViewGroup rootView = (ViewGroup) view;
        rootView.addView(scrollView);


        FirebaseDatabase.getInstance().getReference("RecordStore").child("users")
                .child(AddFragment.getUid())
                .child("collection")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                            Collection resultCollection = dataSnapshot.getValue(Collection.class);
                            if (resultCollection != null && resultCollection.getRecords() != null) {
                                collection.setRecords(new ArrayList<>(resultCollection.getRecords()));

                                for (Record record : collection.getRecords()) {
                                    Log.d("CollectionFragment", "Record title: " + record.getTitle()); // Log the record title

                                    // Create a new ImageView for the album cover
                                    ImageView imageView = new ImageView(getContext());
                                    imageView.setLayoutParams(new ViewGroup.LayoutParams(300, 300)); // Set the size of the ImageView
                                    imageView.setPadding(10, 10, 10, 10);
                                    // Use Glide to load the album cover into the ImageView
                                    Glide.with(requireContext())
                                            .load(record.getPictureURL())
                                            .override(200, 200) // Resize the image
                                            .into(imageView);
                                    gridLayout.addView(imageView);

                                    TextView textView = new TextView(getContext());
                                    textView.setText(record.toString());

                                    textView.setTextSize(18); // Set text size to 20
                                    textView.setTextColor(Color.BLACK); // Set text color to black
                                    gridLayout.addView(textView);
                                }
                            } else {
                                Toast.makeText(getContext(), "Erreur lors de la récupération des données", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        String errorMessage = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                        Toast.makeText(getContext(), "Erreur lors de la récupération des données de la collection", Toast.LENGTH_SHORT).show();
                        Log.e("CollectionFragment", "Error while getting collection : " + errorMessage, task.getException());
                    }
                });

        return view;
    }

}