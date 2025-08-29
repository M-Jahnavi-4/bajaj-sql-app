package com.example.bajaj.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class AppRunner implements CommandLineRunner {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void run(String... args) throws Exception {
        String generateUrl = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("name", "Jahnavi");
        requestBody.put("regNo", "22BCE7727");
        requestBody.put("email", "jahnavi@example.com"); // replace with your real email

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(generateUrl, entity, String.class);
        JsonNode jsonResponse = mapper.readTree(response.getBody());
        String webhookUrl = jsonResponse.get("webhook").asText();
        String accessToken = jsonResponse.get("accessToken").asText();

        System.out.println("Webhook URL: " + webhookUrl);
        System.out.println("Access Token: " + accessToken);

        String finalQuery = "SELECT p.AMOUNT AS SALARY, " +
                "CONCAT(e.FIRST_NAME, ' ', e.LAST_NAME) AS NAME, " +
                "TIMESTAMPDIFF(YEAR, e.DOB, CURDATE()) AS AGE, " +
                "d.DEPARTMENT_NAME " +
                "FROM PAYMENTS p " +
                "JOIN EMPLOYEE e ON p.EMP_ID = e.EMP_ID " +
                "JOIN DEPARTMENT d ON e.DEPARTMENT = d.DEPARTMENT_ID " +
                "WHERE DAY(p.PAYMENT_TIME) <> 1 " +
                "ORDER BY p.AMOUNT DESC " +
                "LIMIT 1;";

        Map<String, String> finalBody = new HashMap<>();
        finalBody.put("finalQuery", finalQuery);

        HttpHeaders finalHeaders = new HttpHeaders();
        finalHeaders.setContentType(MediaType.APPLICATION_JSON);
        finalHeaders.set("Authorization", accessToken);

        HttpEntity<Map<String, String>> finalEntity = new HttpEntity<>(finalBody, finalHeaders);
        ResponseEntity<String> submitResponse = restTemplate.postForEntity(webhookUrl, finalEntity, String.class);

        System.out.println("Submit Response: " + submitResponse.getBody());
    }
}
