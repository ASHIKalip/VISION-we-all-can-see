package org.tensorflow.lite.examples.detection.Login;

import static android.content.Context.MODE_PRIVATE;
import static org.tensorflow.lite.examples.detection.Login.DigitalInkMainActivity.userintt;
import static org.tensorflow.lite.examples.detection.Login.Loginuser.cdd;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.tensorflow.lite.examples.detection.DBHandler;
import org.tensorflow.lite.examples.detection.Home;
import org.tensorflow.lite.examples.detection.Moneytransfer.Banktransfer;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Status bar for the test app.
 *
 * <p>It is updated upon status changes announced by the StrokeManager.
 */
public class StatusTextView1 extends androidx.appcompat.widget.AppCompatTextView implements StrokeManager.StatusChangedListener1, TextToSpeech.OnUtteranceCompletedListener {
    StrokeManager strokeManager;
    private static TextToSpeech textToSpeech;
    static int Size, PinSize;
    ProgressDialog progressDialog;
    SharedPreferences sharedPreferences;
    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;
    FirebaseAuth auth;
    String authenticationPin;

    @SuppressLint("StaticFieldLeak")
    DBHandler dbHandler;

    public StatusTextView1(@NonNull Context context) {
        super(context);
        dbHandler = new DBHandler(getContext());
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Transferring money Please wait ...");
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();


    }

    public StatusTextView1(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

    }

    private static final String SHARED_PREF_NAME = "mypref";
    private static final String KEY_NAME = "name";


    public void setStrokeManager(StrokeManager strokeManager) {
        this.strokeManager = strokeManager;
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onStatusChanged1() {
        this.dbHandler = new DBHandler(getContext());
        sharedPreferences = getContext().getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);

        if (userintt == 0) {
            PinSize = Integer.parseInt(this.strokeManager.getStatus1());
            this.setText("pin " + this.strokeManager.getStatus1() + userintt);

        } else {
            Size = Integer.parseInt(this.strokeManager.getStatus1());
            this.setText(this.strokeManager.getStatus1());
        }

        textToSpeech = new TextToSpeech(getContext(), i -> {
            if (i != TextToSpeech.ERROR) {
                textToSpeech.setLanguage(Locale.US);
                textToSpeech.setSpeechRate((float) 0.7);
                textToSpeech.setOnUtteranceCompletedListener(StatusTextView1.this);
                // Here userintt = 2 i.e user is entering first time in the bank transfer so he needs to add his bank details
                if (userintt == 2 && StrokeManager.getphoneno().size() == 6) {
                    StringBuilder strbul = new StringBuilder();
                    for (String str : StrokeManager.getphoneno()) {
                        strbul.append(str);
                        //for adding comma between elements
                    }
                    if (cdd.isShowing()) {
                        String str = strbul.toString();
                        Loginuser.password.setText(str);
                        cdd.dismiss();
                        StrokeManager.getphoneno().clear();
                        textToSpeech.speak(" swipe left and say yes to confirm or no to cancel the login", TextToSpeech.QUEUE_FLUSH, null);
                        userintt = 0;
                    } else {
                        String str = strbul.toString();
                        Register.password.setText(str);
                        Register.cdd.dismiss();
                        StrokeManager.getphoneno().clear();
                        textToSpeech.speak("your password is set. Don't forgot the password.", TextToSpeech.QUEUE_FLUSH, null);
                        textToSpeech.speak(" tap on the screen and say yes to confirm or no to cancel the registration", TextToSpeech.QUEUE_ADD, null);
                        userintt = 0;

                    }


                }

                //here userintt =0 i.e user had entered the permissions now doing the bank transfer
                else if (userintt == 0 && StrokeManager.getPin().size() == 4) {
                    StringBuilder strbul = new StringBuilder();
                    for (String str : StrokeManager.getPin()) {
                        strbul.append(str);
                        //for adding comma between elements
                    }
                    String name = sharedPreferences.getString(KEY_NAME, null);
                    String str = strbul.toString();
                    databaseReference.child(firebaseUser.getUid()).child("Account information").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            ArrayList<String> values = new ArrayList<>();
                            for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                values.add(snapshot1.getValue().toString());
                            }
                            authenticationPin = values.get(4);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
//
//                    if (name != null && authenticationPin != null || str.equals(name) || str.equals(authenticationPin)) {
//                        progressDialog.show();
////                            Banktransfer.pin = str;
////                            Banktransfer.cdd.dismiss();
//                        StrokeManager.getPin().clear();
//                        userintt = 0;
//                        textToSpeech.speak("Authentication successful. transferring money please wait", TextToSpeech.QUEUE_FLUSH, null);
//                        final Handler h = new Handler();
//                        h.postDelayed(new Runnable() {
//                            @Override
//                            public void run() {
//                                textToSpeech.speak("Amount transferred successfully. returning to main menu", TextToSpeech.QUEUE_FLUSH, null, "menu");
//                                progressDialog.dismiss();
//                            }
//                        }, 7000);
//                    } else {
//                        progressDialog.dismiss();
////                            textToSpeech.speak("Authentication unsuccessful.Please write the correct pin", TextToSpeech.QUEUE_FLUSH, null);
////                            Banktransfer.cdd = new DigitalInkMainActivity(getContext(), 0);
////                            Banktransfer.cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
////                            Banktransfer.cdd.show();
//                    }

                }
//                StrokeManager.getPin().clear();
//                userintt = 0;
            }
        });

    }


    @Override
    public void onUtteranceCompleted(String s) {
        getContext().startActivity(new Intent(getContext(), Home.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
    }
}
