#ifndef EHOOK_COMM_H
#define EHOOK_COMM_H

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

#define EHOOK_JAVA_CLAZZ_NAME "com/bro2/ehook/EHook"
#define EHOOK_JAVA_DISPATCH_METHOD "dispatchHook"
#define EHOOK_JAVA_DISPATCH_METHOD_SIGNATURE "J[Ljava/lang/Object;"

extern int sdk_int;

#endif