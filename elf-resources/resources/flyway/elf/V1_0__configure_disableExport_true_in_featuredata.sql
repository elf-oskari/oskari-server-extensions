UPDATE portti_bundle
   SET config='{"disableExport" : true}'
 WHERE name = 'featuredata2';

UPDATE portti_view_bundle_seq
   SET config='{"disableExport" : true}'

WHERE bundleinstance = 'featuredata2';

