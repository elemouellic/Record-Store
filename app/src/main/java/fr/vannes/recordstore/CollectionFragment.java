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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CollectionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CollectionFragment extends Fragment {


    public CollectionFragment() {
        // Required empty public constructor
    }

//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment CollectionFragment.
//     */
//    // TODO: Rename and change types and number of parameters
//    public static CollectionFragment newInstance(String param1, String param2) {
//        CollectionFragment fragment = new CollectionFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
//    }

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

                                    // Use Glide to load the album cover into the ImageView
                                    Glide.with(requireContext())
                                            .load(record.getPictureURL())
                                            .override(200, 200) // Resize the image
                                            .into(imageView);
                                    gridLayout.addView(imageView);

                                    TextView textView = new TextView(getContext());
                                    textView.setText(record.getTitle());
                                    textView.setTextColor(Color.BLACK); // Set text color to black
                                    gridLayout.addView(textView);
                                }
                            } else {
                                Toast.makeText(getContext(), "Error: Collection or records is null", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        String errorMessage = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                        Toast.makeText(getContext(), "Error while getting collection: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });

        return view;
    }

}