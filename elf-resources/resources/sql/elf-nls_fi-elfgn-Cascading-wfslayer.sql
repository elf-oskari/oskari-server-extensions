-- add map layer; 
-- use unique name value for name column (this is key in below sql segments)
-- url is wfs service url
-- locale is for UI layer names
-- groupId is is id of service maintainer (look table oskari_layergroup)
INSERT INTO oskari_maplayer(type, name, groupId, 
                            minscale, maxscale, 
                            url, username, password, srs_name, version,
                             locale) 
  VALUES('wfslayer', 'elf_elfgn_no', 902, 
         150000, 1, 
          'http://wfs.geonorge.no/skwms1/wfs.elf-gn', null, null, 'urn:ogc:def:crs:EPSG::3857', '2.0.0', '{fi:{name:"GN Geographical Names - kartverket", subtitle:"Norwegian Mapping Authorty"},sv:{name:"GN Geographical Names - kartverket", subtitle:"Norwegian Mapping Authorty"},en:{name:"GN Geographical Names - kartverket", subtitle:"Norwegian Mapping Authorty"}}');
          
 
          
-- link to inspire theme; 
-- Fix LIKE value (look at exsisting themes in portti_inspiretheme table)
INSERT INTO oskari_maplayer_themes(maplayerid, 
                                   themeid) 
  VALUES((SELECT MAX(id) FROM oskari_maplayer), 
         (SELECT id FROM portti_inspiretheme WHERE locale LIKE '%Geographical names%')); 
          
          
-- add template model stuff; 
-- request_template is for GetFeature request
-- response template is java class, which will parse response - use generic ELF_wfs_Parser class, if parse_config is filled
INSERT INTO portti_wfs_template_model(name, description, type, request_template, response_template, parse_config) 
VALUES (
    'ELF gn', 'ELF gn generic parser', 'mah taip', 
    '/fi/nls/oskari/fe/input/request/wfs/generic/ELF_generic_wfs_template.xml', 
    'fi.nls.oskari.eu.elf.recipe.universal.ELF_wfs_Parser',
    '{
    "scan": {
        "scanNS": "http://www.opengis.net/wfs/2.0",
        "name": "member"
    },
    "root": {
        "rootNS": "http://www.locationframework.eu/schemas/GeographicalNames/0.2",
        "name": "NamedPlace"
    },
    "paths": [ {
        "path": "/elf-gn:NamedPlace/@gml:id",
        "type": "String",
        "label": "id"
    }, {
        "path": "/elf-gn:NamedPlace/gn:inspireId/base:Identifier/base:localId",
        "type": "String",
        "label": "InspireLocalId"
    }, {
        "path": "/elf-gn:NamedPlace/gn:inspireId/base:Identifier/base:versionId",
        "type": "String",
        "label": "InspireVersionId"
    }, {
        "path": "/elf-gn:NamedPlace/gn:geometry",
        "type": "Geometry",
        "label": "geom"
    }, {
        "path": "/elf-gn:NamedPlace/gn:localType/gmd:LocalisedCharacterString",
        "type": "String",
        "label": "type"
    }, {
        "path": "/elf-gn:NamedPlace/gn:name/elf-gn:GeographicalName/gn:spelling/gn:SpellingOfName/gn:text",
        "type": "String",
        "label": "name"
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
      'elf_elfgn_no', 
       'geom', '3.2.1', false, 
        5000, 
       'elf-gn', 
       '', 
       '{}', 
       '{}', 
       '{}', 
       '2d', 
       NULL, true, true, false, NULL, 
    'NamedPlace', 'http://www.locationframework.eu/schemas/GeographicalNames/0.2', 
    '', 
    true, '{}', '{ "default" : 1, "oskari_custom" : 1}', 
    'oskari-feature-engine', (select max(id) from portti_wfs_template_model)); 
     
-- add wfs layer styles; 
-- xml is .sld style definition for the layer rendering
INSERT INTO portti_wfs_layer_style (name,sld_style) VALUES(
    'oskari-feature-engine',
    '/fi/nls/oskari/fe/output/style/inspire/gn/nls_fi.xml'
);
 
-- link wfs layer styles; 
INSERT INTO portti_wfs_layers_styles (wfs_layer_id,wfs_layer_style_id) VALUES(
    (select max(id) from portti_wfs_layer),
    (select max(id) from portti_wfs_layer_style));
     
 
-- setup permissions for guest user;
-- !!!!!!!!! set resource_mapping field according to 'oskari_maplayer.url'+'oskari_maplayer.name'  !!!!!!!
INSERT INTO oskari_resource(resource_type, resource_mapping) values ('maplayer', 'wfslayer+http://wfs.geonorge.no/skwms1/wfs.elf-gn+elf_elfgn_no');
 
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
