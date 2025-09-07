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
        try {
            var carpetaResena = Path.of("src/main/resources/resenas");
            var archivos = Files
                    .walk(carpetaResena, 1)
                    .filter(path -> path.toString().endsWith(".txt"))
                    .toList();
            for (Path archivo: archivos){
                System.out.println("Iniciando analisis de "+archivo.getFileName().toString());
                var user = cargarArchivo(archivo);
                var respuesta = dispararRequest(system, user);
                guardarAnalisis(archivo.getFileName().toString().replace(".txt",""), respuesta);
                System.out.println("Fin del analisis");
            }
        } catch (Exception e) {
            System.out.println("Hubo un problema al analisar sentimientos");
        }
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
                            "gemini-2.5-flash-lite",
                            user,
                            config);

            return response.text();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String cargarArchivo(Path archivo) {
        try {
            return Files.readAllLines(archivo).toString();
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
