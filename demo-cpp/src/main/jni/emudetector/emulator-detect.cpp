//
// Created by Bro2 on 2017/9/16.
//

#include <stdio.h>
#include <unistd.h>
#include <sys/mman.h>

extern "C" int detectArm();

bool isEmulator() {
    size_t pageSize = (size_t)getpagesize();
    void *exec = mmap(NULL, pageSize, PROT_EXEC|PROT_WRITE|PROT_READ, MAP_ANONYMOUS | MAP_SHARED, -1, (off_t) 0);
#ifdef __LP64__
    memcpy(exec, (void**)detectArm, 21 * 4);
#else
    memcpy(exec, (void**)detectArm, 17 * 4);
#endif
    printf("mmap result: %p\n", exec);
    int res = ((int (*)())exec)();
    printf("execute result: %d\n", res);
    bool emulator = false;
    if (res == 1) {
        emulator = true;
    } else if (res == 2) {
        emulator = false;
    } else {
        printf("impossible things happen\n");
    }
    munmap(exec, pageSize);
    return emulator;
}

int main() {
    bool res = isEmulator();
    if (res) {
        printf("emulator\n");
    } else {
        printf("real machine\n");
    }
    return 0;
}