# PathfindingAlgorithms

## Description

**PathfindingAlgorithms** est un projet visant à résoudre des labyrinthes pondérés en deux dimensions à l'aide des algorithmes de recherche de chemin **Dijkstra** et **A***. L'objectif est de trouver le plus court chemin entre un point de départ et un point d'arrivée tout en tenant compte des coûts des cases et des obstacles. 

Ce projet permet également de comparer les performances des deux algorithmes dans différentes situations.

---

## Fonctionnalités principales

1. **Lecture de la carte** :
   - Chargement de la carte depuis un fichier texte (`graph.txt`).
   - Chaque carte est décrite par :
     - Dimensions (nombre de lignes et colonnes).
     - Types de terrains et leurs coûts.
     - Carte sous forme de grille.
     - Coordonnées de départ et d'arrivée.

2. **Algorithmes de recherche de chemin** :
   - **Dijkstra** :
     - Recherche exhaustive du chemin optimal à travers le graphe pondéré.
   - **A*** :
     - Variante informée de Dijkstra qui utilise une heuristique pour prioriser les sommets.

3. **Affichage graphique** :
   - Visualisation de la carte et du chemin trouvé.
   - Animation de la progression des algorithmes (exploration des sommets).

4. **Comparaison des algorithmes** :
   - Analyse du nombre de nœuds explorés.
   - Comparaison des coûts des chemins trouvés.
   - Mesure des performances sur des cartes de tailles différentes.

---

## Structure du projet

### Fichiers principaux

- **`WeightedGraph.java`** :
  - Représente le graphe pondéré.
  - Chaque sommet est un nœud avec des propriétés comme son coût, ses voisins, etc.

- **`Dijkstra.java`** :
  - Contient l'implémentation de l'algorithme de Dijkstra.

- **`AStar.java`** :
  - Contient l'implémentation de l'algorithme A* avec heuristique.

- **`Board.java`** :
  - Gère l'affichage graphique des cartes et des chemins.

- **`graph.txt`** :
  - Fichier d'entrée décrivant les cartes à résoudre.

---

## Exemple de fichier `graph.txt`

```txt
5 5
. . . . .
. S . F .
. . . . .
. . . F .
. . . . E
```

- **S** : Départ
- **E** : Arrivée
- **F** : Obstacles
- **.** : Cases traversables

---

## Instructions d'exécution

1. **Compilation des fichiers sources** :
   ```bash
   javac src/*.java
   ```

2. **Exécution du programme** :
   ```bash
   java Main
   ```

3. **Fournir un fichier d'entrée** :
   Placez un fichier `graph.txt` dans le répertoire d'exécution.

---

## Algorithmes utilisés

### Dijkstra
- Explore tous les chemins possibles en trouvant systématiquement le plus court.
- Complexité : **O((V + E) log V)** où :
  - **V** est le nombre de sommets (cases dans la carte).
  - **E** est le nombre d'arêtes (connexions entre les cases).

### A*
- Utilise une heuristique (distance euclidienne) pour prioriser les sommets proches de la destination.
- Réduit souvent la zone explorée, ce qui le rend plus efficace que Dijkstra dans de nombreux cas.

---

## Auteur
- **Zineb Fennich**
