# How to Set Up

To use this script, you will need to alter the variables in Project.java for connecting to Postgres to match your local table/user/pass. Once this has been done, you can run the following commands to seed the database with the sql files provided from within the SQL directory.

`psql -h 127.0.0.1 -d project -f ddl.sql`
`psql -h 127.0.0.1 -d project -f inserts.sql`

Assuming that you're connected at the same port, and that your Database is also called project. Make substitutions as necessary. Now that the DB is seeded, you should be able to open up the Postgres Admin and see your new tables/schema.

![Alt text](/screens/postgres.png?raw=true "Optional Title")

You can then compile the Project file from the root directory with

`javac Project.java`

and then run the file with the following command:

`java -cp .:/Users/ahmed/Downloads/postgresql-42.2.24.jar Project`

making sure to substitute in the proper path to the jar on your machine.

Then feel free to log in and play around with the owner / customer flows


![Alt text](/screens/owner.png?raw=true "Optional Title")

You can verify after you perform an operation that the new records have appeared in Postgres. 
