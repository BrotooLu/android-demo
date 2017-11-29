LOCAL_PATH := $(call my-dir)

####################################################
# bsdiff
####################################################
include $(CLEAR_VARS)

LOCAL_C_INCLUDES += $(LOCAL_PATH)/../bzip2
LOCAL_LDFLAGS += -pie

LOCAL_MODULE := bsdiff
LOCAL_SRC_FILES := bsdiff.c
LOCAL_STATIC_LIBRARIES := bzip

include $(BUILD_EXECUTABLE)

####################################################
# bspatch
####################################################
include $(CLEAR_VARS)

LOCAL_C_INCLUDES += $(LOCAL_PATH)/../bzip2
LOCAL_LDFLAGS += -pie

LOCAL_MODULE := bspatch
LOCAL_SRC_FILES := bspatch.c
LOCAL_STATIC_LIBRARIES := bzip

include $(BUILD_EXECUTABLE)
