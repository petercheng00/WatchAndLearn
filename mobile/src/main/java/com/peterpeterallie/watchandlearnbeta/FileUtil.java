package com.peterpeterallie.watchandlearnbeta;

import com.peterpeterallie.watchandlearnbeta.model.Guide;

public class FileUtil {

    public static final String GUIDE_FILENAME_PREFIX = "guide_";

    public static String getGuideFilename(Guide guide) {
        return GUIDE_FILENAME_PREFIX  + guide.getId();
    }
}
