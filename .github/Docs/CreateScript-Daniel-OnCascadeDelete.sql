DROP DATABASE IF EXISTS TouristattractionDB;
CREATE DATABASE TouristattractionDB DEFAULT CHARACTER SET utf8;
USE TouristattractionDB;

DROP TABLE IF EXISTS Attraction;
DROP TABLE IF EXISTS Location;
DROP TABLE IF EXISTS AttractionTag;
DROP TABLE IF EXISTS Tag;


CREATE TABLE Location (
    postalcode INT,
    city VARCHAR(50),
    PRIMARY KEY (postalcode)            
);

CREATE TABLE Attraction (
    attractionID INT AUTO_INCREMENT,        
    attractionName VARCHAR(50) NOT NULL,
    attractionDesc VARCHAR(400) NOT NULL,
    postalcode INT,
    PRIMARY KEY (attractionID),
    FOREIGN KEY (postalcode) REFERENCES Location(postalcode)
);

CREATE TABLE Tag (
    tagID INT AUTO_INCREMENT,
    tagName VARCHAR(50),
    PRIMARY KEY (tagID)            
);

CREATE TABLE AttractionTag (
    attractionID INT,
    tagID INT,
    PRIMARY KEY (attractionID, tagID),
    FOREIGN KEY (attractionID) REFERENCES Attraction(attractionID) ON DELETE CASCADE,
    FOREIGN KEY (tagID) REFERENCES Tag(tagID)
);