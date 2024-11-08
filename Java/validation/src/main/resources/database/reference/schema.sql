
    create sequence artifact_seq start with 1 increment by 50;

    create sequence category_rule_seq start with 1 increment by 50;

    create sequence result_seq start with 1 increment by 50;

    create table artifact (
        id bigint not null,
        name varchar(255),
        type varchar(255) check (type in ('PACKAGE','RESOURCE')),
        content VARBINARY(MAX),
        primary key (id)
    );

    create table category (
        acceptable bit not null,
        severity smallint not null check (severity between 0 and 2),
        guidance varchar(255) not null,
        id varchar(255) not null,
        title varchar(255) not null,
        primary key (id)
    );

    create table category_rule (
        id bigint not null,
        timestamp datetime2(6) not null,
        category_id varchar(255) not null,
        model nvarchar(max) not null,
        primary key (id)
    );

    create table result (
        severity smallint not null check (severity between 0 and 4),
        type smallint check (type between 0 and 31),
        id bigint not null,
        expression varchar(4096) not null,
        location varchar(255),
        message varchar(max) not null,
        report_id varchar(255) not null,
        tenant_id varchar(255) not null,
        primary key (id)
    );

    alter table category_rule 
       add constraint FK6t0e0ex5fonwb30prx9qxilas 
       foreign key (category_id) 
       references category;
