# boxes schema

# --- !Ups
CREATE TABLE "Boxes"
(
  title text,
  "dronekitMission" uuid,
  id uuid NOT NULL,
  CONSTRAINT boxes_pk PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);

# --- !Downs

DROP TABLE "Boxes";