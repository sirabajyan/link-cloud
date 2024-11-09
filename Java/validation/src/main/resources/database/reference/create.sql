
    create table artifact (
        id bigint identity not null,
        name varchar(255) not null,
        type varchar(255) not null check (type in ('PACKAGE','RESOURCE')),
        content varbinary(max) not null,
        primary key (id)
    );

    create table category (
        acceptable bit not null,
        guidance varchar(1000) not null,
        id varchar(255) not null,
        severity varchar(255) not null check (severity in ('ERROR','WARNING','INFORMATION')),
        title varchar(255) not null,
        primary key (id)
    );

    create table category_rule (
        id bigint identity not null,
        timestamp datetimeoffset(6) not null,
        category_id varchar(255) not null,
        model varchar(max) not null,
        primary key (id)
    );

    create table result (
        id bigint identity not null,
        expression varchar(1000),
        code varchar(255) not null check (code in ('INVALID','STRUCTURE','REQUIRED','VALUE','INVARIANT','SECURITY','LOGIN','UNKNOWN','EXPIRED','FORBIDDEN','SUPPRESSED','PROCESSING','NOTSUPPORTED','DUPLICATE','MULTIPLEMATCHES','NOTFOUND','DELETED','TOOLONG','CODEINVALID','EXTENSION','TOOCOSTLY','BUSINESSRULE','CONFLICT','TRANSIENT','LOCKERROR','NOSTORE','EXCEPTION','TIMEOUT','INCOMPLETE','THROTTLED','INFORMATIONAL','NULL')),
        location varchar(255),
        message varchar(max) not null,
        report_id varchar(255) not null,
        severity varchar(255) not null check (severity in ('FATAL','ERROR','WARNING','INFORMATION','NULL')),
        tenant_id varchar(255) not null,
        primary key (id)
    );

    alter table category_rule 
       add constraint fk_category_rule_category_id 
       foreign key (category_id) 
       references category;
