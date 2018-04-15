#ifndef EHOOK_EHOOK_H
#define EHOOK_EHOOK_H

#include <stdint.h>

typedef size_t HookId;

uintptr_t get_original_fn(HookId id);

HookId hook(const char *const lib, const char *const sym, void *fn);

#endif