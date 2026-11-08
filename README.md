# Lab 2 : API REST CRUD avec Spring Boot

Cours **Développement Backend avec Java, Node.js et PHP** - Master IAIDDWM, FST Marrakech.

## Description

API REST de gestion de produits (CRUD) développée avec **Spring Boot 3** et **Java 21**.
Le stockage est **en mémoire** (pas de base de données) : les données disparaissent à l'arrêt de l'application.
Les exercices du lab sont inclus : recherche par nom, `InvalidQuantityException` (quantité > 1000) et
`ProductRequest` / `ProductResponse` écrits sous forme de **records Java**.

## Prérequis

- JDK 21
- Maven (facultatif : le wrapper `mvnw` est fourni)
- Postman et Newman (facultatif, pour la collection de tests : `npm install -g newman`)

## Structure des packages

```
com.example.productapi
├── ProductApiApplication      point d'entrée
├── controller/                ProductController (mince, délègue au service)
├── service/                   ProductService (logique métier, mapping)
├── repository/                ProductRepository (HashMap + AtomicLong)
├── model/                     Product
├── dto/                       ProductRequest, ProductResponse (records)
└── exception/                 ResourceNotFoundException, InvalidQuantityException, GlobalExceptionHandler
```

Autres dossiers : `src/test/` (tests JUnit 5 + MockMvc), `postman/` (collection et environnement).

## Commandes

```bash
./mvnw spring-boot:run   # démarre l'API sur http://localhost:8080
./mvnw test              # lance les tests automatisés
```

Sous Windows (cmd / PowerShell) : `mvnw.cmd spring-boot:run` et `mvnw.cmd test`.

## Endpoints

| Méthode | URL | Description | Statuts |
|---|---|---|---|
| POST | `/api/products` | Crée un produit | 201 Created, 400 (validation ou quantité > 1000) |
| GET | `/api/products` | Liste tous les produits | 200 OK |
| GET | `/api/products/{id}` | Produit par id | 200 OK, 404 Not Found |
| GET | `/api/products/search?name=...` | Recherche par nom (contient, insensible à la casse) | 200 OK |
| PUT | `/api/products/{id}` | Remplace un produit | 200 OK, 400, 404 |
| DELETE | `/api/products/{id}` | Supprime un produit | 204 No Content, 404 Not Found |

Règles de validation : `name` obligatoire, `price` obligatoire et positif, `quantity` obligatoire,
comprise entre 0 et 1000, `description` facultative.

Formats d'erreur :

- 404 et quantité > 1000 : `{"error": "message"}`
- validation (400) : `{"champ": "message"}`, par exemple `{"name": "Le nom est obligatoire"}`

## Exemples curl

```bash
# Créer (201)
curl -i -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{"name":"Ordinateur Portable","description":"Laptop 15 pouces","price":1200.50,"quantity":10}'

# Création invalide, sans nom (400)
curl -i -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" -d '{"price":10,"quantity":1}'

# Lister, lire un produit, produit inexistant (404)
curl http://localhost:8080/api/products
curl http://localhost:8080/api/products/1
curl -i http://localhost:8080/api/products/999

# Modifier (200)
curl -X PUT http://localhost:8080/api/products/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"Ordinateur Portable Pro","price":1300.00,"quantity":8}'

# Rechercher par nom
curl "http://localhost:8080/api/products/search?name=portable"

# Quantité > 1000 (400)
curl -i -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" -d '{"name":"Stock","price":5,"quantity":1001}'

# Supprimer (204)
curl -i -X DELETE http://localhost:8080/api/products/1
```

## Collection Postman

1. Dans Postman : **Import**, puis sélectionner `postman/lab2-collection.postman_collection.json`
   et `postman/lab2-env.postman_environment.json`.
2. Choisir l'environnement **Lab 2 - Environnement** (`baseUrl` = `http://localhost:8080`).
3. Démarrer l'API, puis lancer **Run collection** dans l'ordre : le POST stocke l'id créé dans `{{productId}}`.

## Newman

Avec l'application démarrée :

```bash
newman run postman/lab2-collection.postman_collection.json \
  -e postman/lab2-env.postman_environment.json
```

Résultat de référence : 10 requêtes, 40 assertions, 0 échec.
