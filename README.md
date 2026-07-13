# **Documentação Oficial: API RESTful de Gerenciamento de Biblioteca**

Esta documentação detalha a especificação técnica, a arquitetura de software e as regras de negócio da API RESTful de Gerenciamento de Biblioteca, desenvolvida para o Processo Seletivo de Trainee da Emakers Jr. A aplicação constitui um sistema corporativo robusto focado no controle de acervo, gerenciamento de usuários, empréstimos e devoluções de livros, empregando práticas consolidadas de mercado como arquitetura limpa, segurança stateless e integridade transacional.

## **1\. Tecnologias e Dependências do Ecossistema**

O projeto foi construído utilizando a plataforma Spring Boot 3.3.5 e a linguagem Java 17, integrando componentes modulares para fornecer alta performance e escalabilidade:

* **Spring Web:** Responsável pela exposição de endpoints RESTful e gerenciamento síncrono do fluxo de requisições e respostas HTTP.  
* **Spring Data JPA / Hibernate:** Camada de abstração de dados (ORM) que simplifica a persistência de entidades relacionais e gerencia o pool de conexões.  
* **Spring Security & com.auth0 (java-jwt):** Mecanismo integrado de autenticação e autorização stateless, responsável pelo controle de acessos via JSON Web Tokens (JWT).  
* **Flyway Migration:** Ferramenta de versionamento estrutural automotivo da base de dados, garantindo a evolução contínua dos schemas sem perda de integridade.  
* **PostgreSQL Driver:** Conector de baixo nível otimizado para comunicação de alta fidelidade com o banco de dados PostgreSQL.  
* **Jakarta Validation:** Framework de validação declarativa que intercepta payloads de entrada diretamente na camada de controle (Controller) usando anotações.  
* **Springdoc OpenAPI UI (Swagger):** Motor de documentação interativa que expõe a especificação da API de forma dinâmica e legível para o time de front-end.  
* **Project Lombok:** Ferramenta de produtividade que elimina códigos boilerplate (getters, setters, construtores) em tempo de compilação.  
* **Spring RestClient:** Componente nativo moderno do Spring utilizado para integração HTTP síncrona com serviços externos de maneira limpa.

## **2\. Arquitetura de Camadas**

A estrutura organizacional do código-fonte adota o padrão de separação de responsabilidades em camadas estanques, o que facilita a manutenção, a escrita de testes unitários e o reaproveitamento de componentes:

| Camada / Pacote | Responsabilidade Técnica   |
| :---- | :---- |
| **br.com.emakers.biblioteca.config** | Centraliza as configurações globais de infraestrutura da aplicação, como regras do Spring Security, interceptores de filtros JWT e metadados customizados do OpenAPI Swagger. |
| **br.com.emakers.biblioteca.controller** | Camada de exposição externa (Controllers REST). Intercepta as requisições HTTP, delega o processamento lógico para os serviços correspondentes e retorna respostas padronizadas com os respectivos HTTP Status Codes. |
| **br.com.emakers.biblioteca.domain** | Representa o núcleo do modelo de dados. Contém as Entidades JPA mapeadas nativamente para as tabelas físicas do banco de dados relacional. |
| **br.com.emakers.biblioteca.dto** | Contém os Data Transfer Objects (implementados como Java Records imutáveis). Servem para blindar o domínio e realizar o transporte seguro de payloads de entrada e saída. |
| **br.com.emakers.biblioteca.exception** | Gerenciamento global de anomalias por meio do Controller Advice. Intercepta exceções de runtime e monta objetos de erro limpos e amigáveis para o cliente da API. |
| **br.com.emakers.biblioteca.repository** | Interfaces de persistência baseadas no Spring Data JPA. Abstraem queries SQL complexas por meio de Derived Queries ou comandos JPQL customizados. |
| **br.com.emakers.biblioteca.service** | Orquestrador da camada de negócio. Concentra todas as validações lógicas, restrições operacionais e a execução de integrações com APIs externas. |

## **3\. Regras de Negócio e Lógica de Domínio**

### **3.1. Gerenciamento de Usuários (Pessoa)**

A entidade Pessoa centraliza os dados cadastrais dos locatários da biblioteca, possuindo as seguintes imposições de negócio:

* **Criptografia Compulsória:** O sistema adota uma postura de segurança rígida de não armazenar credenciais em formato de texto limpo. No ato do salvamento na classe PessoaService, a senha bruta passa por um algoritmo de hash adaptável baseado em BCryptPasswordEncoder.  
* **Validação e Integração Síncrona de Endereço (ViaCep):** Antes de persistir um novo usuário, o sistema realiza uma chamada externa síncrona para a API do ViaCep utilizando o componente RestClient. Caso o CEP informado não seja encontrado na base de dados oficial ou apresente problemas estruturais, a operação de cadastro é imediatamente abortada por uma RuntimeException.  
* **Padronização de Dados (Higiene de Strings):** O CEP aceito na requisição é limpo de caracteres especiais (traços e espaços) e, após a confirmação na API externa, a entidade armazena o CEP formatado conforme o padrão oficial retornado pela plataforma do ViaCep.  
* **Garantia de Unicidade:** O e-mail e o CPF são marcados explicitamente com restrições de unicidade (UNIQUE) no banco de dados, e quaisquer tentativas de inserção duplicada acionam mecanismos de tratamento de integridade de dados.

### **3.2. Controle de Acervo (Livro)**

A entidade Livro modela os títulos literários disponíveis no estoque do sistema:

* **Mecanismos de Busca Avançada:** Foram implementados dois endpoints específicos de consulta avançada baseados em regras de ContainingIgnoreCase no repositório. Isso permite que buscas parciais de termos (por título ou autor) retornem dados ignorando variações de letras maiúsculas e minúsculas (padrão equivalente ao operador ILIKE do SQL).  
* **Bloqueio de Cadastro Futuro:** Por meio da anotação de validação @PastOrPresent aplicada ao campo correspondente do DTO, o sistema barra qualquer tentativa de cadastrar livros com datas de lançamento posteriores ao dia corrente do servidor.  
* **Consistência do Estoque Físico:** O acervo é resguardado contra anomalias físicas utilizando a validação @Min(value \= 0), o que impede que o estoque inicial seja inicializado com valores negativos.  
* **Proteção de Integridade de Vínculos:** Um livro não pode ser deletado fisicamente do acervo se possuir vínculos ativos na tabela associativa de empréstimos. Essa verificação é efetuada defensivamente na camada de serviço antes da chamada de exclusão.

### **3.3. Fluxo de Empréstimos e Devoluções**

A entidade Emprestimo modela uma tabela de ligação muitos-para-muitos (N:M) entre Livro e Pessoa, mapeada em nível JPA por meio de uma chave primária composta (@EmbeddedId):

* **Reserva Estratégica de Leitura Local:** Visando assegurar a disponibilidade contínua de livros dentro do espaço físico da biblioteca, o sistema estipula que um empréstimo externo só pode ser autorizado se o saldo atual do estoque do livro for estritamente superior a um exemplar (quantidade \> 1). Caso o estoque conte com apenas uma unidade, esta é reservada unicamente para consulta local, e o empréstimo externo é bloqueado.  
* **Garantia de Transacionalidade (Atomicidade):** As rotinas de retirada de livros (que realizam o decremento do estoque) e devolução (que realizam o incremento) são executadas sob escopos demarcados com a anotação @Transactional. Isso garante que a atualização das tabelas físicas ocorra de forma atômica, eliminando cenários de condições de corrida (Race Conditions).  
* **Unicidade por Contrato Concorrente:** Uma pessoa fica impedida de abrir múltiplos empréstimos ativos para a mesma obra de maneira simultânea. O sistema valida essa regra consultando a presença da chave composta gerada a partir dos identificadores numéricos das entidades relacionadas.

## **4\. Evolução e Estrutura do Banco de Dados (Flyway Migrations)**

O ciclo de vida do banco de dados relacional PostgreSQL é gerenciado de forma versionada e incremental por meio de arquivos de migração controlados pelo Flyway:

* **V1\_\_create\_initial\_tables.sql (Estrutura Base):** Criação das tabelas de entidades fortes, pessoa e livro, com suas respectivas restrições de integridade, chaves primárias do tipo SERIAL e campos únicos. Adicionalmente, estabelece a tabela associativa de acoplamento emprestimo, configurando chaves estrangeiras amarradas às tabelas principais com comportamento de deleção física em cascata (ON DELETE CASCADE).  
* **V2\_\_create\_initial\_tables.sql (Evolução de Escopo):** Atualiza dinamicamente o schema do banco de dados, injetando a coluna quantidade do tipo inteiro na tabela de livros, atribuindo a obrigatoriedade de preenchimento e um valor padrão unitário (DEFAULT 1\) para preservar a compatibilidade de dados legados já existentes no sistema.

## **5\. Matriz de Segurança e Controle de Acessos**

A arquitetura de segurança da API adota um modelo stateless baseado no Spring Security. Uma vez autenticado na rota pública correspondente, o usuário recebe um token assinado por um segredo criptográfico HMAC256 gerenciado pelo TokenService. Este token possui expiração configurada para duas horas com base no fuso horário brasileiro (ZoneOffset.of("-03:00")) e deve ser anexado via cabeçalho HTTP Authorization: Bearer \[token\] nas chamadas seguintes.

| Rota HTTP | Método | Nível de Permissão   |
| :---- | :---- | :---- |
| /auth/login | **POST** | **Público (Livre)** |
| /pessoas | **POST / GET** | **Público (Autocadastro e Listagem)** |
| /pessoas/{id} | **GET / DELETE** | **Autenticado (Exige Token JWT)** |
| /livros/\*\* | **Todos** | **Autenticado (Exige Token JWT)** |
| /emprestimos/\*\* | **Todos** | **Autenticado (Exige Token JWT)** |
| /swagger-ui/\*\*, /v3/api-docs/\*\* | **Todos** | **Público (Acesso à Documentação)** |

## **6\. Arquitetura Uniforme de Tratamento de Exceções**

Para evitar que detalhes internos da infraestrutura do servidor ou stacktraces longos de exceções de compilação sejam expostos ao ambiente externo, a aplicação utiliza um mecanismo interceptador centralizado gerenciado pela classe GlobalExceptionHandler. Este componente traduz anomalias de execução em contratos previsíveis estruturados com base no ErroResponseDTO, retornando respostas HTTP uniformes:

* **HttpMessageNotReadableException (HTTP 400):** Intercepta payloads JSON corrompidos, com chaves inválidas ou incompatibilidades severas de tipos de dados primitivos.  
* **MethodArgumentNotValidException (HTTP 400):** Captura violações nas restrições definidas pelas anotações do Jakarta Validation nos DTOs de entrada. O handler limpa a estrutura complexa do framework e aglutina os erros de campo em uma string sequencial direta e compreensível.  
* **MethodArgumentTypeMismatchException (HTTP 400):** Trata erros de digitação cometidos nos parâmetros dinâmicos informados diretamente na URL (ex: passar uma cadeia de caracteres alfanumérica em um endpoint que espera um ID numérico).  
* **DataIntegrityViolationException (HTTP 409):** Captura falhas de violação de restrições únicas disparadas pelo banco de dados (como tentativa de cadastrar e-mails ou CPFs que já existem no sistema), retornando o status HTTP Conflict.  
* **RuntimeException (HTTP 404 / 400):** Captura erros gerados de forma proposital na camada de serviço. Se a mensagem da exceção contiver o termo "não encontrado", o interceptador traduz automaticamente o retorno para o status HTTP 404 Not Found.

## **7\. Como Executar o Projeto**
  Na raiz do projeto execute.
  ```
  docker compose up -d
  ```
O que acontece por baixo dos panos durante este comando?

### **7.1 Multi-Stage Build (Dockerfile):**

* **Estágio 1 (Build):** O Docker baixa uma imagem pesada contendo o Maven e o Java 17. Ele copia o código-fonte, limpa o cache e compila o projeto (mvn clean package -DskipTests), gerando o arquivo .jar.
* **Estágio 2 (Runtime):** O Docker descarta a imagem anterior e utiliza uma imagem JRE 17 otimizada e leve (eclipse-temurin:17-jre-jammy). Ele copia apenas o .jar gerado no Estágio 1 para dentro desta imagem limpa e expõe a porta 8080.

### **7.2 Orquestração (docker-compose.yml):**
* **Serviço de Banco de Dados (postgres-db):** Inicia um container com o PostgreSQL 15. Ele cria automaticamente o banco biblioteca_db e define as credenciais base (emakers / emakers_password). Os dados são salvos em um volume persistente (postgres_data), o que significa que seus dados não sumirão caso o container seja reiniciado.
* **Serviço da API (api-service):** Inicia o container gerado pelo nosso Dockerfile. O Compose injeta automaticamente as variáveis de ambiente necessárias (URLs, usuário e senha) para que o Spring Boot e o Flyway consigam enxergar o container do banco de dados na rede isolada (biblioteca-network).
* **Controle de Dependência (depends_on):** O Docker Compose garante que o container da API só inicie depois que o container do banco de dados estiver de pé.

## **8\. 🌐 Acesso à Aplicação**

### **8.1 Com os containers em execução, a API estará imediatamente acessível:**

* **Documentação Interativa (Swagger UI):** ```http://localhost:8080/swagger-ui.html```
* **API Base URL:** ```http://localhost:8080/```

O Flyway será executado automaticamente na inicialização da aplicação, aplicando os arquivos de migração (V1 e V2) na base de dados recém-criada pelo container do Postgres.

### **8.2 🛑 Como Parar a Execução**

- Para parar os serviços e remover os containers gerados (mantendo os dados persistidos em disco graças ao volume), execute na raiz do projeto:
- ```docker compose down```
- Se por algum motivo quiser deletar os dados do banco e recomeçar do zero, rode com a flag -v para remover os volumes: 
- ```docker compose down -v```
