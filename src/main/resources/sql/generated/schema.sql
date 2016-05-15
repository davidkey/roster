create table duty (id bigint generated by default as identity, active boolean not null, description varchar(255), name varchar(255) not null, sort_order integer not null check (sort_order>=1), org_id bigint not null, primary key (id))
create table event (id bigint generated by default as identity, approved boolean, date_event date not null, name varchar(255), event_type_id bigint not null, org_id bigint not null, primary key (id))
create table event_roster_item (id bigint generated by default as identity, duty_id bigint not null, event_id bigint not null, person_id bigint not null, primary key (id))
create table event_type (id bigint generated by default as identity, active boolean not null, description varchar(255), end_time time not null, interval varchar(255) not null, interval_detail varchar(255), name varchar(255) not null, start_time time not null, org_id bigint not null, primary key (id))
create table event_type_duties (event_type_id bigint not null, duties_id bigint not null)
create table mail_msg (id bigint generated by default as identity, active boolean, attachementx text, attachment_count integer not null, body_html text, body_plain text, content_id_map text, from_address text, message_headers text, read boolean, recipient text, sender text, signature text, stripped_html text, stripped_signature text, stripped_text text, subject text, timestamp integer not null, timestamp_date timestamp, token text, primary key (id))
create table organisation (id bigint generated by default as identity, date_created timestamp not null, date_updated timestamp not null, name varchar(255) not null, registration_code varchar(255) not null, primary key (id))
create table person (id bigint generated by default as identity, active boolean not null, email_address varchar(255), last_updated timestamp not null, name_first varchar(255) not null, name_last varchar(255) not null, password varchar(60) not null, reset_token varchar(255), reset_token_expires timestamp, org_id bigint not null, primary key (id))
create table person_duty (id bigint generated by default as identity, adjusted_preference integer not null check (adjusted_preference>=-1 AND adjusted_preference<=10), preference integer not null check (preference>=-1 AND preference<=10), duty_id bigint not null, person_id bigint not null, primary key (id))
create table person_role (id bigint generated by default as identity, role varchar(255) not null, person_id bigint not null, primary key (id))
alter table duty add constraint UK_kr92dcjpsi5do98ud6v7ls4qr unique (name)
alter table event_roster_item add constraint UK_bha3ht6i2rebkp81q3tkcdx38 unique (event_id, duty_id, person_id)
alter table event_type add constraint UK_dunk58vf3o8hxdjjspls1jrl unique (name)
alter table person add constraint UK_pj3hiqd22t8tvqbh5i469wqnk unique (name_first, name_last, org_id)
alter table person_duty add constraint UK_fa6hsugxhnk4xr5s0k8fqnl7x unique (person_id, duty_id)
alter table duty add constraint FK_idy9l7mpe2wtbci3g9972920k foreign key (org_id) references organisation
alter table event add constraint FK_lwfysucxfvfn3d017d8gdjlju foreign key (event_type_id) references event_type
alter table event add constraint FK_rm91wdl0rv2avrwpqjytms7db foreign key (org_id) references organisation
alter table event_roster_item add constraint FK_7vuh92lopp5lxp403iqmx5uv6 foreign key (duty_id) references duty
alter table event_roster_item add constraint FK_5uehwgocdx8kmlehwo4r4913q foreign key (event_id) references event
alter table event_roster_item add constraint FK_7ua1lpamfoug5eg2ljydos5hn foreign key (person_id) references person
alter table event_type add constraint FK_82sm2gk9j0hhfu297k1vtw10x foreign key (org_id) references organisation
alter table event_type_duties add constraint FK_2v51vk2eln607keg983kny5cb foreign key (duties_id) references duty
alter table event_type_duties add constraint FK_kmakjqm68e2er62jhtv2yeks3 foreign key (event_type_id) references event_type
alter table person add constraint FK_gp8likk18nyhapwf23f7f1vba foreign key (org_id) references organisation
alter table person_duty add constraint FK_oqrjyixrime1subqyulfrptwd foreign key (duty_id) references duty
alter table person_duty add constraint FK_22rv0lnc3d47vilwenms8one7 foreign key (person_id) references person
alter table person_role add constraint FK_hefqmfkjf44xhk60kx0pts8p5 foreign key (person_id) references person