#ifndef EHOOK_MAP_READER_H
#define EHOOK_MAP_READER_H

#include <sys/types.h>
#include <stdio.h>
#include "comm.h"

struct ModuleHandle {
    char name[MAX_PATH];
    size_t p_start;
    size_t p_end;
};

struct ModuleHandleList {
    ModuleHandle handle;
    ModuleHandleList *next;
};

class MapReader {
public:
    MapReader(pid_t pid) :
            pid_(pid), head_(NULL), fd_(NULL) {
    }

    ~MapReader() {
        Free();
    }

    bool FindModuleHandle(const char *const module, bool ignore_path,
                          ModuleHandle *const handle);

private:
    MapReader(const MapReader &);

    void operator=(const MapReader &);

    bool LoadMaps();

    void Free();

    pid_t pid_;
    ModuleHandleList *head_;
    FILE *fd_;

};

#endif /* EHOOK_MAP_READER_H */
