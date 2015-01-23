Jetty 8 install

1) Download zip from http://download.eclipse.org/jetty/ -> stable-8 (tested with 8.1.16)
2) Cleanup the demo-content:

- Remove everything from {JETTY_HOME}/contexts/
- Remove .war-files under {JETTY_HOME}/webapps/

3) Adding JNDI support and a PostgreSQL driver

- Download the driver .jar-file from https://jdbc.postgresql.org/download.html (tested withJDBC41 Postgresql Driver, Version 9.3-1102)
- Place the driver in {JETTY_HOME}/lib/ext/[postgresql-9.3-1102.jdbc41.jar]
- Add "jndi" to the OPTIONS-list in {JETTY_HOME}/start.ini

4) Add configuration to serve Oskari frontend files

- add oskari-front.xml to {JETTY_HOME}/contexts/
- run 'git clone https://github.com/nls-oskari/oskari' in {JETTY_HOME}
- after clone you have for example a file in {JETTY_HOME}/oskari/ReleaseNotes.md
- optionally modify 'resourceBase' in oskari-front.xml to point to a location where Oskari frontend files are located

5) Configuring oskari-map as root webapp

- add oskari-map.xml to {JETTY_HOME}/contexts/
- configure the database connection parameters (user/password) for OskariPool in oskari-map.xml
- add oskari-ext.properties to {JETTY_HOME}/resources/
- NOTE! configure the same connection params to oskari-ext.properties, the database creation requires this dual configuration for now.
- build the oskari-map.war file and copy it to {JETTY_HOME}/webapps/oskari-map.war
