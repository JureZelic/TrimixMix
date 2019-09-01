package si.gounitis.trimixmix.model;

public class TrimixData {
    private boolean oxygenFirstMode = true;
    private float fractionOxygen;
    private float fractionHelium;
    private float desiredFractionOxygen;
    private float desiredFractionHelium;
    private SensorData redSensor = new SensorData();
    private SensorData greenSensor = new SensorData();
    private boolean calculated = false;

    public boolean isOxygenFirstMode() {
        return oxygenFirstMode;
    }

    public void setOxygenFirstMode(boolean oxygenFirstMode) {
        this.oxygenFirstMode = oxygenFirstMode;
    }

    public float getFractionOxygen() {
        return fractionOxygen;
    }

    public void setFractionOxygen(float fractionOxygen) {
        this.fractionOxygen = fractionOxygen;
    }

    public float getFractionHelium() {
        return fractionHelium;
    }

    public void setFractionHelium(float fractionHelium) {
        this.fractionHelium = fractionHelium;
    }

    public float getDesiredFractionOxygen() {
        return desiredFractionOxygen;
    }

    public void setDesiredFractionOxygen(float desiredFractionOxygen) {
        this.desiredFractionOxygen = desiredFractionOxygen;
    }

    public float getDesiredFractionHelium() {
        return desiredFractionHelium;
    }

    public void setDesiredFractionHelium(float desiredFractionHelium) {
        this.desiredFractionHelium = desiredFractionHelium;
    }

    public SensorData getRedSensor() {
        return redSensor;
    }

    public void setRedSensor(SensorData redSensor) {
        this.redSensor = redSensor;
    }

    public SensorData getGreenSensor() {
        return greenSensor;
    }

    public void setGreenSensor(SensorData greenSensor) {
        this.greenSensor = greenSensor;
    }

    public boolean isCalculated() {
        return calculated;
    }

    public void setCalculated(boolean calculated) {
        this.calculated = calculated;
    }
}
