    package com.nabarup.avator;

    import android.os.AsyncTask;
    import java.io.OutputStream;
    import java.net.HttpURLConnection;
    import java.net.URL;
    import java.nio.charset.StandardCharsets;

    public class TelegramBotSender {

        // Replace with your bot's token and chat ID
        private static final String BOT_TOKEN = "6870340606:AAG8HQj0zw4Rt5LH-5zf9ZlsgmI-HUtLtFU";
        private static final String CHAT_ID = "6367189135";

        public static void sendMessage(String message) {
            new SendMessageTask().execute(message);
        }

        private static class SendMessageTask extends AsyncTask<String, Void, Void> {
            @Override
            protected Void doInBackground(String... params) {
                String message = params[0];
                try {
                    String urlString = "https://api.telegram.org/bot" + BOT_TOKEN + "/sendMessage";
                    URL url = new URL(urlString);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoOutput(true);
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");

                    String payload = "{\"chat_id\":\"" + CHAT_ID + "\",\"text\":\"" + message + "\"}";

                    try (OutputStream os = conn.getOutputStream()) {
                        byte[] input = payload.getBytes(StandardCharsets.UTF_8);
                        os.write(input, 0, input.length);
                    }

                    int responseCode = conn.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        // Message sent successfully
                    } else {
                        // Error occurred
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }
    }

