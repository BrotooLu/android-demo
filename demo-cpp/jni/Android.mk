LOCAL_PATH := $(call my-dir)

####################################################
# emulator-detect
####################################################
#include $(CLEAR_VARS)
#
#LOCAL_SRC_FILES := emudetector/emulator-detect.cpp
#
#ifeq ($(TARGET_ARCH_ABI), armeabi)
#    LOCAL_MODULE := emulator-detect
#    LOCAL_SRC_FILES += emudetector/detect-arm.s
#else
#    ifeq ($(TARGET_ARCH_ABI), arm64-v8a)
#        LOCAL_MODULE := emulator-detect64
#		LOCAL_SRC_FILES += emudetector/detect-arm64.s
#	endif
#endif
#
#LOCAL_LDFLAGS += -pie
#
#include $(BUILD_EXECUTABLE)

####################################################
# bzip
####################################################
include $(CLEAR_VARS)

LOCAL_MODULE := bzip
LOCAL_SRC_FILES := bzip2/blocksort.c \
    bzip2/huffman.c \
    bzip2/crctable.c \
    bzip2/randtable.c \
    bzip2/compress.c \
    bzip2/decompress.c \
    bzip2/bzlib.c

include $(BUILD_STATIC_LIBRARY)

####################################################
# bsdiff
####################################################
include $(CLEAR_VARS)

LOCAL_MODULE := bsdiff
LOCAL_SRC_FILES := bsdiff/bsdiff.c
LOCAL_STATIC_LIBRARY := libbzip
LOCAL_LDFLAGS += -pie
LOCAL_C_INCLUDES := bzip2

include $(BUILD_EXECUTABLE)

####################################################
# bspatch
####################################################
include $(CLEAR_VARS)

LOCAL_MODULE := bspatch
LOCAL_SRC_FILES := bsdiff/bspatch.c \
    bzip2/blocksort.c \
    bzip2/huffman.c \
    bzip2/crctable.c \
    bzip2/randtable.c \
    bzip2/compress.c \
    bzip2/decompress.c \
    bzip2/bzlib.c
#LOCAL_LDLIBS := -lbzip2

LOCAL_LDFLAGS += -pie
LOCAL_C_INCLUDES := bzip2

include $(BUILD_EXECUTABLE)
