package cn.z.phone2region;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.FileInputStream;

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

    /**
     * 通过url初始化
     */
    @Test
    void test00InitByUrl() {
        log.info(String.valueOf(Phone2Region.initialized()));
        Phone2Region.initByUrl("https://cdn.jsdelivr.net/gh/ali1416/phone2region@master/data/phone2region.zdat");
        log.info(String.valueOf(Phone2Region.initialized()));
        log.info(String.valueOf(Phone2Region.parse("18754710000")));
        // [main] INFO cn.z.phone2region.Phone2Region - false
        // [main] INFO cn.z.phone2region.Phone2Region - 初始化，URL路径为：https://cdn.jsdelivr.net/gh/ali1416/phone2region@master/data/phone2region.zdat
        // [main] INFO cn.z.phone2region.Phone2Region - 数据加载成功，版本号为：2302
        // [main] INFO cn.z.phone2region.Phone2Region - true
        // [main] INFO cn.z.phone2region.Phone2RegionTest - Region{province='山东', city='济宁', zipCode='272000',
        // areaCode='0537', isp='移动'}
    }

    /**
     * 通过文件初始化
     */
    // @Test
    void test01InitByFile() {
        Phone2Region.initByFile("E:/phone2region.zip");
        log.info(String.valueOf(Phone2Region.parse("18754710000")));
        // [main] INFO cn.z.phone2region.Phone2Region - 初始化，文件路径为：E:/phone2region.zip
        // [main] INFO cn.z.phone2region.Phone2Region - 数据加载成功，版本号为：2302
        // [main] INFO cn.z.phone2region.Phone2RegionTest - Region{province='山东', city='济宁', zipCode='272000',
        // areaCode='0537', isp='移动'}
    }

    /**
     * 通过inputStream初始化
     */
    // @Test
    void test02InitByInputStream() {
        try {
            Phone2Region.init(new FileInputStream("E:/phone2region.zip"));
        } catch (Exception ignore) {
        }
        log.info(String.valueOf(Phone2Region.parse("18754710000")));
        // [main] INFO cn.z.phone2region.Phone2Region - 数据加载成功，版本号为：2302
        // [main] INFO cn.z.phone2region.Phone2RegionTest - Region{province='山东', city='济宁', zipCode='272000',
        // areaCode='0537', isp='移动'}
    }

    /**
     * 初始化多次
     */
    // @Test
    void test03InitMore() {
        Phone2Region.initByFile("E:/phone2region.zip");
        Phone2Region.initByFile("E:/phone2region.zip");
        log.info(String.valueOf(Phone2Region.parse("18754710000")));
        // [main] INFO cn.z.phone2region.Phone2Region - 初始化，文件路径为：E:/phone2region.zip
        // [main] INFO cn.z.phone2region.Phone2Region - 数据加载成功，版本号为：2302
        // [main] WARN cn.z.phone2region.Phone2Region - 已经初始化过了，不可重复初始化！
        // [main] INFO cn.z.phone2region.Phone2RegionTest - Region{province='山东', city='济宁', zipCode='272000',
        // areaCode='0537', isp='移动'}
    }

    /**
     * 初始化异常
     */
    // @Test
    void test04InitException() {
        Phone2Region.initByFile("E:/phone2region");
        log.info(String.valueOf(Phone2Region.parse("18754710000")));
        // [main]  INFO cn.z.phone2region.Phone2Region - 初始化，文件路径为：E:/phone2region
        // [main] ERROR cn.z.phone2region.Phone2Region - 初始化文件异常！
        // java.io.FileNotFoundException: E:\phone2region (系统找不到指定的文件。)
    }

    /**
     * 覆盖测试(2302版本497191条数据)
     */
    // @Test
    void test05CoverageTest() {
        Phone2Region.initByFile("E:/phone2region.zip");
        long startTime = System.currentTimeMillis();
        int count = 0;
        for (int i = 1300000; i < 2000000; i++) {
            if (Phone2Region.parse(String.valueOf(i)) != null) {
                count++;
            }
        }
        long endTime = System.currentTimeMillis();
        log.info("查询700000条数据，{}条有效数据，用时{}毫秒", count, endTime - startTime);
        // [main] INFO cn.z.phone2region.Phone2Region - 初始化，文件路径为：E:/phone2region.zip
        // [main] INFO cn.z.phone2region.Phone2Region - 数据加载成功，版本号为：2302
        // [main] INFO cn.z.phone2region.Phone2RegionTest - 查询700000条数据，497191条有效数据，用时322毫秒
    }

}
