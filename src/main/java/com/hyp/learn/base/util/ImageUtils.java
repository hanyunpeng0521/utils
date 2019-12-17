package com.hyp.learn.base.util;

import net.coobird.thumbnailator.Thumbnails;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.*;
import java.io.*;

/**
 *   
 *
 * @author hyp 
 * @version V1.0  
 * @Title: ImageUtils.java
 * @Package com.jarvis.base.util
 * @Description:图片处理工具类。
 * @date 2017年9月2日 下午3:04:40
 */
@SuppressWarnings("restriction")
public class ImageUtils {
    /**
     * 相对于图片的位置
     */
    private static final int POSITION_UPPERLEFT = 0;
    private static final int POSITION_UPPERRIGHT = 10;
    private static final int POSITION_LOWERLEFT = 1;
    private static final int POSITION_LOWERRIGHT = 11;
    /**
     * 几种常见的图片格式
     */
    public static String IMAGE_TYPE_GIF = "gif";// 图形交换格式
    public static String IMAGE_TYPE_JPG = "jpg";// 联合照片专家组
    public static String IMAGE_TYPE_JPEG = "jpeg";// 联合照片专家组
    public static String IMAGE_TYPE_BMP = "bmp";// 英文Bitmap（位图）的简写，它是Windows操作系统中的标准图像文件格式
    public static String IMAGE_TYPE_PNG = "png";// 可移植网络图形


    /**
     * @描述：Base64 解码并生成图片
     * @入参：@param imgStr
     * @入参：@param imgFile
     * @入参：@throws IOException
     * @出参：void
     */
    public static void generateImage(String imgStr, String imgFile) throws IOException {
        BASE64Decoder decoder = new BASE64Decoder();
        // Base64解码
        byte[] bytes;
        OutputStream out = null;
        try {
            bytes = decoder.decodeBuffer(imgStr);
            for (int i = 0; i < bytes.length; ++i) {
                if (bytes[i] < 0) {// 调整异常数据
                    bytes[i] += 256;
                }
            }
            // 生成图片
            out = new FileOutputStream(imgFile);
            out.write(bytes);
            out.flush();
        } catch (IOException e) {
            throw new IOException();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * @throws IOException
     * @描述：根据路径得到base编码后图片
     * @入参：@param imgFilePath
     * @入参：@return
     * @出参：String
     */
    public static String getImageStr(String imgFilePath) throws IOException {// 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        byte[] data = null;

        // 读取图片字节数组
        try {
            InputStream in = new FileInputStream(imgFilePath);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            throw new IOException();
        }

        // 对字节数组Base64编码
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(data);// 返回Base64编码过的字节数组字符串
    }

    /**
     * @throws IOException
     * @描述：图片旋转
     * @入参：@param base64In 传入的图片base64
     * @入参：@param angle 图片旋转度数
     * @入参：@throws Exception
     * @出参：String 传出的图片base64
     */
    public static String imgAngleRevolve(String base64In, int angle) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            Thumbnails.of(base64ToIo(base64In)).scale(1.0).rotate(angle).toOutputStream(os);
        } catch (IOException e) {
            throw new IOException();
        }
        byte[] bs = os.toByteArray();
        String s = new BASE64Encoder().encode(bs);
        return s;
    }

    /**
     * @描述：base64转为io流
     * @入参：@param strBase64
     * @入参：@return
     * @入参：@throws IOException
     * @出参：InputStream
     */
    public static InputStream base64ToIo(String strBase64) throws IOException {
        // 解码，然后将字节转换为文件
        byte[] bytes = new BASE64Decoder().decodeBuffer(strBase64); // 将字符串转换为byte数组
        return new ByteArrayInputStream(bytes);
    }

    public BufferedImage image2BufferedImage(Image image) {
        System.out.println(image.getWidth(null));
        System.out.println(image.getHeight(null));
        BufferedImage bufferedImage = new BufferedImage(image.getWidth(null),
                image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bufferedImage.createGraphics();
        g.drawImage(image, null, null);
        g.dispose();
        System.out.println(bufferedImage.getWidth());
        System.out.println(bufferedImage.getHeight());
        return bufferedImage;
    }


    /**
     * 缩放并转换格式后保存
     *
     * @param srcPath 源路径
     * @param destPath 目标路径
     * @param width        ：目标宽
     * @param height       ：目标高
     * @param format       ：文件格式
     * @return
     */
    public static boolean scaleToFile(String srcPath, String destPath,
                                      int width, int height, String format) {
        boolean flag = false;
        try {
            File file = new File(srcPath);
            File destFile = new File(destPath);
            if (!destFile.getParentFile().exists()) {
                destFile.getParentFile().mkdir();
            }
            BufferedImage src = ImageIO.read(file); // 读入文件
            Image image = src.getScaledInstance(width, height,
                    Image.SCALE_DEFAULT);
            BufferedImage tag = new BufferedImage(width, height,
                    BufferedImage.TYPE_INT_RGB);
            Graphics g = tag.getGraphics();
            g.drawImage(image, 0, 0, null); // 绘制缩小后的图
            g.dispose();
            flag = ImageIO.write(tag, format, new FileOutputStream(destFile));// 输出到文件流
        } catch (IOException e) {
            e.printStackTrace();
        }
        return flag;
    }


    /**
     * 缩放Image，此方法返回源图像按百分比缩放后的图像
     *
     * @param inputImage
     * @param percentage 百分比 允许的输入0<percentage<10000
     * @return
     */
    public static BufferedImage scaleByPercentage(BufferedImage inputImage,
                                                  int percentage) {
// 允许百分比
        if (0 > percentage || percentage > 10000) {
            throw new ImagingOpException("Error::不合法的参数:percentage->"
                    + percentage + ",percentage应该大于0~小于10000");
        }
// 获取原始图像透明度类型
        int type = inputImage.getColorModel().getTransparency();
// 获取目标图像大小
        int w = inputImage.getWidth() * percentage;
        int h = inputImage.getHeight() * percentage;
// 开启抗锯齿
        RenderingHints renderingHints = new RenderingHints(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_ANTIALIAS_ON);
// 使用高质量压缩
        renderingHints.put(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_RENDER_QUALITY);
        BufferedImage img = new BufferedImage(w, h, type);
        Graphics2D graphics2d = img.createGraphics();
        graphics2d.setRenderingHints(renderingHints);
        graphics2d.drawImage(inputImage, 0, 0, w, h, 0, 0,
                inputImage.getWidth(), inputImage.getHeight(), null);
        graphics2d.dispose();
        return img;
/*
* 此代码将返回Image类型 return
* inputImage.getScaledInstance(inputImage.getWidth()*percentage,
* inputImage.getHeight()*percentage, Image.SCALE_SMOOTH);
*/
    }


    /**
     * 缩放Image，此方法返回源图像按给定最大宽度限制下按比例缩放后的图像
     *
     * @param inputImage
     * @param maxWidth   ：压缩后允许的最大宽度
     * @param maxHeight  ：压缩后允许的最大高度
     * @throws java.io.IOException return
     */
    public static BufferedImage scaleByPixelRate(BufferedImage inputImage,
                                                 int maxWidth, int maxHeight) throws Exception {
// 获取原始图像透明度类型
        int type = inputImage.getColorModel().getTransparency();
        int width = inputImage.getWidth();
        int height = inputImage.getHeight();
        int newWidth = maxWidth;
        int newHeight = maxHeight;
// 如果指定最大宽度超过比例
        if (width * maxHeight < height * maxWidth) {
            newWidth = (int) (newHeight * width / height);
        }
// 如果指定最大高度超过比例
        if (width * maxHeight > height * maxWidth) {
            newHeight = (int) (newWidth * height / width);
        }
// 开启抗锯齿
        RenderingHints renderingHints = new RenderingHints(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_ANTIALIAS_ON);
// 使用高质量压缩
        renderingHints.put(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_RENDER_QUALITY);
        BufferedImage img = new BufferedImage(newWidth, newHeight, type);
        Graphics2D graphics2d = img.createGraphics();
        graphics2d.setRenderingHints(renderingHints);
        graphics2d.drawImage(inputImage, 0, 0, newWidth, newHeight, 0, 0,
                width, height, null);
        graphics2d.dispose();
        return img;
    }


    /**
     * 缩放Image，此方法返回源图像按给定宽度、高度限制下缩放后的图像
     *
     * @param inputImage
     * @param newWidth   ：压缩后宽度
     * @param newHeight  ：压缩后高度
     * @throws java.io.IOException return
     */
    public static BufferedImage scaleByPixel(BufferedImage inputImage,
                                             int newWidth, int newHeight) throws Exception {
// 获取原始图像透明度类型
        int type = inputImage.getColorModel().getTransparency();
        int width = inputImage.getWidth();
        int height = inputImage.getHeight();
// 开启抗锯齿
        RenderingHints renderingHints = new RenderingHints(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
// 使用高质量压缩
        renderingHints.put(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        BufferedImage img = new BufferedImage(newWidth, newHeight, type);
        Graphics2D graphics2d = img.createGraphics();
        graphics2d.setRenderingHints(renderingHints);
        graphics2d.drawImage(inputImage, 0, 0, newWidth, newHeight, 0, 0,
                width, height, null);
        graphics2d.dispose();
        return img;
    }


    /**
     * 切割图像，返回指定范围的图像
     *
     * @param inputImage
     * @param x          起点横坐标
     * @param y          起点纵坐标
     * @param width      切割图片宽度:如果宽度超出图片，将改为图片自x剩余宽度
     * @param height     切割图片高度：如果高度超出图片，将改为图片自y剩余高度
     * @param fill       指定目标图像大小超出时是否补白，如果true，则表示补白；false表示不补白，此时将重置目标图像大小
     * @return
     */
    public static BufferedImage cut(BufferedImage inputImage, int x, int y,
                                    int width, int height, boolean fill) {
// 获取原始图像透明度类型
        int type = inputImage.getColorModel().getTransparency();
        int w = inputImage.getWidth();
        int h = inputImage.getHeight();
        int endx = x + width;
        int endy = y + height;
        if (x > w)
            throw new ImagingOpException("起点横坐标超出源图像范围");
        if (y > h)
            throw new ImagingOpException("起点纵坐标超出源图像范围");
        BufferedImage img;
// 补白
        if (fill) {
            img = new BufferedImage(width, height, type);
// 宽度超出限制
            if ((w - x) < width) {
                width = w - x;
                endx = w;
            }
// 高度超出限制
            if ((h - y) < height) {
                height = h - y;
                endy = h;
            }
// 不补
        } else {
// 宽度超出限制
            if ((w - x) < width) {
                width = w - x;
                endx = w;
            }
// 高度超出限制
            if ((h - y) < height) {
                height = h - y;
                endy = h;
            }
            img = new BufferedImage(width, height, type);
        }
// 开启抗锯齿
        RenderingHints renderingHints = new RenderingHints(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_ANTIALIAS_ON);
// 使用高质量压缩
        renderingHints.put(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_RENDER_QUALITY);
        Graphics2D graphics2d = img.createGraphics();
        graphics2d.setRenderingHints(renderingHints);
        graphics2d.drawImage(inputImage, 0, 0, width, height, x, y, endx, endy,
                null);
        graphics2d.dispose();
        return img;
    }


    /**
     * 切割图像，返回指定起点位置指定大小图像
     *
     * @param inputImage
     * @param startPoint 起点位置：左上：0、右上:10、左下:1、右下:11
     * @param width      切割图片宽度
     * @param height     切割图片高度
     * @param fill       指定目标图像大小超出时是否补白，如果true，则表示补白；false表示不补白，此时将重置目标图像大小
     * @return
     */
    public static BufferedImage cut(BufferedImage inputImage, int startPoint,
                                    int width, int height, boolean fill) {
// 获取原始图像透明度类型
        int type = inputImage.getColorModel().getTransparency();
        int w = inputImage.getWidth();
        int h = inputImage.getHeight();
        BufferedImage img;
// 补白
        if (fill) {
            img = new BufferedImage(width, height, type);
            if (width > w)
                width = w;
            if (height > h)
                height = h;
// 不补
        } else {
            if (width > w)
                width = w;
            if (height > h)
                height = h;
            img = new BufferedImage(width, height, type);
        }
// 开启抗锯齿
        RenderingHints renderingHints = new RenderingHints(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_ANTIALIAS_ON);
// 使用高质量压缩
        renderingHints.put(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_RENDER_QUALITY);
        Graphics2D graphics2d = img.createGraphics();
        graphics2d.setRenderingHints(renderingHints);
        switch (startPoint) {
// 右上
            case POSITION_UPPERRIGHT:
                graphics2d.drawImage(inputImage, w - width, 0, w, height, 0, 0,
                        width, height, null);
                break;
// 左下
            case POSITION_LOWERLEFT:
                graphics2d.drawImage(inputImage, 0, h - height, width, h, 0, 0,
                        width, height, null);
                break;
// 右下
            case POSITION_LOWERRIGHT:
                graphics2d.drawImage(inputImage, w - width, h - height, w, h, 0, 0,
                        width, height, null);
                break;
// 默认左上
            case POSITION_UPPERLEFT:
            default:
                graphics2d.drawImage(inputImage, 0, 0, width, height, 0, 0, width,
                        height, null);
        }
        graphics2d.dispose();
        return img;
    }


    /**
     * 以指定角度旋转图片：使用正角度 theta 进行旋转，可将正 x 轴上的点转向正 y 轴。
     *
     * @param inputImage
     * @param degree     角度:以度数为单位
     * @return
     */
    public static BufferedImage rotateImage(final BufferedImage inputImage,
                                            final int degree) {
        int w = inputImage.getWidth();
        int h = inputImage.getHeight();
        int type = inputImage.getColorModel().getTransparency();
        BufferedImage img = new BufferedImage(w, h, type);
        Graphics2D graphics2d = img.createGraphics();
// 开启抗锯齿
        RenderingHints renderingHints = new RenderingHints(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_ANTIALIAS_ON);
// 使用高质量压缩
        renderingHints.put(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_RENDER_QUALITY);
        graphics2d.setRenderingHints(renderingHints);
        graphics2d.rotate(Math.toRadians(degree), w / 2, h / 2);
        graphics2d.drawImage(inputImage, 0, 0, null);
        graphics2d.dispose();
        return img;
    }


    /**
     * 水平翻转图像
     *
     * @param inputImage 目标图像
     * @return
     */
    public static BufferedImage flipHorizontalImage(
            final BufferedImage inputImage) {
        int w = inputImage.getWidth();
        int h = inputImage.getHeight();
        BufferedImage img;
        Graphics2D graphics2d;
        (graphics2d = (img = new BufferedImage(w, h, inputImage.getColorModel()
                .getTransparency())).createGraphics()).drawImage(inputImage, 0,
                0, w, h, w, 0, 0, h, null);
        graphics2d.dispose();
        return img;
    }


    /**
     * 竖直翻转图像
     *
     * @param inputImage 目标图像
     * @return
     */
    public static BufferedImage flipVerticalImage(final BufferedImage inputImage) {
        int w = inputImage.getWidth();
        int h = inputImage.getHeight();
        BufferedImage img;
        Graphics2D graphics2d;
        (graphics2d = (img = new BufferedImage(w, h, inputImage.getColorModel()
                .getTransparency())).createGraphics()).drawImage(inputImage, 0,
                0, w, h, 0, h, w, 0, null);
        graphics2d.dispose();
        return img;
    }


    /**
     * 图片水印
     *
     * @param inputImage 待处理图像
     * @param markImage  水印图像
     * @param x          水印位于图片左上角的 x 坐标值
     * @param y          水印位于图片左上角的 y 坐标值
     * @param alpha      水印透明度 0.1f ~ 1.0f
     */
    public static BufferedImage waterMark(BufferedImage inputImage,
                                          BufferedImage markImage, int x, int y, float alpha) {
        BufferedImage image = new BufferedImage(inputImage.getWidth(),
                inputImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.drawImage(inputImage, 0, 0, null);
// 加载水印图像
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP,
                alpha));
        g.drawImage(markImage, x, y, null);
        g.dispose();
        return image;
    }


    /**
     * 文字水印
     *
     * @param inputImage 待处理图像
     * @param text       水印文字
     * @param font       水印字体信息
     * @param color      水印字体颜色
     * @param x          水印位于图片左上角的 x 坐标值
     * @param y          水印位于图片左上角的 y 坐标值
     * @param alpha      水印透明度 0.1f ~ 1.0f
     */
    public static BufferedImage textMark(BufferedImage inputImage, String text,
                                         Font font, Color color, int x, int y, float alpha) {
        Font dfont = (font == null) ? new Font("宋体", 20, 13) : font;
        BufferedImage image = new BufferedImage(inputImage.getWidth(),
                inputImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.drawImage(inputImage, 0, 0, null);
        g.setColor(color);
        g.setFont(dfont);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP,
                alpha));
        g.drawString(text, x, y);
        g.dispose();
        return image;
    }


    /**
     * 图像彩色转黑白色
     *
     * @param inputImage
     * @return 转换后的BufferedImage
     */
    public final static BufferedImage toGray(BufferedImage inputImage) {
        ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
// 对源 BufferedImage 进行颜色转换。如果目标图像为 null，
// 则根据适当的 ColorModel 创建 BufferedImage。
        ColorConvertOp op = new ColorConvertOp(cs, null);
        return op.filter(inputImage, null);
    }


    /**
     * 图像彩色转为黑白
     *
     * @param srcImageFile  源图像地址
     * @param destImageFile 目标图像地址
     * @param formatType    目标图像格式：如果formatType为null;将默认转换为PNG
     */
    public final static void toGray(String srcImageFile, String destImageFile,
                                    String formatType) {
        try {
            BufferedImage src = ImageIO.read(new File(srcImageFile));
            ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
            ColorConvertOp op = new ColorConvertOp(cs, null);
            src = op.filter(src, null);
// 如果formatType为null;将默认转换为PNG
            if (formatType == null) {
                formatType = "PNG";
            }
            ImageIO.write(src, formatType, new File(destImageFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 图像类型转换：GIF->JPG、GIF->PNG、PNG->JPG、PNG->GIF(X)、BMP->PNG
     *
     * @param inputImage    源图像地址
     * @param formatType    包含格式非正式名称的 String：如JPG、JPEG、GIF等
     * @param destImageFile 目标图像地址
     */
    public final static void convert(BufferedImage inputImage,
                                     String formatType, String destImageFile) {
        try {
            ImageIO.write(inputImage, formatType, new File(destImageFile));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 图像切割（指定切片的行数和列数）
     *
     * @param srcImageFile 源图像地址
     * @param destDir      切片目标文件夹
     * @param formatType   目标格式
     * @param rows         目标切片行数。默认2，必须是范围 [1, 20] 之内
     * @param cols         目标切片列数。默认2，必须是范围 [1, 20] 之内
     */
    public final static void cut(BufferedImage inputImage, String destDir,
                                 String formatType, int rows, int cols) {
        try {
            if (rows <= 0 || rows > 20)
                rows = 2; // 切片行数
            if (cols <= 0 || cols > 20)
                cols = 2; // 切片列数
// 读取源图像
// BufferedImage bi = ImageIO.read(new File(srcImageFile));
            int w = inputImage.getHeight(); // 源图宽度
            int h = inputImage.getWidth(); // 源图高度
            if (w > 0 && h > 0) {
                Image img;
                ImageFilter cropFilter;
                Image image = inputImage.getScaledInstance(w, h,
                        Image.SCALE_DEFAULT);
                int destWidth = w; // 每张切片的宽度
                int destHeight = h; // 每张切片的高度
// 计算切片的宽度和高度
                if (w % cols == 0) {
                    destWidth = w / cols;
                } else {
                    destWidth = (int) Math.floor(w / cols) + 1;
                }
                if (h % rows == 0) {
                    destHeight = h / rows;
                } else {
                    destHeight = (int) Math.floor(h / rows) + 1;
                }
// 循环建立切片
// 改进的想法:是否可用多线程加快切割速度
                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < cols; j++) {
// 四个参数分别为图像起点坐标和宽高
// 即: CropImageFilter(int x,int y,int width,int height)
                        cropFilter = new CropImageFilter(j * destWidth, i
                                * destHeight, destWidth, destHeight);
                        img = Toolkit.getDefaultToolkit().createImage(
                                new FilteredImageSource(image.getSource(),
                                        cropFilter));
                        BufferedImage tag = new BufferedImage(destWidth,
                                destHeight, BufferedImage.TYPE_INT_ARGB);
                        Graphics g = tag.getGraphics();
                        g.drawImage(img, 0, 0, null); // 绘制缩小后的图
                        g.dispose();
// 输出为文件
                        ImageIO.write(tag, formatType,
                                new File(destDir + "_r" + i + "_c" + j + "."
                                        + formatType.toLowerCase()));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 给图片添加文字水印
     *
     * @param pressText     水印文字
     * @param srcImageFile  源图像地址
     * @param destImageFile 目标图像地址
     * @param fontName      水印的字体名称
     * @param fontStyle     水印的字体样式
     * @param color         水印的字体颜色
     * @param fontSize      水印的字体大小
     * @param x             修正值
     * @param y             修正值
     * @param alpha         透明度：alpha 必须是范围 [0.0, 1.0] 之内（包含边界值）的一个浮点数字
     * @param formatType    目标格式
     */
    public final static void pressText(String pressText, String srcImageFile,
                                       String destImageFile, String fontName, int fontStyle, Color color,
                                       int fontSize, int x, int y, float alpha, String formatType) {
        try {
            File img = new File(srcImageFile);
            Image src = ImageIO.read(img);
            int width = src.getWidth(null);
            int height = src.getHeight(null);
            BufferedImage image = new BufferedImage(width, height,
                    BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = image.createGraphics();
            g.drawImage(src, 0, 0, width, height, null);
            g.setColor(color);
            g.setFont(new Font(fontName, fontStyle, fontSize));
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP,
                    alpha));
// 在指定坐标绘制水印文字
            g.drawString(pressText, (width - (getLength(pressText) * fontSize))
                    / 2 + x, (height - fontSize) / 2 + y);
            g.dispose();
            ImageIO.write((BufferedImage) image, formatType, new File(
                    destImageFile));// 输出到文件流
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 给图片添加图片水印
     *
     * @param pressImg      水印图片
     * @param srcImageFile  源图像地址
     * @param destImageFile 目标图像地址
     * @param x             修正值。 默认在中间
     * @param y             修正值。 默认在中间
     * @param alpha         透明度：alpha 必须是范围 [0.0, 1.0] 之内（包含边界值）的一个浮点数字
     * @param formatType    目标格式
     */
    public final static void pressImage(String pressImg, String srcImageFile,
                                        String destImageFile, int x, int y, float alpha, String formatType) {
        try {
            File img = new File(srcImageFile);
            Image src = ImageIO.read(img);
            int wideth = src.getWidth(null);
            int height = src.getHeight(null);
            BufferedImage image = new BufferedImage(wideth, height,
                    BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = image.createGraphics();
            g.drawImage(src, 0, 0, wideth, height, null);
// 水印文件
            Image src_biao = ImageIO.read(new File(pressImg));
            int wideth_biao = src_biao.getWidth(null);
            int height_biao = src_biao.getHeight(null);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP,
                    alpha));
            g.drawImage(src_biao, (wideth - wideth_biao) / 2,
                    (height - height_biao) / 2, wideth_biao, height_biao, null);
// 水印文件结束
            g.dispose();
            ImageIO.write((BufferedImage) image, formatType, new File(
                    destImageFile));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 计算text的长度（一个中文算两个字符）
     *
     * @param text
     * @return
     */
    public final static int getLength(String text) {
        int length = 0;
        for (int i = 0; i < text.length(); i++) {
            if (new String(text.charAt(i) + "").getBytes().length > 1) {
                length += 2;
            } else {
                length += 1;
            }
        }
        return length / 2;
    }


    /**
     * 任意角度旋转图片
     *
     * @param src
     * @param angel 角度
     * @return
     */
    public static BufferedImage Rotate(Image src, int angel) {
        int src_width = src.getWidth(null);
        int src_height = src.getHeight(null);
// calculate the new image size
        Rectangle rect_des = CalcRotatedSize(new Rectangle(new Dimension(
                src_width, src_height)), angel);


        BufferedImage res = null;
        res = new BufferedImage(rect_des.width, rect_des.height,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = res.createGraphics();
// transform
        g2.translate((rect_des.width - src_width) / 2,
                (rect_des.height - src_height) / 2);
        g2.rotate(Math.toRadians(angel), src_width / 2, src_height / 2);


        g2.drawImage(src, null, null);
        return res;
    }


    public static Rectangle CalcRotatedSize(Rectangle src, int angel) {
// if angel is greater than 90 degree, we need to do some conversion
        if (angel >= 90) {
            if (angel / 90 % 2 == 1) {
                int temp = src.height;
                src.height = src.width;
                src.width = temp;
            }
            angel = angel % 90;
        }


        double r = Math.sqrt(src.height * src.height + src.width * src.width) / 2;
        double len = 2 * Math.sin(Math.toRadians(angel) / 2) * r;
        double angel_alpha = (Math.PI - Math.toRadians(angel)) / 2;
        double angel_dalta_width = Math.atan((double) src.height / src.width);
        double angel_dalta_height = Math.atan((double) src.width / src.height);


        int len_dalta_width = (int) (len * Math.cos(Math.PI - angel_alpha
                - angel_dalta_width));
        int len_dalta_height = (int) (len * Math.cos(Math.PI - angel_alpha
                - angel_dalta_height));
        int des_width = src.width + len_dalta_width * 2;
        int des_height = src.height + len_dalta_height * 2;
        return new java.awt.Rectangle(new Dimension(des_width, des_height));
    }


    /**
     * 图片左转90度
     *
     * @param bufferedimage
     * @return
     */
    public static BufferedImage rotateImageLeft90(BufferedImage bufferedimage) {
        int w = bufferedimage.getWidth();
        int h = bufferedimage.getHeight();
        int type = bufferedimage.getColorModel().getTransparency();
        BufferedImage img;
        Graphics2D graphics2d;
        (graphics2d = (img = new BufferedImage(h, w, type)).createGraphics())
                .setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2d.rotate(Math.toRadians(270), w / 2, h / 2 + (w - h) / 2);
        graphics2d.drawImage(bufferedimage, 0, 0, null);
        graphics2d.dispose();
        return img;
    }


    /**
     * 图片右转90度
     *
     * @param bufferedimage
     * @return
     */
    public static BufferedImage rotateImageRight90(BufferedImage bufferedimage) {
        int w = bufferedimage.getWidth();
        int h = bufferedimage.getHeight();
        int type = bufferedimage.getColorModel().getTransparency();
        BufferedImage img;
        Graphics2D graphics2d;
        (graphics2d = (img = new BufferedImage(h, w, type)).createGraphics())
                .setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2d.rotate(Math.toRadians(90), w / 2 - (w - h) / 2, h / 2);
        graphics2d.drawImage(bufferedimage, 0, 0, null);
        graphics2d.dispose();
        return img;
    }


    /**
     * 图片对转
     *
     * @param file
     * @return
     * @throws Exception
     */
    public File rotateImageOppo(File file) throws Exception {
        BufferedImage bufferedimage = ImageIO.read(file);
        int w = bufferedimage.getWidth();
        int h = bufferedimage.getHeight();
        int type = bufferedimage.getColorModel().getTransparency();
        BufferedImage img;
        Graphics2D graphics2d;
        (graphics2d = (img = new BufferedImage(w, h, type)).createGraphics())
                .setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2d.rotate(Math.toRadians(180), w / 2, h / 2);
        graphics2d.drawImage(bufferedimage, 0, 0, null);
        graphics2d.dispose();
        ImageIO.write(img, "jpg", file);
        return file;
    }
}