package com.browserstack;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TranslateUtil {

    private static final Logger logger = LoggerFactory.getLogger(TranslateUtil.class);
    private static final String API_KEY = "AIzaSyAq5eCjzFYOL5dTtl_D-DKSYEdpWzzTmzQ";

    public static String translateText(String text) {
        String apiUrl = "https://translation.googleapis.com/language/translate/v2?key=" + API_KEY;

        try (org.apache.hc.client5.http.impl.classic.CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(apiUrl);

            JSONObject body = new JSONObject();
            body.put("q", text);
            body.put("target", "en");
            body.put("source", "es");

            post.setEntity(new StringEntity(body.toString(), ContentType.APPLICATION_JSON));

            try (CloseableHttpResponse response = client.execute(post)) {
                String jsonResponse = EntityUtils.toString(response.getEntity());

                JSONObject jsonObj = new JSONObject(jsonResponse);
                String rawText = jsonObj
                        .getJSONObject("data")
                        .getJSONArray("translations")
                        .getJSONObject(0)
                        .getString("translatedText");

                return StringEscapeUtils.unescapeHtml4(rawText);
            }

        } catch (Exception e) {
            logger.error("❌ Translation failed. Returning original text.", e);
            return text;
        }
    }
}
