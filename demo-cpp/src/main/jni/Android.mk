LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_SRC_FILES := emulator-detect.cpp

ifeq ($(TARGET_ARCH_ABI), armeabi)
    LOCAL_MODULE := emulator-detect
    LOCAL_SRC_FILES += detect-arm.s
else
    ifeq ($(TARGET_ARCH_ABI), arm64-v8a)
        LOCAL_MODULE := emulator-detect64
		LOCAL_SRC_FILES += detect-arm64.s
	endif
endif

LOCAL_LDFLAGS += -pie

include $(BUILD_EXECUTABLE)
