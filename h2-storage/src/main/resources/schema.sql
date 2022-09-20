create table assembly_order
(
    id INTEGER GENERATED BY DEFAULT AS IDENTITY(START WITH 4) PRIMARY KEY,
    shop_id varchar(255),
    goods_id varchar(255),
    quantity integer,
    status varchar(255),
    assembler varchar(255),
    metadata varchar(10000),
    realization_date date
);

create table assembly_user
(
    user_id INTEGER GENERATED BY DEFAULT AS IDENTITY(START WITH 4) PRIMARY KEY,
    role varchar(255),
    name varchar(255),
    password varchar(255)
);
