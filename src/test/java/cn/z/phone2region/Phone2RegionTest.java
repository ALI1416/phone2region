package cn.z.phone2region;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * <h1>手机号码转区域测试</h1>
 *
 * <p>
 * createDate 2023/03/15 17:31:35
 * </p>
 *
 * @author ALI[ali-k@foxmail.com]
 * @since 1.0.0
 **/
@TestMethodOrder(MethodOrderer.MethodName.class)
@Slf4j
class Phone2RegionTest {

    final String url = "https://www.404z.cn/files/phone2region/v2.0.0/data/phone2region.zdb";
    final String zdbPath = "E:/phone2region.zdb";
    final String txtPath = "E:/phone2region.txt";
    final String errorPath = "E:/phone2region.error.txt";
    final int phone = 1875471;

    /**
     * 通过url初始化
     */
    @Test
    void test00InitByUrl() {
        log.info("是否已经初始化：{}", Phone2Region.initialized());
        Phone2Region.initByUrl(url);
        log.info("是否已经初始化：{}", Phone2Region.initialized());
        log.info(String.valueOf(Phone2Region.parse(phone)));
        // INFO cn.z.phone2region.Phone2RegionTest -- 是否已经初始化：false
        // INFO cn.z.phone2region.Phone2Region -- 手机号码转区域初始化：URL路径URL_PATH https://www.404z.cn/files/phone2region/v2.0.0/data/phone2region.zdb
        // INFO cn.z.phone2region.Phone2Region -- 数据加载成功：版本号VERSION 20230225 ，校验码CRC32 C8AEEA0A
        // INFO cn.z.phone2region.Phone2RegionTest -- 是否已经初始化：true
        // INFO cn.z.phone2region.Phone2RegionTest -- Region{province='山东', city='济宁', zipCode='272000', areaCode='0537', isp='移动'}
    }

    /**
     * 通过文件初始化
     */
    // @Test
    void test01InitByFile() {
        Phone2Region.initByFile(zdbPath);
        log.info(String.valueOf(Phone2Region.parse(phone)));
        // INFO cn.z.phone2region.Phone2Region -- 手机号码转区域初始化：文件路径LOCAL_PATH E:/phone2region.zdb
        // INFO cn.z.phone2region.Phone2Region -- 数据加载成功：版本号VERSION 20230225 ，校验码CRC32 C8AEEA0A
        // INFO cn.z.phone2region.Phone2RegionTest -- Region{province='山东', city='济宁', zipCode='272000', areaCode='0537', isp='移动'}
    }

    /**
     * 通过inputStream初始化
     */
    // @Test
    void test02InitByInputStream() throws IOException {
        Phone2Region.init(Files.newInputStream(Paths.get(zdbPath)));
        log.info(String.valueOf(Phone2Region.parse(phone)));
        // INFO cn.z.phone2region.Phone2Region -- 数据加载成功：版本号VERSION 20230225 ，校验码CRC32 C8AEEA0A
        // INFO cn.z.phone2region.Phone2RegionTest -- Region{province='山东', city='济宁', zipCode='272000', areaCode='0537', isp='移动'}
    }

    /**
     * 初始化多次
     */
    // @Test
    void test03InitMore() {
        Phone2Region.initByFile(zdbPath);
        Phone2Region.initByFile(zdbPath);
        log.info(String.valueOf(Phone2Region.parse(phone)));
        // INFO cn.z.phone2region.Phone2Region -- 手机号码转区域初始化：文件路径LOCAL_PATH E:/phone2region.zdb
        // INFO cn.z.phone2region.Phone2Region -- 数据加载成功：版本号VERSION 20230225 ，校验码CRC32 C8AEEA0A
        // WARN cn.z.phone2region.Phone2Region -- 已经初始化过了，不可重复初始化！
        // INFO cn.z.phone2region.Phone2RegionTest -- Region{province='山东', city='济宁', zipCode='272000', areaCode='0537', isp='移动'}
    }

    /**
     * 初始化异常
     */
    // @Test
    void test04InitException() {
        try {
            Phone2Region.initByFile("A:/1.txt");
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info(String.valueOf(Phone2Region.parse(phone)));
        // INFO cn.z.phone2region.Phone2Region -- 手机号码转区域初始化：文件路径LOCAL_PATH A:/1.txt
        // cn.z.phone2region.Phone2RegionException: 初始化文件异常！
        // java.io.FileNotFoundException: A:\1.txt (系统找不到指定的路径。)
        // cn.z.phone2region.Phone2RegionException: 未初始化！
    }

    /**
     * 数据错误
     */
    @Test
    void test05Error() {
        Phone2Region.initByFile(zdbPath);
        try {
            Phone2Region.parse("123456789012");
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Phone2Region.parse(-1L);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // INFO cn.z.phone2region.Phone2Region -- 手机号码转区域初始化：文件路径LOCAL_PATH E:/phone2region.zdb
        // INFO cn.z.phone2region.Phone2Region -- 数据加载成功：版本号VERSION 20230225 ，校验码CRC32 C8AEEA0A
        // cn.z.phone2region.Phone2RegionException: 手机号码 123456789012 不合法！
        // cn.z.phone2region.Phone2RegionException: 手机号码 -1 不合法！
    }

    /**
     * 性能测试
     */
    // @Test
    void test06PerformanceTest() {
        Phone2Region.initByFile(zdbPath);
        log.info(String.valueOf(Phone2Region.parse(phone)));
        long startTime = System.currentTimeMillis();
        for (int i = 1300000; i < 2000000; i++) {
            Phone2Region.parse(i);
        }
        long endTime = System.currentTimeMillis();
        log.info("查询 {} 条数据，用时 {} 毫秒", 700000, endTime - startTime);
        // INFO cn.z.phone2region.Phone2Region -- 手机号码转区域初始化：文件路径LOCAL_PATH E:/phone2region.zdb
        // INFO cn.z.phone2region.Phone2Region -- 数据加载成功：版本号VERSION 20230225 ，校验码CRC32 C8AEEA0A
        // INFO cn.z.phone2region.Phone2RegionTest -- Region{province='山东', city='济宁', zipCode='272000', areaCode='0537', isp='移动'}
        // INFO cn.z.phone2region.Phone2RegionTest - 查询 700000 条数据，用时 173 毫秒
    }

    /**
     * 完整性测试
     */
    // @Test
    void test07IntegrityTest() throws Exception {
        Phone2Region.initByFile(zdbPath);
        log.info(String.valueOf(Phone2Region.parse(phone)));
        long startTime = System.currentTimeMillis();
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(errorPath));
        BufferedReader bufferedReader = new BufferedReader(new FileReader(txtPath));
        String line = bufferedReader.readLine();
        int successCount = 0;
        int errorCount = 0;
        int nextPhone = 1300000;
        int nullCount = 0;
        while (line != null && !line.isEmpty()) {
            // 手机号码前7位|省份|城市|邮编|区号|ISP
            String[] s = line.split("\\|");
            String record = s[1] + "|" // 省份
                    + s[2] + "|" // 城市
                    + s[3] + "|" // 邮编
                    + s[4] + "|" // 区号
                    + s[5]; // ISP
            int phone = Integer.parseInt(s[0]);
            if (phone != nextPhone) {
                int count = phone - nextPhone;
                for (int i = 0; i < count; i++) {
                    if (Phone2Region.parse(nextPhone + i) != null) {
                        String error = "手机号码`" + (nextPhone + i) + "`解析错误，实际为`null`";
                        errorCount++;
                        log.error(error);
                        bufferedWriter.write(error + "\n");
                    }
                }
                nullCount += count;
                nextPhone += count;
            }
            int hash = (new Region(record)).toString().hashCode();
            if (hash != Phone2Region.parse(phone).toString().hashCode()) {
                String error = "解析记录`" + line + "`时发现手机号码`" + phone //
                        + "`解析错误，实际为`" + Phone2Region.parse(phone) + "`";
                errorCount++;
                log.error(error);
                bufferedWriter.write(error + "\n");
            }
            successCount++;
            nextPhone++;
            line = bufferedReader.readLine();
        }
        for (int i = nextPhone; i < 2000000; i++) {
            if (Phone2Region.parse(i) != null) {
                String error = "手机号码`" + (i) + "`解析错误，实际为`null`";
                errorCount++;
                log.error(error);
                bufferedWriter.write(error + "\n");
            }
        }
        nullCount += (2000000 - nextPhone);
        bufferedReader.close();
        bufferedWriter.flush();
        bufferedWriter.close();
        long endTime = System.currentTimeMillis();
        log.info("解析 {} 条数据，有值 {} 条，空值 {} 条，错误 {} 条，用时 {} 毫秒", 700000, successCount, nullCount, errorCount,
                endTime - startTime);
        // INFO cn.z.phone2region.Phone2Region -- 手机号码转区域初始化：文件路径LOCAL_PATH E:/phone2region.zdb
        // INFO cn.z.phone2region.Phone2Region -- 数据加载成功：版本号VERSION 20230225 ，校验码CRC32 C8AEEA0A
        // INFO cn.z.phone2region.Phone2RegionTest -- Region{province='山东', city='济宁', zipCode='272000', areaCode='0537', isp='移动'}
        // INFO cn.z.phone2region.Phone2RegionTest - 解析 700000 条数据，有值 497191 条，空值 202809 条，错误 0 条，用时 783 毫秒
    }

}
