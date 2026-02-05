# Hybrid-QR-Engine : AGSL & Reed-Solomon from Scratch

![Kotlin](https://img.shields.io/badge/Kotlin-2.0+-blue)
![API](https://img.shields.io/badge/Android_API-33+-green)
![License](https://img.shields.io/badge/License-MIT-lightgrey)

Ce projet est une implémentation from scratch d'un générateur de QR Code pour Android, utilisant une approche hybride : la logique binaire et la correction d'erreur sont traitées par le CPU (Kotlin), tandis que le rendu visuel est délégué au GPU via des shaders AGSL.

L'objectif est d'obtenir un QR Code **100% conforme aux spécifications ISO/IEC 18004**, lisible par Google ML Kit, tout en offrant une flexibilité esthétique totale grâce au pipeline graphique.

### Exemple d’usage

Génération rapide d’un QR et affichage Compose :

1.  **Générer la matrice binaire CPU**
2.  **Encoder dans un buffer compact** (1 bit/module)
3.  **Passer le buffer au RuntimeShader**
4.  **Affichage** avec `QrDisplay()` Compose

### Architecture et Pipeline

#### 1. Phase CPU : "The Bit Architect" (Kotlin)

* **Encodage des données** : Conversion de l'input en flux binaire (Mode Byte/UTF-8).
* **Reed-Solomon** : Calcul des blocs de correction avec tables Log/Antilog pré-calculées pour GF(2^8) — *la "calculatrice de triche" pour multiplier les polynômes.*
* **Placement Matrix** : Agencement en serpentin des bits, insertion des Finder/Alignment/Timing Patterns.
* **Masking Engine** : Application des masques optimisés pour casser les motifs répétitifs qui perturbent les capteurs.

#### 2. Phase GPU : "The AGSL Visualizer"

* **Buffer compact** : Transfert de la matrice binaire CPU → Shader pour minimiser la copie mémoire.
* **SDF Rendering** : Utilisation des *Signed Distance Fields* pour un arrondi des modules et un contrôle précis des bords.
* **Styling & Glow** : Dégradés, effets de luminescence et scanlines calculés par pixel écran.
* **Anti-aliasing matériel** : Rendu parfaitement lisse, peu importe la densité de l'écran.

---

### Special Feature : Stealth "Organic" Mode

L'innovation majeure de ce projet réside dans sa capacité à masquer la structure rigide du QR Code derrière une animation fluide et mystérieuse.

**Le concept : "Camouflage par Dithering Dynamique"**
Au lieu de modules carrés fixes, le shader génère un flux de particules organiques. La matrice de données sert de **"Carte de Densité"** :
* **Zone Noire (Data)** : Haute densité de particules, opacité 100%, mouvements resserrés.
* **Zone Blanche (Vide)** : Basse densité, opacité quasi-nulle, particules floues ou absentes.

**Pourquoi ça marche ?**
* **Humain** : L'œil est attiré par le mouvement incohérent des particules. La persistance rétinienne "lisse" l'image et perçoit un nuage organique plutôt qu'un code-barres.
* **Machine (ML Kit)** : Le capteur photo capture un instantané. En binarisant l'image, les amas de particules recréent les blocs de contraste nécessaires au décodage. La correction Reed-Solomon (Niveau Q) s'occupe de combler les éventuels "trous" dans le nuage de points.

---

### Spécifications Techniques

**Configuration QR Code**
* **Version** : 2 (25 × 25 modules) voir 1 (21 x 21) pour Stealth mode
* **Error Correction Level** : Q (25% de redondance) — H 30-35% *Indispensable pour le mode Stealth.*
* **Data Mode** : Byte (8-bit)

**Stack Technologique**
* **Language** : Kotlin 2.0+
* **Graphics** : AGSL (Android 13+ / API 33+)
* **UI** : Jetpack Compose (pont RuntimeShader → Canvas)

### Structure du Code Source

* `core/`
    * `GaloisField.kt` : Mathématiques des polynômes (GF256), tables Log/Antilog.
    * `ReedSolomon.kt` : Générateur de blocs d'erreur optimisé.
    * `MatrixBuilder.kt` : Placement des patterns et des bits, scoring masques.
* `graphics/`
    * `QrShaderProvider.kt` : Gestionnaire du RuntimeShader, réception buffer.
    * `StealthEngine.agsl` : Logique de dithering dynamique et rendu des particules.
* `ui/`
    * `QrDisplay.kt` : Composant Compose faisant le lien CPU → GPU.

### Roadmap d'implémentation

1.  **Module GF(256)** : Coder les tables de Log/Antilog pour permettre les multiplications de "l'espace".
2.  **Logic Matrix** : Créer la grille de base avec les motifs de repérage fixes (Finder Patterns).
3.  **Reed-Solomon** : Valider la génération des octets de correction avec un set de données connu.
4.  **Shader Bridge** : Envoyer la matrice au shader via un buffer compact.
5.  **Stealth Rendering** : Développer le système de particules AGSL basé sur la densité de la matrice.

