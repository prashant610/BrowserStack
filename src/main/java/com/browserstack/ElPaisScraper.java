package com.browserstack;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

public class ElPaisScraper {

    private static final Logger logger = LoggerFactory.getLogger(ElPaisScraper.class);

    public static void run(WebDriver driver) {
        try {
            driver.get("https://elpais.com/opinion/");
            WebDriverWait wait = new WebDriverWait(driver, 15);

            // ✅ Handle cookie consent for both desktop & mobile
            try {
                try {
                    WebElement allowCookiesButton = wait.until(
                            ExpectedConditions.elementToBeClickable(
                                    By.xpath("//span[contains(text(),'Accept')]")));
                    allowCookiesButton.click();
                    logger.info("'Accept Cookies' button clicked (desktop).");
                } catch (Exception e1) {
                    WebElement mobileAcceptButton = wait.until(
                            ExpectedConditions.elementToBeClickable(
                                    By.xpath("//button[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'accept and continue')]")));
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", mobileAcceptButton);
                    mobileAcceptButton.click();
                    logger.info("'Accept and Continue' button clicked (mobile).");
                }
            } catch (Exception e) {
                logger.info("No cookie consent prompt found or already handled.");
            }

            // ✅ Wait for articles to appear
            wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("article")));
            List<WebElement> articles = driver.findElements(By.tagName("article"));

            int count = Math.min(5, articles.size());
            logger.info("Found {} articles. Extracting top {}...", articles.size(), count);

            List<String> translatedTitles = new ArrayList<>();

            for (int i = 0; i < count; i++) {
                WebElement article = articles.get(i);

                try {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", article);
                    Thread.sleep(500);
                } catch (Exception ignored) {}

                String title = "[No title]";
                try {
                    WebElement header = article.findElement(By.tagName("header"));
                    if (header != null) {
                        title = header.getText().trim();
                        logger.info("Article #{} Title found.", i + 1);
                    }
                } catch (Exception e) {
                    logger.warn("No header for article #{}", i + 1);
                }

                String content = "[No content]";
                try {
                    WebElement para = article.findElement(By.tagName("p"));
                    if (para != null) {
                        content = para.getText().trim();
                        logger.info("Content found for article #{}", i + 1);
                    }
                } catch (Exception e) {
                    logger.warn("No paragraph for article #{}", i + 1);
                }

                // ✅ Robust Image Handling (lazy load + picture tag)
                String imageUrl = "";
                try {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", article);
                    Thread.sleep(500); // allow lazy-load JS to render

                    // Try <img> first
                    List<WebElement> imgs = article.findElements(By.tagName("img"));

                    if (imgs.isEmpty()) {
                        // Try <picture> → <source>
                        List<WebElement> sources = article.findElements(By.tagName("source"));
                        for (WebElement src : sources) {
                            String srcset = src.getAttribute("srcset");
                            if (srcset != null && !srcset.isEmpty()) {
                                imageUrl = srcset.split(" ")[0]; // take first URL
                                break;
                            }
                        }
                    } else {
                        // Use first <img>
                        WebElement img = imgs.get(0);
                        imageUrl = img.getAttribute("src");

                        if (imageUrl == null || imageUrl.isEmpty()) {
                            imageUrl = img.getAttribute("data-src");
                        }

                        if ((imageUrl == null || imageUrl.isEmpty()) && img.getAttribute("srcset") != null) {
                            imageUrl = img.getAttribute("srcset").split(" ")[0];
                        }
                    }

                    if (imageUrl != null && !imageUrl.isEmpty()) {
                        downloadImage(imageUrl, "article_" + (i + 1) + ".jpg");
                        logger.info("Image found and downloaded for article #{}", i + 1);
                    } else {
                        logger.warn("No image URL found inside article #{}", i + 1);
                        // Optional: Uncomment below to debug HTML if needed
                        // logger.debug("Article #{} HTML: {}", i + 1, article.getAttribute("innerHTML"));
                    }

                } catch (Exception e) {
                    logger.warn("Error locating or downloading image for article #{}", i + 1);
                }

                String translatedTitle = TranslateUtil.translateText(title);
                translatedTitles.add(translatedTitle);

                logger.info(
                        "\nArticle #{}\nTitle (ES): {}\nTitle (EN): {}\nContent: {}\nImage URL: {}\n-----------------------------",
                        i + 1, title, translatedTitle, content, imageUrl.isEmpty() ? "N/A" : imageUrl);
            }

            printRepeatedWords(translatedTitles);

        } catch (Exception e) {
            logger.error("Error during scraping", e);
        }
    }

    private static void downloadImage(String imageUrl, String fileName) {
        try (InputStream in = new URL(imageUrl).openStream()) {
            Files.copy(in, Paths.get(fileName), StandardCopyOption.REPLACE_EXISTING);
            logger.info("Image downloaded: {}", fileName);
        } catch (Exception e) {
            logger.warn("Failed to download image: {}", imageUrl);
        }
    }

    private static void printRepeatedWords(List<String> titles) {
        Map<String, Integer> wordCount = new HashMap<>();

        for (String title : titles) {
            String[] words = title.toLowerCase().split("\\W+");
            for (String word : words) {
                if (!word.isEmpty()) {
                    wordCount.put(word, wordCount.getOrDefault(word, 0) + 1);
                }
            }
        }

        logger.info("\nRepeated words (more than twice) in translated titles:");
        boolean found = false;

        for (Map.Entry<String, Integer> entry : wordCount.entrySet()) {
            if (entry.getValue() > 2) {
                logger.info("{}: {}", entry.getKey(), entry.getValue());
                found = true;
            }
        }

        if (!found) {
            logger.info("No words repeated more than twice.");
        }
    }
}

