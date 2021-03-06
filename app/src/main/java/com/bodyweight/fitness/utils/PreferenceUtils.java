package com.bodyweight.fitness.utils;

import android.preference.PreferenceManager;

import com.bodyweight.fitness.App;
import com.bodyweight.fitness.Constants;
import com.bodyweight.fitness.model.WeightMeasurementUnit;

import com.bodyweight.fitness.R;

public class PreferenceUtils {
    private static class InstanceHolder {
        private static final PreferenceUtils mInstance = new PreferenceUtils();
    }

    public static PreferenceUtils getInstance() {
        return InstanceHolder.mInstance;
    }

    private PreferenceUtils() {
        PreferenceManager.setDefaultValues(App.getContext(), R.xml.settings, false);
    }

    public void setDefaultRoutine(String defaultRoutine) {
        PreferenceManager.getDefaultSharedPreferences(App.getContext())
                .edit()
                .putString(Constants.INSTANCE.getPREFERENCE_DEFAULT_ROUTINE(), defaultRoutine)
                .commit();
    }

    public String getDefaultRoutine() {
        return PreferenceManager.getDefaultSharedPreferences(App.getContext())
                .getString(Constants.INSTANCE.getPREFERENCE_DEFAULT_ROUTINE(), "routine0");
    }

    public WeightMeasurementUnit getWeightMeasurementUnit() {
        String value = PreferenceManager.getDefaultSharedPreferences(App.getContext())
                .getString(Constants.INSTANCE.getPREFERENCE_WEIGHT_MEASUREMENT_UNITS(), "kg");

        return WeightMeasurementUnit.get(value);
    }

    public boolean playSoundWhenTimerStops() {
        return PreferenceManager.getDefaultSharedPreferences(App.getContext())
                .getBoolean(Constants.INSTANCE.getPREFERENCE_PLAY_SOUND_WHEN_TIMER_STOPS(), true);
    }

    public boolean automaticallyLogWorkoutTime() {
        return PreferenceManager.getDefaultSharedPreferences(App.getContext())
                .getBoolean(Constants.INSTANCE.getPREFERENCE_AUTOMATICALLY_LOG_WORKOUT_TIME(), true);
    }

    public boolean keepScreenOnWhenAppIsRunning() {
        return PreferenceManager.getDefaultSharedPreferences(App.getContext())
                .getBoolean(Constants.INSTANCE.getPREFERENCE_KEEP_SCREEN_ON(), true);
    }

    public void setTimerValue(String exerciseId, long value) {
        PreferenceManager.getDefaultSharedPreferences(App.getContext())
                .edit()
                .putLong(String.format("%s%s", Constants.INSTANCE.getPREFERENCE_TIMER_KEY(), exerciseId), value)
                .commit();
    }

    public void setNumberOfReps(String exerciseId, int value) {
        PreferenceManager.getDefaultSharedPreferences(App.getContext())
                .edit()
                .putInt(String.format("%s%s", Constants.INSTANCE.getPREFERENCE_NUMBER_OF_REPS_KEY(), exerciseId), value)
                .commit();
    }

    public long getTimerValueForExercise(String exerciseId, long defaultValue) {
        return PreferenceManager.getDefaultSharedPreferences(App.getContext())
                .getLong(String.format("%s%s", Constants.INSTANCE.getPREFERENCE_TIMER_KEY(), exerciseId), defaultValue);
    }

    public int getNumberOfRepsForExercise(String exerciseId, int defaultValue) {
        return PreferenceManager.getDefaultSharedPreferences(App.getContext())
                .getInt(String.format("%s%s", Constants.INSTANCE.getPREFERENCE_NUMBER_OF_REPS_KEY(), exerciseId), defaultValue);
    }
}
