package findmyword.game;

import findmyword.display.Display;
import findmyword.model.Status;
import findmyword.model.Word;
import findmyword.repository.WordRepository;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Game {
    private static final int WORD_LENGTH = 5;

    private final WordRepository repository;
    private final Display display;
    private final Scanner scanner;

    private Word secretWord;
    private String playerName;
    private Word[] historyWords;
    private Status[][] historyStatus;
    private int attempts;
    private int maxAttempts;

    public Game(WordRepository repository, Scanner scanner) {
        this.repository = repository;
        this.display = new Display();
        this.scanner = scanner;
    }

    public void start() {
        display.printHeader();
        askPlayerName();
        display.printWelcome(playerName);

        while (true) {
            System.out.println("        MENU PRINCIPAL        ");
            System.out.println("");
            System.out.println("1. Mode Classique");
            System.out.println("2. Mode Survie");
            System.out.println("3. Mode Multijoueur Local");
            System.out.println("4. Mode Streak");
            System.out.println("5. Mode Time Attack");
            System.out.println("6. Mode Blind");
            System.out.println("7. Quitter le jeu");
            System.out.print("Choisir le mode : ");

            String choix = scanner.nextLine().trim();

            if (choix.equals("1")) {
                playSinglePlayer("Classique", 6, false);
            } else if (choix.equals("2")) {
                playSinglePlayer("Survie", 4, false);
            } else if (choix.equals("3")) {
                playMultiplayer();
            } else if (choix.equals("4")) {
                playStreak();
            } else if (choix.equals("5")) {
                playTimeAttack();
            } else if (choix.equals("6")) {
                playSinglePlayer("Aveugle", 6, true);
            } else if (choix.equals("7")) {
                System.out.println("A bientot !");
                break;
            } else {
                System.out.println("Tu fais quoi la, tape un chiffre entre 1 et 7.");
            }
        }
    }

    private void askPlayerName() {
        System.out.print("Entrez votre nom : ");
        playerName = scanner.nextLine().trim();
        if (playerName.isEmpty()) {
            playerName = "Joueur";
        }
    }

    private void playSinglePlayer(String nomMode, int maxEssais, boolean modeAveugle) {
        int[] res = playRound(this.playerName, maxEssais, modeAveugle);
        sauvegarderScore(this.playerName, nomMode, res[1], res[2]);
    }

    private void playMultiplayer() {
        System.out.print("\nNom du Joueur 1 : ");
        String j1 = scanner.nextLine().trim();
        if (j1.isEmpty()) {
            j1 = "Joueur 1";
        }

        System.out.print("Nom du Joueur 2 : ");
        String j2 = scanner.nextLine().trim();
        if (j2.isEmpty()) {
            j2 = "Joueur 2";
        }

        System.out.println("\n--- Tour de " + j1 + " ---");
        int[] resJ1 = playRound(j1, 6, false);

        System.out.println("\n--- Tour de " + j2 + " ---");
        int[] resJ2 = playRound(j2, 6, false);

        System.out.println("\nRESULTAT MULTIJOUEUR");
        System.out.println(j1 + " : " + resJ1[1] + " points (" + resJ1[2] + " sec)");
        System.out.println(j2 + " : " + resJ2[1] + " points (" + resJ2[2] + " sec)");

        if (resJ1[1] > resJ2[1]) {
            System.out.println(j1 + " a detruit " + j2 + " !");
        } else if (resJ2[1] > resJ1[1]) {
            System.out.println(j2 + " a ecrase " + j1 + " !");
        } else {
            if (resJ1[2] < resJ2[2]) {
                System.out.println("Egalite, mais " + j1 + " a ete plus rapide !");
            } else if (resJ2[2] < resJ1[2]) {
                System.out.println("Egalite, mais " + j2 + " a ete plus rapide !");
            } else {
                System.out.println("Egalite parfaite , Impossible de vous departager.");
            }
        }
    }

    private void playStreak() {
        int chaine = 0;
        System.out.println("\nMODE STREAK : Trouve le maximum de mots d'affilee sans erreur !");

        while (true) {
            int[] res = playRound(this.playerName, 6, false);

            if (res[0] == 1) {
                chaine++;
                System.out.println("\nSerie en cours : " + chaine + " victoire(s) ! On enchaine !");
            } else {
                System.out.println("\nFin de la serie , Tu as trouve " + chaine + " mot(s) d'affilee.");
                sauvegarderScore(this.playerName, "Streak", chaine, 0);
                break;
            }
        }
    }

    // retourne [aTrouve, points, temps]
    private int[] playRound(String joueurActuel, int maxEssais, boolean modeAveugle) {
        this.maxAttempts = maxEssais;
        this.historyWords = new Word[maxAttempts];
        this.historyStatus = new Status[maxAttempts][WORD_LENGTH];
        this.secretWord = repository.getWord();
        this.attempts = 0;
        int malusPoints = 0;

        System.out.println("\n[ ][ ][ ][ ][ ] -> ????? ????? ????? ????? ?????");
        if (!modeAveugle) {
            System.out.println("(Astuce : si t'es bloque, Tape ?AIDE pour obtenir une lettre)\n");
        } else {
            System.out.println("ATTENTION : MODE AVEUGLE. Souviens-toi bien de tes essais precedents, sinon t'es mort !\n");
        }

        boolean trouve = false;
        long debutChrono = System.currentTimeMillis();

        while (attempts < maxAttempts && !trouve) {
            System.out.print("Tentative " + (attempts + 1) + " : ");
            String saisie = scanner.nextLine().trim();

            if (saisie.equalsIgnoreCase("?AIDE")) {
                if (modeAveugle) {
                    System.out.println("Pas de joker en mode Aveugle, ce serait trop facile ");
                    continue;
                }
                char indice = '?';
                for (int i = 0; i < WORD_LENGTH; i++) {
                    boolean dejaTrouve = false;
                    for (int j = 0; j < attempts; j++) {
                        if (historyStatus[j] != null && historyStatus[j][i] == Status.OK) {
                            dejaTrouve = true;
                            break;
                        }
                    }
                    if (!dejaTrouve) {
                        indice = secretWord.charAt(i);
                        break;
                    }
                }
                if (indice != '?') {
                    System.out.println("\n[JOKER] Voici une lettre bien placee : '" + Character.toUpperCase(indice) + "'");
                    System.out.print("Emplacement : ");
                    for (int k = 0; k < WORD_LENGTH; k++) {
                        if (secretWord.charAt(k) == indice) {
                            System.out.print("[ " + Character.toUpperCase(indice) + " ] ");
                        } else {
                            System.out.print("[ _ ] ");
                        }
                    }
                    System.out.println();
                    malusPoints = malusPoints + 2;
                    System.out.println("Attention : Malus applique (-2 pts) !\n");
                } else {
                    System.out.println("\n[JOKER] T'as deja trouve toutes les bonnes places ! Cherche encore !\n");
                }
                continue;
            }

            Word w = new Word(saisie);
            if (!isAttemptValid(w)) {
                continue;
            }

            System.out.println();
            Status[] resultat = analyzeAttempt(w);
            historyWords[attempts] = w;
            historyStatus[attempts] = resultat;
            attempts++;

            displayGrid(modeAveugle);

            if (isVictory(resultat)) {
                trouve = true;
            }
        }

        int tempsSecondes = (int) ((System.currentTimeMillis() - debutChrono) / 1000);
        int points = 0;
        String rang = "";

        if (trouve) {
            points = (maxAttempts + 1) - attempts - malusPoints;
            if (points < 0) {
                points = 0;
            }

            System.out.println("Bravo " + joueurActuel + ", Vous etes le meilleur.");
            System.out.println("Vous avez trouve le mot en " + attempts + " essais et " + tempsSecondes + " secondes.");

            if (attempts <= maxAttempts / 3) {
                rang = "Empereur de Villetaneuse";
            } else if (attempts <= (maxAttempts * 2) / 3) {
                rang = "Prince de Villetaneuse";
            } else {
                rang = "Etudiant de Villetaneuse";
            }

            System.out.println("Titre decroche : " + rang);
            System.out.println("Vous avez : " + points + " points");
        } else {
            rang = "Sous-Fifre de Villetaneuse";
            System.out.println("Vous avez utilise tous vos essais. Reessayez la prochaine fois, looser. ");
            System.out.println("Perdu. J'ai connu des gens bien meilleur.");
            System.out.println("Titre decroche : " + rang);
            System.out.println("Le mot secret etait : " + secretWord.getValue().toUpperCase());
        }

        int aTrouve = 0;
        if (trouve) {
            aTrouve = 1;
        }
        return new int[]{aTrouve, points, tempsSecondes};
    }

    private void playTimeAttack() {
        System.out.println("        CONTRE-LA-MONTRE        ");
        System.out.println("");
        System.out.println("Tu as 60 secondes pour trouver le plus de mots possible.");
        System.out.println("Pas de joker autorise. C'est parti ");

        long debutChrono = System.currentTimeMillis();
        int scoreTotal = 0;

        while (System.currentTimeMillis() - debutChrono < 60000) {
            this.secretWord = repository.getWord();
            this.attempts = 0;
            this.maxAttempts = 6;
            this.historyWords = new Word[6];
            this.historyStatus = new Status[6][WORD_LENGTH];
            boolean trouve = false;

            System.out.println("\nNOUVEAU MOT");

            while (attempts < 6 && !trouve) {
                long tempsRestant = 60 - (System.currentTimeMillis() - debutChrono) / 1000;

                if (tempsRestant <= 0) {
                    break;
                }

                System.out.print("Tentative " + (attempts + 1) + " (" + tempsRestant + "s restantes) : ");
                String saisie = scanner.nextLine().trim();

                if (System.currentTimeMillis() - debutChrono >= 60000) {
                    break;
                }

                if (saisie.equalsIgnoreCase("?AIDE")) {
                    System.out.println("Pas de joker dans le mode contre-la-montre ");
                    continue;
                }

                Word w = new Word(saisie);
                if (!isAttemptValid(w)) {
                    continue;
                }

                System.out.println();
                Status[] resultat = analyzeAttempt(w);
                historyWords[attempts] = w;
                historyStatus[attempts] = resultat;
                attempts++;

                displayGrid(false);

                if (isVictory(resultat)) {
                    trouve = true;
                    scoreTotal++;
                    System.out.println("T'es fort , allez enchaine ");
                }
            }

            if (!trouve && System.currentTimeMillis() - debutChrono < 60000) {
                System.out.println("Bloque ? Le mot etait : " + secretWord.getValue().toUpperCase());
            }
        }

        System.out.println("\nAllez Temps ecoule ");
        System.out.println("\nFIN DU CONTRE-LA-MONTRE ");
        System.out.println("Score final : " + scoreTotal + " mot(s) trouve(s) en 60 secondes ");

        String rang = "";
        if (scoreTotal >= 5) {
            rang = "Empereur de Villetaneuse";
        } else if (scoreTotal >= 3) {
            rang = "Prince de Villetaneuse";
        } else if (scoreTotal >= 1) {
            rang = "Etudiant de Villetaneuse";
        } else {
            rang = "Sous-Fifre de Villetaneuse";
        }

        System.out.println("Titre decroche : " + rang);
        sauvegarderScore(this.playerName, "Contre-la-montre", scoreTotal, 60);
    }

    private void sauvegarderScore(String nom, String mode, int score, int temps) {
        try {
            FileWriter writer = new FileWriter("scores.txt", true);
            writer.write(nom + " | Mode: " + mode + " | Score: " + score + " | Temps: " + temps + "s\n");
            writer.close();
        } catch (IOException e) {
            System.out.println("Erreur : impossible de sauvegarder le score.");
        }
    }

    private boolean isAttemptValid(Word w) {
        if (!w.hasExactLength()) {
            System.out.println("Erreur : le mot doit contenir exactement 5 lettres.\n");
            return false;
        }
        if (!w.hasOnlyLetters()) {
            System.out.println("Erreur : le mot doit contenir uniquement des lettres.\n");
            return false;
        }
        if (!w.hasNoRepeat()) {
            System.out.println("Erreur : le mot ne doit pas contenir deux fois la meme lettre =(.\n");
            return false;
        }
        return true;
    }

    Status[] analyzeAttempt(Word w) {
        Status[] resultat = new Status[WORD_LENGTH];
        for (int i = 0; i < WORD_LENGTH; i++) {
            char c = w.charAt(i);
            if (c == secretWord.charAt(i)) {
                resultat[i] = Status.OK;
            } else if (secretWord.contains(c)) {
                resultat[i] = Status.PRESENT;
            } else {
                resultat[i] = Status.ABSENT;
            }
        }
        return resultat;
    }

    private void displayGrid(boolean modeAveugle) {
        int debut = 0;
        if (modeAveugle && attempts > 0) {
            debut = attempts - 1;
        }

        for (int i = debut; i < attempts; i++) {
            for (int j = 0; j < WORD_LENGTH; j++) {
                System.out.print("[ " + Character.toUpperCase(historyWords[i].charAt(j)) + " ]");
            }
            System.out.print(" -> ");
            for (int j = 0; j < WORD_LENGTH; j++) {
                System.out.printf("%-9s", historyStatus[i][j]);
            }
            System.out.println();
        }
        System.out.println("\n-------------");
    }

    private boolean isVictory(Status[] resultat) {
        for (int i = 0; i < resultat.length; i++) {
            if (resultat[i] != Status.OK) {
                return false;
            }
        }
        return true;
    }
}
