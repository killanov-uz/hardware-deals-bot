package com.monitor.scraper;

import com.monitor.model.Product;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;

public class CategoryScraper {

    private static final List<String> blacklist = Arrays.asList(
            "FAN", "VENTILADOR", "COOLER",
            "SUPORTE", "RISER", "CABO",
            "ADAPTADOR", "EXTENSOR",
            "MINERAÇÃO", "MINER",
            "CAPA", "CASE"
    );

    public static List<Product> getProducts(String baseUrl) throws Exception {

        List<Product> products = new ArrayList<>();
        Set<String> seen = new HashSet<>();

        for (int page = 1; page <= 10; page++) {

            String url = baseUrl + "&page=" + page;

            System.out.println("Scanning page: " + page);

            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0")
                    .header("Accept-Language", "pt-BR,pt;q=0.9")
                    .timeout(20000)
                    .get();

            Elements items = doc.select(".s-result-item");

            for (Element item : items) {

                Element title = item.selectFirst("h2 span");
                Element priceWhole = item.selectFirst(".a-price-whole");
                Element priceFraction = item.selectFirst(".a-price-fraction");
                Element linkElement = item.selectFirst("a.a-link-normal");
                Element imageElement = item.selectFirst("img.s-image");

                if (title == null || priceWhole == null || priceFraction == null ||
                        linkElement == null || imageElement == null)
                    continue;

                String name = title.text();
                String upperName = name.toUpperCase();

                /* ---------- blacklist ---------- */

                boolean blocked = false;

                for (String bad : blacklist) {

                    if (upperName.contains(bad)) {
                        blocked = true;
                        break;
                    }

                }

                if (blocked)
                    continue;

                /* ---------- filtros premium ---------- */

                boolean valid = false;

                // GPUs modernas

                if (
                        upperName.contains("RTX 30") ||
                                upperName.contains("RTX 40") ||
                                upperName.contains("RX 6") ||
                                upperName.contains("RX 7") ||
                                upperName.contains("ARC A")
                ) {
                    valid = true;
                }

                // CPUs boas

                if (
                        upperName.contains("RYZEN 5") ||
                                upperName.contains("RYZEN 7") ||
                                upperName.contains("RYZEN 9") ||
                                upperName.contains("CORE I5") ||
                                upperName.contains("CORE I7") ||
                                upperName.contains("CORE I9")
                ) {
                    valid = true;
                }

                // SSD 1TB+

                if (
                        upperName.contains("SSD") &&
                                upperName.contains("1TB")
                ) {
                    valid = true;
                }

                // RAM 16GB+

                if (
                        upperName.contains("DDR4") ||
                                upperName.contains("DDR5")
                ) {

                    if (
                            upperName.contains("16GB") ||
                                    upperName.contains("32GB") ||
                                    upperName.contains("64GB")
                    ) {
                        valid = true;
                    }
                }

                if (!valid)
                    continue;

                /* ---------- link ---------- */

                String rawLink = linkElement.attr("href");

                if (!rawLink.contains("/dp/"))
                    continue;

                String id = rawLink.split("/dp/")[1].split("/")[0];

                if (seen.contains(id))
                    continue;

                seen.add(id);

                String link = "https://www.amazon.com.br/dp/" + id;

                /* ---------- imagem ---------- */

                String image = imageElement.attr("src");

                /* ---------- preço ---------- */

                String priceStr = priceWhole.text() + priceFraction.text();

                String price = "R$ " + priceStr;

                /* ---------- nome curto ---------- */

                if (name.length() > 90)
                    name = name.substring(0, 90) + "...";

                products.add(new Product(name, price, link, image, id));
            }
        }

        return products;
    }
}
