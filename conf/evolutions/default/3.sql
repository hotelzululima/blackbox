# users and roles schemas

# --- !Ups

CREATE TABLE "Roles"
(
  id serial NOT NULL,
  name text,
  CONSTRAINT "Roles_pk" PRIMARY KEY (id)
)
WITH (
  OIDS=FALSE
);

INSERT INTO "Roles" (name) VALUES ('end-user');
INSERT INTO "Roles" (name) VALUES ('agent');
INSERT INTO "Roles" (name) VALUES ('admin');

CREATE TABLE "Users"
(
  email text,
  role_id integer,
  CONSTRAINT users_pk PRIMARY KEY (email),
  CONSTRAINT "Users_role_id_fk" FOREIGN KEY (role_id)
      REFERENCES "Roles" (id) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);

# --- !Downs

DROP TABLE "Users";
DROP TABLE "Roles";