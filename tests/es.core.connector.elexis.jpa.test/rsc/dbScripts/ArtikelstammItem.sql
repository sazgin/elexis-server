CREATE TABLE IF NOT EXISTS "ARTIKELSTAMM_CH" (
  ID VARCHAR(25) primary key,
  lastupdate BIGINT,
  deleted CHAR(1) default '0',
  TYPE char(1),
  BB char(1),
  CUMM_VERSION char(4),
  GTIN varchar(14),
  PHAR char(7),
  DSCR varchar(50),
  ADDDSCR varchar(50),
  ATC char(10),
  COMP_GLN char(13),
  COMP_NAME varchar(255),
  PEXF char(10),
  PPUB char(10),
  PKG_SIZE char(6),
  SL_ENTRY char(1),
  IKSCAT char(1),
  LIMITATION char(1),
  LIMITIATION_PTS char(4),
  LIMITATION_TXT longtext,
  GENERIC_TYPE char(1),
  HAS_GENERIC char(1),
  LPPV char(1),
  DEDUCTIBLE char(6),
  NARCOTIC char(1),
  NARCOTIC_CAS varchar(20),
  VACCINE char(1),
  LIEFERANTID varchar(25),
  MAXBESTAND varchar(4),
  MINBESTAND varchar(4),
  ISTBESTAND varchar(4),
  VERKAUFSEINHEIT varchar(4),
  ANBRUCH varchar(4),
  PRODNO varchar(10),
  EXTINFO longblob,
  PRIMARY KEY ("ID")
);
CREATE INDEX idxAiPHAR ON ARTIKELSTAMM_CH (PHAR);
CREATE INDEX idxAiITEMTYPE ON ARTIKELSTAMM_CH (TYPE);
CREATE INDEX idxAiGTIN ON ARTIKELSTAMM_CH (GTIN);
CREATE INDEX idxAiMONTH ON ARTIKELSTAMM_CH (CUMM_VERSION);
CREATE INDEX idxAiBB ON ARTIKELSTAMM_CH (BB);
INSERT INTO ARTIKELSTAMM_CH (ID, GTIN, NARCOTIC_CAS, PEXF, PPUB) VALUES ('VERSION','1.3.0','280716 15:21',0,0);