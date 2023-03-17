package cn.z.phone2region;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.zip.ZipInputStream;

/**
 * <h1>手机号码转区域</h1>
 *
 * <p>
 * <a href="https://github.com/EeeMt/phone-number-geo">参考项目</a>
 * <a href="https://github.com/xluohome/phonedata">数据来源</a>
 * <a href="https://github.com/ALI1416/phone2region-test">数据文件</a>
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
     * 初始化实例通过File
     *
     * @param path 文件路径
     * @see FileInputStream
     */
    public static void initByFile(String path) {
        if (notInstantiated) {
            try {
                log.info("初始化，文件路径为：{}", path);
                init(new FileInputStream(path));
            } catch (Exception e) {
                log.error("文件异常！", e);
            }
        } else {
            log.warn("已经初始化过了，不可重复初始化！");
        }
    }

    /**
     * 初始化实例通过URL<br>
     * 可以用：<code>https://cdn.jsdelivr.net/gh/ali1416/phone2region-test/data/phone2region.zdat</code>
     *
     * @param url URL
     * @see URL
     */
    public static void initByUrl(String url) {
        if (notInstantiated) {
            try {
                log.info("初始化，URL路径为：{}", url);
                init(new URL(url).openConnection().getInputStream());
            } catch (Exception e) {
                log.error("URL异常！", e);
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
                    try {
                        if (inputStream == null) {
                            log.error("数据为空！");
                            return;
                        }
                        // 解压并提取文件
                        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
                        zipInputStream.getNextEntry();
                        // 数据
                        buffer = ByteBuffer.wrap(inputStream2bytes(zipInputStream)) //
                                .asReadOnlyBuffer().order(ByteOrder.LITTLE_ENDIAN);
                        // 版本号
                        byte[] versionBytes = new byte[4];
                        buffer.get(versionBytes);
                        // 索引偏移量
                        indicesOffset = buffer.getInt();
                        indicesOffsetMax = buffer.capacity() - 9;
                        log.info("数据加载成功，版本号为：{}", new String(versionBytes));
                        notInstantiated = false;
                    } catch (Exception e) {
                        log.error("初始化异常！", e);
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
                }
                int recordPosEnd = buffer.position() - 1;
                // 读取记录内容
                byte[] recordBytes = new byte[recordPosEnd - recordPos];
                buffer.position(recordPos);
                buffer.get(recordBytes);
                // 返回结果
                return new Region(new String(recordBytes), getIsp(ispMark));
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
     *
     * @param b byte
     * @return ISP
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
        } catch (Exception ignore) {
        } finally {
            try {
                inputStream.close();
            } catch (Exception ignore) {
            }
        }
        return output.toByteArray();
    }

}
