LOCAL_PATH := $(call my-dir)

####################################################
# emulator-detect
####################################################
include $(CLEAR_VARS)

LOCAL_SRC_FILES := emudetector/emulator-detect.cpp

ifeq ($(TARGET_ARCH_ABI), armeabi)
    LOCAL_MODULE := emulator-detect
    LOCAL_SRC_FILES += emudetector/detect-arm.s
else
    ifeq ($(TARGET_ARCH_ABI), arm64-v8a)
        LOCAL_MODULE := emulator-detect64
		LOCAL_SRC_FILES += emudetector/detect-arm64.s
	endif
endif

LOCAL_LDFLAGS += -pie

include $(BUILD_EXECUTABLE)


####################################################
# bsdiff
####################################################
include $(CLEAR_VARS)

LOCAL_MODULE := bsdiff
LOCAL_SRC_FILES := bsdiff/bsdiff.c \
    bsdiff/bspatch.c \
    bzip2/bzip2.c \
    bzip2/bzip.c \
    bzip2/blocksort.c \
    bzip2/compress.c \
    bzip2/crctable.c \
    bzip2/decompress.c \
    bzip2/dlltest.c \
    bzip2/huffman.c \
    bzip2/mk251.c \
    bzip2/randtable.c \
    bzip2/spewG.c \
    bzip2/unzcrash.c
#LOCAL_LDLIBS := -lbzip2

LOCAL_LDFLAGS += -pie
LOCAL_EXPORT_C_INCLUDES := bzip2

include $(BUILD_EXECUTABLE)
