package org.tensorflow.lite.examples.detection.Moneytransfer;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.tensorflow.lite.examples.detection.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;


public class phonetransfer extends AppCompatActivity {

    private TextToSpeech tts;
    private TextView status;
    private TextView To, Subject, To2;
    private int numberOfClicks;
    float x1, x2;
    private boolean IsInitialVoiceFinshed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phone_transfer);
        IsInitialVoiceFinshed = false;
        tts = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = tts.setLanguage(Locale.UK);
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "This Language is not supported");
                }
                tts.speak("Welcome to Phone transfer. tap on the screen , Tell me the Name of person to whom you want to transfer money?", TextToSpeech.QUEUE_FLUSH, null);
                new Handler().postDelayed(() -> IsInitialVoiceFinshed = true, 8500);
            } else {
                Log.e("TTS", "Initilization Failed!");
            }
        });

        status = findViewById(R.id.status);
        To = findViewById(R.id.to);
        Subject = findViewById(R.id.subject);
        To2 = findViewById(R.id.to2);
        numberOfClicks = 0;
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
        if (IsInitialVoiceFinshed) {
            numberOfClicks++;
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
            Toast.makeText(phonetransfer.this, "Your device doesn't support Speech Recognition", Toast.LENGTH_SHORT).show();
        }
    }


    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && IsInitialVoiceFinshed) {
            IsInitialVoiceFinshed = false;
            if (resultCode == RESULT_OK && null != data) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if (result != null && result.get(0).contains("cancel")) {
                    tts.speak("Transaction Cancelled!", TextToSpeech.QUEUE_FLUSH, null);

                }
            } else {
                switch (numberOfClicks) {
                    case 1:
                        tts.speak("Tap on the screen & say the phone number", TextToSpeech.QUEUE_FLUSH, null);

                        break;
                    case 2:
                        tts.speak("tap on the screen & say, how much money you want to transfer", TextToSpeech.QUEUE_FLUSH, null);

                        break;
                    case 3:
                        tts.speak("Please Confirm the details, Name of account holder is: " + To2.getText().toString() + ",Phone number is " + Arrays.toString(To.getText().toString().replace("0", "zero").split("(?!^)")) + ". And Money that you want to transfer is ,: " + Subject.getText().toString() + ",swipe left to listen again, or say Yes to confirm or no to cancel the transaction", TextToSpeech.QUEUE_FLUSH, null);

                        break;

                    default:
                        tts.speak("say yes to proceed the transaction or no to cancel the transaction", TextToSpeech.QUEUE_FLUSH, null);
                        break;
                }
                numberOfClicks--;
            }
        }
        IsInitialVoiceFinshed = true;
    }


    public boolean onTouchEvent(MotionEvent touchEvent) {
        switch (touchEvent.getAction()) {

            case MotionEvent.ACTION_DOWN:
                x1 = touchEvent.getX();
                break;
            case MotionEvent.ACTION_UP:
                x2 = touchEvent.getX();
                if (x1 < x2) {
                    tts.speak("Please Confirm the details, Name of account holder is: " + To2.getText().toString() + ",Phone number is " + Arrays.toString(To.getText().toString().replace("0", "zero").split("(?!^)")) + ". And Money that you want to transfer is ,: " + Subject.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
                    tts.speak(",swipe left to listen again, and say Yes to confirm or no to cancel the transaction", TextToSpeech.QUEUE_ADD, null);
                    break;
                }
                if (x1 > x2) {

                    break;
                }

                break;
        }

        return false;
    }

    public void onPause() {
        if (tts != null) {
            tts.stop();
        }
        super.onPause();

    }


}