package com.samsungschool.umbrellaproject.widgets;

import static android.content.Context.SENSOR_SERVICE;
import static com.samsungschool.umbrellaproject.BuildConfig.DEBUG;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.Surface;
import android.view.WindowManager;

import androidx.annotation.Nullable;

public class Compass implements SensorEventListener {

    // Tag
    private static final String TAG = Compass.class.getSimpleName();

    // Constants
    private static final float ROTATION_VECTOR_SMOOTHING_FACTOR = 0.5f;
    private static final float GEOMAGNETIC_SMOOTHING_FACTOR = 0.4f;
    private static final float GRAVITY_SMOOTHING_FACTOR = 0.1f;

    // Context
    private final Context mContext;

    // Sensors
    private final SensorManager mSensorManager;
    private final Sensor mRotationVectorSensor;
    private final Sensor mMagnetometerSensor;
    private final Sensor mAccelerometerSensor;

    // RotationVectorSensor is more precise than Magnetic+Accelerometer, but on some devices it is not working
    private boolean mUseRotationVectorSensor = false;

    // Orientation
    @SuppressWarnings("FieldCanBeLocal")
    private float mAzimuthDegrees;
    private float mPitchDegrees;
    private float mRollDegrees;
    private float[] mRotationVector = new float[5];
    private float[] mGeomagnetic = new float[3];
    private float[] mGravity = new float[3];

    // Listener
    private CompassListener mCompassListener;

    // The minimum difference in degrees with the last orientation value for the CompassListener to be notified
    private float mAzimuthSensibility;
    private float mPitchSensibility;
    private float mRollSensibility;

    // The last orientation value sent to the CompassListener
    private float mLastAzimuthDegrees;
    private float mLastPitchDegrees;
    private float mLastRollDegrees;


    public interface CompassListener {
        void onOrientationChanged(float azimuth, float pitch, float roll);
    }

    // Private constructor
    public Compass(Context context, CompassListener compassListener) {
        mContext = context;

        // Sensors
        mSensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        mMagnetometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mAccelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mRotationVectorSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        // Listener
        mCompassListener = compassListener;
    }


    @Nullable
    public static Compass newInstance(Context context, CompassListener compassListener) {
        Compass compass = new Compass(context, compassListener);
        if (compass.hasRequiredSensors()) {
            return compass;
        } else {
            return null;
        }
    }

    // Check that the device has the required sensors
    private boolean hasRequiredSensors() {
        if (mRotationVectorSensor != null) {
            if (DEBUG) Log.d(TAG, "Sensor.TYPE_ROTATION_VECTOR found");
            return true;
        } else if (mMagnetometerSensor != null && mAccelerometerSensor != null) {
            if (DEBUG) Log.d(TAG, "Sensor.TYPE_MAGNETIC_FIELD and Sensor.TYPE_ACCELEROMETER found");
            return true;
        } else {
            if (DEBUG) Log.d(TAG, "The device does not have the required sensors");
            return false;
        }
    }

    public void start(float azimuthSensibility, float pitchSensibility, float rollSensibility) {
        mAzimuthSensibility = azimuthSensibility;
        mPitchSensibility = pitchSensibility;
        mRollSensibility = rollSensibility;

        if (mRotationVectorSensor != null) {
            mSensorManager.registerListener(this, mRotationVectorSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (mMagnetometerSensor != null) {
            mSensorManager.registerListener(this, mMagnetometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (mAccelerometerSensor != null) {
            mSensorManager.registerListener(this, mAccelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }


    public void start() {
        start(0, 0, 0);
    }


    public void stop() {
        mAzimuthSensibility = 0;
        mPitchSensibility = 0;
        mRollSensibility = 0;
        mCompassListener = null;
        mSensorManager.unregisterListener(this);
    }

    // SensorEventListener
    @Override
    public void onSensorChanged(SensorEvent event) {
        synchronized (this) {
            // Get the orientation array with Sensor.TYPE_ROTATION_VECTOR if possible (more precise), otherwise with Sensor.TYPE_MAGNETIC_FIELD and Sensor.TYPE_ACCELEROMETER combined
            float[] orientation = new float[3];
            if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
                // Only use rotation vector sensor if it is working on this device
                if (!mUseRotationVectorSensor) {
                    if (DEBUG)
                        Log.d(TAG, "Using Sensor.TYPE_ROTATION_VECTOR (more precise compass data)");
                    mUseRotationVectorSensor = true;
                }
                // Smooth values
                mRotationVector = exponentialSmoothing(event.values, mRotationVector, ROTATION_VECTOR_SMOOTHING_FACTOR);
                // Calculate the rotation matrix
                float[] rotationMatrix = new float[9];
                SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);
                // Calculate the orientation
                SensorManager.getOrientation(rotationMatrix, orientation);
            } else if (!mUseRotationVectorSensor &&
                    (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD || event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)) {
                if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
                    mGeomagnetic = exponentialSmoothing(event.values, mGeomagnetic, GEOMAGNETIC_SMOOTHING_FACTOR);
                }
                if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                    mGravity = exponentialSmoothing(event.values, mGravity, GRAVITY_SMOOTHING_FACTOR);
                }
                // Calculate the rotation and inclination matrix
                float[] rotationMatrix = new float[9];
                float[] inclinationMatrix = new float[9];
                SensorManager.getRotationMatrix(rotationMatrix, inclinationMatrix, mGravity, mGeomagnetic);
                // Calculate the orientation
                SensorManager.getOrientation(rotationMatrix, orientation);
            } else {
                return;
            }

            // Calculate azimuth, pitch and roll values from the orientation[] array
            // Correct values depending on the screen rotation
            final int screenRotation = (((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()).getRotation();
            mAzimuthDegrees = (float) Math.toDegrees(orientation[0]);
            if (screenRotation == Surface.ROTATION_0) {
                mPitchDegrees = (float) Math.toDegrees(orientation[1]);
                mRollDegrees = (float) Math.toDegrees(orientation[2]);
                if (mRollDegrees >= 90 || mRollDegrees <= -90) {
                    mAzimuthDegrees += 180;
                    mPitchDegrees = mPitchDegrees > 0 ? 180 - mPitchDegrees : -180 - mPitchDegrees;
                    mRollDegrees = mRollDegrees > 0 ? 180 - mRollDegrees : -180 - mRollDegrees;
                }
            } else if (screenRotation == Surface.ROTATION_90) {
                mAzimuthDegrees += 90;
                mPitchDegrees = (float) Math.toDegrees(orientation[2]);
                mRollDegrees = (float) -Math.toDegrees(orientation[1]);
            } else if (screenRotation == Surface.ROTATION_180) {
                mAzimuthDegrees += 180;
                mPitchDegrees = (float) -Math.toDegrees(orientation[1]);
                mRollDegrees = (float) -Math.toDegrees(orientation[2]);
                if (mRollDegrees >= 90 || mRollDegrees <= -90) {
                    mAzimuthDegrees += 180;
                    mPitchDegrees = mPitchDegrees > 0 ? 180 - mPitchDegrees : -180 - mPitchDegrees;
                    mRollDegrees = mRollDegrees > 0 ? 180 - mRollDegrees : -180 - mRollDegrees;
                }
            } else if (screenRotation == Surface.ROTATION_270) {
                mAzimuthDegrees += 270;
                mPitchDegrees = (float) -Math.toDegrees(orientation[2]);
                mRollDegrees = (float) Math.toDegrees(orientation[1]);
            }

            // Force azimuth value between 0° and 360°.
            mAzimuthDegrees = (mAzimuthDegrees + 360) % 360;

            // Notify the compass listener if needed
            if (Math.abs(mAzimuthDegrees - mLastAzimuthDegrees) >= mAzimuthSensibility
                    || Math.abs(mPitchDegrees - mLastPitchDegrees) >= mPitchSensibility
                    || Math.abs(mRollDegrees - mLastRollDegrees) >= mRollSensibility
                    || mLastAzimuthDegrees == 0) {
                mLastAzimuthDegrees = mAzimuthDegrees;
                mLastPitchDegrees = mPitchDegrees;
                mLastRollDegrees = mRollDegrees;
                mCompassListener.onOrientationChanged(mAzimuthDegrees, mPitchDegrees, mRollDegrees);
            }
        }
    }

    // SensorEventListener
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Nothing to do
    }

    private float[] exponentialSmoothing(float[] newValue, float[] lastValue, float alpha) {
        float[] output = new float[newValue.length];
        if (lastValue == null) {
            return newValue;
        }
        for (int i = 0; i < newValue.length; i++) {
            output[i] = lastValue[i] + alpha * (newValue[i] - lastValue[i]);
        }
        return output;
    }
}
