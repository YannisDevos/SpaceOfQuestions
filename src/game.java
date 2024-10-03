//si tu veut tester tes fonctions de difficulté, decommente, par exemple, ligne 188 (pour appeller la fonction)
//pour les fonctions de difficulté (du moins "Normal" et "Difficile"), il suffie normalement de copier coller la fonction "jouerPartieModeFacile" et de modifier quelque valeur comme la vie, par exemple (6 pour "Normal" et 3 pour "Difficile").



import extensions.CSVFile;

class game extends Program{



//////////////////////////////////algorithme principal//////////////////////////////////



    void algorithm(){
        //-------------initialisation-------------\\

        String titreJeu = fileAsString("ressources/models/titreJeu");
        CSVFile listeStatsJoueurs = chargerCSV("ressources/CSV/statsJoueurs.csv");

        affichageTitre(titreJeu); //affiche le titre du jeu ainsi que les différentes options (mode, etc...) disponibles
        String[] informationsJoueur = compteJoueur(listeStatsJoueurs); //choisie le nom d'utilisateur (pseudonyme)

        Joueur joueur = loadStatsJoueur(listeStatsJoueurs, informationsJoueur[0], informationsJoueur[1]); //charge les statistiques du joueur

        text("white");
        println();

        //-------------jeu-------------\\

        while(true){
            clearScreen();
            affichageTitre(titreJeu);
            text("white");
            println();
            String choixMode = choisirMode(listeStatsJoueurs, joueur); //choisie le mode de jeu / la difficulté

            if(equals(choixMode, "6")){
                leaderBoardMenu();
            }else{
                StatsPartie pointsEtBonnesRep = lancerPartie(choixMode, informationsJoueur[0]); //commencement de la partie

                //-------------actualisation nouveau meilleur score si ancien meilleur score battu-------------\\
            
                joueur = actualisationMeilleurScore(choixMode, joueur, pointsEtBonnesRep);

                clearScreen();

                finDeLaPartie(listeStatsJoueurs, pointsEtBonnesRep, joueur, choixMode); //affichage du score final
            }
        }
    
    }

    //fonction qui actualise le meilleur score du joueur en fonction du mode de jeu joué


    Joueur actualisationMeilleurScore(String choixMode, Joueur joueur, StatsPartie pointsEtBonnesRep){
        if(equals(choixMode, "1")){
            if(estPLusGrand("" + pointsEtBonnesRep.score, joueur.meilleurScoreFacile)){
                joueur.meilleurScoreFacile = "" + pointsEtBonnesRep.score;
            }
        }
        else if(equals(choixMode, "2")){
            if(estPLusGrand("" + pointsEtBonnesRep.score, joueur.meilleurScoreNormal)){
                joueur.meilleurScoreNormal = "" + pointsEtBonnesRep.score;
            }
        }
        else if(equals(choixMode, "3")){
            if(estPLusGrand("" + pointsEtBonnesRep.score, joueur.meilleurScoreDifficile)){
                joueur.meilleurScoreDifficile = "" + pointsEtBonnesRep.score;
            }
        }
        else if(equals(choixMode, "4")){
            if(estPLusGrand("" + pointsEtBonnesRep.score, joueur.meilleurScoreMortSubite)){
                joueur.meilleurScoreMortSubite = "" + pointsEtBonnesRep.score;
            }
        }

        return joueur;
    }





// fonction permettant de se connecter à son compte ou de s'inscrire si cela n'est pas déjà fait

    String[] compteJoueur(CSVFile listeStatsJoueur){

        String[] resultat = new String[2];

        text("yellow");
        println("Bienvenu(e) sur SpaceOfQuestions !");
        text("white");
        println();

        int choixDuService = 0;
        boolean choixCorrect = false;

        while(!choixCorrect){
            println("1 - Inscription");
            println("2 - Connexion");
            println();

            choixDuService = readInt();

            if(choixDuService > 0 && choixDuService < 3){
                choixCorrect = true;
            }

        }

        if(choixDuService == 1){
            resultat = inscription(listeStatsJoueur);
        }
        else{
            resultat = connexion(listeStatsJoueur);
        }
            
        println();
        text("yellow");
        println("Enchanté " + resultat[0]);
        println();

        return resultat;
    }


// fonction permettant de vérifier qu'un compte existe ou non


    void testCompteExistant(){

        CSVFile CSVTest = chargerCSV("ressources/CSV/statsJoueurs.csv");  

        assertTrue(compteExistant(CSVTest, "Léo", "leo59"));
        assertFalse(compteExistant(CSVTest, "anonymous", "anonymous"));
    }


    boolean compteExistant(CSVFile file, String nomUtilisateur, String password){
        String[][] tab = CSVAsTab(file);

        boolean estTrouve = false;

        int nbLigne = length(tab, 1);
        for(int l = 0; l < nbLigne && !estTrouve; l++){
            if(equals(tab[l][0], nomUtilisateur) && equals(tab[l][1], password)){
                estTrouve = true;
            }
        }

        return estTrouve;
    }


// fonction permettant de s'inscire / créer un nouveau compte

    String[] inscription(CSVFile listeStatsJoueur){

        text("yellow");
        println("Inscription:");
        println();

        boolean estCorrect = false;

        String[] identifiants = new String[]{"", ""};  // nom d'utilisateur, mot de passe

        while(!estCorrect){

            saisieIdentifiants(identifiants);

            if(!equals(identifiants[0], "") && !equals(identifiants[1], "") && !compteExistant(listeStatsJoueur, identifiants[0], identifiants[1])){
                estCorrect = true;
            }
            else{
                text("red");
                println("Nom d'utilisateur ou mot de passe incorrect, veuillez modifier ces informations");
            }

        }

        Joueur joueur = new Joueur();
        joueur.nomUtilisateur = identifiants[0];
        joueur.mdp = identifiants[1];
        joueur.meilleurScoreFacile = "0";
        joueur.meilleurScoreNormal = "0";
        joueur.meilleurScoreDifficile = "0";
        joueur.meilleurScoreMortSubite = "0";

        saveStatsJoueur(listeStatsJoueur, joueur, "-1");

        return identifiants;
    }


    //fonction permettant de saisir nos identifiants lors de la création d'un nouveau compte ou lors d'une connexion à un compte déjà existant

    void saisieIdentifiants(String[] identifiants){
        text("blue");
        println("Nom d'utilisateur: ");
        text("white");
        identifiants[0] = readString();
        println();
        text("blue");
        println("Mot de passe: ");
        text("white");
        identifiants[1] = readString();
        println();
    }



// fonction permettant de se connecter à son compte


    String[] connexion(CSVFile listeStatsJoueur){

        text("yellow");
        println("Connexion:");
        println();

        boolean estCorrect = false;

        String[] identifiants = new String[]{"", ""};  // nom d'utilisateur, mot de passe

        while(!estCorrect){
            saisieIdentifiants(identifiants);

            if(!equals(identifiants[0], "") && !equals(identifiants[1], "") && compteExistant(listeStatsJoueur, identifiants[0], identifiants[1])){
                estCorrect = true;
            }
            else{
                text("red");
                println("Nom d'utilisateur ou mot de passe incorrect, veuillez modifier ces informations");
            }

        }

        return identifiants;
    }


        

// fonction permettant d'afficher le titre du jeu

    void affichageTitre(String titre){
        text("purple");
        println(titre);
        text("white");
    }

    
// fonction permettant de choisir le mode de jeu
    
    String choisirMode(CSVFile listeStatsJoueurs, Joueur joueur){
        String choixDuMode;
        boolean choixCorrect = false;

        afficherDifficultésDisponibles(joueur);

        do{
            println();
            print("Veuillez entrez votre choix : ");
            choixDuMode = readString();
            if(!equals(choixDuMode, "")){
                if(charAt(choixDuMode, 0) < '1' || charAt(choixDuMode, 0) > '6' || length(choixDuMode) > 1){
                    println("Veuillez entrez un choix valide 1,2,3,4,5 ou 6");
                }
                else{
                    choixCorrect = true;
                }
            }
        }while(!choixCorrect);
        
        /*choix doit être un String si l'on souhaite effectuer un controle de saisie,
        car si l'on utilise un int, readInt() renvois une erreur lorsque l'on entre une 
        chaîne de caractère. */
            
        clearScreen();

        print("Vous avez choisis : ");

        afficherModeChoisie(choixDuMode);

        return choixDuMode;
    }


// fonction qui affiche les difficultés disponibles

    void afficherDifficultésDisponibles(Joueur joueur){
        println("| Veuillez choisir une difficulté |");
        println();
        text("green");
        println("       1 - Facile        " + "(Meilleur score: " + joueur.meilleurScoreFacile + ")");
        text("yellow");
        println("       2 - Normal        " + "(Meilleur score: " + joueur.meilleurScoreNormal + ")");
        text("red");
        println("       3 - Difficile     " + "(Meilleur score: " + joueur.meilleurScoreDifficile + ")");
        text("purple");
        println("       4 - MORT SUBITE   " + "(Meilleur score: " + joueur.meilleurScoreMortSubite + ")");
        println();
        text("white");
        println("--------------------");
        println();
        text("blue");
        println("       5 - Mode entraînement");
        println();
        text("white");
        println("--------------------");
        println();
        text("blue");
        println("       6 - LeaderBoard");
        println();
        text("white");
        println("--------------------");
    }


// fonction qui affiche le mode choisie

    void afficherModeChoisie(String choixDuMode){
        if(equals(choixDuMode, "1")){
            text("green");
            print("*Facile*");
        }else if(equals(choixDuMode, "2")){
            text("yellow");
            print("**Normal**");
        }else if(equals(choixDuMode, "3")){
            text("red");
            print("***Difficile***");
        }else if(equals(choixDuMode, "4")){
            text("purple");
            print("/!\\MORT SUBITE/!\\");
        }else if(equals(choixDuMode, "5")){
            text("blue");
            print("<< Entraînement >>");
        }else if(equals(choixDuMode, "6")){
            text("blue");
            print("|| Leaderboard ||");
        }
        

        println();
    }


    //fonction permettant la partie et de jouer au jeu selon le mode selectionné

    StatsPartie lancerPartie(String modeDeJeu, String nomJoueur){

        text("yellow");
        println("Bienvenu(e) dans votre nouvelle fusée " + nomJoueur + ", prêt(e) à embarquer pour une nouvelle aventure?");
        println();

        StatsPartie pointsEtBonnesRep = new StatsPartie();

        if(equals(modeDeJeu, "1")){
            pointsEtBonnesRep = jouerPartie(nomJoueur, 10);
        }
        else if(equals(modeDeJeu, "2")){
            pointsEtBonnesRep = jouerPartie(nomJoueur, 6);
        }
        else if(equals(modeDeJeu, "3")){
            pointsEtBonnesRep = jouerPartie(nomJoueur, 3);
        }
        else if(equals(modeDeJeu, "4")){
            pointsEtBonnesRep = jouerPartie(nomJoueur, 1);
        }
        else if(equals(modeDeJeu, "5")){
            pointsEtBonnesRep = jouerPartieEntrainement(nomJoueur);
        }

        return pointsEtBonnesRep;
    }



    //fonction qui retourne le score du joueur ainsi que son nombre de bonnes réponses (mode entrainement)

    StatsPartie jouerPartieEntrainement(String nomJoueur){

        StatsPartie pointsEtBonnesRep = new StatsPartie();
        String[] questEtRep;
        String reponseJoueur;
        int[] statsPartie = new int[]{0,0,0}; // pointsJoueur, bonneReponses, nombreQuestionsPosées
        CSVFile CSVquestEtRep = chargerCSV("ressources/CSV/qstrep.csv");
        

        do{
            afficherAvancementFusee(statsPartie[0]);
            questEtRep = choixQst(CSVquestEtRep);
            affichageAvantQuestionEntrainement(statsPartie, questEtRep);
            reponseJoueur = readString();
            if(!equals(reponseJoueur, "stop")){
                clearScreen();
                statsPartie[2] = statsPartie[2] + 1;
                if(equals(toLowerCase(reponseJoueur), toLowerCase(questEtRep[1]))){
                text("green");
                println("Bien joué(e)! La réponse était évidemment: " + questEtRep[1]);
                println();
                statsPartie[0] = statsPartie[0] + 100;
                statsPartie[1] = statsPartie[1] + 1;
                }
                else{
                    text("red");
                    println("Perdu(e)! La réponse était: " + questEtRep[1]);
                    println();
                }
            }
            text("white");
        }while(!equals(reponseJoueur,"stop"));

        pointsEtBonnesRep.score = statsPartie[0];
        pointsEtBonnesRep.nbBonneRep = statsPartie[1];
        pointsEtBonnesRep.nombreQuestPosées = statsPartie[2];

        return pointsEtBonnesRep;
    }



    //fonction permettant d'afficher quelque information sur la question avant d'y répondre (mode entrainement)

    void affichageAvantQuestionEntrainement(int[] statsPartie, String[] questEtRep){
        text("blue");
        println("Répondez correctement à la question suivante pour avancer dans l'espace (entrez \"stop\" si vous souhaitez arrêter la partie):");
        println();
        text("yellow");
        println("Question " + (statsPartie[2] + 1));
        println();
        text("white");
        println(questEtRep[0]); //print la question
    }




    //fonction qui retourne le score du joueur ainsi que son nombre de bonnes réponses

    StatsPartie jouerPartie(String nomJoueur, int nombreDeVie){
        StatsPartie pointsEtBonnesRep = new StatsPartie();
        String[] questEtRep;
        String reponseJoueur;
        int[] statsPartie = new int[]{0,0,0, nombreDeVie}; // pointsJoueur, bonneReponses, nombreQuestionsPosées, nombreVieRestante
        CSVFile CSVquestEtRep = chargerCSV("ressources/CSV/qstrep.csv");
        do{
            boolean estBonus = (statsPartie[0] % 1500 == 0 && statsPartie[0] > 0);
            explicationAvantQuestion(statsPartie, estBonus);
            questEtRep = choixQst(CSVquestEtRep);
            if(estBonus){ //Questions Bonus
                println(questEtRep[0]); //print la question
                reponseJoueur = readString();
                if(!equals(reponseJoueur, "stop")){
                    clearScreen();
                    if(equals(toLowerCase(reponseJoueur), toLowerCase(questEtRep[1]))){
                    statsPartie[0] = statsPartie[0] + 500;
                    afficheResultatQuestion(questEtRep,statsPartie[3],true);
                    }else{
                        statsPartie[0] = statsPartie[0] + 100;
                        afficheResultatQuestion(questEtRep,statsPartie[3],false);
                    }
                }
            }else{ //Question Normale
                println(questEtRep[0]); //print la question
                reponseJoueur = readString();
                if(!equals(reponseJoueur, "stop")){
                    clearScreen();
                    statsPartie[2] = statsPartie[2] + 1;
                    if(equals(toLowerCase(reponseJoueur), toLowerCase(questEtRep[1]))){
                        afficheResultatQuestion(questEtRep, statsPartie[3], true);
                        statsPartie[0] = statsPartie[0] + 100;
                        statsPartie[1] = statsPartie[1] + 1;
                    }
                    else{
                        statsPartie[3] = statsPartie[3] - 1;
                        afficheResultatQuestion(questEtRep, statsPartie[3], false);
                    }
                }
                text("white");
            }
        }while(!equals(reponseJoueur,"stop") && statsPartie[3] > 0);
        pointsEtBonnesRep.score = statsPartie[0];
        pointsEtBonnesRep.nbBonneRep = statsPartie[1];
        pointsEtBonnesRep.nombreQuestPosées = statsPartie[2];

        return pointsEtBonnesRep;
    }



    //fonction permettant d'afficher le resultat de la question, indique également si le joueur à répondu correctement ou non à cette dernière

    void afficheResultatQuestion(String[] questEtRep, int vieRestante, boolean bonneOuMauvaiseReponse){ //bonneOuMauvaise permet de savoir si il faut afficher si la réponse est correcte ou non
        if(bonneOuMauvaiseReponse){//fonction permettant d'afficher quelque information sur la question avant d'y répondre
            text("green");
            println("Bien joué(e)! La réponse était évidemment: " + questEtRep[1]);
            println();
            println("Il vous reste " + vieRestante + " vies");
            println();
        }else{
            text("red");
            println("Perdu(e)! La réponse était: " + questEtRep[1]);
            if(vieRestante != 0){
                println("Il vous reste " + vieRestante + " vies");
            }
            println();
        }
    }



    //fonction permettant d'afficher quelque information sur la question avant d'y répondre

    void explicationAvantQuestion(int[] statsPartie, boolean estBonus){
            afficherAvancementFusee(statsPartie[0]);
            text("blue");
            println("Répondez correctement à la question suivante pour avancer dans l'espace (entrez \"stop\" si vous souhaitez arrêter la partie):");
            println();
            text("yellow");
            if(estBonus){
                println("Question Bonus (Pas de vie en moins en cas de mauvaise réponse !)");                    
            }else{
                println("Question " + (statsPartie[2] + 1));
            }
            println();
            text("white");
    }



//affiche l'avancement de la fusée

    void afficherAvancementFusee(int pointsJoueur){
        text("blue");
        String fusee = fileAsString("ressources/models/fuseeModel");
        String planeteTerre = fileAsString("ressources/models/TerreModel");
        String saturne = fileAsString("ressources/models/SaturneModel");
        String planetes = fileAsString("ressources/models/PlanetesModel");

        println("Vous êtes à " + pointsJoueur + "m d'altitude !");
        text("white");
        println(fusee);
        if(pointsJoueur == 0){
            println(planeteTerre);
        }
        else if((pointsJoueur % 1500) == 0){
            println(saturne);
        }
        else if((pointsJoueur % 500) == 0){
            println(planetes);
        }
    }


    //fonction de fin de partie permettant de sauvegarder les stats du joueur (notamment si le meilleur score dans un des mode à changé), ainsi que l'affichage des statistiques finales de la partie

    void finDeLaPartie(CSVFile file, StatsPartie pointsEtBonnesRep, Joueur joueur, String modeDeJeu){
        if(!equals(modeDeJeu, "5")){
            saveStatsJoueur(file, joueur, modeDeJeu);
        }
        text("yellow");
        println("Partie terminée!");
        text("white");
        println("Tu t'es bien débrouillé(e)! ton score est de " + pointsEtBonnesRep.score + " avec un totale de " + pointsEtBonnesRep.nbBonneRep + " bonnes réponses sur " + pointsEtBonnesRep.nombreQuestPosées + " questions répondues!");
    }





    //Choisis aléatoirement une question et la réponse qui lui est associée et la met dans un tableau de taille 2 [La question , La réponse]

    void testChoixQst(){
        String[] tabTest = new String[]{"test1","test2"};

        CSVFile CSVTest = chargerCSV("ressources/CSV/testFonction(choixQst).csv");  

        String[] qstRepTest = choixQst(CSVTest);

        assertEquals("test1", qstRepTest[0]);
        assertEquals("test2", qstRepTest[1]);
    }


    String[] choixQst(CSVFile file){
        String[] questRep = new String[2];

        int alea = (int) (random() * (rowCount(file)-1));
        questRep[0] = getCell(file, alea+1, 0);
        questRep[1] = getCell(file, alea+1, 1);

        return questRep;
    }
    


//sauvegarde les stats du joueur

    Joueur loadStatsJoueur(CSVFile file, String nomUtilisateur, String password){
        String[][] tab = CSVAsTab(file);

        Joueur joueur = new Joueur();

        boolean estTrouve = false;

        for(int l = 0; l < length(tab, 1) && !estTrouve; l++){
            if(equals(tab[l][0],nomUtilisateur) && equals(tab[l][1], password)){
                joueur.nomUtilisateur = tab[l][0];
                joueur.mdp = tab[l][1];
                joueur.meilleurScoreFacile = tab[l][2];
                joueur.meilleurScoreNormal = tab[l][3];
                joueur.meilleurScoreDifficile = tab[l][4];
                joueur.meilleurScoreMortSubite = tab[l][5];
                estTrouve = true;
            }
        }

        if(!estTrouve){
            joueur.nomUtilisateur = nomUtilisateur;
            joueur.mdp = password;
            joueur.meilleurScoreFacile = "0";
            joueur.meilleurScoreNormal = "0";
            joueur.meilleurScoreDifficile = "0";
            joueur.meilleurScoreMortSubite = "0";
        }

        return joueur;
    }



//fonction permettant de sauvegarder les stats du joueur

    void saveStatsJoueur(CSVFile file, Joueur joueur, String modeDeJeu){
        String[][] statsJoueur = CSVAsTab(file);

        boolean estTrouve = false;

        int nbLigne = length(statsJoueur, 1);
        for(int l = 0; l < nbLigne && !estTrouve; l++){
            if(equals(statsJoueur[l][0], joueur.nomUtilisateur) && equals(statsJoueur[l][1], joueur.mdp)){
                
                statsJoueur[l][2] = joueur.meilleurScoreFacile;
                statsJoueur[l][3] = joueur.meilleurScoreNormal;
                statsJoueur[l][4] = joueur.meilleurScoreDifficile;
                statsJoueur[l][5] = joueur.meilleurScoreMortSubite;

                saveCSV(statsJoueur, "ressources/CSV/statsJoueurs.csv");
                estTrouve = true;
            }
        }

        if(!estTrouve){
            String[][] newStatsJoueur = new String[rowCount(file) + 1][6];

            int nbLigneNewStatsJoueur = rowCount(file);
            for(int l = 0; l < nbLigneNewStatsJoueur; l++){
                for(int c = 0; c < columnCount(file); c++){
                    newStatsJoueur[l][c] = getCell(file,l,c);
                }
            }

            newStatsJoueur[length(statsJoueur, 1)][0] = joueur.nomUtilisateur;
            newStatsJoueur[length(statsJoueur, 1)][1] = joueur.mdp; 
            newStatsJoueur[length(statsJoueur, 1)][2] = joueur.meilleurScoreFacile; 
            newStatsJoueur[length(statsJoueur, 1)][3] = joueur.meilleurScoreNormal; 
            newStatsJoueur[length(statsJoueur, 1)][4] = joueur.meilleurScoreDifficile; 
            newStatsJoueur[length(statsJoueur, 1)][5] = joueur.meilleurScoreMortSubite; 
            saveCSV(newStatsJoueur, "ressources/CSV/statsJoueurs.csv");
        }
    }




    //fonction qui affiche le menu du leaderboard

    void leaderBoardMenu(){
        println();

        String choixDuLeaderboard = "";
        boolean choixCorrect = false;

        do{
            println();
            text("blue");
            println("Veuillez choisir le leaderBoard du mode de jeu à afficher: (tappez \"retour\" si vous souhaitez revenir au menu)");
            afficherChoixLeaderboards();
            choixDuLeaderboard = readString();
            if(!equals(choixDuLeaderboard, "")){
                if(equals(choixDuLeaderboard, "retour")){
                    choixCorrect = true;
                }
                else if(charAt(choixDuLeaderboard, 0) < '1' || charAt(choixDuLeaderboard, 0) > '4' || length(choixDuLeaderboard) > 1){
                    println("Veuillez entrez un choix valide 1,2,3 ou 4");
                }
                else{
                    choixCorrect = true;
                }
            }
        }while(!choixCorrect);

        if(!equals(choixDuLeaderboard, "retour")){
            afficherLeaderboard(choixDuLeaderboard);
        }
    }


    //fonction qui affiche les choix disponibles parmis les différents leaderboards

    void afficherChoixLeaderboards(){
        println();
        text("green");
        println("       1 - Mode facile");
        text("yellow");
        println("       2 - Mode normal");
        text("red");
        println("       3 - Mode difficile");
        text("purple");
        println("       4 - Mode MORT SUBITE");
        println();
        text("white");
    }





    //fonction qui affiche le leaderboard choisi

    void afficherLeaderboard(String leaderboardChoisi){
        CSVFile listeStatsJoueurs = chargerCSV("ressources/CSV/statsJoueurs.csv");
        String[][] listeJoueurs = CSVAsTab(listeStatsJoueurs);

        int colonne = choixLeaderboard(leaderboardChoisi);

        trierSurColonne(listeJoueurs, colonne);

        String choixDuJoueur = "";

        do{
            affichageAvantLeaderboard();

            int placement = 1;

            if(length(listeJoueurs,1) > 0){
                if(length(listeJoueurs,1) >= 11){
                    for(int ligne = length(listeJoueurs) - 2; ligne > length(listeJoueurs) - 12; ligne--){
                        choixCouleurLeaderboard(placement);
                        println(placement + " - " + listeJoueurs[ligne][0] + ": " + listeJoueurs[ligne][colonne]);
                        text("white");
                        println();
                        placement++;
                    }
                }
                else{
                    for(int ligne = length(listeJoueurs) - 2; ligne > -1; ligne--){
                        choixCouleurLeaderboard(placement);
                        println(placement + " - " + listeJoueurs[ligne][0] + ": " + listeJoueurs[ligne][colonne]);
                        text("white");
                        println();
                        placement++;
                    }
                }
                println();
                println("--------------------");
                println();

                println("Tappez \"retour\" pour revenir en arrière:");
                choixDuJoueur = readString();
            }
        }while(!equals(choixDuJoueur, "retour"));

        leaderBoardMenu();
    }



    int choixLeaderboard(String leaderboardChoisi){
        int colonne = 0;

        if(equals(leaderboardChoisi, "1")){
            colonne = 2;
        }
        else if(equals(leaderboardChoisi, "2")){
            colonne = 3;
        }
        else if(equals(leaderboardChoisi, "3")){
            colonne = 4;
        }
        else if(equals(leaderboardChoisi, "4")){
            colonne = 5;
        }
        
        return colonne;
    }



    //fonction affichant quelques informations avant l'affichage du leaderboard

    void affichageAvantLeaderboard(){
        clearScreen();
        println("--------------------");
        println();
        text("purple");
        println("Voici nos 10 meilleurs joueurs: ");
        println();
    }



    //fonction permettant de determiner la couleur du texte selon la place du joueurs concerné dans le leaderboard

    void choixCouleurLeaderboard(int placement){
        if(placement == 1){
            text("green");
        }else if(placement == 2){
            text("yellow");
        }else if(placement == 3){
            text("red");
        }else{
            text("blue");
        }
    }





//////////////////////////////////fonctions quelconques facilitant le développement//////////////////////////////////




//fonction permettant de déterminer si une chaîne de caractère est plus grande qu'une autre (au même effet qu'un tri)



    void afficherTab(String[][] tab){
        for(int l = 0; l < length(tab,1); l++){
            for(int c = 0; c < length(tab,2); c++){
                print(tab[l][c] + " | ");
            }
            println();
        }
    }



    //fonction de comparaison permettant de tester si une chaîne de caractère est plus grand ou nom qu'une autre (toute deux données en paramètres)

    void testEstPlusGrand(){
        assertTrue(estPLusGrand("1000", "500"));
        assertFalse(estPLusGrand("5000","10000"));
        assertFalse(estPLusGrand("1000","1000"));
    }



    boolean estPLusGrand(String s1, String s2){
        boolean plusGrand = false;

        if(length(s1) > length(s2)){
            return true;
        }
        else if(length(s1) < length(s2)){
            return false;
        }
        else{
            for(int i = 0; i < length(s1) && !plusGrand; i++){
                if(charAt(s1, i) > charAt(s2, i)){
                    plusGrand = true;
                }
            }
        }
        return plusGrand;
    }




//fonction qui convertie un CSV en tableau à 2 dimensions


    void testCSVAsTab(){
        CSVFile CSVTest = chargerCSV("ressources/CSV/testFonction(CSVAsTab).csv");
        String[][] tabTest = CSVAsTab(CSVTest);

        assertEquals("test1",tabTest[0][0]);
        assertEquals("test2",tabTest[0][1]);
        assertEquals("test3",tabTest[1][0]);
        assertEquals("test4",tabTest[1][1]);
    }


    String[][] CSVAsTab(CSVFile file){
        String[][] tab = new String[rowCount(file)][columnCount(file)];

        int nbLigne = rowCount(file);
        for(int l = 0; l < nbLigne; l++){
            for(int c = 0; c < columnCount(file); c++){
                tab[l][c] = getCell(file,l,c);
            }
        }

        return tab;
    }



    //fonction qui permute deux lignes d'un tableau

    void testPermuterLignes(){
        String[][] tabTest = new String[][]{{"test1", "test2"},{"test3", "test4"}};

        permuterLignes(tabTest, 0, 1);

        assertEquals("test3",tabTest[0][0]);
        assertEquals("test4",tabTest[0][1]);
        assertEquals("test1",tabTest[1][0]);
        assertEquals("test2",tabTest[1][1]);
    }


    void permuterLignes(String[][] produits, int ligneA, int ligneB) {
        String passerelle;
        for (int idxCol = 0; idxCol < length(produits, 2); idxCol = idxCol + 1) {
            passerelle = produits[ligneA][idxCol];
            produits[ligneA][idxCol] = produits[ligneB][idxCol];
            produits[ligneB][idxCol] = passerelle;
        }
    }



    //fonction qui tri un tableau selon une colonne donnée

    void testTrierSurColonne(){
        String[][] tabTest = new String[][]{{"ccc","ddd"},{"aaa","bbb"}};
        trierSurColonne(tabTest, 1); // trier sur la deuxième colonne

        // resultat attendu = ["aaa","bbb"]["ccc","ddd"]

        assertEquals("aaa", tabTest[0][0]);
        assertEquals("bbb", tabTest[0][1]);
        assertEquals("ccc", tabTest[1][0]);
        assertEquals("ddd", tabTest[1][1]);
    }



    void trierSurColonne(String[][] contenu, int colonne){
        for(int i = length(contenu)-1; i >= 0; i--){
            for(int j = 0; j<i; j++){
                if(estPLusGrand(contenu[j][colonne], contenu[j+1][colonne])){
                    permuterLignes(contenu, j, j+1);
                }
            }
            
        }
    }




    //fonction qui permet de récuperer le contenu d'un fichier et le transforme en chaine de caractères

    String fileAsString(String filename) {
        extensions.File file = new extensions.File(filename);
        String content = "";
        while (ready(file)) {
            content = content + readLine(file) + '\n';
        }
        return content;
    }



        //fonction qui charge un CSV

    CSVFile chargerCSV(String filname){
        return loadCSV(filname);
    }

    //fonction qui affiche / print le contenu d'un CSV

    void afficheCSV(CSVFile file){
        int nbLigne = rowCount(file);
        for(int l = 0; l < nbLigne; l++){
            for(int c = 0; c < columnCount(file); c++){
                print(getCell(file,l,c) + " " + "|" + " ");
            }
            println();
        }
    }
}