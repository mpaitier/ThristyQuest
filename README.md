# Thirsty Quest
De la première pinte au dernier shot, on s'en souvient pour toi !

**ThirstyQuest** est une application mobile de suivi de consommation de boissons avec une composante sociale et ludique. L'utilisateur peut publier ses consommations, rejoindre des ligues, suivre d'autres utilisateurs, et cumuler de l'XP en partageant sa consommation.

Cette application a été crée dans le cadre du cours *8INF865 - Programmation de plateformes mobiles* de l'**UQAC**.

## Membres de l'équipe:
- [CROGUENNOC Romain](https://github.com/CroguennocRomain) - CROR09080300
- [MENARD Paul](https://github.com/PaulDranem) - MENP19120300
- [PAITIER Mathias](https://github.com/mpaitier) - PAIM12050300

## Technologies utilisées

Lors de ce projet nous avons utilisés les technologies suivantes :
- **Kotlin**
- **Jetpack Compose**
- **Firebase Authentication**
- **Firestore (NoSQL Database)**

## Structure du projet
com.example.thirstyquest

```
Racine du projet
├── data (Classes de données sur les catégories et publication)
│ 
├── db (Manipulation de Firestore Database)
│   └── catégories, follow, league, publications, utilisateurs ...
├── navigation 
└── ui 
    ├── components (toutes les interfaces composants directements les écrans, ex: bouton d'ajout d'amis, contenu des écrans de ligue, collection de l'utilisateur ...)
    ├── dialog (tous les pop-ups de l'application, ils interviennent dans les composants)
    ├── screens
    ├── theme
    ├── viewmodel  
    │    ├── Auth.kt (connexion, inscription & accès à l'id utilisateur)
    │    └── Settings.kt 
    └── MainActivity.kt
```

## Structure de la base de données

*Toutes les photoUrl redirigent vers Firebase Storage.*
```
drinkPoints
└── current
    └── chaque champ est une catégorie avec un score (String: Int)

leagues
└── identifiant de ligue (2 chiffres, 2 lettres, 2 chiffres: 00AA00)
    ├── count : Int (nombre de membres)
    ├── name : String
    ├── owner uid : String
    ├── photoUrl : String
    ├── total liters : Float
    ├── total price : Float
    └── xp : Int
└────── category
        └── Nom de catégorie
            ├── total
            └── total liters
└────── members
        └── uid
            └── uid
└────── publications
        └── pid
            └── pid

publication
└── identifiant de publication (pid)
    ├── ID : String
    ├── category : String
    ├── date : String
    ├── description : String (name)
    ├── hour : String
    ├── photo : String
    ├── points : Int
    ├── price : Float
    ├── user id : String
    └── volume : Float

users
└── identifiant d'utilisateur 
    ├── name : String
    ├── photoUrl : String
    ├── total drink : Float
    ├── total paid : Float
    ├── uid : String
    └── xp : Int
└────── category
        └── Nom de catégorie
            ├── level
            ├── total
            └── total liters
└────── followers
        └── uid
            └── uid
└────── following
        └── uid
            └── uid
└────── leagues
        └── lid
            └── League name
└────── publications
        └── pid
            ├── date : String
            └── hour : String
```

### Authentification & Utilisateur

- Authentification gérée par **Firebase Authentication**.
- Lors de la création d’un compte :
    - Un document de la collection `users` est automatiquement généré dans Firestore.
    - Le nom d’utilisateur est **unique**.
- Lorsqu’un utilisateur A suit un autre utilisateur B :
    - L'ID de B est ajouté aux `following/` de A : `users/{userId_A}/following/{userId_B}`
    - L'ID de A est ajouté aux `followers/` de B :  `users/{userId_B}/followers/{userId_A}`
 
### Stockage des photos utilisateurs
L’application Thirst Quest utilise Firebase Cloud Storage pour héberger les images liées aux utilisateurs. Cela concerne :
- les photos de profil (upload lors de l’inscription ou dans les paramètres),
- les photos des publications (prises ou sélectionnées via la caméra/galerie).
Les photos prises par les utilisateurs (ex. : photos de boissons) sont compressées localement au format JPEG avec une qualité de 60% avant d’être uploadées sur Firebase Storage.
```
└──Images/ (Stockage des photos de profiles et des publications)
└──LeagueImage/ (Stockage des photos de league)
```

### Publications

Lorsqu’un utilisateur crée une publication :

- Elle est ajoutée à une collection globale `publications`.
- Son ID est référencé dans `users/{userId}/publications`.
- Elle est également référencée dans les ligues associées à l’utilisateur : `leagues/{leagueId}/publications`.
- Les compteurs suivants sont mis à jour (pour la ligue et l'utilisateur) :
    - **XP**
    - **Prix total**
    - **Volume total**

## Points sensibles

- **UID** : l'AuthViewModel permet de récupérer l'identifiant de l'utilisateur
- **Coroutines** : pour accéder à des données sur *Firebase*, toujours créer une valeur par défaut et l'initialiser dans un LaunchEffect.
  Exemple pour le graphique de consommation :
```kotlin
fun UserStatsContent(userId: String, isFriend: Boolean) {
// on déclare les variables avec des valeurs par défaut (différentes de null car nos fonctions pour récupérer les valeurs peuvent renvoyer null)
    var weeklyConsumptionList by remember { mutableStateOf<List<Point>>( listOf(Point(-1f, -1f)) ) }
    var monthlyConsumptionList by remember { mutableStateOf<List<Point>>( listOf(Point(-1f, -1f)) ) }
    var yearlyConsumptionList by remember { mutableStateOf<List<Point>>( listOf(Point(-1f, -1f)) ) }

    var weeklyVolumeList by remember { mutableStateOf<List<Point>>( listOf(Point(-1f, -1f)) ) }
    var monthlyVolumeList by remember { mutableStateOf<List<Point>>( listOf(Point(-1f, -1f)) ) }
    var yearlyVolumeList by remember { mutableStateOf<List<Point>>( listOf(Point(-1f, -1f)) ) }
    var showedList by remember { mutableStateOf<List<Point>>(listOf(Point(0f, 0f))) }

// on récupère les valeurs réelles
    LaunchedEffect(userId) {
        weeklyConsumptionList = getWeekConsumptionPoints(userId, "users")
        monthlyConsumptionList = getMonthConsumptionPoints(userId, "users")
        yearlyConsumptionList = getYearConsumptionPoints(userId, "users")

        weeklyVolumeList = getWeekVolumeConsumptionPoints(userId, "users")
        monthlyVolumeList = getMonthVolumeConsumptionPoints(userId, "users")
        yearlyVolumeList = getYearVolumeConsumptionPoints(userId, "users")
        showedList = weeklyConsumptionList
    }

// on vérifie si les données sont prêtes pour afficher le graphique
    if (
        weeklyConsumptionList == listOf(Point(-1f, -1f)) ||
        monthlyConsumptionList == listOf(Point(-1f, -1f)) ||
        yearlyConsumptionList == listOf(Point(-1f, -1f)) ||
        weeklyVolumeList == listOf(Point(-1f, -1f)) ||
        monthlyVolumeList == listOf(Point(-1f, -1f)) ||
        yearlyVolumeList == listOf(Point(-1f, -1f))
    ) {
        LoadingSection()
    }
    else {
        ConsumptionChart(showedList, selectedDuration)
    }
}
```

