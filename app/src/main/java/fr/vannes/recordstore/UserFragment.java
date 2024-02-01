package fr.vannes.recordstore;

import static androidx.core.app.ActivityCompat.finishAffinity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
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

        userNameTextView.setText(R.string.vous_tes_connect_avec_l_adresse);
        userEmailTextView.setText(userEmail);
    }

logoutButton.setOnClickListener(v -> {
    Toast.makeText(getActivity(), "Déconnexion en cours", Toast.LENGTH_SHORT).show();
    FirebaseAuth.getInstance().signOut();

    // Redirect to Login Activity after successful sign out
    Intent intent = new Intent(getActivity(), LoginActivity.class);
    intent.putExtra("justLoggedOut", true);
    startActivity(intent);

    // Finish current activity
    requireActivity().finish();
});
    return view;
}}