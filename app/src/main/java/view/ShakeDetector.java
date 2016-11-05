package view;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.FloatMath;

public class ShakeDetector implements SensorEventListener {

    /*
     * The gForce that is necessary to register as shake.
     * Must be greater than 1G (one earth gravity unit).
     * You can install "G-Force", by Blake La Pierre
     * from the Google Play Store and run it to see how
     *  many G's it takes to register a shake
     */
    private static final float SHAKE_THRESHOLD_GRAVITY = 2.7F;
    private static final int SHAKE_SLOP_TIME_MS = 500;
    private static final int SHAKE_COUNT_RESET_TIME_MS = 3000;
    private static final int UPSIDE_DOWN_REQUIRED_TIME = 2000;
    private static final int GIRAR_REQUIRED_TIME = 2000;
    private static final int ROTATION_TIMES_NEEDED = 2;

    private OnShakeListener mListener;
    private long mShakeTimestamp;
    private int mShakeCount;
    // Girar dos veces
    private char lastOrientation = 'o';
    private int orientationChangesCounter;
    // Boca abajo
    private boolean isUpsideDown = false;

    public void setOnShakeListener(OnShakeListener listener) {
        this.mListener = listener;
    }

    public interface OnShakeListener {
        public void onShake(int count);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // ignore
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (mListener != null) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            float gX = x / SensorManager.GRAVITY_EARTH;
            float gY = y / SensorManager.GRAVITY_EARTH;
            float gZ = z / SensorManager.GRAVITY_EARTH;

            // gForce will be close to 1 when there is no movement.
            float gForce = (float)Math.sqrt(gX * gX + gY * gY + gZ * gZ);

            switch (MainActivity.RANDOM_OPTION){
                case 0:
                    if (gForce > SHAKE_THRESHOLD_GRAVITY) {
                        final long now2 = System.currentTimeMillis();
                        // ignore shake events too close to each other (500ms)
                        if (mShakeTimestamp + SHAKE_SLOP_TIME_MS > now2) {
                            return;
                        }

                        // reset the shake count after 3 seconds of no shakes
                        if (mShakeTimestamp + SHAKE_COUNT_RESET_TIME_MS < now2) {
                            mShakeCount = 0;
                        }

                        mShakeTimestamp = now2;
                        mShakeCount++;

                        mListener.onShake(mShakeCount);
                    }
                    break;
                case 1:
                    char actualOrientation = getOrientation(x,y);
                    System.out.println(actualOrientation);
                    if (actualOrientation == 'o')
                        return;

                    final long now3 = System.currentTimeMillis();

                    if (actualOrientation != lastOrientation){
                        lastOrientation = actualOrientation;
                        if (orientationChangesCounter == 0)
                            mShakeTimestamp = now3;
                        orientationChangesCounter++;
                    }

                    if (orientationChangesCounter >= ROTATION_TIMES_NEEDED)
                        mListener.onShake(0);

                    if (now3 - mShakeTimestamp > GIRAR_REQUIRED_TIME){
                        orientationChangesCounter = 0;
                    }
                    break;
                case 2:
                    final long now = System.currentTimeMillis();
                    if (z > -5){ // Si el smartphone está boca abajo
                        if (isUpsideDown == false)  // Y acaba de cambiarse
                            mShakeTimestamp = now;  // Tomamos nota del momento
                        isUpsideDown = true;
                    } else {    // Si está boca arriba
                        if (now - mShakeTimestamp > UPSIDE_DOWN_REQUIRED_TIME) {  // Comprobamos que haya pasado el tiempo necesario
                            mListener.onShake(0);
                            mShakeTimestamp = now;
                        }
                        isUpsideDown = false;   // Anotamos que ahora ya no está boca abajo
                    }
                    break;
            }
        }
    }

    private char getOrientation(float x, float y){
        if ((x > -2 && x < 2) && (y > 7 && y < 13))
            return 'v';
        else if ((y > -2 && y < 2) && (x > 7 && x < 13))
            return 'h';
        else
            return 'o';
    }
}