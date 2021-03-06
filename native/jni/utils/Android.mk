LOCAL_PATH := $(call my-dir)

# All Magisk common code lives here

include $(CLEAR_VARS)
LOCAL_MODULE:= libutils
LOCAL_C_INCLUDES := jni/include $(LOCAL_PATH)/include out/generated
LOCAL_EXPORT_C_INCLUDES := $(LOCAL_C_INCLUDES)
LOCAL_EXPORT_STATIC_LIBRARIES := libcxx
LOCAL_STATIC_LIBRARIES := libcxx
LOCAL_SRC_FILES := \
    missing.cpp \
    new.cpp \
    files.cpp \
    misc.cpp \
    selinux.cpp \
    logging.cpp \
    xwrap.cpp \
    stream.cpp

include $(BUILD_STATIC_LIBRARY)
