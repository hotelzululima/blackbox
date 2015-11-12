# stories schema

# --- !Ups
CREATE TABLE "Stories"
(
  title text,
  "dronekitMedia" uuid,
  created timestamp without time zone,
  boxId uuid NOT NULL,
  id uuid NOT NULL,
  CONSTRAINT stories_pk PRIMARY KEY (id)
)
WITH (
OIDS=FALSE
);

# --- !Downs

DROP TABLE "Stories";