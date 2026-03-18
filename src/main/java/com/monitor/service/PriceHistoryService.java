package com.monitor.service;

import java.io.*;
import java.util.*;

public class PriceHistoryService {

    private static final String FILE = "price_history.txt";

    public static Map<String, List<Double>> loadHistory() {

        Map<String, List<Double>> history = new HashMap<>();

        try {

            File file = new File(FILE);

            if (!file.exists())
                return history;

            BufferedReader reader = new BufferedReader(new FileReader(file));

            String line;

            while ((line = reader.readLine()) != null) {

                String[] parts = line.split("=");

                if (parts.length != 2)
                    continue;

                String id = parts[0];
                String[] values = parts[1].split(",");

                List<Double> prices = new ArrayList<>();

                for (String v : values) {

                    try {

                        double price = Double.parseDouble(
                                v.replace("R$", "")
                                        .replace(".", "")
                                        .replace(",", ".")
                                        .trim()
                        );

                        prices.add(price);

                    } catch (Exception ignored) {
                        // ignora valores inválidos
                    }
                }

                if (!prices.isEmpty())
                    history.put(id, prices);
            }

            reader.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return history;
    }

    public static void saveHistory(Map<String, List<Double>> history) {

        try {

            PrintWriter writer = new PrintWriter(new FileWriter(FILE));

            for (String id : history.keySet()) {

                List<Double> prices = history.get(id);

                StringBuilder sb = new StringBuilder();

                for (double p : prices) {
                    sb.append(p).append(",");
                }

                writer.println(id + "=" + sb.toString());
            }

            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static double getAverage(List<Double> prices) {

        if (prices.isEmpty())
            return 0;

        double sum = 0;

        for (double p : prices)
            sum += p;

        return sum / prices.size();
    }
}