package com.browserstack;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class BrowserStackTest {

    public static void main(String[] args) throws Exception {
        final String USERNAME = "prashantpandey_k5QEPX";
        final String ACCESS_KEY = "FhYXVjxAyHCu7JUTcWfC";
        final String URL_STRING = "https://" + USERNAME + ":" + ACCESS_KEY + "@hub-cloud.browserstack.com/wd/hub";

        // Capability sets
        List<Map<String, String>> capsList = new ArrayList<>();

        Map<String, String> chromeCaps = new HashMap<>();
        chromeCaps.put("os", "Windows");
        chromeCaps.put("os_version", "11");
        chromeCaps.put("browser", "Chrome");
        chromeCaps.put("browser_version", "latest");
        chromeCaps.put("name", "ChromeTest");
        capsList.add(chromeCaps);

        Map<String, String> firefoxCaps = new HashMap<>();
        firefoxCaps.put("os", "Windows");
        firefoxCaps.put("os_version", "10");
        firefoxCaps.put("browser", "Firefox");
        firefoxCaps.put("browser_version", "latest");
        firefoxCaps.put("name", "FirefoxTest");
        capsList.add(firefoxCaps);

        Map<String, String> safariCaps = new HashMap<>();
        safariCaps.put("os", "OS X");
        safariCaps.put("os_version", "Monterey");
        safariCaps.put("browser", "Safari");
        safariCaps.put("browser_version", "latest");
        safariCaps.put("name", "SafariTest");
        capsList.add(safariCaps);

        Map<String, String> iphoneCaps = new HashMap<>();
        iphoneCaps.put("device", "iPhone 13");
        iphoneCaps.put("real_mobile", "true");
        iphoneCaps.put("os_version", "15.0");
        iphoneCaps.put("browserName", "iPhone");
        iphoneCaps.put("name", "iPhoneTest");
        capsList.add(iphoneCaps);

        Map<String, String> androidCaps = new HashMap<>();
        androidCaps.put("device", "Samsung Galaxy S23");
        androidCaps.put("real_mobile", "true");
        androidCaps.put("os_version", "13.0");
        androidCaps.put("browserName", "Android");
        androidCaps.put("name", "AndroidTest");
        capsList.add(androidCaps);

        // Use ExecutorService for thread management
        ExecutorService executor = Executors.newFixedThreadPool(capsList.size());

        for (final Map<String, String> caps : capsList) {
            executor.submit(() -> {
                WebDriver driver = null;
                try {
                    DesiredCapabilities capabilities = new DesiredCapabilities();
                    for (Map.Entry<String, String> entry : caps.entrySet()) {
                        capabilities.setCapability(entry.getKey(), entry.getValue());
                    }

                    System.out.println("🚀 Starting test: " + caps.get("name"));

                    driver = new RemoteWebDriver(new URL(URL_STRING), capabilities);
                    ElPaisScraper.run(driver);  // Run scraper

                    System.out.println("✅ Completed test: " + caps.get("name"));

                } catch (Exception e) {
                    System.err.println("❌ Error in test: " + caps.get("name"));
                    e.printStackTrace();
                } finally {
                    if (driver != null) {
                        driver.quit();
                        System.out.println("🧹 Browser closed for: " + caps.get("name"));
                    }
                }
            });
        }

        // Shutdown executor and exit
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.MINUTES);

        // Kill any remaining threads (OkHttp etc.)
        System.exit(0);
    }
}
