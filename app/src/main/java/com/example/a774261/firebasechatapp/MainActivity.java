package com.example.a774261.firebasechatapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

//Firebase

import com.firebase.ui.auth.AuthUI;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseError;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    private FirebaseAnalytics mFirebaseAnalytics;
    private DatabaseReference rootRef;


    private static final int SIGN_IN_REQUEST_CODE = 123;
    ArrayList<String> messageList = new ArrayList<>();
    ArrayAdapter<String> adapter;
    ListView listOfMessages;
    ChildEventListener listener;
    ChildEventListener onCreateListener;
    private boolean initialDataLoaded = false;


// ...

    // Choose authentication providers
    List<AuthUI.IdpConfig> providers = Arrays.asList(
            new AuthUI.IdpConfig.EmailBuilder().build());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createNotificationChannel();
        Log.d("OnCreate", "Activity Created");
        System.out.println("start");
       // getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
        rootRef = FirebaseDatabase.getInstance().getReference();
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
        setContentView(R.layout.activity_main);

        //ListView stuff
        //initialDataLoaded = false;
        listOfMessages = findViewById(R.id.list_of_messages);
        adapter = new ArrayAdapter<>(this, R.layout.row, messageList);
        listOfMessages.setAdapter(adapter);
        scrollMyListViewToBottom();
       //get a single snapshot of the database on app creation



        final EditText input = (EditText) findViewById(R.id.messageTxt);

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

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                scrollMyListViewToBottom();
            }
        });


        rootRef = FirebaseDatabase.getInstance().getReference();
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        initialDataLoaded = false;
        //ListView stuff
        listOfMessages = findViewById(R.id.list_of_messages);
        adapter = new ArrayAdapter<>(this, R.layout.row, messageList);
        listOfMessages.setAdapter(adapter);
        Log.d("onResume", "Here");
        scrollMyListViewToBottom();
        listener = rootRef.addChildEventListener(new ChildEventListener() {
                                                     @Override
                                                     public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                                         Log.d("ChildAddedStart", "Program Resumed");
                                                         ChatMessage chatMessage = new ChatMessage();
                                                         String message;
                                                         String user;
                                                         String time;
                                                         Log.e("Count " ,""+dataSnapshot.getChildrenCount());
                                                         for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                                             Log.d("debug", "loop in datasnapshot on child added");
                                                             String key = childSnapshot.getKey();
                                                             if(key.equals("messageText")){
                                                                 chatMessage.setMessageText(childSnapshot.getValue(String.class));
                                                                 message = childSnapshot.getValue(String.class);
                                                                 //System.out.println(message);
                                                             }
                                                             else if(key.equals("messageTime")){
                                                                 chatMessage.setMessageTime(childSnapshot.getValue(String.class));
                                                                 time = childSnapshot.getValue(String.class);
                                                                 // System.out.println(time);
                                                             }
                                                             else if(key.equals("messageUser")){
                                                                 chatMessage.setMessageUser(childSnapshot.getValue(String.class));
                                                                 user = childSnapshot.getValue(String.class);
                                                                 //System.out.println(user);
                                                             }
                                                             // System.out.println(childSnapshot.getKey());

                                                         }
                                                         String messageT = chatMessage.getMessageText();
                                                         String messages = "" + chatMessage.getMessageText() + "\nFrom: " + chatMessage.getMessageUser() + "\nTime: " + String.valueOf(chatMessage.getMessageTime()) + "\n\n";

                                                         if((chatMessage.checkMessageLength(messageT))) {
                                                             System.out.println(messages);
                                                             if(!messageList.contains(messages)) {
                                                                 messageList.add(messages);
                                                                 adapter.notifyDataSetChanged(); //update the array adapter
                                                                 if(initialDataLoaded){
                                                                     createNotification(chatMessage, pendingIntent);
                                                                 }

                                                             }
                                                         }
                                                         scrollMyListViewToBottom();
//
                                                     }

                                                     @Override
                                                     public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                                                     }

                                                     @Override
                                                     public void onChildRemoved(DataSnapshot dataSnapshot) {
                                                         ChatMessage chatMessage = new ChatMessage();
                                                         String message;
                                                         String user;
                                                         String time;
                                                         Log.e("Count " ,""+dataSnapshot.getChildrenCount());
                                                         for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                                             Log.d("debug", "loop in datasnapshot on childremoved");
                                                             String key = childSnapshot.getKey();
                                                             if(key.equals("messageText")){
                                                                 chatMessage.setMessageText(childSnapshot.getValue(String.class));
                                                                 message = childSnapshot.getValue(String.class);
                                                                 //  System.out.println(message);
                                                             }
                                                             else if(key.equals("messageTime")){
                                                                 chatMessage.setMessageTime(childSnapshot.getValue(String.class));
                                                                 time = childSnapshot.getValue(String.class);
                                                                 // System.out.println(time);
                                                             }
                                                             else if(key.equals("messageUser")){
                                                                 chatMessage.setMessageUser(childSnapshot.getValue(String.class));
                                                                 user = childSnapshot.getValue(String.class);
                                                                 // System.out.println(user);
                                                             }
                                                             // System.out.println(childSnapshot.getKey());

                                                         }
                                                         String messageT = chatMessage.getMessageText();
                                                         String messages = "" + chatMessage.getMessageText() + "\nFrom: " + chatMessage.getMessageUser() + "\nTime: " + String.valueOf(chatMessage.getMessageTime()) + "\n\n";

                                                         if((chatMessage.checkMessageLength(messageT))) {
                                                             System.out.println(messages);
                                                             messageList.remove(messages);
                                                             adapter.notifyDataSetChanged(); //update the array adapter
                                                         }
                                                         scrollMyListViewToBottom();

                                                     }

                                                     @Override
                                                     public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                                                     }

                                                     @Override
                                                     public void onCancelled(DatabaseError databaseError) {

                                                     }
                                                 }
        );


    }

    @Override
    protected void onResume(){
        super.onResume();
        // Create an explicit intent for an Activity in your app
        //Firebase database reference
        Log.d("onResume", "Program Resumed");
        initialDataLoaded = false;
        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("SingleValueEvent", "triggered method");
                //this is called once after the onChildAdded function finishes populating the initial data
                initialDataLoaded = true;

            }




            @Override
            public void onCancelled(DatabaseError firebaseError) {
                System.out.println("hello");

            }
        });

    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

        if(rootRef!=null)
        {
            Log.d("Remove", "Event listener removed");
            rootRef.removeEventListener(listener);
            listener=null;
            rootRef=null;
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

    private void scrollMyListViewToBottom() {
        listOfMessages.post(new Runnable() {
            @Override
            public void run() {
                listOfMessages.setSelection(adapter.getCount() - 1);
            }
        });
    }
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("id", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void createNotification(ChatMessage chatMessage, PendingIntent pendingIntent){
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), "id")
                .setSmallIcon(R.drawable.icon)
                .setContentTitle(chatMessage.getMessageUser())
                .setContentText(chatMessage.getMessageText())
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        int id = 0;
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify((id + 1), mBuilder.build());
    }


}

