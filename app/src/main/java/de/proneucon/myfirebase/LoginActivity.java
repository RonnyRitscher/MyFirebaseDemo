package de.proneucon.myfirebase;
/**
 * denke daran diese in die Manifest zu hinterlegen/als intent zu setzen
 *
 * Firebase-Authentifizierung
 */

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements View .OnClickListener{


    private FirebaseAuth auth; //Declare an instance of FirebaseAuth

    private TextView statusTextView , detailTextView;
    private EditText emailField , passwordField;

    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance(); // initialize the FirebaseAuth instance.
        statusTextView = findViewById(R.id.status);
        detailTextView = findViewById(R.id.detail);
        emailField = findViewById(R.id.fieldEmail);
        passwordField = findViewById(R.id.fieldPassword);

        //Buttons den onClickListener hinzufügen
        findViewById(R.id.emailSignInButton).setOnClickListener(this);
        findViewById(R.id.emailCreateAccountButton).setOnClickListener(this);
        findViewById(R.id.signOutButton).setOnClickListener(this);
        findViewById(R.id.verifyEmailButton).setOnClickListener(this);
    }

    //----------------------


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = auth.getCurrentUser(); // Check if user is signed in (non-null) and update UI accordingly.
        updateUI(currentUser);


    }

    //**************************************************
    //METHODE updateUI
    private void updateUI(FirebaseUser currentUser) {
        hideProgressDialog();   //verstecken der FortschritsAnzeige
        if(currentUser!=null){
            statusTextView.setText(getString(
                    R.string.emailpassword_status_fmt ,
                    currentUser.getEmail() ,
                    currentUser.isEmailVerified()));

            detailTextView.setText(getString(R.string.firebase_status_fmt, currentUser.getUid()));

            findViewById(R.id.emailPasswordButtons).setVisibility(View.GONE);       //PasswortButton nicht anzeigen
            findViewById(R.id.emailPasswordFields).setVisibility(View.GONE);        //PasswortFeld nicht anzeigen
            findViewById(R.id.signedInButtons).setVisibility(View.VISIBLE);         //AnmeldeButton anzeigen

            findViewById(R.id.verifyEmailButton).setEnabled(!currentUser.isEmailVerified());
        }else{
            statusTextView.setText(R.string.signed_out);
            detailTextView.setText(null);

            findViewById(R.id.emailPasswordButtons).setVisibility(View.VISIBLE);    //PasswortButton anzeigen
            findViewById(R.id.emailPasswordFields).setVisibility(View.VISIBLE);     //PasswortFeld anzeigen
            findViewById(R.id.signedInButtons).setVisibility(View.GONE);            //AnmeldeButton nicht anzeigen
        }
    }

    private void showProgressDialog() {
        //Zeigen des Fortschritts
        if(progressDialog==null){ //ggf. erstellen einer neuen FortschritsAnzeige
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.setIndeterminate(true);
        }
        progressDialog.show(); //zeigen des Dialogs
    }

    private void hideProgressDialog() {
        //verstecken der FortschritsAnzeige
        if(progressDialog!=null && progressDialog.isShowing()){
            progressDialog.dismiss(); //
        }
    }

    //**************************************************

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.emailSignInButton:
                signIn(emailField.getText().toString() , passwordField.getText().toString());
                break;
            case R.id.emailCreateAccountButton:
                createAccount(emailField.getText().toString() , passwordField.getText().toString());
                break;
            case R.id.signOutButton:
                signOut();
                break;
            case R.id.verifyEmailButton:
                sendEmailVerification();
                break;

        }
    }

    //ON_CLICK_METHODEN:
    private void signOut() {
        auth.signOut();                     //methodenaufruf: signOut()
        updateUI(null);
    }

    private void signIn(String email, String password) {
        //prüfen
        if( !valdateForm() ){   //methodenaufruf: valdateForm
            return; //wenn nicht valide ist
        }
        showProgressDialog();
        auth.signInWithEmailAndPassword(email , password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //prüfung
                        if(task.isSuccessful()){
                            FirebaseUser user = auth.getCurrentUser();
                            updateUI(user);
                        }else{
                            Toast.makeText(LoginActivity.this, "Authentification failed!", Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        if(!task.isSuccessful()){           //erneute abfrage ob successful
                            statusTextView.setText(R.string.auth_failed);
                        }
                        hideProgressDialog();
                    }
        });
    }

    private void createAccount(String email, String password) {     //ACCOUNT ERSTELLEN
        if( !valdateForm() ){   //methodenaufruf: valdateForm
            return; //wenn nicht valide ist
        }
        showProgressDialog();   //Anzeige
        auth.createUserWithEmailAndPassword(email , password) //erstellt hier einen neuen user
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser user = auth.getCurrentUser();
                            updateUI(user);
                        }else{
                            Toast.makeText(LoginActivity.this, "Authentification failed", Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        hideProgressDialog(); //ProgressBar soll wieder verschwinden
                    }
                });
    }

    private void sendEmailVerification() {  //SENDEN EINER VALIDIERUNG

    }

    private boolean valdateForm() {     //EINGABEN DER EMAIL UND PASSWORT PRÜFEN(validieren)
        boolean valid = true;

        //PRÜFEN der EmailFeld leer ist
        String email = emailField.getText().toString();
        if(TextUtils.isEmpty(email)){                       //prüft ob leerstring/null/oder inhalt besitzt
            emailField.setError("Required!");
            valid=false;
        }else {
            emailField.setError(null);
            valid=true;
        }

        //PRÜFEN der PasswortFeld leer ist
        String password = passwordField.getText().toString();
        if(TextUtils.isEmpty(password)){
            passwordField.setError("Required!");
            valid=false;
        }else {
            passwordField.setError(null);
            valid=true;
        }

        return valid;
    }

    //**************************************************
}
