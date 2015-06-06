
    drop table duty if exists;

    drop table event if exists;

    drop table event_roster_item if exists;

    drop table event_type if exists;

    drop table event_type_duty if exists;

    drop table person if exists;

    drop table person_duty if exists;

    drop table person_role if exists;

    create table duty (
        id bigint generated by default as identity,
        active bit not null,
        description varchar(255),
        name varchar(255) not null,
        sortOrder integer not null,
        primary key (id)
    );

    create table event (
        id bigint generated by default as identity,
        approved bit not null,
        dateEvent date not null,
        name varchar(255),
        event_type_id bigint not null,
        primary key (id)
    );

    create table event_roster_item (
        id bigint generated by default as identity,
        duty_id bigint not null,
        event_id bigint not null,
        person_id bigint not null,
        primary key (id)
    );

    create table event_type (
        id bigint generated by default as identity,
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

    create table person (
        id bigint generated by default as identity,
        active bit not null,
        emailAddress varchar(255),
        lastUpdated datetime not null,
        nameFirst varchar(255) not null,
        nameLast varchar(255) not null,
        password varchar(255) not null,
        primary key (id)
    );

    create table person_duty (
        id bigint generated by default as identity,
        adjustedPreference integer not null,
        preference integer not null,
        duty_id bigint not null,
        person_id bigint not null,
        primary key (id)
    );

    create table person_role (
        id bigint generated by default as identity,
        role varchar(255),
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
        add constraint UK_8awmj38lti0eu2c51nwdi75xt  unique (emailAddress);

    alter table event 
        add constraint FK_lwfysucxfvfn3d017d8gdjlju 
        foreign key (event_type_id) 
        references event_type;

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

    alter table event_type_duty 
        add constraint FK_ci8qxraa2rpy0jywu67yn7eu9 
        foreign key (duties_id) 
        references duty;

    alter table event_type_duty 
        add constraint FK_qq3467u5brbc3f0y7pq6b3tjm 
        foreign key (event_type_id) 
        references event_type;

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
