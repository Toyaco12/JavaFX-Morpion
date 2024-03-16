package com.project.morpion.model.ai;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.scene.control.ProgressBar;

import java.io.*;
import java.util.Arrays;
//
import java.util.HashMap;
//


public class Test {

    public static void main(String[] args) {
        try {
            //
            // LOAD DATA ...
            //
            HashMap<Integer, Coup> coups = loadGames("src/main/resources/com/project/morpion/ai/dataset/Tic_tac_initial_results.csv");
            saveGames(coups, "src/main/resources/com/project/morpion/ai/train_dev_test/", 0.7);
            //
            // LOAD CONFIG ...
            //
            ConfigFileLoader cfl = new ConfigFileLoader();
            cfl.loadConfigFile("src/main/resources/com/project/morpion/ai/config.txt");
            Config config = cfl.get("F");
            System.out.println("Test.main() : "+config);
            //
            //TRAIN THE MODEL ...
            //
            double epochs = 10000 ;
            HashMap<Integer, Coup> mapTrain = loadCoupsFromFile("src/main/resources/com/project/morpion/ai/train_dev_test/train.txt");
            MultiLayerPerceptron net = learn(9, mapTrain, config.hiddenLayerSize, config.learningRate, config.numberOfhiddenLayers, true, epochs);
            //
            //PLAY ...
            //
            HashMap<Integer, Coup> mapDev = loadCoupsFromFile("src/main/resources/com/project/morpion/ai/train_dev_test/dev.txt");
            Coup c = mapTrain.get((int)(Math.round(Math.random() * mapDev.size())));
            double[] res = play(net, c);
            System.out.println("Dev predicted: "+Arrays.toString(res) + " -> true: "+ Arrays.toString(c.out));
            //
            HashMap<Integer, Coup> mapTest = loadCoupsFromFile("src/main/resources/com/project/morpion/ai/train_dev_test/test.txt");
            c = mapTrain.get((int)(Math.round(Math.random() * mapTest.size())));
            System.out.println(c);
            res = play(net, c);
            System.out.println("Test predicted: "+Arrays.toString(res) + " -> true: "+ Arrays.toString(c.out));
// Initialisation du plateau de jeu
            Coup coupActuel = new Coup(9, "Morpion");

            int joueurActuel = Coup.X;  // Commence avec le joueur X

// Boucle de jeu (par exemple, pour un maximum de 9 coups)
            for (int i = 0; i < 9; i++) {
                System.out.println("Tour " + (i + 1) + " - Joueur " + (joueurActuel == Coup.X ? "X" : "O"));

                // Simulation de la prédiction de l'IA pour le coup
                double[] resul = play(net, coupActuel);

                // Choix du meilleur coup basé sur la prédiction
                int bestMove = -1;
                double maxPrediction = -Double.MAX_VALUE;

                for (int j = 0; j < resul.length; j++) {
                    if (resul[j] > maxPrediction && coupActuel.cellAvailable(j)) {
                        maxPrediction = resul[j];
                        bestMove = j;
                    }
                }

                // Mise à jour du plateau avec le meilleur coup
                if (bestMove != -1) {
                    coupActuel.in[bestMove] = joueurActuel;
                } else {
                    System.out.println("Aucun mouvement sélectionné par l'IA.");
                    break;
                }

                // Affichage de l'état du plateau après le coup
                System.out.println("État du plateau : " + coupActuel);

                // Vérification de la fin du jeu (victoire ou nul) ici...
                // Si le jeu est terminé, sortez de la boucle

                // Changement de joueur
                joueurActuel = coupActuel.getNextTurnPiece(joueurActuel);
            }

            System.out.println("Fin du jeu.");




        }
        catch (Exception e) {
            System.out.println("Test.main()");
            e.printStackTrace();
            System.exit(-1);
        }
    }


    ///////////

    public static MultiLayerPerceptron learn(int size, HashMap<Integer, Coup> mapTrain, int h, double lr, int l, boolean verbose, double epochs){
        try {
            if ( verbose ) {
                System.out.println();
                System.out.println("START TRAINING ...");
                System.out.println();
            }
            //
            //			int[] layers = new int[]{ size, 128, 128, size };
            int[] layers = new int[l+2];
            layers[0] = size ;
            for (int i = 0; i < l; i++) {
                layers[i+1] = h ;
            }
            layers[layers.length-1] = size ;
            //
            double error = 0.0 ;
            MultiLayerPerceptron net = new MultiLayerPerceptron(layers, lr, new SigmoidalTransferFunction());

            if ( verbose ) {
                System.out.println("---");
                System.out.println("Load data ...");
                System.out.println("---");
            }
            //TRAINING ...
            for(int i = 0; i < epochs; i++){

                Coup c = null ;
                while ( c == null )
                    c = mapTrain.get((int)(Math.round(Math.random() * mapTrain.size())));

                error += net.backPropagate(c.in, c.out);

                if(i%100 == 0 ){
                    System.out.println(i/100);
                }
                if ( i % 10000 == 0 && verbose) System.out.println("Error at step "+i+" is "+ (error/(double)i));
            }
            if ( verbose )
                System.out.println("Learning completed!");

            return net ;
        }
        catch (Exception e) {
            System.out.println("Test.learn()");
            e.printStackTrace();
            System.exit(-1);
        }

        return null ;
    }

    public static double[] play(MultiLayerPerceptron net, Coup c){
        try {
            double[] res = net.forwardPropagation(c.in);
            return res ;
        }
        catch (Exception e) {
            System.out.println("Test.play()");
            e.printStackTrace();
            System.exit(-1);
        }

        return null ;
    }

    ///////////
    public static void saveGames(HashMap<Integer, Coup> coups, String rep, double trainRate) {
        try {
            System.out.println("saveGames ...");
            PrintWriter pwTrain = new PrintWriter(new File(rep+"/train.txt"));
            PrintWriter pwDev = new PrintWriter(new File(rep+"/dev.txt"));
            PrintWriter pwTest = new PrintWriter(new File(rep+"/test.txt"));

            int nbTrain = (int)(coups.size() * trainRate) ;
            int nbDev = (int)((coups.size()-nbTrain)/2.0) ;

            for ( int  pos : coups.keySet() ) {
                double[] in = coups.get(pos).in;
                double[] out = coups.get(pos).out;

                String sIn = "" ;
                for (int i = 0; i < in.length; i++) {
                    sIn += in[i]+" ";
                }
                //
                String sOut = "" ;
                for (int i = 0; i < out.length; i++) {
                    sOut += out[i]+" ";
                }

                if ( pos <= nbTrain ) {
                    pwTrain.write(sIn.trim()+"\t"+sOut.trim()+"\n");
                }
                else if ( pos <= (nbTrain + nbDev) ) {
                    pwDev.write(sIn.trim()+"\t"+sOut.trim()+"\n");
                }
                else
                    pwTest.write(sIn.trim()+"\t"+sOut.trim()+"\n");
            }

            pwTrain.close();
            pwDev.close();
            pwTest.close();
        }
        catch (Exception e) {
            System.out.println("Test.saveGames()");
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public static HashMap<Integer, double[]> getGameSequence(String x, String o, int size){
        HashMap<Integer, double[]> sequence = new HashMap<>();
        double[] board = new double[size];
        sequence.put(0, board);

        x = x.replace(",win", "").replace(",loss", "");
        o = o.replace(",win", "").replace(",loss", "");

        String[] tabX = x.split(",");
        String[] tabO = o.split(",");

        int len = tabX.length;
        if ( tabO.length > tabX.length )
            len = tabO.length ;

        for (int i = 0; i < len; i ++ ) {

            //			System.out.println("---");
            //			System.out.println("\ti: "+i);
            if ( tabX.length > i ) {
                board = new double[size];
                int c = new Integer(tabX[i]);
                //				System.out.println("c: "+c);
                board[c] = Coup.X ;
                sequence.put(sequence.size(), board);
            }
            //
            if ( tabO.length > i ) {
                board = new double[size];
                int c = new Integer(tabO[i]);
                board[c] = Coup.O ;
                sequence.put(sequence.size(), board);
                //				System.out.println("c: "+c);
            }

        }

        //System.out.println("sequence: "+Arrays.asList(sequence));
        return sequence ;
    }

    public static HashMap<Integer, Coup> loadGames(String fileName) {
        System.out.println("loadGames from "+fileName+ " ...");
        HashMap<Integer, Coup> map = new HashMap<>();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(fileName))));
            String s = "";
            br.readLine();
            while ((s = br.readLine()) != null) {
                if ( ! s.endsWith("draw") ) {
                    //
                    String playerX = s.replace(",?", "");
                    String playerO = br.readLine().replace(",?", "");
                    //0,8,1,3,?,?,?,loss
                    //4,7,2,6,?,?,?,win
                    //
                    HashMap<Integer, double[]> sequenceMoves = getGameSequence(playerX, playerO, 9);
                    //
                    int startEmptyBoard = 0 ;
                    if ( playerO.endsWith("win") ) {
                        startEmptyBoard = 1 ;
                    }
                    boolean in = true ;
                    double[] currentBoard = new double[9];
                    //
                    for (int pos = startEmptyBoard; pos < sequenceMoves.size(); pos ++ ) {
                        double[] board = sequenceMoves.get(pos);
                        if ( ! in ) {
                            Coup c = new Coup(9, playerX+" "+playerO);
                            c.in = currentBoard.clone() ;
                            c.out = board ;
                            map.put(map.size(), c);
                        }
                        in = !in ;
                        for (int i = 0; i < board.length; i++) {
                            if ( currentBoard[i] == 0.0 )
                                currentBoard[i] = board[i];
                        }
                    }
                }
            }
            br.close();
        }
        catch (Exception e) {
            System.out.println("Test.loadGames()");
            e.printStackTrace();
            System.exit(-1);
        }
        return map ;
    }

    public static HashMap<Integer, Coup> loadCoupsFromFile(String file){
        System.out.println("loadCoupsFromFile from "+file+" ...");
        HashMap<Integer, Coup> map = new HashMap<>();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(file))));
            String s = "";
            while ((s = br.readLine()) != null) {
                String[] sIn = s.split("\t")[0].split(" ");
                String[] sOut = s.split("\t")[1].split(" ");

                double[] in = new double[sIn.length];
                double[] out = new double[sOut.length];

                for (int i = 0; i < sIn.length; i++) {
                    in[i] = Double.parseDouble(sIn[i]);
                }

                for (int i = 0; i < sOut.length; i++) {
                    out[i] = Double.parseDouble(sOut[i]);
                }

                Coup c = new Coup(9, "");
                c.in = in ;
                c.out = out ;
                map.put(map.size(), c);
            }
            br.close();
        }
        catch (Exception e) {
            System.out.println("Test.loadCoupsFromFile()");
            e.printStackTrace();
            System.exit(-1);
        }
        return map ;
    }

    public static HashMap<Integer, Coup> setup(){
        try {
            //
            // LOAD DATA ...
            //
            HashMap<Integer, Coup> coups = loadGames("src/main/resources/com/project/morpion/ai/dataset/Tic_tac_initial_results.csv");
            saveGames(coups, "src/main/resources/com/project/morpion/ai/train_dev_test/", 0.7);
            HashMap<Integer, Coup> mapTrain = loadCoupsFromFile("src/main/resources/com/project/morpion/ai/train_dev_test/train.txt");
            return mapTrain;
        }
        catch (Exception e) {
            System.out.println("Test.main()");
            e.printStackTrace();
            System.exit(-1);
        }
        return null;
    }
    ///////////

    public static void test(int size, int h, double lr, int l){
        try {
            System.out.println();
            System.out.println("START TRAINING ...");
            System.out.println();
            //
            //			int[] layers = new int[]{ size, 128, 128, size };
            int[] layers = new int[l+2];
            layers[0] = size ;
            for (int i = 0; i < l; i++) {
                layers[i+1] = h ;
            }
            layers[layers.length-1] = size ;
            //
            double error = 0.0 ;
            MultiLayerPerceptron net = new MultiLayerPerceptron(layers, lr, new SigmoidalTransferFunction());
            double epochs = 1000000000 ;

            System.out.println("---");
            System.out.println("Load data ...");
            HashMap<Integer, Coup> mapTrain = loadCoupsFromFile("train_dev_test/train.txt");
            HashMap<Integer, Coup> mapDev = loadCoupsFromFile("train_dev_test/dev.txt");
            HashMap<Integer, Coup> mapTest = loadCoupsFromFile("train_dev_test/test.txt");
            System.out.println("---");
            //TRAINING ...
            for(int i = 0; i < epochs; i++){

                Coup c = null ;
                while ( c == null )
                    c = mapTrain.get((int)(Math.round(Math.random() * mapTrain.size())));

                error += net.backPropagate(c.in, c.out);

                if ( i % 10000 == 0 ) System.out.println("Error at step "+i+" is "+ (error/(double)i));
            }

            System.out.println("Learning completed!");
        }
        catch (Exception e) {
            System.out.println("Test.test()");
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public static void test(){
        try {
            System.out.println();
            System.out.println("START TRAINING ...");
            System.out.println();
            int[] layers = new int[]{ 2, 5, 1 };

            double error = 0.0 ;
            MultiLayerPerceptron net = new MultiLayerPerceptron(layers, 0.1, new SigmoidalTransferFunction());
            double samples = 1000000000 ;

            //TRAINING ...
            for(int i = 0; i < samples; i++){
                double[] inputs = new double[]{Math.round(Math.random()), Math.round(Math.random())};
                double[] output = new double[1];

                if((inputs[0] == 1.0) || (inputs[1] == 1.0))
                    output[0] = 1.0;
                else
                    output[0] = 0.0;



                error += net.backPropagate(inputs, output);

                if ( i % 100000 == 0 ) System.out.println("Error at step "+i+" is "+ (error/(double)i));
            }
            error /= samples ;
            System.out.println("Error is "+error);
            //
            System.out.println("Learning completed!");

            //TEST ...
            double[] inputs = new double[]{0.0, 1.0};
            double[] output = net.forwardPropagation(inputs);

            System.out.println(inputs[0]+" or "+inputs[1]+" = "+Math.round(output[0])+" ("+output[0]+")");
        }
        catch (Exception e) {
            System.out.println("Test.test()");
            e.printStackTrace();
            System.exit(-1);
        }
    }
}