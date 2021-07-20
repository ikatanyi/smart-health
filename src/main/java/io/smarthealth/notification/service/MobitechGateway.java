/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package io.smarthealth.notification.service;

import org.springframework.boot.configurationprocessor.json.JSONArray;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author Simon.Waweru
 */
public class MobitechGateway {

    private String _username;
    private String _apiKey;
    private String _sender_id;
    private String _environment;
    private String _api_host;
    private int responseCode;

    private static final int HTTP_CODE_OK = 200;
    private static final int HTTP_CODE_CREATED = 201;

    //Change debug flag to true to view raw server response
    private static final boolean DEBUG = false;

    private static FileWriter file;
    private static final String SMS_STATUS_FILE_PATH = "";

//    private String getApiHost() {
//        return "http://bulksms.mobitechtechnologies.com";
//    }

//    private String getSmsUrl() {
//        return getApiHost() + "/api/sendsms";
//    }

    public MobitechGateway(String username, String apiKey, String sender_id, String host) {
        _username = username;
        _apiKey = apiKey;
        _sender_id = sender_id;
        _api_host = host;
    }

    //Bulk messages methods
    public JSONArray sendMessage(String to_, String message_) throws Exception {
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("username", _username);
        data.put("api_key", _apiKey);
        data.put("sender_id", _sender_id);
        data.put("phone", to_);
        data.put("message", message_);

        return sendMessageImpl(to_, message_, data);
    }

    private void SMSStatusConfirmation(JSONArray jsonArray) {
        try {
            // Constructs a FileWriter given a file name, using the platform's default charset
            String jsonFile = SMS_STATUS_FILE_PATH + String.valueOf(System.currentTimeMillis()).concat(".json");
            file = new FileWriter(jsonFile);
            file.write(jsonArray.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                file.flush();
                file.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private JSONArray sendMessageImpl(String to_, String message_, HashMap<String, String> data_) throws Exception {
        String response = sendPOSTRequest(data_, _api_host);
        if (responseCode == HTTP_CODE_OK || responseCode == HTTP_CODE_CREATED) {
            JSONArray recipientsStatus = new JSONArray(response);
            SMSStatusConfirmation(recipientsStatus);
            if (recipientsStatus.length() > 0) {
                return recipientsStatus;
            }

            throw new Exception(response);
        }

        throw new Exception(response);
    }

    private String sendPOSTRequest(HashMap<String, String> dataMap_, String urlString_) throws Exception {
        String data = new String();
        Iterator<Map.Entry<String, String>> it = dataMap_.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> pairs = (Map.Entry<String, String>) it.next();
            data += URLEncoder.encode(pairs.getKey().toString(), "UTF-8");
            data += "=" + URLEncoder.encode(pairs.getValue().toString(), "UTF-8");
            if (it.hasNext()) {
                data += "&";
            }
        }
        URL url = new URL(urlString_);
        URLConnection conn = url.openConnection();
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("apikey", _apiKey);
        conn.setDoOutput(true);
        return sendPOSTRequestImpl(data, conn);
    }

    private String sendPOSTRequestImpl(String data_, URLConnection conn_) throws Exception {
        try {
            OutputStreamWriter writer = new OutputStreamWriter(conn_.getOutputStream());
            writer.write(data_);
            writer.flush();

            HttpURLConnection http_conn = (HttpURLConnection) conn_;
            responseCode = http_conn.getResponseCode();

            BufferedReader reader;
            boolean passed = true;

            if (responseCode == HTTP_CODE_OK || responseCode == HTTP_CODE_CREATED) {
                reader = new BufferedReader(new InputStreamReader(http_conn.getInputStream()));
            } else {
                reader = new BufferedReader(new InputStreamReader(http_conn.getErrorStream()));
                passed = false;
            }
            String response = readResponse(reader);

            if (DEBUG) {
                System.out.println("ResponseCode: " + responseCode + " RAW Response: " + response);
            }

            reader.close();

            if (passed) {
                return response;
            }

            throw new Exception(response);

        } catch (Exception e) {
            throw e;
        }
    }

    private String readResponse(BufferedReader reader) throws Exception {
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        return response.toString();
    }

}
