package org.tensorflow.lite.examples.detection.Login;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.tensorflow.lite.examples.detection.DBHandler;
import org.tensorflow.lite.examples.detection.Home;
import org.tensorflow.lite.examples.detection.R;

import java.util.ArrayList;
import java.util.Locale;

public class Register extends AppCompatActivity {

    public TextToSpeech tts;
     @SuppressLint("StaticFieldLeak")
     static TextView status;
     static  TextView name;
     Button view;
     static TextView age;
     TextView emerg;
     DBHandler dbHandler;
    private TextView username;
    ImageView btnsubmit;
    private ProgressDialog progressDialog;
    @SuppressLint("StaticFieldLeak")
    static TextView password;
    static int numberOfClicks;
    static String to;
    @SuppressLint("StaticFieldLeak")
    static DigitalInkMainActivity cdd;
    float x1, x2;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        emerg = findViewById(R.id.emerg);
         tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    tts.setLanguage(Locale.getDefault());
                    tts.setSpeechRate((float) 0.7);
                    tts.speak("Welcome to registration. tap on the screen and say the name", TextToSpeech.QUEUE_FLUSH, null);
                 }
            }
        });
        dbHandler = new DBHandler(Register.this);
        cdd = new DigitalInkMainActivity(Register.this,2);
        status = (TextView) findViewById(R.id.status);
        name = (TextView) findViewById(R.id.name);
        age = findViewById(R.id.age);
        username = (TextView) findViewById(R.id.username);
        password = findViewById(R.id.password);
        numberOfClicks = 0;
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        progressDialog = new ProgressDialog(this);

    }


    @Override
    public void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    public void layoutClicked(View view) {
        if (!tts.isSpeaking()) {
            numberOfClicks++;
            Toast.makeText(getApplicationContext(), String.valueOf(numberOfClicks), Toast.LENGTH_LONG).show();
            listen();
        }
    }

    private void listen() {
        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say something");

        try {
            startActivityForResult(i, 100);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(Register.this, "Your device doesn't support Speech Recognition", Toast.LENGTH_SHORT).show();
        }
    }


    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && !tts.isSpeaking()) {
            if (resultCode == RESULT_OK && null != data) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if (result.get(0).contains("cancel")) {
                    tts.speak("Registration Cancelled!", TextToSpeech.QUEUE_FLUSH, null);

                } else {

                    switch (numberOfClicks) {
                        case 1:
                            String namee;
                            namee = result.get(0);
                            name.setText(namee);
                            if (!name.getText().toString().isEmpty()) {
                                tts.speak("tap on the screen & say age", TextToSpeech.QUEUE_FLUSH, null);
                            }
                            if (name.getText().toString().isEmpty()) {
                                numberOfClicks--;
                            }
                            break;
                        case 2:
                            to = result.get(0);
                            to = to.replaceAll("[^\\d.]", "");
                            age.setText(to);

                            if (!to.isEmpty()) {
                                tts.speak("tap on the screen & say Emergency contact number", TextToSpeech.QUEUE_FLUSH, null);
                            }
                            if (to.isEmpty()) {
                                numberOfClicks--;
                            }
                            break;

                        case 3:
                            String emergs = result.get(0);
                            emergs = emergs.replaceAll("[^\\d.]", "");
                            emerg.setText(emergs);
                            ArrayList<Character> chars = new ArrayList<Character>();
                            for (char c : emergs.toCharArray()) {
                                chars.add(c);
                            }
                            if (!emergs.isEmpty()) {
                                tts.speak("Your phone number is, " + chars.toString() + ", tap on the screen and say yes to confirm, or no . to rewrite the number", TextToSpeech.QUEUE_FLUSH, null);
                                //tts.speak("tap on the screen & say username", TextToSpeech.QUEUE_FLUSH, null);
                            }
                            if (emergs.isEmpty()) {
                                numberOfClicks--;
                            }
                            break;
                        case 4:
                            if (result.get(0).contains("yes") || result.get(0).contains("s")) {
                                tts.speak("tap on the screen & say username", TextToSpeech.QUEUE_FLUSH, null);
                            }
                           else if (result.get(0).contains("no")) {
                                numberOfClicks = 2;
                                tts.speak("tap on the screen & say Emergency contact number", TextToSpeech.QUEUE_FLUSH, null);
                            }
                           else {
                               numberOfClicks=2;
                               tts.speak("say yes to confirm the number. or, no to rewrite the number",TextToSpeech.QUEUE_FLUSH,null);
                            }
                            break;
                        case 5:
                            String user;
                            user = result.get(0);
                            username.setText(user);
                            if (!user.isEmpty()) {
                                status.setText("confirm");
                                final Handler h = new Handler();
                                h.postDelayed(new Runnable() {
                                    @RequiresApi(api = Build.VERSION_CODES.O)
                                    @Override
                                    public void run() {
                                        cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                        cdd.show();
                                    }
                                }, 1000);


                            }
                            if (user.isEmpty()) {
                                numberOfClicks--;
                            }
                            break;
                        default:
                            if (numberOfClicks > 4) {
                                String res = result.get(0);
                                if (res.contains("yes") || res.contains("s")) {

                                    tts.speak("registered successfully", TextToSpeech.QUEUE_FLUSH, null);
                                    final Handler h = new Handler();
                                    h.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            progressDialog.setMessage("Registering...");
                                            progressDialog.show();
                                            String email = username.getText().toString() + "@gmail.com";
                                            String pass = password.getText().toString();

                                            firebaseAuth.createUserWithEmailAndPassword(email, pass)
                                                    .addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                                            //progressDialog.dismiss();
                                                            if (task.isSuccessful()) {
                                                                // Login with details
                                                                firebaseAuth.signInWithEmailAndPassword(email, pass);
                                                                UserInformation userInformation = new UserInformation(name.getText().toString(), age.getText().toString(), emerg.getText().toString(),"location");
                                                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                                                databaseReference.child(user.getUid()).child("userinformation").setValue(userInformation);
                                                                progressDialog.dismiss();

                                                                Toast.makeText(getApplicationContext(), "Welcome", Toast.LENGTH_SHORT).show();
                                                                startActivity(new Intent(Register.this, Home.class));
                                                                finish();
                                                            } else {
                                                                Toast.makeText(getApplicationContext(), "Oops!! Try again later!", Toast.LENGTH_SHORT).show();
                                                                tts.speak("Details incorrect. tap on the screen and say username again", TextToSpeech.QUEUE_FLUSH, null);
                                                                numberOfClicks = 0;

                                                            }
                                                        }

                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                                            Log.d("error",e.getMessage());
                                                        }
                                                    });

//                                            finish();
//                                            startActivity(new Intent(Register.this, Loginuser.class));
                                        }
                                    }, 3000);
                                    break;
                                }

                                if (res.contains("no")) {
                                    tts.speak("registration unsuccessful. restarting.", TextToSpeech.QUEUE_FLUSH, null);
                                    final Handler hh = new Handler();
                                    hh.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            finish();
                                            Intent i = new Intent(Register.this, Register.class);
                                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(i);
                                        }
                                    }, 5000);
                                    break;
                                }
                            }
                    }
                }

            }
        }

    }
    @Override
    public boolean onTouchEvent(MotionEvent touchEvent) {
        switch (touchEvent.getAction()) {

            case MotionEvent.ACTION_DOWN:
                x1 = touchEvent.getX();
                break;
            case MotionEvent.ACTION_UP:
                x2 = touchEvent.getX();
                if (x1 < x2) {
                     break;
                }
                if (x1 > x2) {

                    break;
                }

                break;
        }

        return false;
    }


    @Override
    protected void onPause() {
        if (tts != null) {
            tts.stop();
        }
        super.onPause();

    }



}