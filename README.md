# 🕸️ Cross-Browser Web Scraping Automation – Final Report

---

## 🎯 Objective

To build a **robust, scalable, and cross-browser automated scraping suite** using **Java**, **Selenium**, and **BrowserStack**.  

The system performs the following:

- Launches the El País **"Opinion"** section on both desktop and real mobile browsers.  
- Dynamically accepts cookie notices.  
- Extracts the **first five articles**: Spanish title, body content, and image.  
- Translates each title from **Spanish → English** using the **Google Translate API**.  
- Downloads available images to the local system.  
- Analyzes translated titles for **repeated words (more than twice)**.  
- Runs the entire test suite in **parallel across 5 environments** using BrowserStack.  
- Uses **logging instead of `System.out.println`** for structured output.  
- Handles **mobile-specific rendering issues** (e.g., iPhone Safari).  
- Detects and decodes **HTML entities** (e.g., `&#39; → '`).  

---

## ⚙️ Technologies Used

| Component | Technology |
|------------|-------------|
| **Language** | Java 1.8 |
| **Automation** | Selenium WebDriver |
| **API Integration** | Google Translate API |
| **Browser Testing** | BrowserStack Automate |
| **Build Tool** | Maven |
| **Logging** | SLF4J + Simple Logger (slf4j-simple) |
| **Concurrency** | Java ExecutorService (5 threads) |
| **HTML Unescaping** | Apache Commons Text |

---

## 🌐 Browsers & Devices Tested (BrowserStack)

- 🖥️ **Chrome (Windows 11)**  
- 🦊 **Firefox (Windows 10)**  
- 🍎 **Safari (macOS Monterey)**  
- 📱 **iPhone 14 Plus (iOS 18.4.1, Chrome)**  
- 🤖 **Samsung Galaxy S23 (Android 13, Chrome)**  

---

## 🗂️ Project Structure

src/main/java/com/browserstack/
├── BrowserStackTest.java → Executes parallel threads and manages BrowserStack sessions
├── ElPaisScraper.java → Scrapes content, downloads images, translates titles, and analyzes words
└── TranslateUtil.java → Handles Google Translate API integration and HTML decoding

markdown
Copy code

---

## 🔄 Test Workflow

1. **Thread Initialization:**  
   - Five parallel threads start different browsers/devices using `RemoteWebDriver`.

2. **Navigation & Cookie Handling:**  
   - Dynamically detects and accepts cookies for desktop and mobile layouts.

3. **Content Extraction:**  
   - Retrieves article **titles**, **paragraphs**, and **cover images** using `<article>`, `<header>`, `<p>`, and `<img>` tags.

4. **Translation API Call:**  
   - Sends each Spanish title to **Google Translate API** and decodes HTML entities.

5. **Image Download:**  
   - Downloads available images locally using **Java I/O streams**.

6. **Word Frequency Analysis:**  
   - Extracts and counts words repeated more than twice across translated titles.

7. **Thread Termination:**  
   - Uses graceful shutdown with `ExecutorService` and `System.exit(0)` to prevent lingering threads.

---

## ✅ Features & Improvements

- Multi-threaded **parallel execution** across browsers  
- **Dynamic cookie handling** for desktop and mobile views  
- Robust **image detection and lazy-load handling**  
- **HTML decoding** for accurate translations  
- Clean **structured logging** (SLF4J)  
- **Scroll-into-view** before interacting with hidden elements  
- Comprehensive **error handling and fallback locators**  
- Reliable **BrowserStack integration** for real device coverage  

---

## 📌 Sample Log Output (Chrome)

✅ 'Accept Cookies' button clicked.
✅ Found 27 articles. Extracting top 5...

📰 Article #1
Title (ES): Viejas recetas para nuevos tiempos
Title (EN): Old recipes for new times
Content: El informe del FMI...
Image URL: N/A

✅ Image downloaded: article_4.jpg

🔁 Repeated words (more than twice): N/A

✅ Completed test: ChromeTest

yaml
Copy code

---

## ⚠️ Challenges & Resolutions

| Issue | Resolution |
|--------|-------------|
| Cookie button unclickable on mobile | Added fallback XPath + JS click() + scroll-into-view |
| Lazy-loaded or missing images | Added explicit scroll and retry mechanism |
| HTML entities in translated text | Used `StringEscapeUtils.unescapeHtml4()` |
| iPhone layout variation | Handled with multiple XPath options |
| Threads hanging post-execution | Used `System.exit(0)` after all threads completed |

---

## 🏁 Final Verdict

🎯 **100% requirements met**  
📱 Successfully executed on **5 parallel BrowserStack threads**  
🔐 Handled edge cases like missing titles, lazy images, and mobile rendering  
🚀 Fully automated, **scalable**, and **well-logged** scraping suite  
