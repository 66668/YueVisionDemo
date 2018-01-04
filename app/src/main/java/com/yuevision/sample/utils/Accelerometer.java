package com.yuevision.sample.utils;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;


/**
 * 自定义重力传感器
 */

public class Accelerometer {

    private SensorManager mSensorManager = null;

    private boolean mHasStarted = false;

    private static CLOCKWISE_ANGLE sRotation;

    public enum CLOCKWISE_ANGLE {
        Deg0(0), Deg90(1), Deg180(2), Deg270(3);
        private int value;

        private CLOCKWISE_ANGLE(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    /**
     * @param ctx 用Activity初始化获得传感器
     */
    public Accelerometer(Context ctx) {
        mSensorManager = (SensorManager) ctx
                .getSystemService(Context.SENSOR_SERVICE);
        sRotation = CLOCKWISE_ANGLE.Deg0;
    }

    /**
     * 开始对传感器的监听
     */
    public void start() {
        if (mHasStarted)
            return;
        mHasStarted = true;
        sRotation = getDefaultDirection();
        mSensorManager.registerListener(mAccListener,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    private CLOCKWISE_ANGLE getDefaultDirection() {
        CLOCKWISE_ANGLE defaultAngle = CLOCKWISE_ANGLE.Deg0;
//        if (RomUtils.checkIsRockchip()) {
//            defaultAngle = CLOCKWISE_ANGLE.Deg180;
//        }
        return defaultAngle;
    }

    /**
     * 结束对传感器的监听
     */
    public void stop() {
        if (!mHasStarted)
            return;
        mHasStarted = false;
        mSensorManager.unregisterListener(mAccListener);
    }

    /**
     * @return 返回当前手机转向
     */
    static public int getDirection() {
        return sRotation.getValue();
    }

    /**
     * 传感器与手机转向之间的逻辑
     */
    private SensorEventListener mAccListener = new SensorEventListener() {

        @Override
        public void onAccuracyChanged(Sensor arg0, int arg1) {
        }

        @Override
        public void onSensorChanged(SensorEvent arg0) {
            if (arg0.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                float x = arg0.values[0];
                float y = arg0.values[1];
                float z = arg0.values[2];
                if (Math.abs(x) > 3 || Math.abs(y) > 3) {
                    if (Math.abs(x) > Math.abs(y)) {
                        if (x > 0) {
                            sRotation = CLOCKWISE_ANGLE.Deg0;
                        } else {
                            sRotation = CLOCKWISE_ANGLE.Deg180;
                        }
                    } else {
                        if (y > 0) {
                            sRotation = CLOCKWISE_ANGLE.Deg90;
                        } else {
                            sRotation = CLOCKWISE_ANGLE.Deg270;
                        }
                    }
                }
            }
        }
    };
}
