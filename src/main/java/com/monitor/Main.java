package com.monitor;

import com.monitor.model.Product;
import com.monitor.scraper.CategoryScraper;
import com.monitor.service.*;

import java.util.*;

public class Main {

    public static void main(String[] args) throws Exception {

        String[] categories = {
                "https://www.amazon.com.br/s?k=placa+de+video+rtx",
                "https://www.amazon.com.br/s?k=processador+ryzen",
                "https://www.amazon.com.br/s?k=ssd+nvme",
                "https://www.amazon.com.br/s?k=memoria+ram+ddr5"
        };

        while (true) {

            System.out.println("\n🔎 Searching premium deals...\n");

            Set<String> sent = SentProductsService.loadSentProducts();
            Map<String, List<Double>> history = PriceHistoryService.loadHistory();

            for (String category : categories) {

                List<Product> products = CategoryScraper.getProducts(category);

                products.sort(Comparator.comparing(Product::getPriceValue));

                int sentCount = 0;

                String[] ranks = {
                        "🥇 BEST",
                        "🥈 SECOND",
                        "🥉 THIRD",
                        "🏅 DEAL",
                        "🏅 DEAL"
                };

                for (Product p : products) {

                    List<Double> prices = history.getOrDefault(p.getId(), new ArrayList<>());
                    prices.add(p.getPriceValue());

                    history.put(p.getId(), prices);

                    boolean isSuperDeal = false;
                    double discount = 0;

                    if (prices.size() >= 3) {

                        double avg = PriceHistoryService.getAverage(prices);
                        discount = ((avg - p.getPriceValue()) / avg) * 100;

                        if (discount >= 20) {
                            isSuperDeal = true;
                        }
                    }

                    if (sent.contains(p.getId()))
                        continue;

                    String rank = ranks[Math.min(sentCount, ranks.length - 1)];

                    TelegramService.sendProduct(p, rank, isSuperDeal, discount);

                    SentProductsService.saveProduct(p.getId());

                    sentCount++;

                    Thread.sleep(2000);

                    if (sentCount >= 5)
                        break;
                }
            }

            PriceHistoryService.saveHistory(history);

            System.out.println("\n⏳ Waiting 10 minutes...\n");

            Thread.sleep(600000);
        }
    }
}