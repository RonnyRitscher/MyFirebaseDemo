package de.proneucon.myfirebase;
/**
 * FIREBASE nutzen und hinzufügen:
 * *Die Firebase ist keine "richtige" Datenbank und die Ausgabe verhält sich wie ein JSON-Objekt
 *
 *__FIREBASE_IN_APP___
 * FIREBASE zum Projekt hinzufügen
 * - Tools -> Firebase
 *
 * PROJEKTEINSETLLUNGEN
 * - hinzufügen von Depencies in buildGradle (app und projekt) :
 * 'com.google.firebase:firebase-core:16.0.7'
 *
 *
 * __FIREBASE_IN_BROWSER___
 * AUTIFICATION einstellen
 * - angeben welche Anmeldemöglichkeit gewünscht wird
 *
 * DATABASE einstellen
 * - RealtimeDatabase -> Regeln -> lesen und schreiben erlauben (true)
 */

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChatActivity extends AppCompatActivity {

    EditText et_input, et_user;
    TextView tv_output;
    Button send;

    private FirebaseAuth auth; //Declare an instance of FirebaseAuth
    private FirebaseUser user;

    FirebaseDatabase database; //Hier kann nun die Firebase verwendet werden nach dem configurieren
    DatabaseReference reference; //Referenz auf die Database
    DatabaseReference childReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialisieren der Elemente/Members
        et_input = findViewById(R.id.et_input);
        tv_output = findViewById(R.id.tv_output);
        et_user = findViewById(R.id.et_user);
        send = findViewById(R.id.btn_send);

        tv_output.setText(""); //tv_output leeren

        Message fromServer = new Message(); // Message erstellen

        //initialisieren der User-Daten
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        et_user.setText(user.getEmail());

        //initialisieren der Firebase und Reference
        database = FirebaseDatabase.getInstance(); //findet automatisch die angebundene database
        reference = database.getReference("messages_reference"); //als parameter der Pfad
        //>>childReference = reference.child("message");


        //*****ADD_VALUE_EVENT_LISTENER   falls sich Daten ändern oder etwas schief läuft (
//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                //WENN SICH DATEN ÄNDERN -über den DataSnapshot
//
//                //>>String value = dataSnapshot.getValue(String.class); //!beim holen des Strings muss der Parameter (String.class) angegeben werden
//                Message value = dataSnapshot.getValue(Message.class); //!beim holen des Strings muss der Parameter (String.class) angegeben werden
//                if(value!=null){
//                    //>>tv_output.setText(value);  //Ausgeben des Values in der TextView
//                    tv_output.setText(value.getUser() + ": " + value.getText());  //Ausgeben des Values in der TextView
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                //WENN FEHLER PASSIEREN
//                Toast.makeText(ChatActivity.this, "Fehler beim lesen der Daten", Toast.LENGTH_SHORT).show();
//            }
//        });

        //*****ADD_CHILD_EVENT_LISTENER
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                //wie bei dem addValueEventListener!
                Message value = dataSnapshot.getValue(Message.class);
                if(value!=null){
                    tv_output.append(value.getUser() + ": " + value.getText() +"\n");  //APPEND
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    //**************************************


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Menu einbinden über den menuinflater
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.it_logout:
                auth.signOut();         //Methode signOut() über den auth
                //Aufruf der Login Seite
                Intent intent = new Intent(this , LoginActivity.class);
                startActivity(intent);

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //****************************************

    //ON-CLICK des Buttons btn_send
    public void send(View view) {
        //verknüpfen der referens mit dem send_button

        Message message = new Message(et_user.getText().toString() , et_input.getText().toString());
        //>>reference.setValue(et_input.getText().toString());   //! haben probleme bei Arrays, gut mit Strings


        //keine feste ChildReferen, nur über push... dann gibt es für jeden Eintrag eine eindeutige ID unter "message" in der DB
        //childReference = reference.child("message_childReference").push(); //Fügt in der FDB einen unterpunkt und eindeutige ID hinzu
        childReference = reference.push(); // fügt nur eine eindeutige ID (ohne unterpunkt) hinzu
        childReference.setValue(message);
        //...->nun benötigen wir einen ChildEventListener
    }



}
