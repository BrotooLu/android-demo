#include <string.h>
#include "map_reader.h"

inline static
int CompareName(const char *m1, const char *m2, bool ignore_path) {
    const char *name1;
    const char *name2;
    if (ignore_path) {
        name1 = strrchr(m1, '/');
        if (NULL == name1)
            name1 = m1;
        else
            name1++;

        if ('\0' == name1[0])
            return -1;

        name2 = strrchr(m2, '/');
        if (NULL == name2)
            name2 = m2;
        else
            name2++;

        if ('\0' == name2[0])
            return 1;
    } else {
        name1 = m1;
        name2 = m2;
    }
    return strcmp(name1, name2);
}

bool MapReader::FindModuleHandle(const char *const module, bool ignore_path,
                                 ModuleHandle *const handle) {
    if (module == NULL || !LoadMaps()) {
        return false;
    }
    ModuleHandleList *list = head_;
    while (list != NULL) {
        ModuleHandle m = list->handle;
        if (CompareName(module, m.name, ignore_path) == 0) {
            *handle = m;
            return true;
        }
        list = list->next;
    }
    return false;
}

bool MapReader::LoadMaps() {
    if (fd_ != NULL) {
        return true;
    }

    char file[MAX_PATH];
    sprintf(file, "/proc/%d/maps", pid_);
    fd_ = fopen(file, "rb");
    if (fd_ == NULL) {
        return false;
    }

    while (true) {
        char line[MAX_LINE] = {0};
        char *p = fgets(line, MAX_LINE, fd_);
        if (feof(fd_) || NULL == p) {
            break;
        }

        char path[MAX_PATH] = {0};
        size_t start = 0;
        size_t end = 0;
        int rv = sscanf(p, "%p-%p %*s %*s %*s %*s %" S(MAX_PATH) "s\n", &start, &end, path);
        if (rv == 2) {
            LOG_D("rv == 2");
            continue;
        }

        if (strlen(path) > MAX_PATH || strlen(path) <= 0) {
            continue;
        }

        ModuleHandleList *list = head_;
        ModuleHandle *m = NULL;
        while (list != NULL) {
            if (strcmp(list->handle.name, path) == 0) {
                m = &list->handle;
                break;
            }

            list = list->next;
        }

        if (m != NULL) {
            if (start < m->p_start) {
                m->p_start = start;
            }
            if (end > m->p_end) {
                m->p_end = end;
            }
        } else {
            ModuleHandleList *ml = new ModuleHandleList;
            ml->handle.p_start = start;
            ml->handle.p_end = end;
            strcpy(ml->handle.name, path);
            ml->next = head_;
            head_ = ml;
            LOG_D("lib: %s start: %p", path, start);
        }
    }
    return true;
}

void MapReader::Free() {
    if (fd_ != NULL) {
        fclose(fd_);
        fd_ = NULL;
    }

    ModuleHandleList *ml = head_;
    while (ml != NULL) {
        ModuleHandleList *t = ml;
        ml = t->next;
        delete t;
    }
}
