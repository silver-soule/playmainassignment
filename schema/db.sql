

CREATE TABLE IF NOT EXISTS userinfo(
    id              serial NOT NULL PRIMARY KEY,
    name            VARCHAR(254) ,
    middlename      VARCHAR(254) ,
    lastname        VARCHAR(254) ,
    mobilenumber    bigint,
    emailid         VARCHAR(254) ,
    password        VARCHAR(254) ,
    gender          VARCHAR(254) ,
    age             INT
    );