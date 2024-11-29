package cn.z.phone2region;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
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
     * @return 是否已经初始化
     * @since 1.1.0
     */
    public static boolean initialized() {
        return !notInstantiated;
    }

    /**
     * 通过文件初始化实例
     *
     * @param path 文件路径
     */
    public static void initByFile(String path) {
        if (notInstantiated) {
            try {
                log.info("手机号码转区域初始化：文件路径LOCAL_PATH {}", path);
                init(Files.newInputStream(Paths.get(path)));
            } catch (Exception e) {
                throw new Phone2RegionException("初始化文件异常！", e);
            }
        } else {
            log.warn("已经初始化过了，不可重复初始化！");
        }
    }

    /**
     * 通过URL初始化实例<br>
     * 例如：<code>https://www.404z.cn/files/phone2region/v2.0.0/data/phone2region.zdb</code>
     *
     * @param url URL
     */
    public static void initByUrl(String url) {
        if (notInstantiated) {
            try {
                log.info("手机号码转区域初始化：URL路径URL_PATH {}", url);
                init(new URI(url).toURL().openConnection().getInputStream());
            } catch (Exception e) {
                throw new Phone2RegionException("初始化URL异常！", e);
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
                    if (inputStream == null) {
                        throw new Phone2RegionException("数据文件为空！");
                    }
                    try (ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {
                        // 解压
                        ZipEntry entry = zipInputStream.getNextEntry();
                        if (entry == null) {
                            throw new Phone2RegionException("数据文件为空！");
                        }
                        // 数据
                        buffer = ByteBuffer.wrap(inputStream2Bytes(zipInputStream))
                                .asReadOnlyBuffer().order(ByteOrder.LITTLE_ENDIAN);
                        int crc32OriginValue = buffer.getInt();
                        CRC32 crc32 = new CRC32();
                        crc32.update(buffer);
                        if (crc32OriginValue != (int) crc32.getValue()) {
                            throw new Phone2RegionException("数据文件校验错误！");
                        }
                        buffer.position(4);
                        int version = buffer.getInt();
                        buffer.position(12);
                        vector2AreaPtr = buffer.getInt();
                        vectorAreaPtr = buffer.getInt();
                        log.info("数据加载成功：版本号VERSION {} ，校验码CRC32 {}", version,
                                String.format("%08X", crc32OriginValue));
                        notInstantiated = false;
                    } catch (Exception e) {
                        throw new Phone2RegionException("初始化异常！", e);
                    } finally {
                        try {
                            inputStream.close();
                        } catch (Exception e) {
                            log.error("关闭异常！", e);
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
        } catch (Exception e) {
            throw new Phone2RegionException("手机号码 " + phone + " 不合法！", e);
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
     * 解析手机号码的区域(内部)
     *
     * @param phone 手机号码前7位-1300000
     * @return Region(找不到返回null)
     */
    private static Region innerParse(int phone) {
        if (notInstantiated) {
            throw new Phone2RegionException("未初始化！");
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
     *
     * @param pos 位置
     * @return 对齐后的位置
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
     * InputStream转byte[]
     *
     * @param input InputStream
     * @return byte[]
     */
    public static byte[] inputStream2Bytes(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int n;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }
        input.close();
        return output.toByteArray();
    }

}
