Jetty 8 install

1) Download zip from http://download.eclipse.org/jetty/ -> stable-8 (tested with 8.1.16)
2) Cleanup the demo-content:

- Remove everything from {JETTY_HOME}/contexts/
- Remove .war-files under {JETTY_HOME}/webapps/

3) Adding JNDI support and a PostgreSQL driver

- Download the driver .jar-file from https://jdbc.postgresql.org/download.html (tested withJDBC41 Postgresql Driver, Version 9.3-1102)
- Place the driver in {JETTY_HOME}/lib/ext/[postgresql-9.3-1102.jdbc41.jar]
- Add "jndi" to the OPTIONS-list in {JETTY_HOME}/start.ini