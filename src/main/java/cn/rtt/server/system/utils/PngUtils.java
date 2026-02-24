package cn.rtt.server.system.utils;

import cn.rtt.server.system.domain.dto.ImageParam;
import net.coobird.thumbnailator.Thumbnails;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * CopyRight : <company domain>
 * Project :  zcbf
 * Comments : <对此类的描述，可以引用系统设计中的描述>
 * JDK version : JDK1.8
 * Create Date : 2024-07-26 09:16
 *
 * @author : xql
 */

public class PngUtils {

    public static void compress(String url, ImageParam imageParam) throws IOException {
        Thumbnails.Builder<File> builder = Thumbnails.of(url);
        if (imageParam.getWidth() != null && imageParam.getHeight() != null) {
            builder.size(imageParam.getWidth(), imageParam.getHeight());
        }
        builder.outputQuality(0.75).imageType(BufferedImage.TYPE_INT_ARGB).toFile(url);
    }

    public static void main(String[] args) throws IOException {
        Thumbnails.Builder<File> builder = Thumbnails.of("D:\\微信图片_20240815145904.png");
        builder.size(100, 100);
        builder.outputQuality(0.2).imageType(BufferedImage.TYPE_INT_ARGB).toFile("D:\\test.png");
    }

}
