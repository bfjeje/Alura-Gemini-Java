package com.alura.ecommerce;

import com.google.genai.Client;
import com.google.genai.types.*;

public class CategorizadorDeProductos {
    public static void main(String[] args) {
        var system = """
                Sos un categorizador de productos y tenes que responder solamente con el nombre de la categoria.
                Las posibles categorias son:
                1. Higiene personal
                2. Deportes
                3. Electronica
                4. Otros
                ##### ejemplos de respuesta:
                pregunta: Pelota de futbol
                respuesta: Deportes
                """;
        var usuario = "Celular";
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
