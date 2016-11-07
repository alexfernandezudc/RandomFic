package view;

import android.hardware.SensorEventListener;

/**
 * Created by Brais on 07/11/2016.
 */

public interface Detector extends SensorEventListener{
    public void setOnRandomSelectListener(OnRandomSelectListener listener);
}
