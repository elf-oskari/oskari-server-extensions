--delete publisher
DELETE FROM portti_view_bundle_seq
WHERE bundleinstance = 'publisher' AND view_id = (select id from portti_view where name = 'ELF view' and application='elf_guest' AND page = 'elf_guest');

--delete myplaces
DELETE FROM portti_view_bundle_seq
WHERE bundleinstance = 'myplaces2' AND view_id = (select id from portti_view where name = 'ELF view' and application='elf_guest' AND page = 'elf_guest');

--delete personaldata
DELETE FROM portti_view_bundle_seq
WHERE bundleinstance = 'personaldata' AND view_id = (select id from portti_view where name = 'ELF view' and application='elf_guest' AND page = 'elf_guest');

--remove linktool
UPDATE portti_view_bundle_seq set config =
'{"viewtools":{"link":false,"print":false}}'
WHERE bundleinstance = 'toolbar' AND view_id = (select id from portti_view where name = 'ELF view' and application='elf_guest' AND page = 'elf_guest');
