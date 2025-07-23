Centro comuntario api - Guia de Uso
Esta é uma API RESTful para gerenciamento de centros comunitários, permitindo cadastro, atualização de ocupação, intercâmbio de recursos e geração de relatórios. Desenvolvida com Spring Boot e MongoDB, é fácil de configurar e usar.
Como Usar
Pré-requisitos

Java 17
Maven 3.8+
Docker (para MongoDB)
Insomnia (para testar a API)

1. Clonar o Repositório


2. Configurar o MongoDB
Inicie o MongoDB com Docker:
docker-compose up -d

Isso cria um container MongoDB com:

Usuário: admin
Senha: admin
Banco: community_center
Porta: 27017

3. Executar a API
Compile e inicie a aplicação:
mvn clean compile
mvn spring-boot:run

A API estará disponível em http://localhost:8080.
4. Testar no Insomnia ou outra ferramente 
Para testar os endpoints abaixo. Todos os exemplos assumem que você configurou o ambiente corretamente.
Cadastrar um Centro
Cria um novo centro comunitário.
``POST http://localhost:8080/centers``

body: application/json

``{
  "id": "68815b1396ef83016f0fee2e",
  "nome": "Centro Comunitário A",
  "endereco": "Rua Exemplo, 123",
  "localizacao": {
    "latitude": -23.5505,
    "longitude": -46.6333
  },
  "capacidadeMaxima": 100,
  "ocupacaoAtual": 50,
  "recursos": {
    "medicos": 2,
    "voluntarios": 5,
    "kitsMedicos": 3,
    "veiculos": 1,
    "cestasBasicas": 10
  }
}``

Resposta: 201 Created com os dados do centro.
Atualizar Ocupação
Atualiza a ocupação de um centro.
``PUT http://localhost:8080/centers/(id)/occupancy``
Body: text/plain

75

Resposta: 200 OK com os dados atualizados do centro.
Realizar Intercâmbio
Troca recursos entre dois centros. Exemplo com ocupação > 90% no destino.
``POST http://localhost:8080/centers/exchange``
body: application/json
``
{
  "centroOrigemId": "(ID)",
  "centroDestinoId": "(ID)",
  "recursosOrigem": {
    "medicos": 1,
    "voluntarios": 0,
    "kitsMedicos": 0,
    "veiculos": 0,
    "cestasBasicas": 0
  },
  "recursosDestino": {
    "medicos": 0,
    "kitsMedicos": 1,
    "voluntarios": 0,
    "veiculos": 0,
    "cestasBasicas": 0
  }
}
``
Pré-condição: Crie o centro destino com ocupacaoAtual: 91:
``POST http://localhost:8080/centers``
body: application/json
``
{
  "id": "68815ab396ef83016f0fee2d",
  "nome": "Centro Comunitário B",
  "endereco": "Rua Teste, 456",
  "localizacao": {
    "latitude": -23.5505,
    "longitude": -46.6333
  },
  "capacidadeMaxima": 100,
  "ocupacaoAtual": 91,
  "recursos": {
    "medicos": 1,
    "voluntarios": 1,
    "kitsMedicos": 1,
    "veiculos": 1,
    "cestasBasicas": 5
  }
}``

Resposta: 201 Created com os detalhes da negociação.
Relatório: Centros com Ocupação > 90%
Lista centros com ocupação superior a 90%.
``GET http://localhost:8080/centers/high-occupancy``

Resposta: 200 OK com lista de centros (ex.: Centro B com ocupacaoAtual: 91).
Relatório: Média de Recursos
Calcula a média de recursos por centro.
``GET http://localhost:8080/centers/resources-average``

Resposta: 200 OK, exemplo:``
{
  "medicos": 1.5,
  "voluntarios": 3.0,
  "kitsMedicos": 1.5,
  "veiculos": 1.0,
  "cestasBasicas": 7.5
}``

Relatório: Histórico de Negociações
Lista negociações de um centro, com filtro opcional por data.
``GET http://localhost:8080/centers/(id)/exchanges?dataInicio=2025-07-23T17:06:15-03:00``

Resposta: 200 OK com lista de negociações do centro no período.


5. Verificar Dados no MongoDB
docker exec -it mongodb mongosh -u admin -p admin
use community_center
db.centros.find().pretty()
db.negociacoes.find().pretty()

Notas

Erros: Se receber 400 Bad Request (ex.: no PUT /centers/{id}/occupancy), verifique:
O corpo da requisição (ex.: número inteiro como 75).
O ID do centro existe no MongoDB.


Pontuação de Recursos:
Médicos: 4 pontos
Voluntários: 3 pontos
Kits médicos: 7 pontos
Veículos: 5 pontos
Cestas básicas: 2 pontos


Intercâmbio: Pontuação deve ser igual, salvo se um centro tiver ocupação > 90%.
Notificação: Log é gerado quando a ocupação atinge 100% (verifique no console).

Se precisar de ajuda com erros ou mais exemplos, entre em contato!

