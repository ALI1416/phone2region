package cn.z.phone2region;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
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
     * 例如：<code>https://cdn.jsdelivr.net/gh/ali1416/phone2region@2.0.0/data/phone2region.zdb</code>
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
     * @return Region(找不到返回null)
     */
    public static Region parse(String phone) {
        // 7-11位
        if (phone == null || phone.length() < 7 || phone.length() > 11) {
            throw new Phone2RegionException("手机号码 " + phone + " 不合法！");
        }
        int num;
        try {
            num = Integer.parseInt(phone.substring(0, 7));
        } catch (Exception ignore) {
            throw new Phone2RegionException("手机号码 " + phone + " 不合法！");
        }
        // 1300000-1999999
        if (num < 1300000 || num > 1999999) {
            throw new Phone2RegionException("手机号码 " + phone + " 不合法！");
        }
        return innerParse(num - 1300000);
    }

    /**
     * 解析手机号码的区域
     *
     * @param phone 手机号码(11位)
     * @return Region(找不到返回null)
     * @since 2.0.0
     */
    public static Region parse(long phone) {
        // 1300000_0000-1999999_9999
        if (phone < 1300000_0000L || phone > 1999999_9999L) {
            throw new Phone2RegionException("手机号码 " + phone + " 不合法！");
        }
        return innerParse(((int) (phone / 10000)) - 1300000);
    }

    /**
     * 解析手机号码的区域
     *
     * @param phone 手机号码(前7位)
     * @return Region(找不到返回null)
     * @since 2.0.0
     */
    public static Region parse(int phone) {
        // 1300000-1999999
        if (phone < 1300000 || phone > 1999999) {
            throw new Phone2RegionException("手机号码 " + phone + " 不合法！");
        }
        return innerParse(phone - 1300000);
    }

    /**
     * 解析手机号码的区域
     *
     * @param phone 手机号码前7位-1300000
     * @return Region(找不到返回null)
     */
    public static Region innerParse(int phone) {
        if (notInstantiated) {
            log.error("未初始化！");
            return null;
        }

        // 二级索引区
        buffer.position(vector2AreaPtr + ((phone >> 8) << 2));
        int left = buffer.getInt();
        int right = buffer.getInt();

        // 索引区
        if (left == right) {
            return null;
        } else {
            right -= 5;
            // 二分查找
            int num = 0;
            int phoneSegments = phone & 0xFF;
            // 索引区
            while (left <= right) {
                int mid = align((left + right) / 2);
                // 查找是否匹配到
                buffer.position(mid);
                num = buffer.get() & 0xFF;
                if (phoneSegments < num) {
                    right = mid - 5;
                } else if (phoneSegments > num) {
                    left = mid + 5;
                } else {
                    break;
                }
            }
            if (num != phoneSegments) {
                return null;
            }
        }

        // 记录区
        buffer.position(buffer.getInt());
        byte[] recordValue = new byte[buffer.get() & 0xFF];
        buffer.get(recordValue);
        return new Region(new String(recordValue, StandardCharsets.UTF_8));
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
