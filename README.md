# Emiteaí

## Sobre o projeto

Sistema para a empresa Emiteaí que gerencia o cadastro de pessoas físicas.

## Instruções para gerar o relatório
- Cadastrar uma ou mais pessoas com CPF distintos
- Envia uma requisição no endpoint Solicitar relatório. Ex: {host}/v1/pessoas/relatorio/solicitar
- Envia uma requisição no endpoint Acompanhar status. Ex: {host}/v1/pessoas/relatorio/status
- Quando o endpoint pessoas/relatorio/status retorna a resposta "CONCLUIDO"
- Envia uma requisição no endpoint Buscar Relatório. Ex: {host}/v1/pessoas/relatorio

### Lista de CPFs válidos para cadastro de pessoas
- 833.334.480-17
- 914.334.180-29
- 156.218.570-54
- 948.823.320-60
- 851.611.570-46

## Collection do postman
- Está na pasta postman deste repositório.

## Funcionalidades desenvolvidas
- CRUD de pessoas
- Validação de duplicidade no campo CPF
- Geração de relatório em csv executada de forma assíncrona pelo RabbitMQ
- Implementação de Filter para interceptar as requisição que chegam na API

## Tecnologias utilizadas
- Java 17
- Spring (boot, web, data jpa, validation)
- Postgres
- H2
- Flyway
- Lombok
- ModelMapper
- Maven
- RabbitMQ
- Docker

## Ferramentas utilizadas
- IntelliJ
- Postman
- PgAdmin
- Docker Desktop
- Git Bash

## Como executar o projeto

### Pré-requisitos
Docker em execução

```bash
# clonar repositório
git clone https://github.com/LuisPaulo1/emiteai.git

# entrar na pasta do projeto emiteai
cd emiteai

# executar o projeto
docker-compose up
```

# Autor

Luis Paulo

https://www.linkedin.com/in/luis-paulo-souza-a54358134/
