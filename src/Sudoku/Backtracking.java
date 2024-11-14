package Sudoku;

import java.util.*;

public class Backtracking {

    // Variable pour stocker le choix de l'algorithme AC
    private static int acChoice;

    public static void setAcChoice(int choice) {
        acChoice = choice;
    }

    public static String getVariable(ST<String, String> config) {
        // Retrieve a variable based on a heuristic or the next 'unfilled' one if there is no heuristic
        for (String s : config) {
            if(config.get(s).equalsIgnoreCase(""))
                return s;
        }
        // Get variable failed (all variables have been coloured)
        return null;
    }

    public static SET<String> orderDomainValue(String variable, ST<String, SET<String>> domain) {
        // Return the SET of domain values for the variable
        return domain.get(variable);
    }

    public static boolean complete(ST<String, String> config) {
        //if we find a variable in the config with no value, then this means that the config is NOT complete
        for (String s : config) {
            if(config.get(s).equalsIgnoreCase(""))
                return false;
        }
        //ALL variables in config have a value, so the configuration is complete
        return true;
    }

    public static boolean consistent(String value, String variable, ST<String, String> config, Graph g) {
        //we need to get the adjacency list for the variable
        for (String adj : g.adjacentTo(variable)) {
            //if the adjacency list member's value is equal to the variable's selected value, then consistency fails
            if (config.get(adj) != null && config.get(adj).equalsIgnoreCase(value)) {
                //consistency check failed
                return false;
            }
        }
        //consistency check passed according to the variable's adjacancy list
        return true;
    }
//*************************************  AC1   ******************************************************
    public static boolean ac1(ST<String, SET<String>> domain, Graph g) {
        boolean changed = true;
        while (changed) {
            changed = false;
            for (String xi : domain) {
                for (String xj : g.adjacentTo(xi)) {
                    if (revise(domain, xi, xj)) {
                        // If any revision happened, set changed to true
                        changed = true;
                    }
                }
            }
        }
        return true;// All arcs consistent
    }

//*************************************  AC3   ******************************************************
    public static boolean ac3(ST<String, SET<String>> domain, Graph g) {
        SET<String> arcs = new SET<>();

        // Enqueue all arcs (variables) in the graph
        for (String v : domain) {
            for (String w : g.adjacentTo(v)) {
                arcs.add(v + "," + w);
            }
        }
        // Process the set of arcs
        while (arcs.size() > 0) {
            String arc = arcs.iterator().next();// Get an arbitrary arc
            arcs.remove(arc); //Remove it from the set
            String[] arcParts = arc.split(",");
            String xi = arcParts[0];
            String xj = arcParts[1];

            if (revise(domain, xi, xj)) {
                if (domain.get(xi).size() == 0) {
                    return false;// No valid values left
                }
                for (String xk : g.adjacentTo(xi)) {
                    if (!xk.equals(xj)) {
                        arcs.add(xk + "," + xi);// Add new arcs to the set
                    }
                }
            }
        }
        return true;
    }

//*************************************  AC4   ******************************************************
    public static boolean ac4(ST<String, SET<String>> domain, Graph g) {
        // Initialize support counts
        int[] supportCount = new int[domain.size()];
        ST<String, SET<String>> valueSupports = new ST<>();

        // Initialize value supports
        for (String v : domain) {
            for (String value : domain.get(v)) {
                if (!valueSupports.contains(value)) {
                    valueSupports.put(value, new SET<>());
                }
                valueSupports.get(value).add(v);
            }
        }

        // Iterate through the domains and check support for values
        for (String xi : domain) {
            for (String xj : g.adjacentTo(xi)) {
                SET<String> valuesXi = domain.get(xi);
                SET<String> valuesToRemove = new SET<>();

                for (String valueXi : valuesXi) {
                    boolean hasSupport = false;

                    for (String valueXj : domain.get(xj)) {
                        if (!valueXi.equals(valueXj)) {
                            hasSupport = true;
                            break;
                        }
                    }

                    if (!hasSupport) {
                        valuesToRemove.add(valueXi);
                    }
                }

                // Remove values after iteration to avoid concurrent modification
                for (String valueToRemove : valuesToRemove) {
                    domain.get(xi).remove(valueToRemove);
                    supportCount[domain.get(xi).size()]--;
                }
            }
        }

        return true; // All arcs consistent
    }


    private static boolean revise(ST<String, SET<String>> domain, String xi, String xj) {
        boolean revised = false;
        SET<String> valuesXi = domain.get(xi);
        SET<String> valuesToRemove = new SET<>();

        for (String value : valuesXi) {
            boolean satisfies = false;
            for (String valueJ : domain.get(xj)) {
                if (!value.equals(valueJ)) {
                    satisfies = true;// Found a value that satisfies the constraint
                    break;
                }
            }
            if (!satisfies) {
                valuesToRemove.add(value);//Mark the value for removal
                revised = true;
            }
        }
        // Remove the values that do not satisfy
        for (String value : valuesToRemove) {
            valuesXi.remove(value);
        }
        return revised;
    }

    //*****************************************************************************************************
    public static ST<String, String> backtracking(ST<String, String> config, ST<String, SET<String>> domain, Graph g) {
        boolean isConsistent;
        // Dynamic call of AC algorithm based on user choice
        switch (acChoice) {
            case 1:
                isConsistent = ac1(domain, g);
                break;
            case 3:
                isConsistent = ac3(domain, g);
                break;
            case 4:
                isConsistent = ac4(domain, g);
                break;
            default:
                System.out.println("Choix invalid d'algorithme !!!");
                return null;
        }

        if (!isConsistent) {
            return null;
        }

        if (complete(config)) {
            return config;
        }

        ST<String, String> result = null;
        String v = getVariable(config);
        SET<String> vu = orderDomainValue(v, domain);

        for (String u : vu) {
            if (consistent(u, v, config, g)) {
                config.put(v, u);
                result = backtracking(config, domain, g);
                if (result != null) {
                    return result;
                }
                config.put(v, "");
            }
        }
        return null;
    }

    public static void main(String[] args) {
        Graph G = new Graph();

        // Grille de Sudoku initiale
        String grille [][]={

                { "", "" ,  "", "", "", "", "7", "2", ""},
                { "", "1" , "3", "",  "" , "7" , "" , "4" , ""},
                { "2", "" ,  "", "", "5", "", "", "", "8"},
                { "" , "" , "", "", "" , "6" , "4" , "7" , ""},
                { "7", "8" ,  "", "4", "", "1", "", "3", "6"},
                { "" , "9", "4",  "3", "", "" , "" , "" , ""},
                { "3", "",  "", "", "6", "", "", "", "4"},
                { "" , "7" , "",  "5", "" , "" , "8", "1" , ""},
                { "", "5", "8", "", "", "", "", "", "" }
        };

        // Affichage de la grille initiale
        System.out.println("----------Initial Grid ------------");
        for(int i=0; i<9; i++) {
            System.out.println();
            for(int j=0; j<9; j++) {
                if(!grille[i][j].isEmpty())
                    System.out.print(grille[i][j]);
                else
                    System.out.print(".");
            }
        }

        // Contraintes au niveau des lignes
        for(int i=1; i<=9; i++) {
            for(int j=1; j<=8; j++) {
                for(int k=j+1; k<=9; k++) {
                    String var1 = "x" + i + j;
                    String var2 = "x" + i + k;
                    G.addEdge(var1, var2);
                }
            }
        }

        // Contraintes au niveau des colonnes
        for(int i=1; i<=9; i++) {
            for(int j=1; j<=8; j++) {
                for(int k=j+1; k<=9; k++) {
                    String var1 = "x" + j + i;
                    String var2 = "x" + k + i;
                    G.addEdge(var1, var2);
                }
            }
        }

        // Contraintes au niveau des sous-grilles
        for(int ii=1; ii<=3; ii++) {
            for(int jj=1; jj<=3; jj++) {
                int toplefti = (ii-1) * 3;
                int topleftj = (jj-1) * 3;
                for(int i=1; i<=3; i++) {
                    for(int j=1; j<=3; j++) {
                        for(int k=1; k<=3; k++) {
                            for(int l=1; l<=3; l++) {
                                if(k != i || l != j) {
                                    String var1 = "x" + (toplefti + i) + (topleftj + j);
                                    String var2 = "x" + (toplefti + k) + (topleftj + l);
                                    G.addEdge(var1, var2);
                                }
                            }
                        }
                    }
                }
            }
        }

        // Table des domaines
        ST<String, SET<String>> domainTable = new ST<>();

        // Initialisation des domaines pour chaque case
        Object[][] domains = new Object[9][9];
        for(int i=0; i<9; i++) {
            for(int j=0; j<9; j++) {
                domains[i][j] = new SET<String>();
            }
        }

        for(int i=0; i<9; i++) {
            for(int j=0; j<9; j++) {
                if(!grille[i][j].isEmpty()) {
                    ((SET<String>)domains[i][j]).add(grille[i][j]);
                } else {
                    for(int k=1; k<=9; k++) {
                        ((SET<String>)domains[i][j]).add("" + k);
                    }
                }
            }
        }

        // Ajouter les domaines à la table
        for(int i=1; i<=9; i++) {
            for(int j=1; j<=9; j++) {
                domainTable.put("x" + i + j, ((SET<String>)domains[i-1][j-1]));
            }
        }

        // Configuration initiale
        ST<String, String> config = new ST<>();
        for(int i=1; i<=9; i++) {
            for(int j=1; j<=9; j++) {
                config.put("x" + i + j, "");
            }
        }

        // Choix de l'algorithme de consistance d'arc
        Scanner scr = new Scanner(System.in);
        System.out.println("\n- - - Bienvenue dans le Solveur de Sudoku - - -");
        System.out.println("\nChoisissez un algorithme de consistance d'arc à appliquer:");
        System.out.println("*)---Tapez 1 pour AC-1---");
        System.out.println("*)---Tapez 3 pour AC-3---");
        System.out.println("*)---Tapez 4 pour AC-4---");
        System.out.println("*)---Tapez 0 pour quitter----");

        System.out.println("Votre choix: ");
        int n = scr.nextInt();

        if (n == 0) {
            System.out.println("Fin du programme.");
            return;
        }else if (n==1){
            System.out.printf("Appliquons AC-1 ......");
        }else if (n==3){
            System.out.printf("Appliquons AC-3 ......");
        }else if (n==4) {
            System.out.printf("Appliquons AC-4 ......");
        }else {
            System.out.println("Choix invalid d'algorithme !!!");
            return;
        }

        setAcChoice(n);

        // Calcul de la solution
        System.out.println("\nCalculating...");
        long startTime = System.nanoTime();
        ST<String, String> solution = backtracking(config, domainTable, G);        long endTime = System.nanoTime();
        long durationInMilliseconds = (endTime - startTime) / 1_000_000;
        System.out.println("Execution time: " + durationInMilliseconds + " milliseconds");

        // Affichage de la solution
        if (solution == null) {
            System.out.println("No solution !!");
        } else {
            System.out.println("Solution found:");
            // Afficher la solution ici
            for (int i = 1; i <= 9; i++) { // Row
                System.out.println();
                for (int j = 1; j <= 9; j++) { // Column
                    String value = solution.get("x" + i + j);
                    if (value == null || value.isEmpty()) {
                        System.out.print(". ");
                    } else {
                        System.out.print(value + " ");
                    }
                }
            }
            System.out.println(); // Pour ajouter une ligne vide après la solution
        }

        scr.close();
    }
}
