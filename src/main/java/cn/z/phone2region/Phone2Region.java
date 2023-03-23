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
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * <h1>手机号码转区域</h1>
 *
 * <p>
 * <a href="https://github.com/EeeMt/phone-number-geo">参考项目</a>
 * <a href="https://github.com/xluohome/phonedata">数据来源</a>
 * <a href="https://github.com/ALI1416/phone2region">数据文件</a>
 * </p>
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
     * 索引偏移量
     */
    private static int indicesOffset;
    /**
     * 索引偏移量最大值
     */
    private static int indicesOffsetMax;

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
     * 可以用：<code>https://cdn.jsdelivr.net/gh/ali1416/phone2region@master/data/phone2region.zdat</code>
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
     * @param inputStream 压缩的zdat输入流
     */
    public static void init(InputStream inputStream) {
        if (notInstantiated) {
            synchronized (Phone2Region.class) {
                if (notInstantiated) {
                    if (inputStream == null) {
                        throw new Phone2RegionException("数据文件为空！");
                    }
                    try {
                        // 解压并提取文件
                        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
                        ZipEntry entry = zipInputStream.getNextEntry();
                        if (entry == null) {
                            throw new Phone2RegionException("数据文件异常！");
                        }
                        // 数据
                        buffer = ByteBuffer.wrap(inputStream2bytes(zipInputStream)) //
                                .asReadOnlyBuffer().order(ByteOrder.LITTLE_ENDIAN);
                        // 版本号
                        byte[] versionBytes = new byte[4];
                        buffer.get(versionBytes);
                        String version = new String(versionBytes);
                        // 索引偏移量
                        indicesOffset = buffer.getInt();
                        indicesOffsetMax = buffer.capacity() - 9;
                        // 检查数据文件是否正确
                        // [0,99]
                        int year = ((versionBytes[0] - 48) * 10 + versionBytes[1] - 48);
                        // [1,12]
                        int month = ((versionBytes[2] - 48) * 10 + versionBytes[3] - 48);
                        // indicesOffset [9,indicesOffsetMax]
                        if (year < 0 || year > 99 || month < 1 || month > 12 || indicesOffset < 9 || indicesOffset > indicesOffsetMax) {
                            throw new Phone2RegionException("数据文件错误！" + //
                                    "年份应为[0,99]，当前为" + year + "；" + //
                                    "月份应为[1,12]，当前为" + month + "；" + //
                                    "索引偏移量应为[9," + indicesOffsetMax + "]，当前为" + indicesOffset);
                        }
                        log.info("数据加载成功，版本号为：{}", version);
                        notInstantiated = false;
                    } catch (Exception e) {
                        log.error("初始化异常！", e);
                        throw new Phone2RegionException("初始化异常！");
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
        // 二分查找
        int left = indicesOffset;
        int right = left + (num - 1300000) * 9;
        if (right > indicesOffsetMax) {
            right = indicesOffsetMax;
        }
        int mid;
        while (left <= right) {
            mid = (left + right) / 2;
            mid = align(mid);
            // 查找是否匹配到
            buffer.position(mid);
            int compare = Integer.compare(buffer.getInt(), num);
            if (compare == 0) {
                // 跳过前4字节(手机号码前7位)
                buffer.position(mid + 4);
                // 记录pos
                int recordPos = buffer.getInt();
                // isp标记位
                byte ispMark = buffer.get();
                // 计算记录长度
                buffer.position(recordPos);
                while ((buffer.get()) != 0) {
                    // 每条记录以0x00结尾
                }
                int recordPosEnd = buffer.position() - 1;
                // 读取记录内容
                byte[] recordBytes = new byte[recordPosEnd - recordPos];
                buffer.position(recordPos);
                buffer.get(recordBytes);
                // 返回结果
                return new Region(new String(recordBytes, StandardCharsets.UTF_8), getIsp(ispMark));
            } else if (compare > 0) {
                right = mid - 9;
            } else {
                left = mid + 9;
            }
        }
        return null;
    }

    /**
     * 字节对齐
     */
    private static int align(int pos) {
        int remain = (pos - indicesOffset) % 9;
        if (pos - indicesOffset < 9) {
            return pos - remain;
        } else if (remain != 0) {
            return pos + 9 - remain;
        } else {
            return pos;
        }
    }

    /**
     * 获取ISP
     */
    public static String getIsp(byte b) {
        switch (b) {
            case 1: {
                return "移动";
            }
            case 2: {
                return "联通";
            }
            case 3: {
                return "电信";
            }
            case 4: {
                return "移动虚拟";
            }
            case 5: {
                return "联通虚拟";
            }
            case 6: {
                return "电信虚拟";
            }
            case 7: {
                return "广电";
            }
            case 8: {
                return "广电虚拟";
            }
            default: {
                return "未知";
            }
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
