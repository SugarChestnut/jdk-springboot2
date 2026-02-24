package cn.rtt.server.system.security.verify;

import net.coobird.thumbnailator.Thumbnails;
import net.coobird.thumbnailator.geometry.Positions;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Random;

/**
 * @author rtt
 * @date 2025/12/25 09:16
 */
public class SlideVerifyService {

    private static final String IMG_PATH = "classpath*:img/slide/*.*";

    private static final String BASE64_PREFIX = "data:image/png;base64,";

    private static final int IMG_WIDTH = 480;

    private static final int IMG_HEIGHT = 240;

    private static final int RATIO = 2;

    private static final int CUT_WIDTH = 54;

    private static final int RADIUS = 9;

    private static final int RADIUS_OFFSET = 3;

    private static final int FLAG = 0xFFFFFF;

    private static final int SHADOW_WIDTH = 2;

    // 127
    private final Color SHADOW_COLOR = new Color(105, 105, 105, 130);

    private final Color HIGHLIGHT_COLOR = new Color(248, 248, 255, 255);

    public static void main(String[] args) throws IOException {
        SlideVerifyService slideVerifyService = new SlideVerifyService();
        SlideEntity imageResult = slideVerifyService.imageResult();
        System.out.println(imageResult);
    }

    public SlideEntity imageResult() throws IOException {
        return imageResult(getRandomImage());
    }

    private BufferedImage getRandomImage() throws IOException {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources(IMG_PATH);
        if (resources.length == 0) {
            throw new IOException("未找到文件");
        }
        int index = new Random().nextInt(resources.length);
        InputStream input = resources[index].getInputStream();
        return ImageIO.read(input);
    }

    public SlideEntity imageResult(BufferedImage oriImage) throws IOException {
        // 验证图片大小
        checkImage(oriImage);
        // 图片按比例裁剪
        BufferedImage cutImage = cutImage(oriImage);
        // 获取切图区域
        int[][] blockData = getBlockData(cutImage);
        // 切图
        return cutByTemplate(cutImage, blockData);
    }

    private String imageBase64(BufferedImage image) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(image, "png", out);
        byte[] bytes = out.toByteArray();
        return Base64.getEncoder().encodeToString(bytes);
    }

    private SlideEntity cutByTemplate(BufferedImage image, int[][] blockData) throws IOException {

        SlideEntity entity = new SlideEntity();

        int w = (CUT_WIDTH + 2 * RADIUS) * image.getWidth() / IMG_WIDTH;
        int h = image.getHeight();

        // 创建一张矩形图
        BufferedImage cutImage = new BufferedImage(w, h, image.getType());
        Graphics2D graphics = cutImage.createGraphics();
        // 透明化整张图
        cutImage = graphics.getDeviceConfiguration().createCompatibleImage(w, h, Transparency.BITMASK);
        graphics.dispose();
        graphics = cutImage.createGraphics();
        graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (int x = 0; x < blockData.length; x++) {
            for (int y = 0; y < blockData[0].length; y++) {
                if (blockData[x][y] == FLAG) {
                    if (entity.getOffset() == 0) entity.setOffset(x);
                    // 原图
                    int rgb = image.getRGB(x, y);
                    image.setRGB(x, y, FLAG);
                    // 边框
                    int shadowWidth = SHADOW_WIDTH * image.getWidth() / IMG_WIDTH;
                    int highlight = SHADOW_WIDTH * image.getWidth() / IMG_WIDTH / 2;
                    if (blockData[x - highlight][y] != FLAG || blockData[x + highlight][y] != FLAG ||
                            blockData[x][y - highlight] != FLAG || blockData[x][y + highlight] != FLAG) {
                        graphics.setColor(HIGHLIGHT_COLOR);
                    } else if (blockData[x - shadowWidth][y] != FLAG || blockData[x + shadowWidth][y] != FLAG ||
                            blockData[x][y - shadowWidth] != FLAG || blockData[x][y + shadowWidth] != FLAG) {
                        graphics.setColor(SHADOW_COLOR);
                    } else {
                        graphics.setColor(new Color(rgb));
                    }
                    // 设置笔触大小
                    graphics.setStroke(new BasicStroke(1f));
                    // 1个像素填充
                    graphics.fillRect(x - entity.getOffset() + 2, y, 1, 1);
                }
            }
        }
        graphics.dispose();
        entity.setBgImg(BASE64_PREFIX + imageBase64(compressImage(image, IMG_WIDTH, IMG_HEIGHT)));
        entity.setPuzzleImg(BASE64_PREFIX + imageBase64(compressImage(cutImage, w * IMG_HEIGHT / h, IMG_HEIGHT)));
        entity.setBgWidth(IMG_WIDTH);
        entity.setBgHeight(IMG_HEIGHT);
        entity.setPuzzleWidth(w * IMG_HEIGHT / h);
        entity.setPuzzleHeight(IMG_HEIGHT);
        entity.setOffset(entity.getOffset() * IMG_HEIGHT / h);
        return entity;
    }

    /**
     * 获取抠图的起始坐标
     */
    private Point createXYPos(BufferedImage oriImage) {

        // 图片的大小
        int height = oriImage.getHeight();
        int width = oriImage.getWidth();

        int xPos = new Random().nextInt(width - width * (CUT_WIDTH * 3 / 2 + 2 * RADIUS) / IMG_WIDTH);
        if (xPos < width / 4) xPos += width / 4;

        int yPos = new Random().nextInt(height - height * (CUT_WIDTH * 3 / 2 + 2 * RADIUS) / IMG_HEIGHT);
        if (yPos < height * (CUT_WIDTH + RADIUS) / IMG_HEIGHT / 2)
            yPos += height * (CUT_WIDTH + RADIUS) / IMG_HEIGHT / 2;

        return new Point(xPos, yPos);
    }

    private int[][] getBlockData(BufferedImage image) {
        int height = image.getHeight();
        int width = image.getWidth();

        int[][] blockData = new int[width][height];

        Point p = createXYPos(image);

        // 矩形区域
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (x > p.x && x < (p.x + width * CUT_WIDTH / IMG_WIDTH)
                        && y > p.y && y < (p.y + height * CUT_WIDTH / IMG_HEIGHT)) {
                    blockData[x][y] = FLAG;
                }
            }
        }

        int direct = new Random().nextInt(4);
        int p1x, p1y, p2x, p2y;
        if (direct == 0) {
            // 上凸左凹
            p1x = p.x + CUT_WIDTH / 2 * width / IMG_WIDTH;
            p1y = p.y - RADIUS_OFFSET * width / IMG_WIDTH;
            p2x = p.x + RADIUS_OFFSET * width / IMG_WIDTH;
            p2y = p.y + CUT_WIDTH / 2 * width / IMG_WIDTH;
        } else if (direct == 1) {
            // 右凸上凹
            p1x = p.x + (CUT_WIDTH + RADIUS_OFFSET) * width / IMG_WIDTH;
            p1y = p.y + CUT_WIDTH / 2 * width / IMG_WIDTH;
            p2x = p.x + CUT_WIDTH / 2 * width / IMG_WIDTH;
            p2y = p.y + RADIUS_OFFSET * width / IMG_WIDTH;
        } else if (direct == 2) {
            // 下凸右凹
            p1x = p.x + CUT_WIDTH / 2 * width / IMG_WIDTH;
            p1y = p.y + (CUT_WIDTH + RADIUS_OFFSET) * width / IMG_WIDTH;
            p2x = p.x + (CUT_WIDTH - RADIUS_OFFSET) * width / IMG_WIDTH;
            p2y = p.y + CUT_WIDTH / 2 * width / IMG_WIDTH;
        } else {
            // 左凸下凹
            p1x = p.x - RADIUS_OFFSET * width / IMG_WIDTH;
            p1y = p.y + CUT_WIDTH / 2 * width / IMG_WIDTH;
            p2x = p.x + CUT_WIDTH / 2 * width / IMG_WIDTH;
            p2y = p.y + (CUT_WIDTH - RADIUS_OFFSET) * width / IMG_WIDTH;
        }

        int radiusPow2 = RADIUS * RADIUS * width * width / IMG_WIDTH / IMG_WIDTH;

        // 突出区域
        for (int x = p1x - RADIUS * width / IMG_WIDTH; x < p1x + RADIUS * width / IMG_WIDTH; x++) {
            for (int y = p1y - RADIUS * width / IMG_WIDTH; y < p1y + RADIUS * width / IMG_WIDTH; y++) {
                if (Math.pow(x - p1x, 2) + Math.pow(y - p1y, 2) < radiusPow2) {
                    blockData[x][y] = FLAG;
                }
            }
        }
        // 凹陷区域
        for (int x = p2x - RADIUS * width / IMG_WIDTH; x < p2x + RADIUS * width / IMG_WIDTH; x++) {
            for (int y = p2y - RADIUS * width / IMG_WIDTH; y < p2y + RADIUS * width / IMG_WIDTH; y++) {
                if (Math.pow(x - p2x, 2) + Math.pow(y - p2y, 2) < radiusPow2) {
                    blockData[x][y] = 0;
                }
            }
        }

        return blockData;
    }

    private void checkImage(BufferedImage image) {
        if (image.getWidth() < IMG_WIDTH || image.getHeight() < IMG_HEIGHT) {
            throw new IllegalArgumentException("图片太小，不符合要求");
        }
    }

    private BufferedImage cutImage(BufferedImage image) throws IOException {
        int w = image.getWidth();
        int h = image.getHeight();
        if (w / h > RATIO) {
            return Thumbnails.of(image).sourceRegion(Positions.CENTER, h * 2, h).size(h * 2, h).asBufferedImage();
        } else if (w / h < 2) {
            if (w % 2 == 1) w -= 1;
            return Thumbnails.of(image).sourceRegion(Positions.CENTER, w, w / 2).size(w, w / 2).asBufferedImage();
        }
        return image;
    }

    private BufferedImage compressImage(BufferedImage image, int width, int height) throws IOException {
        return Thumbnails.of(image).forceSize(width, height).outputFormat("png").asBufferedImage();
    }
}
