# 数据文件

## 文件列表

### 版本：`v2.0.0`

- 文件名：`phone2region.zdb`
  - 版本号：`20230225`
  - 校验码：`C8AEEA0A`
  - 点击下载：[![phone2region.zdb](https://img.shields.io/github/size/ALI1416/phone2region/data/phone2region.zdb?label=phone2region.zdb&color=success&branch=v2.0.0)](https://www.404z.cn/files/phone2region/v2.0.0/data/phone2region.zdb)
  - 链接地址：`https://www.404z.cn/files/phone2region/v2.0.0/data/phone2region.zdb`
  - 原始地址：`https://github.com/ALI1416/phone2region/blob/v2.0.0/data/phone2region.zdb`
- 文件名：`phone2region.txt`
  - 点击下载：[![phone2region.txt](https://img.shields.io/github/size/ALI1416/phone2region/data/phone2region.txt?label=phone2region.txt&color=success&branch=v2.0.0)](https://www.404z.cn/files/phone2region/v2.0.0/data/phone2region.txt)
  - 链接地址：`https://www.404z.cn/files/phone2region/v2.0.0/data/phone2region.txt`
  - 原始地址：`https://github.com/ALI1416/phone2region/blob/v2.0.0/data/phone2region.txt`

## 数据来源

- 数据文件来源：<https://github.com/xluohome/phonedata>
- 查看最新版本：<https://github.com/xluohome/phonedata/commits/master/phone.dat>
- 数据文件下载：<https://cdn.jsdelivr.net/gh/xluohome/phonedata@master/phone.dat>
- 数据文件生成方法java程序：[点击查看](../src/test/java/cn/z/phone2region/DataGenerationTest.java)

## 数据文件设计

### 整体结构

| 中文名   | 头部区 | 记录区     | 二级索引区 | 索引区   |
| -------- | ------ | ---------- | ---------- | -------- |
| 英文名   | header | record     | vector2    | vector   |
| 长度     | 不定长 | 不定长     | 定长       | 不定长   |
| 数据类型 | 不限   | 不定长数组 | 定长数组   | 定长数组 |

### 头部区

| 中文名   | CRC32校验和 | 版本号  | 记录区指针    | 二级索引区指针 | 索引区指针    | 拓展 |
| -------- | ----------- | ------- | ------------- | -------------- | ------------- | ---- |
| 英文名   | crc32       | version | recordAreaPtr | vector2AreaPtr | vectorAreaPtr | ...  |
| 长度     | 4           | 4       | 4             | 4              | 4             | ...  |
| 数据类型 | int         | int     | int           | int            | int           | ...  |

- `头部区`可以进行拓展
- `CRC32校验和`是除去前`4`字节，对剩下的所有数据进行校验

### 记录区

每条记录格式如下：

| 中文名   | 记录值长度        | 记录值      |
| -------- | ----------------- | ----------- |
| 英文名   | recordValueLength | recordValue |
| 长度     | 1                 | 不定长      |
| 数据类型 | byte              | utf8        |

- `记录值长度`不包括自己所占的`1`字节
- `记录值`最大长度为`255`字节

### 二级索引区

每条二级索引格式如下(`700000>>8=2734+1+1`块)：

| 中文名   | 索引指针  |
| -------- | --------- |
| 英文名   | vectorPtr |
| 长度     | 4         |
| 数据类型 | int       |

- 附加一块的值为`文件总字节数`

### 索引区

每条索引格式如下：

| 中文名   | 号码   | 记录指针  |
| -------- | ------ | --------- |
| 英文名   | number | recordPtr |
| 长度     | 1      | 4         |
| 数据类型 | byte   | int       |

- `号码`为`手机号码前7位-1300000`的后8bit

## 参考

- [ALI1416/ip2region](https://github.com/ALI1416/ip2region/tree/master/data)
