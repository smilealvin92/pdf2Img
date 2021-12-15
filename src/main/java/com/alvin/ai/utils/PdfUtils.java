package com.alvin.ai.utils;

import com.artifex.mupdf.fitz.Pixmap;

import java.awt.image.BufferedImage;
import java.io.File;

/**
 * @ClassName PdfUtils
 * @Description TODO
 * @Date 2021/12/13 上午10:49
 * @Version 1.0
 */
public class PdfUtils {
    public static String getAcceleratorPath(String documentPath) {
        String acceleratorName = documentPath.substring(1);
        acceleratorName = acceleratorName.replace(File.separatorChar, '%');
        acceleratorName = acceleratorName.replace('\\', '%');
        acceleratorName = acceleratorName.replace(':', '%');
        String tmpdir = System.getProperty("java.io.tmpdir");
        return new StringBuffer(tmpdir).append(File.separatorChar).append(acceleratorName).append(".accel").toString();
    }

    public static boolean acceleratorValid(String documentPath, String acceleratorPath) {
        long documentModified = new File(documentPath).lastModified();
        long acceleratorModified = new File(acceleratorPath).lastModified();
        return acceleratorModified != 0 && acceleratorModified > documentModified;
    }

    public static BufferedImage imageFromPixmap(Pixmap pixmap) {
        int w = pixmap.getWidth();
        int h = pixmap.getHeight();
        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_3BYTE_BGR);
        image.setRGB(0, 0, w, h, pixmap.getPixels(), 0, w);
        return image;
    }
}
