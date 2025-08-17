package com.alura.ecommerce;

import com.google.genai.Client;
import com.google.genai.types.*;

public class TestearIntegracion {
    public static void main(String[] args) {
        var system = "Sos una IA que genera productos para un ecommerce. La lista de productos debe contener solamente los nombres de los productos.";
        var usuario = "Genera 5 productos";

        Client client = new Client();
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
    }
}
