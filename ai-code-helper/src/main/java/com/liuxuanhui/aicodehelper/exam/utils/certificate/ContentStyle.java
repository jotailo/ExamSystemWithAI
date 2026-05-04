package com.liuxuanhui.aicodehelper.exam.utils.certificate;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContentStyle {
    private String TTFPath = "C:/WINDOWS/Fonts/SIMYOU.TTF";
    private float fontSize = 12;
    private BaseColor baseColor = new BaseColor(0, 0, 0);
    private int style = Font.NORMAL;
    private int alignment = Element.ALIGN_LEFT;
}
