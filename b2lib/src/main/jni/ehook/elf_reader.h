
#ifndef EHOOK_ELF_READER_H
#define EHOOK_ELF_READER_H

#include "so_info.h"

bool get_so_offset(const char *path, const char *const *p_symbols,
                   ElfW(Addr) *p_addresses, size_t count);

#endif /* EHOOK_ELF_READER_H */
