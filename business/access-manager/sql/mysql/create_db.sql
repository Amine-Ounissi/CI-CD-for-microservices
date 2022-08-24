-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: /home/azureuser/business/access-manager/src/main/resources/db/changelog.yml
-- Ran at: 8/18/22, 12:56 PM
-- Against: @jdbc:h2:mem:access-manager
-- Liquibase version: 3.8.5
-- *********************************************************************

-- Create Database Lock Table
CREATE TABLE DATABASECHANGELOGLOCK (ID INT NOT NULL, LOCKED BOOLEAN NOT NULL, LOCKGRANTED TIMESTAMP, LOCKEDBY VARCHAR(255), CONSTRAINT PK_DATABASECHANGELOGLOCK PRIMARY KEY (ID));

-- Initialize Database Lock Table
DELETE FROM DATABASECHANGELOGLOCK;

INSERT INTO DATABASECHANGELOGLOCK (ID, LOCKED) VALUES (1, FALSE);

-- Lock Database
UPDATE DATABASECHANGELOGLOCK SET LOCKED = TRUE, LOCKEDBY = 'value.internal.cloudapp.net (10.0.0.6)', LOCKGRANTED = '2022-08-18 12:56:34.417' WHERE ID = 1 AND LOCKED = FALSE;

-- Create Database Change Log Table
CREATE TABLE DATABASECHANGELOG (ID VARCHAR(255) NOT NULL, AUTHOR VARCHAR(255) NOT NULL, FILENAME VARCHAR(255) NOT NULL, DATEEXECUTED TIMESTAMP NOT NULL, ORDEREXECUTED INT NOT NULL, EXECTYPE VARCHAR(10) NOT NULL, MD5SUM VARCHAR(35), DESCRIPTION VARCHAR(255), COMMENTS VARCHAR(255), TAG VARCHAR(255), LIQUIBASE VARCHAR(20), CONTEXTS VARCHAR(255), LABELS VARCHAR(255), DEPLOYMENT_ID VARCHAR(10));

-- Changeset /home/azureuser/business/access-manager/src/main/resources/db/changelog.yml::create_db::oussema
CREATE TABLE greetings (id VARCHAR(36) NOT NULL, message VARCHAR(255));

INSERT INTO DATABASECHANGELOG (ID, AUTHOR, FILENAME, DATEEXECUTED, ORDEREXECUTED, MD5SUM, DESCRIPTION, COMMENTS, EXECTYPE, CONTEXTS, LABELS, LIQUIBASE, DEPLOYMENT_ID) VALUES ('create_db', 'oussema', '/home/azureuser/business/access-manager/src/main/resources/db/changelog.yml', NOW(), 1, '8:74cd732df5419944ae8f34a129323ec3', 'createTable tableName=greetings', '', 'EXECUTED', NULL, NULL, '3.8.5', '0827396857');

-- Release Database Lock
UPDATE DATABASECHANGELOGLOCK SET LOCKED = FALSE, LOCKEDBY = NULL, LOCKGRANTED = NULL WHERE ID = 1;

