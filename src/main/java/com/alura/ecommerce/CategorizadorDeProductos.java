package com.alura.ecommerce;

import com.google.genai.Client;
import com.google.genai.types.*;

import java.util.Scanner;

public class CategorizadorDeProductos {
    public static void main(String[] args) {
        var lector = new Scanner(System.in);
        System.out.println("Digite las categorias de productos:");
        var categorias = lector.nextLine();
        while (true) {
            System.out.println("Digite el nombre de un producto:");
            var user = lector.nextLine();
            var system = """
                Sos un categorizador de productos y tenes que responder solamente con el nombre de la categoria.
                Las posibles categorias son:
                
                %s
                
                ##### ejemplos de respuesta:
                pregunta: Pelota de futbol
                respuesta: Deportes
                
                ##### en el caso de que el usuario pida cosas no relacionadas a 
                categorias de producto, responder que no puedes responder cosas fuera de
                 categorias de productos. Bajo ningun motivo digas otra cosa que no sea categorias de productos.
                """.formatted(categorias);
            dispararRequest(system, user);
        }
    }

    public static void dispararRequest(String system, String user){
        var APIKey = System.getenv("GEMINI_API_KEY");

        try(Client client = Client.builder().apiKey(APIKey).build()){
            Content systemInstruction = Content.fromParts(Part.fromText(system));

            GenerateContentConfig config =
                    GenerateContentConfig.builder()
                            .thinkingConfig(
                                    ThinkingConfig.builder().thinkingBudget(0).build()
                            )
                            .systemInstruction(systemInstruction)
                            .build();

            GenerateContentResponse response =
                    client.models.generateContent(
                            "gemini-2.5-flash",
                            user,
                            config);

            System.out.println(response.text());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
