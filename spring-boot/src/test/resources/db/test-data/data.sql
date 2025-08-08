CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

create schema if not exists tcla;

create table tcla.assessment
(
    id uuid not null,
    primary key (id)
);

create table tcla.questionnaire
(
    id                               uuid                           not null,
    external_questionnaire_id        varchar(255),
    external_questionnaire_is_public boolean                        not null,
    end_date                         timestamp(6) without time zone not null,
    start_date                       timestamp(6) without time zone not null,
    assessment_id                    uuid                           not null unique,
    primary key (id),
    constraint fk_tcla_assessment foreign key (assessment_id) references tcla.assessment
);

create table tcla.question
(
    id               uuid          not null,
    title            varchar(2048) not null,
    "order"          integer       not null,
    questionnaire_id uuid          not null,
    primary key (id),
    constraint fk_tcla_survey foreign key (questionnaire_id) references tcla.questionnaire
);

create table tcla.multiple_choice_question
(
    id uuid not null,
    primary key (id),
    constraint fk_tcla_question foreign key (id) references tcla.question
);

create table tcla.answer_option
(
    id                          uuid         not null,
    "order"                     integer      not null,
    value                       varchar(255) not null,
    multiple_choice_question_id uuid         not null,
    primary key (id),
    constraint fk_tcla_multiple_choice_question foreign key (multiple_choice_question_id) references tcla.multiple_choice_question
);

create table tcla.questionnaire_filling
(
    id               uuid not null,
    questionnaire_id uuid not null,
    primary key (id),
    constraint fk_tcla_survey foreign key (questionnaire_id) references tcla.questionnaire
);

create table tcla.answer
(
    id                       uuid not null,
    questionnaire_filling_id uuid not null,
    primary key (id),
    constraint fk_tcla_response foreign key (questionnaire_filling_id) references tcla.questionnaire_filling
);

create table tcla.multiple_choice_answer
(
    id               uuid not null,
    answer_option_id uuid not null,
    primary key (id),
    constraint fk_tcla_answer_option foreign key (answer_option_id) references tcla.answer_option,
    constraint fk_tcla_answer foreign key (id) references tcla.answer
);

create table tcla.tcl_driver
(
    id        uuid         not null,
    name      varchar(255) not null,
    parent_id uuid,
    primary key (id),
    constraint fk_tcla_tcl_driver foreign key (parent_id) references tcla.tcl_driver
);

create table tcla.tcl_driver_score
(
    id                uuid      not null,
    value             float(53) not null,
    tcl_assessment_id uuid      not null,
    tcl_driver_id     uuid      not null,
    primary key (id),
    constraint fk_tcla_assessment foreign key (tcl_assessment_id) references tcla.assessment,
    constraint fk_tcla_tcl_driver foreign key (tcl_driver_id) references tcla.tcl_driver
);

INSERT INTO tcla.tcl_driver
    (id, name, parent_id)
values ('0fc00659-527b-4ad3-b449-007c9bb82a67', 'Team Characteristics', null);

INSERT INTO tcla.tcl_driver
    (id, name, parent_id)
values ('9607872f-059d-41ec-9662-f17778e5bc3a', 'Team Composition', '0fc00659-527b-4ad3-b449-007c9bb82a67');

INSERT INTO tcla.tcl_driver
    (id, name, parent_id)
values ('3c2f2271-0551-454c-ac7b-74e2909b7159', 'Team Complexity', '9607872f-059d-41ec-9662-f17778e5bc3a');

INSERT INTO tcla.tcl_driver
    (id, name, parent_id)
values ('583d82c7-96a3-4b6f-a71e-e45f46a949f7', 'Team Competence', '9607872f-059d-41ec-9662-f17778e5bc3a');

INSERT INTO tcla.tcl_driver
    (id, name, parent_id)
values ('66f9073f-43e7-4362-848e-010f4f53722c', 'Member Roles', '0fc00659-527b-4ad3-b449-007c9bb82a67');

INSERT INTO tcla.tcl_driver
    (id, name, parent_id)
values ('4776965b-ae88-461f-bfe9-f2396d070fb0', 'Role Clarity', '66f9073f-43e7-4362-848e-010f4f53722c');

INSERT INTO tcla.tcl_driver
    (id, name, parent_id)
values ('adcbdd72-7727-4b3f-b630-3ef321fda248', 'Role Fit', '66f9073f-43e7-4362-848e-010f4f53722c');

INSERT INTO tcla.tcl_driver
    (id, name, parent_id)
values ('b0821f0c-5708-421c-a3b8-6204ae11af56', 'Role Load', '66f9073f-43e7-4362-848e-010f4f53722c');

INSERT INTO tcla.tcl_driver
    (id, name, parent_id)
values ('fffb132c-154b-4f70-9290-0a8302e25e63', 'Culture', '0fc00659-527b-4ad3-b449-007c9bb82a67');

INSERT INTO tcla.tcl_driver
    (id, name, parent_id)
values ('9936e560-1b43-4df1-8f33-ccee53609e08', 'Team Alignment', 'fffb132c-154b-4f70-9290-0a8302e25e63');

INSERT INTO tcla.tcl_driver
    (id, name, parent_id)
values ('80730316-1903-46e4-b824-f808012634dd', 'Team Interaction', 'fffb132c-154b-4f70-9290-0a8302e25e63');

INSERT INTO tcla.tcl_driver
    (id, name, parent_id)
values ('d9922cef-111c-4247-8cfc-62026625c783', 'Communication', '80730316-1903-46e4-b824-f808012634dd');

INSERT INTO tcla.tcl_driver
    (id, name, parent_id)
values ('e451865d-0e18-4209-b1fa-5efeffe5d2b6', 'Knowledge Exchange', '80730316-1903-46e4-b824-f808012634dd');

INSERT INTO tcla.tcl_driver
    (id, name, parent_id)
values ('7e1df8e7-7954-478f-b6d8-17a1cc94c3fc', 'Member Psychological Safety', 'fffb132c-154b-4f70-9290-0a8302e25e63');

INSERT INTO tcla.tcl_driver
    (id, name, parent_id)
values ('56e29585-f823-47d1-990c-b19bbb8e1c39', 'Authenticity', '7e1df8e7-7954-478f-b6d8-17a1cc94c3fc');

INSERT INTO tcla.tcl_driver
    (id, name, parent_id)
values ('d2f31415-9aa0-46e3-bde2-8ac550ef9ec3', 'Speaking-Up', '7e1df8e7-7954-478f-b6d8-17a1cc94c3fc');

INSERT INTO tcla.tcl_driver
    (id, name, parent_id)
values ('373a00c8-07f0-4608-b3d6-fed22012ccab', 'Embracing Failure', '7e1df8e7-7954-478f-b6d8-17a1cc94c3fc');

INSERT INTO tcla.tcl_driver
    (id, name, parent_id)
values ('d74d9b22-1e11-43df-bd1d-5c278e738ce4', 'Work Practices & Processes', null);

INSERT INTO tcla.tcl_driver
    (id, name, parent_id)
values ('9329f3ef-5c04-4956-ae32-14b517239d68', 'Use Of Information', 'd74d9b22-1e11-43df-bd1d-5c278e738ce4');

INSERT INTO tcla.tcl_driver
    (id, name, parent_id)
values ('15e40f37-d863-467e-9f86-3818e165ad22', 'Efficiency & Effectiveness', 'd74d9b22-1e11-43df-bd1d-5c278e738ce4');

INSERT INTO tcla.tcl_driver
    (id, name, parent_id)
values ('6635f911-daf8-4d1e-8a4b-6da9f9107201', 'Process', '15e40f37-d863-467e-9f86-3818e165ad22');

INSERT INTO tcla.tcl_driver
    (id, name, parent_id)
values ('eae15c6c-235b-4299-bc2f-5d5719a988fb', 'Consistency', '15e40f37-d863-467e-9f86-3818e165ad22');

INSERT INTO tcla.tcl_driver
    (id, name, parent_id)
values ('33a76fe3-3d60-484d-a44f-b9c038c35c54', 'Pace', '15e40f37-d863-467e-9f86-3818e165ad22');

INSERT INTO tcla.tcl_driver
    (id, name, parent_id)
values ('4156000d-837e-4820-bc72-56cf73be8e51', 'Performance', '15e40f37-d863-467e-9f86-3818e165ad22');

INSERT INTO tcla.tcl_driver
    (id, name, parent_id)
values ('cd5ef942-a6f8-4a6e-8c4e-18c811b58c4c', 'Adaptability', 'd74d9b22-1e11-43df-bd1d-5c278e738ce4');

INSERT INTO tcla.tcl_driver
    (id, name, parent_id)
values ('b1e1e80d-a4eb-497a-af14-5591c06abb1c', 'Resilience', 'cd5ef942-a6f8-4a6e-8c4e-18c811b58c4c');

INSERT INTO tcla.tcl_driver
    (id, name, parent_id)
values ('101bbde8-228c-4056-883b-add27306bb9c', 'Iterative Working', 'cd5ef942-a6f8-4a6e-8c4e-18c811b58c4c');

INSERT INTO tcla.tcl_driver
    (id, name, parent_id)
values ('efbd44d5-b79a-4e57-bfdf-d761aff3250b', 'Continuous Learning', 'cd5ef942-a6f8-4a6e-8c4e-18c811b58c4c');

INSERT INTO tcla.tcl_driver
    (id, name, parent_id)
values ('8ddef539-54f8-413c-87e5-a99eacd1db68', 'Task Characteristics', null);

INSERT INTO tcla.tcl_driver
    (id, name, parent_id)
values ('00cf4195-6bf4-4d5f-94dd-b045b4e735db', 'Problem Statement', '8ddef539-54f8-413c-87e5-a99eacd1db68');

INSERT INTO tcla.tcl_driver
    (id, name, parent_id)
values ('b4ab706b-6f5f-4ac4-bbea-23f822a0b711', 'Problem Definition', '00cf4195-6bf4-4d5f-94dd-b045b4e735db');

INSERT INTO tcla.tcl_driver
    (id, name, parent_id)
values ('8e528d6a-5d2f-4287-9f9d-bcff50c76cdf', 'Solution Alignment', '00cf4195-6bf4-4d5f-94dd-b045b4e735db');

INSERT INTO tcla.tcl_driver
    (id, name, parent_id)
values ('0c5c9ab1-6c3d-40f1-a700-cc677c26681f', 'Complexity', '8ddef539-54f8-413c-87e5-a99eacd1db68');

INSERT INTO tcla.tcl_driver
    (id, name, parent_id)
values ('8c95ba02-2fc1-46a4-b2c6-a4d7a3eb7524', 'Task Complexity', '0c5c9ab1-6c3d-40f1-a700-cc677c26681f');

INSERT INTO tcla.tcl_driver
    (id, name, parent_id)
values ('e01fb140-36f5-4abc-b45b-ace3d4b8e43f', 'Contextual Complexity', '0c5c9ab1-6c3d-40f1-a700-cc677c26681f');

INSERT INTO tcla.tcl_driver
    (id, name, parent_id)
values ('9416fd0a-636e-4cda-a07c-edb93c888ee2', 'Metrics', '8ddef539-54f8-413c-87e5-a99eacd1db68');

INSERT INTO tcla.tcl_driver
    (id, name, parent_id)
values ('5a9f12ed-3e2d-445c-b699-8b426b1d7f9f', 'Work Environment & Tools', null);

INSERT INTO tcla.tcl_driver
    (id, name, parent_id)
values ('d5c1a5e3-c769-492f-bbf4-1fdf0d00d1ab', 'Tools', '5a9f12ed-3e2d-445c-b699-8b426b1d7f9f');

INSERT INTO tcla.tcl_driver
    (id, name, parent_id)
values ('894b7fab-c3ae-43e5-96a8-b36952b698ec', 'Tool Suitability', 'd5c1a5e3-c769-492f-bbf4-1fdf0d00d1ab');

INSERT INTO tcla.tcl_driver
    (id, name, parent_id)
values ('54986fdc-e3fd-4ff4-a776-cf8c5a206021', 'Tool Performance', 'd5c1a5e3-c769-492f-bbf4-1fdf0d00d1ab');

INSERT INTO tcla.tcl_driver
    (id, name, parent_id)
values ('43792379-6c2e-4940-ba10-423a551f4741', 'Environment', '5a9f12ed-3e2d-445c-b699-8b426b1d7f9f');

-- Assessment data 1

INSERT INTO tcla.assessment
    (id)
values ('db1aacc3-2f3e-40f9-9cb8-cb0388466914');

INSERT INTO tcla.questionnaire
(id, external_questionnaire_id, external_questionnaire_is_public, start_date, end_date, assessment_id)
values ('0fc00659-527b-4ad3-b449-007c9bb82a67', 'bGVHG', false, '2023-10-19 10:23:54', '2023-10-28 22:10:33',
        'db1aacc3-2f3e-40f9-9cb8-cb0388466914');


-- question 1

INSERT INTO tcla.question
    (id, title, "order", questionnaire_id)
VALUES ('df4221dd-bd3a-41c9-842a-d1554a44b15a', 'Which of the following describes how you think of yourself?', 1,
        '0fc00659-527b-4ad3-b449-007c9bb82a67');

INSERT INTO tcla.multiple_choice_question
    (id)
VALUES ('df4221dd-bd3a-41c9-842a-d1554a44b15a');

INSERT INTO tcla.answer_option
    ("order", id, multiple_choice_question_id, value)
VALUES (1, '3223c40f-d986-424c-988c-2070ffba82f6', 'df4221dd-bd3a-41c9-842a-d1554a44b15a', 'Female');

INSERT INTO tcla.answer_option
    ("order", id, multiple_choice_question_id, value)
VALUES (2, 'a968248a-e2f1-4388-8ff8-2d7d663c9396', 'df4221dd-bd3a-41c9-842a-d1554a44b15a', 'Male');

INSERT INTO tcla.answer_option
    ("order", id, multiple_choice_question_id, value)
VALUES (3, 'a712bfbf-104d-4a1a-aff1-3316991bd764', 'df4221dd-bd3a-41c9-842a-d1554a44b15a', 'Other');

-- question 2

INSERT INTO tcla.question
    (id, title, "order", questionnaire_id)
VALUES ('05ec3a55-adaa-4f92-94ba-edbb3b3956c8', 'There are multiple stakeholder groups with differing expectations.', 2,
        '0fc00659-527b-4ad3-b449-007c9bb82a67');

INSERT INTO tcla.multiple_choice_question
    (id)
VALUES ('05ec3a55-adaa-4f92-94ba-edbb3b3956c8');

INSERT INTO tcla.answer_option
    ("order", id, multiple_choice_question_id, value)
VALUES (1, 'a9f848b0-3328-4784-8cec-e5309f7b115d', '05ec3a55-adaa-4f92-94ba-edbb3b3956c8', '1');

INSERT INTO tcla.answer_option
    ("order", id, multiple_choice_question_id, value)
VALUES (2, 'e92ac2f5-20e1-4ea9-ad08-7aec01373fa1', '05ec3a55-adaa-4f92-94ba-edbb3b3956c8', '2');

INSERT INTO tcla.answer_option
    ("order", id, multiple_choice_question_id, value)
VALUES (3, '0b9d17a4-1cb5-4451-8185-d936d759c937', '05ec3a55-adaa-4f92-94ba-edbb3b3956c8', '3');

INSERT INTO tcla.answer_option
    ("order", id, multiple_choice_question_id, value)
VALUES (4, '5ffa6aad-56a0-4ec6-a84a-83b4798903f9', '05ec3a55-adaa-4f92-94ba-edbb3b3956c8', '4');

INSERT INTO tcla.answer_option
    ("order", id, multiple_choice_question_id, value)
VALUES (5, '2d1a1773-f43e-44a4-8191-4973e7ed2d0f', '05ec3a55-adaa-4f92-94ba-edbb3b3956c8', '5');

-- Questionnaire answer

INSERT INTO tcla.questionnaire_filling
    (id, questionnaire_id)
VALUES ('b8acf9cf-b595-48e4-905a-aaaf34544e8d', '0fc00659-527b-4ad3-b449-007c9bb82a67');

-- Question answer

INSERT INTO tcla.answer
    (id, questionnaire_filling_id)
VALUES ('2b4d7bb5-38c2-4d89-9a6f-d87df06ac376', 'b8acf9cf-b595-48e4-905a-aaaf34544e8d');

INSERT INTO tcla.multiple_choice_answer
    (answer_option_id, id)
VALUES ('3223c40f-d986-424c-988c-2070ffba82f6', '2b4d7bb5-38c2-4d89-9a6f-d87df06ac376');

-- Question answer

INSERT INTO tcla.answer
    (id, questionnaire_filling_id)
VALUES ('7ff296db-180c-40ae-96b7-02fd28e4ce9b', 'b8acf9cf-b595-48e4-905a-aaaf34544e8d');

INSERT INTO tcla.multiple_choice_answer
    (answer_option_id, id)
VALUES ('0b9d17a4-1cb5-4451-8185-d936d759c937', '7ff296db-180c-40ae-96b7-02fd28e4ce9b');

-- TCL Driver Scores

INSERT INTO tcla.tcl_driver_score
    (id, value, tcl_assessment_id, tcl_driver_id)
VALUES ('963e6a17-6398-45e3-bf13-14ec1a8222b9', 2.20, 'db1aacc3-2f3e-40f9-9cb8-cb0388466914',
        '0fc00659-527b-4ad3-b449-007c9bb82a67');

INSERT INTO tcla.tcl_driver_score
    (id, value, tcl_assessment_id, tcl_driver_id)
VALUES ('f0748477-2923-4625-a815-fcbb714e5d93', 2.60, 'db1aacc3-2f3e-40f9-9cb8-cb0388466914',
        '9607872f-059d-41ec-9662-f17778e5bc3a');

INSERT INTO tcla.tcl_driver_score
    (id, value, tcl_assessment_id, tcl_driver_id)
VALUES ('597c012e-730e-4e1d-a96a-ba7706ed5b2a', 3.46, 'db1aacc3-2f3e-40f9-9cb8-cb0388466914',
        '3c2f2271-0551-454c-ac7b-74e2909b7159');

INSERT INTO tcla.tcl_driver_score
    (id, value, tcl_assessment_id, tcl_driver_id)
VALUES ('646991b7-481a-43ec-8143-2c27723def0e', 1.75, 'db1aacc3-2f3e-40f9-9cb8-cb0388466914',
        '583d82c7-96a3-4b6f-a71e-e45f46a949f7');
--
INSERT INTO tcla.tcl_driver_score
    (id, value, tcl_assessment_id, tcl_driver_id)
VALUES ('66c5b6b2-937c-4242-9637-fecaf61e719e', 2.11, 'db1aacc3-2f3e-40f9-9cb8-cb0388466914',
        '66f9073f-43e7-4362-848e-010f4f53722c');

INSERT INTO tcla.tcl_driver_score
    (id, value, tcl_assessment_id, tcl_driver_id)
VALUES ('77013278-a30d-47f5-b66a-ec4c1850e15a', 1.96, 'db1aacc3-2f3e-40f9-9cb8-cb0388466914',
        '4776965b-ae88-461f-bfe9-f2396d070fb0');

INSERT INTO tcla.tcl_driver_score
    (id, value, tcl_assessment_id, tcl_driver_id)
VALUES ('52a3c855-bff0-49e6-bbd8-e52a2e3a9721', 2.00, 'db1aacc3-2f3e-40f9-9cb8-cb0388466914',
        'adcbdd72-7727-4b3f-b630-3ef321fda248');

INSERT INTO tcla.tcl_driver_score
    (id, value, tcl_assessment_id, tcl_driver_id)
VALUES ('55caa490-1ab2-4b98-af6f-9e6f21bc24fd', 2.38, 'db1aacc3-2f3e-40f9-9cb8-cb0388466914',
        'b0821f0c-5708-421c-a3b8-6204ae11af56');

INSERT INTO tcla.tcl_driver_score
    (id, value, tcl_assessment_id, tcl_driver_id)
VALUES ('8f1c96b1-f8f7-451d-8afd-11401479420a', 1.88, 'db1aacc3-2f3e-40f9-9cb8-cb0388466914',
        'fffb132c-154b-4f70-9290-0a8302e25e63');

INSERT INTO tcla.tcl_driver_score
    (id, value, tcl_assessment_id, tcl_driver_id)
VALUES ('01aa4873-76b6-4ed9-9810-ea2c559d1b7b', 1.79, 'db1aacc3-2f3e-40f9-9cb8-cb0388466914',
        '9936e560-1b43-4df1-8f33-ccee53609e08');

INSERT INTO tcla.tcl_driver_score
    (id, value, tcl_assessment_id, tcl_driver_id)
VALUES ('ced6669b-0316-4440-ae1f-2419970a96c9', 2.02, 'db1aacc3-2f3e-40f9-9cb8-cb0388466914',
        '80730316-1903-46e4-b824-f808012634dd');

INSERT INTO tcla.tcl_driver_score
    (id, value, tcl_assessment_id, tcl_driver_id)
VALUES ('024a4fb9-ad8c-472c-809e-b9951c8e4d8f', 2.12, 'db1aacc3-2f3e-40f9-9cb8-cb0388466914',
        'd9922cef-111c-4247-8cfc-62026625c783');

INSERT INTO tcla.tcl_driver_score
    (id, value, tcl_assessment_id, tcl_driver_id)
VALUES ('da9c0210-6108-46d1-a953-ee7d2b336c01', 1.92, 'db1aacc3-2f3e-40f9-9cb8-cb0388466914',
        'e451865d-0e18-4209-b1fa-5efeffe5d2b6');

INSERT INTO tcla.tcl_driver_score
    (id, value, tcl_assessment_id, tcl_driver_id)
VALUES ('233b9a13-51eb-4193-a503-6ef51536285f', 1.82, 'db1aacc3-2f3e-40f9-9cb8-cb0388466914',
        '7e1df8e7-7954-478f-b6d8-17a1cc94c3fc');

INSERT INTO tcla.tcl_driver_score
    (id, value, tcl_assessment_id, tcl_driver_id)
VALUES ('ccc069f4-d349-4887-8638-d8aa209d3656', 1.79, 'db1aacc3-2f3e-40f9-9cb8-cb0388466914',
        '56e29585-f823-47d1-990c-b19bbb8e1c39');

INSERT INTO tcla.tcl_driver_score
    (id, value, tcl_assessment_id, tcl_driver_id)
VALUES ('1d17d4cb-11d2-4705-9b4e-0c7f02485237', 1.88, 'db1aacc3-2f3e-40f9-9cb8-cb0388466914',
        'd2f31415-9aa0-46e3-bde2-8ac550ef9ec3');

INSERT INTO tcla.tcl_driver_score
    (id, value, tcl_assessment_id, tcl_driver_id)
VALUES ('a9ec9345-0a50-4322-b6bb-0a3931816cae', 1.79, 'db1aacc3-2f3e-40f9-9cb8-cb0388466914',
        '373a00c8-07f0-4608-b3d6-fed22012ccab');

INSERT INTO tcla.tcl_driver_score
    (id, value, tcl_assessment_id, tcl_driver_id)
VALUES ('0e24be8d-54a5-4812-94b0-9c31c3c080a0', 2.47, 'db1aacc3-2f3e-40f9-9cb8-cb0388466914',
        'd74d9b22-1e11-43df-bd1d-5c278e738ce4');

INSERT INTO tcla.tcl_driver_score
    (id, value, tcl_assessment_id, tcl_driver_id)
VALUES ('ae2e5df1-c0fb-4b01-b5c3-8bb87854af1f', 2.50, 'db1aacc3-2f3e-40f9-9cb8-cb0388466914',
        '9329f3ef-5c04-4956-ae32-14b517239d68');

INSERT INTO tcla.tcl_driver_score
    (id, value, tcl_assessment_id, tcl_driver_id)
VALUES ('65ec6373-8e25-4521-8eda-a5686570d494', 2.55, 'db1aacc3-2f3e-40f9-9cb8-cb0388466914',
        '15e40f37-d863-467e-9f86-3818e165ad22');

INSERT INTO tcla.tcl_driver_score
    (id, value, tcl_assessment_id, tcl_driver_id)
VALUES ('eecd1dd8-f363-4d27-9893-cc04491e2b2e', 2.54, 'db1aacc3-2f3e-40f9-9cb8-cb0388466914',
        '6635f911-daf8-4d1e-8a4b-6da9f9107201');

INSERT INTO tcla.tcl_driver_score
    (id, value, tcl_assessment_id, tcl_driver_id)
VALUES ('cad7d4fb-83d6-4c8d-8de2-d240c70cbce7', 2.62, 'db1aacc3-2f3e-40f9-9cb8-cb0388466914',
        'eae15c6c-235b-4299-bc2f-5d5719a988fb');

INSERT INTO tcla.tcl_driver_score
    (id, value, tcl_assessment_id, tcl_driver_id)
VALUES ('4290e62a-b920-451f-a1fa-8755f2cd41d9', 2.67, 'db1aacc3-2f3e-40f9-9cb8-cb0388466914',
        '33a76fe3-3d60-484d-a44f-b9c038c35c54');

INSERT INTO tcla.tcl_driver_score
    (id, value, tcl_assessment_id, tcl_driver_id)
VALUES ('3c2bda75-14e9-4f9d-b593-db67364156cc', 2.38, 'db1aacc3-2f3e-40f9-9cb8-cb0388466914',
        '4156000d-837e-4820-bc72-56cf73be8e51');

INSERT INTO tcla.tcl_driver_score
    (id, value, tcl_assessment_id, tcl_driver_id)
VALUES ('8ea9a120-ea30-4207-bbf7-6fb05750051d', 2.36, 'db1aacc3-2f3e-40f9-9cb8-cb0388466914',
        'cd5ef942-a6f8-4a6e-8c4e-18c811b58c4c');

INSERT INTO tcla.tcl_driver_score
    (id, value, tcl_assessment_id, tcl_driver_id)
VALUES ('4a7b9133-5496-4fd7-b44d-b77f5cc5632c', 2.50, 'db1aacc3-2f3e-40f9-9cb8-cb0388466914',
        'b1e1e80d-a4eb-497a-af14-5591c06abb1c');

INSERT INTO tcla.tcl_driver_score
    (id, value, tcl_assessment_id, tcl_driver_id)
VALUES ('fbc6d9be-6cf0-4147-afc7-fd2d346d1aaa', 2.54, 'db1aacc3-2f3e-40f9-9cb8-cb0388466914',
        '101bbde8-228c-4056-883b-add27306bb9c');

INSERT INTO tcla.tcl_driver_score
    (id, value, tcl_assessment_id, tcl_driver_id)
VALUES ('62cdd3d3-9bf4-410a-8092-2f052a7e25ca', 2.04, 'db1aacc3-2f3e-40f9-9cb8-cb0388466914',
        'efbd44d5-b79a-4e57-bfdf-d761aff3250b');

INSERT INTO tcla.tcl_driver_score
    (id, value, tcl_assessment_id, tcl_driver_id)
VALUES ('e832cc08-a0b9-4ad8-a4bb-4190160c4a43', 2.80, 'db1aacc3-2f3e-40f9-9cb8-cb0388466914',
        '8ddef539-54f8-413c-87e5-a99eacd1db68');

INSERT INTO tcla.tcl_driver_score
    (id, value, tcl_assessment_id, tcl_driver_id)
VALUES ('bd380f4d-bb0f-475a-b29b-8fc20d0be5e4', 2.17, 'db1aacc3-2f3e-40f9-9cb8-cb0388466914',
        '00cf4195-6bf4-4d5f-94dd-b045b4e735db');

INSERT INTO tcla.tcl_driver_score
    (id, value, tcl_assessment_id, tcl_driver_id)
VALUES ('3647fafb-bd7f-4bd2-90eb-9fce9022c58e', 2.29, 'db1aacc3-2f3e-40f9-9cb8-cb0388466914',
        'b4ab706b-6f5f-4ac4-bbea-23f822a0b711');

INSERT INTO tcla.tcl_driver_score
    (id, value, tcl_assessment_id, tcl_driver_id)
VALUES ('231f1762-898a-4fd3-805e-4008d381bbd6', 2.04, 'db1aacc3-2f3e-40f9-9cb8-cb0388466914',
        '8e528d6a-5d2f-4287-9f9d-bcff50c76cdf');

INSERT INTO tcla.tcl_driver_score
    (id, value, tcl_assessment_id, tcl_driver_id)
VALUES ('2554426e-032e-481d-a13f-02a641465bf3', 3.22, 'db1aacc3-2f3e-40f9-9cb8-cb0388466914',
        '0c5c9ab1-6c3d-40f1-a700-cc677c26681f');

INSERT INTO tcla.tcl_driver_score
    (id, value, tcl_assessment_id, tcl_driver_id)
VALUES ('8f9ce7b2-383d-48b9-a744-b3f87a17e133', 2.94, 'db1aacc3-2f3e-40f9-9cb8-cb0388466914',
        '8c95ba02-2fc1-46a4-b2c6-a4d7a3eb7524');

INSERT INTO tcla.tcl_driver_score
    (id, value, tcl_assessment_id, tcl_driver_id)
VALUES ('b01de8fa-61e6-4223-a27a-f49dd16e03fd', 3.50, 'db1aacc3-2f3e-40f9-9cb8-cb0388466914',
        'e01fb140-36f5-4abc-b45b-ace3d4b8e43f');

INSERT INTO tcla.tcl_driver_score
    (id, value, tcl_assessment_id, tcl_driver_id)
VALUES ('51a97872-4936-41e5-ad91-43fb8c9f5a61', 3.00, 'db1aacc3-2f3e-40f9-9cb8-cb0388466914',
        '9416fd0a-636e-4cda-a07c-edb93c888ee2');

INSERT INTO tcla.tcl_driver_score
    (id, value, tcl_assessment_id, tcl_driver_id)
VALUES ('d60bcc39-3886-453b-948a-39cc551aa334', 2.40, 'db1aacc3-2f3e-40f9-9cb8-cb0388466914',
        '5a9f12ed-3e2d-445c-b699-8b426b1d7f9f');

INSERT INTO tcla.tcl_driver_score
    (id, value, tcl_assessment_id, tcl_driver_id)
VALUES ('9104102a-1707-4a10-8997-801d58ffb040', 2.92, 'db1aacc3-2f3e-40f9-9cb8-cb0388466914',
        'd5c1a5e3-c769-492f-bbf4-1fdf0d00d1ab');

INSERT INTO tcla.tcl_driver_score
    (id, value, tcl_assessment_id, tcl_driver_id)
VALUES ('efe972be-81d8-463d-b706-c762ed52980c', 2.81, 'db1aacc3-2f3e-40f9-9cb8-cb0388466914',
        '894b7fab-c3ae-43e5-96a8-b36952b698ec');

INSERT INTO tcla.tcl_driver_score
    (id, value, tcl_assessment_id, tcl_driver_id)
VALUES ('ae844af9-daec-4adc-9db6-8fa61498cefc', 3.03, 'db1aacc3-2f3e-40f9-9cb8-cb0388466914',
        '54986fdc-e3fd-4ff4-a776-cf8c5a206021');

INSERT INTO tcla.tcl_driver_score
    (id, value, tcl_assessment_id, tcl_driver_id)
VALUES ('a8987e11-a561-4686-9319-872d3665415c', 1.88, 'db1aacc3-2f3e-40f9-9cb8-cb0388466914',
        '43792379-6c2e-4940-ba10-423a551f4741');

-- Assessment data 2

INSERT INTO tcla.assessment
    (id)
values ('0b16acab-7c3c-4611-b5d1-0c3372e71c24');

INSERT INTO tcla.questionnaire
(id, external_questionnaire_id, external_questionnaire_is_public, start_date, end_date, assessment_id)
values ('d1c43632-86d1-4de3-a2f0-68dd42a5f75e', 'JNIASH', false, '2023-10-19 10:23:54', '2023-10-28 22:10:33',
        '0b16acab-7c3c-4611-b5d1-0c3372e71c24');

-- TCL Driver Scores
