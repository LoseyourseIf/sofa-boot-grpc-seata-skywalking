drop table if exists seata_order.orders;
drop table if exists seata_order.undo_log;
drop table if exists seata_pay.account;
drop table if exists seata_pay.undo_log;
drop table if exists seata_stock.product;
drop table if exists seata_stock.undo_log;

create table seata_order.orders
(
    id int auto_increment
        primary key,
    user_id int null,
    product_id int null,
    pay_amount decimal null,
    status varchar(100) null,
    add_time datetime default CURRENT_TIMESTAMP null,
    last_update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP
)
    ENGINE=InnoDB DEFAULT CHARSET=utf8;
create table seata_order.undo_log
(
    id bigint auto_increment
        primary key,
    branch_id bigint not null,
    xid varchar(100) not null,
    context varchar(128) not null,
    rollback_info longblob not null,
    log_status int not null,
    log_created datetime not null,
    log_modified datetime not null,
    constraint ux_undo_log
        unique (xid, branch_id)
)
    ENGINE=InnoDB DEFAULT CHARSET=utf8;

create table seata_pay.account
(
    id int auto_increment
        primary key,
    balance double null,
    last_update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP
)
    ENGINE=InnoDB DEFAULT CHARSET=utf8;

create table seata_pay.undo_log
(
    id bigint auto_increment
        primary key,
    branch_id bigint not null,
    xid varchar(100) not null,
    context varchar(128) not null,
    rollback_info longblob not null,
    log_status int not null,
    log_created datetime not null,
    log_modified datetime not null,
    constraint ux_undo_log
        unique (xid, branch_id)
)
    ENGINE=InnoDB DEFAULT CHARSET=utf8;

create table seata_stock.product
(
    id int auto_increment
        primary key,
    price double null,
    stock int null,
    last_update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP
)
    ENGINE=InnoDB DEFAULT CHARSET=utf8;

create table seata_stock.undo_log
(
    id bigint auto_increment
        primary key,
    branch_id bigint not null,
    xid varchar(100) not null,
    context varchar(128) not null,
    rollback_info longblob not null,
    log_status int not null,
    log_created datetime not null,
    log_modified datetime not null,
    constraint ux_undo_log
        unique (xid, branch_id)
)
    ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO seata_stock.product (id, price, stock, last_update_time) VALUES (1, 5, 10, '2022-04-01 12:36:10');
INSERT INTO seata_pay.account (id, balance, last_update_time) VALUES (1, 1, '2022-04-01 12:36:11');