# Documentation pour lancer l'environnement Docker

## Prérequis

Avant de commencer, assurez-vous que Docker est installé sur votre machine.

- **Docker** : [Installation Docker](https://www.docker.com/get-started)

## Lancer l'Environnement Docker pour le Front-End

### Étape 1 : Vérification de Docker

Avant de lancer l'environnement, vérifiez que Docker est bien en fonctionnement sur votre machine. Si Docker est déjà en cours d'exécution, passez à l'étape suivante.

### Étape 2 : Positionnez-vous dans le Répertoire du Projet

Lancez l'environnement Docker pour le Front-end :

```bash
  docker-compose -f docker-compose.base.yml -f docker-compose.front.yml up -d
```

Lien de la documentation de l'[API](http://localhost:8080/swagger-ui/index.html#/)

Arrêtez l'environnement Docker pour le Front-end :

```bash
  docker-compose -f docker-compose.base.yml -f docker-compose.front.yml down
```

