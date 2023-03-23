package cn.z.phone2region;

/**
 * <h1>Phone2Region异常类</h1>
 *
 * <p>
 * createDate 2023/03/23 09:53:07
 * </p>
 *
 * @author ALI[ali-k@foxmail.com]
 * @since 1.1.0
 **/
public class Phone2RegionException extends RuntimeException {

    /**
     * Phone2Region异常
     */
    public Phone2RegionException() {
        super();
    }

    /**
     * Phone2Region异常
     *
     * @param message 信息
     */
    public Phone2RegionException(String message) {
        super(message);
    }

}
