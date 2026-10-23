# SIRH

## Pages de test HTML/JS

Une interface de test du Bloc 3 est disponible dans
`src/main/resources/static/`. Elle permet de lister et créer des propositions
d'embauche, consulter leur historique, tester les actions d'acceptation,
refus ou expiration, et rechercher un employé.

Pour l'utiliser :

```bash
./mvnw spring-boot:run
```

Puis ouvrir <http://localhost:8080/> dans un navigateur. La page appelle les
endpoints `/api/propositions-embauche` et `/api/employes` sur la même origine.
La base PostgreSQL configurée dans `src/main/resources/application.properties`
doit être accessible.