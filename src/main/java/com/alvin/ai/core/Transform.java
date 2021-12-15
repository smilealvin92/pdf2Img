package com.alvin.ai.core;

import com.artifex.mupdf.fitz.*;
import com.alvin.ai.example.Viewer;
import com.alvin.ai.utils.PdfUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @ClassName Transform
 * @Description TODO
 * @Author Yang Jianren
 * @Date 2021/12/13 上午10:45
 * @Version 1.0
 */
public class Transform {
    protected static int zoomLevel = 10;
    protected static int rotate = 0;
    protected static boolean currentICC = true;
    protected static int currentAA = 8;
    protected static boolean currentInvert = false;
    protected static boolean currentTint = false;
    protected static int tintBlack = 0x303030;
    protected static int tintWhite = 0xFFFFF0;
    protected static final int[] zoomList = {
            18, 24, 36, 54, 72, 96, 120, 144, 180, 216, 288
    };
    protected static Matrix pageCTM = new Matrix().scale(zoomList[zoomLevel] / 72.0f * 1.0f);

    public Transform() {
    }

    public static BufferedImage turnPage2Img(Page page) {
        Matrix trm = new Matrix(pageCTM).rotate(rotate);
        Rect bbox = page.getBounds().transform(trm);
        Pixmap pixmap = new Pixmap(ColorSpace.DeviceBGR, bbox, true);
        pixmap.clear(255);
        if (currentICC)
            Context.enableICC();
        else
            Context.disableICC();
        Context.setAntiAliasLevel(currentAA);
        DrawDevice dev = new DrawDevice(pixmap);
        page.run(dev, trm, null);
        dev.close();
        dev.destroy();
        if (currentInvert) {
            pixmap.invertLuminance();
            pixmap.gamma(1 / 1.4f);
        }
        if (currentTint) {
            pixmap.tint(tintBlack, tintWhite);
        }
        BufferedImage image = PdfUtils.imageFromPixmap(pixmap);
        pixmap.destroy();
        return image;
    }

    public static void init(String muPdfSoAbsPath){
        try {
            addLibraryPath(muPdfSoAbsPath);
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("初始化so文件路径失败，程序退出");
            System.exit(1);
        }
    }

    public static List<BufferedImage> turnPdf2Imgs(String pdfAbsPath){
        List<BufferedImage> res = new ArrayList<>();
        final String acceleratorPath = PdfUtils.getAcceleratorPath(pdfAbsPath);
        Document document;
        try {
            if (PdfUtils.acceleratorValid(pdfAbsPath, acceleratorPath)) {
                document = Document.openDocument(pdfAbsPath, acceleratorPath);
            } else {
                document = Document.openDocument(pdfAbsPath);
            }
            document.saveAccelerator(acceleratorPath);
            int pageCount = document.countPages();
            for (int i = 0; i < pageCount; i++){
                Page page = document.loadPage(i);
                res.add(Transform.turnPage2Img(page));
            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println(pdfAbsPath);
            res = new ArrayList<>();
        }
        return res;
    }

    /**
     * Adds the specified path to the java library path
     *
     * @param pathToAdd the path to add
     * @throws Exception
     */
    public static void addLibraryPath(String pathToAdd) throws Exception{
        final Field usrPathsField = ClassLoader.class.getDeclaredField("usr_paths");
        usrPathsField.setAccessible(true);

        //get array of paths
        final String[] paths = (String[])usrPathsField.get(null);

        //check if the path to add is already present
        for(String path : paths) {
            if(path.equals(pathToAdd)) {
                return;
            }
        }

        //add the new path
        final String[] newPaths = Arrays.copyOf(paths, paths.length + 1);
        newPaths[newPaths.length-1] = pathToAdd;
        usrPathsField.set(null, newPaths);
    }

    public static void traverse(File file, List<String> allPdfPath) {
        File[] files = file.listFiles();
        if (files == null) {
            return;
        }
        for (File file1 : files) {
            if (file1.isDirectory()) {
                traverse(file1, allPdfPath);
            } else if (file1.isFile()) {
                allPdfPath.add(file1.getAbsolutePath());
            }
        }
    }


    public static void main(String[] args) throws Exception {
        addLibraryPath("/mupdf-1.19.0-source/build/java/release");
        List<String> allPdfs = new ArrayList<>();
        String pdfDir = "pdfs";
        traverse(new File(pdfDir), allPdfs);
        for (String pdfPath : allPdfs) {
            final String acceleratorPath = PdfUtils.getAcceleratorPath(pdfPath);
            Document document;
            try {
                if (PdfUtils.acceleratorValid(pdfPath, acceleratorPath)) {
                    document = Document.openDocument(pdfPath, acceleratorPath);
                } else {
                    document = Document.openDocument(pdfPath);
                }
                document.saveAccelerator(acceleratorPath);
                int pageCount = document.countPages();
                for (int i = 0; i < pageCount; i++){
                    File output = new File("pdf2Img/img"+ File.separator+i+".jpg");
//            pdfDocument.saveAsPng(i, "/pdf转image"+ File.separator+i+".png",
//                    1.0f, ImageType.IMAGE_TYPE_RGB);
                    Page page = document.loadPage(i);
                    final BufferedImage bufferedImage = Transform.turnPage2Img(page);
                    try {
                        ImageIO.write(bufferedImage, "jpg", output);
                    } catch (Exception e) {
                        System.out.println("路径出错");
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
                System.out.println(pdfPath);
            }

        }
    }
}
