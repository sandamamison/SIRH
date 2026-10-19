# Guide & Conseils de Conception de Base de Données

Ce document résume les règles d'or, les réflexes et les pièges classiques à garder en tête pour réussir la conception d'une base relationnelle (TPs, projets, examens).

---

## 1. Les 3 cardinalités et où mettre les clés

C'est 80 % des erreurs de conception. Il faut toujours se poser la question : *« Une entité A peut être liée à combien de B, et une entité B à combien de A ? »*

### Cas 1 : Relation 1 à N (Un-à-Plusieurs)
- **Exemple gargote :** Une catégorie a plusieurs plats, mais un plat a une seule catégorie ($1:N$).
- **Exemple SIRH :** Un département a plusieurs employés, un employé appartient à un département ($1:N$).
- **Règle :** **La clé étrangère (FK) va toujours du côté du "1"** (la table fille).
  - On met `id_categorie` dans `PLAT`.
  - On ne touche pas à la table `CATEGORIE`.

### Cas 2 : Relation N à N (Plusieurs-à-Plusieurs)
- **Exemple gargote :** Une commande contient plusieurs plats, et un plat est commandé dans plusieurs commandes ($N:N$).
- **Exemple SIRH :** Un employé participe à plusieurs projets, un projet a plusieurs employés ($N:N$).
- **Règle :** **Obligation de créer une table d'association (intermédiaire)**.
  - La clé primaire est souvent composée : `PRIMARY KEY (id_commande, id_plat)`.
  - Cela évite aussi les doublons accidentels sur la même ligne.

### Cas 3 : Relation 0..1 à N (Optionnelle d'un côté)
- **Exemple gargote :** Une commande peut appartenir à un client habitué ou à un client de passage ($0..1:N$).
- **Règle :** Clé étrangère simple dans la table `COMMANDE`, **mais sans `NOT NULL` (nullable)**.
  - Notation relationnelle : `#id_client?`
  - En SQL : `id_client INTEGER REFERENCES CLIENT(id_client)` (sans le mot-clé NOT NULL).
  - Éviter de créer une table d'association inutile pour du $0..1:N$, sauf si le besoin devient vraiment $N:N$.

---

## 2. Où placer un attribut ? (Le test des dépendances)

Quand on hésite sur la table qui doit porter un attribut, on teste ses dépendances :

- **Le prix du plat :** Est-ce qu'il dépend de la catégorie ? Non, du plat $\rightarrow$ dans `PLAT`.
- **La quantité commandée :** 
  - Est-ce qu'elle dépend du plat seul ? Non (un plat n'a pas de quantité fixe).
  - De la commande seule ? Non (une commande contient plein de plats différents).
  - $\rightarrow$ Elle dépend du **couple (commande, plat)** $\rightarrow$ attribut porté par la table d'association `LIGNE_COMMANDE`.
- **La note d'un entretien :** Portée par la table `EVALUATION_CANDIDAT` (ou `ENTRETIEN`), pas par le candidat seul.

---

## 3. L'indépendance des concepts physiques vs rôles

Ne jamais confondre le meuble/l'objet physique et son utilisation événementielle :

| Mauvais réflexe | Bonne pratique | Pourquoi ? |
|---|---|---|
| Mettre le montant directement dans `TABLE` | Table `COMMANDE` séparée de `TABLE_SALLE` | Une table de restaurant est permanente et accueille 20 commandes par jour. |
| Fusionner `POSTE` et `EMPLOYE` | Table `POSTE` indépendante + table `AFFECTATION` | Le poste de "Développeur" existe même si la personne démissionne (le poste devient vacant). |
| Fusionner `CANDIDAT` et `EMPLOYE` | Table `PERSONNE` + tables `CANDIDATURE` et `EMPLOYE` | Une personne physique peut postuler 3 fois avant d'être embauchée, ou être refusée sans jamais devenir salariée. |

---

## 4. L'historisation : ne jamais écraser le passé

C'est le piège récurrent du monde réel et des TPs :

1. **L'évolution des prix :**
   - Si tu fais `UPDATE PLAT SET prix = 7500 WHERE id_plat = 1`, et que tu calcules le total des vieilles factures avec `JOIN PLAT`, **toutes les anciennes commandes voient leur total augmenter rétroactivement !**
   - **Solution :** Soit figer le `prix_unitaire_facture` au moment de la commande dans `LIGNE_COMMANDE`, soit avoir une table `HISTORIQUE_PRIX(id_plat, prix, date_debut, date_fin)`.
2. **L'évolution des contrats / affectations :**
   - Ne pas mettre simplement `type_contrat` dans la table `EMPLOYE`.
   - Si l'employé passe de CE (Essai) à CDD puis CDI, on doit garder la trace des 3 contrats dans une table `CONTRAT` liée par une auto-référence (`id_contrat_precedent`).

---

## 5. Mots réservés SQL : les pièges fréquents

Certains noms courants provoquent des erreurs de syntaxe immédiates :

- `TABLE` $\rightarrow$ Nommer `TABLE_SALLE` ou `SALLE_TABLE` (sinon conflit avec `CREATE TABLE`).
- `USER` $\rightarrow$ Nommer `UTILISATEUR` ou `COMPTE_UTILISATEUR` (mot-clé système PostgreSQL).
- `ORDER` $\rightarrow$ Nommer `COMMANDE` (conflit avec `ORDER BY`).
- `DATE` ou `TIME` en nom de colonne $\rightarrow$ Préférer `date_commande`, `date_creation`, etc.
- `GROUP` $\rightarrow$ Préférer `GROUPE` ou `EQUIPE` (conflit avec `GROUP BY`).

---

## 6. Table de référence vs Contrainte CHECK vs ENUM

Quand on a une liste fermée de valeurs (statuts, modes de paiement, types de contrat) :

| Solution | Syntaxe | Avantages | Inconvénients | Quand l'utiliser ? |
|---|---|---|---|---|
| **Table dédiée** | `MODE_PAIEMENT(id, nom)` + FK | Très souple : un simple `INSERT` ajoute une option sans toucher au schéma. | Nécessite un `JOIN` supplémentaire. | Idéal si la liste évolue souvent (modes de paiement, catégories, rôles). |
| **Contrainte `CHECK`** | `statut VARCHAR CHECK (statut IN (...))` | Pas de table en plus, très lisible. | Pour ajouter une valeur, il faut faire un `ALTER TABLE DROP/ADD CONSTRAINT`. | Bon pour les valeurs immuables (ex: `sexe IN ('M', 'F')`). |
| **Type `ENUM` (PostgreSQL)** | `CREATE TYPE mon_statut AS ENUM (...)` | Typage fort, prend peu d'octets. | Propre à PostgreSQL, syntaxe un peu rigide pour retirer une valeur. | Parfait pour les machines à états fixes (workflows de statut). |

---

## 7. Jointures : le pense-bête infaillible

- **`INNER JOIN`** : Ne garde QUE ce qui existe des deux côtés.
  - *Piège :* Si tu fais `CATEGORIE JOIN PLAT`, les catégories qui n'ont aucun plat disparaissent du résultat.
- **`LEFT JOIN`** : Garde **tout ce qui est dans la table de gauche**, même si la table de droite ne trouve rien (rempli avec des `NULL`).
  - *Règle :* Dès qu'on demande "y compris ceux qui n'ont rien" ou "clients de passage", c'est un `LEFT JOIN`.
- **`COALESCE(colonne, valeur_si_null)`** :
  - Remplace les `NULL` générés par le `LEFT JOIN`.
  - Ex: `COALESCE(nom_client, 'client de passage')`.
  - Ex: `COALESCE(SUM(montant), 0)` (sinon une somme de NULL renvoie NULL au lieu de 0).

---

## 8. Multi-agrégations : le piège du produit cartésien

Si une commande a :
- 2 lignes de plats
- 2 paiements

Si tu écris :
```sql
SELECT co.id_commande, SUM(p.prix * lc.quantite), SUM(paye.montant)
FROM COMMANDE co
JOIN LIGNE_COMMANDE lc ON lc.id_commande = co.id_commande
JOIN PLAT p ON p.id_plat = lc.id_plat
JOIN PAIEMENT paye ON paye.id_commande = co.id_commande
GROUP BY co.id_commande;
```
👉 **Résultat faux !** 2 plats × 2 paiements = 4 lignes intermédiaires. Les prix et les montants payés seront doublés !

**La parade :** Toujours faire les sommes dans des **sous-requêtes indépendantes groupées par commande**, puis faire la jointure sur les totaux calculés.

---

## 9. Le piège des listes dans une colonne (1ère Forme Normale)

Ne jamais stocker plusieurs valeurs séparées par des virgules dans un champ texte (ex: `ingredients VARCHAR(500) = 'riz, zebu, bredes'`).

- **Pourquoi c'est interdit ?**
  - Impossible de faire un `JOIN` propre vers le stock des ingrédients.
  - Impossible de calculer la quantité consommée par plat.
  - Faire un `LIKE '%riz%'` est lent, ignore les index, et peut matcher par erreur des mots comme `"cerise"` ou `"riz-otto"`.
- **Bonne pratique :** Créer une table de composition :
  `COMPOSITION_PLAT(#id_plat, #id_ingredient, quantite_necessaire)`.

---

## 10. L'association réflexive (Une table liée à elle-même)

Parfois, une entité est composée d'éléments de même nature qu'elle-même :
- **Exemple 1 (Menu gargote) :** Un `Menu Romazava` est vendu comme un plat, mais il contient d'autres plats (`Romazava`, `Ranon'ampango`, `Mofo gasy`).
  $\rightarrow$ Table `COMPOSITION_MENU(#id_menu, #id_plat_inclus, quantite)` où les deux IDs pointent tous les deux vers `PLAT(id_plat)`.
  *Piège réflexif :* Mettre une contrainte `CHECK (id_menu <> id_plat_inclus)` pour empêcher un menu de se contenir lui-même !
- **Exemple 2 (Hiérarchie d'entreprise) :** Dans `EMPLOYE`, un manager est lui-même un employé.
  $\rightarrow$ Clé étrangère réflexive : `id_manager INTEGER REFERENCES EMPLOYE(id_employe)`.

---

## 11. Choix des types de données : pièges fréquents

| Donnée | Type recommandé | Mauvais type | Règle / Raison |
|---|---|---|---|
| **Téléphone** | `VARCHAR(20)` | `INTEGER` | Un numéro peut commencer par un zéro (`034...`) ou un signe (`+261...`). Un entier fait sauter le zéro initial ! |
| **Code postal / Matricule** | `VARCHAR(10)` | `INTEGER` | Même raison : évite la perte du zéro (ex: `01000`) et permet les lettres. |
| **Monnaie locale (Ariary)** | `INTEGER` | `DECIMAL` / `FLOAT` | Sans centimes $\rightarrow$ `INTEGER`. Si centimes $\rightarrow$ `DECIMAL(12,2)`. **Jamais de `FLOAT`** pour de l'argent. |
| **Horodatage** | `TIMESTAMP` | `VARCHAR` | Permet les calculs d'intervalles (`NOW() - date_creation`), les tris chronologiques et les filtres temporels. |
| **Texte long / Commentaires** | `TEXT` | `VARCHAR(255)` | En PostgreSQL, `TEXT` et `VARCHAR` ont les mêmes performances de stockage. Pas besoin de tronquer arbitrairement à 255 si le texte peut être plus long. |

---

## 12. Les contraintes d'intégrité (Garde-fous de la BDD)

La base de données doit être le dernier rempart contre les données corrompues, même si le code applicatif a un bug :

1. **`CHECK` pour les règles de bon sens :**
   - Prix et montants : `CHECK (prix >= 0)`
   - Quantités : `CHECK (quantite > 0)`
   - Cohérence de dates : `CHECK (date_fin IS NULL OR date_fin >= date_debut)`
2. **`UNIQUE` pour éviter les doublons métier :**
   - L'email d'un utilisateur : `email VARCHAR(150) UNIQUE`
   - Le matricule d'un salarié : `matricule VARCHAR(30) UNIQUE`
   - Un candidat postulant à une offre : `UNIQUE (id_offre, id_personne)`
3. **`NOT NULL` par défaut :**
   - Rendre une colonne nullable **uniquement** si le métier a une vraie raison d'avoir une absence de valeur (ex: client de passage, date de fin de contrat en cours, référence de paiement en espèces).

---

## 13. Clé artificielle (`SERIAL` / `ID`) vs Clé naturelle

- **Clé naturelle :** Utiliser une donnée métier comme clé primaire (ex: `nom_plat`, numéro de sécurité sociale).
  - *Danger :* Si l'entreprise fait une faute d'orthographe ou change le libellé, changer la clé primaire brise toutes les clés étrangères existantes !
- **Clé artificielle / synthétique :** Utiliser un `id SERIAL PRIMARY KEY`.
  - C'est un simple entier technique, invisible au client, stable et immuable dans le temps.
  - **Recommandation :** Préférer toujours une clé artificielle technique (`SERIAL`), et poser une contrainte `UNIQUE` sur la donnée métier si nécessaire.

---

## 14. Stratégie de migration sur une base avec données existantes

Pendant un projet ou un TP, une base en production a déjà des lignes insérées.

Si le client demande : *« Rend obligatoire le champ X sur la table T »* :
1. **Ne jamais faire** directement : `ALTER TABLE T ADD COLUMN x INT NOT NULL;` (PostgreSQL lèvera une erreur car les anciennes lignes n'ont pas de valeur).
2. **Ne jamais supprimer (`DROP TABLE`)** la table pour la recréer (perte sèche de données).
3. **La méthode pro en 3 étapes :**
   ```sql
   -- Étape 1 : ajouter la colonne nullable
   ALTER TABLE T ADD COLUMN x INTEGER REFERENCES AUTRE_TABLE(id);

   -- Étape 2 : mettre à jour les données existantes
   UPDATE T SET x = ... WHERE ...;

   -- Étape 3 : verrouiller en NOT NULL une fois tout renseigné
   ALTER TABLE T ALTER COLUMN x SET NOT NULL;
   ```

