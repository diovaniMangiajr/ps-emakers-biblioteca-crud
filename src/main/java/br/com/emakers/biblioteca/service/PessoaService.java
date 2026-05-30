package br.com.emakers.biblioteca.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.com.emakers.biblioteca.domain.Pessoa;
import br.com.emakers.biblioteca.dto.ViaCepResponseDTO;
import br.com.emakers.biblioteca.repository.PessoaRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PessoaService {

    private final PessoaRepository pessoaRepository;
    private final ViaCepService viaCepService;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    public Pessoa salvar(Pessoa novaPessoa) {
        // 1. Consome a API externa para validar se o CEP realmente existe e é ativo
    ViaCepResponseDTO dadosEndereco = viaCepService.consultarCep(novaPessoa.getCep());
    
    // Validação de segurança: O ViaCep retorna um campo "erro" caso o CEP tenha 8 dígitos mas não exista
    if (dadosEndereco == null || dadosEndereco.cep() == null) {
        throw new RuntimeException("O CEP informado não foi encontrado na base de dados do ViaCep.");
    }
    
    // Opcional/Destaque: Ajusta o CEP da entidade para o formato padrão devolvido pela API oficial
    novaPessoa.setCep(dadosEndereco.cep());

    String senhaCriptografada = passwordEncoder.encode(novaPessoa.getSenha());
    novaPessoa.setSenha(senhaCriptografada);

    // 2. Persiste a pessoa no banco de dados local com o CEP devidamente validado e higienizado
    return pessoaRepository.save(novaPessoa);
    }

    public List<Pessoa> buscarTodas() {
        return pessoaRepository.findAll();
    }

    public Pessoa buscarPorId(Integer id) {
        return pessoaRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Pessoa não encontrada com o ID: " + id));
    }

    public void deletar(Integer id) {
        Pessoa pessoa = buscarPorId(id);
        pessoaRepository.delete(pessoa);
    }
}