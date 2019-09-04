package si.gounitis.trimixmix.util;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import si.gounitis.trimixmix.model.SensorData;
import si.gounitis.trimixmix.model.SensorStatus;
import si.gounitis.trimixmix.model.TrimixData;
import si.gounitis.trimixmix.model.Voltages;

public class Utils {

    private static String TEST_STRING = "{\"ads0mv\":7.123,\"ads1mv\":3.325,\"ads2mv\":0.01,\"ads3mv\":0.01,\"adr0mv\":0.01,\"adr1mv\":0.01,\"adr2mv\":0.01,\"adr3mv\":0.01}";

    public static Voltages toJson(String measureString) {
        //measureString = TEST_STRING; // todo uncomment for test

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
        sensorData.setStatus(SensorStatus.CALIBRATING);

        // check if enough voltage
        if (sensorData.getSensorVoltage()<sensorData.getSensorMinVoltage()) {
            sensorData.setStatus(SensorStatus.VOLTAGE_LOW);
        } else if (sensorData.getSensorVoltage()>sensorData.getSensorMaxVoltage()) {
            sensorData.setStatus(SensorStatus.VOLTAGE_HIGH);
        } else {
            // calibrate
            sensorData.setSensorFactor(20.9f / (sensorData.getSensorVoltage() - sensorData.getSensorOffset()));
            sensorData.setStatus(SensorStatus.CALIBRATED);
        }
    }

    public static void calculateOxygen(SensorData sensorData) {
        if (sensorData.getStatus().equals(SensorStatus.CALIBRATED))
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
            float fHe = 100f - secondSensor.getFractionOxygen()*100f/firstSensor.getFractionOxygen();
            trimixData.setFractionHelium(fHe);
            trimixData.setCalculated((trimixData.getFractionHelium()+trimixData.getFractionOxygen())<100f && trimixData.getFractionHelium()>=0f && trimixData.getFractionOxygen()>=0f);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public static float calculateAfterOxygenSensor(TrimixData trimixData) {
        if (trimixData.isOxygenFirstMode()) {
            float oxygenFlow = getActualOxygenFlow(trimixData);
            float heliumFlow = getActualHeliumFlow(trimixData);
            float desiredOxygenFlow = getDesiredOxygenFlow(trimixData);
            float desiredHeliumFlow = getDesiredHeliumFlow(trimixData);
            float airFlow = 100f - desiredOxygenFlow - heliumFlow;
            return getNitrox(20.9f, airFlow, 100f, desiredOxygenFlow);
        }
        else
            throw new UnsupportedOperationException();
    }

    public static float calculateAfterHeliumSensor(TrimixData trimixData) {
        if (trimixData.isOxygenFirstMode())
            return trimixData.getDesiredFractionOxygen();
        else
            throw new UnsupportedOperationException();
    }

    private static float getActualOxygenFlow(TrimixData trimixData) {
        return (100 - getActualHeliumFlow(trimixData)) * (trimixData.getFractionOxygen()/(100f-trimixData.getFractionHelium()) *100f - 20.9f) / (100f-20.9f);
    }

    private static float getActualHeliumFlow(TrimixData trimixData) {
        return trimixData.getFractionHelium();
    }

    private static float getDesiredOxygenFlow(TrimixData trimixData) {
        return (100 - getDesiredHeliumFlow(trimixData)) * (trimixData.getDesiredFractionOxygen()/(100f-trimixData.getDesiredFractionHelium()) *100f - 20.9f) / (100f-20.9f);
    }

    private static float getDesiredHeliumFlow(TrimixData trimixData) {
        return trimixData.getDesiredFractionHelium();
    }

    private static float getNitrox(float nitrox1, float nitrox1Flow, float nitrox2, float nitrox2Folw) {
        return (nitrox1*nitrox1Flow + nitrox2*nitrox2Folw)/(nitrox1Flow + nitrox2Folw);

    }
}