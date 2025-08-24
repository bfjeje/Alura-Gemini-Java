package com.alura.ecommerce;

import com.google.genai.Client;
import com.google.genai.types.*;

public class CategorizadorDeProductos {
    public static void main(String[] args) {
        var system = "Sos un categorizador de productos";
        var usuario = "Cepillo de dientes";
        var APIKey = System.getenv("GEMINI_API_KEY");

        try(Client client = Client.builder().apiKey(APIKey).build()){
            Content systemInstruction = Content.fromParts(Part.fromText(system));

            GenerateContentConfig config =
                    GenerateContentConfig.builder()
                            .candidateCount(5)
                            .thinkingConfig(
                                    ThinkingConfig.builder().thinkingBudget(0).build()
                            )
                            .systemInstruction(systemInstruction)
                            .build();

            GenerateContentResponse response =
                    client.models.generateContent(
                            "gemini-2.5-flash",
                            usuario,
                            config);

            response.candidates().ifPresent(candidates ->
                candidates.forEach(candidate ->
                    candidate.content().flatMap(Content::parts).ifPresent(parts ->
                            parts.forEach(part -> {
                                part.text().ifPresent(System.out::println);
                                System.out.println("------------");
                            })
                    )
                )
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
