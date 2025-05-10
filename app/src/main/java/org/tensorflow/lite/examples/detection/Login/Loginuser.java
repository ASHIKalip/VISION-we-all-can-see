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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.tensorflow.lite.examples.detection.DBHandler;
import org.tensorflow.lite.examples.detection.Home;
import org.tensorflow.lite.examples.detection.R;

import java.util.ArrayList;
import java.util.Locale;

public class Loginuser extends AppCompatActivity {

    private TextToSpeech tts;
    private TextView status;
     private TextView username;
     @SuppressLint("StaticFieldLeak")
     static TextView password;
    static int numberOfClicks;
    @VisibleForTesting
    final StrokeManager strokeManager = new StrokeManager();
    DBHandler dbHandler;
    static String to;
    @SuppressLint("StaticFieldLeak")
    static DigitalInkMainActivity cdd;
    float x1, x2;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginuser);

        if(!DigitalInkMainActivity.ModelLanguageContainer.downloaded){
            strokeManager.download();
            Toast.makeText(getApplicationContext(), "Model is downloading", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(getApplicationContext(), "Model already downloaded", Toast.LENGTH_SHORT).show();
        }
        strokeManager.download();

        progressDialog = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();

        cdd = new DigitalInkMainActivity(Loginuser.this,2);

        tts = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = tts.setLanguage(Locale.UK);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "This Language is not supported");
                    }

                    else  {
                     tts.speak("Welcome to app.swipe left and say the username for login, or swipe right for registration", TextToSpeech.QUEUE_FLUSH, null);
                 }
            }
            }
        });
        //check user already login
         if(firebaseAuth.getCurrentUser() != null){
             tts.shutdown();
            finish();
             Intent i = new Intent(Loginuser.this,Home.class);
             i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(i);
        }

        dbHandler = new DBHandler(this);


        status = (TextView) findViewById(R.id.status);
         username = (TextView) findViewById(R.id.username);
         password = findViewById(R.id.password);

        numberOfClicks = 0;
    }

    @Override
    public boolean onTouchEvent(MotionEvent touchEvent) {
         switch (touchEvent.getAction()) {

            case MotionEvent.ACTION_DOWN:
                x1 = touchEvent.getX();
                 break;
            case MotionEvent.ACTION_UP:
                x2 = touchEvent.getX();
                if(x1<x2){
                    if(!tts.isSpeaking()) {
                        numberOfClicks++;
                        listen();
                    }
                }
                if (x1 > x2) {
                    //startVoiceInput();
                    Intent intent = new Intent(Loginuser.this, Register.class);
                    startActivity(intent);
//                    MainActivity mainActivity = new MainActivity(this);
//                    mainActivity.show();
                    break;
                }
                break;
        }

        return false;
    }

    @Override
    public void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    private void listen() {
        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say something");

        try {
            startActivityForResult(i, 100);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(Loginuser.this, "Your device doesn't support Speech Recognition", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && !tts.isSpeaking() ) {
             if (resultCode == RESULT_OK && null != data) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if (result.get(0).contains("cancel")) {
                    tts.speak("  Cancelled!", TextToSpeech.QUEUE_FLUSH, null);

                } else {

                    switch (numberOfClicks) {
                        case 1:
                            String name;
                            name = result.get(0);
                            username.setText(name);

                            if (!username.getText().toString().isEmpty()) {
                                status.setText("confirm");
                                final Handler h = new Handler();
                                h.postDelayed(new Runnable() {
                                    @RequiresApi(api = Build.VERSION_CODES.O)
                                    @Override
                                    public void run() {
                                        cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                        cdd.show();
                                    }
                                },1000);


                            }
                            if (username.getText().toString().isEmpty()) {
                                numberOfClicks--;
                            }

                            break;

                        default:
                            if(username.getText().toString().isEmpty()){
                                String user = result.get(0);
                                user=user.replace("yes","");
                                user=user.replace("no","");
                                username.setText(user);
                            }
                            if (result.get(0).equals("yes")) {
                                String email = username.getText().toString()+"@gmail.com";
                                String pass = password.getText().toString();
                                firebaseAuth.signInWithEmailAndPassword(email, pass)
                                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                progressDialog.dismiss();
                                                if (task.isSuccessful()) {
                                                    // Start Dashboard Activity
                                                    Toast.makeText(Loginuser.this, "successfully login", Toast.LENGTH_SHORT).show();
                                                    tts.speak("login successfully. ",TextToSpeech.QUEUE_FLUSH,null);
                                                    final Handler h = new Handler();
                                                    h.postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Intent i1 = new Intent(getApplicationContext(),Home.class);
                                                            startActivity(i1);
                                                        }
                                                    },3000);
                                                }
                                                else
                                                {
                                                    Toast.makeText(getApplicationContext(), "Oops!! Try again later!", Toast.LENGTH_SHORT).show();
                                                    tts.speak("Details incorrect. tap on the screen and say username again",TextToSpeech.QUEUE_FLUSH,null);
                                                    numberOfClicks=0;

                                                }
                                            }
                                        });

                            }
                            if(result.get(0).contains("no")){
                                finish();
                             startActivity(new Intent(this,Loginuser.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                            }

                    }

                }

            }
        }

    }

    @Override
    protected void onPause() {
        if (tts != null) {
            tts.stop();
        }
        super.onPause();

    }

}