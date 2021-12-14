# pdf2Img
this repo use mupdf to turn pdf to imgs, free and best performance we ever see, fast and no elements loss on mouse-selectable pdf.
# usage
1. first, call Transform.init once and only once is enough.
2. then, you can call Transform.turnPdf2Imgs, which turn pdf into List<BufferedImage>, where BufferedImage is java.awt.image.BufferedImage, and it can be manipulated easily.
# details
1. this jar use the lib of mupdf, which is written by c, so this jar is very fast.
2. as far as I know, most opensource tool such as pdfbox of apache, can not performs well on mouse-selectable pdf, which contain chars you can copy. some tools may can, but they are not free and opensource.
