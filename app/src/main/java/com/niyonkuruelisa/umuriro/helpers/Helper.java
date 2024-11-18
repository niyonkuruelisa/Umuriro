package com.niyonkuruelisa.umuriro.helpers;
import android.annotation.SuppressLint;
import android.util.Base64;
import android.util.Log;

import com.niyonkuruelisa.umuriro.models.DeviceSettings;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

public class Helper {

    private static final String TAG = "OfflineStorageService";
    // Method to encode a map to a Base64 string
    public static String encodeMap(Map<String, String> map) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(map);
            objectOutputStream.close();

            return Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
            return null; // Return null if encoding fails
        }
    }

    // Method to decode a Base64 string back to a map
    public static Map<String, String> decodeMap(String encodedMap) {
        try {
            byte[] bytes = Base64.decode(encodedMap, Base64.DEFAULT);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);

            @SuppressWarnings("unchecked")
            Map<String, String> map = (Map<String, String>) objectInputStream.readObject();
            objectInputStream.close();
            return map;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new HashMap<>(); // Return an empty map if decoding fails
        }
    }

    // Method to convert a class object DeviceSettings to map<string,string>
    public static Map<String, String> convertDeviceSettingsToMap(DeviceSettings deviceSettings) {
        Map<String, String> map = new HashMap<>();
        map.put("isMonitor", deviceSettings.getIsMonitor() + "");
        map.put("phoneNumber1", deviceSettings.getPhoneNumber1());
        map.put("phoneNumberOwner1", deviceSettings.getPhoneNumberOwner1());
        map.put("phoneNumber2", deviceSettings.getPhoneNumber2());
        map.put("phoneNumberOwner2", deviceSettings.getPhoneNumberOwner2());
        map.put("phoneNumber3", deviceSettings.getPhoneNumber3());
        map.put("phoneNumberOwner3", deviceSettings.getPhoneNumberOwner3());
        map.put("phoneNumber4", deviceSettings.getPhoneNumber4());
        map.put("phoneNumberOwner4", deviceSettings.getPhoneNumberOwner4());
        map.put("phoneNumber5", deviceSettings.getPhoneNumber5());
        map.put("phoneNumberOwner5", deviceSettings.getPhoneNumberOwner5());
        map.put("remainingSMS", deviceSettings.getRemainingSMS());
        map.put("usedSMS", deviceSettings.getUsedSMS());
        return map;
    }
    // Method to convert a map<string,string> to a class object DeviceSettings
    public static DeviceSettings convertMapToDeviceSettings(Map<String, String> map) {
        DeviceSettings deviceSettings = new DeviceSettings();
        deviceSettings.setIsMonitor(map.get("isMonitor").equals("true"));
        deviceSettings.setPhoneNumber1(map.get("phoneNumber1"));
        deviceSettings.setPhoneNumberOwner1(map.get("phoneNumberOwner1"));
        deviceSettings.setPhoneNumber2(map.get("phoneNumber2"));
        deviceSettings.setPhoneNumberOwner2(map.get("phoneNumberOwner2"));
        deviceSettings.setPhoneNumber3(map.get("phoneNumber3"));
        deviceSettings.setPhoneNumberOwner3(map.get("phoneNumberOwner3"));
        deviceSettings.setPhoneNumber4(map.get("phoneNumber4"));
        deviceSettings.setPhoneNumberOwner4(map.get("phoneNumberOwner4"));
        deviceSettings.setPhoneNumber5(map.get("phoneNumber5"));
        deviceSettings.setPhoneNumberOwner5(map.get("phoneNumberOwner5"));
        deviceSettings.setRemainingSMS(map.get("remainingSMS"));
        deviceSettings.setUsedSMS(map.get("usedSMS"));
        return deviceSettings;
    }

    // Method to encode a class object to a Base64 string
    public static String encodeObject(Object object) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(object);
            objectOutputStream.close();

            @SuppressLint({"NewApi", "LocalSuppress"}) String encodedObject = java.util.Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
            // Save encodedObject to SharedPreferences or any other storage
            Log.d(TAG, "DeviceSettings saved successfully: " + encodedObject);
            return encodedObject;
        } catch (IOException e) {
            Log.e(TAG, "Failed to save DeviceSettings", e);
            return null;
        }
    }

    // Method to decode a Base64 string back to a class object
    public static Object decodeObject(String encodedObject) {
        try {
            byte[] bytes = Base64.decode(encodedObject, Base64.DEFAULT);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);

            Object object = objectInputStream.readObject();
            objectInputStream.close();

            return object;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();

            return null; // Return null if decoding fails
        }
    }
}
