package cn.z.phone2region;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * <h1>手机号码转区域</h1>
 *
 * <p>
 * createDate 2023/03/15 17:31:35
 * </p>
 *
 * @author ALI[ali-k@foxmail.com]
 * @since 1.0.0
 **/
public class Phone2Region {

    /**
     * 日志实例
     */
    private static final Logger log = LoggerFactory.getLogger(Phone2Region.class);
    /**
     * 标记没有实例化
     */
    private static volatile boolean notInstantiated = true;
    /**
     * 数据
     */
    private static ByteBuffer buffer;
    /**
     * 二级索引区指针
     */
    private static int vector2AreaPtr;
    /**
     * 索引区指针
     */
    private static int vectorAreaPtr;

    private Phone2Region() {
    }

    /**
     * 是否已经初始化
     *
     * @since 1.1.0
     */
    public static boolean initialized() {
        return !notInstantiated;
    }

    /**
     * 初始化实例通过File
     *
     * @param path 文件路径
     */
    public static void initByFile(String path) {
        if (notInstantiated) {
            try {
                log.info("初始化，文件路径为：{}", path);
                init(new FileInputStream(path));
            } catch (Exception e) {
                log.error("初始化文件异常！", e);
                throw new Phone2RegionException("初始化文件异常！");
            }
        } else {
            log.warn("已经初始化过了，不可重复初始化！");
        }
    }

    /**
     * 初始化实例通过URL<br>
     * 可以用：<code>https://cdn.jsdelivr.net/gh/ali1416/phone2region@2.0.0/data/phone2region.zdb</code>
     *
     * @param url URL
     */
    public static void initByUrl(String url) {
        if (notInstantiated) {
            try {
                log.info("初始化，URL路径为：{}", url);
                init(new URL(url).openConnection().getInputStream());
            } catch (Exception e) {
                log.error("初始化URL异常！", e);
                throw new Phone2RegionException("初始化URL异常！");
            }
        } else {
            log.warn("已经初始化过了，不可重复初始化！");
        }
    }

    /**
     * 初始化实例
     *
     * @param inputStream 压缩的zdb输入流
     */
    public static void init(InputStream inputStream) {
        if (notInstantiated) {
            synchronized (Phone2Region.class) {
                if (notInstantiated) {
                    try {
                        if (inputStream == null) {
                            throw new Phone2RegionException("数据文件为空！");
                        }
                        // 解压
                        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
                        ZipEntry entry = zipInputStream.getNextEntry();
                        if (entry == null) {
                            throw new Phone2RegionException("数据文件异常！");
                        }
                        // 数据
                        buffer = ByteBuffer.wrap(inputStream2bytes(zipInputStream)) //
                                .asReadOnlyBuffer().order(ByteOrder.LITTLE_ENDIAN);
                        int crc32OriginValue = buffer.getInt();
                        CRC32 crc32 = new CRC32();
                        crc32.update(buffer);
                        if (crc32OriginValue != (int) crc32.getValue()) {
                            throw new Phone2RegionException("数据文件校验错误！");
                        }
                        buffer.position(4);
                        int version = buffer.getInt();
                        buffer.position(buffer.position() + 4);
                        vector2AreaPtr = buffer.getInt();
                        vectorAreaPtr = buffer.getInt();
                        log.info("数据加载成功，版本号为：{}，校验码为：{}", version,
                                Integer.toHexString(crc32OriginValue).toUpperCase());
                        notInstantiated = false;
                    } catch (Exception e) {
                        log.error("初始化异常！", e);
                        throw new Phone2RegionException("初始化异常！");
                    } finally {
                        if (inputStream != null) {
                            try {
                                inputStream.close();
                            } catch (Exception e) {
                                log.error("关闭异常！", e);
                            }
                        }
                    }
                } else {
                    log.warn("已经初始化过了，不可重复初始化！");
                }
            }
        } else {
            log.warn("已经初始化过了，不可重复初始化！");
        }
    }

    /**
     * 解析手机号码的区域
     *
     * @param phone 手机号码(前7-11位)
     * @return Region
     */
    public static Region parse(String phone) {
        if (notInstantiated) {
            log.error("未初始化！");
            return null;
        }
        // 7-11位
        if (phone == null || phone.length() < 7 || phone.length() > 11) {
            return null;
        }
        int num;
        try {
            num = Integer.parseInt(phone.substring(0, 7));
        } catch (Exception ignore) {
            return null;
        }
        // 1300000-1999999
        if (num < 1300000 || num > 1999999) {
            return null;
        }

        return null;
    }

    /**
     * 解析手机号码的区域
     *
     * @param phone 手机号码前7位-1300000
     * @return Region
     */
    public static Region innerParse(int phone) {
        return null;
    }

    /**
     * 字节对齐
     */
    private static int align(int pos) {
        int remain = (pos - vectorAreaPtr) % 5;
        if (pos - vectorAreaPtr < 5) {
            return pos - remain;
        } else if (remain != 0) {
            return pos + 5 - remain;
        } else {
            return pos;
        }
    }

    /**
     * inputStream转byte[]
     */
    public static byte[] inputStream2bytes(InputStream inputStream) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int n;
        try {
            while (-1 != (n = inputStream.read(buffer))) {
                output.write(buffer, 0, n);
            }
        } catch (Exception e) {
            log.error("转换异常！", e);
        } finally {
            try {
                inputStream.close();
            } catch (Exception e) {
                log.error("关闭异常！", e);
            }
        }
        return output.toByteArray();
    }

}
