#ifndef EHOOK_EHOOK_H
#define EHOOK_EHOOK_H

#include <stdint.h>

uintptr_t hook(const char *const lib, const char *const sym, void *fn);

#endif