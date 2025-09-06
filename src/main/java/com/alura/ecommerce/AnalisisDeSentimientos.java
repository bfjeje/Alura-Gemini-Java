package com.alura.ecommerce;

import com.google.genai.Client;
import com.google.genai.types.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class AnalisisDeSentimientos {
    public static void main(String[] args) {
        var system = """
            Sos un analizador de sentimientos de reseñas de productos.
            Escribe un parrafo con hasta 50 palabras resumiendo las reseñas, y después di cual es el sentimiento general para ese producto.
            Identifica también 3 puntos fuertes y 3 puntos débiles identificados a partir de las reseñas.
            
            #### Formato de la salida:
            Nombre del producto:
            Resumen de reseñas: [resumen en hasta 50 palabras]
            Sentimiento general: [debe ser: POSITIVO, NEUTRO o NEGATIVO]
            Puntos fuertes: [3 bullets points]
            Puntos debiles: [3 bullets points]
            """;
        var producto = "colchoneta-de-yoga";
        var user = cargarArchivo(producto);

        var respuesta = dispararRequest(system, user);
        guardarAnalisis(producto, respuesta);
    }

    public static String dispararRequest(String system, String user){
        var APIKey = System.getenv("GEMINI_API_KEY");

        try(Client client = Client.builder().apiKey(APIKey).build()){
            Content systemInstruction = Content.fromParts(Part.fromText(system));

            GenerateContentConfig config =
                    GenerateContentConfig.builder()
                            .thinkingConfig(
                                    ThinkingConfig.builder()
                                            .thinkingBudget(0)
                                            .build()
                            )
                            .systemInstruction(systemInstruction)
                            .build();

            GenerateContentResponse response =
                    client.models.generateContent(
                            "gemini-2.5-flash",
                            user,
                            config);

            return response.text();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String cargarArchivo(String archivo) {
        try {
            var path = Path.of(ClassLoader
                    .getSystemResource("resenas/resenas-"+archivo+".txt")
                    .toURI());
            return Files.readAllLines(path).toString();
        } catch (Exception e) {
            throw new RuntimeException("Error al cargar el archivo.",e);
        }
    }

    private static void guardarAnalisis(String producto, String analisis) {
        try {
            var path = Path.of("src/main/resources/analisis/analisis-sentimientos-"+producto+".txt");
            Files.writeString(path, analisis, StandardOpenOption.CREATE_NEW);
        } catch (Exception e) {
            throw new RuntimeException("Error al guardar el archivo!", e);
        }
    }
}
