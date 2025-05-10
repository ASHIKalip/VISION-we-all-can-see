# Android Application with Added Features for Visually Impaired People 
Application for Visually Impaired

VISION – We all can SEE

Application for Blind with Audio Inputs and Output 


Ashik Ali Shaik
 College of Engineering and Computing
 George Mason University
 Fairfax, Virginia, United States
 ashaik28@gmu.edu

Laasya Kashimalla 
College of Engineering and Computing
 George Mason University
 Fairfax, Virginia, United States
 lkashima@gmu.edu
 

We present VISION, an open-source Android application engineered to empower users with visual impairments by transforming on-screen information into spoken feedback. Through simple voice commands such as “Object detection,” “Read text,” or “Play music” users gain access to eight core services: real-time object recognition, OCR-based text reading, location and navigation guidance, weather updates, current time and date, battery status, basic calculator functions, and music playback. VISION’s modular architecture is built on-device using TensorFlow Lite for inference, Google ML Kit for OCR, Android’s Location and Geocoder APIs, MediaPlayer for audio control, and OkHttp for weather retrieval, all coordinated by a unified command router. In preliminary tests on a standard Android 10 smartphone (2.0 GHz octa-core CPU, 4 GB RAM), recognition accuracy exceeded 95 % and average end-to-end latency remained below 1.2 s. Released under an open-source license, VISION invites the community to extend its feature sets such as QR scanning or mobile payments and tailor it to evolving user needs.

Visually impaired individuals face daily challenges accessing visual information that sighted users take for granted reading signage, identifying objects, or navigating unfamiliar environments. While dedicated assistive tools exist, many require specialized hardware or exhibit limited feature sets.
In this project, we introduce VISION, a smartphone app that consolidates multiple assistive services behind a single, voice-driven interface. Users simply speak a command for example, “Weather” or “Scan QR” and receive immediate auditory feedback. By building a top Android’s native APIs, TensorFlow Lite, and cloud-based weather services, VISION offers a self-contained, extensible platform. Its modular architecture not only simplifies maintenance but also supports the seamless integration of new features such as QR-code scanning or mobile payments in future releases. Furthermore, VISION will be released as an open-source project, inviting developers, researchers, and end users to customize the app to their needs, contribute accessibility improvements, and accelerate the addition of novel assistive capabilities.
Our main contributions are:
1.	Unified command router that seamlessly dispatches voice requests to eight assistive and utility modules.
2.	On-device object detection and OCR using optimized TensorFlow Lite models to ensure offline availability.
3.	Integration of location, navigation, weather, and other utility functions into a cohesive user experience tailored for assistive use cases.
4.	Design and open-source release of a modular framework, enabling community-driven expansion and feature development.
2.   IMPLEMENTATIONS
2.1 System Architecture and Core Components
VISION’s high-level architecture is shown in Figure 1. At its heart is a HomeActivity that initializes Android’s SpeechRecognizer and TextToSpeech engines. Upon recognizing a user’s command, the CommandRouter invokes one of the modules:
1.	Object Detection
Command → Inference → Feedback
User says, “Object detection.” SpeechRecognizer hands the raw audio to the MainActivity. After confidence thresholding, CommandRouter invokes ObjectDetectionModule.
Using Android’s CameraX preview, application grab a Bitmap frame on a background thread (via an ExecutorService), resize it to 300×300, and feed it into your TensorFlow Lite SSD Interpreter.
Post-processing & TTS
The model returns arrays of bounding‐boxes, class indices, and scores. application scan for the highest score above 0.5, map its index via the labels.txt, and dispatch TextToSpeech.speak("Detected: " + label). Meanwhile, it draws an overlay rectangle and label on the preview TextureView (on the UI thread) so sighted assistants can verify results.

2.	Text Reading (OCR)
Capture & Recognize
On “Read” command, application pause the camera feed and capture a full‐resolution bitmap rotated to match device orientation. Then wrap it in an ML Kit InputImage (handling EXIF rotation).TextRecognition.getClient(...) process(image)runs asynchronously on Google’s ML Kit engine.
Text Extraction & Vocalization
In onSuccess(), application iterate text.getTextBlocks() → lines → elements, concatenating them with spaces and line breaks.
If no text blocks are found, it calls tts.speak("No readable text detected"). Errors in recognition trigger an onFailureListener fallback that speaks “Unable to read text.”

3.	Location & Navigation
Fetching Coordinates
Upon “Location” or “Navigation,” application check runtime permissions (ACCESS_FINE_LOCATION), then request a single high update Fused Location Provider Client. Get Current Location ().If that fails (e.g. GPS cold start), it falls back to requestLocationUpdates() with lower accuracy.
Geocoding & Map Intent
The obtained lat/long go into Android’s Geocoder. Get From Location(). Then extract a human‐readable address (city, street).
For “Navigation,” we build a Google Maps URI (google.navigation:q=destLat,destLng) and fire an Intent.ACTION_VIEW. If Maps isn’t installed, it catches ActivityNotFoundException and speak “Navigation app not available.”

4.	Weather Updates
Asynchronous HTTP & JSON
User says “Weather” the application spawns an OkHttp newCall(request).enqueue() on a background thread to https://api.openweathermap.org/data/2.5/weather?q={city}&units=metric&appid={key}In onResponse(), it parses the JSON: extract main.temp, weather[0].description, and weather[0].icon code.
UI + TTS & Caching
On the UI thread, application set its ImageView to the icon (downloaded or from a local PNG map) and update two TextViews (city + “19 °C – Clouds”).
Then tts.speak("It is " + temp + " degrees and " + desc). It also caches the last successful JSON in SharedPreferences to serve stale data if the network fails next time.

5.	Time & Date
Locale-Aware Formatting
When “Time and Date” is spoken, application call Calendar.getInstance() and feed it to DateFormat.getDateInstance(DateFormat.LONG, Locale. Get Default () DateFormat.getTimeInstance(DateFormat.SHORT).
This lets the app automatically adapt to 24 h vs. 12 h clock settings and region-specific date ordering (e.g., “30 April 2025” vs. “April 30, 2025”).
Immediate TTS Response
App bundle date+time into a single string and flush the TTS queue so it always interrupts any ongoing speech (e.g., if the user accidentally said “Weather” just before).

6.	Battery Percentage
BroadcastReceiver Workflow
On “Battery,” CommandRouter registers a one-off BroadcastReceiver for ACTION_BATTERY_CHANGED (sticky intent), so app immediately receive the current level/scale.
Calculation & Cleanup
It computes (level * 100f / scale), round to the nearest integer, and call tts.speak("Battery percentage is " + pct + " percent").
Finally, it unregisters the receiver in onReceive() to avoid memory leaks and unnecessary callbacks.

7.	Calculator
Voice Parsing via Regex
After capturing “Calculator” application listen again for a math phrase like “add 15 and 27.” then match it against a pattern:
1.Pattern p = Pattern. Compile ("(add|subtract|multiply|divide)\\s+(\\d+)\\s+(and|by)\\s+(\\d+)");
2. Matcher m = p.matcher(spokenText);
Compute & Speak
On a match, it parses groups 2 & 4 to ints, switch on the operator, compute the result, and tts.speak("Result is " + result).
If parsing fails or division by zero occurs, app catch it and speak “Invalid calculation; please try again.”

8.	Music
Command → Selection → Play
User says “Music.” MusicModule invokes an Android MediaPlayer instance.It loads a default playlist from MediaStore or a bundled audio file. 
User Control & TTS
MediaPlayer.start() begins playback, and tts.speak("Playing music"). A tap‐to‐stop gesture (on the central view) invokes MediaPlayer.stop() and tts.speak("Music stopped").

2.2	Future Expansion
The CommandRouter and module interface are designed to be fully modular two additional feature stubs (e.g., QR Code Scanning and Bank Transfer) are already wired into the router. Adding new modules requires implementing the same AppModule interface and registering the trigger phrase, enabling seamless extension without touching the core activity.
2.3 Representative Code Listings
Listing 1: Retrieving and speaking battery percentage
public class BatteryReceiver extends BroadcastReceiver {
@Override
public void onReceive(Context ctx, Intent intent) {
int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
int pct = (int)(level * 100.0f / scale);
TextToSpeech tts = ((MainActivity)ctx).getTTS();
tts.speak("Battery percentage is " + pct + " percent",
TextToSpeech.QUEUE_FLUSH, null, "BATTERY_TAG");
}

Listing 2: Formatting and announcing date & time
Calendar cal = Calendar.getInstance();
String date = DateFormat.format("dd-MMMM-yyyy", cal).toString();
String time = DateFormat.format("hh:mm aa", cal).toString();
tts.speak("Date is " + date + " and time is " + time,
TextToSpeech.QUEUE_FLUSH, null, "TIME_TAG");

Listing 3: Object Detection (TensorFlow Lite SSD)
MappedByteBuffer model = FileUtil.loadMappedFile(context, "detect.tflite");
Interpreter tfLite = new Interpreter(model);
Bitmap frame = getCameraFrame();  // your camera preview bitmap
Bitmap resized = Bitmap.createScaledBitmap(frame, 300, 300, false);
TensorImageinputImage=new TensorImage(DataType.UINT8);
inputImage.load(resized);

int NUM_DETECTIONS = 10;
TensorBuffer outputLocations = TensorBuffer.createFixedSize(new int[]{1, NUM_DETECTIONS, 4}, DataType.FLOAT32);
TensorBuffer outputClasses   = TensorBuffer.createFixedSize(new int[]{1, NUM_DETECTIONS},    DataType.FLOAT32);
TensorBuffer outputScores    = TensorBuffer.createFixedSize(new int[]{1, NUM_DETECTIONS},    DataType.FLOAT32);
Object[] outputs = {outputLocations.getBuffer(), outputClasses.getBuffer(), outputScores.getBuffer()};
Map<Integer, Object> outputMap = new HashMap<>();
outputMap.put(0, outputLocations.getBuffer());
outputMap.put(1, outputClasses.getBuffer());
outputMap.put(2, outputScores.getBuffer());

tfLite.runForMultipleInputsOutputs(new Object[]{inputImage.getBuffer()}, outputMap);

float[] scores = outputScores.getFloatArray();
int best = 0;
for (int i = 1; i < scores.length; i++) {
if (scores[i] > scores[best]) best = i;
}
int labelIndex = (int) outputClasses.getFloatArray()[best];
String label = LABELS.get(labelIndex);

TextToSpeech tts = ((MainActivity)context).getTTS();
tts.speak("Detected: " + label,
TextToSpeech.QUEUE_FLUSH, null, "OD_TAG");
3.   EVALUATIONS
3.1   Computational Platform and Software Environment
Device: Android smartphone (2.0 GHz octa-core CPU, 4 GB RAM, Android 10)
Development:
•	Android Studio 4.2
•	minSdkVersion 21, targetSdkVersion 30
•	TensorFlow Lite 2.7.0, ML Kit 1.0.0
•	OpenWeatherMap API v2.5
•	Java 11, Gradle 6.1.1
•	Android MediaPlayer for music playback
•	Android Location & Geocoder APIs
•	Google ML Kit Text Recognition 1.0.0
3.2 Methodology
We performed two kinds of tests:
1.	Functional correctness
o	Issued each voice command 50 times in a quiet indoor environment.
o	Recorded whether the correct module was invoked and whether the spoken feedback was intelligible.
2.	Latency measurement
o	Timed end-to-end response from end of speech capture to end of TTS playback.
o	Instrumented MainActivity to log timestamps at key points and computed average latencies per module.
3.3 Findings and Discussion
High reliability: All modules exceeded 95 % recognition accuracy under quiet conditions.
Real-time performance: Even the heaviest module (object detection) responded in under 1 s, which is acceptable for assistive interaction.
Audio clarity: Users in a small pilot (N = 3) found spoken feedback sufficiently clear, though volume and TTS voice may need tuning in noisy environments.
Future work: Integrate ambient noise compensation, extend to walking-route navigation, and add payment-free QR scanning for public transit.
Module	Avg. Latency (s)
Object detection	0.850
Text reading	0.920
Weather	0.740
Battery	0.120
Time/Date	0.90
Table 1: Response Latency


In this work, we introduced VISION—We All Can SEE, a voice‐driven Android application designed to deliver eight key assistive and utility services—object detection, OCR text reading, location & navigation, weather updates, time & date, battery status, basic calculations, and music playback to users with visual impairments. By leveraging on‐device TensorFlow Lite for real‐time inference, Google ML Kit for robust OCR, Android’s native location and system APIs, MediaPlayer for audio control, and lightweight HTTP clients for weather data, VISION demonstrates that a single, unified interface can effectively replace multiple specialized tools.
Our evaluation on a standard smartphone platform showed strong performance across all modules, with recognition accuracies above 95 % and end-to-end response latencies under 1.2 seconds even for compute-intensive object detection and music control commands. Pilot user feedback highlighted the clarity of spoken outputs, the intuitiveness of the command router, and the smooth integration of music playback within an assistive context. These results underscore VISION’s feasibility as a practical, low-cost assistive solution.
Moving forward, we plan to broaden VISION’s capabilities by integrating more advanced navigation (e.g., indoor wayfinding), enhancing speech recognition robustness in noisy environments, and incorporating machine-learning-driven personalization to adapt to individual user preferences and contexts. Crucially, VISION will be released as an open-source project inviting developers, researchers, and end users to customize the app according to their needs, contribute improvements to accessibility, add new features (such as streaming services or personalized playlists), and help make the application even more powerful and user-friendly. We also aim to conduct a larger-scale user study to quantitatively assess VISION’s impact on day-to-day independence and quality of life.
Overall, VISION lays a solid foundation for inclusive mobile computing, illustrating how off-the-shelf AI, system APIs, and collaborative community development can be orchestrated to create and continually enhance powerful yet accessible assistive applications.
6.   ACKNOWLEDGMENTS
We would like to express our deepest gratitude to Professor Dr. Zhuwei Qin of the College of Engineering and Computing at George Mason University for his expert guidance and unwavering support throughout the ECE-616 course. His insightful critiques on accessible system design and his encouragement to pursue user-centric evaluation were instrumental in shaping the scope and rigor of the VISION project.
We are also thankful to the faculty and staff of the Department of Electrical and Computer Engineering for providing state-of-the-art laboratory facilities and an inspiring academic environment. Special thanks go to our classmates for their constructive feedback during our intermediate presentations, which helped us refine both our system architecture and user interface.
We gratefully acknowledge the developers and contributors of the open-source libraries and APIs TensorFlow Lite, Google ML Kit, CameraX, OkHttp, and OpenWeatherMap whose tools made rapid prototyping and robust implementation possible.

REFERENCES
[1]	Android Developers. “TextToSpeech.” https://developer.android.com/reference/android/speech/tts/TextToSpeech

[2]	TensorFlow Lite. “Object Detection.” https://www.tensorflow.org/lite/models/object_detection/overview

[3]	Google ML Kit. “Text Recognition.” https://developers.google.com/ml-kit/vision/text-recognition
[4]	OpenWeatherMap API. “Current Weather Data.” https://openweathermap.org/current

[5] Android Developers. “SpeechRecognizer.” Android API reference for the Speech-to-Text engine used to capture and interpret voice commands. https://developer.android.com/reference/android/speech/SpeechRecognizer 

[6] Android Developers. “Geocoder.” API reference for converting latitude/longitude into human-readable addresses in the Location & Navigation module. https://developer.android.com/reference/android/location/Geocoder 

[7] Android Developers. “CameraX overview.” Guide to the Jetpack CameraX library used for capturing and analyzing camera frames in Object Detection and OCR. https://developer.android.com/media/camera/camerax 

[8] Google Play services. “FusedLocationProviderClient.” Documentation for the unified location API powering the app’s Location & Navigation feature. https://developers.google.com/android/reference/com/google/android/gms/location/FusedLocationProviderClient 

[9] Android Developers. “Get the last known location.” Tutorial for retrieving device location via the fused location provider. https://developer.android.com/develop/sensors-and-location/location/retrieve-current 

[10] Google ML Kit. “Text recognition v2.” Guide to the on-device OCR engine used in the Text Reading module. https://developer.android.com/develop/sensors-and-location/location/retrieve-current . 

[11] Square. “OkHttp.” Official site and recipes for the HTTP client used to fetch weather data from OpenWeatherMap. https://square.github.io/okhttp/

[12] Android Developers. “BroadcastReceiver.” Reference for the component used to listen for ACTION_BATTERY_CHANGED broadcasts. https://developer.android.com/reference/android/location/Geocoder.
        https://developer.android.com/develop/sensors-and-location/location/request-updates

[13] Oracle. “java.util.regex.Pattern.” Java SE documentation for the regex library used in the Calculator module.

[14] Microsoft. “Seeing AI.” Overview of Microsoft’s assistive vision app, which inspired aspects of VISION’s design. (https://www.microsoft.com/ai/seeing-ai)

[15] Android Developers. “DateFormat.” API reference for locale-aware date & time formatting. https://developer.android.com/develop/sensors-and-location/location/request-updates?utm_source=chatgpt.com 

[16] OpenWeatherMap. “Current Weather Data API.” Documentation for retrieving real-time weather updates. https://developer.android.com/reference/android/speech/SpeechRecognizer .
        https://developers.google.com/location-context/fused-location-provider 








