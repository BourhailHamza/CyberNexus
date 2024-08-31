# CyberNexus - Forum

Bienvenue sur **CyberNexus**, une plateforme conçue pour créer des salons et discuter en ligne.

## Table des matières

- [Aperçu du Projet](#aperçu-du-projet)
- [Fonctionnalités](#fonctionnalités)
- [Prérequis](#prérequis)
- [Installation](#installation)
- [Configuration de Sécurité](#configuration-de-sécurité)
- [Structure de la Base de Données](#structure-de-la-base-de-données)
- [Licence](#licence)

## Aperçu du Projet

**CyberNexus** est une application web développée avec **Spring Boot** et **Thymeleaf** permettant aux utilisateurs de créer des salons de discussion et d'y participer en envoyant des messages. Cette plateforme inclut des fonctionnalités de sécurité grâce à **Spring Security** pour authentifier les utilisateurs et contrôler l'accès aux différentes parties de l'application.

## Fonctionnalités

- Gestion des utilisateurs avec des rôles **ADMIN** et **USER**.
- Tableau de bord pour les administrateurs afin de voir et gérer les messages signalés.
- Création et suppression des salons.
- Création, suppression et signalement des messages.
- Fonctions de connexion et de déconnexion sécurisées.
- Protection CSRF intégrée.
- Interface utilisateur moderne utilisant **Bootstrap 5.3.0**.

## Prérequis

Avant de commencer, assurez-vous d'avoir les éléments suivants installés sur votre machine :

- [Docker](https://www.docker.com/)

## Installation

1. **Clonez le dépôt :**

    ```bash
    git clone https://github.com/BourhailHamza/CyberNexus
    cd CyberNexus
    ```

2. **Lancez l'application avec Docker :**

   Utilisez Docker pour construire et lancer l'application :

    ```bash
    docker-compose up --build
    ```

3. **Accédez à l'application :**

   Ouvrez votre navigateur et allez à [http://localhost:8080](http://localhost:8080).

## Configuration de Sécurité

Le fichier `SecurityConfig` contient la configuration de sécurité de l'application, utilisant **Spring Security** pour gérer l'authentification et l'autorisation.

- **Formulaire de connexion** sécurisé avec gestion des erreurs et des succès de connexion.
- **Protection CSRF** activée.
- **Autorisation basée sur les rôles** avec des contrôles d'accès spécifiques.

## Structure de la Base de Données

La base de données utilise plusieurs tables pour gérer les utilisateurs, les rôles, les messages et les signalements. Voici une brève description des tables :

- **users** : Stocke les informations des utilisateurs.
- **roles** : Définit les rôles disponibles (ADMIN, USER).
- **user_roles** : Associe les utilisateurs à leurs rôles.
- **messages** : Contient les messages postés dans le forum.
- **reports** : Contient les informations des messages signalés par les utilisateurs.

## Licence

Ce projet est sous licence MIT. Vous êtes libre d'utiliser, de modifier et de distribuer ce logiciel sous les conditions de la licence. Voir le fichier [LICENSE](LICENSE.md) pour plus de détails.
