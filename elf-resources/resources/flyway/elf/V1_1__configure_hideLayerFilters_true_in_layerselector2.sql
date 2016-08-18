-- Disable layer filters
UPDATE portti_bundle
SET config='{
	"hideLayerFilters": true
}'
WHERE name = 'layerselector2';


UPDATE portti_view_bundle_seq
SET config='{
	"hideLayerFilters": true
}'
WHERE bundle_id=(SELECT id FROM portti_bundle WHERE name='layerselector2');