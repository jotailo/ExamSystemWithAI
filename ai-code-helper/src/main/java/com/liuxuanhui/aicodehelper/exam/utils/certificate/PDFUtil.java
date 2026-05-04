package com.liuxuanhui.aicodehelper.exam.utils.certificate;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class PDFUtil {

    private Document document;
    private PdfWriter writer;

    public PDFUtil openDocument(String pdfPath) throws FileNotFoundException, DocumentException {
        Document document = new Document();
        writer = PdfWriter.getInstance(document, new FileOutputStream(pdfPath));
        document.open();
        this.document = document;
        return this;
    }

    public PDFUtil addImage(String imagePath, float absoluteX, float absoluteY) throws IOException, DocumentException {
        Image tImgCover = Image.getInstance(imagePath);
        tImgCover.setAbsolutePosition(absoluteX, absoluteY);
        float height = tImgCover.getHeight();
        float width = tImgCover.getWidth();
        int percent = getPercent2(height, width);
        tImgCover.scalePercent(percent);
        document.add(tImgCover);
        return this;
    }

    public PDFUtil addLogo(String imagePath, float absoluteX, float absoluteY) throws IOException, DocumentException {
        Image tImgCover = Image.getInstance(imagePath);
        tImgCover.setAbsolutePosition(absoluteX, absoluteY);
        tImgCover.scalePercent(20);
        document.add(tImgCover);
        return this;
    }

    public PDFUtil addContent(String certificateContent, float x, float y, ContentStyle contentStyle) throws DocumentException, IOException {
        if (contentStyle == null) {
            contentStyle = new ContentStyle();
        }
        PdfContentByte canvas = writer.getDirectContent();
        BaseFont bf = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
        Font secFont = new Font(bf, contentStyle.getFontSize(), contentStyle.getStyle(), contentStyle.getBaseColor());
        Phrase phrase = new Phrase(certificateContent, secFont);
        ColumnText.showTextAligned(canvas, contentStyle.getAlignment(), phrase, x, y, 0);
        return this;
    }

    public void close() {
        document.close();
    }

    public int getPercent2(float h, float w) {
        float p2 = 595 / w * 100;
        return Math.round(p2);
    }
}
