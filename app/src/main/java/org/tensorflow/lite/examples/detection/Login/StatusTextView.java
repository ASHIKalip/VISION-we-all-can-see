package org.tensorflow.lite.examples.detection.Login;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;

import org.tensorflow.lite.examples.detection.Login.StrokeManager;

/**
 * Status bar for the test app.
 *
 * <p>It is updated upon status changes announced by the StrokeManager.
 */
public class StatusTextView extends androidx.appcompat.widget.AppCompatTextView implements StrokeManager.StatusChangedListener {

    private StrokeManager strokeManager;

    public StatusTextView(@NonNull Context context) {
        super(context);
    }

    public StatusTextView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override
    public void onStatusChanged() {
        this.setText(this.strokeManager.getStatus());
     }

    public void setStrokeManager(StrokeManager strokeManager) {
        this.strokeManager = strokeManager;
    }
}
