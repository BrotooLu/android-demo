//
// Created by Brotoo on 2018/4/15.
//

#ifndef EHOOK_SOINFO_H
#define EHOOK_SOINFO_H

#include <unistd.h>
#include <linux/elf.h>

#if defined(__LP64__)
#define ElfW(type) Elf64_ ## type
#else
#define ElfW(type) Elf32_ ## type
#endif

typedef void (*linker_function_t)();

#define SOINFO_NAME_LEN 128

struct SoInfo {
    char name[SOINFO_NAME_LEN];
    const ElfW(Phdr) *phdr;
    size_t phnum;
    ElfW(Addr) entry;
    ElfW(Addr) base;
    size_t size;

#ifndef __LP64__
    uint32_t unused1;  // DO NOT USE, maintained for compatibility.
#endif

    ElfW(Dyn) *dynamic;

#ifndef __LP64__
    uint32_t unused2; // DO NOT USE, maintained for compatibility
  uint32_t unused3; // DO NOT USE, maintained for compatibility
#endif

    struct SoInfo *next;
    unsigned flags;

    const char *strtab;
    ElfW(Sym) *symtab;

    size_t nbucket;
    size_t nchain;
    unsigned *bucket;
    unsigned *chain;

#if defined(__mips__) || !defined(__LP64__)
    // This is only used by mips and mips64, but needs to be here for
  // all 32-bit architectures to preserve binary compatibility.
  ElfW(Addr)** plt_got;
#endif

#if defined(USE_RELA)
    ElfW(Rela)* plt_rela;
  size_t plt_rela_count;

  ElfW(Rela)* rela;
  size_t rela_count;
#else
    ElfW(Rel) *plt_rel;
    size_t plt_rel_count;

    ElfW(Rel) *rel;
    size_t rel_count;
#endif

    linker_function_t *preinit_array;
    size_t preinit_array_count;

    linker_function_t *init_array;
    size_t init_array_count;
    linker_function_t *fini_array;
    size_t fini_array_count;

    linker_function_t init_func;
    linker_function_t fini_func;
};

struct LinkMap {
    ElfW(Addr) l_addr;
    char *l_name;
    ElfW(Dyn) *l_ld;
    struct LinkMap *l_next;
    struct LinkMap *l_prev;
};

struct SoInfo64Android6 {
    const ElfW(Phdr) *phdr;
    size_t phnum;
    ElfW(Addr) entry;
    ElfW(Addr) base;
    size_t size;

    ElfW(Dyn) *dynamic;

    struct SoInfo64Android6 *next;
    unsigned flags;

    const char *strtab;
    ElfW(Sym) *symtab;

    size_t nbucket;
    size_t nchain;
    unsigned *bucket;
    unsigned *chain;

    ElfW(Rela) *plt_rela;
    size_t plt_rela_count;

    ElfW(Rela) *rela;
    size_t rela_count;

    linker_function_t *preinit_array;
    size_t preinit_array_count;

    linker_function_t *init_array;
    size_t init_array_count;
    linker_function_t *fini_array;
    size_t fini_array_count;

    linker_function_t init_func;
    linker_function_t fini_func;

#if defined(__arm__)
    uint32_t* ARM_exidx;
    size_t ARM_exidx_count;
#elif defined(__mips__)
    uint32_t mips_symtabno_;
    uint32_t mips_local_gotno_;
    uint32_t mips_gotsym_;
#endif
    size_t ref_count_;
    struct LinkMap link_map_head;
};

union PUnionSoInfo {
    struct SoInfo *p_so;
    struct SoInfo64Android6 *p_so64;
};

#endif //EHOOK_SOINFO_H
