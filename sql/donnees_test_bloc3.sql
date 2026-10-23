-- Données de test PostgreSQL pour le bloc 3.
-- À exécuter après sql/base_bloc3.sql.
-- Le script est réexécutable : les entrées sont identifiées par leur email,
-- leur candidature ou leur matricule.

DELETE
FROM historique_propositions;
DELETE
FROM propositions_embauche;
DELETE
FROM employes;
DELETE
FROM candidatures;
DELETE
FROM personnes;


DO
$$
    DECLARE
        personne_alice_id     BIGINT;
        personne_benoit_id    BIGINT;
        personne_claire_id    BIGINT;
        personne_david_id     BIGINT;
        candidature_alice_id  BIGINT;
        candidature_benoit_id BIGINT;
        candidature_claire_id BIGINT;
    BEGIN
        INSERT INTO personnes (nom, prenom, email, telephone, adresse)
        VALUES ('Martin', 'Alice', 'alice.martin.test@sirh.local',
                '0600000001', '10 rue des Lilas, Antananarivo')
        ON CONFLICT (email) DO UPDATE SET nom       = EXCLUDED.nom,
                                          prenom    = EXCLUDED.prenom,
                                          telephone = EXCLUDED.telephone,
                                          adresse   = EXCLUDED.adresse
        RETURNING id INTO personne_alice_id;

        INSERT INTO personnes (nom, prenom, email, telephone, adresse)
        VALUES ('Rakoto', 'Benoit', 'benoit.rakoto.test@sirh.local',
                '0600000002', '25 avenue de la Republique, Antananarivo')
        ON CONFLICT (email) DO UPDATE SET nom       = EXCLUDED.nom,
                                          prenom    = EXCLUDED.prenom,
                                          telephone = EXCLUDED.telephone,
                                          adresse   = EXCLUDED.adresse
        RETURNING id INTO personne_benoit_id;

        INSERT INTO personnes (nom, prenom, email, telephone, adresse)
        VALUES ('Rasoanaivo', 'Claire', 'claire.rasoanaivo.test@sirh.local',
                '0600000003', '8 rue du Marche, Antsirabe')
        ON CONFLICT (email) DO UPDATE SET nom       = EXCLUDED.nom,
                                          prenom    = EXCLUDED.prenom,
                                          telephone = EXCLUDED.telephone,
                                          adresse   = EXCLUDED.adresse
        RETURNING id INTO personne_claire_id;

        INSERT INTO candidatures (personne_id, statut)
        VALUES (personne_alice_id, 'RETENUE')
        ON CONFLICT DO NOTHING
        RETURNING id INTO candidature_alice_id;

        IF candidature_alice_id IS NULL THEN
            SELECT id
            INTO candidature_alice_id
            FROM candidatures
            WHERE personne_id = personne_alice_id
            ORDER BY id
            LIMIT 1;
        END IF;

        INSERT INTO candidatures (personne_id, statut)
        VALUES (personne_benoit_id, 'RETENUE')
        ON CONFLICT DO NOTHING
        RETURNING id INTO candidature_benoit_id;

        IF candidature_benoit_id IS NULL THEN
            SELECT id
            INTO candidature_benoit_id
            FROM candidatures
            WHERE personne_id = personne_benoit_id
            ORDER BY id
            LIMIT 1;
        END IF;

        INSERT INTO candidatures (personne_id, statut)
        VALUES (personne_claire_id, 'PRESELECTIONNEE')
        ON CONFLICT DO NOTHING
        RETURNING id INTO candidature_claire_id;

        IF candidature_claire_id IS NULL THEN
            SELECT id
            INTO candidature_claire_id
            FROM candidatures
            WHERE personne_id = personne_claire_id
            ORDER BY id
            LIMIT 1;
        END IF;

        INSERT INTO personnes (nom, prenom, email, telephone, adresse)
        VALUES ('Andriamihaja', 'David', 'david.andriamihaja.test@sirh.local',
                '0600000004', '12 rue des Jacarandas, Toamasina')
        ON CONFLICT (email) DO UPDATE SET nom       = EXCLUDED.nom,
                                          prenom    = EXCLUDED.prenom,
                                          telephone = EXCLUDED.telephone,
                                          adresse   = EXCLUDED.adresse
        RETURNING id INTO personne_david_id;

        IF NOT EXISTS (SELECT 1
                       FROM candidatures
                       WHERE personne_id = personne_david_id) THEN
            INSERT INTO candidatures (personne_id, statut)
            VALUES (personne_david_id, 'RETENUE');
        END IF;

        INSERT INTO propositions_embauche (candidature_id, statut, date_envoi, date_reponse, commentaire, created_by)
        VALUES (candidature_alice_id, 'ACCEPTEE',
                TIMESTAMP '2026-01-10 09:00:00',
                TIMESTAMP '2026-01-12 14:30:00',
                'Proposition acceptee par la candidate.',
                'rh-test')
        ON CONFLICT (candidature_id) DO NOTHING;

        INSERT INTO propositions_embauche (candidature_id, statut, date_envoi, date_reponse, commentaire, created_by)
        VALUES (candidature_benoit_id, 'REFUSEE',
                TIMESTAMP '2026-02-03 10:00:00',
                TIMESTAMP '2026-02-05 16:00:00',
                'Proposition refusee par le candidat.',
                'rh-test')
        ON CONFLICT (candidature_id) DO NOTHING;

        INSERT INTO propositions_embauche (candidature_id, statut, date_envoi, commentaire, created_by)
        VALUES (candidature_claire_id, 'ENVOYEE',
                TIMESTAMP '2026-03-01 08:30:00',
                'Proposition en attente de reponse.',
                'rh-test')
        ON CONFLICT (candidature_id) DO NOTHING;

        INSERT INTO employes (matricule, personne_id, date_embauche, statut)
        VALUES ('EMP-TEST-0001', personne_alice_id, DATE '2026-01-15', 'ACTIF')
        ON CONFLICT (matricule) DO NOTHING;

        INSERT INTO employes (matricule, personne_id, date_embauche, statut)
        VALUES ('EMP-TEST-0002', personne_benoit_id, DATE '2026-02-15', 'INACTIF')
        ON CONFLICT (matricule) DO NOTHING;
    END
$$;

SELECT p.id,
       p.nom,
       p.prenom,
       p.email,
       c.id      AS candidature_id,
       c.statut  AS candidature_statut,
       pe.id     AS proposition_id,
       pe.statut AS proposition_statut,
       e.id      AS employe_id,
       e.matricule,
       e.statut  AS employe_statut
FROM personnes p
         LEFT JOIN candidatures c ON c.personne_id = p.id
         LEFT JOIN propositions_embauche pe ON pe.candidature_id = c.id
         LEFT JOIN employes e ON e.personne_id = p.id
WHERE p.email LIKE '%.test@sirh.local'
ORDER BY p.id, c.id;
