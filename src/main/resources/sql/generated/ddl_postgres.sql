
    alter table duty 
        drop constraint FK_idy9l7mpe2wtbci3g9972920k;

    alter table event 
        drop constraint FK_lwfysucxfvfn3d017d8gdjlju;

    alter table event 
        drop constraint FK_rm91wdl0rv2avrwpqjytms7db;

    alter table event_roster_item 
        drop constraint FK_7vuh92lopp5lxp403iqmx5uv6;

    alter table event_roster_item 
        drop constraint FK_5uehwgocdx8kmlehwo4r4913q;

    alter table event_roster_item 
        drop constraint FK_7ua1lpamfoug5eg2ljydos5hn;

    alter table event_type 
        drop constraint FK_82sm2gk9j0hhfu297k1vtw10x;

    alter table event_type_duty 
        drop constraint FK_ci8qxraa2rpy0jywu67yn7eu9;

    alter table event_type_duty 
        drop constraint FK_qq3467u5brbc3f0y7pq6b3tjm;

    alter table person 
        drop constraint FK_gp8likk18nyhapwf23f7f1vba;

    alter table person_duty 
        drop constraint FK_oqrjyixrime1subqyulfrptwd;

    alter table person_duty 
        drop constraint FK_22rv0lnc3d47vilwenms8one7;

    alter table person_role 
        drop constraint FK_hefqmfkjf44xhk60kx0pts8p5;

    drop table if exists duty cascade;

    drop table if exists event cascade;

    drop table if exists event_roster_item cascade;

    drop table if exists event_type cascade;

    drop table if exists event_type_duty cascade;

    drop table if exists mail_msg cascade;

    drop table if exists organisation cascade;

    drop table if exists person cascade;

    drop table if exists person_duty cascade;

    drop table if exists person_role cascade;

    drop sequence duty_id_seq;

    drop sequence eri_id_seq;

    drop sequence event_id_seq;

    drop sequence eventtype_id_seq;

    drop sequence mail_id_seq;

    drop sequence org_id_seq;

    drop sequence perrole_id_seq;

    drop sequence person_duty_id_seq;

    drop sequence person_id_seq;

    create table duty (
        id bigint not null,
        active bit not null,
        description varchar(255),
        name varchar(255) not null,
        sortOrder integer not null,
        org_id bigint not null,
        primary key (id)
    );

    create table event (
        id bigint not null,
        approved bit,
        dateEvent date not null,
        name varchar(255),
        event_type_id bigint not null,
        org_id bigint not null,
        primary key (id)
    );

    create table event_roster_item (
        id bigint not null,
        duty_id bigint not null,
        event_id bigint not null,
        person_id bigint not null,
        primary key (id)
    );

    create table event_type (
        id bigint not null,
        active bit not null,
        description varchar(255),
        endTime time not null,
        interval varchar(255) not null,
        intervalDetail varchar(255),
        name varchar(255) not null,
        startTime time not null,
        org_id bigint not null,
        primary key (id)
    );

    create table event_type_duty (
        event_type_id bigint not null,
        duties_id bigint not null
    );

    create table mail_msg (
        id bigint not null,
        active bit,
        attachementX text,
        attachmentCount integer not null,
        bodyHtml text,
        bodyPlain text,
        contentIdMap text,
        fromAddress text,
        messageHeaders text,
        read bit,
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

    create table organisation (
        id bigint not null,
        dateCreated datetime not null,
        dateUpdated datetime not null,
        name varchar(255) not null,
        registrationCode varchar(255) not null,
        primary key (id)
    );

    create table person (
        id bigint not null,
        active bit not null,
        emailAddress varchar(255),
        lastUpdated datetime not null,
        nameFirst varchar(255) not null,
        nameLast varchar(255) not null,
        password varchar(60) not null,
        org_id bigint not null,
        primary key (id)
    );

    create table person_duty (
        id bigint not null,
        adjustedPreference integer not null,
        preference integer not null,
        duty_id bigint not null,
        person_id bigint not null,
        primary key (id)
    );

    create table person_role (
        id bigint not null,
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

    alter table duty 
        add constraint FK_idy9l7mpe2wtbci3g9972920k 
        foreign key (org_id) 
        references organisation;

    alter table event 
        add constraint FK_lwfysucxfvfn3d017d8gdjlju 
        foreign key (event_type_id) 
        references event_type;

    alter table event 
        add constraint FK_rm91wdl0rv2avrwpqjytms7db 
        foreign key (org_id) 
        references organisation;

    alter table event_roster_item 
        add constraint FK_7vuh92lopp5lxp403iqmx5uv6 
        foreign key (duty_id) 
        references duty;

    alter table event_roster_item 
        add constraint FK_5uehwgocdx8kmlehwo4r4913q 
        foreign key (event_id) 
        references event;

    alter table event_roster_item 
        add constraint FK_7ua1lpamfoug5eg2ljydos5hn 
        foreign key (person_id) 
        references person;

    alter table event_type 
        add constraint FK_82sm2gk9j0hhfu297k1vtw10x 
        foreign key (org_id) 
        references organisation;

    alter table event_type_duty 
        add constraint FK_ci8qxraa2rpy0jywu67yn7eu9 
        foreign key (duties_id) 
        references duty;

    alter table event_type_duty 
        add constraint FK_qq3467u5brbc3f0y7pq6b3tjm 
        foreign key (event_type_id) 
        references event_type;

    alter table person 
        add constraint FK_gp8likk18nyhapwf23f7f1vba 
        foreign key (org_id) 
        references organisation;

    alter table person_duty 
        add constraint FK_oqrjyixrime1subqyulfrptwd 
        foreign key (duty_id) 
        references duty;

    alter table person_duty 
        add constraint FK_22rv0lnc3d47vilwenms8one7 
        foreign key (person_id) 
        references person;

    alter table person_role 
        add constraint FK_hefqmfkjf44xhk60kx0pts8p5 
        foreign key (person_id) 
        references person;

    create sequence duty_id_seq;

    create sequence eri_id_seq;

    create sequence event_id_seq;

    create sequence eventtype_id_seq;

    create sequence mail_id_seq;

    create sequence org_id_seq;

    create sequence perrole_id_seq;

    create sequence person_duty_id_seq;

    create sequence person_id_seq;
