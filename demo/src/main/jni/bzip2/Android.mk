LOCAL_PATH := $(call my-dir)

####################################################
# bzip
####################################################
include $(CLEAR_VARS)

LOCAL_MODULE := bzip
LOCAL_SRC_FILES := blocksort.c \
    huffman.c \
    crctable.c \
    randtable.c \
    compress.c \
    decompress.c \
    bzlib.c

include $(BUILD_STATIC_LIBRARY)