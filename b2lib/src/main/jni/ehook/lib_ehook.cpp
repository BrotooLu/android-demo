#include "comm.h"
#include "ehook.h"

#include <jni.h>
#include <sys/system_properties.h>
#include <stdlib.h>

int sdk_int = 0;

jint JNI_OnLoad(JavaVM *jvm, void *args) {

    char buffer[128] = {0};
    __system_property_get("ro.build.version.sdk", buffer);
    sdk_int = atoi(buffer);
    LOG_D("sdk int: %d", sdk_int);
    get_original_fn(0);

    return JNI_VERSION_1_4;
}