package view;

import android.hardware.Sensor;
import android.hardware.SensorEvent;

/**
 * Created by brais on 9/11/16.
 */

public class RotationDetector implements Detector {

    private static final int GIRAR_REQUIRED_TIME = 1000;
    private static final int ROTATION_TIMES_NEEDED = 2;

    private OnRandomSelectListener mListener;
    private long mTimestamp;
    char lastOrientation;
    int orientationChangesCounter = 0;

    public void setOnRandomSelectListener(OnRandomSelectListener listener){this.mListener = listener;}

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // ignore
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        char actualOrientation = getOrientation(x,y);

        if (actualOrientation == 'o')
            return;

        final long now = System.currentTimeMillis();
        //
        if (actualOrientation != lastOrientation){
            lastOrientation = actualOrientation;
            if (orientationChangesCounter == 0)
                mTimestamp = now;
            orientationChangesCounter++;
        }
        // Si se cambia de orientación el número de veces necesario hay que resetear y lanzar la acción.
        if ((orientationChangesCounter >= ROTATION_TIMES_NEEDED)) {
            mListener.onRandomSelect();
            orientationChangesCounter = 0;
        }
        // Si pasa el tiempo máximo se resetea el contador.
        if (now - mTimestamp > GIRAR_REQUIRED_TIME)
            orientationChangesCounter = 0;
    }

    /**
     * Devuelve la orientación (vertical, horizontal u otro) en función de su posicón x e y.
     * @param x : eje x según el acelerómetro.
     * @param y : eje y según el acelerómetro.
     * @return 'v' = vertical, 'h' = horizonal, 'o' = otro.
     */
    private char getOrientation(float x, float y){
        if ((x > -2 && x < 2) && (y > 7 && y < 13))
            return 'v';
        else if ((y > -2 && y < 2) && (x > 7 && x < 13))
            return 'h';
        else
            return 'o';
    }
}
