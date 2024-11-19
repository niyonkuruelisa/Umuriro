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
    // Method to encode a class object to a Base64 string
    public static String encodeObject(Object object) {
        Log.d(TAG, "DeviceSettings to save: " + object.toString());
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
