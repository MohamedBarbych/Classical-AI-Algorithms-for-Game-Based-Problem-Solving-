package Maze;

import javax.swing.*;
import java.awt.*;
import java.awt.image.*;

/**
 * Title:        MazeAStarSearch<p>
 * Description:  Demo program for A* pathfinding in a maze<p>
 * Copyright:    Copyright (c) Mark Watson, Released under Open Source Artistic License<p>
 * Company:      Mark Watson Associates<p>
 * @author Mark Watson
 * @version 1.0
 */

public class MazeAStarSearch extends JFrame {
    JPanel jPanel1 = new JPanel(); // Panneau pour afficher le labyrinthe.
    AStarSearchEngine currentSearchEngine = null; // Moteur de recherche A*.

    public MazeAStarSearch(int height, int width) {
        try {
            jbInit(); // Initialisation de l'interface graphique.
        } catch (Exception e) {
            System.out.println("GUI initialization error: " + e);
        }
        currentSearchEngine = new AStarSearchEngine(height, width); // Initialisation du moteur de recherche A*.
        currentSearchEngine.search(); // Lancement de la recherche A*.
        repaint(); // Redessine la fenêtre pour afficher les résultats de la recherche.
    }

    public void paint(Graphics g_unused) {
        if (currentSearchEngine == null) return; // Si le moteur de recherche n'est pas initialisé, on ne fait rien.

        Maze maze = currentSearchEngine.getMaze(); // Récupération du labyrinthe.
        int width = maze.getWidth(); // Largeur du labyrinthe.
        int height = maze.getHeight(); // Hauteur du labyrinthe.
        System.out.println("Size of current maze: " + width + " by " + height);

        Graphics g = jPanel1.getGraphics(); // Obtention du contexte graphique pour dessiner sur le panneau.

        // Création d'une image tampon pour dessiner le labyrinthe.
        BufferedImage image = new BufferedImage(320, 320, BufferedImage.TYPE_INT_RGB);
        Graphics g2 = image.getGraphics();

        // Remplit l'image avec un fond blanc.
        g2.setColor(Color.white);
        g2.fillRect(0, 0, 320, 320);

        // Trace les cellules du labyrinthe.
        g2.setColor(Color.black);
        maze.setValue(0, 0, Maze.START_LOC_VALUE); // Définit la position de départ du labyrinthe.

        // Boucle pour dessiner chaque cellule du labyrinthe en fonction de sa valeur.
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                short val = maze.getValue(x, y); // Obtention de la valeur de la cellule.

                // Si la cellule est un obstacle.
                if (val == Maze.OBSTICLE) {
                    g2.setColor(Color.lightGray); // Couleur des obstacles.
                    g2.fillRect(6 + x * 29, 3 + y * 29, 29, 29); // Remplit la case de l'obstacle.
                    g2.setColor(Color.black);
                    g2.drawRect(6 + x * 29, 3 + y * 29, 29, 29); // Trace les contours de la case.
                }
                // Si c'est la position de départ.
                else if (val == Maze.START_LOC_VALUE) {
                    g2.setColor(Color.blue);
                    g2.drawString("S", 16 + x * 29, 19 + y * 29); // Indique "S" pour le départ.
                    g2.setColor(Color.black);
                    g2.drawRect(6 + x * 29, 3 + y * 29, 29, 29);
                }
                // Si c'est la position de l'objectif (fin du labyrinthe).
                else if (val == Maze.GOAL_LOC_VALUE) {
                    g2.setColor(Color.red);
                    g2.drawString("G", 16 + x * 29, 19 + y * 29); // Indique "G" pour l'objectif.
                    g2.setColor(Color.black);
                    g2.drawRect(6 + x * 29, 3 + y * 29, 29, 29);
                }
                // Si la cellule est vide (chemin possible).
                else {
                    g2.setColor(Color.black);
                    g2.drawRect(6 + x * 29, 3 + y * 29, 29, 29); // Contours de la case vide.
                }
            }
        }

        // Redessine le chemin trouvé par l'algorithme A*.
        g2.setColor(Color.black);
        Dimension[] path = currentSearchEngine.getPath(); // Récupération du chemin trouvé.
        for (int i = 1; i < (path.length - 1); i++) {
            int x = path[i].width; // Coordonnée x du nœud dans le chemin.
            int y = path[i].height; // Coordonnée y du nœud dans le chemin.
            short val = maze.getValue(x, y); // Obtention de la valeur de la cellule sur le chemin.
            g2.drawString("" + (path.length - i), 16 + x * 29, 19 + y * 29); // Indique le numéro du nœud sur le chemin.
        }
        g.drawImage(image, 30, 40, 320, 320, null); // Affiche l'image sur le panneau.
    }

    public static void main(String[] args) {
        MazeAStarSearch mazeSearch1 = new MazeAStarSearch(10, 10); // Création d'une instance pour un labyrinthe 10x10.
    }

    private void jbInit() throws Exception {
        this.setContentPane(jPanel1); // Définit le contenu du JFrame.
        this.setCursor(null); // Supprime le curseur par défaut.
        this.setDefaultCloseOperation(3); // Ferme l'application à la fermeture de la fenêtre.
        this.setTitle("MazeAStarSearch"); // Titre de la fenêtre.
        this.getContentPane().setLayout(null); // Pas de gestion de mise en page.
        jPanel1.setBackground(Color.white); // Définit le fond du panneau en blanc.
        jPanel1.setDebugGraphicsOptions(DebugGraphics.NONE_OPTION); // Pas d'options de débogage.
        jPanel1.setDoubleBuffered(false); // Désactive le double buffering.
        jPanel1.setRequestFocusEnabled(false); // Désactive la demande de focus.
        jPanel1.setLayout(null); // Pas de gestion de mise en page.
        this.setSize(370, 420); // Définit la taille de la fenêtre.
        this.setVisible(true); // Rendre la fenêtre visible.
    }
}
