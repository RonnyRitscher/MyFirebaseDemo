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
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;

import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {

    //MEMBER
    EditText messageArea;
    ImageView sendButton;
    LinearLayout layout_1;
    RelativeLayout layout_2;
    ScrollView scrollView;

    String username, chatWith;

    FirebaseAuth auth;                              //Declare an instance of FirebaseAuth
    FirebaseUser user;
    FirebaseDatabase database;                      //Hier kann nun die Firebase verwendet werden nach dem configurieren
    DatabaseReference reference1, reference2;       //Referenz auf die Database

    //**************************************
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialisierung der Member/Elemente
        messageArea = findViewById(R.id.et_message_area);
        layout_1 = findViewById(R.id.layout1);
        layout_2 = findViewById(R.id.layout2);
        scrollView = findViewById(R.id.scrollView);
        messageArea = findViewById(R.id.et_message_area);
        sendButton = findViewById(R.id.iv_sendButton);

        auth = FirebaseAuth.getInstance(); //! erst eine Instanz von auth bilden um diese verwenden zu können
        username = auth.getCurrentUser().getEmail(); //über auth die Email als Usernamen verwenden
        username = username.replace(".", "_"); //! Firebase Database paths must not contain '.', '#', '$', '[', or ']'

        chatWith = getIntent().getStringExtra("chatWith");  //zeigt die Trennung wer etwas geschrieben hat(rechts) und von wem empfangen(links)
        chatWith = chatWith.replace(".", "_"); //! Firebase Database paths must not contain '.', '#', '$', '[', or ']'

        database = FirebaseDatabase.getInstance(); //
        //Referenzen anlegen:
        reference1 = database.getReference("messages/" + username + "_" + chatWith);
        reference2 = database.getReference("messages/" + chatWith + "_" + username);

        //Send Button den onClickListener mitgeben
        sendButton.setOnClickListener( (v) -> {
            String messageText = messageArea.getText().toString();
            if(!TextUtils.isEmpty(messageText)){
                Map<String, String> map = new HashMap<>();          //HashMap
                map.put("message" , messageText);                   //put message
                map.put("user" , username);                         //put username

                reference1.push().setValue(map);                    //ref1 setValue map
                reference2.push().setValue(map);                    //ref1 setValue map
                messageArea.setText("");


            }
        });
        //**************************************
        //braucht nur eine Referent, egaö ob x->y oder y->x
        reference1.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                //Child ()DB-Eintrag) aus dataSnapShot herausholen
                GenericTypeIndicator<Map<String,String>> typeIndicator = new GenericTypeIndicator<Map<String, String>>() {};
                Map<String, String> map = dataSnapshot.getValue(typeIndicator);
                String message = map.get("message").toString();
                String username = map.get("user").toString();

                if(username.equals(ChatActivity.this.username)){
                    //Methode ... wenn ich geschrieben habe
                    addMessageBox("You:\n" , message , 1);
                }else {
                    //Methode ... wenn anderer geschrieben habe
                    addMessageBox(ChatActivity.this.chatWith +":\n", message , 2);
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
    private void addMessageBox(String from, String message, int type) {
        //Wir bauen hier unsere View zusammen: dynamisch mit unterschiedlichen darstellung je nachdem wer schreibt
        TextView textView = new TextView(this);
        textView.setText( from + message );

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT ,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.weight = 1.0F;

        //Position festlegen
        if(type == 1){
            lp.gravity = Gravity.RIGHT; //Ausrichtung
            textView.setBackgroundResource(R.drawable.bubble_in);//Image
        }else{
            lp.gravity = Gravity.LEFT; //Ausrichtung
            textView.setBackgroundResource(R.drawable.bubble_out);//Image
        }

        textView.setLayoutParams(lp);           //layoutParameter wird der TextView übergeben
        layout_1.addView(textView);             //textView wird dem Layout übergeben
        scrollView.fullScroll(View.FOCUS_DOWN); //setzt die Anzeige auf das letzte Element der SV

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



}
