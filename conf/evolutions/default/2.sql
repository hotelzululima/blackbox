# boxes schema

# --- !Ups
ALTER TABLE "Boxes" ADD COLUMN created timestamp without time zone;

# --- !Downs

ALTER TABLE "Boxes" DROP COLUMN created;