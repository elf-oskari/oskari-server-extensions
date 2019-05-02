# ELF Showcase application

## Introduction

This web application was developed to showcase the benefits of harmonized European geospatial data. The application features a web map interface and administration tools for setting up a geoportal.
The application requests data from distributed sources following the principles of OGC standards (Open Geospatial Consortium http://www.opengeospatial.org/) and INSPIRE https://inspire.ec.europa.eu/.
Development was performed during the ELF (European Location Framework) project and then improved during the Open ELS (Open European Location Services https://openels.eu/) project, which were both co-financed by the European Union. The application was developed by the National Land Survey of Finland and is built on top of [Oskari](http://oskari.org) extending and customizing its functionality.

## Installation

### Components

- Oskari frontend code (https://github.com/oskariorg/oskari-frontend)
- Showcase frontend code (https://github.com/oskariorg/oskari-frontend-contrib)
- Oskari server (this directory, https://github.com/elf-oskari/oskari-server-extensions/tree/master/webapp-map)
- Oskari transport (WFS services: https://github.com/elf-oskari/oskari-server-extensions/tree/master/webapp-transport)
- Geoserver with WPS-plugin and Oskari extensions (https://github.com/oskariorg/oskari-server/tree/master/geoserver-ext)
- Redis (required for WFS-integrations)
- Jetty 9
- Database (PostgreSQL 9+ & PostGIS)

### Installation instructions

1. Set up database: http://www.oskari.org/documentation/backend/setup-database
2. Set up Redis: http://www.oskari.org/documentation/backend/setup-redis
3. Download Oskari Jetty bundle: http://www.oskari.org/download
4. Unpack the zip file to selected location

   The zip includes Howto.md, jetty-distribution-9.4.12.v20180830 (referred as {jetty.home}) and oskari-server folder (referred as {jetty.base})

5. Clone ELF server-side code (this directory) to selected location
6. Replace default Oskari webapps with corresponding ELF webapps

   - Run 'mvn clean install' in this directory. A deployable WAR-files will be compiled to ./webapp-map/target/ and  ./webapp-transport/target/
   - Copy oskari-map.war and transport.war under {jetty.base}/webapps

7. Configure the database properties (host/credentials) by editing {jetty.base}/resources/oskari-ext.properties

8. Clone the frontend code repositories

Oskari frontend:
```
git clone https://github.com/oskariorg/oskari-frontend
cd oskari-frontend
git checkout 1.50.0
npm install
```

Showcase frontend (on the same parent folder as oskari-frontend):
```
git clone https://github.com/oskariorg/oskari-frontend-contrib
cd oskari-frontend-contrib
git checkout 1.50.0
npm install
```

9. Build the showcase frontend

Run (in oskari-frontend-contrib)
```
npm run build -- --env.appdef=1.50.0:applications/elf
```

10. Replace frontend code under Jetty

Replace path in [{jetty.base}/webapps/oskari-front.xml](https://github.com/oskariorg/sample-configs/blob/master/jetty-9/oskari-server/webapps/oskari-front.xml#L13)
 to point to the showcase frontend folder (`oskari-frontend-contrib`)

```
  <Set name="resourceBase"><SystemProperty name="jetty.base" default="."/>/oskari-frontend</Set>
```

## Configuration

### ELF Topographic Basemap

WMTS serving ELF Topographic Basemap ("elf_topographic_basemap") is hosted at http://10.3.10.51/mapcache/wmts. 

Note! The IP-address is internal to the currently running network and cannot be accessed from the internet, but any basemap supporting EPSG:3035 can be used.

### Geolocator
	(Pekka Latvala will provide more information)
   - http://services.locationframework.eu/elf/GeolocatorService

### Properties
   Application specific properties should be configured to {jetty.base}/resources/oskari-ext.properties.
   
   Here's a template that has all the configuration excluding credentials for the showcase application:

```
##################################
# Environment config
##################################

# change to match your database connection parameters
db.url=jdbc:postgresql://localhost:5432/oskaridb
db.username=xxx
db.password=xxx

# Use autorepair to fix an issue with SQL-script checksums.
# Checksums fail if an updated war-file includes different line-endings than the original one.
# Note! This disable the guard against user-modified scripts so use with caution.
#db.flyway.autorepair=true

# pools used by flyway to automigrate the db!
db.additional.modules=myplaces,userlayer,elf
geoserver.url=http://localhost:7703/geoserver
geoserver.user=xxx
geoserver.password=xxx

maplayer.wmsurl.secure=/tiles/

# set development to false or comment it out to load using minified javascript
development=false
oskari.client.version=dist/1.50.0

# Logger implementation - SystemLogger logs into System.out/err, replace with logging implementation of your choice
oskari.logger=fi.nls.oskari.utils.Log4JLogger

# this is used as baseurl for published maps (external url)
oskari.domain=https://demo.locationframework.eu

# path for incoming calls to access map
oskari.map.url=/

# url path to call for ajax requests/action routes
oskari.ajax.url.prefix=/action?

# Crs support in layer selection
# Only those layers are selected, which support requested csr
oskari.crs.switch.supported=true

# redis
redis.hostname=localhost
redis.port=6379
redis.pool.size=50

# Supported locales, comma separated and default first
oskari.locales=en_US,fi_FI,sv_SE,es_ES,et_EE,nb_NO,nn_NO,is_IS,it_IT,sl_SI,fr_FR,nl_NL,sk_SK,de_DE

# Used by metadata flyout/CSW coverage scheduled task
service.metadata.url=https://demo.locationframework.eu/geonetwork/srv/eng/csw
#metadata rating type
service.metadata.rating=ELF_METADATA

# Allow published maps to be loaded from these domains
view.published.usage.unrestrictedDomains = demo.locationframework.eu, localhost, 159.162.102.173

# Disable Table Export (FeatureData Excel/CSV export)
actioncontrol.blacklist=ExportTableFile

##################################
# ELF user handling
##################################
#eu.elf.oskari.user.ConterraSecurityManagerUserService.url=https://security.locationframework.eu/administration/WAS
# true all ssl certs/hosts for debugging! configure certs on the server for production
oskari.trustAllCerts=true
# true all ssl certs/hosts for debugging! configure certs on the server for production
oskari.trustAllHosts=true

# UserService implementation
oskari.user.service=fi.nls.oskari.user.DatabaseUserService


##################################
# Configurations specific to database content
##################################
# Used to configure a template view for publishing maps, defaults to view with id 1
#ol3
view.template.publish=108

#EPSG:3035
view.default=107
oskari.user.role.admin=sM_Administrator

# bundles that are added on runtime to view if user has one of configured role
actionhandler.GetAppSetup.dynamic.bundles = admin-layerselector, admin-layerrights, myplacesimport, elf-metadatafeedback

# Linking dynamic bundles based on user roles
# Properties are named 'actionhandler.GetAppSetup.dynamic.[BUNDLE ID].roles'
#   with value as comma-separated list of role names that should be served the bundle
actionhandler.GetAppSetup.dynamic.bundle.admin-layerrights.roles = sM_Administrator
actionhandler.GetAppSetup.dynamic.bundle.admin-layerselector.roles = sM_Administrator
actionhandler.GetAppSetup.dynamic.bundle.myplacesimport.roles = sM_Administrator
actionhandler.GetAppSetup.dynamic.bundle.elf-metadatafeedback.roles = User

##################################
# Search channels configuration
##################################

# comma-separated list of id for search channel that will be used (defaulted to) if none are explicitly configured
#search.channels.default=OPENSTREETMAP_CHANNEL, ELFGEOLOCATOR_CHANNEL
search.channels.default=ELFGEOLOCATOR_CHANNEL,METADATA_CATALOGUE_CHANNEL,ELFADDRESSLOCATOR_CHANNEL

# comma-separated list of search channel ids to use (whitelist)
#search.channels=OPENSTREETMAP_CHANNEL, ELFGEOLOCATOR_CHANNEL
search.channels=ELFGEOLOCATOR_CHANNEL, METADATA_CATALOGUE_CHANNEL,ELFADDRESSLOCATOR_CHANNEL

# comma-separated list of search channel ids used by GetSearchResult
#actionhandler.GetSearchResult.channels=OPENSTREETMAP_CHANNEL, ELFGEOLOCATOR_CHANNEL
actionhandler.GetSearchResult.channels=ELFGEOLOCATOR_CHANNEL, ELFADDRESSLOCATOR_CHANNEL

# OpenStreetMap search channel settings
search.channel.OPENSTREETMAP_CHANNEL.service.url=http://nominatim.openstreetmap.org/search

#ELF GeoLocator channel settings
search.channel.ELFGEOLOCATOR_CHANNEL.service.url=http://services.locationframework.eu/elf/GeolocatorService

# Metadata catalogue channel config
search.channel.METADATA_CATALOGUE_CHANNEL.metadata.catalogue.server=http://localhost:7701
search.channel.METADATA_CATALOGUE_CHANNEL.metadata.catalogue.path=/geonetwork/srv/eng/csw
search.channel.METADATA_CATALOGUE_CHANNEL.metadata.catalogue.queryParams=SERVICE=CSW&VERSION=2.0.2&request=GetDomain&PropertyName=
# List of fields to show in advanced form (values retrieved by GetDomain requests)
search.channel.METADATA_CATALOGUE_CHANNEL.fields=type,serviceType,Title,OrganisationName,Subject,TopicCategory,Language,ResourceLanguage,coverage
# Coverage filter type
search.channel.METADATA_CATALOGUE_CHANNEL.field.coverage.filterOp=INTERSECTS
# isMulti means user can select multiple values ie. checkbox vs. dropdown
search.channel.METADATA_CATALOGUE_CHANNEL.field.type.isMulti=true
# this means that if type has value 'service' add to the same filter operation any serviceType parameters as single AND-operation
search.channel.METADATA_CATALOGUE_CHANNEL.field.type.dependencies=service|serviceType
# filter is the property to be used on filter operations
search.channel.METADATA_CATALOGUE_CHANNEL.field.serviceType.isMulti=true
# shownIf is sent to client meaning this field is only shown if field 'type' has value 'service' selected, closely related to field.dependencies!
search.channel.METADATA_CATALOGUE_CHANNEL.field.serviceType.shownIf=[{"type":"service"}]
search.channel.METADATA_CATALOGUE_CHANNEL.field.Title.filter=gmd:title
search.channel.METADATA_CATALOGUE_CHANNEL.field.TopicCategory.filter=gmd:topicCategory
# fields without filterOp are treated as LIKE operations, filterOp param value is mapped in code to deegree operations
search.channel.METADATA_CATALOGUE_CHANNEL.field.Subject.filterOp=COMP_EQUAL
search.channel.METADATA_CATALOGUE_CHANNEL.field.serviceType.filterOp=COMP_EQUAL
# must match means that this will be a top level filter element added as AND-operations (where as most other fields are added as OR)
search.channel.METADATA_CATALOGUE_CHANNEL.field.Language.mustMatch=true
search.channel.METADATA_CATALOGUE_CHANNEL.field.ResourceLanguage.mustMatch=true

search.channel.METADATA_CATALOGUE_CHANNEL.image.url.fi=/geonetwork/srv/fin/resources.get.uuid?access=public&
search.channel.METADATA_CATALOGUE_CHANNEL.image.url.sv=/geonetwork/srv/swe/resources.get.uuid?access=public&
search.channel.METADATA_CATALOGUE_CHANNEL.image.url.en=/geonetwork/srv/eng/resources.get.uuid?access=public&
search.channel.METADATA_CATALOGUE_CHANNEL.fetchpage.url.fi=/geonetwork/srv/eng/csw?Request=GetRecordById&version=2.0.2&outputSchema=csw:IsoRecord&id=
search.channel.METADATA_CATALOGUE_CHANNEL.fetchpage.url.sv=/geonetwork/srv/eng/csw?Request=GetRecordById&version=2.0.2&outputSchema=csw:IsoRecord&id=
search.channel.METADATA_CATALOGUE_CHANNEL.fetchpage.url.en=/geonetwork/srv/eng/csw?Request=GetRecordById&version=2.0.2&outputSchema=csw:IsoRecord&id=

search.channel.METADATA_CATALOGUE_CHANNEL.field.OrganisationName.space.char=?

# ReverseGeocoding settings
actionhandler.GetReverseGeocodingResult.channels=ELFGEOLOCATOR_CHANNEL
actionhandler.GetReverseGeocodingResult.buffer=5000
actionhandler.GetReverseGeocodingResult.maxfeatures=1

##############################
# Proxy services
##############################
oskari.proxyservices = myplacestile, userlayertile

oskari.proxy.myplacestile.url=http://localhost:7703/geoserver/wms?CQL_FILTER=
oskari.proxy.myplacestile.handler=fi.nls.oskari.proxy.MyPlacesProxyHandler
oskari.proxy.myplacestile.user=xxx
oskari.proxy.myplacestile.pass=xxx

oskari.proxy.userlayertile.url=http://localhost:7703/geoserver/wms?buffer=128&tiled=yes&tilesorigin=0,0&CQL_FILTER=
oskari.proxy.userlayertile.handler=fi.nls.oskari.proxy.UserLayerProxyHandler
oskari.proxy.userlayertile.user=xxx
oskari.proxy.userlayertile.pass=xxx

##############################
# Myplaces configuration
##############################

# My places query url
# MyPlacesBundleHandler.java, GeoServerProxyService.java
myplaces.ows.url=http://localhost:7703/geoserver/oskari/ows?
# MapFullHandler.java, MyPlacesHandler.java
myplaces.wms.url=http://localhost:7703/geoserver/oskari/wms?buffer=128&tiled=yes&tilesorigin=0,0&CQL_FILTER=
myplaces.user=xxx
myplaces.password=xxx
# Base WFS layer id for myplaces (oskari_maplayer table)
# Find correct id layer later on when my_places wfs layer is inserted
myplaces.baselayer.id=1
# My places namespace
myplaces.xmlns=http://www.oskari.org
# My places namespace prefix
myplaces.xmlns.prefix=oskari

##############################
# User layers configuration
# Note! requires 'userlayertile' in config for oskari.proxyservices
##############################

# Userlayer base WFS layer id for vuser_data layer (look at oskari_maplayer table)
## NOTE! This layer must have the same url as in 'userLayer.oskari.url'
userlayer.baselayer.id=55

# user data store (user layers) properties - edit url/user/pw
userLayer.oskari.url=http://localhost:7703/geoserver/oskari/wfs?
userlayer.user=xxx
userlayer.password=xxx

# Userlayer rendering Element (view table)
userlayer.rendering.element=oskari:user_layer_data_style
# Userlayer max features count  allowed to store (-1 unlimited)
userlayer.maxfeatures.count=-1
# Userlayer max file size in Mb
userlayer.max.filesize.mb=10


###################################
# License Manager details
###################################
search.channel.METADATA_CATALOGUE_CHANNEL.resultparser=eu.elf.oskari.search.MetadataCatalogueELFResultParser
search.channel.METADATA_CATALOGUE_CHANNEL.licenseUrlPrefix=https://security.locationframework.eu/wss/service/

# "CMS content" files location
actionhandler.GetArticlesByTag.dir=/articlesByTag/

# Increase default GetLayerTile.timeout.read to be able to serve the German basemap
GetLayerTile.timeout.read=20000

# Publisher action handler
oskari.map.terms.url = http://www.elfproject.eu/content/terms-service

# Spatineo Monitoring API key (CONFIDENTIAL!)
# ELF
spatineo.monitoring.key=xxx


##############################
# Oskari Scheduler configuration
##############################
# scheduled tasks are only run on node 1
#oskari.scheduler.jobs=spatineo_update
#oskari.scheduler.job.spatineo_update.cronLine=0 */5 * * * ?
#oskari.scheduler.job.spatineo_update.className=fi.nls.oskari.spatineo.SpatineoServalUpdateService
#oskari.scheduler.job.spatineo_update.methodName=scheduledServiceCall
```