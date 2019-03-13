package de.proneucon.myfirebase;
/**
 * denke daran diese in die Manifest zu hinterlegen/als intent zu setzen
 *
 * Firebase-Authentifizierung
 */

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {


    private FirebaseAuth auth; //Declare an instance of FirebaseAuth


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        auth = FirebaseAuth.getInstance(); // initialize the FirebaseAuth instance.
    }

    //----------------------


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = auth.getCurrentUser(); // Check if user is signed in (non-null) and update UI accordingly.
        updateUI(currentUser);


    }

    //METHODE updateUI
    private void updateUI(FirebaseUser currentUser) {
    }
}
