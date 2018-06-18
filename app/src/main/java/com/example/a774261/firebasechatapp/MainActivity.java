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
import android.view.WindowManager;
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
import com.google.firebase.database.ChildEventListener;
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

    private static final int SIGN_IN_REQUEST_CODE = 123;
    ArrayList<String> messageList = new ArrayList<>();
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
       // getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

        //Firebase database reference
        rootRef = FirebaseDatabase.getInstance().getReference();

        //ListView stuff
        ListView listOfMessages = findViewById(R.id.list_of_messages);
        adapter = new ArrayAdapter<>(this, R.layout.row, messageList);
        listOfMessages.setAdapter(adapter);
        final EditText input = (EditText) findViewById(R.id.messageTxt);



        rootRef.addChildEventListener(new ChildEventListener() {
                                          @Override
                                          public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                              ChatMessage chatMessage = new ChatMessage();
                                              String message;
                                              String user;
                                              String time;
                                              Log.e("Count " ,""+dataSnapshot.getChildrenCount());
                                              for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                                  Log.d("debug", "loop");
                                                  String key = childSnapshot.getKey();
                                                  if(key.equals("messageText")){
                                                      chatMessage.setMessageText(childSnapshot.getValue(String.class));
                                                      message = childSnapshot.getValue(String.class);
                                                      System.out.println(message);
                                                  }
                                                  else if(key.equals("messageTime")){
                                                      chatMessage.setMessageTime(childSnapshot.getValue(String.class));
                                                      time = childSnapshot.getValue(String.class);
                                                      System.out.println(time);
                                                  }
                                                  else if(key.equals("messageUser")){
                                                      chatMessage.setMessageUser(childSnapshot.getValue(String.class));
                                                      user = childSnapshot.getValue(String.class);
                                                      System.out.println(user);
                                                  }
                                                  // System.out.println(childSnapshot.getKey());

                                              }
                                              String messageT = chatMessage.getMessageText();
                                              String messages = "" + chatMessage.getMessageText() + "\nFrom: " + chatMessage.getMessageUser() + "\nTime: " + String.valueOf(chatMessage.getMessageTime()) + "\n\n";

                                              if((chatMessage.checkMessageLength(messageT))) {
                                                  System.out.println(messages);
                                                  messageList.add(messages);
                                                  adapter.notifyDataSetChanged(); //update the array adapter
                                              }
//
                                          }

                                          @Override
                                          public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                                          }

                                          @Override
                                          public void onChildRemoved(DataSnapshot dataSnapshot) {

                                          }

                                          @Override
                                          public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                                          }

                                          @Override
                                          public void onCancelled(DatabaseError databaseError) {

                                          }
                                      }
        );
        //send message button functions
        Button sendButton = (Button) findViewById(R.id.sendBtn);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //EditText input = (EditText) findViewById(R.id.messageTxt);

                // Read the input field and push a new instance
                // of ChatMessage to the Firebase database

                if(input.getText().length() < 256) {
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
                else{
                    Toast.makeText(getApplicationContext(), "Over 255 characters, not sent", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
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
        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SIGN_IN_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this,
                        "Successfully signed in. Welcome!",
                        Toast.LENGTH_LONG)
                        .show();
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
        if (item.getItemId() == R.id.menu_sign_out) {
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

