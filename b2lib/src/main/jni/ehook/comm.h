#ifndef EHOOK_COMM_H
#define EHOOK_COMM_H

#include <stdint.h>

#define TAG "ehook"

#define LOG_DEBUG 3
#define LOG_ERROR 6

#ifdef DBG

#include <android/log.h>

#define LOG_D(...) ((void)__android_log_print(LOG_DEBUG, TAG, __VA_ARGS__))
#define LOG_E(...) ((void)__android_log_print(LOG_ERROR, TAG, __VA_ARGS__))
#else
#define LOG_D(...) ((void) 0)
#define LOG_E(...) ((void) 0)
#endif

#ifndef MAX_PATH
#define MAX_PATH    256
#endif

#ifndef MAX_LINE
#define MAX_LINE    2048
#endif

#define _S(n) #n
#define S(n) _S(n)

#define EHOOK_JAVA_CLAZZ_NAME "com/bro2/ehook/EHook"
#define EHOOK_JAVA_DISPATCH_METHOD "dispatchHook"
#define EHOOK_JAVA_DISPATCH_METHOD_SIGNATURE "J[Ljava/lang/Object;"

#if defined(__LP64__)
#define LINKER                "linker64"
#define LINKER_PATH "/system/bin/linker64"
#else
#define LINKER              	"linker"
#define LINKER_PATH "/system/bin/linker"
#endif

extern int sdk_int;

#endif