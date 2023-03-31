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

### 通过url初始化

代码

```java
log.info(String.valueOf(Phone2Region.initialized()));
Phone2Region.initByUrl("https://cdn.jsdelivr.net/gh/ali1416/phone2region@master/data/phone2region.zdat");
log.info(String.valueOf(Phone2Region.initialized()));
log.info(String.valueOf(Phone2Region.parse("18754710000")));
```

结果

```txt
[main] INFO cn.z.phone2region.Phone2Region - false
[main] INFO cn.z.phone2region.Phone2Region - 初始化，URL路径为：https://cdn.jsdelivr.net/gh/ali1416/phone2region@master/data/phone2region.zdat
[main] INFO cn.z.phone2region.Phone2Region - 数据加载成功，版本号为：2302
[main] INFO cn.z.phone2region.Phone2Region - true
[main] INFO cn.z.phone2region.Phone2RegionTest - Region{province='山东', city='济宁', zipCode='272000', areaCode='0537', isp='移动'}
```

### 通过文件初始化

代码

```java
Phone2Region.initByFile("E:/phone2region.zip");
log.info(String.valueOf(Phone2Region.parse("18754710000")));
```

结果

```txt
[main] INFO cn.z.phone2region.Phone2Region - 初始化，文件路径为：E:/phone2region.zip
[main] INFO cn.z.phone2region.Phone2Region - 数据加载成功，版本号为：2302
[main] INFO cn.z.phone2region.Phone2RegionTest - Region{province='山东', city='济宁', zipCode='272000', areaCode='0537', isp='移动'}
```

### 通过inputStream初始化

代码

```java
try {
    Phone2Region.init(new FileInputStream("E:/phone2region.zip"));
} catch (Exception ignore) {
}
log.info(String.valueOf(Phone2Region.parse("18754710000")));
```

结果

```txt
[main] INFO cn.z.phone2region.Phone2Region - 数据加载成功，版本号为：2302
[main] INFO cn.z.phone2region.Phone2RegionTest - Region{province='山东', city='济宁', zipCode='272000', areaCode='0537', isp='移动'}
```

### 初始化多次

代码

```java
Phone2Region.initByFile("E:/phone2region.zip");
Phone2Region.initByFile("E:/phone2region.zip");
log.info(String.valueOf(Phone2Region.parse("18754710000")));
```

结果

```txt
[main] INFO cn.z.phone2region.Phone2Region - 初始化，文件路径为：E:/phone2region.zip
[main] INFO cn.z.phone2region.Phone2Region - 数据加载成功，版本号为：2302
[main] WARN cn.z.phone2region.Phone2Region - 已经初始化过了，不可重复初始化！
[main] INFO cn.z.phone2region.Phone2RegionTest - Region{province='山东', city='济宁', zipCode='272000', areaCode='0537', isp='移动'}
```

### 初始化异常

代码

```java
Phone2Region.initByFile("E:/phone2region");
log.info(String.valueOf(Phone2Region.parse("18754710000")));
```

结果

```txt
[main]  INFO cn.z.phone2region.Phone2Region - 初始化，文件路径为：E:/phone2region
[main] ERROR cn.z.phone2region.Phone2Region - 文件异常！
java.io.FileNotFoundException: E:\phone2region (系统找不到指定的文件。)
[main] ERROR cn.z.phone2region.Phone2Region - 未初始化！
[main]  INFO cn.z.phone2region.Phone2RegionTest - null
```

### 覆盖测试(2302版本497191条数据)

代码

```java
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
```

结果

```txt
[main] INFO cn.z.phone2region.Phone2Region - 初始化，文件路径为：E:/phone2region.zip
[main] INFO cn.z.phone2region.Phone2Region - 数据加载成功，版本号为：2302
[main] INFO cn.z.phone2region.Phone2RegionTest - 查询700000条数据，497191条有效数据，用时322毫秒
```

## 交流

- [x] QQ：`1416978277`
- [x] 微信：`1416978277`
- [x] 支付宝：`1416978277@qq.com`

![交流](https://cdn.jsdelivr.net/gh/ALI1416/ALI1416/image/contact.png)

## 赞助

![赞助](https://cdn.jsdelivr.net/gh/ALI1416/ALI1416/image/donate.png)
