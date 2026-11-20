-- Données de test pour Blocs 8 à 12 avec les employés des photos

DO $$
DECLARE
    p_id BIGINT;
    e_id BIGINT;
    dep_id INT;
    pos_id INT;
BEGIN
    SELECT id INTO dep_id FROM departement LIMIT 1;

    -- 1. Employé Photo 6 & 8: 627/TNR (DRH)
    IF NOT EXISTS (SELECT 1 FROM employes WHERE matricule = '627/TNR') THEN
        INSERT INTO personnes(nom, prenom, email, telephone, adresse)
        VALUES ('ANDRIANARIVO', 'Hery', 'drh@sirh.local', '0340011122', 'Antananarivo')
        RETURNING id INTO p_id;

        INSERT INTO poste(codeposte, titre, descriptionmission, salairemin, salairemax, departementid)
        VALUES ('P-DRH', 'DRH', 'Directeur des Ressources Humaines', 200000, 2000000, dep_id)
        RETURNING id INTO pos_id;

        INSERT INTO employes(matricule, personneid, dateembauche, statut, departementid, numero_cnaps, classification, indice)
        VALUES ('627/TNR', p_id, '2011-03-25', 'ACTIF', dep_id, '345670000', 'HC', 1733)
        RETURNING id INTO e_id;

        INSERT INTO affectation(employeid, posteid, datedebut)
        VALUES (e_id, pos_id, '2011-03-25');

        INSERT INTO contrat(numerocontrat, employeid, typecontrat, datedebut, salairebrut, statut, employecreateurid, employesignataireid, datesignature)
        VALUES ('CTR-DRH-001', e_id, 'CDI', '2011-03-25', 262680, 'SIGNE', 1, 1, '2011-03-25');
    ELSE
        UPDATE employes SET numero_cnaps = '345670000', classification = 'HC', indice = 1733 WHERE matricule = '627/TNR';
    END IF;

    -- 2. Employé Photo 7: RAZAFY Albert
    IF NOT EXISTS (SELECT 1 FROM personnes WHERE nom = 'RAZAFY' AND prenom = 'Albert') THEN
        INSERT INTO personnes(nom, prenom, email, telephone, adresse)
        VALUES ('RAZAFY', 'Albert', 'albert.razafy@sirh.local', '0340022233', 'Antananarivo')
        RETURNING id INTO p_id;

        INSERT INTO poste(codeposte, titre, descriptionmission, salairemin, salairemax, departementid)
        VALUES ('P-AAC', 'Assistant Administratif et Commercial', 'Support commercial', 1000000, 4000000, dep_id)
        RETURNING id INTO pos_id;

        INSERT INTO employes(matricule, personneid, dateembauche, statut, departementid, numero_cnaps, classification, indice)
        VALUES ('1002/TNR', p_id, '2014-01-01', 'ACTIF', dep_id, '710.922.846.807', '5B', 1500)
        RETURNING id INTO e_id;

        INSERT INTO affectation(employeid, posteid, datedebut)
        VALUES (e_id, pos_id, '2014-01-01');

        INSERT INTO contrat(numerocontrat, employeid, typecontrat, datedebut, salairebrut, statut, employecreateurid, employesignataireid, datesignature)
        VALUES ('CTR-1002', e_id, 'CDI', '2014-01-01', 3000000, 'SIGNE', 1, 1, '2014-01-01');
    END IF;

    -- 3. Employé Photo 7 & 9: RAMAHAY Gilbert
    IF NOT EXISTS (SELECT 1 FROM personnes WHERE nom = 'RAMAHAY' AND prenom = 'Gilbert') THEN
        INSERT INTO personnes(nom, prenom, email, telephone, adresse)
        VALUES ('RAMAHAY', 'Gilbert', 'gilbert.ramahay@sirh.local', '0340033344', 'Antananarivo')
        RETURNING id INTO p_id;

        INSERT INTO poste(codeposte, titre, descriptionmission, salairemin, salairemax, departementid)
        VALUES ('P-CM', 'Coursier Magasinier', 'Logistique et courses', 200000, 500000, dep_id)
        RETURNING id INTO pos_id;

        INSERT INTO employes(matricule, personneid, dateembauche, statut, departementid, numero_cnaps, classification, indice)
        VALUES ('1003/TNR', p_id, '2014-03-01', 'ACTIF', dep_id, '730.727.004.484', '4A', 1200)
        RETURNING id INTO e_id;

        INSERT INTO affectation(employeid, posteid, datedebut)
        VALUES (e_id, pos_id, '2014-03-01');

        INSERT INTO contrat(numerocontrat, employeid, typecontrat, datedebut, salairebrut, statut, employecreateurid, employesignataireid, datesignature)
        VALUES ('CTR-1003', e_id, 'CDI', '2014-03-01', 250000, 'SIGNE', 1, 1, '2014-03-01');
    END IF;

    -- 4. Employé Photo 7: MANDRESY Alexandre
    IF NOT EXISTS (SELECT 1 FROM personnes WHERE nom = 'MANDRESY' AND prenom = 'Alexandre') THEN
        INSERT INTO personnes(nom, prenom, email, telephone, adresse)
        VALUES ('MANDRESY', 'Alexandre', 'alexandre.mandresy@sirh.local', '0340044455', 'Antananarivo')
        RETURNING id INTO p_id;

        INSERT INTO poste(codeposte, titre, descriptionmission, salairemin, salairemax, departementid)
        VALUES ('P-RAF', 'Responsable Administratif et Financier', 'Gestion financière', 300000, 1500000, dep_id)
        RETURNING id INTO pos_id;

        INSERT INTO employes(matricule, personneid, dateembauche, statut, departementid, numero_cnaps, classification, indice)
        VALUES ('1004/TNR', p_id, '2005-12-01', 'ACTIF', dep_id, '770.618.002.680', 'HC', 1800)
        RETURNING id INTO e_id;

        INSERT INTO affectation(employeid, posteid, datedebut)
        VALUES (e_id, pos_id, '2005-12-01');

        INSERT INTO contrat(numerocontrat, employeid, typecontrat, datedebut, salairebrut, statut, employecreateurid, employesignataireid, datesignature)
        VALUES ('CTR-1004', e_id, 'CDI', '2005-12-01', 300000, 'SIGNE', 1, 1, '2005-12-01');
    END IF;

END $$;
