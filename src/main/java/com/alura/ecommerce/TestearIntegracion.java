package com.alura.ecommerce;

import com.google.genai.Client;
import com.google.genai.types.*;

public class TestearIntegracion {
    public static void main(String[] args) {
        var system = "Sos una IA que genera productos para un ecommerce. La lista de productos debe contener solamente los nombres de los productos.";
        var usuario = "Genera 5 productos";
        var APIKey = System.getenv("GEMINI_API_KEY");

        try(Client client = Client.builder().apiKey(APIKey).build()){
            Content systemInstruction = Content.fromParts(Part.fromText(system));

            GenerateContentConfig config =
                    GenerateContentConfig.builder()
                            .candidateCount(1)
                            .systemInstruction(systemInstruction)
                            .build();

            GenerateContentResponse response =
                    client.models.generateContent(
                            "gemini-2.5-flash",
                            usuario,
                            config);

            System.out.println(response.text());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
