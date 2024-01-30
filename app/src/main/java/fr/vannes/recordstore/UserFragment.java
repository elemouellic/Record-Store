package fr.vannes.recordstore;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;


public class UserFragment extends Fragment {


    public UserFragment() {
        // Required empty public constructor
    }


@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container,
                         Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_user, container, false);

    TextView userNameTextView = view.findViewById(R.id.userName);
    TextView userEmailTextView = view.findViewById(R.id.userEmail);
    Button logoutButton = view.findViewById(R.id.logoutButton);

    if (FirebaseAuth.getInstance().getCurrentUser() != null) {
//        String userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        userNameTextView.setText("Vous êtes connecté avec l'adresse :");
        userEmailTextView.setText(userEmail);
    }

logoutButton.setOnClickListener(v -> {
    FirebaseAuth.getInstance().signOut();

});
    return view;
}}