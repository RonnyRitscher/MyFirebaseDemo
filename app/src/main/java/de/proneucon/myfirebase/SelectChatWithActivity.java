package de.proneucon.myfirebase;
/**
 * Hier soll ausgesucht werden mit wem man chatten möchte
 */

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.List;

public class SelectChatWithActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_chat_with);

        ListView chat_user = findViewById(R.id.lv_chat_user);

        //TODO: Hier muss ein Array mit den Benutzern aus der Datenbank hinzugefügt werden:



        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this ,
                android.R.layout.simple_list_item_1 ,
                getResources().getStringArray(R.array.chat_user)); //TODO: Hier muss das Array aus der DB übergeben werden

        chat_user.setAdapter(adapter);
        chat_user.setOnItemClickListener( (adapterView, view , i , l) -> {

            //beschaffen uns den Namen des ausgewählten ListItems/User(chatWith)
            TextView textView = (TextView) view;
            String chatWith = textView.getText().toString();

            //INTENT
            Intent intent = new Intent(SelectChatWithActivity.this , ChatActivity.class ); // ! Intent über die Klasse bilden
            intent.putExtra("chatWith" , chatWith) ; //übergeben den intent den ausgewählten chatUser
            startActivity(intent);


        });
    }
}
