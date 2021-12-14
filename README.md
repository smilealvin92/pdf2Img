# pdf2Img
this repo use mupdf to turn pdf to imgs, free and best performance we ever see, fast and no elements loss on mouse-selectable pdf.
# usage
1. first, download "mupdf-1.19.0.source" from official site. then unpack it. change directory to its root, mkdir build/java, then cd to mupdf-1.19.0.source/platform/java/, make sure you can call javac command of java 11, and there is jni.h in your $JAVA_HOME/include. then just call make command under terminal of mupdf-1.19.0.source/platform/java/, till it is finished, find mupdf-1.19.0-source/build/java/release/libmupdf_java64.so lib. call Transform.init with the path to the directory which contains "libmupdf_java64.so". call it once and only once is enough.
2. then, you can call Transform.turnPdf2Imgs, which turn pdf into List<BufferedImage>, where BufferedImage is java.awt.image.BufferedImage, and it can be manipulated easily.
# details
1. this jar use the lib of mupdf, which is written by c, so this jar is very fast.
2. as far as I know, most opensource tool such as pdfbox of apache, can not performs well on mouse-selectable pdf, which contain chars you can copy. some tools may can, but they are not free and opensource.
# thanks
- thanks to the great [mupdf library](https://www.mupdf.com/)
