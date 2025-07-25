package org.opencv.photo;

// C++: class TonemapDrago
/**
 * Adaptive logarithmic mapping is a fast global tonemapping algorithm that scales the image in
 * logarithmic domain.
 *
 * Since it's a global operator the same function is applied to all the pixels, it is controlled by the
 * bias parameter.
 *
 * Optional saturation enhancement is possible as described in CITE: FL02 .
 *
 * For more information see CITE: DM03 .
 */
public class TonemapDrago extends Tonemap {

    protected TonemapDrago(long addr) { super(addr); }

    // internal usage only
    public static TonemapDrago __fromPtr__(long addr) { return new TonemapDrago(addr); }

    //
    // C++:  float cv::TonemapDrago::getSaturation()
    //

    public float getSaturation() {
        return getSaturation_0(nativeObj);
    }


    //
    // C++:  void cv::TonemapDrago::setSaturation(float saturation)
    //

    public void setSaturation(float saturation) {
        setSaturation_0(nativeObj, saturation);
    }


    //
    // C++:  float cv::TonemapDrago::getBias()
    //

    public float getBias() {
        return getBias_0(nativeObj);
    }


    //
    // C++:  void cv::TonemapDrago::setBias(float bias)
    //

    public void setBias(float bias) {
        setBias_0(nativeObj, bias);
    }


    @Override
    protected void finalize() throws Throwable {
        delete(nativeObj);
    }



    // C++:  float cv::TonemapDrago::getSaturation()
    private static native float getSaturation_0(long nativeObj);

    // C++:  void cv::TonemapDrago::setSaturation(float saturation)
    private static native void setSaturation_0(long nativeObj, float saturation);

    // C++:  float cv::TonemapDrago::getBias()
    private static native float getBias_0(long nativeObj);

    // C++:  void cv::TonemapDrago::setBias(float bias)
    private static native void setBias_0(long nativeObj, float bias);

    // native support for java finalize()
    private static native void delete(long nativeObj);

}
