/**
 * Created by JONG HO BAEK on 20,February,2019
 * email: icanmobile@gmail.com
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <inttypes.h>
#include <pthread.h>
#include <jni.h>
#include <android/log.h>
#include <android/bitmap.h>
#include <assert.h>


// Android log function wrappers
static const char* kTAG = "NativeEngine";
#define LOGI(...) \
  ((void)__android_log_print(ANDROID_LOG_INFO, kTAG, __VA_ARGS__))
#define LOGW(...) \
  ((void)__android_log_print(ANDROID_LOG_WARN, kTAG, __VA_ARGS__))
#define LOGE(...) \
  ((void)__android_log_print(ANDROID_LOG_ERROR, kTAG, __VA_ARGS__))





//region create and delete Filter class object
#include "ldr/LDR.h"
PCLDR g_pFilter = NULL;
PCLDR createFilter(int filter) {
    if (g_pFilter != NULL) {
        delete g_pFilter;
        g_pFilter = NULL;
    }

    g_pFilter = new CLdr();
    return g_pFilter;
}
void deleteFilter() {
    if (g_pFilter != NULL) {
        delete g_pFilter;
        g_pFilter = NULL;
    }
}
//endregion




#ifdef __cplusplus
extern "C" {
#endif

// photo effect context
typedef struct photo_effect_context {
    JavaVM  *javaVM;
    jclass   photoEffectClz;
    pthread_mutex_t  lock;
} PhotoEffectContext;
PhotoEffectContext g_ctx;

// native bitmap structure
typedef struct jni_bitmap {
    uint8_t* _rgb888;
    uint8_t* _a8;
    AndroidBitmapInfo _Info;
} JniBitmap;
typedef JniBitmap* PJNI_BITMAP;

// thread parameter structure
typedef struct thread_parameter {
    PJNI_BITMAP pJniBitmap;
    PhotoEffectContext* pCtx;
}ThreadParameter;
typedef ThreadParameter* PTHREAD_PARAMETER;


// progress status
int         g_progress = 0;

// the definitions of local methods
void*       toneMapping(void* context);
void        progressCallback(int progress);
PJNI_BITMAP getJNIBitmap(uint8_t* bitmap, AndroidBitmapInfo info);
jobject     getBitmap(JNIEnv* env, PJNI_BITMAP pJniBitmap);



/**
 * JNI_OnLoad method
 * Save Java VM and find class to return the processed bitmap.
 */
JNIEXPORT jint JNICALL
JNI_OnLoad(JavaVM* vm, void* reserved) {
    JNIEnv* env;
    memset(&g_ctx, 0, sizeof(g_ctx));

    g_ctx.javaVM = vm;
    if (vm->GetEnv((void**)&env, JNI_VERSION_1_6) != JNI_OK) {
        return JNI_ERR; // JNI version not supported.
    }
    jclass clz = env->FindClass("com/icanmobile/effects/PhotoEffect");
    g_ctx.photoEffectClz = (jclass)env->NewGlobalRef(clz);
    return  JNI_VERSION_1_6;
}


/**
 * intiEngine method
 * create filter object and call init() method.
 */
JNIEXPORT void JNICALL
Java_com_icanmobile_effects_PhotoEffect_initEngine(JNIEnv *env, jobject instance, jint filter) {
    // create filter class object
    createFilter(filter);

    float power = 30000/1000.0f;
    float blur = 80000/1000.0f;

    // call init method with parameters such as power and blur values
    if (g_pFilter != NULL)
        g_pFilter->init(power, blur);
}

/**
 * deinitEngine method
 * call deinit() method of the current filter object.
 */
JNIEXPORT void JNICALL
Java_com_icanmobile_effects_PhotoEffect_deinitEngine(JNIEnv *env, jobject instance) {

    // call deinit method of filter object.
    if (g_pFilter != NULL)
        g_pFilter->deinit();
}

/**
 * applyEffect method
 * apply the filter effect to input bitmap object
 */
JNIEXPORT void JNICALL
Java_com_icanmobile_effects_PhotoEffect_applyEffect(JNIEnv *env, jobject instance, jobject bitmap) {
    if (g_pFilter == NULL) return;

    AndroidBitmapInfo  info;
    void*              pixels;
    int                ret;

    PJNI_BITMAP      pJniBitmap;
    pthread_t       threadInfo_;
    pthread_attr_t  threadAttr_;


    //region the native bitmap preparation
    // get bitmap information
    if ((ret = AndroidBitmap_getInfo(env, bitmap, &info)) < 0) {
        LOGE("AndroidBitmap_getInfo() failed ! error=%d", ret);
        return;
    }

    if (info.format != ANDROID_BITMAP_FORMAT_RGBA_8888) {
        LOGE("Bitmap format is not RGBA_8888 !");
        return;
    }

    // create a native bitmap object
    if ((ret = AndroidBitmap_lockPixels(env, bitmap, &pixels)) < 0) {
        LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
    }
    pJniBitmap = getJNIBitmap((uint8_t*)pixels, info);
    AndroidBitmap_unlockPixels(env, bitmap);
    //endregion



    // set progress as 0
    g_progress = 0;




    //region native effect process with a thread
    // create a thread and run toneMapping() method for effect processing
    pthread_attr_init(&threadAttr_);
    pthread_attr_setdetachstate(&threadAttr_, PTHREAD_CREATE_DETACHED);
    pthread_mutex_init(&g_ctx.lock, NULL);

    // prepare thread parameter
    PTHREAD_PARAMETER pParam = (PTHREAD_PARAMETER) malloc(sizeof(ThreadParameter));
    pParam->pCtx = &g_ctx;
    pParam->pJniBitmap = pJniBitmap;

    int result  = pthread_create( &threadInfo_, &threadAttr_, toneMapping, pParam);
    assert(result == 0);
    pthread_attr_destroy(&threadAttr_);
    (void)result;
    //endregion
}

/**
 * effect progress status method
 */
JNIEXPORT jint JNICALL
Java_com_icanmobile_effects_PhotoEffect_progressEffect(JNIEnv *env, jobject instance) {
    if (g_pFilter == NULL) return -1;
    return g_progress;
}


/**
 * tone mapping method of thread.
 * This method calls toneMapping method of filter and return the processed bitmap to Java object.
 */
void* toneMapping(void* context) {
    PTHREAD_PARAMETER param = (PTHREAD_PARAMETER) context;
    PJNI_BITMAP pJniBitmap = param->pJniBitmap;
    PhotoEffectContext* pCtx = param->pCtx;


    JavaVM *javaVM = pCtx->javaVM;
    JNIEnv *env;
    jint res = javaVM->GetEnv((void**)&env, JNI_VERSION_1_6);
    if (res != JNI_OK) {
        res = javaVM->AttachCurrentThread(&env, NULL);
        if (JNI_OK != res) {
            LOGE("Failed to AttachCurrentThread, ErrorCode = %d", res);
            return NULL;
        }
    }

    //region call toneMapping() method of filter and create bitmap object and set progressCallback method using function callback.
    pthread_mutex_lock(&pCtx->lock);
    if(g_pFilter != NULL)
        g_pFilter->toneMapping(pJniBitmap->_rgb888, pJniBitmap->_Info.width, pJniBitmap->_Info.height, progressCallback);
    jobject bitmap = getBitmap(env, pJniBitmap);
    pthread_mutex_unlock(&pCtx->lock);
    //endregion


    // set progress status as 90
    progressCallback(90);



    //region call onEffectResult method of Java object.
    jmethodID on_effect_result_method_id = env->GetStaticMethodID(pCtx->photoEffectClz,
                                                                  "onEffectResult",
                                                                  "(Landroid/graphics/Bitmap;)V");
    // set progress status as 95
    progressCallback(95);

    env->CallStaticVoidMethod(pCtx->photoEffectClz, on_effect_result_method_id, bitmap);
    //endregion


    javaVM->DetachCurrentThread();
    pthread_mutex_destroy(&pCtx->lock);

    // set progress status as 100
    progressCallback(100);
    return context;
}

/**
 * progress callback method
 * this method will be sent to filter object to return the progress status.
 */
void progressCallback(int progress) {
    LOGI("progressCallback : progress = %d", progress);
    g_progress = progress;
}

/**
 * create a native bitmap from the input Java bitmap.
 */
PJNI_BITMAP getJNIBitmap(uint8_t* bitmap, AndroidBitmapInfo info) {
    if (bitmap == NULL) return NULL;

    PJNI_BITMAP pJniBitmap = (PJNI_BITMAP)malloc(sizeof(JniBitmap));
    pJniBitmap->_rgb888  = new uint8_t[info.width * info.height * 3];
    pJniBitmap->_a8      = new uint8_t[info.width * info.height];
    pJniBitmap->_Info = info;

    uint8_t* src = bitmap;
    uint8_t* dstRGB = pJniBitmap->_rgb888;
    uint8_t* dstA = pJniBitmap->_a8;

    for (int i = 0; i < info.width * info.height; i++) {
        *dstRGB++ = *src++;
        *dstRGB++ = *src++;
        *dstRGB++ = *src++;
        *dstA++ = *src++;
    }

    return pJniBitmap;
}

/**
 * create a Java bitmap from the input native bitmap.
 */
jobject getBitmap(JNIEnv* env, PJNI_BITMAP pJniBitmap) {
    if (pJniBitmap == NULL) return NULL;

    //region creating a new bitmap to put the pixels into it - using Bitmap Bitmap.createBitmap (int width, int height, Bitmap.Config config)
    jclass bitmapCls = env->FindClass("android/graphics/Bitmap");
    jmethodID createBitmapFunction = env->GetStaticMethodID(bitmapCls, "createBitmap", "(IILandroid/graphics/Bitmap$Config;)Landroid/graphics/Bitmap;");
    jstring configName = env->NewStringUTF("ARGB_8888");
    jclass bitmapConfigClass = env->FindClass("android/graphics/Bitmap$Config");
    jmethodID valueOfBitmapConfigFunction = env->GetStaticMethodID(bitmapConfigClass, "valueOf", "(Ljava/lang/String;)Landroid/graphics/Bitmap$Config;");
    jobject bitmapConfig = env->CallStaticObjectMethod(bitmapConfigClass, valueOfBitmapConfigFunction, configName);
    jobject bitmap = env->CallStaticObjectMethod(bitmapCls, createBitmapFunction, pJniBitmap->_Info.width, pJniBitmap->_Info.height, bitmapConfig);
    //endregion


    //region putting the pixels into the new bitmap
    int ret;
    void* bitmapPixels;
    if ((ret = AndroidBitmap_lockPixels(env, bitmap, &bitmapPixels)) < 0)
    {
        LOGE("AndroidBitmap_lockPixels() failed ! error=%d", ret);
        return NULL;
    }

    AndroidBitmapInfo info = pJniBitmap->_Info;
    uint8_t* srcRGB = pJniBitmap->_rgb888;
    uint8_t* srcA = pJniBitmap->_a8;
    uint8_t* dst = (uint8_t*)bitmapPixels;

    for (int i = 0; i < info.width * info.height; i++) {
        *dst++ = *srcRGB++;
        *dst++ = *srcRGB++;
        *dst++ = *srcRGB++;
        *dst++ = *srcA++;
    }

    AndroidBitmap_unlockPixels(env, bitmap);
    //endregion


    return bitmap;
}


#ifdef __cplusplus
}
#endif
