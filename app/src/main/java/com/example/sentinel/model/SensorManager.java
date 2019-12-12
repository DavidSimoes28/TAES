package com.example.sentinel.model;

import java.util.LinkedList;
import java.util.List;

public enum SensorManager {
    INSTANCE;

    private List<Sensor> sensors;

    SensorManager() {
        this.sensors = new LinkedList<>();
    }

    public List<Sensor> getSensors() {
        return sensors;
    }

    public void setSensors(List<Sensor> sensors) {
        this.sensors = sensors;
    }

    public void addSensor(Sensor sensor){
        this.sensors.remove(sensor);
    }
    public void removeSensor(Sensor sensor){
        this.sensors.remove(sensor);
    }
    public Sensor getSensor(String localizacao){
        for (Sensor sensor: sensors){
            if (sensor.getLocalizacao().equals(localizacao)){
                return sensor;
            }
        }
        return null;
    }
}
