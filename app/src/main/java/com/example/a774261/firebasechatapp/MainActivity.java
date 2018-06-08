package com.example.a774261.firebasechatapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

//Firebase

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private FirebaseAnalytics mFirebaseAnalytics;
    private DatabaseReference rootRef;
    //private FirebaseListAdapter<ChatMessage> adapter;
    private static final int SIGN_IN_REQUEST_CODE = 123;
    List<String> messageList = new ArrayList<>();
    ArrayAdapter<String> adapter;


// ...

    // Choose authentication providers
    List<AuthUI.IdpConfig> providers = Arrays.asList(
            new AuthUI.IdpConfig.EmailBuilder().build());



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("start", "Start Program");
        System.out.println("start");
        //DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        rootRef = FirebaseDatabase.getInstance().getReference();

        Button sendButton = (Button)findViewById(R.id.sendBtn);



        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText input = (EditText)findViewById(R.id.messageTxt);

                // Read the input field and push a new instance
                // of ChatMessage to the Firebase database
                FirebaseDatabase.getInstance()
                        .getReference()
                        .push()
                        .setValue(new ChatMessage(input.getText().toString(),
                                FirebaseAuth.getInstance()
                                        .getCurrentUser()
                                        .getDisplayName())
                        );

                // Clear the input
                input.setText("");
            }
        });

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            // Start sign in/sign up activity
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .build(),
                    SIGN_IN_REQUEST_CODE
            );
        } else {
            // User is already signed in. Therefore, display
            // a welcome Toast
            Toast.makeText(this,
                    "Welcome " + FirebaseAuth.getInstance()
                            .getCurrentUser()
                            .getDisplayName(),
                    Toast.LENGTH_LONG)
                    .show();

            // Load chat room contents
            displayChatMessages();
        }


    }

    private void displayChatMessages() {
        final ListView listOfMessages = findViewById(R.id.list_of_messages);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line);
        listOfMessages.setAdapter(adapter);

        //try on child added and working with lists of data in android
        ValueEventListener messageListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                Log.e("Count " ,""+dataSnapshot.getChildrenCount());
               for(DataSnapshot messageSnapshot: dataSnapshot.getChildren()) {
                   ChatMessage chatMessage = dataSnapshot.getValue(ChatMessage.class);
                   //Adding it to a string
                   String messages = "MSG: "+ chatMessage.getMessageText()+"\nUser: "+chatMessage.getMessageUser()+"\nTime: "+String.valueOf(chatMessage.getMessageTime())+"\n\n";

                   String message = chatMessage.getMessageText();
                   String user = chatMessage.getMessageUser();
                   long time = chatMessage.getMessageTime();
                   System.out.println("message only");
                   System.out.println(message);
                   //  String item = dogExpenditure.getItem();

                   messageList.add(messages);
                   System.out.println("entire string");
                   System.out.println(messages);

               }

                if(messageList.size() == 1){
                    adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, messageList);
                    listOfMessages.setAdapter(adapter);

                }
                else if(messageList.size() > 1){
                    adapter.notifyDataSetChanged();
                }



                // ...
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting message failed, log a message
                Log.d("database error", "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        rootRef.addValueEventListener(messageListener);


//        ListView listOfMessages = findViewById(R.id.list_of_messages);
//        FirebaseListOptions<ChatMessage> options = new FirebaseListOptions.Builder<ChatMessage>()
//                .setQuery(rootRef, ChatMessage.class)
//                .build();
//        adapter = new FirebaseListAdapter<ChatMessage>(options)
//        {
//
//        @Override
//        protected void populateView(View v, ChatMessage model, int position) {
//            // Get references to the views of message.xml
//            TextView messageText = (TextView)v.findViewById(R.id.message_text);
//            TextView messageUser = (TextView)v.findViewById(R.id.message_user);
//            TextView messageTime = (TextView)v.findViewById(R.id.message_time);
//
//            // Set their text
//            messageText.setText(model.getMessageText());
//            messageUser.setText(model.getMessageUser());
//
//            // Format the date before showing it
//            messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",
//                    model.getMessageTime()));
//        }
//    };
//        listOfMessages.setAdapter(adapter);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SIGN_IN_REQUEST_CODE) {
            if(resultCode == RESULT_OK) {
                Toast.makeText(this,
                        "Successfully signed in. Welcome!",
                        Toast.LENGTH_LONG)
                        .show();
                displayChatMessages();
            } else {
                Toast.makeText(this,
                        "We couldn't sign you in. Please try again later.",
                        Toast.LENGTH_LONG)
                        .show();

                // Close the app
                finish();
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_sign_out) {
            AuthUI.getInstance().signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(MainActivity.this,
                                    "You have been signed out.",
                                    Toast.LENGTH_LONG)
                                    .show();

                            // Close activity
                            finish();
                        }
                    });
        }
        return true;
    }




}

