package com.monitor.service;

import com.monitor.model.Product;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class TelegramService {

    private static final String TOKEN = "Your_Token_Here";
    private static final String CHAT_ID = "Your_Chat_ID_Here";

    public static void sendProduct(Product product, String rank, boolean isSuperDeal, double discount) {

        try {

            String caption;

            if (isSuperDeal) {

                caption =
                        "🚨 SUPER DEAL\n\n" +
                                "🖥 " + product.getName() +
                                "\n\n💰 " + product.getPrice() +
                                "\n📉 -" + (int) discount + "% abaixo do normal" +
                                "\n🔗 Ver produto:\n" + product.getLink();

            } else {

                caption =
                        rank + "DEAL\n\n" +
                                "🖥 " + product.getName() +
                                "\n\n💰 Preço: " + product.getPrice() +
                                "\n🔗 Ver produto:\n" + product.getLink();
            }

            String encodedCaption = URLEncoder.encode(caption, "UTF-8");
            String encodedImage = URLEncoder.encode(product.getImage(), "UTF-8");

            String urlString =
                    "https://api.telegram.org/bot" + TOKEN +
                            "/sendPhoto?chat_id=" + CHAT_ID +
                            "&photo=" + encodedImage +
                            "&caption=" + encodedCaption;

            URL url = new URL(urlString);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();

            if (responseCode == 200) {
                System.out.println("✅ Enviado: " + product.getName());
            } else {
                System.out.println("❌ Erro ao enviar: " + responseCode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}