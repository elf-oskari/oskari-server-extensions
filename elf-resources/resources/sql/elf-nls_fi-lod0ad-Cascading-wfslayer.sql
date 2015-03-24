-- add map layer; 
-- use unique name value for name column (this is key in below sql segments)
-- url is wfs service url
-- locale is for UI layer names
-- groupId is is id of service maintainer (look table oskari_layergroup)
INSERT INTO oskari_maplayer(type, name, groupId, 
                            minscale, maxscale, 
                            url, username, password, srs_name, version,
                             locale) 
  VALUES('wfslayer', 'elf_lod0ad_fi', 905, 
         50000, 1, 
          'http://54.228.221.191/ELFcascadingWFS/service', null, null, 'urn:ogc:def:crs:EPSG::3857', '2.0.0', '{fi:{name:"AD cascading Osoitteet - ELF", subtitle:"ELF Cascading"},sv:{name:"Ad cascading Adresser - ELF", subtitle:"ELF Cascading"},en:{name:"AD cascading Address- ELF", subtitle:"ELF Cascading"}}');
          
 
          
-- link to inspire theme; 
-- Fix LIKE value (look at exsisting themes in portti_inspiretheme table)
INSERT INTO oskari_maplayer_themes(maplayerid, 
                                   themeid) 
  VALUES((SELECT MAX(id) FROM oskari_maplayer), 
         (SELECT id FROM portti_inspiretheme WHERE locale LIKE '%Address%')); 
          
          
-- add template model stuff; 
-- request_template is for GetFeature request
-- response template is java class, which will parse response - use generic ELF_wfs_Parser class, if parse_config is filled
INSERT INTO portti_wfs_template_model(name, description, type, request_template, response_template, parse_config) 
VALUES (
    'ELF ad', 'ELF ad generic parser', 'mah taip', 
    '/fi/nls/oskari/fe/input/format/gml/inspire/ad/nls_fi_wfs_template.xml', 
    'fi.nls.oskari.eu.elf.recipe.universal.ELF_wfs_Parser',
    '{
    "scan": {
        "scanNS": "http://www.opengis.net/wfs/2.0",
        "name": "member"
    },
    "root": {
        "rootNS": "http://www.locationframework.eu/schemas/Addresses/MasterLoD0/1.0",
        "name": "Address"
    },
    "paths": [ {
        "path": "/elf-lod0ad:Address/@gml:id",
        "type": "String",
        "label": "id"
    }, {
        "path": "/elf-lod0ad:Address/ad:inspireId/base:Identifier/base:localId",
        "type": "String",
        "label": "InspireLocalId"
    }, {
        "path": "/elf-lod0ad:Address/ad:inspireId/base:Identifier/base:versionId",
        "type": "String",
        "label": "InspireVersionId"
    }, {
        "path": "/elf-lod0ad:Address/ad:position/ad:GeographicPosition/ad:geometry",
        "type": "Geometry",
        "label": "geom"
    }, {
        "path": "/elf-lod0ad:Address/ad:locator/ad:AddressLocator/ad:designator/ad:LocatorDesignator",
        "type": "Object",
        "label": "addressLocatorDesignators"
    }, {
        "path": "/elf-lod0ad:Address/ad:validFrom",
        "type": "String",
        "label": "validFrom"
    }, {
        "path": "/elf-lod0ad:Address/ad:validTo",
        "type": "String",
        "label": "validTo"
    }, {
        "path": "/elf-lod0ad:Address/ad:beginLifespanVersion",
        "type": "String",
        "label": "beginLifespanVersion"
    }, {
        "path": "/elf-lod0ad:Address/ad:endLifespanVersion",
        "type": "String",
        "label": "endLifespanVersion"
    },{
        "path": "/elf-lod0ad:Address/ad:component/@xlink:title",
        "type": "String",
        "label": "title"}, 
        {
        "path": "/elf-lod0ad:Address/ad:component/@xlink:href",
        "type": "Href",
        "label": "components",
        "hrefPath": [{
            "path": "/wfs:SimpleFeatureCollection/wfs:member/elf-lod0ad:AdminUnitName/@gml:id",
            "type": "String",
            "label": "id"
        }, {
            "path": "/wfs:SimpleFeatureCollection/wfs:member/elf-lod0ad:AdminUnitName/ad:inspireId/base:Identifier/base:namespace",
            "type": "String",
            "label": "type"
        }, {
            "path": "/wfs:SimpleFeatureCollection/wfs:member/elf-lod0ad:AdminUnitName/ad:name/gn:GeographicalName/gn:language",
            "type": "String",
            "label": "language"
        }, {
            "path": "/wfs:SimpleFeatureCollection/wfs:member/elf-lod0ad:AdminUnitName/ad:name/gn:GeographicalName/gn:spelling/gn:SpellingOfName/gn:text",
            "type": "String",
            "label": "name"
        }, {
            "path": "/wfs:SimpleFeatureCollection/wfs:member/elf-lod0ad:ThoroughfareName/@gml:id",
            "type": "String",
            "label": "id"
        }, {
            "path": "/wfs:SimpleFeatureCollection/wfs:member/elf-lod0ad:ThoroughfareName/ad:inspireId/base:Identifier/base:namespace",
            "type": "String",
            "label": "type"
        }, {
            "path": "/wfs:SimpleFeatureCollection/wfs:member/elf-lod0ad:ThoroughfareName/ad:name/gn:GeographicalName/gn:language",
            "type": "String",
            "label": "language"
        }, {
            "path": "/wfs:SimpleFeatureCollection/wfs:member/elf-lod0ad:ThoroughfareName/ad:name/gn:GeographicalName/gn:spelling/gn:SpellingOfName/gn:text",
            "type": "String",
            "label": "name"
        }]
    }]
}');          
 
-- add wfs specific layer data; 
-- layer_name  - use same name as in oskari_maplayer.name
-- gml_geometry_property - use fixed name 'geom', when FE engine parser in use and wfs 2.0
-- (gml_geometry_property is not needed in wfs 2.0 getfeature filter)
-- feature_namespace  - use prefix of root element (name of feature type element)
-- feature_element  - put feature type name without prefix
-- feature_namespace_uri  namespace uri of feature type element
-- job_type  - use 'oskari-feature-engine'  for FE wfs parser
-- wfs_template_model_id  - id of previous insert row of portti_wfs_template_model table
INSERT INTO portti_wfs_layer ( 
    maplayer_id, 
    layer_name, 
    gml_geometry_property, gml_version, gml2_separator, 
    max_features, 
    feature_namespace, 
    properties, 
    feature_type, 
    selected_feature_params, 
    feature_params_locales, 
    geometry_type, 
    selection_sld_style_id, get_map_tiles, get_feature_info, tile_request, wms_layer_id, 
    feature_element, feature_namespace_uri, 
    geometry_namespace_uri, 
    get_highlight_image, 
    wps_params, 
    tile_buffer, 
    job_type, 
    wfs_template_model_id) 
    VALUES ( (select max(id) from oskari_maplayer), 
      'elf_lod0ad_fi', 
       'geom', '3.2.1', false, 
        5000, 
       'elf-lod0ad', 
       '', 
       '{}', 
       '{}', 
       '{}', 
       '2d', 
       NULL, true, true, false, NULL, 
    'Address', 'http://www.locationframework.eu/schemas/Addresses/MasterLoD0/1.0', 
    '', 
    true, '{}', '{ "default" : 1, "oskari_custom" : 1}', 
    'oskari-feature-engine', (select max(id) from portti_wfs_template_model)); 
     
-- add wfs layer styles; 
-- xml is .sld style definition for the layer rendering
INSERT INTO portti_wfs_layer_style (name,sld_style) VALUES(
    'oskari-feature-engine',
    '/fi/nls/oskari/fe/output/style/inspire/ad/nls_fi.xml'
);
 
-- link wfs layer styles; 
INSERT INTO portti_wfs_layers_styles (wfs_layer_id,wfs_layer_style_id) VALUES(
    (select max(id) from portti_wfs_layer),
    (select max(id) from portti_wfs_layer_style));
     
 
-- setup permissions for guest user;
-- !!!!!!!!! set resource_mapping field according to 'oskari_maplayer.url'+'oskari_maplayer.name'  !!!!!!!
INSERT INTO oskari_resource(resource_type, resource_mapping) values ('maplayer', 'wfslayer+http://54.228.221.191/ELFcascadingWFS/service+elf_lod0ad_fi');
 
-- permissions;
-- adding permissions to roles with id 10110, 2, and 3;
 
-- give view_layer permission for the resource to ROLE 10110 (guest);
INSERT INTO oskari_permission(oskari_resource_id, external_type, permission, external_id) values
((SELECT MAX(id) FROM oskari_resource), 'ROLE', 'VIEW_LAYER', '10110');
 
-- give view_layer permission for the resource to ROLE 1 (guest);
INSERT INTO oskari_permission(oskari_resource_id, external_type, permission, external_id) values
((SELECT MAX(id) FROM oskari_resource), 'ROLE', 'VIEW_LAYER', '1');
 
 
-- give view_layer permission for the resource to ROLE 2 (user);
INSERT INTO oskari_permission(oskari_resource_id, external_type, permission, external_id) values
((SELECT MAX(id) FROM oskari_resource), 'ROLE', 'VIEW_LAYER', '2');
 
-- give publish permission for the resource to ROLE 2 (user);
INSERT INTO oskari_permission(oskari_resource_id, external_type, permission, external_id) values
((SELECT MAX(id) FROM oskari_resource), 'ROLE', 'PUBLISH', '2');
 
 
-- give publish permission for the resource to ROLE 3 (admin);
INSERT INTO oskari_permission(oskari_resource_id, external_type, permission, external_id) values
((SELECT MAX(id) FROM oskari_resource), 'ROLE', 'PUBLISH', '3');
 
-- give view_published_layer permission for the resource to ROLE 10110 (guest);
INSERT INTO oskari_permission(oskari_resource_id, external_type, permission, external_id) values
((SELECT MAX(id) FROM oskari_resource), 'ROLE', 'VIEW_PUBLISHED', '10110');
 
-- give view_published_layer permission for the resource to ROLE 2 (user);
INSERT INTO oskari_permission(oskari_resource_id, external_type, permission, external_id) values
((SELECT MAX(id) FROM oskari_resource), 'ROLE', 'VIEW_PUBLISHED', '2');
 
 
-- give view_published_layer permission for the resource to ROLE 10110 (guest);
INSERT INTO oskari_permission(oskari_resource_id, external_type, permission, external_id) values
((SELECT MAX(id) FROM oskari_resource), 'ROLE', 'VIEW_PUBLISHED', '1');
