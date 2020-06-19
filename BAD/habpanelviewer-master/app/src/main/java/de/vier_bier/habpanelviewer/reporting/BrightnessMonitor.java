package de.vier_bier.habpanelviewer.reporting;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;

import de.vier_bier.habpanelviewer.R;
import de.vier_bier.habpanelviewer.openhab.ServerConnection;

/**
 * Monitors brightness sensor state and reports to openHAB.
 */
public class BrightnessMonitor extends SensorMonitor {
    private boolean mDoAverage;
    private int mInterval;

    public BrightnessMonitor(Context ctx, SensorManager sensorManager, ServerConnection serverConnection) throws SensorMissingException {
        super(ctx, sensorManager, serverConnection, "brightness", Sensor.TYPE_LIGHT);
    }

    protected synchronized void addStatusItems() {
        if (mStatus == null) {
            return;
        }

        if (mSensorEnabled) {
            String state = mCtx.getString(R.string.enabled);
            if (!mSensorItem.isEmpty()) {
                final String brightness = mServerConnection.getState(mSensorItem);
                state += "\n" + mCtx.getString(R.string.brightness) + " : " + brightness + " lx [" + mSensorItem + "=" + brightness + "]";
            }

            mStatus.set(mCtx.getString(R.string.pref_brightness), state);
        } else {
            mStatus.set(mCtx.getString(R.string.pref_brightness), mCtx.getString(R.string.disabled));
        }
    }

    @Override
    public synchronized void updateFromPreferences(SharedPreferences prefs) {
        if (mDoAverage != prefs.getBoolean("pref_brightness_average", true)) {
            mDoAverage = prefs.getBoolean("pref_brightness_average", true);
        }

        if (mInterval != Integer.parseInt(prefs.getString("pref_brightness_intervall", "60"))) {
            mInterval = Integer.parseInt(prefs.getString("pref_brightness_intervall", "60"));
        }

        super.updateFromPreferences(prefs);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int brightness = (int) event.values[0];
        if (mDoAverage) {
            mServerConnection.addStateToAverage(mSensorItem, brightness, mInterval);
        } else {
            mServerConnection.updateState(mSensorItem, String.valueOf(brightness));
        }
    }
}
