delete from address;
delete from book_user;
delete from book;
delete from publisher;
delete from book_order;
delete from user_order;

insert into address (street, city, postal) values('26 fable st', 'Ottawa', 'K2J2A6');
insert into address (street, city, postal) values('150 elgin st', 'Ottawa', 'K2P1L4');
insert into address (street, city, postal) values('151 Oconner St', 'Ottawa', 'K2P2L8');
insert into address (street, city, postal) values('1340 Avenue R', 'Ottawa', 'K1G0B9');


insert into book_user (name, email, phone, role, address)
  values('Ahmed A.', 'ahmed@gmail.com', '6131112222', 'Owner', 1);


insert into book_user (name, email, phone, role, address)
  values('Customer Guy', 'customer@gmail.com', '6471234567', 'Customer', 4);


insert into publisher (name, email, phone, address, bank_account)
  values('Elgin Publishing', 'elgin@gmail.com', '1234567890', 2, '32894732');

insert into publisher (name, email, phone, address, bank_account)
  values('Oconner Publishing', 'oconner@gmail.com', '4677703433', 3, '84975983');

insert into book (title, author, genre, ISBN, publisher, stock, price, margin, pages)
  values('Harry Potter 1', 'J.K. Rowling', 'Fantasy', '111344', 1, 45, 29.99, 15, 340);

insert into book (title, author, genre, ISBN, publisher, stock, price, margin, pages)
  values('Common Sense Investing', 'John C. Bogle', 'Non-Fiction', '83248', 2, 12, 19.95, 10, 220);


insert into user_order(total, billing_address, shipping_address, order_number, shipping_status, book_user)
  values(49.94, 4, 4, '1004', 'Delivered', 2);

insert into book_order(book, book_order)
  values(1, 1);

insert into book_order(book, book_order)
  values(2, 1);
