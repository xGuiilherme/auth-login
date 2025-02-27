## Sistema de Login e Signup com Spring Security, JWT e RabbitMQ!
Este projeto foi separado em duas APIs para implementa um sistema de autenticação baseado em JSON Web Tokens (JWT) usando Spring Boot. Ele permite o cadastro, login, recuperação de senha e autorização de usuários com diferentes papéis (ADMIN e USER). O sistema é stateless e utiliza boas práticas de segurança, como criptografia de senhas e validação de tokens.

Esse microsserviço faz uma chamada para uma api externa do email link do [ms-email](https://github.com/xGuiilherme/ms-email).

## Funcionalidades Principais
- Cadastro de Usuário: Permite criar novos usuários com nome, email e senha.
- Login: Autentica os usuários e gera um token JWT para acesso às rotas protegidas.
- Recuperação de Senha: Fornece um mecanismo para redefinir a senha usando tokens temporários.
- Autorização por Papel: Restringe o acesso a determinadas rotas com base nos papéis do usuário (ADMIN ou USER).
- Token Stateless: Implementa autenticação stateless com tokens JWT.

## API Endpoints
### User Signup
- Method: POST
- Path: ```http://localhost:8081/api/v1/auth/signup```
- Descrição: Cadastra novos usuários
- Request Body: Dados do usuário no formato JSON (name, email, password)
```
{
  "fullName": "João Kumawat",
  "email": "teste@example.com",
  "password": "Shimbhu@123",
  "role": "ADMIN"
}
```
- Response:
```
{
    "success": true,
    "message": "Usuário cadastrado com sucesso",
    "data": {
        "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIi-_",
        "type": "Bearer"
    }
}
```
### User Login
- Method: POST
- Path: ```http://localhost:8081/api/v1/auth/login```
- Descrição: Autenticar um usuário e recuperar o token(jwt)
- Request Body: Dados do usuário no formato JSON (email e password)
```
{
    "email": "teste@example.com",
    "password": "Shimbhu@123"
}
```
- Response:
```
{
    "success": true,
    "message": "Autenticação realizada com sucesso",
    "data": {
        "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJndW-_",
        "type": "Bearer"
    }
}
```
### Solicitação de resete de senha
- Method: POST
- Path: ```http://localhost:8081/api/v1/auth/forgot-password```
- Descrição: Um endpoint protegido que requer autenticação para acesso de recuperação da senha
- Authorization - Auth Type: Bearer Token
- Exemplo:
  - Bearer Token: ```eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJTaGltYmh1Iiwic3ViIjoiSldUIFRva2VuIi-_```
- Request Body: Dados do usuário no formato JSON (e-mail)
```
{
    "email": "teste@example.com"
}
```
- Response:
```
{
    "success": true,
    "message": "Email de recuperação enviado!",
    "token": "1949b1a1-6a34-4449-bd03-25f4fc2e1bc6-_"
}
```
### Criar uma nova senha
- Method: PUT
- Path: ```http://localhost:8081/api/v1/auth/reset-password?token=```
- Descrição: Um endpoint protegido que requer autenticação passando o token gerado via parâmetro para criar uma nova senha
- Params: token: ```d108d03f-09d4-42c0-8771-8924e80d6daf```
- Request Body: Dados do usuário no formato JSON (email e password)
```
{
    "newPassword": "Teste1234@",
    "passwordConfirmation": "Teste1234@"
}
```
- Response:
```
{
    "success": true,
    "message": "Senha redefinida com sucesso",
    "data": null
}
```

## Tecnologias Utilizadas
- Java 21
- Spring Boot 3.4.x
- Spring Security
- Open Feign
- RabbitMQ
- JWT (JSON Web Token)
- MySQL
- Maven
