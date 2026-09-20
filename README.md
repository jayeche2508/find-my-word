# Find My Word

Version enrichie du jeu de devinette de mots inspiré de Wordle, développée en Java dans le cadre de ma formation en BUT Informatique.

## Description

Le joueur dispose de 6 tentatives pour deviner un mot secret de 5 lettres. Après chaque tentative, chaque lettre est indiquée comme correcte et bien placée (`OK`), présente mais mal placée (`PRESENT`), ou absente du mot (`ABSENT`).

Projet réalisé en binôme, avec une modélisation UML préalable (classes abstraites, polymorphisme) respectant les principes de la programmation orientée objet.

## Fonctionnalités

Au-delà des règles de base, le jeu propose plusieurs modes :

- **Mode Classique** — les règles standards du jeu
- **Mode Survie** — enchaîne les mots jusqu'à la première erreur
- **Mode Multijoueur local** — plusieurs joueurs s'affrontent sur le même mot
- **Mode Streak** — enchaîne les victoires consécutives
- **Mode Time Attack** — ajoute une contrainte de temps
- **Mode Blind** — variante sans affichage de certains indices

Le joueur gagne des points selon le nombre d'essais utilisés, ainsi qu'un titre selon sa performance.

## Architecture

Le projet respecte une séparation claire entre la logique de jeu et l'affichage :

- `Main` — point d'entrée du programme
- `Game` — gère la partie, les tentatives, l'analyse des lettres et la détection de fin de partie
- `Word` — encapsule les règles de validité d'un mot (longueur, caractères, lettres non répétées)
- `Display` — gère uniquement l'affichage, sans logique métier
- `WordRepository` (classe abstraite) — définit le contrat de récupération d'un mot secret
- `JsonWordRepository` / `FixedWordRepository` — implémentations concrètes : mot aléatoire depuis un fichier JSON, ou mot fixé à l'avance (utile pour les tests)
- `RetrieveWordsFromJSON` — lecture et parsing du fichier de mots

## Technologies utilisées

- Java
- JSON (liste de mots)
- UML (modélisation orientée objet)

## Lancer le projet

Depuis un terminal, à la racine du projet :

```bash
javac -d bin $(find src -name "*.java")
java -cp bin findmyword.Main
```

Le programme propose ensuite deux modes de lancement : mots aléatoires ou mode test (mot fixe `ligne`), puis un menu pour choisir parmi les différents modes de jeu.

## Auteurs

- Carounagarane Jayeche
- Shayan Issac
