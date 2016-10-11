package com.marv.element;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

/* © Copyright 2016 Siddhant Vinchurkar

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.   */

public class CloudActivity extends AppCompatActivity {

    EditText notifyTitle,notifyContent,wnew,mgreetContent,sgreetContent,pictureURL;
    Button sendpn,update,update2,learnMore;
    TextView online;
    Typewriter welcome;
    Switch greet, master;
    private Firebase firebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cloud);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.polymer);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Display general information
                LayoutInflater inflater= LayoutInflater.from(CloudActivity.this);
                final View elementInfo=inflater.inflate(R.layout.info_dialog, null);
                AlertDialog.Builder ab=new AlertDialog.Builder(CloudActivity.this);
                ab.setView(elementInfo);
                ab.setCancelable(true);
                ab.create();
                final AlertDialog show=ab.show();
                learnMore=(Button)elementInfo.findViewById(R.id.learnMore);
                learnMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri uri = Uri.parse("http://marv.tk/"); // missing 'http://' will cause the app to crash
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                });
            }
        });
        Firebase.setAndroidContext(this);
        firebase=new Firebase("https://marvelement.firebaseio.com/");
        if(!MainActivity.authenticated){
            AlertDialog.Builder ab=new AlertDialog.Builder(CloudActivity.this);
            ab.setMessage("You seem to have accessed Element unethically. As a result, Element will now quit.");
            ab.setCancelable(true);
            ab.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    finish();
                    System.exit(0);
                }
            });
            ab.create();
            ab.show();
        }
        notifyTitle=(EditText)findViewById(R.id.notifyTitle);
        notifyContent=(EditText)findViewById(R.id.notifyContent);
        wnew=(EditText)findViewById(R.id.wnew);
        sendpn=(Button)findViewById(R.id.sendpn);
        update=(Button)findViewById(R.id.update);
        update2=(Button)findViewById(R.id.update2);
        mgreetContent=(EditText)findViewById(R.id.mGreetContent);
        sgreetContent=(EditText)findViewById(R.id.sGreetContent);
        pictureURL=(EditText)findViewById(R.id.pictureURL);
        greet=(Switch)findViewById(R.id.greet);
        master=(Switch)findViewById(R.id.master);
        online=(TextView)findViewById(R.id.online);
        welcome=(Typewriter)findViewById(R.id.welcome);
        firebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                online.setText(String.valueOf((long) dataSnapshot.child("User Metadata").child("Number of users online").getValue())+" user(s) online right now");
                if(((String)dataSnapshot.child("Greet").getValue().toString()).equals("Yes")){
                    Toast.makeText(getApplicationContext(),"Startup greeting enabled",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getApplicationContext(),"Startup greeting disabled",Toast.LENGTH_SHORT).show();
                }
                if((boolean)dataSnapshot.child("Alive").getValue()){
                    Toast.makeText(getApplicationContext(),"Marv is up and running",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getApplicationContext(),"Marv's services have been shut down",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        final ProgressDialog pd= new ProgressDialog(CloudActivity.this);
        pd.setMessage("Loading");
        pd.create();
        pd.setCancelable(false);
        pd.show();
        Handler handler1=new Handler();
        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                pd.dismiss();
                welcome.setCharacterDelay(50);
                welcome.animateText("Welcome to Element");
            }
        },3000);
        sendpn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (notifyTitle.getText().toString().isEmpty() && notifyContent.getText().toString().isEmpty()) {
                    AlertDialog.Builder ab = new AlertDialog.Builder(CloudActivity.this);
                    ab.setMessage("You haven't entered the notification title and content");
                    ab.create();
                    ab.show();
                } else if (notifyTitle.getText().toString().isEmpty()) {
                    AlertDialog.Builder ab = new AlertDialog.Builder(CloudActivity.this);
                    ab.setMessage("You haven't entered the notification title");
                    ab.create();
                    ab.show();
                } else if (notifyContent.getText().toString().isEmpty()) {
                    AlertDialog.Builder ab = new AlertDialog.Builder(CloudActivity.this);
                    ab.setMessage("You haven't entered the notification content");
                    ab.create();
                    ab.show();
                } else {
                    sendpn.setEnabled(false);
                    Toast.makeText(getApplicationContext(), "Please wait for 10 seconds", Toast.LENGTH_LONG).show();
                    firebase.child("NotifyTitle").setValue((String) notifyTitle.getText().toString());
                    firebase.child("NotifyContent").setValue((String) notifyContent.getText().toString());
                    Handler handler2 = new Handler();
                    handler2.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            firebase.child("Notify").setValue("Yes");
                            Handler handler3 = new Handler();
                            handler3.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    sendpn.setEnabled(true);
                                    Toast.makeText(getApplicationContext(), "Notification sent successfully", Toast.LENGTH_LONG).show();
                                }
                            }, 9500);
                        }
                    }, 500);
                }
            }
        });
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(wnew.getText().toString().isEmpty()){
                    AlertDialog.Builder ab = new AlertDialog.Builder(CloudActivity.this);
                    ab.setMessage("You haven't entered what's new");
                    ab.create();
                    ab.show();
                }
                else{
                    firebase.child("New").setValue((String)wnew.getText().toString());
                    Toast.makeText(getApplicationContext(),"Updated successfully",Toast.LENGTH_SHORT).show();
                }
            }
        });
        greet.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    firebase.child("Greet").setValue("Yes");
                } else {
                    firebase.child("Greet").setValue("No");
                }
            }
        });
        master.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    firebase.child("Alive").setValue(true);
                } else {
                    firebase.child("Alive").setValue(false);
                }
            }
        });
        update2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mgreetContent.getText().toString().isEmpty() && sgreetContent.getText().toString().isEmpty()&& pictureURL.getText().toString().isEmpty()) {
                    AlertDialog.Builder ab = new AlertDialog.Builder(CloudActivity.this);
                    ab.setMessage("You haven't entered the greet content, greet speech content and picture URL");
                    ab.create();
                    ab.show();
                }
                else if (mgreetContent.getText().toString().isEmpty()) {
                    AlertDialog.Builder ab = new AlertDialog.Builder(CloudActivity.this);
                    ab.setMessage("You haven't entered the greet content");
                    ab.create();
                    ab.show();
                }
                else if (sgreetContent.getText().toString().isEmpty()) {
                    AlertDialog.Builder ab = new AlertDialog.Builder(CloudActivity.this);
                    ab.setMessage("You haven't entered the greet speech content");
                    ab.create();
                    ab.show();
                }
                else if (pictureURL.getText().toString().isEmpty()) {
                    AlertDialog.Builder ab = new AlertDialog.Builder(CloudActivity.this);
                    ab.setMessage("You haven't entered the picture URL");
                    ab.create();
                    ab.show();
                }
                else if (pictureURL.getText().toString().isEmpty()&&sgreetContent.getText().toString().isEmpty()) {
                    AlertDialog.Builder ab = new AlertDialog.Builder(CloudActivity.this);
                    ab.setMessage("You haven't entered the greet speech content and picture URL");
                    ab.create();
                    ab.show();
                }
                else if (pictureURL.getText().toString().isEmpty()&&mgreetContent.getText().toString().isEmpty()) {
                    AlertDialog.Builder ab = new AlertDialog.Builder(CloudActivity.this);
                    ab.setMessage("You haven't entered the greet content and picture URL");
                    ab.create();
                    ab.show();
                }
                else if (sgreetContent.getText().toString().isEmpty()&&mgreetContent.getText().toString().isEmpty()) {
                    AlertDialog.Builder ab = new AlertDialog.Builder(CloudActivity.this);
                    ab.setMessage("You haven't entered the greet content and greet speech content");
                    ab.create();
                    ab.show();
                }
                else{
                    firebase.child("GreetContent").setValue((String)mgreetContent.getText().toString());
                    firebase.child("GreetSpeak").setValue((String)sgreetContent.getText().toString());
                    firebase.child("PictureURL").setValue((String)pictureURL.getText().toString());
                    Toast.makeText(getApplicationContext(),"Updated successfully",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if(id==R.id.action_settings){
            //Sign the user out
            Toast.makeText(getApplicationContext(),"Successfully signed out",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(CloudActivity.this,MainActivity.class));
            return true;
        }

        else if(id==R.id.action_about){
            //Display general information
            LayoutInflater inflater= LayoutInflater.from(CloudActivity.this);
            final View elementInfo=inflater.inflate(R.layout.info_dialog, null);
            AlertDialog.Builder ab=new AlertDialog.Builder(CloudActivity.this);
            ab.setView(elementInfo);
            ab.setCancelable(true);
            ab.create();
            final AlertDialog show=ab.show();
            learnMore=(Button)elementInfo.findViewById(R.id.learnMore);
            learnMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri uri = Uri.parse("http://marv.tk/"); // missing 'http://' will cause the app to crash
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            });
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //closing transition animations
        overridePendingTransition(R.anim.activity_open_scale,R.anim.activity_close_translate);
        finish();
    }

    @Override
    protected void onResume() {
        //opening transition animations
        overridePendingTransition(R.anim.activity_open_translate,R.anim.activity_close_scale);
        super.onResume();
    }
}
