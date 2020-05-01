/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.yraykuffer.forexnotifier;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.awt.MenuShortcut;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author admin
 */
public class ForexNotifier {

    // https://www.freeforexapi.com/api/live?pairs=USDPHP
    public static void main(String[] args) {
        TrayIcon ti;
        System.out.println(ForexNotifier.getRate());
        try {
            if (SystemTray.isSupported()) {
                SystemTray st = SystemTray.getSystemTray();
                ti = new TrayIcon(Toolkit.getDefaultToolkit().createImage("icon.png"));
                ti.setImageAutoSize(true);
                st.add(ti);
                ti.addActionListener((e) -> {
                    System.exit(0);
                });
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        double rate = getRate();
                        if (rate >= 50.9) {
                             ti.displayMessage("USD to PHP", "PHP" + getRate(), TrayIcon.MessageType.INFO);
                        }
                    }
                };
                Timer timer = new Timer("Timer");

                long delay = 1000L;
                timer.schedule(task, delay, 1000 * 10);

            }
        } catch (Exception e) {
        }

    }

    public static double getRate() {
        double rate = 0.0;
        try {

            URL url = new URL("https://www.freeforexapi.com/api/live?pairs=USDPHP");//your url i.e fetch data from .
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP Error code : "
                        + conn.getResponseCode());
            }

            InputStreamReader in = new InputStreamReader(conn.getInputStream());
            BufferedReader br = new BufferedReader(in);
            String output;
            while ((output = br.readLine()) != null) {
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Map<String, Map>> map = mapper.readValue(output, HashMap.class);
                rate = Double.parseDouble(map.get("rates").get("USDPHP").get("rate").toString());
//                System.out.println(rate);
            }

            conn.disconnect();

        } catch (Exception e) {
            System.out.println("Exception in NetClientGet:- " + e);
        }

        return rate;
    }
}
