
    alter table event 
        drop 
        foreign key FK_lwfysucxfvfn3d017d8gdjlju;

    alter table event_roster_item 
        drop 
        foreign key FK_7vuh92lopp5lxp403iqmx5uv6;

    alter table event_roster_item 
        drop 
        foreign key FK_5uehwgocdx8kmlehwo4r4913q;

    alter table event_roster_item 
        drop 
        foreign key FK_7ua1lpamfoug5eg2ljydos5hn;

    alter table event_type_duty 
        drop 
        foreign key FK_ci8qxraa2rpy0jywu67yn7eu9;

    alter table event_type_duty 
        drop 
        foreign key FK_qq3467u5brbc3f0y7pq6b3tjm;

    alter table person_duty 
        drop 
        foreign key FK_oqrjyixrime1subqyulfrptwd;

    alter table person_duty 
        drop 
        foreign key FK_22rv0lnc3d47vilwenms8one7;

    alter table person_role 
        drop 
        foreign key FK_hefqmfkjf44xhk60kx0pts8p5;

    drop table if exists duty;

    drop table if exists event;

    drop table if exists event_roster_item;

    drop table if exists event_type;

    drop table if exists event_type_duty;

    drop table if exists mail_msg;

    drop table if exists person;

    drop table if exists person_duty;

    drop table if exists person_role;

    create table duty (
        id bigint not null auto_increment,
        active bit not null,
        description varchar(255),
        name varchar(255) not null,
        sortOrder integer not null,
        primary key (id)
    );

    create table event (
        id bigint not null auto_increment,
        approved bit not null,
        dateEvent date not null,
        name varchar(255),
        event_type_id bigint not null,
        primary key (id)
    );

    create table event_roster_item (
        id bigint not null auto_increment,
        duty_id bigint not null,
        event_id bigint not null,
        person_id bigint not null,
        primary key (id)
    );

    create table event_type (
        id bigint not null auto_increment,
        active bit not null,
        description varchar(255),
        endTime time not null,
        interval varchar(255) not null,
        intervalDetail varchar(255),
        name varchar(255) not null,
        startTime time not null,
        primary key (id)
    );

    create table event_type_duty (
        event_type_id bigint not null,
        duties_id bigint not null
    );

    create table mail_msg (
        id bigint not null auto_increment,
        attachementX text,
        attachmentCount integer not null,
        bodyHtml text,
        bodyPlain text,
        contentIdMap text,
        fromAddress text,
        messageHeaders text,
        recipient text,
        sender text,
        signature text,
        strippedHtml text,
        strippedSignature text,
        strippedText text,
        subject text,
        timestamp integer not null,
        timestampDate datetime,
        token text,
        primary key (id)
    );

    create table person (
        id bigint not null auto_increment,
        active bit not null,
        emailAddress varchar(255),
        lastUpdated datetime not null,
        nameFirst varchar(255) not null,
        nameLast varchar(255) not null,
        password varchar(60) not null,
        primary key (id)
    );

    create table person_duty (
        id bigint not null auto_increment,
        adjustedPreference integer not null,
        preference integer not null,
        duty_id bigint not null,
        person_id bigint not null,
        primary key (id)
    );

    create table person_role (
        id bigint not null auto_increment,
        role varchar(255) not null,
        person_id bigint not null,
        primary key (id)
    );

    alter table duty 
        add constraint UK_kr92dcjpsi5do98ud6v7ls4qr  unique (name);

    alter table event_roster_item 
        add constraint UK_bha3ht6i2rebkp81q3tkcdx38  unique (event_id, duty_id, person_id);

    alter table event_type 
        add constraint UK_dunk58vf3o8hxdjjspls1jrl  unique (name);

    alter table person 
        add constraint UK_16j3oat1osvcnt3kgq1dwyrxp  unique (nameFirst, nameLast);

    alter table event 
        add constraint FK_lwfysucxfvfn3d017d8gdjlju 
        foreign key (event_type_id) 
        references event_type (id);

    alter table event_roster_item 
        add constraint FK_7vuh92lopp5lxp403iqmx5uv6 
        foreign key (duty_id) 
        references duty (id);

    alter table event_roster_item 
        add constraint FK_5uehwgocdx8kmlehwo4r4913q 
        foreign key (event_id) 
        references event (id);

    alter table event_roster_item 
        add constraint FK_7ua1lpamfoug5eg2ljydos5hn 
        foreign key (person_id) 
        references person (id);

    alter table event_type_duty 
        add constraint FK_ci8qxraa2rpy0jywu67yn7eu9 
        foreign key (duties_id) 
        references duty (id);

    alter table event_type_duty 
        add constraint FK_qq3467u5brbc3f0y7pq6b3tjm 
        foreign key (event_type_id) 
        references event_type (id);

    alter table person_duty 
        add constraint FK_oqrjyixrime1subqyulfrptwd 
        foreign key (duty_id) 
        references duty (id);

    alter table person_duty 
        add constraint FK_22rv0lnc3d47vilwenms8one7 
        foreign key (person_id) 
        references person (id);

    alter table person_role 
        add constraint FK_hefqmfkjf44xhk60kx0pts8p5 
        foreign key (person_id) 
        references person (id);
