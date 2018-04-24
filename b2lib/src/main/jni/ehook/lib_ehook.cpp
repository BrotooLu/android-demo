#include "comm.h"
#include "ehook.h"
#include "so_info.h"
#include "os_symbol.h"

#include <jni.h>
#include <sys/system_properties.h>
#include <stdlib.h>
#include <unistd.h>

int sdk_int = 0;

extern PUnionSoInfo (*g_get_libdl_info)(const char *);

PUnionSoInfo (*g_get_libdl_info)(const char *) = NULL;

jint JNI_OnLoad(JavaVM *jvm, void *args) {

    char buffer[128] = {0};
    __system_property_get("ro.build.version.sdk", buffer);
    sdk_int = atoi(buffer);

    if (sdk_int >= 24) {
        OsSymbol get_libdl_infov(const_cast<char *>(LINKER), const_cast<char *>(LINKER_PATH),
                                 "__dl__Z14get_libdl_infov");
        find_os_symbol(&get_libdl_infov, 1);
        if (get_libdl_infov.val != 0) {
            g_get_libdl_info = reinterpret_cast<PUnionSoInfo (*)(
                    const char *)>(get_libdl_infov.val);
        }
    }

    LOG_D("sdk int: %d g_get_libdl_info: %p", sdk_int, g_get_libdl_info);
    PUnionSoInfo so_info = g_get_libdl_info(LINKER_PATH);
#ifdef __LP64__
    SoInfo64Android6 *p_so_info = so_info.p_so64;
    do {
        if (p_so_info == NULL) {
            break;
        }

        LOG_D("64name: %s addr: %p", p_so_info->link_map_head.l_name, p_so_info);
    } while (1);
#else
    SoInfo* p_so_info = so_info.p_so;
    do {
        if (p_so_info == NULL) {
            break;
        }

        LOG_D("32name: %s addr: %p", p_so_info->name, p_so_info);
    } while (1);
#endif
    return JNI_VERSION_1_4;
}