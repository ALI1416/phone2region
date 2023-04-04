package cn.z.phone2region;

/**
 * <h1>区域</h1>
 *
 * <p>
 * createDate 2023/03/15 17:22:49
 * </p>
 *
 * @author ALI[ali-k@foxmail.com]
 * @since 1.0.0
 **/
public class Region {

    /**
     * 省份
     */
    private String province;
    /**
     * 城市
     */
    private String city;
    /**
     * 邮编
     */
    private String zipCode;
    /**
     * 区号
     */
    private String areaCode;
    /**
     * isp
     */
    private String isp;

    /**
     * 构造函数
     */
    public Region() {
    }

    /**
     * 构造函数
     */
    public Region(String region) {
        // 省份|城市|邮编|区号|ISP
        String[] s = region.split("\\|", -1);
        if (s.length == 5) {
            this.province = s[0];
            this.city = s[1];
            this.zipCode = s[2];
            this.areaCode = s[3];
            this.isp = s[4];
        }
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getIsp() {
        return isp;
    }

    public void setIsp(String isp) {
        this.isp = isp;
    }

    @Override
    public String toString() {
        return "Region{" + "province='" + province + '\'' + ", city='" + city + '\'' + ", zipCode='" + zipCode + '\'' + ", areaCode='" + areaCode + '\'' + ", isp='" + isp + '\'' + '}';
    }

}
