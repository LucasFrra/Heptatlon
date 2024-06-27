faire attention avec le javafx, 
il a été installé a la main,
j'ai modifié dans le passé
le sdk,
File > Project Structure > SDKs.
les dépendances du projet
File > Project Structure > Modules.
et les options d'exécution
Run > Edit Configurations.

Ajouter le JAR comme dépendance au module :

Va dans File > Project Structure > Modules.
Sélectionne ton module (par défaut, il devrait être nommé src ou le nom de ton projet).
Clique sur l'onglet Dependencies.
Clique sur le bouton + en haut à droite et sélectionne Library.
Sélectionne le JAR de MySQL Connector que tu as ajouté précédemment.
Clique sur OK.

on peut pas racheter 2 fois le meme article

si un client a plusieurs factures, ca affiche que la premiere (id facture)