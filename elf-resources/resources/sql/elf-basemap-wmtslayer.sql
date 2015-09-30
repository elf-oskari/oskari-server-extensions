-- NOTE! Terms of use are listed in here;
--  ;
INSERT INTO oskari_layergroup (id, locale) values (999, '{ fi:{name:"ELF"},sv:{name:"ELF"},en:{name:"ELF"}}');

-- THIS IS AN EXAMPLE FOR ADDING WMTS LAYER ;
INSERT INTO oskari_maplayer(type, name, groupId,
                            metadataId, url,
                            locale,
                            tile_matrix_set_id)
  VALUES('wmtslayer', 'elf_basemap', 999,
         '', 'http://opencache.statkart.no/gatekeeper/gk/gk.open_wmts',
         '{ fi:{name:"ELF taustakartta",subtitle:"(WMTS)"},sv:{name:"ELF Backgrundskarta",subtitle:"(WMTS)"},en:{name:"ELF Background map",subtitle:"(WMTS)"}}',
         'EPSG:3857');

-- link to inspire theme;
INSERT INTO oskari_maplayer_themes(maplayerid,
                                   themeid)
  VALUES((select max(id) from oskari_maplayer),
         (SELECT id FROM portti_inspiretheme WHERE locale LIKE '%Background maps%'));

-- setup permissions for guest user;
INSERT INTO oskari_resource(resource_type, resource_mapping) values ('maplayer', 'wmtslayer+http://opencache.statkart.no/gatekeeper/gk/gk.open_wmts+elf_basemap');

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

-- give view_published_layer permission for the resource to ROLE 10110 (guest);
INSERT INTO oskari_permission(oskari_resource_id, external_type, permission, external_id) values
((SELECT MAX(id) FROM oskari_resource), 'ROLE', 'VIEW_PUBLISHED', '1');


-- give view_published_layer permission for the resource to ROLE 2 (user);
INSERT INTO oskari_permission(oskari_resource_id, external_type, permission, external_id) values
((SELECT MAX(id) FROM oskari_resource), 'ROLE', 'VIEW_PUBLISHED', '2');


