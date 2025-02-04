## Sistema de Login e Signup com Spring Security e JWT Tokens!
Este projeto implementa um sistema de autenticação baseado em JSON Web Tokens (JWT) usando Spring Boot. Ele permite o cadastro, login, recuperação de senha e autorização de usuários com diferentes papéis (ADMIN e USER). O sistema é stateless e utiliza boas práticas de segurança, como criptografia de senhas e validação de tokens.

## Funcionalidades Principais
- Cadastro de Usuário: Permite criar novos usuários com nome, email e senha.
- Login: Autentica os usuários e gera um token JWT para acesso às rotas protegidas.
- Recuperação de Senha: Fornece um mecanismo para redefinir a senha usando tokens temporários.
- Autorização por Papel: Restringe o acesso a determinadas rotas com base nos papéis do usuário (ADMIN ou USER).
- Token Stateless: Implementa autenticação stateless com tokens JWT.

## API Endpoints
### User Signup
- Method: POST
- Path: ```http://localhost:8081/api/v1/auth/singup```
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
    "message": "Usuário registrado com sucesso"
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
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJsZW8uc2lsdmFAZXhhb=..."
}
```
### Solicitação de resete de senha
- Method: POST
- Path: ```http://localhost:8081/api/v1/auth/forgot-password```
- Descrição: Um endpoint protegido que requer autenticação para acesso de recuperação da senha
- Authentication: Bearer Token
- Exemplo:
  - Bearer Token: ```eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJTaGltYmh1Iiwic3ViIjoiSldUIFRva2VuIiwidXNlcm5hbWUiOiJza0BnbWFpbC5jb20iLCJyb2xlIjoiUk9MRV9VU0VSIiwiaWF0IjoxNjg1Njc3Mzg3LCJleHAiOjE2OD```
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
    "message": "Token de redefinição de senha gerado com sucesso",
    "token": "1949b1a1-6a34-4449-bd03-25f4fc2e1bc6"
}
```
### Criar uma nova senha
- Method: PUT
- Path: ```http://localhost:8081/api/v1/auth/reset-password```
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
    "message": "Senha redefinida com sucesso"
}
```

## Tecnologias Utilizadas
- Java 21
- Spring Boot 3.4.x
- Spring Security.
- JWT (JSON Web Token)
- H2 Database.
- Lombok.
- Maven.
