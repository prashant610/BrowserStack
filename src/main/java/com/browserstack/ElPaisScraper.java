package com.browserstack;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

public class ElPaisScraper {

    public static void run(WebDriver driver) {
        try {
            driver.get("https://elpais.com/opinion/");
            WebDriverWait wait = new WebDriverWait(driver, 15);

            // ✅ Step 1: Accept Cookies
            try {
                WebElement allowCookiesButton = wait.until(
                        ExpectedConditions.elementToBeClickable(By.xpath("//button[@id='didomi-notice-agree-button']"))
                );
                allowCookiesButton.click();
                System.out.println("Allow Cookies' button clicked.");
            } catch (Exception e) {
                System.out.println("Allow Cookies' button not found or already handled.");
            }

            // ✅ Step 2: Wait for articles to load
            wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("article")));
            List<WebElement> articles = driver.findElements(By.tagName("article"));

            int count = Math.min(5, articles.size());
            System.out.println("Found " + articles.size() + " articles. Extracting top " + count + "...\n");

            List<String> translatedTitles = new ArrayList<>();

            for (int i = 0; i < count; i++) {
                WebElement article = articles.get(i);

                try {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", article);
                    Thread.sleep(500); // Give time for lazy-loading
                } catch (Exception ignored) {}

                // ✅ Spanish title
                String title = "[No title]";
                try {
                    WebElement header = article.findElement(By.tagName("header"));
                    if (header != null) {
                        title = header.getText().trim();
                        System.out.println("Article #" + (i + 1) + " Title found.");
                    } else {
                        System.out.println("Header not found for article #" + (i + 1));
                    }
                } catch (Exception e) {
                    System.out.println("No header for article #" + (i + 1));
                }

                // ✅ Content
                String content = "[No content]";
                try {
                    WebElement para = article.findElement(By.tagName("p"));
                    if (para != null) {
                        content = para.getText().trim();
                        System.out.println("Content found for article #" + (i + 1));
                    }
                } catch (Exception e) {
                    System.out.println("No paragraph for article #" + (i + 1));
                }

                // ✅ Image
                String imageUrl = "";
                try {
                    WebElement img = article.findElement(By.cssSelector("img"));
                    imageUrl = img.getAttribute("src");
                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        downloadImage(imageUrl, "article_" + (i + 1) + ".jpg");
                    } else {
                        System.out.println("Image src is empty for article #" + (i + 1));
                    }
                } catch (Exception e) {
                    System.out.println("No image for article #" + (i + 1));
                }

                // ✅ Translate Title
                String translatedTitle = TranslateUtil.translateText(title);
                translatedTitles.add(translatedTitle);

                // ✅ Print Results
                System.out.println("📰 Article #" + (i + 1));
                System.out.println("Title (ES): " + title);
                System.out.println("Title (EN): " + translatedTitle);
                System.out.println("Content: " + content);
                System.out.println("Image URL: " + (imageUrl.isEmpty() ? "N/A" : imageUrl));
                System.out.println("------------------------------------------------------");
            }

            // ✅ Analyze repeated words
            printRepeatedWords(translatedTitles);

        } catch (Exception e) {
            System.err.println("Error during scraping:");
            e.printStackTrace();
        }
    }

    private static void downloadImage(String imageUrl, String fileName) {
        try (InputStream in = new URL(imageUrl).openStream()) {
            Files.copy(in, Paths.get(fileName), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Image downloaded: " + fileName);
        } catch (Exception e) {
            System.out.println("Failed to download image: " + imageUrl);
        }
    }

    private static void printRepeatedWords(List<String> titles) {
        Map<String, Integer> wordCount = new HashMap<>();

        for (String title : titles) {
            String[] words = title.toLowerCase().split("\\W+");
            for (String word : words) {
                if (word.isEmpty()) continue;
                wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
            }
        }

        System.out.println("\n Repeated words (more than twice) in translated titles:");

        boolean[] found = {false};

        wordCount.entrySet().stream()
                .filter(e -> e.getValue() > 2)
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .forEach(e -> {
                    System.out.println(e.getKey() + ": " + e.getValue());
                    found[0] = true;
                });

        if (!found[0]) {
            System.out.println("No words repeated more than twice.");
        }
    }
}