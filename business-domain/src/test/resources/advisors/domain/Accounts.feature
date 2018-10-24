#language: fr

Fonctionnalité: Gestion des comptes

  Scénario: Récupération initiale des comptes
    Quand je récupère la liste des comptes
    Alors la liste contient 0 compte

  Scénario: Création d'un compte
    Quand je crée le compte "FR76001"
    Et je récupère la liste des comptes
    Alors la liste contient 1 compte
#    Et le solde du compte "FR76001" est 0
    Et le solde du compte "FR76001" est 0.0
