package com.project.morpion.model;

import com.project.morpion.model.ai.Coup;
import com.project.morpion.model.ai.MultiLayerPerceptron;

import java.util.Scanner;

public class Morpion {
    private double[] board;
    private int currentPlayer;
    private MultiLayerPerceptron model;
    private int successWin = 0;

    public Morpion(MultiLayerPerceptron model,int startingPlayer) {
        this.board = new double[9];
        this.currentPlayer = startingPlayer;
        this.model = model;
    }
    public Morpion(int startingPlayer) {
        this.board = new double[9];
        this.currentPlayer = startingPlayer;
        this.model = null;
    }

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

    public boolean isDraw() {
        // Check empty cases
        for (double val : board) {
            if (val == Coup.EMPTY) {
                return false;
            }
        }
        return true;
    }

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

    private void printBoard() {
        for (int i = 0; i < board.length; i++) {
            System.out.print(board[i] == Coup.X ? "X" : board[i] == Coup.O ? "O" : ".");
            if (i % 3 == 2) System.out.println();
        }
    }

    public boolean isAvailable(int n){
        if(board[n] == Coup.EMPTY){
            return true;
        }
        return false;
    }

    public void playGUI(int place){
        if(isWin() || isDraw()){
            return;
        }
        play(place);
    }
    // Getters et Setters
    public double[] getBoard(){
        return this.board;
    }

    public void restart(){
        board = new double[9];
    }
    public void addSuccessWin(){
        this.successWin++;
    }
    // Getters & Setters
    public void setCurrentPlayer(int player){
        this.currentPlayer = player;
    }
    public int getSuccessWin(){
        return this.successWin;
    }


}


