# jdbc-sql-microsoft-access-transactions
Java/JDBC application implementing transactions to a MS Access database file. The code runs SQL statements and ensures that every transaction satisfies Primary Key, Foreign Key, and other constraints.


To run the code, the "Ammache-Fawzi-database.mdb" file must be dragged an copied into Eclipse. A "before" version of the database is also available ("Ammache-Fawzi-database-before.accdb", to compare the changes before and after the code executes. 

How the code works:
The application reads the tuples to be added from the INPUTRel relation, and tries to add them to the "Applications" relation, and make the relevant updates in the "Schools" relation. In total, 8 tuples are in INPUTRel: 2 tuples will be added successfully, 2 will violate a Primary Key constraint, 2 will violate a Foreign Key constraint, and 2 will violate a dynamic constraint. The program also prints a Run Message corresponding to each transaction in the OUTPUTRel relation in the database.
