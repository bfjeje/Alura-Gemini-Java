package com.alura.ecommerce;

import com.google.genai.Client;
import com.google.genai.types.*;

public class ContadorDeTokens {
    public static void main(String[] args) {
        var APIKey = System.getenv("GEMINI_API_KEY");
        var user = "Identifique el perfil de compra para cada cliente a continuación.";
        try(Client client = Client.builder().apiKey(APIKey).build()){
           var contador = client.models.countTokens(
                   "gemini-2.5-flash-lite",
                   user,
                   CountTokensConfig.builder().build()
           );
           if (contador.totalTokens().isPresent()) {
               var totalTokens = contador.totalTokens().get();
               System.out.println("Cantidad de tokens: "+totalTokens);
               var costo = (totalTokens / 1000000.00) * 0.1;
               System.out.println("Precio por el prompt: USD"+costo);
           }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
