
//
// Created by Brotoo on 2017/9/16.
//

#include <stdio.h>
#include <unistd.h>
#include <sys/mman.h>
#include <sys/errno.h>
#include <string.h>

extern "C" int detect_arm();

static void *g_shared_mem = MAP_FAILED;
static volatile bool g_has_invoked = false;
static volatile bool g_is_emulator = false;

void dump() {
    unsigned char *p = (unsigned char *) g_shared_mem;
    for (int i = 0; i < 50; ++i) {
        char buf[80] = {0};
        for (int j = 0; j < 16; ++j) {
            sprintf(buf, "%s %02x", buf, p[i * 16 + j]);
        }
        printf("%s\n", buf);
    }
}

bool is_emulator() {
    if (g_has_invoked || g_shared_mem == MAP_FAILED) {
        return g_is_emulator;
    }

    printf("start execute\n");
    g_has_invoked = true;
    int res = ((int (*)()) g_shared_mem)();
    printf("execute result: %d\n", res);
    dump();
    if (res == 1) {
        g_is_emulator = true;
    } else if (res == 2) {
        g_is_emulator = false;
    } else {
        printf("impossible things happen: %d\n", res);
    }
    return g_is_emulator;
}


int main() {
    size_t page_size = (size_t) getpagesize();
    do {
        g_shared_mem = mmap(NULL, page_size, PROT_EXEC | PROT_WRITE | PROT_READ,
                            MAP_ANONYMOUS | MAP_PRIVATE, -1, (off_t) 0);
    } while (errno == EINTR);

    if (g_shared_mem == MAP_FAILED) {
        int err = errno;
        printf("mmap err: %s\n", strerror(err));
        return 0;
    }

    printf("JNI_OnLoad mmap result: %p\n", g_shared_mem);
#ifdef __LP64__
    memcpy(g_shared_mem, (void **) detect_arm, 21 * 4);
#else
    memcpy(g_shared_mem, (void **) detect_arm, 17 * 4);
#endif
    dump();

    if (is_emulator()) {
        printf("detect result: emulator\n");
    } else {
        printf("detect result: real machine\n");
    }

    return 0;
}
