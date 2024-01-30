package fr.vannes.recordstore;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import fr.vannes.recordstore.BO.Collection;
import fr.vannes.recordstore.BO.Record;


public class RemoveFragment extends Fragment {

    public RemoveFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_remove, container, false);

        ScrollView scrollView = new ScrollView(getContext());
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        scrollView.addView(linearLayout);
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
                                for (Record record : resultCollection.getRecords()) {
                                    TextView textView = new TextView(getContext());
                                    textView.setText(record.toString());
                                    textView.setTextSize(18);
                                    textView.setTextColor(Color.BLACK);
                                    linearLayout.addView(textView);

                                    Button removeButton = new Button(getContext());
                                    removeButton.setText("Supprimer");
                                    removeButton.setOnClickListener(v -> {
                                        new AlertDialog.Builder(getContext())
                                                .setTitle("Confirmation de suppression")
                                                .setMessage("Êtes-vous sûr de vouloir supprimer cet enregistrement ?")
                                                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                                                    // Remove the record from the database
                                                    FirebaseDatabase.getInstance().getReference("RecordStore").child("users")
                                                            .child(AddFragment.getUid())
                                                            .child("collection")
                                                            .child(dataSnapshot.getKey())
                                                            .removeValue();
                                                    // Remove the record view from the layout
                                                    linearLayout.removeView(textView);
                                                    linearLayout.removeView(removeButton);
                                                })
                                                .setNegativeButton(android.R.string.no, null)
                                                .setIcon(android.R.drawable.ic_dialog_alert)
                                                .show();
                                    });
                                    ;
                                    linearLayout.addView(removeButton);
                                }
                            } else {
                                Toast.makeText(getContext(), "Erreur lors de la récupération des données", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        String errorMessage = task.getException() != null ? task.getException().getMessage() : "Unknown error";
                        Toast.makeText(getContext(), "Erreur lors de la récupération des données de la collection", Toast.LENGTH_SHORT).show();
                        Log.e("RemoveFragment", "Error while getting collection : " + errorMessage, task.getException());
                    }
                });

        return view;
    }
}