# Phone Number To Region 手机号码转区域

[![License](https://img.shields.io/github/license/ali1416/phone2region?label=License)](https://opensource.org/licenses/BSD-3-Clause)
[![Java Support](https://img.shields.io/badge/Java-8+-green)](https://openjdk.org/)
[![Maven Central](https://img.shields.io/maven-central/v/cn.404z/phone2region?label=Maven%20Central)](https://mvnrepository.com/artifact/cn.404z/phone2region)
[![Tag](https://img.shields.io/github/v/tag/ali1416/phone2region?label=Tag)](https://github.com/ALI1416/phone2region/tags)
[![Repo Size](https://img.shields.io/github/repo-size/ali1416/phone2region?label=Repo%20Size&color=success)](https://github.com/ALI1416/phone2region/archive/refs/heads/master.zip)

[![Java CI](https://github.com/ALI1416/phone2region/actions/workflows/ci.yml/badge.svg)](https://github.com/ALI1416/phone2region/actions/workflows/ci.yml)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=ALI1416_phone2region&metric=coverage)
![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=ALI1416_phone2region&metric=reliability_rating)
![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=ALI1416_phone2region&metric=sqale_rating)
![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=ALI1416_phone2region&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=ALI1416_phone2region)

## 简介

本项目根据[EeeMt/phone-number-geo](https://github.com/EeeMt/phone-number-geo)重构，并加上了数据文件压缩后从外部导入、静态方法调用等，以及支持[SpringBoot自动配置](https://github.com/ALI1416/phone2region-spring-boot-autoconfigure)

## 数据文件

- 数据文件目录：[点击查看](./data)

## 依赖导入

```xml
<dependency>
  <groupId>cn.404z</groupId>
  <artifactId>phone2region</artifactId>
  <version>2.0.0</version>
</dependency>
<dependency>
  <groupId>ch.qos.logback</groupId>
  <artifactId>logback-classic</artifactId>
  <version>1.4.6</version>
</dependency>
```

## 使用方法

常量

```java
final String url = "https://cdn.jsdelivr.net/gh/ali1416/phone2region@2.0.0/data/phone2region.zdb";
final String zdbPath = "E:/phone2region.zdb";
final String txtPath = "E:/phone2region.txt";
final String errorPath = "E:/phone2region.error.txt";
final int phone = 1875471;
```

### 通过url初始化

代码

```java
log.info("是否已经初始化：{}", Phone2Region.initialized());
Phone2Region.initByUrl(url);
log.info(String.valueOf(Phone2Region.initialized()));
log.info("是否已经初始化：{}", Phone2Region.initialized());
log.info(String.valueOf(Phone2Region.parse(phone)));
```

结果

```txt
[main] INFO cn.z.phone2region.Phone2RegionTest - 是否已经初始化：false
[main] INFO cn.z.phone2region.Phone2Region - 初始化，URL路径为：https://cdn.jsdelivr.net/gh/ali1416/phone2region@2.0.0/data/phone2region.zdb
[main] INFO cn.z.phone2region.Phone2Region - 数据加载成功，版本号为：20230225，校验码为：C8AEEA0A
[main] INFO cn.z.phone2region.Phone2RegionTest - 是否已经初始化：true
[main] INFO cn.z.phone2region.Phone2RegionTest - Region{province='山东', city='济宁', zipCode='272000', areaCode='0537', isp='移动'}
```

### 通过文件初始化

代码

```java
Phone2Region.initByFile(zdbPath);
log.info(String.valueOf(Phone2Region.parse(phone)));
```

结果

```txt
[main] INFO cn.z.phone2region.Phone2Region - 初始化，文件路径为：E:/phone2region.zdb
[main] INFO cn.z.phone2region.Phone2Region - 数据加载成功，版本号为：20230225，校验码为：C8AEEA0A
[main] INFO cn.z.phone2region.Phone2RegionTest - Region{province='山东', city='济宁', zipCode='272000', areaCode='0537', isp='移动'}
```

### 通过inputStream初始化

代码

```java
try {
    Phone2Region.init(new FileInputStream(zdbPath));
} catch (Exception ignore) {
}
log.info(String.valueOf(Phone2Region.parse(phone)));
```

结果

```txt
[main] INFO cn.z.phone2region.Phone2Region - 数据加载成功，版本号为：20230225，校验码为：C8AEEA0A
[main] INFO cn.z.phone2region.Phone2RegionTest - Region{province='山东', city='济宁', zipCode='272000', areaCode='0537', isp='移动'}
```

### 初始化多次

代码

```java
Phone2Region.initByFile(zdbPath);
Phone2Region.initByFile(zdbPath);
log.info(String.valueOf(Phone2Region.parse(phone)));
```

结果

```txt
[main] INFO cn.z.phone2region.Phone2Region - 初始化，文件路径为：E:/phone2region.zdb
[main] INFO cn.z.phone2region.Phone2Region - 数据加载成功，版本号为：20230225，校验码为：C8AEEA0A
[main] WARN cn.z.phone2region.Phone2Region - 已经初始化过了，不可重复初始化！
[main] INFO cn.z.phone2region.Phone2RegionTest - Region{province='山东', city='济宁', zipCode='272000', areaCode='0537', isp='移动'}
```

### 初始化异常

代码

```java
Phone2Region.initByFile("A:/1.txt");
log.info(String.valueOf(Phone2Region.parse(phone)));
```

结果

```txt
[main]  INFO cn.z.phone2region.Phone2Region - 初始化，文件路径为：A:/1.txt
[main] ERROR cn.z.phone2region.Phone2Region - 初始化文件异常！
java.io.FileNotFoundException: A:/1.txt (系统找不到指定的文件。)
cn.z.phone2region.Phone2RegionException: 初始化文件异常！
```

### 数据错误

代码

```java
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
```

结果

```txt
[main] INFO cn.z.phone2region.Phone2Region - 初始化，文件路径为：E:/phone2region.zdb
[main] INFO cn.z.phone2region.Phone2Region - 数据加载成功，版本号为：20230225，校验码为：C8AEEA0A
cn.z.phone2region.Phone2RegionException: 手机号码 123456789012 不合法！
cn.z.phone2region.Phone2RegionException: 手机号码 -1 不合法！
```

### 性能测试

代码

```java
Phone2Region.initByFile(zdbPath);
log.info(String.valueOf(Phone2Region.parse(phone)));
long startTime = System.currentTimeMillis();
for (int i = 1300000; i < 2000000; i++) {
    Phone2Region.parse(i);
}
long endTime = System.currentTimeMillis();
log.info("查询 {} 条数据，用时 {} 毫秒", 700000, endTime - startTime);
```

结果

```txt
[main] INFO cn.z.phone2region.Phone2Region - 初始化，文件路径为：E:/phone2region.zdb
[main] INFO cn.z.phone2region.Phone2Region - 数据加载成功，版本号为：20230225，校验码为：C8AEEA0A
[main] INFO cn.z.phone2region.Phone2RegionTest - Region{province='山东', city='济宁', zipCode='272000', areaCode='0537', isp='移动'}
[main] INFO cn.z.phone2region.Phone2RegionTest - 查询 700000 条数据，用时 173 毫秒
```

### 完整性测试

代码

```java
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
```

结果

```txt
[main] INFO cn.z.phone2region.Phone2Region - 初始化，文件路径为：E:/phone2region.zdb
[main] INFO cn.z.phone2region.Phone2Region - 数据加载成功，版本号为：20230225，校验码为：C8AEEA0A
[main] INFO cn.z.phone2region.Phone2RegionTest - Region{province='山东', city='济宁', zipCode='272000', areaCode='0537', isp='移动'}
[main] INFO cn.z.phone2region.Phone2RegionTest - 解析 700000 条数据，有值 497191 条，空值 202809 条，错误 0 条，用时 783 毫秒
```

## 交流

- [x] QQ：`1416978277`
- [x] 微信：`1416978277`
- [x] 支付宝：`1416978277@qq.com`

![交流](https://cdn.jsdelivr.net/gh/ALI1416/ALI1416/image/contact.png)

## 赞助

![赞助](https://cdn.jsdelivr.net/gh/ALI1416/ALI1416/image/donate.png)
