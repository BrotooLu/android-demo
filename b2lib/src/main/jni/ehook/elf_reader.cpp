
#include <malloc.h>
#include <fcntl.h>
#include <string.h>
#include "elf_reader.h"
#include "comm.h"

template<typename T>
struct AutoMalloc {
    T *ptr;

    AutoMalloc(size_t size) {
        ptr = reinterpret_cast<T *>(::malloc(size));
    }

    ~AutoMalloc() {
        ::free(ptr);
    }

private:
    AutoMalloc(const AutoMalloc &);

    AutoMalloc &operator=(const AutoMalloc &);
};

struct FileReader {
    int fd;

    FileReader(const char *path) {
        fd = ::open(path, O_RDONLY);
    }

    ~FileReader() {
        if (fd >= 0) {
            ::close(fd);
        }
    }

    bool read(off_t offset, void *buf, size_t size) {
        off_t v = ::lseek(fd, offset, SEEK_SET);
        if (v < 0) {
            return false;
        }

        ssize_t count = ::read(fd, buf, size);
        return count == size;
    }

private:
    FileReader(const FileReader &);

    FileReader &operator=(const FileReader &);
};

bool get_so_offset(const char *path, const char *const *p_symbols,
                   ElfW(Addr) *p_addresses, size_t count) {

    FileReader reader(path);
    if (reader.fd < 0) {
        LOG_E("fd error %lu-%s-%s", count, path, p_symbols[0]);
        return false;
    }

    ElfW(Ehdr) header;
    if (!reader.read(0, &header, sizeof(header))) {
        LOG_E("header error %lu-%s-%s", count, path, p_symbols[0]);
        return false;
    }

    if (header.e_shnum > 100) {
        LOG_E("shnum error %lu-%s-%s %d", count, path, p_symbols[0], (int) header.e_shnum);
        return false;
    }

    int size = sizeof(ElfW(Shdr)) * header.e_shnum;
    AutoMalloc<ElfW(Shdr)> shdr(size);
    if (!reader.read(header.e_shoff, shdr.ptr, size)) {
        LOG_E("shoff error %lu-%s-%s %d", count, path, p_symbols[0], (int) header.e_shoff);
        return false;
    }

    ElfW(Shdr) *p_sym_tab = 0, *p_str_tab = 0;

    for (ElfW(Shdr) *p_shdr = shdr.ptr; p_shdr < (shdr.ptr + header.e_shnum);
         p_shdr++) {
        if (p_shdr->sh_type == SHT_SYMTAB) {
            p_sym_tab = p_shdr;
        }
        if (p_shdr->sh_type == SHT_STRTAB) {
            p_str_tab = p_shdr;
        }
    }

    if (p_sym_tab == 0 || p_str_tab == 0) {
        LOG_E("no sym or str 93 %lu-%s-%s %p %p", count, path, p_symbols[0], p_sym_tab, p_str_tab);
        return false;
    }

    if (p_sym_tab->sh_size > 1048576 * 10 || p_str_tab->sh_size > 1048576 * 10) {
        LOG_E("too large %lu-%s-%s", count, path, p_symbols[0]);
        return false;
    }

    AutoMalloc<ElfW(Sym)> sym_tab(p_sym_tab->sh_size);
    if (!reader.read(p_sym_tab->sh_offset, sym_tab.ptr, p_sym_tab->sh_size)) {
        LOG_E("95 %lu-%s-%s %d", count, path, p_symbols[0], (int) p_sym_tab->sh_offset);
        return false;
    }

    AutoMalloc<char> str_tab(p_str_tab->sh_size);
    if (!reader.read(p_str_tab->sh_offset, str_tab.ptr, p_str_tab->sh_size)) {
        LOG_E("96 %lu-%s-%s %llu", count, path, p_symbols[0], p_str_tab->sh_offset);
        return false;
    }

    int SymCount = p_sym_tab->sh_size / sizeof(ElfW(Sym));
    for (ElfW(Sym) *pSym = sym_tab.ptr; pSym < sym_tab.ptr + SymCount; pSym++) {
        if (pSym->st_name >= p_str_tab->sh_size) {
            LOG_E("97 %lu-%s-%s %d %d", count, path, p_symbols[0], (int) pSym->st_name,
                  (int) p_str_tab->sh_size);
            return false;
        }
        for (size_t i = 0; i < count; i++) {
            if (::strcmp(p_symbols[i], str_tab.ptr + pSym->st_name) == 0) {
                for (ElfW(Shdr) *p_shdr = shdr.ptr;
                     p_shdr < (shdr.ptr + header.e_shnum); p_shdr++) {
                    if (pSym->st_value >= p_shdr->sh_offset
                        && pSym->st_value
                           < (p_shdr->sh_offset + p_shdr->sh_size)) {
                        p_addresses[i] = pSym->st_value - p_shdr->sh_addr
                                         + p_shdr->sh_offset;
                    }
                }
            }
        }
    }

    return true;
}

