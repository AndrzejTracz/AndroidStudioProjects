package edu.utexas.scottm.rotationvectorsimpledemo;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

public class RotationVectorSimpleDemo extends Activity {

    private SensorManager mSensorManager;
    private Sensor mRotationVectorSensor;
    private TextView[] sensorValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rotation_vector_simple_demo);

        // Get an instance of the SensorManager
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        getTextViews();
    }

    private void getTextViews() {
        int[] ids = {R.id.x_value, R.id.y_value, R.id.z_value,
                R.id.cos_value, R.id.heading_value};
        sensorValues = new TextView[5];
        for(int i = 0; i < sensorValues.length; i++)
            sensorValues[i] = (TextView) this.findViewById(ids[i]);
    }


    @Override
    protected void onResume() {
        // Ideally a game should implement onResume() and onPause()
        // to take appropriate action when the activity loses focus
        super.onResume();
        mRotationVectorSensor = mSensorManager.getDefaultSensor(
                Sensor.TYPE_ROTATION_VECTOR);
        mSensorManager.registerListener(new RotationListener(),
                mRotationVectorSensor, 200000);
    }

    @Override
    protected void onPause() {
        // Ideally a game should implement onResume() and onPause()
        // to take appropriate action when the activity loses focus
        super.onPause();

    }

    private  class RotationListener implements SensorEventListener {


        public void onSensorChanged(SensorEvent event) {
            // we received a sensor event. it is a good practice to check
            // that we received the proper event
            if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
                for(int i = 0; i < event.values.length; i++) {
                    float value = event.values[i];
                    value = ((int) (value * 100)) / 100f;
                    sensorValues[i].setText("" + value);
                }

//                // convert the rotation-vector to a 4x4 matrix. the matrix
//                // is interpreted by Open GL as the inverse of the
//                // rotation-vector, which is what we want.
//                SensorManager.getRotationMatrixFromVector(
//                        mRotationMatrix , event.values);

            }
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    }
}
