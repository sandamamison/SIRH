-- Comptes de demonstration. Mot de passe initial pour tous : password
-- A remplacer immediatement en environnement reel.
INSERT INTO personnes (nom, prenom, email)
VALUES ('Admin', 'Systeme', 'admin@sirh.local'),
       ('Direction', 'Generale', 'direction@sirh.local'),
       ('Ressources', 'Humaines', 'rh@sirh.local'),
       ('Manager', 'Departement', 'manager@sirh.local'),
       ('Candidat', 'Test', 'candidat@sirh.local')
ON CONFLICT (email) DO NOTHING;

INSERT INTO utilisateurs (username, passwordhash, personneid)
SELECT v.username,
       '$2b$10$fLTRuyR8XcyNUVnivMz4wusuWUI9IQdDBteQMu4lUeuVaYlux7Nym',
       p.id
FROM (VALUES ('admin', 'admin@sirh.local'),
             ('direction', 'direction@sirh.local'),
             ('rh', 'rh@sirh.local'),
             ('manager', 'manager@sirh.local'),
             ('candidat', 'candidat@sirh.local')) AS v(username, email)
         JOIN personnes p ON p.email = v.email
ON CONFLICT (username) DO UPDATE
    SET passwordhash = EXCLUDED.passwordhash,
        personneid   = EXCLUDED.personneid,
        actif        = TRUE;

INSERT INTO utilisateur_roles (utilisateurid, role)
SELECT u.id, v.role
FROM (VALUES ('admin', 'ADMIN'),
             ('direction', 'DIRECTION'),
             ('rh', 'RH'),
             ('manager', 'MANAGER'),
             ('candidat', 'CANDIDAT')) AS v(username, role)
         JOIN utilisateurs u ON u.username = v.username
ON CONFLICT (utilisateurid) DO UPDATE
    SET role = EXCLUDED.role;