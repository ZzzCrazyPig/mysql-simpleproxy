# mysql-simpleproxy

> For the purpose of learning netty and mysql protocol

This project is in the purpose of learning [netty](http://netty.io/) and mysql protocol.

## How to use

Run `Server.java`, and then use the mysql command line tool to login in this proxy just like logining mysql server:

```bash
mysql -u${mysql_user} -p${mysql_password} -P8066 -h127.0.0.1
```

By default, it will route to the default backend mysql server `localhost:3306`. you can change it in the `SystemConfig.java`:

```java
private String mysqlHost = "localhost";
private int mysqlPort = 3306;
```

As soon as login successfully, you can use whatever command like what you use with mysql, becasue it just trasfered your command directly into the real mysql server and recevie the server reponse back to you!

In the output console, you can see each mysql protocol packet data like :

```bash

# input show database, and get its packet data :

          +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 0f 00 00 00 03 73 68 6f 77 20 64 61 74 61 62 61 |.....show databa|
|00000010| 73 65 73                                        |ses             |
+--------+-------------------------------------------------+----------------+

# then reponse like this:

         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 01 00 00 01 01 4b 00 00 02 03 64 65 66 12 69 6e |.....K....def.in|
|00000010| 66 6f 72 6d 61 74 69 6f 6e 5f 73 63 68 65 6d 61 |formation_schema|
|00000020| 08 53 43 48 45 4d 41 54 41 08 53 43 48 45 4d 41 |.SCHEMATA.SCHEMA|
|00000030| 54 41 08 44 61 74 61 62 61 73 65 0b 53 43 48 45 |TA.Database.SCHE|
|00000040| 4d 41 5f 4e 41 4d 45 0c 21 00 c0 00 00 00 fd 01 |MA_NAME.!.......|
|00000050| 00 00 00 00 13 00 00 03 12 69 6e 66 6f 72 6d 61 |.........informa|
|00000060| 74 69 6f 6e 5f 73 63 68 65 6d 61 06 00 00 04 05 |tion_schema.....|
|00000070| 6d 79 73 71 6c 13 00 00 05 12 70 65 72 66 6f 72 |mysql.....perfor|
|00000080| 6d 61 6e 63 65 5f 73 63 68 65 6d 61 04 00 00 06 |mance_schema....|
|00000090| 03 73 79 73 08 00 00 07 07 74 65 73 74 64 62 31 |.sys.....testdb1|
|000000a0| 08 00 00 08 07 74 65 73 74 64 62 32 08 00 00 09 |.....testdb2....|
|000000b0| 07 74 65 73 74 64 62 33 08 00 00 0a 07 74 65 73 |.testdb3.....tes|
|000000c0| 74 64 62 34 07 00 00 0b fe 00 00 22 00 00 00    |tdb4......."... |
+--------+-------------------------------------------------+----------------+

```