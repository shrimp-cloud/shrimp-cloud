package org.opencv.photo;

// C++: class TonemapMantiuk
/**
 * This algorithm transforms image to contrast using gradients on all levels of gaussian pyramid,
 * transforms contrast values to HVS response and scales the response. After this the image is
 * reconstructed from new contrast values.
 *
 * For more information see CITE: MM06 .
 */
public class TonemapMantiuk extends Tonemap {

    protected TonemapMantiuk(long addr) { super(addr); }

    // internal usage only
    public static TonemapMantiuk __fromPtr__(long addr) { return new TonemapMantiuk(addr); }

    //
    // C++:  float cv::TonemapMantiuk::getScale()
    //

    public float getScale() {
        return getScale_0(nativeObj);
    }


    //
    // C++:  void cv::TonemapMantiuk::setScale(float scale)
    //

    public void setScale(float scale) {
        setScale_0(nativeObj, scale);
    }


    //
    // C++:  float cv::TonemapMantiuk::getSaturation()
    //

    public float getSaturation() {
        return getSaturation_0(nativeObj);
    }


    //
    // C++:  void cv::TonemapMantiuk::setSaturation(float saturation)
    //

    public void setSaturation(float saturation) {
        setSaturation_0(nativeObj, saturation);
    }


    @Override
    protected void finalize() throws Throwable {
        delete(nativeObj);
    }



    // C++:  float cv::TonemapMantiuk::getScale()
    private static native float getScale_0(long nativeObj);

    // C++:  void cv::TonemapMantiuk::setScale(float scale)
    private static native void setScale_0(long nativeObj, float scale);

    // C++:  float cv::TonemapMantiuk::getSaturation()
    private static native float getSaturation_0(long nativeObj);

    // C++:  void cv::TonemapMantiuk::setSaturation(float saturation)
    private static native void setSaturation_0(long nativeObj, float saturation);

    // native support for java finalize()
    private static native void delete(long nativeObj);

}
