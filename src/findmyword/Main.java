package findmyword;

import findmyword.game.Game;
import findmyword.repository.FixedWordRepository;
import findmyword.repository.JsonWordRepository;
import findmyword.repository.WordRepository;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("       LANCEMENT DE FIND MY WORD      ");
        System.out.println("");
        System.out.println("1. Lancer le jeu normalement (mots aleatoires)");
        System.out.println("2. Lancer le mode TEST (mot fixe 'ligne')");
        System.out.print("Choix du lancement (1 ou 2) : ");

        String choix = scanner.nextLine().trim();

        WordRepository repository;
        if (choix.equals("2")) {
            System.out.println("\nMode TEST : le mot secret est 'ligne'.\n");
            repository = new FixedWordRepository("ligne");
        } else {
            System.out.println("\nMode normal.\n");
            repository = new JsonWordRepository("resources/words.json");
        }

        Game game = new Game(repository, scanner);
        game.start();
    }
}
