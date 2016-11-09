package view;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

/**
 * Created by Brais on 07/11/2016.
 */

public class UpsideDownDetector implements Detector {

    private OnRandomSelectListener mListener;
    private long mTimestamp;
    private boolean mAntes = false;

    public void setOnRandomSelectListener(OnRandomSelectListener listener){this.mListener = listener;}

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // ignore
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float dis = event.values[0];
        long now = System.currentTimeMillis();
        boolean mAhora;

        if (dis < 10)
            mAhora = true;
        else
            mAhora = false;

        if (mAhora == true)
            mTimestamp = now;

        if (mAhora == false && mAntes == true) {
            long tiempoTranscurrido = now - mTimestamp;
            if (tiempoTranscurrido > 1000)
                mListener.onRandomSelect();
        }
        mAntes = mAhora;
    }
}
