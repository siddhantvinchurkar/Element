package com.marv.element;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import se.simbio.encryption.Encryption;

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

public class MainActivity extends AppCompatActivity {

    EditText password,username;
    Button login;
    ProgressBar loader;
    public static boolean authenticated=false;
    private FirebaseAuth mAuth;
    private FirebaseAnalytics mFirebaseAnalytics;
    CheckBox rememberMeCheckBox;
    private ProgressDialog pd,pd2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.polymer);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        username=(EditText) findViewById(R.id.username);
        password=(EditText)findViewById(R.id.password);
        login=(Button)findViewById(R.id.login);
        loader=(ProgressBar)findViewById(R.id.loader);
        loader.setVisibility(View.GONE);
        rememberMeCheckBox=(CheckBox)findViewById(R.id.rememberMeCheckBox);
        //Decrypt and load username & password from xml
        SharedPreferences sp=getPreferences(MODE_PRIVATE);
        final String tempUsername=sp.getString("username","");
        final String tempPassword=sp.getString("password","");
        if(!tempUsername.isEmpty()&&!tempPassword.isEmpty()){
           new Handler().postDelayed(new Runnable() {
               @Override
               public void run() {
                   new AsyncTask<Void,Void,Void>(){

                       String u="",p="";

                       @Override
                       protected void onPreExecute() {
                           super.onPreExecute();
                           pd = new ProgressDialog(MainActivity.this);
                           pd.setMessage("Please wait...");
                           pd.setCancelable(false);
                           pd.show();
                       }

                       @Override
                       protected void onPostExecute(Void aVoid) {
                           super.onPostExecute(aVoid);
                           username.setText(u);
                           password.setText(p);
                           pd.dismiss();
                       }

                       @Override
                       protected Void doInBackground(Void... params) {
                           u=decryptString(tempUsername);
                           p=decryptString(tempPassword);
                           return null;
                       }
                   }.execute();
               }
           },10);
        }
        rememberMeCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!rememberMeCheckBox.isChecked()){
                    //Delete stored username & password from xml
                    SharedPreferences sp=getPreferences(MODE_PRIVATE);
                    SharedPreferences.Editor edit=sp.edit();
                    edit.putString("username",null);
                    edit.putString("password",null);
                    edit.commit();
                }
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable()) {
                    if (password.getText().toString().isEmpty() && username.getText().toString().isEmpty()) {
                        AlertDialog.Builder ab = new AlertDialog.Builder(MainActivity.this);
                        ab.setMessage("You haven't entered your username and password");
                        ab.create();
                        ab.show();
                    } else if (username.getText().toString().isEmpty()) {
                        AlertDialog.Builder ab = new AlertDialog.Builder(MainActivity.this);
                        ab.setMessage("You haven't entered your username");
                        ab.create();
                        ab.show();
                    } else if (password.getText().toString().isEmpty()) {
                        AlertDialog.Builder ab = new AlertDialog.Builder(MainActivity.this);
                        ab.setMessage("You haven't entered your password");
                        ab.create();
                        ab.show();
                    } else {
                        login.setEnabled(false);
                        if(rememberMeCheckBox.isChecked()){
                            //Encrypt and store username & password in xml
                            final String a=username.getText().toString();
                            final String b=password.getText().toString();
                            new  Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    new AsyncTask<Void,Void,Void>(){

                                        String u="",p="";

                                        @Override
                                        protected void onPreExecute() {
                                            super.onPreExecute();
                                            pd2 = new ProgressDialog(MainActivity.this);
                                            pd2.setMessage("Please wait...");
                                            pd2.setCancelable(false);
                                            pd2.show();
                                        }

                                        @Override
                                        protected void onPostExecute(Void aVoid) {
                                            super.onPostExecute(aVoid);
                                            SharedPreferences sp=getPreferences(MODE_PRIVATE);
                                            SharedPreferences.Editor edit=sp.edit();
                                            edit.putString("username",u);
                                            edit.putString("password",p);
                                            edit.commit();
                                            pd2.dismiss();
                                            loader.setVisibility(View.VISIBLE);
                                            //Sign the user in
                                            signIn();
                                        }

                                        @Override
                                        protected Void doInBackground(Void... params) {
                                            SharedPreferences sp=getPreferences(MODE_PRIVATE);
                                            if(sp.getString("username","").isEmpty()&&sp.getString("password","").isEmpty()) {
                                                u = encryptString(a);
                                                p = encryptString(b);
                                            }
                                            return null;
                                        }
                                    }.execute();
                                }
                            },10);
                        }
                        else{
                            //Delete stored username & password from xml
                            SharedPreferences sp=getPreferences(MODE_PRIVATE);
                            SharedPreferences.Editor edit=sp.edit();
                            edit.putString("username",null);
                            edit.putString("password",null);
                            edit.commit();
                            loader.setVisibility(View.VISIBLE);
                            signIn();
                        }
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(),"No internet connection",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private String encryptString(String encrypt){
        Encryption encryption = Encryption.getDefault("Key", "Salt", new byte[16]);
        return encryption.encryptOrNull(encrypt);
    }

    private String decryptString(String decrypt){
        Encryption encryption = Encryption.getDefault("Key", "Salt", new byte[16]);
        return encryption.decryptOrNull(decrypt);
    }

    private void signIn(){
        mAuth.signInWithEmailAndPassword(username.getText().toString(), password.getText().toString())
                .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("User Session Status", "Sign in process complete" + task.isSuccessful());
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w("User Session Status", "Sign in", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed", Toast.LENGTH_SHORT).show();
                        }

                        else{
                            Toast.makeText(getApplicationContext(), "Successfully signed in", Toast.LENGTH_SHORT).show();
                            authenticated=true;
                            Bundle bundle = new Bundle();
                            bundle.putString(FirebaseAnalytics.Param.ITEM_ID,"LOGIN_COUNT");
                            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Total number of times someone logged in");
                            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "number");
                            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                            startActivity(new Intent(MainActivity.this,CloudActivity.class));
                        }

                        loader.setVisibility(View.GONE);
                        login.setEnabled(true);
                    }
                });
    }

}
