package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        // Создаем HTTP-клиент
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)
                        .setSocketTimeout(30000)
                        .setRedirectsEnabled(false)
                        .build())
                .build();

        // Создаем объект запроса
        HttpGet request = new HttpGet("https://raw.githubusercontent.com/netology-code/jd-homeworks/master/http/task1/cats");

        try {
            // Выполняем запрос
            CloseableHttpResponse response = httpClient.execute(request);

            // Преобразуем ответ в строку
            String json = new String(response.getEntity().getContent().readAllBytes());

            // Преобразуем JSON в список объектов CatFact
            ObjectMapper mapper = new ObjectMapper();
            List<CatFact> catFacts = Arrays.asList(mapper.readValue(json, CatFact[].class));

            // Фильтруем факты, где upvotes не равно null
            List<CatFact> filteredCatFacts = catFacts.stream()
                    .filter(catFact -> catFact.getUpvotes() != null)
                    .collect(Collectors.toList());

            // Выводим результат на экран
            filteredCatFacts.forEach(System.out::println);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}