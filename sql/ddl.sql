create table address(
  ID SERIAL PRIMARY KEY,
  street varchar(20) not null,
  city varchar(20) not null,
  postal varchar(6) not null
);

create table book_user(
  ID SERIAL PRIMARY KEY,
  name varchar(20) not null,
  email varchar(20) not null,
  CONSTRAINT user_email_unique UNIQUE (email),
  phone varchar(10),
  role varchar(20),
    check (role in ('Owner', 'Customer')),
  address int,
  foreign key (address) references address
    on delete set null
);

create table publisher(
  ID SERIAL PRIMARY KEY,
  name varchar(20) not null,
  email varchar(20) not null,
  CONSTRAINT publisher_email_unique UNIQUE (email),
  phone varchar(10) not null,
  address int,
  bank_account varchar(10) not null,
  foreign key (address) references address
    on delete set null
);

create table book(
  ID SERIAL PRIMARY KEY,
  title varchar(99) not null,
  author varchar(20) not null,
  genre varchar(20) not null,
  ISBN varchar(20) not null,
  publisher int not null,
  stock numeric(5, 0) not null,
  price numeric(5, 2) not null,
  margin numeric(2, 0) not null,
  pages numeric (3, 0) not null,

  foreign key (publisher) references publisher
    on delete cascade
);

create table user_order(
  ID SERIAL PRIMARY KEY,
  ordered_at TIMESTAMP DEFAULT CURRENT_DATE,
  total numeric(10, 2) not null,
  billing_address int,
  shipping_address int,
  order_number varchar(100),
  CONSTRAINT number_unique UNIQUE (order_number),
  shipping_status varchar(20)
    check (shipping_status in ('Not shipped', 'In transit', 'Delivered')),
  book_user int,
  foreign key (billing_address) references address,
  foreign key (shipping_address) references address,
  foreign key (book_user) references book_user
);

create table book_order(
  ID SERIAL PRIMARY KEY,
  book int,
  book_order int,
  foreign key (book) references book on delete cascade,
  foreign key (book_order) references book_order on delete cascade
);
