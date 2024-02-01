package fr.vannes.recordstore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);

    // Check if user just logged out
    boolean justLoggedOut = getIntent().getBooleanExtra("justLoggedOut", false);
    if (justLoggedOut) {
        // User just logged out, remove the extra
        getIntent().removeExtra("justLoggedOut");
    }

    // Check if a user is already signed in
    if (FirebaseAuth.getInstance().getCurrentUser() == null) {
        // No user is signed in, show sign in UI
        createSignInIntent();
    }
}
    public void createSignInIntent() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                // Successfully signed in
                boolean justLoggedOut = getIntent().getBooleanExtra("justLoggedOut", false);
                if(justLoggedOut){
                    // Redirect to login activity after logout
                    getIntent().removeExtra("justLoggedOut");
                } else {
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                }

                // save user in database if not already saved
                if(FirebaseAuth.getInstance().getCurrentUser() != null){
                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    String name = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                    String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    Map<String, Object> user = new HashMap<>();
                    user.put("uid", uid);
                    user.put("name", name);
                    user.put("email", email);

                    db.collection("users").document(uid).set(user)
                            .addOnSuccessListener(aVoid -> Log.d("LoginActivity", "User successfully written!"))
                            .addOnFailureListener(e -> Log.w("LoginActivity", "Error writing user", e));
                }

                finish();

            } else {
                // Sign in failed
                Log.e("LoginActivity", "Sign in failed");
            }
        }
    }
}