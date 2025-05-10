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

public class Features extends AppCompatActivity {
    private static final int REQ_CODE_SPEECH_INPUT = 100;
    private TextView mVoiceInputTv;
    float x1, x2, y1, y2;

    private static TextToSpeech textToSpeech;
    private DBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main7);
        dbHandler = new DBHandler(this);

        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {

            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.US);
                    textToSpeech.setSpeechRate(1f);
                    textToSpeech.speak("say read for read, calculator for calculator, Weather for weather, Location for location, Battery, Time and date. say bank transfer. or, say phone transfer, to transfer the amount. say object detection to detect the object.say scan the qr, to scan qr of product, say Reminder to create reminder. Say navigation, to navigate to the destination. say exit for closing the application.  Swipe right and say what you want ", TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });
        mVoiceInputTv = (TextView) findViewById(R.id.voiceInput);


    }


    public boolean onTouchEvent(MotionEvent touchEvent) {
        switch (touchEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x1 = touchEvent.getX();
                y1 = touchEvent.getY();
                break;
            case MotionEvent.ACTION_UP:
                x2 = touchEvent.getX();
                y2 = touchEvent.getY();
                if (x1 > x2) {
                    textToSpeech.stop();
                    startVoiceInput();
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

                if (mVoiceInputTv.getText().toString().contains("read")) {
                    Intent intent = new Intent(getApplicationContext(), OCRReader.class);
                    startActivity(intent);
                    mVoiceInputTv.setText(null);
                }
                else if (mVoiceInputTv.getText().toString().contains("calculator")) {
                    Intent intent = new Intent(getApplicationContext(), Calculator.class);
                    startActivity(intent);
                    mVoiceInputTv.setText(null);
                }
                else if (mVoiceInputTv.getText().toString().contains("time and date")) {
                    Intent intent = new Intent(getApplicationContext(), DateAndTime.class);
                    startActivity(intent);
                    mVoiceInputTv.setText(null);
                }
                else if (mVoiceInputTv.getText().toString().contains("weather")) {
                    Intent intent = new Intent(getApplicationContext(), Weather.class);
                    startActivity(intent);
                    mVoiceInputTv.setText(null);
                }

                else if (mVoiceInputTv.getText().toString().contains("battery")) {
                    Intent intent = new Intent(getApplicationContext(), Battery.class);
                    startActivity(intent);
                    mVoiceInputTv.setText(null);
                }

                else if (mVoiceInputTv.getText().toString().contains("location")) {
                    Intent intent = new Intent(getApplicationContext(), LocationActivity.class);
                    startActivity(intent);
                    mVoiceInputTv.setText(null);
                }
                else if (mVoiceInputTv.getText().toString().contains("bank transfer")) {
                    Intent i = new Intent(Features.this, Banktransfer.class);
                    startActivity(i);
                }
                else if (mVoiceInputTv.getText().toString().contains("phone transfer")) {
                    Intent i = new Intent(Features.this, phonetransfer.class);
                    startActivity(i);
                }
                else if (mVoiceInputTv.getText().toString().contains("object detection")) {
                    Intent i = new Intent(Features.this, ObjectDetection.class);
                    startActivity(i);
                }
                else if (mVoiceInputTv.getText().toString().contains("scan the QR") || mVoiceInputTv.getText().toString().contains("QR code")) {
                    Intent i = new Intent(Features.this, QRactivity.class);
                    startActivity(i);
                } else if (mVoiceInputTv.getText().toString().contains("message")) {
                    Intent i = new Intent(this, MessageReader.class);
                    startActivity(i);
                } else if (mVoiceInputTv.getText().toString().contains("music")) {
                    Intent i = new Intent(this, Music.class);
                    startActivity(i);
                }
                else if (mVoiceInputTv.getText().toString().contains("exit")) {
                    onPause();
                    finishAffinity();
                }
                else if (mVoiceInputTv.getText().toString().contains("navigation")) {
                    Intent i = new Intent(Features.this, Navigation.class);
                    startActivity(i);
                }
                else {
                    textToSpeech.speak("do not understand, please say again",TextToSpeech.QUEUE_FLUSH,null);
                }
            }
        }
    }

    public void onDestroy() {
        if (mVoiceInputTv.getText().toString().contains("exit")) {
            finish();
        }
        super.onDestroy();
    }

    public void onPause() {
        if (textToSpeech != null) {
            textToSpeech.stop();
        }
        super.onPause();

    }
}

