UPDATE portti_view_bundle_seq SET startup ='{
    "title" : "Metadata Flyout",
    "fi" : "metadataflyout",
    "sv" : "metadataflyout",
    "en" : "metadataflyout",
    "bundlename" : "metadataflyout",
    "bundleinstancename" : "metadataflyout",
    "metadata" : {
        "Import-Bundle" : {
            "metadataflyout" : {
                "bundlePath" : "/Oskari/packages/elf/bundle/"
            }
        },
        "Require-Bundle-Instance" : []
    },
    "instanceProps" : {}
}'
WHERE bundle_id IN(SELECT id FROM portti_bundle WHERE name = 'metadataflyout');