package cn.z.phone2region;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.text.Collator;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * <h1>数据文件生成测试</h1>
 *
 * <p>
 * createDate 2023/03/23 14:28:16
 * </p>
 *
 * @author ALI[ali-k@foxmail.com]
 * @since 1.0.0
 **/
@TestMethodOrder(MethodOrderer.MethodName.class)
@Slf4j
class DataGenerationTest {

    final String datPath = "E:/phone.dat";
    final String dat2Path = "E:/phone2region.dat";
    final String txtPath = "E:/phone2region.txt";
    final String dbPath = "E:/phone2region.db";
    final String zdbPath = "E:/phone2region.zdb";
    final int version = 20230225;
    final String version2 = "2302";

    /**
     * 数据文件生成
     */
    // @Test
    void test00DataGeneration() throws Exception {
        test03Txt2Db();
        test04Compress();
    }

    /**
     * dat文件转txt文件
     */
    // @Test
    void test01Dat2Txt() throws Exception {
        log.info("---------- dat文件转txt文件 ---------- 开始");
        // 记录区Map<偏移量,记录值>
        Map<Integer, String> recordMap = new HashMap<>();
        // 索引区List(手机号码前7位|省份|城市|邮编|区号|ISP)
        List<String> vectorList = new ArrayList<>();
        /* 读取文件 */
        FileInputStream fileInputStream = new FileInputStream(datPath);
        byte[] bytes = cn.z.phone2region.Phone2Region.inputStream2bytes(fileInputStream);
        ByteBuffer buffer = ByteBuffer.wrap(bytes).asReadOnlyBuffer().order(ByteOrder.LITTLE_ENDIAN);
        /* 获取版本号 */
        // pos 0 len 4 type utf8
        // 32 33 30 32 -> 2302
        byte[] versionBytes = new byte[4];
        buffer.get(versionBytes);
        String version = new String(versionBytes);
        log.info("版本号为：{}", version);
        /* 获取索引偏移量 */
        // pos 4 len 4 type int
        // 59 27 00 00 -> 10073
        int indicesOffset = buffer.getInt();
        log.info("索引偏移量为：{}", indicesOffset);
        /* 获取记录区Map */
        // pos 8 len indicesOffset-8 type list<utf8> split 0x00
        // E5 AE 89 E5 BE BD 7C E5 B7 A2 E6 B9 96 7C 32 33 38 30 30 30 7C 30 35 35 31 00 -> 安徽|巢湖|238000|0551
        // E5 AE 89 E5 BE BD 7C E5 90 88 E8 82 A5 7C 32 33 30 30 30 30 7C 30 35 35 31 00 -> 安徽|合肥|230000|0551
        // pos 7098 -> 1B BA
        // E5 B1 B1 E4 B8 9C 7C E6 B5 8E E5 AE 81 7C 32 37 32 30 30 30 7C 30 35 33 37 00 -> 山东|济宁|272000|0537
        int start = buffer.position();
        while (start != indicesOffset) {
            buffer.position(start);
            while (buffer.get() != 0) {
                // 每条记录以0x00结尾
            }
            int end = buffer.position();
            // 读取当前记录值
            buffer.position(start);
            byte[] b = new byte[end - start - 1];
            buffer.get(b);
            recordMap.put(start, new String(b));
            start = end;
        }
        log.info("读取到记录区数据 {} 条", recordMap.size());
        /* 索引区保存到结果List */
        // pos indicesOffset len capacity-indicesOffset type list<byte[9]>
        // pos 0 len 4 type int -> tel
        // pos 4 len 4 type int -> recordPos
        // pos 8 len 1 type byte -> isp
        // 20 D6 13 00 4E 1A 00 00 02
        // 21 D6 13 00 2C 12 00 00 02
        // pos 3757934 -> 39 57 6E , tel 1875471 -> 00 1C 9E 0F , recordPos 7098 -> 00 00 1B BA , isp 01 -> 移动
        // 0F 9E 1C 00 BA 1B 00 00 01
        buffer.position(indicesOffset);
        int capacity = buffer.capacity();
        while (buffer.position() != capacity) {
            int tel = buffer.getInt();
            int recordPos = buffer.getInt();
            byte isp = buffer.get();
            vectorList.add(tel + "|" + recordMap.get(recordPos) + "|" + cn.z.phone2region.Phone2Region.getIsp(isp));
        }
        log.info("读取到索引区数据 {} 条", vectorList.size());
        /* 保存文件 */
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(txtPath));
        for (String result : vectorList) {
            bufferedWriter.write(result + "\n");
        }
        bufferedWriter.flush();
        bufferedWriter.close();
        log.info("写入文件完成");
        log.info("---------- dat文件转txt文件 ---------- 结束");
    }

    /**
     * txt文件转dat文件
     */
    @Test
    void test02Txt2Dat() throws Exception {
        log.info("---------- txt文件转dat文件 ---------- 开始");
        final byte BYTE0 = 0;
        // 记录区Set
        Set<String> recordSet = new TreeSet<>((o1, o2) -> Collator.getInstance(Locale.CHINA).compare(o1, o2));
        // 记录区Map<记录值hash,Record>
        Map<Integer, Record> recordMap = new LinkedHashMap<>();
        // 索引区List[{手机号码前7位,省份|城市|邮编|区号,ISP}]
        List<String[]> vectorList = new ArrayList<>();
        // 结果Map<手机号码前7位,{省份|城市|邮编|区号,ISP}>
        /* 读取文件 */
        BufferedReader bufferedReader = new BufferedReader(new FileReader(txtPath));
        String line = bufferedReader.readLine();
        while (line != null && !line.isEmpty()) {
            // 手机号码前7位|省份|城市|邮编|区号|ISP
            String[] s = line.split("\\|");
            String region = s[1] + "|" + s[2] + "|" + s[3] + "|" + s[4];
            recordSet.add(region);
            vectorList.add(new String[]{s[0], region, s[5]});
            line = bufferedReader.readLine();
        }
        bufferedReader.close();
        log.info("记录区数据 {} 条", recordSet.size());
        log.info("索引区数据 {} 条", vectorList.size());
        /* 计算文件大小 */
        // 版本号4字节，索引偏移量4字节
        int size = 8;
        // 记录区
        for (String s : recordSet) {
            // java的String为UTF16LE是变长4字节，而UTF8是变长3字节
            byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
            recordMap.put(s.hashCode(), new Record(size, bytes));
            // 每条记录以0x00结尾
            size += (bytes.length + 1);
        }
        // 记录获取索引偏移量
        int indicesOffset = size;
        // 索引区
        size += vectorList.size() * 9;
        log.info("文件容量 {} 字节", size);
        /* 创建二进制文件 */
        ByteBuffer buffer = ByteBuffer.allocate(size).order(ByteOrder.LITTLE_ENDIAN);
        // 版本号
        buffer.put(version2.getBytes(StandardCharsets.UTF_8));
        // 索引偏移量
        buffer.putInt(indicesOffset);
        // 记录区
        for (Record r : recordMap.values()) {
            buffer.put(r.getBytes());
            // 每条记录以0x00结尾
            buffer.put(BYTE0);
        }
        // 索引区
        for (String[] s : vectorList) {
            // 手机号码前7位
            buffer.putInt(Integer.parseInt(s[0]));
            // 偏移量
            buffer.putInt(recordMap.get(s[1].hashCode()).getPrt());
            // ISP
            buffer.put(getIsp(s[2]));
        }
        /* 导出文件 */
        FileOutputStream fileOutputStream = new FileOutputStream(dat2Path);
        fileOutputStream.write(buffer.array());
        fileOutputStream.flush();
        fileOutputStream.close();
        log.info("写入文件完成");
        log.info("---------- txt文件转dat文件 ---------- 结束");
    }

    /**
     * 获取ISP
     */
    byte getIsp(String isp) {
        switch (isp) {
            case "移动": {
                return 1;
            }
            case "联通": {
                return 2;
            }
            case "电信": {
                return 3;
            }
            case "移动虚拟": {
                return 4;
            }
            case "联通虚拟": {
                return 5;
            }
            case "电信虚拟": {
                return 6;
            }
            case "广电": {
                return 7;
            }
            case "广电虚拟": {
                return 8;
            }
            default: {
                return Byte.MAX_VALUE;
            }
        }
    }

    /**
     * 记录
     */
    static class Record {
        /**
         * 指针
         */
        private int prt;
        /**
         * 记录值
         */
        private byte[] bytes;
        /**
         * 记录值长度
         */
        private int byteLength;

        public Record() {
        }

        public Record(int prt, byte[] bytes) {
            this.prt = prt;
            this.bytes = bytes;
        }

        public Record(int prt, byte[] bytes, int byteLength) {
            this.prt = prt;
            this.bytes = bytes;
            this.byteLength = byteLength;
        }

        public int getPrt() {
            return prt;
        }

        public void setPrt(int prt) {
            this.prt = prt;
        }

        public byte[] getBytes() {
            return bytes;
        }

        public void setBytes(byte[] bytes) {
            this.bytes = bytes;
        }

        public int getByteLength() {
            return byteLength;
        }

        public void setByteLength(int byteLength) {
            this.byteLength = byteLength;
        }

        @Override
        public String toString() {
            return "Record{" + "prt=" + prt + ", bytes=" + new String(bytes) + ", byteLength=" + byteLength + '}';
        }
    }

    /**
     * txt文件转db文件
     */
    // @Test
    void test03Txt2Db() {

    }

    /**
     * 压缩
     */
    // @Test
    void test04Compress() throws Exception {
        log.info("---------- 压缩 ---------- 开始");
        ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(zdbPath));
        zipOutputStream.putNextEntry(new ZipEntry(new File(dbPath).getName()));
        FileInputStream fileInputStream = new FileInputStream(dbPath);
        byte[] buffer = new byte[4096];
        int n;
        while (-1 != (n = fileInputStream.read(buffer))) {
            zipOutputStream.write(buffer, 0, n);
        }
        zipOutputStream.flush();
        fileInputStream.close();
        zipOutputStream.closeEntry();
        zipOutputStream.close();
        log.info("---------- 压缩 ---------- 结束");
    }

}
