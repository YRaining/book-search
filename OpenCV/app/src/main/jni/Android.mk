LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)


OpenCV_INSTALL_MODULES := on
OpenCV_CAMERA_MODULES := off

OPENCV_LIB_TYPE :=STATIC

ifeq ("$(wildcard $(OPENCV_MK_PATH))","")
include ..\..\..\..\native\jni\OpenCV.mk
else
include $(OPENCV_MK_PATH)
endif

LOCAL_MODULE := OpenCV

LOCAL_SRC_FILES :=com_example_jhon_opencv_OpenCVHelper.cpp
LOCAL_ALLOW_UNDEFINED_SYMBOLS := true
LOCAL_LDLIBS +=  -lm -llog
include $(BUILD_SHARED_LIBRARY)