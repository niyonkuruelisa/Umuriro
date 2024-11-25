package com.niyonkuruelisa.umuriro.models;

import java.io.Serializable;

public class DeviceSettings implements Serializable {
    private static final long serialVersionUID = 1L;
    private boolean isMonitor =  false;
    private boolean permissionsGranted = false;
    private String phoneNumber1;
    private String phoneNumberOwner1;
    private String phoneNumber2;
    private String phoneNumberOwner2;
    private String phoneNumber3;
    private String phoneNumberOwner3;
    private String phoneNumber4;
    private String phoneNumberOwner4;
    private String remainingSMS;
    private String usedSMS;
    private boolean initialized = false;

    public boolean isPermissionsGranted() {
        return permissionsGranted;
    }

    public void setPermissionsGranted(boolean permissionsGranted) {
        this.permissionsGranted = permissionsGranted;
    }

    public boolean isMonitor() {
        return isMonitor;
    }

    public void setMonitor(boolean monitor) {
        isMonitor = monitor;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }

    public boolean getIsMonitor() {
        return isMonitor;
    }

    public void setIsMonitor(boolean isMonitor) {
        this.isMonitor = isMonitor;
    }

    public String getPhoneNumber1() {
        return phoneNumber1;
    }

    public void setPhoneNumber1(String phoneNumber1) {
        this.phoneNumber1 = phoneNumber1;
    }

    public String getPhoneNumberOwner1() {
        return phoneNumberOwner1;
    }

    public void setPhoneNumberOwner1(String phoneNumberOwner1) {
        this.phoneNumberOwner1 = phoneNumberOwner1;
    }

    public String getPhoneNumber2() {
        return phoneNumber2;
    }

    public void setPhoneNumber2(String phoneNumber2) {
        this.phoneNumber2 = phoneNumber2;
    }

    public String getPhoneNumberOwner2() {
        return phoneNumberOwner2;
    }

    public void setPhoneNumberOwner2(String phoneNumberOwner2) {
        this.phoneNumberOwner2 = phoneNumberOwner2;
    }

    public String getPhoneNumber3() {
        return phoneNumber3;
    }

    public void setPhoneNumber3(String phoneNumber3) {
        this.phoneNumber3 = phoneNumber3;
    }

    public String getPhoneNumberOwner3() {
        return phoneNumberOwner3;
    }

    public void setPhoneNumberOwner3(String phoneNumberOwner3) {
        this.phoneNumberOwner3 = phoneNumberOwner3;
    }

    public String getPhoneNumber4() {
        return phoneNumber4;
    }

    public void setPhoneNumber4(String phoneNumber4) {
        this.phoneNumber4 = phoneNumber4;
    }

    public String getPhoneNumberOwner4() {
        return phoneNumberOwner4;
    }

    public void setPhoneNumberOwner4(String phoneNumberOwner4) {
        this.phoneNumberOwner4 = phoneNumberOwner4;
    }

    public String getRemainingSMS() {
        return remainingSMS;
    }

    public void setRemainingSMS(String remainingSMS) {
        this.remainingSMS = remainingSMS;
    }

    public String getUsedSMS() {
        return usedSMS;
    }

    public void setUsedSMS(String usedSMS) {
        this.usedSMS = usedSMS;
    }
}
