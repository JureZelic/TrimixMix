package si.gounitis.trimixmix.model;

public class TrimixData {
    private float fractionOxygen;
    private float fractionHelium;
    private SensorData redSensor = new SensorData();
    private SensorData greenSensor = new SensorData();

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
}
