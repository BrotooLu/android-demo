LOCAL_PATH := $(call my-dir)

####################################################
# ehook
####################################################

include $(CLEAR_VARS)

LOCAL_CFLAGS += -Werror

ifeq ($(NDK_DEBUG), 1)
    LOCAL_LDFLAGS += -llog
    LOCAL_CFLAGS += -DDBG
endif

LOCAL_MODULE := ehook
LOCAL_SRC_FILES := lib_ehook.cpp\
    ehook.cpp\
    elf_reader.cpp


include $(BUILD_SHARED_LIBRARY)
