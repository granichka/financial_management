insert into usr (email, name, phone_number) values ('petrov@gmail.com','Петров Иван Сергеевич','+380965431251');
insert into usr (email, name, phone_number) values ('sidorova@gmail.com','Сидорова Мария Ивановна','+380981235671');

insert into account(amount, user_id) values (50000, 1);
insert into account(amount, user_id) values (71000, 1);
insert into account(amount, user_id) values (150000, 4);

insert into expense_category (name) values ('Коммунальные услуги');
insert into expense_category (name) values ('Ремонт автомобиля');
insert into expense_category (name) values ('Досуг');
insert into expense_category (name) values ('Покупка товаров и услуг');

insert into income_category (name) values ('Заработная плата');
insert into income_category (name) values ('Финансовые активы');
insert into income_category (name) values ('Бизнес');