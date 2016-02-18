-- Workaround to avoid admin tools such as myplacesimport to be added to this view (TYPE = DEFAULT -> SYSTEM)
UPDATE portti_view set
type = 'SYSTEM', is_default=false
WHERE name = 'ELF view' and page='elf_guest' and application='elf_guest';