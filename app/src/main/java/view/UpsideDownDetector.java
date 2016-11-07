package view;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

/**
 * Created by Brais on 07/11/2016.
 */

public class UpsideDownDetector implements Detector {

    OnRandomSelectListener mListener;

    public void setOnRandomSelectListener(OnRandomSelectListener listener){this.mListener = listener;}

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // ignore
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
    }
}
