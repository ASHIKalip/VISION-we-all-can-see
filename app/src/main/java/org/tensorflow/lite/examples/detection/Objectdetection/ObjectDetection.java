package org.tensorflow.lite.examples.detection.Objectdetection;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;

import org.tensorflow.lite.examples.detection.R;

import java.util.Locale;

public class ObjectDetection extends AppCompatActivity {

    private TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_object_detection);
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.UK);
                     textToSpeech.speak("Welcome to object detection, while detecting object if you want to return in main menu, then press long on the screen", TextToSpeech.QUEUE_FLUSH,null);
                }

                final Handler h = new Handler(Looper.getMainLooper());
                h.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent i = new Intent(ObjectDetection.this, DetectorActivity.class);
                        startActivity(i);
                    }
                },8500);
            }
        });
    }
    public void onPause() {
        if (textToSpeech != null) {
            textToSpeech.stop();
        }
        super.onPause();

    }
}