# Architecture Microservices avec RestTemplate

Ce projet présente une architecture microservices complète utilisant Spring Boot, Spring Cloud, Eureka pour la découverte de services, Spring Cloud Gateway comme API Gateway, et RestTemplate pour la communication inter-services.
<img width="940" height="375" alt="image" src="https://github.com/user-attachments/assets/edfbe278-8102-414f-b01f-567e13985c05" />

## 📋 Table des matières

- [Vue d'ensemble](#vue-densemble)
- [Architecture](#architecture)
- [Services](#services)
- [Technologies utilisées](#technologies-utilisées)
- [Prérequis](#prérequis)
- [Installation et démarrage](#installation-et-démarrage)
- [Endpoints API](#endpoints-api)
- [Communication inter-services](#communication-inter-services)
- [Monitoring](#monitoring)
- [Structure du projet](#structure-du-projet)

## 🎯 Vue d'ensemble

Cette application démontre une architecture microservices avec :
- **Service Discovery** : Eureka Server pour l'enregistrement et la découverte des services
- **API Gateway** : Spring Cloud Gateway comme point d'entrée unique
- **Microservices métier** :
  - **Service Client** : Gestion des clients
  - **Service Car** : Gestion des voitures avec récupération des informations clients
- **Communication inter-services** : RestTemplate avec LoadBalancing via Eureka

## 🏗️ Architecture

```
┌─────────────┐
│   Client    │
│  (Browser)  │
└──────┬──────┘
       │
       │ HTTP Requests
       │
┌──────▼──────────────────────────────────────┐
│         Spring Cloud Gateway                │
│              (Port 8888)                    │
│     Point d'entrée unique de l'API         │
└──────┬──────────────────────────────────────┘
       │
       │ Routage dynamique
       │
       ├──────────────────┬──────────────────┐
       │                  │                  │
┌──────▼──────┐    ┌──────▼──────┐    ┌──────▼──────┐
│  SERVICE-   │    │  SERVICE-   │    │   Eureka    │
│  CLIENT     │    │    CAR       │    │   Server    │
│  (8081)     │    │  (8082)      │    │   (8761)    │
│             │    │              │    │             │
│  MySQL      │    │  MySQL       │    │  Service    │
│  clientserv │    │  carservicedb│    │  Discovery  │
│  icedb      │    │              │    │             │
└──────┬──────┘    └──────┬───────┘    └─────────────┘
       │                  │
       │                  │ RestTemplate
       │                  │ (avec @LoadBalanced)
       └──────────────────┘
       Communication inter-services
```

## 🔧 Services

### 1. Eureka Server (Port 8761)
Service de découverte et d'enregistrement des microservices.

**Fonctionnalités :**
- Enregistrement automatique des services
- Découverte de services
- Dashboard de monitoring

**Accès :** http://localhost:8761

### 2. Spring Cloud Gateway (Port 8888)
API Gateway centralisée qui route les requêtes vers les microservices appropriés.

**Fonctionnalités :**
- Routage dynamique basé sur la découverte de services
- Point d'entrée unique pour tous les clients
- Load balancing automatique

### 3. Service Client (Port 8081)
Microservice de gestion des clients.

**Base de données :** `clientservicedb`

**Entité :**
- `Client` : id, nom, age

**Fonctionnalités :**
- CRUD complet pour les clients
- Enregistrement auprès d'Eureka
- Endpoints Actuator pour le monitoring

### 4. Service Car (Port 8082)
Microservice de gestion des voitures avec récupération des informations clients.

**Base de données :** `carservicedb`

**Entité :**
- `Car` : id, brand, model, matricule, clientId

**Fonctionnalités :**
- CRUD pour les voitures
- Communication avec le Service Client via RestTemplate
- Enrichissement des données avec les informations clients
- Enregistrement auprès d'Eureka

## 🛠️ Technologies utilisées

- **Java** : 21
- **Spring Boot** : 3.3.5 / 4.0.0
- **Spring Cloud** : 2023.0.4 / 2025.0.0
- **Spring Cloud Netflix Eureka** : Service Discovery
- **Spring Cloud Gateway** : API Gateway
- **Spring Data JPA** : Accès aux données
- **MySQL** : Base de données
- **RestTemplate** : Communication inter-services
- **Lombok** : Réduction du code boilerplate
- **Spring Boot Actuator** : Monitoring et health checks

## 📦 Prérequis

Avant de commencer, assurez-vous d'avoir installé :

- **JDK 21** ou supérieur
- **Maven 3.6+**
- **MySQL 8.0+**
- **IDE** (IntelliJ IDEA, Eclipse, VS Code)

## 🚀 Installation et démarrage

### 1. Configuration de la base de données

Assurez-vous que MySQL est démarré et créez les bases de données (elles seront créées automatiquement si `createDatabaseIfNotExist=true`) :

```sql
-- Les bases de données seront créées automatiquement
-- clientservicedb pour le service Client
-- carservicedb pour le service Car
```

Modifiez les identifiants dans les fichiers `application.yml` si nécessaire :
- `username: "root"`
- `password: ""` (votre mot de passe MySQL)

### 2. Ordre de démarrage

**IMPORTANT :** Démarrez les services dans l'ordre suivant :

#### Étape 1 : Démarrer Eureka Server
```bash
cd eureka-server
mvn spring-boot:run
```

Vérifiez que le serveur est démarré : http://localhost:8761

#### Étape 2 : Démarrer Spring Cloud Gateway
```bash
cd gateway
mvn spring-boot:run
```

#### Étape 3 : Démarrer le Service Client
```bash
cd client
mvn spring-boot:run
```

#### Étape 4 : Démarrer le Service Car
```bash
cd car
mvn spring-boot:run
```

### 3. Vérification

Une fois tous les services démarrés, vérifiez le dashboard Eureka :
- Ouvrez http://localhost:8761
- Vous devriez voir les services suivants enregistrés :
  - **GATEWAY** (Port 8888)
  - **SERVICE-CLIENT** (Port 8081)
  - **SERVICE-CAR** (Port 8082)

## 📡 Endpoints API

### Via API Gateway (Port 8888)

Tous les endpoints sont accessibles via la Gateway :

```
http://localhost:8888/{SERVICE-NAME}/api/{resource}
```

### Service Client

#### Direct (Port 8081)
- `GET http://localhost:8081/api/client` - Liste tous les clients
- `GET http://localhost:8081/api/client/{id}` - Récupère un client par ID
- `POST http://localhost:8081/api/client` - Crée un nouveau client

**Exemple de requête POST :**
```json
{
  "nom": "Jean Dupont",
  "age": 30
}
```

#### Via Gateway
- `GET http://localhost:8888/SERVICE-CLIENT/api/client`
- `GET http://localhost:8888/SERVICE-CLIENT/api/client/{id}`
- `POST http://localhost:8888/SERVICE-CLIENT/api/client`

### Service Car

#### Direct (Port 8082)
- `GET http://localhost:8082/api/car` - Liste toutes les voitures avec leurs clients
- `GET http://localhost:8082/api/car/{id}` - Récupère une voiture par ID avec les détails du client

**Exemple de réponse :**
```json
{
  "id": 1,
  "brand": "Toyota",
  "model": "Corolla",
  "matricule": "AB-123-CD",
  "client": {
    "id": 1,
    "name": "Jean Dupont",
    "age": 30
  }
}
```

#### Via Gateway
- `GET http://localhost:8888/SERVICE-CAR/api/car`
- `GET http://localhost:8888/SERVICE-CAR/api/car/{id}`

## 🔄 Communication inter-services

Le **Service Car** communique avec le **Service Client** en utilisant **RestTemplate** avec **@LoadBalanced**.

### Configuration

Dans `CarApplication.java` :
```java
@Bean
@LoadBalanced  // Active le load balancing via Eureka
public RestTemplate restTemplate() {
    // Configuration avec timeouts
}
```

### Utilisation

Dans `CarService.java` :
```java
private static final String CLIENT_SERVICE_URL = 
    "http://SERVICE-CLIENT/api/client/";

// RestTemplate résout automatiquement le nom du service via Eureka
Client client = restTemplate.getForObject(
    CLIENT_SERVICE_URL + car.getClientId(),
    Client.class
);
```

**Avantages :**
- Résolution automatique des services via Eureka
- Load balancing automatique
- Pas besoin de connaître l'URL exacte du service
- Découplage des services

## 📊 Monitoring

### Spring Boot Actuator

Tous les services exposent des endpoints Actuator :

#### Service Client (Port 8081)
- Health : http://localhost:8081/actuator/health
- Info : http://localhost:8081/actuator/info
- Metrics : http://localhost:8081/actuator/metrics
- Discovery : http://localhost:8081/actuator/discovery

#### Service Car (Port 8082)
- Health : http://localhost:8082/actuator/health
- Info : http://localhost:8082/actuator/info
- Metrics : http://localhost:8082/actuator/metrics
- Discovery : http://localhost:8082/actuator/discovery

### Eureka Dashboard

- Dashboard : http://localhost:8761
- Visualisation des services enregistrés
- Statut de santé des instances
- Métriques système

## 📁 Structure du projet

```
Architecture Micro-services avec RestTemplate/
│
├── eureka-server/          # Service Discovery (Port 8761)
│   ├── src/main/java/
│   │   └── EurekaServerApplication.java
│   └── src/main/resources/
│       └── application.yml
│
├── gateway/                # API Gateway (Port 8888)
│   ├── src/main/java/
│   │   └── GatewayApplication.java
│   └── src/main/resources/
│       └── application.yml
│
├── client/                 # Service Client (Port 8081)
│   ├── src/main/java/
│   │   ├── ClientApplication.java
│   │   ├── controllers/
│   │   │   └── ClientController.java
│   │   ├── entities/
│   │   │   └── Client.java
│   │   ├── repositories/
│   │   │   └── ClientRepository.java
│   │   └── services/
│   │       └── ClientService.java
│   └── src/main/resources/
│       └── application.yml
│
└── car/                    # Service Car (Port 8082)
    ├── src/main/java/
    │   ├── CarApplication.java
    │   ├── controllers/
    │   │   └── CarController.java
    │   ├── entities/
    │   │   └── Car.java
    │   ├── models/
    │   │   ├── CarResponse.java
    │   │   └── Client.java
    │   ├── repositories/
    │   │   └── CarRepository.java
    │   └── services/
    │       └── CarService.java
    └── src/main/resources/
        └── application.yml
```

## 🧪 Tests

### Test manuel avec cURL

#### Créer un client
```bash
curl -X POST http://localhost:8081/api/client \
  -H "Content-Type: application/json" \
  -d '{"nom":"Jean Dupont","age":30}'
```

#### Récupérer tous les clients
```bash
curl http://localhost:8081/api/client
```

#### Récupérer une voiture avec son client
```bash
curl http://localhost:8082/api/car/1
```

### Test via Postman

Importez les endpoints dans Postman et testez les différentes requêtes.

## 🔍 Dépannage

### Problème : Service non enregistré dans Eureka

**Solution :**
1. Vérifiez que Eureka Server est démarré en premier
2. Vérifiez la configuration dans `application.yml`
3. Vérifiez les logs pour les erreurs de connexion

### Problème : Erreur de communication inter-services

**Solution :**
1. Vérifiez que `@LoadBalanced` est présent sur le bean RestTemplate
2. Vérifiez que le nom du service dans l'URL correspond au nom dans Eureka
3. Vérifiez que les deux services sont enregistrés dans Eureka

### Problème : Erreur de connexion à la base de données

**Solution :**
1. Vérifiez que MySQL est démarré
2. Vérifiez les identifiants dans `application.yml`
3. Vérifiez que les ports MySQL ne sont pas bloqués

## 📝 Notes importantes

- **Ordre de démarrage** : Toujours démarrer Eureka Server en premier
- **Ports** : Assurez-vous que les ports 8761, 8888, 8081, 8082 sont libres
- **Base de données** : Les bases de données sont créées automatiquement au premier démarrage
- **Load Balancing** : RestTemplate avec `@LoadBalanced` résout automatiquement les services via Eureka

## 👥 Auteur

Projet développé pour démontrer une architecture microservices avec Spring Cloud.

## 📄 Licence

Ce projet est à des fins éducatives.

---

**Bon développement ! 🚀**

