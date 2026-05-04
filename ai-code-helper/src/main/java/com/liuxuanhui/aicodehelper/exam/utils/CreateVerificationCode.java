package com.liuxuanhui.aicodehelper.exam.utils;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.Random;

@Data
@Component
public class CreateVerificationCode {

    private String code;
    private Graphics g;

    public Color getRandColor(int s, int e) {
        Random random = new Random();
        if (s > 255) s = 91;
        if (e > 255) e = 97;
        int r = s + random.nextInt(e - s);
        int g = s + random.nextInt(e - s);
        int b = s + random.nextInt(e - s);
        return new Color(r, g, b);
    }

    public BufferedImage getIdentifyImg() {
        int width = 100;
        int height = 28;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        Graphics2D g2d = (Graphics2D) g;
        Random random = new Random();
        Font font = new Font("华文宋体", Font.BOLD, 19);
        g.setColor(this.getRandColor(200, 250));
        g.fillRect(0, 0, width, height);
        g.setFont(font);
        g.setColor(this.getRandColor(180, 200));

        for (int i = 0; i < 100; i++) {
            int x = random.nextInt(width - 1);
            int y = random.nextInt(height - 1);
            int x1 = random.nextInt(6) + 1;
            int y1 = random.nextInt(12) + 1;
            BasicStroke bs = new BasicStroke(2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL);
            Line2D line = new Line2D.Double(x, y, x1 + x, y1 + y);
            g2d.setStroke(bs);
            g2d.draw(line);
        }

        StringBuilder sRand = new StringBuilder();
        String ctmp;
        int itmp;
        for (int i = 0; i < 4; i++) {
            switch (random.nextInt(3)) {
                case 1:
                case 2:
                    itmp = random.nextInt(26) + 65;
                    ctmp = String.valueOf((char) itmp);
                    break;
                default:
                    itmp = random.nextInt(10) + 48;
                    ctmp = String.valueOf((char) itmp);
                    break;
            }
            sRand.append(ctmp);
            Color color = new Color(20 + random.nextInt(110), 20 + random.nextInt(110), random.nextInt(110));
            g.setColor(color);
            g.drawString(ctmp, 19 * i + 19, 19);
        }
        this.setCode(sRand.toString());
        this.setG(g);
        return image;
    }
}
