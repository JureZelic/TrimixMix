package si.gounitis.trimixmix.model;

public class SensorData {
    private boolean calibrated = false;
    private String tipText = "Calibrate sensor";
    private float sensorOffset = -0.40f;
    private float sensorFactor;
    private float sensorVoltage;
    private float sensorMinVoltage = 3f; // on air
    private float sensorMaxVoltage = 13f; // on air
    private float fractionOxygen;

    public boolean isCalibrated() {
        return calibrated;
    }

    public void setCalibrated(boolean calibrated) {
        this.calibrated = calibrated;
    }

    public String getTipText() {
        return tipText;
    }

    public void setTipText(String tipText) {
        this.tipText = tipText;
    }

    public float getSensorOffset() {
        return sensorOffset;
    }

    public void setSensorOffset(float sensorOffset) {
        this.sensorOffset = sensorOffset;
    }

    public float getSensorFactor() {
        return sensorFactor;
    }

    public void setSensorFactor(float sensorFactor) {
        this.sensorFactor = sensorFactor;
    }

    public float getSensorVoltage() {
        return sensorVoltage;
    }

    public void setSensorVoltage(float sensorVoltage) {
        this.sensorVoltage = sensorVoltage;
    }

    public float getSensorMinVoltage() {
        return sensorMinVoltage;
    }

    public void setSensorMinVoltage(float sensorMinVoltage) {
        this.sensorMinVoltage = sensorMinVoltage;
    }

    public float getSensorMaxVoltage() {
        return sensorMaxVoltage;
    }

    public void setSensorMaxVoltage(float sensorMaxVoltage) {
        this.sensorMaxVoltage = sensorMaxVoltage;
    }

    public float getFractionOxygen() {
        return fractionOxygen;
    }

    public void setFractionOxygen(float fractionOxygen) {
        this.fractionOxygen = fractionOxygen;
    }
}