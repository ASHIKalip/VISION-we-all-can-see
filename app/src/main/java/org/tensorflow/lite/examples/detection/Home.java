package org.tensorflow.lite.examples.detection;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.MotionEvent;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.tensorflow.lite.examples.detection.Location.LocationActivity;
import org.tensorflow.lite.examples.detection.Message.MessageReader;
import org.tensorflow.lite.examples.detection.Moneytransfer.Banktransfer;
import org.tensorflow.lite.examples.detection.Moneytransfer.phonetransfer;
import org.tensorflow.lite.examples.detection.Music.Music;
import org.tensorflow.lite.examples.detection.Navigation.Navigation;
import org.tensorflow.lite.examples.detection.Objectdetection.ObjectDetection;
import org.tensorflow.lite.examples.detection.QRProduct.QRactivity;

import java.util.ArrayList;
import java.util.Locale;

public class Home extends AppCompatActivity {

    private static final int REQ_CODE_SPEECH_INPUT = 100;
    private static int firstTime = 0;
    private TextView mVoiceInputTv;
    float x1, x2, y1, y2;
    private static TextToSpeech textToSpeech;
    private DBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHandler = new DBHandler(this);

        textToSpeech = new TextToSpeech(this, status -> {
            if (status != TextToSpeech.ERROR) {
                textToSpeech.setLanguage(Locale.US);
                textToSpeech.setSpeechRate(1f);
                if (firstTime == 0)
                    textToSpeech.speak("Welcome to Blind App. Swipe left to listen the features of the app and swipe right and say what you want", TextToSpeech.QUEUE_FLUSH, null);
                //when user return from another activities to main activities.
                if (firstTime != 0)
                    textToSpeech.speak("you are in main menu. just swipe right and say what you want", TextToSpeech.QUEUE_FLUSH, null);

            }
        });


        mVoiceInputTv = (TextView) findViewById(R.id.voiceInput);

    }

    public boolean onTouchEvent(MotionEvent touchEvent) {
        firstTime = 1;
        switch (touchEvent.getAction()) {

            case MotionEvent.ACTION_DOWN:
                x1 = touchEvent.getX();
                y1 = touchEvent.getY();
                break;
            case MotionEvent.ACTION_UP:
                x2 = touchEvent.getX();
                y2 = touchEvent.getY();
                if (x1 < x2) {
                    firstTime = 1;
                    Intent intent = new Intent(Home.this, Features.class);
                    startActivity(intent);
                }
                if (x1 > x2) {
                    startVoiceInput();
                    break;
                }


                break;
        }

        return false;
    }


    private void startVoiceInput() {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hello, How can I help you?");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            a.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_SPEECH_INPUT) {
            if (resultCode == RESULT_OK && null != data) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                mVoiceInputTv.setText(result.get(0));
                if (mVoiceInputTv.getText().toString().equals("exit")) {
                    finishAffinity();
                    System.exit(0);
                }
                if (mVoiceInputTv.getText().toString().equals("read")) {
                    Intent intent = new Intent(getApplicationContext(), OCRReader.class);
                    startActivity(intent);
                    mVoiceInputTv.setText(null);
                } else {
                    textToSpeech.speak("Do not understand just Swipe right Say again", TextToSpeech.QUEUE_FLUSH, null);

                }
                if (mVoiceInputTv.getText().toString().equals("calculator")) {
                    Intent intent = new Intent(getApplicationContext(), Calculator.class);
                    startActivity(intent);
                    mVoiceInputTv.setText(null);
                } else {
                    textToSpeech.speak("Do not understand just Swipe right Say again", TextToSpeech.QUEUE_FLUSH, null);
                }
                if (mVoiceInputTv.getText().toString().equals("time and date")) {
                    Intent intent = new Intent(getApplicationContext(), DateAndTime.class);
                    startActivity(intent);
                    mVoiceInputTv.setText(null);
                } else {
                    textToSpeech.speak("Do not understand just Swipe right  Say again", TextToSpeech.QUEUE_FLUSH, null);
                }
                if (mVoiceInputTv.getText().toString().equals("weather")) {
                    Intent intent = new Intent(getApplicationContext(), Weather.class);
                    startActivity(intent);
                    mVoiceInputTv.setText(null);
                } else {
                    textToSpeech.speak("Do not understand just Swipe right Say again", TextToSpeech.QUEUE_FLUSH, null);
                }

                if (mVoiceInputTv.getText().toString().equals("battery")) {
                    Intent intent = new Intent(getApplicationContext(), Battery.class);
                    startActivity(intent);
                    mVoiceInputTv.setText(null);
                } else {
                    textToSpeech.speak("Do not understand Swipe right Say again", TextToSpeech.QUEUE_FLUSH, null);
                }
                if (mVoiceInputTv.getText().toString().equals("yes")) {
                    textToSpeech.speak("  Say Read for reading,  calculator for calculator,  time and date,  weather for weather,  battery for battery. Do you want to listen again", TextToSpeech.QUEUE_FLUSH, null);
                    mVoiceInputTv.setText(null);
                } else if ((mVoiceInputTv.getText().toString().equals("no"))) {
                    textToSpeech.speak("then Swipe right and say what you want", TextToSpeech.QUEUE_FLUSH, null);

                } else if (mVoiceInputTv.getText().toString().equals("location")) {

                    Intent intent = new Intent(getApplicationContext(), LocationActivity.class);
                    startActivity(intent);
                    mVoiceInputTv.setText(null);
                }
                if (mVoiceInputTv.getText().toString().contains("bank transfer")) {
                    Intent i = new Intent(Home.this, Banktransfer.class);
                    startActivity(i);
                } else if (mVoiceInputTv.getText().toString().contains("phone transfer")) {
                    Intent i = new Intent(Home.this, phonetransfer.class);
                    startActivity(i);
                } else {
                    textToSpeech.speak("Do not understand just Swipe right Say again", TextToSpeech.QUEUE_FLUSH, null);
                }
                if (mVoiceInputTv.getText().toString().contains("object detection")) {
                    Intent i = new Intent(Home.this, ObjectDetection.class);
                    startActivity(i);
                }
                if (mVoiceInputTv.getText().toString().contains("navigation")) {
                    Intent i = new Intent(Home.this, Navigation.class);
                    startActivity(i);
                }
                if (result.get(0).contains("scan the QR") || result.get(0).contains("QR")) {
                    Intent i = new Intent(Home.this, QRactivity.class);
                    startActivity(i);
                } else if (mVoiceInputTv.getText().toString().contains("message")) {
                    Intent i = new Intent(Home.this, MessageReader.class);
                    startActivity(i);
                } else if (mVoiceInputTv.getText().toString().contains("music")) {
                    Intent i = new Intent(Home.this, Music.class);
                    startActivity(i);
                } else if (mVoiceInputTv.getText().toString().contains("exit")) {
                    mVoiceInputTv.setText(null);
                    finishAffinity();
                }

            }
        }


    }

    public void onPause() {
        if (textToSpeech != null) {
            textToSpeech.stop();
        }
        super.onPause();

    }
}
