//
// Created by Brotoo on 2018/4/17.
//

#ifndef EHOOK_OS_SYMBOL_H
#define EHOOK_OS_SYMBOL_H

#include "so_info.h"

struct OsSymbol {
    const char *const lib;
    const char *const lib_path;
    const char *const sym;
    uintptr_t val;

    OsSymbol(char *lib, char *lib_path, char *sym) :
            lib(lib), lib_path(lib_path), sym(sym), val(0) {

    }
};

void find_os_symbol(OsSymbol *const symbols, size_t count);

#endif //EHOOK_OS_SYMBOL_H
