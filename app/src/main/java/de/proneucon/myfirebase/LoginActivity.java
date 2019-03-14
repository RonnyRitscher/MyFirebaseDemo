package de.proneucon.myfirebase;
/**
 * denke daran diese in die Manifest zu hinterlegen/als intent zu setzen
 *
 * Firebase-Authentifizierung
 */

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

    private TextView statusTextView , detailTextView;
    private EditText emailField , passwordField;
    private ProgressDialog progressDialog;
    private FirebaseAuth auth; //Declare an instance of FirebaseAuth
    private Boolean fromChat;  //Testet ob jemand sich ausgelogt hat um erneuten aufruf von is
    private Menu menu;          //zugriff aus das Menu

    private static final String TAG = LoginActivity.class.getSimpleName(); //LOG-TAG



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if(savedInstanceState==null){
            fromChat = false;   //Testet ob das zurückkehren aus der ChatActivity
            Log.d(TAG, "onCreate: fromChat = false");
        }else {
            fromChat = savedInstanceState.getBoolean("fromChat");   //info aus der SIS
            Log.d(TAG, "onCreate:  fromChat (SAVEINSTANCESTATE= " + savedInstanceState.getBoolean("fromChat"));
        }

        auth = FirebaseAuth.getInstance(); // initialize the FirebaseAuth instance.

        //initialisieren der Views/Fields
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
    protected void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState: ");
        super.onSaveInstanceState(outState);
        outState.putBoolean("fromChat" , fromChat); //setzt den Key "fromChat" in dem SaveInstanceState auf den wert
        Log.d(TAG, "onSaveInstanceState: outState.putBoolean(\"fromChat\" , fromChat) ");
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart: ");
        super.onStart();
        //FirebaseUser currentUser = auth.getCurrentUser(); // Check if et_user is signed in (non-null) and update UI accordingly.
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: ");
        super.onResume();
        updateUI(auth.getCurrentUser());
    }

    //**************************************************

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu: ");
        super.onCreateOptionsMenu(menu);
        //Menu einbinden über den menuinflater
        getMenuInflater().inflate(R.menu.menu_login, menu);
        this.menu = menu;                               //übergebe das Menu Global, damit wir damit arbeiten können
        if(auth.getCurrentUser()==null){
            //TEste beim starten ob der User angemeldet ist und verstecke das MenuItem
            menu.findItem(R.id.it_toChat).setVisible(false);
        }
        return true;
    }



    //**********************


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected: ");
        switch (item.getItemId()){
            case R.id.it_toChat:
                //Weiterleitung zum Chat:
                FirebaseUser currentUser = auth.getCurrentUser();
                if(currentUser != null){
                    Log.d(TAG, "onOptionsItemSelected: currentUser != null");
                    if( currentUser.isEmailVerified() && !fromChat ){       //Testet ob verifiziert und ob der User aus der ChatActivity kommt
                        Log.d(TAG, "onOptionsItemSelected: currentUser.isEmailVerified() && !fromChat");
                        Intent intent = new Intent(this , ChatActivity.class);
                        fromChat=true;
                        Log.d(TAG, "onOptionsItemSelected: fromChat=true;");
                        Log.d(TAG, "onOptionsItemSelected: startActivity ChatActivity");
                        startActivity(intent);
                    }
                }else {
                    Toast.makeText(this, "Bitte einloggen!", Toast.LENGTH_SHORT).show();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //**************************************************
    //METHODE updateUI
    private void updateUI(FirebaseUser currentUser) {
        //Log.d(TAG, "updateUI: ");
        hideProgressDialog();   //verstecken der FortschritsAnzeige

        Log.d(TAG, "updateUI: " + fromChat);
        if(currentUser!=null){
            //WEITERLEITUNG von dem LogIn zum AppContent/ChatApp
            if( currentUser.isEmailVerified() && !fromChat ){       //Testet ob verifiziert und ob der User aus der ChatActivity kommt
                Intent intent = new Intent(this , ChatActivity.class);
                fromChat=true;
                Log.d(TAG, "updateUI: startActivity ChatActivity");
                startActivity(intent);
                
            }else {
                fromChat=false;
            }

            //Anzeigen der USER-DATEN:
            statusTextView.setText(getString(
                    R.string.emailpassword_status_fmt ,
                    currentUser.getEmail() ,
                    currentUser.isEmailVerified()));    //USER-EMAIL wird angezeigt
            detailTextView.setText(getString(R.string.firebase_status_fmt, currentUser.getUid()));      //USER-ID wird angezeigt

            //MENU bearbeiten
            if(menu!=null){
                Log.d(TAG, "updateUI: MenüItem wird angezeigt ");
                menu.findItem(R.id.it_toChat).setVisible(true); //MenuItem anzeigen
            }


            //ANZEIGEN DER BUTTONS:
            findViewById(R.id.emailPasswordButtons).setVisibility(View.GONE);       //PasswortButton nicht anzeigen
            findViewById(R.id.emailPasswordFields).setVisibility(View.GONE);        //PasswortFeld nicht anzeigen
            findViewById(R.id.signedInButtons).setVisibility(View.VISIBLE);         //AnmeldeButton anzeigen

            findViewById(R.id.verifyEmailButton).setEnabled(!currentUser.isEmailVerified());
        }else{

            statusTextView.setText(R.string.signed_out);
            detailTextView.setText(null);

            if(menu!=null){
                Log.d(TAG, "updateUI: MenüItem wird nicht angezeigt ");
                menu.findItem(R.id.it_toChat).setVisible(false); //MenuItem nicht anzeigen
            }

            findViewById(R.id.emailPasswordButtons).setVisibility(View.VISIBLE);    //PasswortButton anzeigen
            findViewById(R.id.emailPasswordFields).setVisibility(View.VISIBLE);     //PasswortFeld anzeigen
            findViewById(R.id.signedInButtons).setVisibility(View.GONE);            //AnmeldeButton nicht anzeigen
        }
    }

    private void showProgressDialog() {
        Log.d(TAG, "showProgressDialog: ");
        //Zeigen des Fortschritts
        if(progressDialog==null){ //ggf. erstellen einer neuen FortschritsAnzeige
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.setIndeterminate(true);
        }
        progressDialog.show(); //zeigen des Dialogs
    }

    private void hideProgressDialog() {
        Log.d(TAG, "hideProgressDialog: ");
        //verstecken der FortschritsAnzeige
        if(progressDialog!=null && progressDialog.isShowing()){
            progressDialog.dismiss(); //
        }
    }

    //**************************************************

    @Override
    public void onClick(View v) {
        Log.d(TAG, "onClick: ");
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
        Log.d(TAG, "signOut: ");
        auth.signOut();                     //methodenaufruf: signOut()
        updateUI(null);
    }

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn: ");
        //prüfen
        if( !valdateForm() ){   //methodenaufruf: valdateForm
            return; //wenn nicht valide ist
        }
        fromChat = false;
        Log.d(TAG, "signIn: fromChat = false;");

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

//                        //direkter start
//                        Intent intent = new Intent(LoginActivity.this , ChatActivity.class);
//                        startActivity(intent);
                    }
        });
    }

    private void createAccount(String email, String password) {     //ACCOUNT ERSTELLEN
        Log.d(TAG, "createAccount: ");
        if( !valdateForm() ){   //methodenaufruf: valdateForm
            return; //wenn nicht valide ist
        }
        showProgressDialog();   //Anzeige
        auth.createUserWithEmailAndPassword(email , password) //erstellt hier einen neuen et_user
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
        Log.d(TAG, "sendEmailVerification: ");
        
        findViewById(R.id.verifyEmailButton).setEnabled(false);// Deaktivieren des VerifizierungsButton
        final FirebaseUser user = auth.getCurrentUser();
        user.sendEmailVerification().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                findViewById(R.id.verifyEmailButton).setEnabled(true);// Aktivieren des VerifizierungsButton
                if(task.isSuccessful()){
                    Toast.makeText(LoginActivity.this, "Verification mail send to " + user.getEmail(), Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(LoginActivity.this, "Faild to send verification email to " + user.getEmail(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean valdateForm() {     //EINGABEN DER EMAIL UND PASSWORT PRÜFEN(validieren)
        Log.d(TAG, "valdateForm: ");
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
