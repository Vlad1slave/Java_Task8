package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;

public class NasaApiClient {

    private static final String API_KEY = "6CwPfoHXSBXJiublDneJtvMdsFlyJ4ezY75MUpKJ";
    private static final String APOD_URL = "https://api.nasa.gov/planetary/apod?api_key=" + API_KEY;

    public static void main(String[] args) {
        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {
            // Шаг 1: Запрос к API NASA
            HttpGet request = new HttpGet(APOD_URL);
            CloseableHttpResponse response = httpClient.execute(request);

            // Шаг 2: Парсинг JSON ответа
            String jsonResponse = EntityUtils.toString(response.getEntity());
            ObjectMapper objectMapper = new ObjectMapper();
            NasaApiResponse nasaResponse = objectMapper.readValue(jsonResponse, NasaApiResponse.class);

            // Шаг 3: Извлечение URL изображения
            String imageUrl = nasaResponse.getUrl();
            if (imageUrl == null) {
                System.out.println("URL изображения не найден в ответе.");
                return;
            }

            // Шаг 4: Получение имени файла из URL
            String fileName = imageUrl.substring(imageUrl.lastIndexOf('/') + 1);
            System.out.println("Скачивание файла: " + fileName);

            // Шаг 5: Скачивание изображения
            HttpGet imageRequest = new HttpGet(imageUrl);
            try (CloseableHttpResponse imageResponse = httpClient.execute(imageRequest);
                 InputStream inputStream = imageResponse.getEntity().getContent();
                 FileOutputStream outputStream = new FileOutputStream(fileName)) {

                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }

            System.out.println("Файл успешно скачан: " + fileName);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}