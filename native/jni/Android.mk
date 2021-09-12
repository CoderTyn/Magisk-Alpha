LOCAL_PATH := $(call my-dir)

########################
# Binaries
########################

ifdef B_MAGISK

include $(CLEAR_VARS)
LOCAL_MODULE := magisk
LOCAL_STATIC_LIBRARIES := libnanopb libsystemproperties libutils libphmap libxhook

LOCAL_SRC_FILES := \
    core/applets.cpp \
    core/magisk.cpp \
    core/daemon.cpp \
    core/bootstages.cpp \
    core/socket.cpp \
    core/db.cpp \
    core/scripting.cpp \
    core/restorecon.cpp \
    core/module.cpp \
    core/logging.cpp \
    core/thread.cpp \
    magiskhide/magiskhide.cpp \
    magiskhide/hide_utils.cpp \
    magiskhide/hide_policy.cpp \
    magiskhide/proc_monitor.cpp \
    resetprop/persist_properties.cpp \
    resetprop/resetprop.cpp \
    su/su.cpp \
    su/connect.cpp \
    su/pts.cpp \
    su/su_daemon.cpp \
    zygisk/entry.cpp \
    zygisk/utils.cpp \
    zygisk/hook.cpp \
    zygisk/memory.cpp

LOCAL_LDLIBS := -llog

include $(BUILD_EXECUTABLE)

endif

include $(CLEAR_VARS)

ifdef B_INIT

LOCAL_MODULE := magiskinit
LOCAL_STATIC_LIBRARIES := libsepol libxz libutils

LOCAL_SRC_FILES := \
    init/init.cpp \
    init/mount.cpp \
    init/rootdir.cpp \
    init/getinfo.cpp \
    init/twostage.cpp \
    init/raw_data.cpp \
    core/socket.cpp \
    magiskpolicy/sepolicy.cpp \
    magiskpolicy/magiskpolicy.cpp \
    magiskpolicy/rules.cpp \
    magiskpolicy/policydb.cpp \
    magiskpolicy/statement.cpp \
    magiskboot/pattern.cpp

LOCAL_LDFLAGS := -static
include $(BUILD_EXECUTABLE)

endif

ifdef B_BOOT

include $(CLEAR_VARS)
LOCAL_MODULE := magiskboot
LOCAL_STATIC_LIBRARIES := libmincrypt liblzma liblz4 libbz2 libfdt libutils libz libzopfli

LOCAL_SRC_FILES := \
    magiskboot/main.cpp \
    magiskboot/bootimg.cpp \
    magiskboot/hexpatch.cpp \
    magiskboot/compress.cpp \
    magiskboot/format.cpp \
    magiskboot/dtb.cpp \
    magiskboot/ramdisk.cpp \
    magiskboot/pattern.cpp \
    magiskboot/cpio.cpp

LOCAL_LDFLAGS := -static
include $(BUILD_EXECUTABLE)

endif

ifdef B_POLICY

include $(CLEAR_VARS)
LOCAL_MODULE := magiskpolicy
LOCAL_STATIC_LIBRARIES := libsepol libutils

LOCAL_SRC_FILES := \
    core/applet_stub.cpp \
    magiskpolicy/sepolicy.cpp \
    magiskpolicy/magiskpolicy.cpp \
    magiskpolicy/rules.cpp \
    magiskpolicy/policydb.cpp \
    magiskpolicy/statement.cpp

LOCAL_CFLAGS := -DAPPLET_STUB_MAIN=magiskpolicy_main
LOCAL_LDFLAGS := -static
include $(BUILD_EXECUTABLE)

endif

ifdef B_PROP

include $(CLEAR_VARS)
LOCAL_MODULE := resetprop
LOCAL_STATIC_LIBRARIES := libnanopb libsystemproperties libutils

LOCAL_SRC_FILES := \
    core/applet_stub.cpp \
    resetprop/persist_properties.cpp \
    resetprop/resetprop.cpp \

LOCAL_CFLAGS := -DAPPLET_STUB_MAIN=resetprop_main
LOCAL_LDFLAGS := -static
include $(BUILD_EXECUTABLE)

endif

ifdef B_TEST
ifneq (,$(wildcard jni/test.cpp))

include $(CLEAR_VARS)
LOCAL_MODULE := test
LOCAL_STATIC_LIBRARIES := libutils libphmap
LOCAL_SRC_FILES := test.cpp
include $(BUILD_EXECUTABLE)

endif
endif

ifdef B_BB

include jni/external/busybox/Android.mk

endif

########################
# Libraries
########################
include jni/utils/Android.mk
include jni/external/Android.mk
