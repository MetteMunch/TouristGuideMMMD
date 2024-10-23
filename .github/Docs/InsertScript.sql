USE TouristattractionDB;

INSERT IGNORE INTO Location (postalcode, city) VALUES (1000, 'København');
INSERT IGNORE INTO Location (postalcode, city) VALUES (2000, 'Frederiksberg');
INSERT IGNORE INTO Location (postalcode, city) VALUES (4000, 'Roskilde');
INSERT IGNORE INTO Location (postalcode, city) VALUES (5000, 'Odense');
INSERT IGNORE INTO Location (postalcode, city) VALUES (8000, 'Aarhus');
INSERT IGNORE INTO Location (postalcode, city) VALUES (9000, 'Aalborg');
INSERT IGNORE INTO Location (postalcode, city) VALUES (6700, 'Esbjerg');
INSERT IGNORE INTO Location (postalcode, city) VALUES (6760, 'Ribe');
INSERT IGNORE INTO Location (postalcode, city) VALUES (7400, 'Herning');
INSERT IGNORE INTO Location (postalcode, city) VALUES (2791, 'Dragør');

INSERT IGNORE INTO Attraction (attractionName, attractionDesc, postalcode) VALUES ('Tivoli', 'Forlystelsespark i centrum af KBH', 1000);
INSERT IGNORE INTO Attraction (attractionName, attractionDesc, postalcode) VALUES ('Frederiksberg Have', 'Åben park midt på Frederiksberg', 2000);
INSERT IGNORE INTO Attraction (attractionName, attractionDesc, postalcode) VALUES ('Københavns Museum', 'Museum i KBH der dækker over københavns historie', 1000);
INSERT IGNORE INTO Attraction (attractionName, attractionDesc, postalcode) VALUES ('Vikingemuseet', 'Vikingemuseet i Ribe', 6760);
INSERT IGNORE INTO Attraction (attractionName, attractionDesc, postalcode) VALUES ('Hollænderhallen', 'Danmarks flotteste idrætshal', 2791);
INSERT IGNORE INTO Attraction (attractionName, attractionDesc, postalcode) VALUES ('Aros', 'Kunstmuseum', 8000);
INSERT IGNORE INTO Attraction (attractionName, attractionDesc, postalcode) VALUES ('HC Andersens hus', 'Danmarks smukkeste hus', 5000);

INSERT IGNORE INTO Tag (tagName) VALUES ('Teater');
INSERT IGNORE INTO Tag (tagName) VALUES ('Restaurant');
INSERT IGNORE INTO Tag (tagName) VALUES ('Koncert');
INSERT IGNORE INTO Tag (tagName) VALUES ('Natur');
INSERT IGNORE INTO Tag (tagName) VALUES ('Museum');
INSERT IGNORE INTO Tag (tagName) VALUES ('Sport');
INSERT IGNORE INTO Tag (tagName) VALUES ('Park');
INSERT IGNORE INTO Tag (tagName) VALUES ('Forlystelse');
INSERT IGNORE INTO Tag (tagName) VALUES ('Design');
INSERT IGNORE INTO Tag (tagName) VALUES ('Arkitektur');
INSERT IGNORE INTO Tag (tagName) VALUES ('Monumenter');

INSERT IGNORE INTO AttractionTag (attractionID, tagID) VALUES (1, 7);
INSERT IGNORE INTO AttractionTag (attractionID, tagID) VALUES (1, 8);
INSERT IGNORE INTO AttractionTag (attractionID, tagID) VALUES (1, 2);
INSERT IGNORE INTO AttractionTag (attractionID, tagID) VALUES (2, 7);
INSERT IGNORE INTO AttractionTag (attractionID, tagID) VALUES (3, 5);
INSERT IGNORE INTO AttractionTag (attractionID, tagID) VALUES (4, 5);
INSERT IGNORE INTO AttractionTag (attractionID, tagID) VALUES (5, 6);
INSERT IGNORE INTO AttractionTag (attractionID, tagID) VALUES (6, 5);
INSERT IGNORE INTO AttractionTag (attractionID, tagID) VALUES (7, 5);