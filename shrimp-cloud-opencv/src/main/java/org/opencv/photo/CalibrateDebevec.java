package org.opencv.photo;

// C++: class CalibrateDebevec
/**
 * Inverse camera response function is extracted for each brightness value by minimizing an objective
 * function as linear system. Objective function is constructed using pixel values on the same position
 * in all images, extra term is added to make the result smoother.
 *
 * For more information see CITE: DM97 .
 */
public class CalibrateDebevec extends CalibrateCRF {

    protected CalibrateDebevec(long addr) { super(addr); }

    // internal usage only
    public static CalibrateDebevec __fromPtr__(long addr) { return new CalibrateDebevec(addr); }

    //
    // C++:  float cv::CalibrateDebevec::getLambda()
    //

    public float getLambda() {
        return getLambda_0(nativeObj);
    }


    //
    // C++:  void cv::CalibrateDebevec::setLambda(float lambda)
    //

    public void setLambda(float lambda) {
        setLambda_0(nativeObj, lambda);
    }


    //
    // C++:  int cv::CalibrateDebevec::getSamples()
    //

    public int getSamples() {
        return getSamples_0(nativeObj);
    }


    //
    // C++:  void cv::CalibrateDebevec::setSamples(int samples)
    //

    public void setSamples(int samples) {
        setSamples_0(nativeObj, samples);
    }


    //
    // C++:  bool cv::CalibrateDebevec::getRandom()
    //

    public boolean getRandom() {
        return getRandom_0(nativeObj);
    }


    //
    // C++:  void cv::CalibrateDebevec::setRandom(bool random)
    //

    public void setRandom(boolean random) {
        setRandom_0(nativeObj, random);
    }


    @Override
    protected void finalize() throws Throwable {
        delete(nativeObj);
    }



    // C++:  float cv::CalibrateDebevec::getLambda()
    private static native float getLambda_0(long nativeObj);

    // C++:  void cv::CalibrateDebevec::setLambda(float lambda)
    private static native void setLambda_0(long nativeObj, float lambda);

    // C++:  int cv::CalibrateDebevec::getSamples()
    private static native int getSamples_0(long nativeObj);

    // C++:  void cv::CalibrateDebevec::setSamples(int samples)
    private static native void setSamples_0(long nativeObj, int samples);

    // C++:  bool cv::CalibrateDebevec::getRandom()
    private static native boolean getRandom_0(long nativeObj);

    // C++:  void cv::CalibrateDebevec::setRandom(bool random)
    private static native void setRandom_0(long nativeObj, boolean random);

    // native support for java finalize()
    private static native void delete(long nativeObj);

}
