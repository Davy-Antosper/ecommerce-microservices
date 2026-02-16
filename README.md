# E-commerce Microservices Architecture

Système de microservices robuste et scalable construit avec **Java 21**, **Spring Boot 3.5**, et **Spring Cloud**.

---

##  Architecture du Système

Le projet est organisé en **Monorepo** comprenant les modules suivants :

* **`api-gateway`** : Point d'entrée unique gérant le routage, le Load Balancing et la résilience (Circuit Breaker avec Resilience4j).
* **`auth-service`** : Gestion de l'authentification JWT.
* **`catalog-servicer`** : Gestion du catalogue de produits (CRUD, stock).
* **`cart-service`** : Gestion des paniers d'achat utilisateurs.

---

## Techno

* **Backend** : Java 21, Spring Boot 3.5.10
* **Service Discovery** : Netflix Eureka
* **database** : H2 et Redis pour Cart(panier)
* **Routing** : Spring Cloud Gateway (Webflux)
* **Resilience** : Resilience4j (Circuit Breaker & Fallback)
* **Build Tool** : Maven
* **log** : SLF4J
* **Container** : Docker(en cours)

---
Accès API
<img width="1354" height="700" alt="Capture d’écran du 2026-02-16 13-06-18" src="https://github.com/user-attachments/assets/7540d1cd-6c5c-4415-a4c2-b78a15381f47" />
<img width="1354" height="700" alt="Capture d’écran du 2026-02-16 13-06-12" src="https://github.com/user-attachments/assets/5f26fbf9-9fd2-440b-898d-e6f164037888" />
<img width="1354" height="700" alt="Capture d’écran du 2026-02-16 13-06-02" src="https://github.com/user-attachments/assets/449705c7-4221-4133-b8e5-cf5a767a391b" />

Résilience
Le projet intègre des Circuit Breakers. Si un service comme le catalog-service tombe, la Gateway renvoie une réponse de secours (Fallback) pour éviter de bloquer l'utilisateur.

## Installation et Lancement (Local)
### Pré-requis
* JDK 21 ou supérieur
* Maven 3.9+
* Un serveur Eureka lancé sur le port `8761`

### Cloner le projet
   ```bash
   git clone [https://github.com/Davy-Antosper/ecommerce-microservices.git](https://github.com/Davy-Antosper/ecommerce-microservices.git)
   cd ecommerce-microservices
----
