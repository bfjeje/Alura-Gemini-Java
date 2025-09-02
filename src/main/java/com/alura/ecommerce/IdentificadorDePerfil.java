package com.alura.ecommerce;

import com.google.genai.Client;
import com.google.genai.types.*;

import java.nio.file.Files;
import java.nio.file.Path;

public class IdentificadorDePerfil {
    public static void main(String[] args) {
        var system = """
            Identifique el perfil de compra para cada cliente a continuación.
            El formato de salida debe ser:
            cliente – describa el perfil del cliente en 3 palabras
            """;
        var user = cargarClientesDelArchivo();

        dispararRequest(system, user);
    }

    private static String cargarClientesDelArchivo() {
        try {
            var path = Path.of(ClassLoader
                    .getSystemResource("compras/lista_de_compras_100_clientes.csv")
                    .toURI());
            return Files.readAllLines(path).toString();
        } catch (Exception e) {
            throw new RuntimeException("Error al cargar el archivo.",e);
        }
    }

    public static void dispararRequest(String system, String user){
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

            var modelo = "gemini-2.5-flash";
            var cantidadTokens = contadorDeTokens(client, user);
            if (cantidadTokens > 4096)
                modelo = "gemini-2.5-flash-lite";

            System.out.println("Cantidad de tokens: "+cantidadTokens);
            System.out.println("Modelo seleccionado: "+modelo);
            GenerateContentResponse response =
                    client.models.generateContent(
                            modelo,
                            user,
                            config);

            System.out.println(response.text());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Integer contadorDeTokens(Client client, String user) {
        var contador = client.models.countTokens(
                "gemini-2.5-flash-lite",
                user,
                CountTokensConfig.builder().build()
        );
        if (contador.totalTokens().isPresent()) {
            return contador.totalTokens().get();
        }
        return -1;
    }
}
