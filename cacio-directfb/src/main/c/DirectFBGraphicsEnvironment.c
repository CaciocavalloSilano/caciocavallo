
#include <directfb.h>
#include "net_java_openjdk_cacio_directfb_DirectFBGraphicsEnvironment.h"

JNIEXPORT jlong JNICALL Java_net_java_openjdk_cacio_directfb_DirectFBGraphicsEnvironment_createDirectFB(JNIEnv* env, jobject thiz) {

  IDirectFB* dfb = NULL;
  int dummy_argc = 0;
  char* dummy_argv[0];
  DirectFBInit(&dummy_argc, &dummy_argv);
  DirectFBCreate(&dfb);
  return (long) dfb;
}
