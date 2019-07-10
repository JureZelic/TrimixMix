package si.gounitis.trimixmix.util;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import si.gounitis.trimixmix.model.SensorData;
import si.gounitis.trimixmix.model.TrimixData;
import si.gounitis.trimixmix.model.Voltages;

public class Utils {

    private static String TEST_STRING = "{\"ads0mv\":7.123,\"ads1mv\":2.325,\"ads2mv\":0.01,\"ads3mv\":0.01,\"adr0mv\":0.01,\"adr1mv\":0.01,\"adr2mv\":0.01,\"adr3mv\":0.01}";

    public static Voltages toJson(String measureString) {
        // todo - remove - just for test
        //measureString = TEST_STRING;

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
        SensorData firstSensor, secondSensor;

        if (trimixData.isOxygenFirstMode()) {
            if (trimixData.getRedSensor().getFractionOxygen()<20.9f && trimixData.getGreenSensor().getFractionOxygen()<20.9f) {
                trimixData.setCalculated(false);
                return;
            }
            if (trimixData.getRedSensor().getFractionOxygen() > trimixData.getGreenSensor().getFractionOxygen()) {
                firstSensor = trimixData.getRedSensor();
                secondSensor = trimixData.getGreenSensor();
            } else {
                firstSensor = trimixData.getGreenSensor();
                secondSensor = trimixData.getRedSensor();
            }
            trimixData.setFractionOxygen(secondSensor.getFractionOxygen());
            float fHe = 100 - secondSensor.getFractionOxygen()*100/firstSensor.getFractionOxygen();
            trimixData.setFractionHelium(fHe);
            trimixData.setCalculated((trimixData.getFractionHelium()+trimixData.getFractionOxygen())<100f && trimixData.getFractionHelium()>0f && trimixData.getFractionOxygen()>0);
        } else {
            throw new UnsupportedOperationException();
        }
    }
}