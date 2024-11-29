package cn.z.phone2region;

/**
 * <h1>Phone2Region异常</h1>
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
     * @param message 详细信息
     */
    public Phone2RegionException(String message) {
        super(message);
    }

    /**
     * Phone2Region异常
     *
     * @param message 详细信息
     * @param cause   原因
     */
    public Phone2RegionException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Phone2Region异常
     *
     * @param cause 原因
     */
    public Phone2RegionException(Throwable cause) {
        super(cause);
    }

    /**
     * Phone2Region异常
     *
     * @param message            详细信息
     * @param cause              原因
     * @param enableSuppression  是否启用抑制
     * @param writableStackTrace 堆栈跟踪是否为可写的
     */
    protected Phone2RegionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
