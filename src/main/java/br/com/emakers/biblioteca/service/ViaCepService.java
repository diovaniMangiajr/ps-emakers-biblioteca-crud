package br.com.emakers.biblioteca.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import br.com.emakers.biblioteca.dto.ViaCepResponseDTO;

@Service
public class ViaCepService {

    // Instancia o RestClient nativo do Spring para realizar chamadas HTTP
    private final RestClient restClient = RestClient.create();

    /**
     * Consome a API externa do ViaCep de forma síncrona.
     * @param cep O CEP enviado pela requisição (com ou sem hífen).
     * @return DTO com os dados do endereço preenchidos.
     */
    public ViaCepResponseDTO consultarCep(String cep) {
        try {
            // Remove caracteres especiais caso o CEP venha formatado incorretamente
            String cepLimpo = cep.replace("-", "").replace(" ", "");

            // Monta a URL dinâmica e executa um GET HTTP síncrono na API externa
            return restClient.get()
                    .uri("https://viacep.com.br/ws/{cep}/json/", cepLimpo)
                    .retrieve()
                    .body(ViaCepResponseDTO.class); // Converte o JSON recebido diretamente no nosso DTO
        } catch (Exception e) {
            // Se o ViaCep estiver fora do ar ou o CEP for estruturalmente inválido, dispara erro lógico
            throw new RuntimeException("Falha ao integrar com a API externa do ViaCep: CEP inválido ou indisponível.");
        }
    }
}