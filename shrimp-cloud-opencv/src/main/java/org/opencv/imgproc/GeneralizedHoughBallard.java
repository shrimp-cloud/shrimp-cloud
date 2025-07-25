package org.opencv.imgproc;

// C++: class GeneralizedHoughBallard
/**
 * finds arbitrary template in the grayscale image using Generalized Hough Transform
 *
 * Detects position only without translation and rotation CITE: Ballard1981 .
 */
public class GeneralizedHoughBallard extends GeneralizedHough {

    protected GeneralizedHoughBallard(long addr) { super(addr); }

    // internal usage only
    public static GeneralizedHoughBallard __fromPtr__(long addr) { return new GeneralizedHoughBallard(addr); }

    //
    // C++:  void cv::GeneralizedHoughBallard::setLevels(int levels)
    //

    public void setLevels(int levels) {
        setLevels_0(nativeObj, levels);
    }


    //
    // C++:  int cv::GeneralizedHoughBallard::getLevels()
    //

    public int getLevels() {
        return getLevels_0(nativeObj);
    }


    //
    // C++:  void cv::GeneralizedHoughBallard::setVotesThreshold(int votesThreshold)
    //

    public void setVotesThreshold(int votesThreshold) {
        setVotesThreshold_0(nativeObj, votesThreshold);
    }


    //
    // C++:  int cv::GeneralizedHoughBallard::getVotesThreshold()
    //

    public int getVotesThreshold() {
        return getVotesThreshold_0(nativeObj);
    }


    @Override
    protected void finalize() throws Throwable {
        delete(nativeObj);
    }



    // C++:  void cv::GeneralizedHoughBallard::setLevels(int levels)
    private static native void setLevels_0(long nativeObj, int levels);

    // C++:  int cv::GeneralizedHoughBallard::getLevels()
    private static native int getLevels_0(long nativeObj);

    // C++:  void cv::GeneralizedHoughBallard::setVotesThreshold(int votesThreshold)
    private static native void setVotesThreshold_0(long nativeObj, int votesThreshold);

    // C++:  int cv::GeneralizedHoughBallard::getVotesThreshold()
    private static native int getVotesThreshold_0(long nativeObj);

    // native support for java finalize()
    private static native void delete(long nativeObj);

}
