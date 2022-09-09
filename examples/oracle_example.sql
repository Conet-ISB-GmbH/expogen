-- <editor-fold desc="TEST_TABLE">
CREATE TABLE TEST_TABLE
(
    ID                     VARCHAR(128)      NOT NULL PRIMARY KEY,
    OFFICE_NUMBER          VARCHAR2(2 CHAR)  NOT NULL,
    KIND                   VARCHAR2(32 CHAR) NOT NULL,
    YEAR                   NUMBER            NOT NULL,
    MAX_NUMBER             number            NOT NULL,
    IS_VALID               NUMBER(1, 0) DEFAULT 1,
    ADMISSION_DATE         DATE              NOT NULL,
    ADMISSION_EDITOR_ID    VARCHAR(128)      NOT NULL,
    MODIFICATION_DATE      DATE,
    MODIFICATION_EDITOR_ID VARCHAR(128),
    UNIQUE (OFFICE_NUMBER, KIND, YEAR),
    CONSTRAINT fk_other_id FOREIGN KEY (ID) REFERENCES OTHER_TABLE (ID),
);
-- </editor-fold>
