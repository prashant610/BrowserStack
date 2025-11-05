Cross-Browser Web Scraping Automation – Final Report
🎯 Objective
To build a robust, scalable, and cross-browser automated scraping suite using Java, Selenium, and BrowserStack. The system should:
1. Launch the El País "Opinion" section on both desktop and real mobile browsers.
2. Accept cookie notices dynamically.
3. Extract the first five articles: Spanish title, body content, and image.
4. Translate each title from Spanish to English using the Google Translate API.
5. Download available images to the local system.
6. Analyze the translated titles for repeated words (more than twice).
7. Run the entire test suite in parallel across 5 environments using BrowserStack.
8. Use logging instead of System.out.println for structured output.
9. Handle mobile-specific rendering issues (e.g., on iPhone Safari).
10. Detect and decode HTML entities (e.g., &#39; → ').

⚙️ Technologies Used

| Component          | Technology                            |
|-------------------|----------------------------------------|
| Language           | Java 1.8                               |
| Automation         | Selenium WebDriver                     |
| API Integration    | Google Translate API                  |
| Browser Testing    | BrowserStack Automate                  |
| Build Tool         | Maven                                  |
| Logging            | SLF4J + Simple Logger (`slf4j-simple`) |
| Concurrency        | Java ExecutorService (5 threads)       |
| HTML Unescaping    | Apache Commons Text                    |


🌐 Browsers & Devices Tested (BrowserStack)
1. Chrome (Windows 11)
2. Firefox (Windows 10)
3. Safari (macOS Monterey)

4. iPhone 14 (iOS 18.4.1, Chrome)
5. Samsung Galaxy S23 (Android 13, Chrome)

🗂 Project Structure

src/main/java/com/browserstack/
 ├── BrowserStackTest.java   → Executes parallel threads
 ├── ElPaisScraper.java      → Scrapes content from El País
 └── TranslateUtil.java      → Integrates Google Translate API


🔄 Test Workflow
1. Thread Initialization: 5 parallel threads each boot up a browser/device using RemoteWebDriver.
2. Navigation & Cookie Acceptance: Dynamically detects and clicks cookie popups.
3. Content Extraction: Retrieves article titles, paragraphs, and images (<img> inside <figure>).
4. Translation API Call: Sends Spanish titles to Google Translate API and decodes the response.
5. Image Download: Saves cover images locally using URL streams.
6. Word Frequency Analysis: Extracts and counts words repeated more than twice across all titles.


✅ Features & Improvements
- Multi-threaded execution
- Robust cross-browser and mobile compatibility
- Image handling with fallback
- HTML entity decoding for translation output
- SLF4J-based structured logging
- Dynamic cookie acceptance for mobile and desktop
- Failsafe scroll-into-view before accessing elements
- Proper thread shutdown with System.exit(0)
- Dynamic image fallback via <figure> tag


📌 Sample Log Output (Chrome)
✅ 'Allow Cookies' button clicked.
✅ Found 27 articles. Extracting top 5...

📰 Article #1
Title (ES): Viejas recetas para nuevos tiempos
Title (EN): Old recipes for new times
Content: El informe del FMI...
Image URL: N/A

✅ Image downloaded: article_4.jpg
🔁 Repeated words (more than twice): N/A

✅ Completed test: ChromeTest


⚠️ Challenges & Resolutions
| Issue                                        | Resolution                                         |
|---------------------------------------------|----------------------------------------------------|
| Cookie button unclickable on mobile         | Fallback XPath + JS click() + scroll into view     |
| Images missing or lazy-loaded               | Added explicit scroll + wait                       |
| HTML entities in translations               | Used StringEscapeUtils.unescapeHtml4()             |
| iPhone layout different (button missing)    | Handled with multiple XPath options                |
| Threads hanging after completion            | Used System.exit(0) to clean lingering threads     |


✅ Final Verdict
🎯 100% requirements met
📱 Successfully executed on 5 parallel BrowserStack threads
🔐 Handled edge cases like missing titles, lazy images, and mobile rendering
🚀 Fully automated, scalable, and well-logged scraping suite

