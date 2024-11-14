package MenuGastro;
import java.util.Scanner;
import java.util.ArrayList;

public class Backtracking {
    // Graphe des contraintes entre les variables du menu
    private Graph constraintsGraph;
    int assignmentCount = 0;
    // Domaines possibles pour chaque variable (entrée, plat, boisson, dessert)
    private ST<String, ArrayList<String>> domains;

    // Affectation des valeurs aux variables
    private ST<String, String> assignment;

    public Backtracking() {
        // Initialisation des structures nécessaires
        constraintsGraph = new Graph();
        domains = new ST<>();
        assignment = new ST<>();

        // Initialisation des domaines de chaque variable
        initializeDomains();

        // Définition des contraintes de compatibilité
        defineConstraints();
    }

    // Initialise les domaines de valeurs possibles pour chaque variable
    private void initializeDomains() {
        ArrayList<String> entrees = new ArrayList<>();
        entrees.add("houmous avec légumes croquants");
        entrees.add("salade caprese avec pesto");
        entrees.add("mini quiches aux épinards et bruschetta aux tomates et persil");

        ArrayList<String> plats = new ArrayList<>();
        plats.add("curry de légumes aux noix de cajou");
        plats.add("pâtes primavera avec légumes de saison");
        plats.add("steak de chou-fleur grillé");
        plats.add("risotto crémeux aux champignons");

        ArrayList<String> boissons = new ArrayList<>();
        boissons.add("eau gazeuse aromatisée");
        boissons.add("limonade maison au citron vert");
        boissons.add("thé glacé à la menthe");
        boissons.add("smoothie tropical");

        ArrayList<String> desserts = new ArrayList<>();
        desserts.add("mousse au chocolat noir");
        desserts.add("tarte aux fruits de saison");
        desserts.add("panna cotta à la vanille");
        desserts.add("sorbet à la mangue");

        // Ajout des domaines de chaque variable (entrée, plat, boisson, dessert)
        domains.put("entrée", entrees);
        domains.put("plat", plats);
        domains.put("boisson", boissons);
        domains.put("dessert", desserts);
    }

    // Définit les contraintes de compatibilité entre les variables
    private void defineConstraints() {
        constraintsGraph.addEdge("entrée", "plat");
        constraintsGraph.addEdge("boisson", "dessert");
        constraintsGraph.addEdge("entrée", "dessert");
        constraintsGraph.addEdge("plat", "boisson");
    }

    // Vérifie si l'affectation actuelle respecte les contraintes de compatibilité
    private boolean isConsistent(String var, String value) {
        switch (var) {
            case "entrée":
                if (value.equals("houmous avec légumes croquants")) {
                    String plat = assignment.get("plat");
                    return plat == null || plat.equals("curry de légumes aux noix de cajou") || plat.equals("pâtes primavera avec légumes de saison");
                }

                if (value.equals("mini quiches aux épinards et bruschetta aux tomates et persil")) {
                    String plat = assignment.get("plat");
                    return plat == null || !plat.equals("steak de chou-fleur grillé");
                }
                break;

            case "boisson":
                if (value.equals("limonade maison au citron vert")) {
                    String dessert = assignment.get("dessert");
                    return dessert == null || dessert.equals("tarte aux fruits de saison") || dessert.equals("panna cotta à la vanille");
                }
                break;

            case "plat":
                if (value.equals("risotto crémeux aux champignons")) {
                    String boisson = assignment.get("boisson");
                    String dessert = assignment.get("dessert");
                    return (boisson == null || boisson.equals("thé glacé à la menthe")) &&
                            (dessert == null || dessert.equals("sorbet à la mangue"));
                }
                break;

            case "dessert":
                if (value.equals("mousse au chocolat noir")) {
                    String entree = assignment.get("entrée");
                    return entree == null || entree.equals("houmous avec légumes croquants") || entree.equals("salade caprese avec pesto");
                }
                break;
        }
        return true;
    }

    //---------- Sélectionne la variable non assignée ayant le plus petit domaine de valeurs restantes (heuristique MRV) Minimum Remaining Values -----------
    private String selectUnassignedVariable() {
        String minVar = null;
        int minSize = Integer.MAX_VALUE;

        // Pour chaque variable, on vérifie si elle est déjà affectée
        for (String var : domains) {
            if (!assignment.contains(var)) {
                // Taille du domaine de la variable actuelle
                int domainSize = domains.get(var).size();

                // Mise à jour de la variable si le domaine est plus petit
                if (domainSize < minSize) {
                    minSize = domainSize;
                    minVar = var;
                }
            }
        }
        return minVar;
    }

    // Fonction principale de backtracking pour trouver une solution au problème
    // Fonction principale de backtracking pour trouver une solution au problème
    private boolean backtrack() {
        // Si toutes les variables sont affectées, la solution est complète
        if (assignment.size() == domains.size()) {
            return true;
        }

        // Sélectionne une variable non assignée en utilisant l'heuristique MRV
        String var = selectUnassignedVariable();

        // Essaie chaque valeur du domaine de la variable sélectionnée
        for (String value : domains.get(var)) {
            // Vérifie si l'affectation est cohérente avec les contraintes
            if (isConsistent(var, value)) {
                // Affectation temporaire
                assignment.put(var, value);
                assignmentCount++;  // Incrémentation du compteur

                // Appel récursif pour continuer l'affectation
                if (backtrack()) {
                    return true;
                }

                // Retrait de l'affectation si elle ne mène pas à une solution
                assignment.remove(var);
            }
        }
        return false;
    }

    // Résout le problème en utilisant des affectations prédéfinies et le backtracking
    public ST<String, String> solve(ST<String, String> predefinedAssignment) {
        // Affectations prédéfinies
        for (String key : predefinedAssignment) {
            assignment.put(key, predefinedAssignment.get(key));
        }

        // Lancement de l'algorithme de backtracking
        if (backtrack()) {
            return assignment;
        } else {
            System.out.println("Aucun menu valide trouvé");
            return null;
        }
    }

    public static void main(String[] args) {
        Backtracking solver = new Backtracking();

        // Scanner pour lire les entrées utilisateur
        Scanner scanner = new Scanner(System.in);

        // Choix de test à réaliser
        System.out.println("Choisissez un test de menu à effectuer :");
        System.out.println("--- Appuyez sur 1 pour : première configuration (mini quiches et limonade) ---");
        System.out.println("--- Appuyez sur 2 pour : deuxième configuration (bruschetta et limonade) ---");
        System.out.println("--- Appuyez sur 3 pour : troisième configuration (houmous et thé glacé) ---");
        System.out.print("Votre choix : ");
        int choix = scanner.nextInt();

        // Affectation prédéfinie basée sur le choix de l'utilisateur
        ST<String, String> predefinedAssignment = new ST<>();

        switch (choix) {
            case 1:
                predefinedAssignment.put("entrée", "mini quiches aux épinards et bruschetta aux tomates et persil");
                predefinedAssignment.put("boisson", "limonade maison au citron vert");
                break;
            case 2:
                predefinedAssignment.put("entrée", "bruschetta");
                predefinedAssignment.put("boisson", "limonade maison");
                break;
            case 3:
                predefinedAssignment.put("entrée", "houmous");
                predefinedAssignment.put("boisson", "thé glacé à la menthe");
                break;
            default:
                System.out.println("Choix invalide, test par défaut sera effectué.");
                predefinedAssignment.put("entrée", "mini quiches aux épinards et bruschetta aux tomates et persil");
                predefinedAssignment.put("boisson", "limonade maison au citron vert");
        }

        // Temps avant le début de la recherche
        long startTime = System.currentTimeMillis();
        System.out.println("Heure de début : " + startTime + " ms");

        // Recherche de la solution
        ST<String, String> solution = solver.solve(predefinedAssignment);

        // Temps après la recherche
        long endTime = System.currentTimeMillis();
        System.out.println("Heure de fin : " + endTime + " ms");

        // Calcul et affichage de la durée d'exécution
        long duration = endTime - startTime;

        // Affichage de la solution
        if (solution != null) {
            System.out.println("------------- Menu valide : ---------------");
            for (String key : solution) {
                System.out.println(key + " = " + solution.get(key));
            }
        } else {
            System.out.println("Aucun menu valide trouvé.");
        }

        System.out.println("\nDurée d'exécution : " + duration + " ms");
        System.out.println("Nombre d'affectations effectuées (MRV) : " + solver.assignmentCount);  // Affichage du compteur
        System.out.println(" ");
        scanner.close();
    }
}
