package com.MailAI.MailMind.Service;

import com.MailAI.MailMind.MailController.EmailRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class EmailGenService {

//NON BLOCKING HTTP CLIENT
    private final WebClient webClient;

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    public EmailGenService(WebClient.Builder webClient) {
        this.webClient = WebClient.builder().build();
    }


    public String generateEmailReply(EmailRequest emailRequest)
    {
        //Build the prompt
        String prompt = buildPrompt(emailRequest);
        
        //Craft a request
        //prepares request body
        Map<String,Object> requestBody = Map.of(
            "contents", new Object[]{
                            Map.of("parts", new Object[]{
                                            Map.of("text", prompt)
                                        })
                                }
        );
        
        //Do request and get response

        String response = webClient.post()
                .uri(geminiApiUrl + geminiApiKey)
                .header("Content-Type","application/json")
                .bodyValue(requestBody)
                .retrieve() //starts the requst
                .bodyToMono(String.class)
                .block();

        //Extract Response and Return
        return extractResponseContent(response);
    }
    //parses the JSON string from gemini
    private String extractResponseContent(String response) {
        try{
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(response);
            return rootNode.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();
        }
        catch (Exception e)
        {
            return "Error Processing Request: "+ e.getMessage();
        }
    }

    private String buildPrompt(EmailRequest emailRequest) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Generate a professional email reply in 100-150 words for the following email content. Please don't generate a subject line ");

        if(emailRequest.getTone() != null && !emailRequest.getTone().isEmpty())
        {
            prompt.append("Use a ").append(emailRequest.getTone()).append(" tone. ");

        }
        prompt.append("\nOriginal email: \n").append(emailRequest.getEmailContent());
        return prompt.toString();
    }
}
