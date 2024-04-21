package com.project.morpion.model;

import com.project.morpion.tools.ai.Coup;
import com.project.morpion.tools.ai.MultiLayerPerceptron;

import java.util.Scanner;

public class Morpion {
    private double[] board;
    private int currentPlayer;
    private MultiLayerPerceptron model;
    private int successWinPlayer = 0;
    private int successWinBot = 0;

    // Constructeur pour initialiser un jeu de Morpion avec un modèle d'IA et un joueur initial.

    public Morpion(MultiLayerPerceptron model,int startingPlayer) {
        this.board = new double[9];
        this.currentPlayer = startingPlayer;
        this.model = model;
    }

    // Constructeur pour initialiser un jeu de Morpion sans modèle d'IA avec un joueur initial.

    public Morpion(int startingPlayer) {
        this.board = new double[9];
        this.currentPlayer = startingPlayer;
        this.model = null;
    }

    // Effectue un coup dans le jeu à la position indiquée, met à jour le plateau et change de joueur si le coup est valide.

    public boolean play(int place) {
        // Check if the position is possible
        if (place < 0 || place >= board.length || board[place] != Coup.EMPTY) {
            return false;
        }

        board[place] = currentPlayer;
        if (isWin() || isDraw()) {
            return true;
        }
        currentPlayer = -currentPlayer;
        return true;
    }

    // Vérifie si le joueur courant a gagné la partie en examinant toutes les lignes, colonnes et diagonales.

    public boolean isWin() {
        for (int i = 0; i < 3; i++) {
            if (board[i * 3] != Coup.EMPTY && board[i * 3] == board[i * 3 + 1] && board[i * 3] == board[i * 3 + 2]) {
                return true;
            }
            if (board[i] != Coup.EMPTY && board[i] == board[i + 3] && board[i] == board[i + 6]) {
                return true;
            }
        }
        if (board[0] != Coup.EMPTY && board[0] == board[4] && board[0] == board[8]) {
            return true;
        }
        if (board[2] != Coup.EMPTY && board[2] == board[4] && board[2] == board[6]) {
            return true;
        }
        return false;
    }

    // Vérifie si toutes les cases du plateau sont remplies sans qu'un joueur ait gagné, indiquant une partie nulle.

    public boolean isDraw() {
        // Check empty cases
        for (double val : board) {
            if (val == Coup.EMPTY) {
                return false;
            }
        }
        return true;
    }

    // Fait jouer l'IA en calculant le meilleur coup possible avec le modèle de perceptron multicouche actuel.

    public void playIA() {
        if (model == null) {
            System.err.println("IA is not set up.");
            return;
        }
        // Conversion de l'état du plateau pour l'IA
        Coup actualBoard = new Coup(board.length,"Morpion");
        actualBoard.addInBoard(board);
        actualBoard.out = model.forwardPropagation(actualBoard.in);
        int bestPlace = 0;
        double maxVal = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < actualBoard.out.length; i++) {
            if (actualBoard.out[i] > maxVal && board[i] == Coup.EMPTY) {
                maxVal = actualBoard.out[i];
                bestPlace = i;
            }
        }
        play(bestPlace);
    }

    // Identique à playIA mais retourne également l'indice du meilleur coup pour l'interface graphique.

    public int playIAGUI() {
        if (model == null) {
            System.err.println("IA is not set up.");
            return -1;
        }
        // Conversion de l'état du plateau pour l'IA
        Coup actualBoard = new Coup(board.length,"Morpion");
        actualBoard.addInBoard(board);
        actualBoard.out = model.forwardPropagation(actualBoard.in);
        int bestPlace = 0;
        double maxVal = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < actualBoard.out.length; i++) {
            if (actualBoard.out[i] > maxVal && board[i] == Coup.EMPTY) {
                maxVal = actualBoard.out[i];
                bestPlace = i;
            }
        }
        play(bestPlace);
        return bestPlace;
    }

    // Démarre une partie en mode console, alternant entre les coups de l'IA et de l'humain jusqu'à la fin de la partie.

    public void startGame() {
        Scanner scanner = new Scanner(System.in);
        while (!isWin() && !isDraw()) {
            System.out.println("Current board:");
            printBoard();
            if (currentPlayer == Coup.X || model == null) {  // human player or human vs human
                System.out.print("Enter your move (0-8): ");
                int move = scanner.nextInt();
                if (!play(move)) {
                    System.out.println("Invalid move. Try again.");
                }
            } else {  // Mode IA
                playIA();
                System.out.println("IA played.");
            }
        }

        printBoard();
        if (isWin()) {
            System.out.println("Player " + (currentPlayer == Coup.X ? "X" : "O") + " wins!");
        } else {
            System.out.println("It's a draw!");
        }
    }

    // Renvoie les indices des cases formant une ligne gagnante, si une victoire a été détectée.

    public int[] getVictory() {
        // Vérifie chaque ligne
        for (int i = 0; i <= 6; i += 3) {
            if (board[i] != 0 && board[i] == board[i + 1] && board[i] == board[i + 2]) {
                return new int[]{i, i + 1, i + 2};
            }
        }

        // Vérifie chaque colonne
        for (int i = 0; i < 3; i++) {
            if (board[i] != 0 && board[i] == board[i + 3] && board[i] == board[i + 6]) {
                return new int[]{i, i + 3, i + 6};
            }
        }

        // Vérifie les diagonales
        if (board[0] != 0 && board[0] == board[4] && board[0] == board[8]) {
            return new int[]{0, 4, 8};
        }
        if (board[2] != 0 && board[2] == board[4] && board[2] == board[6]) {
            return new int[]{2, 4, 6};
        }

        // Si aucune ligne gagnante n'est trouvée, retourne un tableau vide ou null
        return new int[0];
    }

    // Affiche l'état actuel du plateau dans la console, utilisé principalement pour le débogage ou le mode console.

    private void printBoard() {
        for (int i = 0; i < board.length; i++) {
            System.out.print(board[i] == Coup.X ? "X" : board[i] == Coup.O ? "O" : ".");
            if (i % 3 == 2) System.out.println();
        }
    }

    // Vérifie si une case spécifique est disponible pour jouer (non occupée).

    public boolean isAvailable(int n){
        if(board[n] == Coup.EMPTY){
            return true;
        }
        return false;
    }

    // Joue un coup à l'interface graphique, vérifie la fin du jeu avant de permettre le coup.

    public void playGUI(int place){
        if(isWin() || isDraw()){
            return;
        }
        play(place);
    }

    public double[] getBoard(){
        return this.board;
    }

    // Réinitialise le plateau de jeu à son état initial, prêt pour une nouvelle partie.

    public void restart(){
        board = new double[9];
    }

    // Incrémente le compteur de victoires du joueur ou de l'IA, respectivement.

    public void addSuccessWinPlayer(){
        this.successWinPlayer++;
    }
    public void addSuccessWinBot(){
        this.successWinBot++;
    }

    // Getters & Setters

    public void setCurrentPlayer(int player){
        this.currentPlayer = player;
    }
    public int getSuccessWinPlayer(){
        return this.successWinPlayer;
    }
    public int getSuccessWinBot(){
        return this.successWinBot;
    }
    public void setModel(MultiLayerPerceptron model){
        this.model = model;
    }
}


