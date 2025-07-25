package org.opencv.features2d;

import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;

// C++: class BRISK
/**
 * Class implementing the BRISK keypoint detector and descriptor extractor, described in CITE: LCS11 .
 */
public class BRISK extends Feature2D {

    protected BRISK(long addr) { super(addr); }

    // internal usage only
    public static BRISK __fromPtr__(long addr) { return new BRISK(addr); }

    //
    // C++: static Ptr_BRISK cv::BRISK::create(int thresh = 30, int octaves = 3, float patternScale = 1.0f)
    //

    /**
     * The BRISK constructor
     *
     *     @param thresh AGAST detection threshold score.
     *     @param octaves detection octaves. Use 0 to do single scale.
     *     @param patternScale apply this scale to the pattern used for sampling the neighbourhood of a
     *     keypoint.
     * @return automatically generated
     */
    public static BRISK create(int thresh, int octaves, float patternScale) {
        return BRISK.__fromPtr__(create_0(thresh, octaves, patternScale));
    }

    /**
     * The BRISK constructor
     *
     *     @param thresh AGAST detection threshold score.
     *     @param octaves detection octaves. Use 0 to do single scale.
     *     keypoint.
     * @return automatically generated
     */
    public static BRISK create(int thresh, int octaves) {
        return BRISK.__fromPtr__(create_1(thresh, octaves));
    }

    /**
     * The BRISK constructor
     *
     *     @param thresh AGAST detection threshold score.
     *     keypoint.
     * @return automatically generated
     */
    public static BRISK create(int thresh) {
        return BRISK.__fromPtr__(create_2(thresh));
    }

    /**
     * The BRISK constructor
     *
     *     keypoint.
     * @return automatically generated
     */
    public static BRISK create() {
        return BRISK.__fromPtr__(create_3());
    }


    //
    // C++: static Ptr_BRISK cv::BRISK::create(vector_float radiusList, vector_int numberList, float dMax = 5.85f, float dMin = 8.2f, vector_int indexChange = std::vector<int>())
    //

    /**
     * The BRISK constructor for a custom pattern
     *
     *     @param radiusList defines the radii (in pixels) where the samples around a keypoint are taken (for
     *     keypoint scale 1).
     *     @param numberList defines the number of sampling points on the sampling circle. Must be the same
     *     size as radiusList..
     *     @param dMax threshold for the short pairings used for descriptor formation (in pixels for keypoint
     *     scale 1).
     *     @param dMin threshold for the long pairings used for orientation determination (in pixels for
     *     keypoint scale 1).
     * @param indexChange index remapping of the bits.
     * @return automatically generated
     */
    public static BRISK create(MatOfFloat radiusList, MatOfInt numberList, float dMax, float dMin, MatOfInt indexChange) {
        Mat radiusList_mat = radiusList;
        Mat numberList_mat = numberList;
        Mat indexChange_mat = indexChange;
        return BRISK.__fromPtr__(create_4(radiusList_mat.nativeObj, numberList_mat.nativeObj, dMax, dMin, indexChange_mat.nativeObj));
    }

    /**
     * The BRISK constructor for a custom pattern
     *
     *     @param radiusList defines the radii (in pixels) where the samples around a keypoint are taken (for
     *     keypoint scale 1).
     *     @param numberList defines the number of sampling points on the sampling circle. Must be the same
     *     size as radiusList..
     *     @param dMax threshold for the short pairings used for descriptor formation (in pixels for keypoint
     *     scale 1).
     *     @param dMin threshold for the long pairings used for orientation determination (in pixels for
     *     keypoint scale 1).
     * @return automatically generated
     */
    public static BRISK create(MatOfFloat radiusList, MatOfInt numberList, float dMax, float dMin) {
        Mat radiusList_mat = radiusList;
        Mat numberList_mat = numberList;
        return BRISK.__fromPtr__(create_5(radiusList_mat.nativeObj, numberList_mat.nativeObj, dMax, dMin));
    }

    /**
     * The BRISK constructor for a custom pattern
     *
     *     @param radiusList defines the radii (in pixels) where the samples around a keypoint are taken (for
     *     keypoint scale 1).
     *     @param numberList defines the number of sampling points on the sampling circle. Must be the same
     *     size as radiusList..
     *     @param dMax threshold for the short pairings used for descriptor formation (in pixels for keypoint
     *     scale 1).
     *     keypoint scale 1).
     * @return automatically generated
     */
    public static BRISK create(MatOfFloat radiusList, MatOfInt numberList, float dMax) {
        Mat radiusList_mat = radiusList;
        Mat numberList_mat = numberList;
        return BRISK.__fromPtr__(create_6(radiusList_mat.nativeObj, numberList_mat.nativeObj, dMax));
    }

    /**
     * The BRISK constructor for a custom pattern
     *
     *     @param radiusList defines the radii (in pixels) where the samples around a keypoint are taken (for
     *     keypoint scale 1).
     *     @param numberList defines the number of sampling points on the sampling circle. Must be the same
     *     size as radiusList..
     *     scale 1).
     *     keypoint scale 1).
     * @return automatically generated
     */
    public static BRISK create(MatOfFloat radiusList, MatOfInt numberList) {
        Mat radiusList_mat = radiusList;
        Mat numberList_mat = numberList;
        return BRISK.__fromPtr__(create_7(radiusList_mat.nativeObj, numberList_mat.nativeObj));
    }


    //
    // C++: static Ptr_BRISK cv::BRISK::create(int thresh, int octaves, vector_float radiusList, vector_int numberList, float dMax = 5.85f, float dMin = 8.2f, vector_int indexChange = std::vector<int>())
    //

    /**
     * The BRISK constructor for a custom pattern, detection threshold and octaves
     *
     *     @param thresh AGAST detection threshold score.
     *     @param octaves detection octaves. Use 0 to do single scale.
     *     @param radiusList defines the radii (in pixels) where the samples around a keypoint are taken (for
     *     keypoint scale 1).
     *     @param numberList defines the number of sampling points on the sampling circle. Must be the same
     *     size as radiusList..
     *     @param dMax threshold for the short pairings used for descriptor formation (in pixels for keypoint
     *     scale 1).
     *     @param dMin threshold for the long pairings used for orientation determination (in pixels for
     *     keypoint scale 1).
     * @param indexChange index remapping of the bits.
     * @return automatically generated
     */
    public static BRISK create(int thresh, int octaves, MatOfFloat radiusList, MatOfInt numberList, float dMax, float dMin, MatOfInt indexChange) {
        Mat radiusList_mat = radiusList;
        Mat numberList_mat = numberList;
        Mat indexChange_mat = indexChange;
        return BRISK.__fromPtr__(create_8(thresh, octaves, radiusList_mat.nativeObj, numberList_mat.nativeObj, dMax, dMin, indexChange_mat.nativeObj));
    }

    /**
     * The BRISK constructor for a custom pattern, detection threshold and octaves
     *
     *     @param thresh AGAST detection threshold score.
     *     @param octaves detection octaves. Use 0 to do single scale.
     *     @param radiusList defines the radii (in pixels) where the samples around a keypoint are taken (for
     *     keypoint scale 1).
     *     @param numberList defines the number of sampling points on the sampling circle. Must be the same
     *     size as radiusList..
     *     @param dMax threshold for the short pairings used for descriptor formation (in pixels for keypoint
     *     scale 1).
     *     @param dMin threshold for the long pairings used for orientation determination (in pixels for
     *     keypoint scale 1).
     * @return automatically generated
     */
    public static BRISK create(int thresh, int octaves, MatOfFloat radiusList, MatOfInt numberList, float dMax, float dMin) {
        Mat radiusList_mat = radiusList;
        Mat numberList_mat = numberList;
        return BRISK.__fromPtr__(create_9(thresh, octaves, radiusList_mat.nativeObj, numberList_mat.nativeObj, dMax, dMin));
    }

    /**
     * The BRISK constructor for a custom pattern, detection threshold and octaves
     *
     *     @param thresh AGAST detection threshold score.
     *     @param octaves detection octaves. Use 0 to do single scale.
     *     @param radiusList defines the radii (in pixels) where the samples around a keypoint are taken (for
     *     keypoint scale 1).
     *     @param numberList defines the number of sampling points on the sampling circle. Must be the same
     *     size as radiusList..
     *     @param dMax threshold for the short pairings used for descriptor formation (in pixels for keypoint
     *     scale 1).
     *     keypoint scale 1).
     * @return automatically generated
     */
    public static BRISK create(int thresh, int octaves, MatOfFloat radiusList, MatOfInt numberList, float dMax) {
        Mat radiusList_mat = radiusList;
        Mat numberList_mat = numberList;
        return BRISK.__fromPtr__(create_10(thresh, octaves, radiusList_mat.nativeObj, numberList_mat.nativeObj, dMax));
    }

    /**
     * The BRISK constructor for a custom pattern, detection threshold and octaves
     *
     *     @param thresh AGAST detection threshold score.
     *     @param octaves detection octaves. Use 0 to do single scale.
     *     @param radiusList defines the radii (in pixels) where the samples around a keypoint are taken (for
     *     keypoint scale 1).
     *     @param numberList defines the number of sampling points on the sampling circle. Must be the same
     *     size as radiusList..
     *     scale 1).
     *     keypoint scale 1).
     * @return automatically generated
     */
    public static BRISK create(int thresh, int octaves, MatOfFloat radiusList, MatOfInt numberList) {
        Mat radiusList_mat = radiusList;
        Mat numberList_mat = numberList;
        return BRISK.__fromPtr__(create_11(thresh, octaves, radiusList_mat.nativeObj, numberList_mat.nativeObj));
    }


    //
    // C++:  String cv::BRISK::getDefaultName()
    //

    public String getDefaultName() {
        return getDefaultName_0(nativeObj);
    }


    //
    // C++:  void cv::BRISK::setThreshold(int threshold)
    //

    /**
     * Set detection threshold.
     *     @param threshold AGAST detection threshold score.
     */
    public void setThreshold(int threshold) {
        setThreshold_0(nativeObj, threshold);
    }


    //
    // C++:  int cv::BRISK::getThreshold()
    //

    public int getThreshold() {
        return getThreshold_0(nativeObj);
    }


    //
    // C++:  void cv::BRISK::setOctaves(int octaves)
    //

    /**
     * Set detection octaves.
     *     @param octaves detection octaves. Use 0 to do single scale.
     */
    public void setOctaves(int octaves) {
        setOctaves_0(nativeObj, octaves);
    }


    //
    // C++:  int cv::BRISK::getOctaves()
    //

    public int getOctaves() {
        return getOctaves_0(nativeObj);
    }


    //
    // C++:  void cv::BRISK::setPatternScale(float patternScale)
    //

    /**
     * Set detection patternScale.
     *     @param patternScale apply this scale to the pattern used for sampling the neighbourhood of a
     *     keypoint.
     */
    public void setPatternScale(float patternScale) {
        setPatternScale_0(nativeObj, patternScale);
    }


    //
    // C++:  float cv::BRISK::getPatternScale()
    //

    public float getPatternScale() {
        return getPatternScale_0(nativeObj);
    }


    @Override
    protected void finalize() throws Throwable {
        delete(nativeObj);
    }



    // C++: static Ptr_BRISK cv::BRISK::create(int thresh = 30, int octaves = 3, float patternScale = 1.0f)
    private static native long create_0(int thresh, int octaves, float patternScale);
    private static native long create_1(int thresh, int octaves);
    private static native long create_2(int thresh);
    private static native long create_3();

    // C++: static Ptr_BRISK cv::BRISK::create(vector_float radiusList, vector_int numberList, float dMax = 5.85f, float dMin = 8.2f, vector_int indexChange = std::vector<int>())
    private static native long create_4(long radiusList_mat_nativeObj, long numberList_mat_nativeObj, float dMax, float dMin, long indexChange_mat_nativeObj);
    private static native long create_5(long radiusList_mat_nativeObj, long numberList_mat_nativeObj, float dMax, float dMin);
    private static native long create_6(long radiusList_mat_nativeObj, long numberList_mat_nativeObj, float dMax);
    private static native long create_7(long radiusList_mat_nativeObj, long numberList_mat_nativeObj);

    // C++: static Ptr_BRISK cv::BRISK::create(int thresh, int octaves, vector_float radiusList, vector_int numberList, float dMax = 5.85f, float dMin = 8.2f, vector_int indexChange = std::vector<int>())
    private static native long create_8(int thresh, int octaves, long radiusList_mat_nativeObj, long numberList_mat_nativeObj, float dMax, float dMin, long indexChange_mat_nativeObj);
    private static native long create_9(int thresh, int octaves, long radiusList_mat_nativeObj, long numberList_mat_nativeObj, float dMax, float dMin);
    private static native long create_10(int thresh, int octaves, long radiusList_mat_nativeObj, long numberList_mat_nativeObj, float dMax);
    private static native long create_11(int thresh, int octaves, long radiusList_mat_nativeObj, long numberList_mat_nativeObj);

    // C++:  String cv::BRISK::getDefaultName()
    private static native String getDefaultName_0(long nativeObj);

    // C++:  void cv::BRISK::setThreshold(int threshold)
    private static native void setThreshold_0(long nativeObj, int threshold);

    // C++:  int cv::BRISK::getThreshold()
    private static native int getThreshold_0(long nativeObj);

    // C++:  void cv::BRISK::setOctaves(int octaves)
    private static native void setOctaves_0(long nativeObj, int octaves);

    // C++:  int cv::BRISK::getOctaves()
    private static native int getOctaves_0(long nativeObj);

    // C++:  void cv::BRISK::setPatternScale(float patternScale)
    private static native void setPatternScale_0(long nativeObj, float patternScale);

    // C++:  float cv::BRISK::getPatternScale()
    private static native float getPatternScale_0(long nativeObj);

    // native support for java finalize()
    private static native void delete(long nativeObj);

}
