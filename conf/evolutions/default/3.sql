# vehicles schema

# --- !Ups
CREATE TABLE "Vehicles"
(
  mac text,
  "dronekitVehicle" uuid,
  created timestamp without time zone,
  id uuid NOT NULL,
  CONSTRAINT vehicles_pk PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);

# --- !Downs

DROP TABLE "Vehicles";