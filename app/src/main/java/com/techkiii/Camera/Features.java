package com.techkiii.Camera;

public class Features {
    String name;
    String method;
    String clearMethod;
    String clearValue;
    Float maxValue = 1.0F;
    Float defaultValue = .0F;
    Float value = defaultValue;

    public Features(String name, String method, String clearMethod, String clearValue) {
        this.name = name;
        this.method = method;
        this.clearMethod = clearMethod;
        this.clearValue = clearValue;
    }

    public boolean isDefault() {
        return false;
    }

    public void reset() {

    }
}
