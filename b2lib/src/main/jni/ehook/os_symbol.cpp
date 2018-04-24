#include "os_symbol.h"
#include "map_reader.h"
#include "elf_reader.h"

#include <dlfcn.h>

static void find_os_symbol(OsSymbol &symbol, MapReader &map_reader) {
    void *handle = dlopen(symbol.lib, RTLD_NOW | RTLD_GLOBAL);
    void *sym = NULL;
    if (handle != NULL) {
        sym = dlsym(handle, symbol.sym);
    }

    if (sym == NULL) {
        do {
            ModuleHandle module_handle;
            if (!map_reader.FindModuleHandle(symbol.lib, true, &module_handle)) {
                break;
            }

            const char *const sym_arr[] = {symbol.sym};
            size_t count = sizeof(sym_arr) / sizeof(sym_arr[0]);
            ElfW(Addr) addr_arr[count];
            if (!get_so_offset(symbol.lib_path, sym_arr, addr_arr, count)) {
                break;
            }

            sym = reinterpret_cast<void *>(addr_arr[0] + module_handle.p_start);
        } while (0);
    }

    if (sym != NULL) {
        symbol.val = reinterpret_cast<uintptr_t>(sym);
    }
}

void find_os_symbol(OsSymbol *const symbols, size_t count) {
    if (symbols == NULL) {
        return;
    }

    pid_t pid = getpid();
    MapReader map_reader(pid);
    for (int i = 0; i < count; ++i) {
        find_os_symbol(symbols[i], map_reader);
    }

}