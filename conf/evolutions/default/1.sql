
# --- !Ups

CREATE TABLE IF NOT EXISTS userinfo(
    id              serial NOT NULL ,
    firstname       VARCHAR(254) ,
    middlename      VARCHAR(254) ,
    lastname        VARCHAR(254) ,
    mobilenumber    bigint,
    emailid         VARCHAR(254) PRIMARY KEY ,
    password        VARCHAR(254) ,
    gender          VARCHAR(254) ,
    age             INT ,
    isenabled       boolean,
    isadmin         boolean
    );

CREATE TABLE IF NOT EXISTS hobby(
    id              serial NOT NULL,
    hobbyname       VARCHAR(254) UNIQUE,
    PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS hobbytouser(
    useremailid      VARCHAR(254) REFERENCES userinfo (emailid),
    hobbyid          INT REFERENCES hobby(id),
     PRIMARY KEY(useremailid,hobbyid)
    );

CREATE TABLE IF NOT EXISTS assignment(
    id               serial NOT NULL,
    title            VARCHAR(254)  PRIMARY KEY,
    description      text
);


# --- !Downs
DROP TABLE hobbytouser;
DROP table userinfo;
DROP TABLE hobby;
DROP TABLE assignment;
