package com.browserstack;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.json.JSONObject;

public class TranslateUtil {

    private static final String API_KEY = "AIzaSyAq5eCjzFYOL5dTtl_D-DKSYEdpWzzTmzQ";

    public static String translateText(String text) {
        String apiUrl = "https://translation.googleapis.com/language/translate/v2?key=" + API_KEY;

        try (org.apache.hc.client5.http.impl.classic.CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(apiUrl);

            // Prepare JSON request body
            JSONObject body = new JSONObject();
            body.put("q", text);
            body.put("target", "en");
            body.put("source", "es");

            // Set headers and entity
            post.setEntity(new StringEntity(body.toString(), ContentType.APPLICATION_JSON));

            try (CloseableHttpResponse response = client.execute(post)) {
                String jsonResponse = EntityUtils.toString(response.getEntity());

                JSONObject jsonObj = new JSONObject(jsonResponse);
                return jsonObj
                        .getJSONObject("data")
                        .getJSONArray("translations")
                        .getJSONObject(0)
                        .getString("translatedText");
            }

        } catch (Exception e) {
            System.err.println("❌ Translation failed. Returning original text.");
            e.printStackTrace();
            return text;
        }
    }
}
