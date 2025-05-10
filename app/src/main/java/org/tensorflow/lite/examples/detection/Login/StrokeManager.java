package org.tensorflow.lite.examples.detection.Login;

import static org.tensorflow.lite.examples.detection.Login.DigitalInkMainActivity.drawingView;
import static org.tensorflow.lite.examples.detection.Login.DigitalInkMainActivity.textToSpeech;
import static org.tensorflow.lite.examples.detection.Login.DigitalInkMainActivity.userintt;

import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.mlkit.vision.digitalink.Ink;
import com.google.mlkit.vision.digitalink.Ink.Point;

import org.tensorflow.lite.examples.detection.DBHandler;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/** Manages the recognition logic and the content that has been added to the current page. */
public class StrokeManager {

    //static ArrayList<Integer> phoneno = new ArrayList<>();
    static List<String> list1 = new LinkedList<>();// for register activity we required password
    static List<String> Pin = new LinkedList<>();
    boolean isClearCurrentInkAfterRecognition = true;
    DBHandler dbHandler;


    int count = 0;//for register activity. each digit increment
    int pincount = 0; // for setting pin

    /** Interface to register to be notified of changes in the recognized content. */
    public interface ContentChangedListener {


        /** This method is called when the recognized content changes. */
        void onContentChanged();
    }

    /** Interface to register to be notified of changes in the status. */
    public interface StatusChangedListener {

        /** This method is called when the recognized content changes. */
        void onStatusChanged();
    }

    public interface StatusChangedListener1 {

        /** This method is called when the recognized content changes. */
        void onStatusChanged1();
    }


    /** Interface to register to be notified of changes in the downloaded model state. */
    public interface DownloadedModelsChangedListener {

        /** This method is called when the downloaded models changes. */
        void onDownloadedModelsChanged(Set<String> downloadedLanguageTags);
    }

    @VisibleForTesting
    static final long CONVERSION_TIMEOUT_MS = 1000;
    private static final String TAG = "MLKD.StrokeManager";
    // This is a constant that is used as a message identifier to trigger the timeout.
    private static final int TIMEOUT_TRIGGER = 1;
    static String num;
    // For handling recognition and model downloading.
    private RecognitionTask recognitionTask = null;
    @VisibleForTesting
    ModelManager modelManager = new ModelManager();
    // Managing the recognition queue.
    private final List<RecognitionTask.RecognizedInk> content = new ArrayList<>();
    // Managing ink currently drawn.
    private Ink.Stroke.Builder strokeBuilder = Ink.Stroke.builder();
    private Ink.Builder inkBuilder = Ink.builder();
    private boolean stateChangedSinceLastRequest = false;
    @Nullable
    private ContentChangedListener contentChangedListener = null;
    @Nullable
    private StatusChangedListener statusChangedListener = null;
    @Nullable
    private StatusChangedListener1 statusChangedListener1 = null;
    @Nullable
    private DownloadedModelsChangedListener downloadedModelsChangedListener = null;
    private boolean triggerRecognitionAfterInput = true;
    private boolean clearCurrentInkAfterRecognition = true;
    private String status = "";
    private String status1 = "";

    public void setTriggerRecognitionAfterInput(boolean shouldTrigger) {
        triggerRecognitionAfterInput = shouldTrigger;
    }

    public void setClearCurrentInkAfterRecognition(boolean shouldClear) {
        clearCurrentInkAfterRecognition = shouldClear;

    }


    // Handler to handle the UI Timeout.
    // This handler is only used to trigger the UI timeout. Each time a UI interaction happens,
    // the timer is reset by clearing the queue on this handler and sending a new delayed message (in
    // addNewTouchEvent).

    private final Handler uiHandler =
            new Handler(
                    msg -> {
                        if (msg.what == TIMEOUT_TRIGGER) {
                            Log.i(TAG, "Handling timeout trigger.");
                            commitResult();
                            return true;
                        }
/**In the current use this statement is never reached because we only ever send
 //TIMEOUT_TRIGGER messages to this handler.
 // This line is necessary because otherwise Java's static analysis doesn't allow for compiling. Returning false indicates that a message wasn't handled.*/
                        return false;
                    });

    private void setStatus(String newStatus) {
        status = newStatus;
        if (statusChangedListener != null) {
            statusChangedListener.onStatusChanged();
        }

    }

    private void setStatus1(String newStatus) {
        status1 = newStatus;
        dbHandler = new DBHandler(drawingView.getContext());
        if (statusChangedListener1 != null) {
            statusChangedListener1.onStatusChanged1();

        }

    }


    private void commitResult() {
        dbHandler = new DBHandler(drawingView.getContext());

        if (recognitionTask.done() && recognitionTask.result() != null) {
            content.add(recognitionTask.result());
            setStatus("Successful recognition: " + recognitionTask.result().text);
            Toast.makeText(drawingView.getContext(), String.valueOf(userintt), Toast.LENGTH_LONG).show();
            num = recognitionTask.result().text;
            num = num.replace("o", "0");
            num = num.replace("g", "9");
            num = num.replace("I", "1");
            num = num.replace("l", "1");
            num = num.replaceAll("[^0-9]", "");

            if (num.isEmpty()) {
                if (userintt == 2 && list1.size() == 6) {
                    textToSpeech.stop();
                    textToSpeech.shutdown();
                } else if (userintt == 0 && Pin.size() == 4) {
                    textToSpeech.stop();
                    textToSpeech.shutdown();
                }
                textToSpeech.speak("Error write again", TextToSpeech.QUEUE_FLUSH, null);
                reset();
                DigitalInkMainActivity.cleardraw();

            } else if (userintt == 2 && list1.size() < 6) {
                list1.add(num);
                setStatus1(String.valueOf(list1.size()));
                count++;
                if (count == 1) {
                    textToSpeech.speak("write 2nd digit", TextToSpeech.QUEUE_FLUSH, null);

                }
                if (count == 2) {
                    textToSpeech.speak("write 3rd digit", TextToSpeech.QUEUE_FLUSH, null);

                }
                if (count == 3) {
                    textToSpeech.speak("write 4th digit", TextToSpeech.QUEUE_FLUSH, null);

                }
                if (count == 4) {
                    textToSpeech.speak("write 5th digit", TextToSpeech.QUEUE_FLUSH, null);
                }
                if (count == 5) {
                    textToSpeech.speak("write 6th digit", TextToSpeech.QUEUE_FLUSH, null);

                }

                DigitalInkMainActivity.Toaststring("passwd" + list1.toString());


            }
           else if (userintt == 2 && list1.size() == 6) {
                isClearCurrentInkAfterRecognition = false;
                DigitalInkMainActivity.Toaststring(getphoneno().toString());
            }
            else if (userintt == 0 && Pin.size() < 4) {
                Pin.add(num);
                setStatus1(String.valueOf(Pin.size()));
                pincount++;
                if (pincount == 1) {
                    textToSpeech.speak("write 2nd digit pin", TextToSpeech.QUEUE_FLUSH, null);
                }
                if (pincount == 2) {
                    textToSpeech.speak("write 3rd digit pin", TextToSpeech.QUEUE_FLUSH, null);
                }
                if (pincount == 3) {
                    textToSpeech.speak("write 4th digit pin", TextToSpeech.QUEUE_FLUSH, null);
                }

                DigitalInkMainActivity.Toaststring("pin " + Pin.toString());
                if (Pin.size() == 4) {
                    isClearCurrentInkAfterRecognition = false;
                    DigitalInkMainActivity.Toaststring(getPin().toString());
                }

            }
            final Handler h = new Handler();
            h.postDelayed(new Runnable() {
                @Override
                public void run() {
                    reset();
                    DigitalInkMainActivity.cleardraw();
                }
            }, 500);
            if (clearCurrentInkAfterRecognition) {
                resetCurrentInk();
                isClearCurrentInkAfterRecognition = true;
            }
            if (contentChangedListener != null) {
                contentChangedListener.onContentChanged();
            }
        }
    }

    public static List<String> getphoneno() {
        return list1;
    }

    public static List<String> getPin() {
        return Pin;
    }

    public String getnum() {
        return num;
    }


    public void reset() {
        Log.i(TAG, "reset");
        resetCurrentInk();
        content.clear();
        if (recognitionTask != null && !recognitionTask.done()) {
            recognitionTask.cancel();
        }
        setStatus("");
    }

    private void resetCurrentInk() {
        inkBuilder = Ink.builder();
        strokeBuilder = Ink.Stroke.builder();
        stateChangedSinceLastRequest = false;
    }

    public Ink getCurrentInk() {


        return inkBuilder.build();
    }

    /**
     * This method is called when a new touch event happens on the drawing client and notifies the
     * StrokeManager of new content being added.
     *
     * <p>This method takes care of triggering the UI timeout and scheduling recognitions on the
     * background thread.
     *
     * @return whether the touch event was handled.
     */
    public boolean addNewTouchEvent(MotionEvent event) {

        int action = event.getActionMasked();
        float x = event.getX();
        float y = event.getY();
        long t = System.currentTimeMillis();
        // A new event happened -> clear all pending timeout messages.
        uiHandler.removeMessages(TIMEOUT_TRIGGER);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                strokeBuilder.addPoint(Point.create(x, y, t));
                break;
            case MotionEvent.ACTION_UP:
                strokeBuilder.addPoint(Point.create(x, y, t));
                inkBuilder.addStroke(strokeBuilder.build());
                strokeBuilder = Ink.Stroke.builder();
                stateChangedSinceLastRequest = true;
                recognize();

                if (triggerRecognitionAfterInput) {
                    recognize();

                }
                break;
            default:

                // Indicate touch event wasn't handled.
                return false;
        }

        return true;
    }

    // Listeners to update the drawing and status.
    public void setContentChangedListener(ContentChangedListener contentChangedListener) {
        this.contentChangedListener = contentChangedListener;
    }

    public void setStatusChangedListener(StatusChangedListener statusChangedListener) {
        this.statusChangedListener = statusChangedListener;
    }

    public void setStatusChangedListener1(StatusChangedListener1 statusChangedListener1) {
        this.statusChangedListener1 = statusChangedListener1;
    }


    public void setDownloadedModelsChangedListener(
            DownloadedModelsChangedListener downloadedModelsChangedListener) {
        this.downloadedModelsChangedListener = downloadedModelsChangedListener;
    }

    public List<RecognitionTask.RecognizedInk> getContent() {
        return content;
    }


    public String getStatus() {
        return status;
    }

    public String getStatus1() {
        return status1;
    }


    // Model downloading / deleting / setting.

    public void setActiveModel(String languageTag) {
        setStatus(modelManager.setModel(languageTag));

    }


    public Task<Void> download() {
        setStatus("Download started.");
        return modelManager
                .download()
                .addOnSuccessListener(unused -> refreshDownloadedModelsStatus())
                .onSuccessTask(
                        status -> {
                            setStatus(status);
                            return Tasks.forResult(null);
                        });
    }

    // Recognition-related.

    public Task<String> recognize() {

        if (!stateChangedSinceLastRequest || inkBuilder.isEmpty()) {
            setStatus("No recognition, ink unchanged or empty");
            return Tasks.forResult(null);
        }
        if (modelManager.getRecognizer() == null) {
            setStatus("Recognizer not set");
            return Tasks.forResult(null);
        }

        return modelManager
                .checkIsModelDownloaded()
                .onSuccessTask(
                        result -> {
                            if (!result) {
                                setStatus("Model not downloaded yet");
                                return Tasks.forResult(null);
                            }

                            stateChangedSinceLastRequest = false;
                            recognitionTask =
                                    new RecognitionTask(modelManager.getRecognizer(), inkBuilder.build());
                            uiHandler.sendMessageDelayed(
                                    uiHandler.obtainMessage(TIMEOUT_TRIGGER), CONVERSION_TIMEOUT_MS);
                            return recognitionTask.run();
                        });
    }

    public void refreshDownloadedModelsStatus() {
        modelManager
                .getDownloadedModelLanguages()
                .addOnSuccessListener(
                        downloadedLanguageTags -> {
                            if (downloadedModelsChangedListener != null) {
                                downloadedModelsChangedListener.onDownloadedModelsChanged(downloadedLanguageTags);
                            }
                        });


    }

}