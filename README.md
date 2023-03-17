# Phone Number To Region 手机号码转区域

[![License](https://img.shields.io/github/license/ali1416/phone2region)](https://opensource.org/licenses/BSD-3-Clause)
[![Maven Central](https://img.shields.io/maven-central/v/cn.404z/phone2region)](https://mvnrepository.com/artifact/cn.404z/phone2region)
[![Tag](https://img.shields.io/github/v/tag/ali1416/phone2region)](https://github.com/ALI1416/phone2region/tags)
[![Repo Size](https://img.shields.io/github/repo-size/ali1416/phone2region?color=success)](https://github.com/ALI1416/phone2region)
[![phone2region.txt](https://img.shields.io/github/size/ali1416/phone2region-test/data/phone2region.txt?label=phone2region.txt&color=success)](https://github.com/ALI1416/phone2region-test/raw/master/data/phone2region.txt)
[![phone2region.zdat](https://img.shields.io/github/size/ali1416/phone2region-test/data/phone2region.zdat?label=phone2region.zdat&color=success)](https://github.com/ALI1416/phone2region-test/raw/master/data/phone2region.zdat)

[Github源码](https://github.com/ALI1416/phone2region)
[Gitee源码](https://gitee.com/ALI1416/phone2region)
[![Java CI with Maven](https://github.com/ALI1416/phone2region/actions/workflows/maven.yml/badge.svg)](https://github.com/ALI1416/phone2region/actions/workflows/maven.yml)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=ALI1416_phone2region&metric=reliability_rating)
![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=ALI1416_phone2region&metric=sqale_rating)
![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=ALI1416_phone2region&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=ALI1416_phone2region)

[Github测试](https://github.com/ALI1416/phone2region-test)
[Gitee测试](https://gitee.com/ALI1416/phone2region-test)
[![Java CI with Maven](https://github.com/ALI1416/phone2region-test/actions/workflows/maven.yml/badge.svg)](https://github.com/ALI1416/phone2region-test/actions/workflows/maven.yml)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=ALI1416_phone2region-test&metric=reliability_rating)
![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=ALI1416_phone2region-test&metric=sqale_rating)
![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=ALI1416_phone2region-test&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=ALI1416_phone2region-test)

SpringBoot自动配置项目地址

[Github源码](https://github.com/ALI1416/phone2region-spring-boot-autoconfigure)
[Gitee源码](https://gitee.com/ALI1416/phone2region-spring-boot-autoconfigure)
[![Java CI with Maven](https://github.com/ALI1416/phone2region-spring-boot-autoconfigure/actions/workflows/maven.yml/badge.svg)](https://github.com/ALI1416/phone2region-spring-boot-autoconfigure/actions/workflows/maven.yml)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=ALI1416_phone2region-spring-boot-autoconfigure&metric=reliability_rating)
![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=ALI1416_phone2region-spring-boot-autoconfigure&metric=sqale_rating)
![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=ALI1416_phone2region-spring-boot-autoconfigure&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=ALI1416_phone2region-spring-boot-autoconfigure)

[Github测试](https://github.com/ALI1416/phone2region-spring-boot-autoconfigure-test)
[Gitee测试](https://gitee.com/ALI1416/phone2region-spring-boot-autoconfigure-test)
[![Java CI with Maven](https://github.com/ALI1416/phone2region-spring-boot-autoconfigure-test/actions/workflows/maven.yml/badge.svg)](https://github.com/ALI1416/phone2region-spring-boot-autoconfigure-test/actions/workflows/maven.yml)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=ALI1416_phone2region-spring-boot-autoconfigure-test&metric=reliability_rating)
![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=ALI1416_phone2region-spring-boot-autoconfigure-test&metric=sqale_rating)
![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=ALI1416_phone2region-spring-boot-autoconfigure-test&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=ALI1416_phone2region-spring-boot-autoconfigure-test)

## 简介

手机号码归属地查询

参考项目：<https://github.com/EeeMt/phone-number-geo>

## 数据文件

数据文件来源：<https://github.com/xluohome/phonedata>

数据文件下载：<https://github.com/xluohome/phonedata/blob/master/phone.dat>

本项目所使用的数据文件：[点击查看](./data)

### 生成本项目所使用的数据文件

<https://github.com/ALI1416/phone2region-test/blob/master/src/main/java/com/demo/DatGeneration.java>

### dat文件转txt文件

<https://github.com/ALI1416/phone2region-test/blob/master/src/main/java/com/demo/dat/Dat2Txt.java>

### txt文件转dat文件

<https://github.com/ALI1416/phone2region-test/blob/master/src/main/java/com/demo/dat/Txt2Dat.java>

## 依赖导入

```xml
<!-- 必须依赖 -->
<dependency>
  <groupId>cn.404z</groupId>
  <artifactId>phone2region</artifactId>
  <version>1.0.0</version>
</dependency>
<!-- 额外依赖(运行未报错，不需要加) -->
<dependency>
  <groupId>ch.qos.logback</groupId>
  <artifactId>logback-classic</artifactId>
  <version>1.4.5</version>
</dependency>
```

## 使用方法

### 通过url初始化

代码

```java
Phone2Region.initByUrl("https://cdn.jsdelivr.net/gh/ali1416/phone2region-test/data/phone2region.zxdb");
log.info(String.valueOf(Phone2Region.parse("18754710000")));
```

结果

```txt
[main] INFO cn.z.phone2region.Phone2Region - 初始化，URL路径为：https://cdn.jsdelivr.net/gh/ali1416/phone2region-test/data/phone2region.zxdb
[main] INFO cn.z.phone2region.Phone2Region - 数据加载成功，版本号为：2302
[main] INFO com.demo.Phone2RegionTest - Region{province='山东', city='济宁', zipCode='272000', areaCode='0537', isp='移动'}
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
[main] INFO com.demo.Phone2RegionTest - Region{province='山东', city='济宁', zipCode='272000', areaCode='0537', isp='移动'}
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
[main] INFO com.demo.Phone2RegionTest - Region{province='山东', city='济宁', zipCode='272000', areaCode='0537', isp='移动'}
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
[main] INFO com.demo.Phone2RegionTest - Region{province='山东', city='济宁', zipCode='272000', areaCode='0537', isp='移动'}
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
[main]  INFO com.demo.Phone2RegionTest - null
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
[main] INFO com.demo.Phone2RegionTest - 查询700000条数据，497191条有效数据，用时322毫秒
```

## 交流

QQ：1416978277  
微信：1416978277  
支付宝：1416978277@qq.com  
![交流](https://cdn.jsdelivr.net/gh/ALI1416/ALI1416/image/contact.png)

## 赞助

![赞助](https://cdn.jsdelivr.net/gh/ALI1416/ALI1416/image/donate.png)
