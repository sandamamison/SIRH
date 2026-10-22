-- ============================================================================
-- SIRH - Donnees de test Bloc 5
-- Pre-requis : bloc1.sql + bloc1_donnees_test.sql + bloc5.sql
-- ============================================================================

-- Structure : services dans les departements du Bloc 1 (1=IT, 2=RH, 3=FIN)
INSERT INTO service(codeService, nomService, departementId) VALUES
    ('SVC-DEV',   'Developpement',        1),
    ('SVC-INFRA', 'Infrastructure',       1),
    ('SVC-REC',   'Recrutement',          2),
    ('SVC-COMPTA','Comptabilite',         3);

-- Sections dans les services
INSERT INTO section(codeSection, nomSection, serviceId) VALUES
    ('SEC-BACK',  'Backend',   1),
    ('SEC-FRONT', 'Frontend',  1),
    ('SEC-RES',   'Reseaux',   2),
    ('SEC-SOURC', 'Sourcing',  3);

-- Personnel rattache a chaque niveau de la structure (RG5-04 : une seule unite)
INSERT INTO employe(matricule, nom, prenom, email, telephone, dateEmbauche, departementId) VALUES
    ('EMP-0001', 'Rabe',     'Tovo',   'rabe.tovo@sirh.mg',    '0340000001', '2018-01-15', 1);

UPDATE departement SET employeResponsableId = 1 WHERE codeDepartement = 'IT';

INSERT INTO employe(matricule, nom, prenom, email, telephone, dateEmbauche, serviceId, employeManagerId) VALUES
    ('EMP-0002', 'Randria',  'Mialy',  'randria.m@sirh.mg',    '0340000002', '2019-04-01', 1, 1),
    ('EMP-0003', 'Rakoto',   'Solo',   'rakoto.s@sirh.mg',     '0340000003', '2020-09-14', 3, NULL),
    ('EMP-0006', 'Rasoa',    'Lala',   'rasoa.l@sirh.mg',      '0340000006', '2022-02-07', 4, NULL);

UPDATE service SET employeResponsableId = 2 WHERE codeService = 'SVC-DEV';

INSERT INTO employe(matricule, nom, prenom, email, telephone, dateEmbauche, sectionId, employeManagerId) VALUES
    ('EMP-0004', 'Andria',   'Naina',  'andria.n@sirh.mg',     '0340000004', '2021-06-21', 1, 2),
    ('EMP-0005', 'Hery',     'Misa',   'hery.m@sirh.mg',       '0340000005', '2023-03-13', 2, 2),
    ('EMP-0007', 'Vao',      'Kanto',  'vao.k@sirh.mg',        '0340000007', '2023-11-06', 4, 3);

UPDATE section SET employeResponsableId = 4 WHERE codeSection = 'SEC-BACK';
