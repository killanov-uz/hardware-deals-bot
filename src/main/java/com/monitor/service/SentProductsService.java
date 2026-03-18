package com.monitor.service;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

public class SentProductsService {

    private static final String FILE = "sent_products.txt";

    public static Set<String> loadSentProducts() {

        Set<String> sent = new HashSet<>();

        try {

            File file = new File(FILE);

            if (!file.exists()) return sent;

            BufferedReader reader = new BufferedReader(new FileReader(file));

            String line;

            while ((line = reader.readLine()) != null) {

                sent.add(line.trim());

            }

            reader.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return sent;
    }

    public static void saveProduct(String productId) {

        try {

            PrintWriter writer =
                    new PrintWriter(new FileWriter(FILE, true));

            writer.println(productId);

            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
