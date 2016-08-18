--replace all bundleinstances of coordinatedisplay with coordinatetool
UPDATE portti_view_bundle_seq set
	bundle_id = (SELECT id FROM portti_bundle WHERE name='coordinatetool'),
	config =(SELECT config FROM portti_bundle WHERE name='coordinatetool'),
	state = (SELECT state FROM portti_bundle WHERE name='coordinatetool'),
	startup =(SELECT startup FROM portti_bundle WHERE name='coordinatetool'),
	bundleinstance = 'coordinatetool'
WHERE
	bundle_id = (SELECT id FROM portti_bundle WHERE name='coordinatedisplay');