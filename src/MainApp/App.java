// Par Sylvain Lobry, pour le cours "IF05X040 Algorithmique avanc?e"
// de l'Université de Paris, 11/2020

package MainApp;

import MainApp.WeightedGraph.Edge;
import MainApp.WeightedGraph.Graph;

import java.io.*;
import java.util.*;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.RenderingHints;
import javax.swing.JComponent;
import javax.swing.JFrame;

/**
 * Classe pour gérer l'affichage graphique du graphe sur un plateau (Board).
 */
class Board extends JComponent {
	private static final long serialVersionUID = 1L;
	/** Le graphe à afficher. */
	Graph graph;
	/** Taille d'un pixel (échelle d'affichage). */
	int pixelSize;
	/** Nombre de colonnes de la grille. */
	int ncols;
	/** Nombre de lignes de la grille. */
	int nlines;
	/** Couleurs des cases, en fonction de leur type (entier). */
	HashMap<Integer, String> colors;
	/** Sommet de départ. */
	int start;
	/** Sommet d'arrivée. */
	int end;
	/** Distance maximale (utilisée pour le gradient d'affichage). */
	double max_distance;
	/** Sommet courant en cours de traitement (pour l'affichage). */
	int current;
	/** Chemin trouvé, sous forme de liste d'entiers représentant des sommets. */
	LinkedList<Integer> path;

	/**
	 * Constructeur.
	 *
	 * @param graph      Le graphe à afficher.
	 * @param pixelSize  Taille d'un pixel.
	 * @param ncols      Nombre de colonnes de la grille.
	 * @param nlines     Nombre de lignes de la grille.
	 * @param colors     Dictionnaire associant un entier de type de case à une couleur.
	 * @param start      Sommet de départ.
	 * @param end        Sommet d'arrivée.
	 */
	public Board(Graph graph, int pixelSize, int ncols, int nlines, HashMap<Integer, String> colors, int start, int end) {
		super();
		this.graph = graph;
		this.pixelSize = pixelSize;
		this.ncols = ncols;
		this.nlines = nlines;
		this.colors = colors;
		this.start = start;
		this.end = end;
		this.max_distance = ncols * nlines;
		this.current = -1;
		this.path = null;
	}

	/**
	 * Méthode appelée pour peindre le composant.
	 *
	 * @param g L'objet Graphics utilisé pour dessiner.
	 */
	@Override
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		// Nettoyage de la zone de dessin
		g2.setColor(Color.cyan);
		g2.fill(new Rectangle2D.Double(0, 0, this.ncols * this.pixelSize, this.nlines * this.pixelSize));

		int num_case = 0;
		for (WeightedGraph.Vertex v : this.graph.vertexlist) {
			double type = v.indivTime;
			int i = num_case / this.ncols;
			int j = num_case % this.ncols;

			if (colors.get((int) type).equals("green"))
				g2.setPaint(Color.green);
			if (colors.get((int) type).equals("gray"))
				g2.setPaint(Color.gray);
			if (colors.get((int) type).equals("blue"))
				g2.setPaint(Color.blue);
			if (colors.get((int) type).equals("yellow"))
				g2.setPaint(Color.yellow);

			g2.fill(new Rectangle2D.Double(j * this.pixelSize, i * this.pixelSize, this.pixelSize, this.pixelSize));

			if (num_case == this.current) {
				g2.setPaint(Color.red);
				g2.draw(new Ellipse2D.Double(j * this.pixelSize + this.pixelSize / 2,
						i * this.pixelSize + this.pixelSize / 2, 6, 6));
			}
			if (num_case == this.start) {
				g2.setPaint(Color.white);
				g2.fill(new Ellipse2D.Double(j * this.pixelSize + this.pixelSize / 2,
						i * this.pixelSize + this.pixelSize / 2, 4, 4));
			}
			if (num_case == this.end) {
				g2.setPaint(Color.black);
				g2.fill(new Ellipse2D.Double(j * this.pixelSize + this.pixelSize / 2,
						i * this.pixelSize + this.pixelSize / 2, 4, 4));
			}

			num_case += 1;
		}

		num_case = 0;
		for (WeightedGraph.Vertex v : this.graph.vertexlist) {
			int i = num_case / this.ncols;
			int j = num_case % this.ncols;
			if (v.timeFromSource < Double.POSITIVE_INFINITY) {
				float g_value = (float) (1 - v.timeFromSource / this.max_distance);
				if (g_value < 0)
					g_value = 0;
				g2.setPaint(new Color(g_value, g_value, g_value));
				g2.fill(new Ellipse2D.Double(j * this.pixelSize + this.pixelSize / 2,
						i * this.pixelSize + this.pixelSize / 2, 4, 4));
				WeightedGraph.Vertex previous = v.prev;
				if (previous != null) {
					int i2 = previous.num / this.ncols;
					int j2 = previous.num % this.ncols;
					g2.setPaint(Color.black);
					g2.draw(new Line2D.Double(
							j * this.pixelSize + this.pixelSize / 2,
							i * this.pixelSize + this.pixelSize / 2,
							j2 * this.pixelSize + this.pixelSize / 2,
							i2 * this.pixelSize + this.pixelSize / 2
					));
				}
			}
			num_case += 1;
		}

		int prev = -1;
		if (this.path != null) {
			g2.setStroke(new BasicStroke(3.0f));
			for (int cur : this.path) {
				if (prev != -1) {
					g2.setPaint(Color.red);
					int i = prev / this.ncols;
					int j = prev % this.ncols;
					int i2 = cur / this.ncols;
					int j2 = cur % this.ncols;
					g2.draw(new Line2D.Double(
							j * this.pixelSize + this.pixelSize / 2,
							i * this.pixelSize + this.pixelSize / 2,
							j2 * this.pixelSize + this.pixelSize / 2,
							i2 * this.pixelSize + this.pixelSize / 2
					));
				}
				prev = cur;
			}
		}
	}

	/**
	 * Met à jour le graphe (notamment les valeurs calculées) et provoque un repaint().
	 *
	 * @param graph    Le nouveau graphe (ou graphe modifié).
	 * @param current  Le sommet courant.
	 */
	public void update(Graph graph, int current) {
		this.graph = graph;
		this.current = current;
		repaint();
	}

	/**
	 * Fixe le chemin trouvé pour l'affichage et rafraîchit la vue.
	 *
	 * @param graph Le graphe (pour afficher les éventuelles modifications finales).
	 * @param path  La liste des sommets formant le chemin trouvé.
	 */
	public void addPath(Graph graph, LinkedList<Integer> path) {
		this.graph = graph;
		this.path = path;
		this.current = -1;
		repaint();
	}
}

/**
 * Classe principale. Point d'entrée pour exécuter le programme.
 */
public class App {

	/**
	 * Initialise l'affichage en créant la fenêtre et en y insérant le Board.
	 *
	 * @param board     Le composant d'affichage.
	 * @param nlines    Nombre de lignes de la grille.
	 * @param ncols     Nombre de colonnes de la grille.
	 * @param pixelSize Taille d'un pixel (échelle).
	 */
	private static void drawBoard(Board board, int nlines, int ncols, int pixelSize) {
		JFrame window = new JFrame("Plus court chemin");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setBounds(0, 0, ncols * pixelSize + 20, nlines * pixelSize + 40);
		window.getContentPane().add(board);
		window.setVisible(true);
	}

	/**
	 * Méthode A* pour trouver le plus court chemin.
	 *
	 * @param graph    Le graphe représentant la carte.
	 * @param start    Le sommet de départ.
	 * @param end      Le sommet d'arrivée.
	 * @param ncols    Le nombre de colonnes dans la carte.
	 * @param numberV  Le nombre de sommets (cases) dans la carte.
	 * @param board    Le composant d'affichage.
	 * @return Une liste d'entiers correspondant au chemin (sommets).
	 */
	private static LinkedList<Integer> AStar(Graph graph, int start, int end, int ncols, int numberV, Board board) {
		// Réinitialiser le graphe
		resetGraph(graph);
		graph.vertexlist.get(start).timeFromSource = 0;
		graph.vertexlist.get(start).heuristic = calculateHeuristic(start, end, ncols);
		graph.vertexlist.get(start).f = graph.vertexlist.get(start).heuristic;

		// Définir un Comparator pour la PriorityQueue basé sur f = g + h
		Comparator<WeightedGraph.Vertex> comparator = Comparator.comparingDouble(v -> v.f);
		PriorityQueue<WeightedGraph.Vertex> to_visit = new PriorityQueue<>(comparator);
		to_visit.add(graph.vertexlist.get(start));

		// Tableau pour suivre les nœuds déjà visités
		boolean[] visited = new boolean[numberV];

		int number_tries = 0;

		while (!to_visit.isEmpty()) {
			// Extraire le nœud avec la plus petite valeur de f
			WeightedGraph.Vertex currentVertex = to_visit.poll();
			int current = currentVertex.num;

			// Si déjà visité, ignorer
			if (visited[current]) {
				continue;
			}

			// Marquer comme visité
			visited[current] = true;

			// Si nous avons atteint la destination, on arrête
			if (current == end) {
				System.out.println("Destination trouvée avec A*!");
				break;
			}

			number_tries++;

			// Explorer les voisins du nœud courant
			for (Edge edge : currentVertex.adjacencylist) {
				int neighbor = edge.destination;
				double edgeWeight = edge.weight;
				double newDistance = currentVertex.timeFromSource + edgeWeight;

				// Si on trouve un chemin plus court vers neighbor
				if (newDistance < graph.vertexlist.get(neighbor).timeFromSource) {
					WeightedGraph.Vertex neighborVertex = graph.vertexlist.get(neighbor);
					neighborVertex.timeFromSource = newDistance;
					neighborVertex.f = newDistance + calculateHeuristic(neighbor, end, ncols);
					neighborVertex.prev = currentVertex;

					// Ajouter le voisin à la PriorityQueue
					to_visit.add(neighborVertex);

					// Mettre à jour l'affichage
					try {
						board.update(graph, neighbor);
						Thread.sleep(10);
					} catch (InterruptedException e) {
						System.out.println("Thread interrompu.");
					}
				}
			}
		}

		System.out.println("Done! Using A*:");
		System.out.println("    Number of nodes explored: " + number_tries);
		System.out.println("    Total time of the path: " + graph.vertexlist.get(end).timeFromSource);

		// Construire le chemin final
		LinkedList<Integer> path = new LinkedList<>();
		WeightedGraph.Vertex currentVertex = graph.vertexlist.get(end);

		while (currentVertex != null && currentVertex.num != start) {
			path.addFirst(currentVertex.num);
			currentVertex = currentVertex.prev;
		}

		if (currentVertex != null) {
			path.addFirst(currentVertex.num);
		}

		// Afficher le chemin
		board.addPath(graph, path);
		return path;
	}

	/**
	 * Calcule l'heuristique (distance euclidienne) entre deux sommets dans une grille 2D.
	 *
	 * @param current Le sommet courant.
	 * @param end     Le sommet d'arrivée.
	 * @param ncols   Le nombre de colonnes.
	 * @return La distance euclidienne à vol d'oiseau.
	 */
	private static double calculateHeuristic(int current, int end, int ncols) {
		int x1 = current / ncols;
		int y1 = current % ncols;
		int x2 = end / ncols;
		int y2 = end % ncols;
		return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
	}

	/**
	 * Construit un chemin depuis la fin jusqu'au début en suivant les prédécesseurs.
	 *
	 * @param graph Le graphe.
	 * @param end   Le sommet d'arrivée.
	 * @return Une liste de sommets (entiers) du début à la fin.
	 */
	private static LinkedList<Integer> constructPath(Graph graph, int end) {
		LinkedList<Integer> path = new LinkedList<>();
		WeightedGraph.Vertex current = graph.vertexlist.get(end);

		while (current != null) {
			path.addFirst(current.num);
			current = current.prev;
		}
		return path;
	}

	/**
	 * Méthode Dijkstra pour trouver le plus court chemin en pondérant chaque arête.
	 *
	 * @param graph   Le graphe représentant la carte.
	 * @param start   Le sommet de départ.
	 * @param end     Le sommet d'arrivée.
	 * @param numberV Le nombre de sommets dans le graphe.
	 * @param board   Le composant d'affichage.
	 * @return Une liste d'entiers correspondant au chemin.
	 */
	private static LinkedList<Integer> Dijkstra(Graph graph, int start, int end, int numberV, Board board) {
		// Réinitialiser le graphe
		resetGraph(graph);
		graph.vertexlist.get(start).timeFromSource = 0; // La distance de la source est 0

		// Comparator basé sur timeFromSource
		Comparator<WeightedGraph.Vertex> comparator = Comparator.comparingDouble(v -> v.timeFromSource);
		PriorityQueue<WeightedGraph.Vertex> to_visit = new PriorityQueue<>(comparator);
		to_visit.add(graph.vertexlist.get(start));

		// Tableau pour savoir si un sommet a été visité
		boolean[] visited = new boolean[numberV];

		int number_tries = 0;

		while (!to_visit.isEmpty()) {
			WeightedGraph.Vertex currentVertex = to_visit.poll();
			int current = currentVertex.num;

			if (visited[current]) {
				continue;
			}
			visited[current] = true;

			if (current == end) {
				System.out.println("Destination trouvée avec Dijkstra!");
				break;
			}
			number_tries++;

			for (Edge edge : currentVertex.adjacencylist) {
				int neighbor = edge.destination;
				double edgeWeight = edge.weight;
				double newDistance = currentVertex.timeFromSource + edgeWeight;

				if (newDistance < graph.vertexlist.get(neighbor).timeFromSource) {
					WeightedGraph.Vertex neighborVertex = graph.vertexlist.get(neighbor);
					neighborVertex.timeFromSource = newDistance;
					neighborVertex.prev = graph.vertexlist.get(current);

					to_visit.add(neighborVertex);

					// Mettre à jour l'affichage
					try {
						board.update(graph, neighbor);
						Thread.sleep(10);
					} catch (InterruptedException e) {
						System.out.println("Thread interrompu.");
					}
				}
			}
		}

		System.out.println("Done! Using Dijkstra:");
		System.out.println("    Number of nodes explored: " + number_tries);
		System.out.println("    Total time of the path: " + graph.vertexlist.get(end).timeFromSource);

		// Reconstruire le chemin
		LinkedList<Integer> path = new LinkedList<>();
		WeightedGraph.Vertex currentVertex = graph.vertexlist.get(end);

		while (currentVertex != null && currentVertex.num != start) {
			path.addFirst(currentVertex.num);
			currentVertex = currentVertex.prev;
		}
		if (currentVertex != null) {
			path.addFirst(currentVertex.num);
		}

		board.addPath(graph, path);
		return path;
	}

	/**
	 * Réinitialise les propriétés (timeFromSource, heuristic, f, prev) de tous les sommets du graphe.
	 *
	 * @param graph Le graphe dont on veut réinitialiser les valeurs.
	 */
	private static void resetGraph(Graph graph) {
		for (WeightedGraph.Vertex v : graph.vertexlist) {
			v.timeFromSource = Double.POSITIVE_INFINITY;
			v.heuristic = 0;
			v.f = Double.POSITIVE_INFINITY;
			v.prev = null;
		}
	}

	/**
	 * Méthode principale. Lit la carte, construit le graphe, puis exécute l'algorithme choisi par l'utilisateur.
	 *
	 * @param args Arguments de la ligne de commande (non utilisés ici).
	 */
	public static void main(String[] args) {
		Scanner myReader = null;
		try {
			// Charger graph.txt en tant que ressource depuis le JAR
			InputStream is = App.class.getResourceAsStream("/MainApp/graph.txt");
			if (is == null) {
				System.err.println("Resource /MainApp/graph.txt introuvable dans le JAR.");
				return;
			}
			myReader = new Scanner(is);
			String data = "";

			// On ignore les trois premières lignes
			for (int i = 0; i < 3; i++) {
				if (myReader.hasNextLine()) {
					data = myReader.nextLine();
				} else {
					System.out.println("Fichier de carte incomplet.");
					myReader.close();
					return;
				}
			}

			// Lecture du nombre de lignes
			int nlines = Integer.parseInt(data.split("=")[1].trim());

			// Lecture du nombre de colonnes
			if (myReader.hasNextLine()) {
				data = myReader.nextLine();
			} else {
				System.out.println("Fichier de carte incomplet.");
				myReader.close();
				return;
			}
			int ncols = Integer.parseInt(data.split("=")[1].trim());

			// Initialisation du graphe
			Graph graph = new Graph();

			HashMap<String, Integer> groundTypes = new HashMap<>();
			HashMap<Integer, String> groundColor = new HashMap<>();
			if (myReader.hasNextLine()) {
				data = myReader.nextLine(); // Supposons que c'est "==Types=="
			} else {
				System.out.println("Fichier de carte incomplet.");
				myReader.close();
				return;
			}

			if (myReader.hasNextLine()) {
				data = myReader.nextLine();
			} else {
				System.out.println("Fichier de carte incomplet.");
				myReader.close();
				return;
			}

			// Lire les différents types de cases
			while (!data.equals("==Graph==")) {
				String[] parts = data.split("=");
				if (parts.length < 2) {
					System.out.println("Format de type de sol incorrect: " + data);
					myReader.close();
					return;
				}
				String name = parts[0].trim();
				int time = Integer.parseInt(parts[1].trim());
				if (myReader.hasNextLine()) {
					String color = myReader.nextLine().trim();
					groundTypes.put(name, time);
					groundColor.put(time, color);
				} else {
					System.out.println("Fichier de carte incomplet.");
					myReader.close();
					return;
				}
				if (myReader.hasNextLine()) {
					data = myReader.nextLine();
				} else {
					System.out.println("Fichier de carte incomplet.");
					myReader.close();
					return;
				}
			}

			// On ajoute les sommets dans le graphe (avec le bon type)
			for (int line = 0; line < nlines; line++) {
				if (myReader.hasNextLine()) {
					data = myReader.nextLine();
				} else {
					System.out.println("Fichier de carte incomplet.");
					myReader.close();
					return;
				}
				for (int col = 0; col < ncols; col++) {
					if (col >= data.length()) {
						System.out.println("Ligne " + line + " trop courte.");
						myReader.close();
						return;
					}
					char groundChar = data.charAt(col);
					String groundName = String.valueOf(groundChar);
					if (!groundTypes.containsKey(groundName)) {
						System.out.println("Type de sol inconnu: " + groundName);
						myReader.close();
						return;
					}
					graph.addVertex(groundTypes.get(groundName));
				}
			}

			// On ajoute les arêtes au graphe
			for (int line = 0; line < nlines; line++) {
				for (int col = 0; col < ncols; col++) {
					int source = line * ncols + col;

					// Connexions avec les voisins (8 directions)
					for (int dLine = -1; dLine <= 1; dLine++) {
						for (int dCol = -1; dCol <= 1; dCol++) {
							if (dLine == 0 && dCol == 0) continue; // Ignorer la case elle-même
							int newLine = line + dLine;
							int newCol = col + dCol;

							// Vérifier si le voisin est dans les limites
							if (newLine >= 0 && newLine < nlines && newCol >= 0 && newCol < ncols) {
								int dest = newLine * ncols + newCol;

								double weightSource = graph.vertexlist.get(source).indivTime;
								double weightDest   = graph.vertexlist.get(dest).indivTime;
								double weight;

								if (dLine != 0 && dCol != 0) {
									// Mouvement diagonal
									weight = (weightSource + weightDest) / 2.0;
								} else {
									// Mouvement horizontal ou vertical
									weight = weightSource + weightDest;
								}
								graph.addEgde(source, dest, weight);
							}
						}
					}
				}
			}

			// On obtient les nœuds de départ et d'arrivée
			if (myReader.hasNextLine()) {
				data = myReader.nextLine(); // Supposons que c'est "==StartEnd=="
			} else {
				System.out.println("Fichier de carte incomplet.");
				myReader.close();
				return;
			}

			if (myReader.hasNextLine()) {
				data = myReader.nextLine(); // Start
				String[] startParts = data.split("=")[1].trim().split(",");
				if (startParts.length < 2) {
					System.out.println("Format de start incorrect: " + data);
					myReader.close();
					return;
				}
				int startRow = Integer.parseInt(startParts[0].trim());
				int startCol = Integer.parseInt(startParts[1].trim());
				int startV = startRow * ncols + startCol;

				if (myReader.hasNextLine()) {
					data = myReader.nextLine(); // End
					String[] endParts = data.split("=")[1].trim().split(",");
					if (endParts.length < 2) {
						System.out.println("Format de end incorrect: " + data);
						myReader.close();
						return;
					}
					int endRow = Integer.parseInt(endParts[0].trim());
					int endCol = Integer.parseInt(endParts[1].trim());
					int endV = endRow * ncols + endCol;

					myReader.close();

					// Affichage
					int pixelSize = 10; // Ajustez selon vos préférences
					Board board = new Board(graph, pixelSize, ncols, nlines, groundColor, startV, endV);
					drawBoard(board, nlines, ncols, pixelSize);
					board.repaint();

					// Petite pause pour s'assurer que la fenêtre est prête
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						System.out.println("Interruption du thread.");
					}

					// Choix de l'algorithme
					Scanner input = new Scanner(System.in);
					String algorithm = "";
					while (true) {
						System.out.println("Choisissez l'algorithme à utiliser :");
						System.out.println("1. Dijkstra");
						System.out.println("2. A*");
						System.out.print("Entrez le numéro de l'algorithme (1 ou 2) : ");
						String choice = input.nextLine().trim();
						if (choice.equals("1")) {
							algorithm = "Dijkstra";
							break;
						} else if (choice.equals("2")) {
							algorithm = "AStar";
							break;
						} else {
							System.out.println("Choix invalide. Veuillez entrer 1 ou 2.");
						}
					}
					input.close();

					// Exécution de l'algorithme choisi
					LinkedList<Integer> path;
					if (algorithm.equals("Dijkstra")) {
						path = Dijkstra(graph, startV, endV, nlines * ncols, board);
					} else {
						path = AStar(graph, startV, endV, ncols, nlines * ncols, board);
					}

					// Écriture du chemin dans un fichier texte
					try {
						File file = new File("out.txt");
						if (!file.exists()) {
							file.createNewFile();
						}
						FileWriter fw = new FileWriter(file.getAbsoluteFile());
						BufferedWriter bw = new BufferedWriter(fw);

						for (int i : path) {
							int row = i / ncols;
							int col = i % ncols;
							bw.write(row + "," + col);
							bw.newLine();
						}
						bw.close();
						System.out.println("Chemin écrit dans out.txt");
					} catch (IOException e) {
						e.printStackTrace();
					}

				} else {
					System.out.println("Fichier de carte incomplet.");
					myReader.close();
				}
			} else {
				System.out.println("Fichier de carte incomplet.");
				myReader.close();
			}
		} catch (Exception e) { // Remplacez IOException par Exception
			System.out.println("An error occurred.");
			e.printStackTrace();
		} finally {
			if (myReader != null) {
				myReader.close();
			}
		}
	}
}
