package si.gounitis.trimixmix.util;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import si.gounitis.trimixmix.model.SensorData;
import si.gounitis.trimixmix.model.TrimixData;
import si.gounitis.trimixmix.model.TrimixMode;
import si.gounitis.trimixmix.model.Voltages;

import static si.gounitis.trimixmix.model.TrimixMode.MODE_HELIUM_FIRST;
import static si.gounitis.trimixmix.model.TrimixMode.MODE_OXYGEN_FIRST;

public class Utils {
    public static Voltages toJson(String measureString) {
        // todo - remove - just for test
        measureString = "{\"ads0mv\":7.123,\"ads1mv\":2.325,\"ads2mv\":0.01,\"ads3mv\":0.01,\"adr0mv\":0.01,\"adr1mv\":0.01,\"adr2mv\":0.01,\"adr3mv\":0.01}";

        if (measureString==null) return null;

        try {
            JsonParser parser = new JsonParser();
            JsonElement mJson = parser.parse(measureString);
            Gson gson = new Gson();
            return gson.fromJson(mJson, Voltages.class);
        } catch (Exception e) {
            return null;
        }
    }

    public static void calibrate(SensorData sensorData) {
        // check if enough voltage
        if (sensorData.getSensorVoltage()<sensorData.getSensorMinVoltage() || sensorData.getSensorVoltage()>sensorData.getSensorMaxVoltage()) {
            sensorData.setTipText("Check sensor");
        }
        // calibrate
        sensorData.setSensorFactor(20.9f / (sensorData.getSensorVoltage()-sensorData.getSensorOffset()));
        sensorData.setCalibrated(true);
    }

    public static void calculateOxygen(SensorData sensorData) {
        if (sensorData.isCalibrated())
            sensorData.setFractionOxygen(sensorData.getSensorFactor()*(sensorData.getSensorVoltage()-sensorData.getSensorOffset()));
    }

    public static void calculateTrimix(TrimixData trimixData) {
        // red sensor is firs sensor
        SensorData afterOxygenSensor, afterHeliumSensor;
        TrimixMode trimixMode;

        if (trimixData.getRedSensor().getFractionOxygen()>=20.9f) {
            trimixMode = MODE_OXYGEN_FIRST;
            afterOxygenSensor = trimixData.getRedSensor();
            afterHeliumSensor = trimixData.getGreenSensor();
        } else if (trimixData.getGreenSensor().getFractionOxygen()>=20.9f) {
            trimixMode = MODE_HELIUM_FIRST;
            afterHeliumSensor = trimixData.getRedSensor();
            afterOxygenSensor = trimixData.getGreenSensor();
        } else {
            return;
        }

        trimixData.setFractionOxygen(trimixData.getGreenSensor().getFractionOxygen());

        if (MODE_OXYGEN_FIRST.equals(trimixMode)) {
            trimixData.setFractionHelium(0f); // todo
        } else {
            trimixData.setFractionHelium(0f); // todo
        }


    }
}