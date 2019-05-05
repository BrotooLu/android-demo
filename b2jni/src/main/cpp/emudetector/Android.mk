LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := emulator-detect
LOCAL_SRC_FILES := emulator_detect.cpp

ifeq ($(TARGET_ARCH_ABI), armeabi-v7a)
    LOCAL_SRC_FILES += detect_arm.S
else
    ifeq ($(TARGET_ARCH_ABI), arm64-v8a)
		LOCAL_SRC_FILES += detect_arm64.S
	endif
endif

LOCAL_LDFLAGS += -pie
LOCAL_CFLAGS += -Werror

include $(BUILD_EXECUTABLE)
