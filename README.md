# 🚦 Monitoramento de Tráfego - Backend (São José dos Campos)

Este repositório contém o **back-end do sistema de monitoramento de tráfego** da cidade de São José dos Campos, desenvolvido pela equipe **VORTEK**.

O back-end é responsável pelo **processamento de dados dos radares** e pela **disponibilização de APIs REST escaláveis** para integração com o front-end, fornecendo informações de tráfego em tempo real e armazenando dados históricos para análises.

O projeto foi desenvolvido com **Java Spring Boot**, garantindo performance, escalabilidade e facilidade de manutenção.

O banco de dados utilizado é **Oracle**, utilizando **wallet** para conexão segura.

---

## ⚙️ Pré-requisitos

Antes de rodar o projeto, certifique-se de ter instalado:

- **Java 17** ou superior  
- **Maven** ou **Gradle**  
- **Oracle Database** com acesso via wallet  

Verifique as versões no terminal:

```bash
java -version
mvn -v      # Se usar Maven
gradle -v   # Se usar Gradle
```

👨‍💻 Configuração do Banco de Dados Oracle
No repositório, há uma pasta config com exemplo de application.properties para Oracle.

Abra src/main/resources/application.properties (ou application.yml) e configure a conexão:


``` bash
# Configuração do Oracle DB
spring.datasource.url=jdbc:oracle:thin:@//meu_host:1521/meu_servico
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
spring.datasource.driver-class-name=oracle.jdbc.OracleDriver

# Configuração da Wallet (SSL)
spring.datasource.ssl=true
spring.datasource.wallet.path=classpath:wallet
```
⚠️ A pasta wallet/ já está no repositório com os arquivos necessários para conectar ao banco. Não compartilhe fora do projeto.

### 🔑 Login e Autenticação

O back-end possui endpoints de autenticação JWT:
``` bash
POST /auth/login: recebe { "email": "email", "senha": "senha" } e retorna token JWT.
``` 

Use o token JWT retornado no login para acessar endpoints protegidos, enviando no header:
``` bash
Authorization: Bearer <token>
``` 

👨‍💻 Rodando o Backend

Clone o repositório e entre na pasta:

``` bash
git clone <url-do-seu-repositorio>
cd caminho/do/seu-projeto-backend
``` 

Compile e rode a aplicação:
``` bash
Com Maven:

mvn clean install
mvn spring-boot:run
``` 

O back-end estará disponível em: http://localhost:8080
