package com.oschain.fastchaindb.common;

public class GlobalConsts {

    /**
     * 系统用户名称，输出log时使用
     */
    public static final String SYSTEM_NAME = "system";

    /**
     * 是否
     */
    public interface YesOrNo {
        /** 否 */
        Integer NO = 0;
        /** 是 */
        Integer YES = 1;
        /** 处理中 */
        Integer CENTER = 2;
    }

}
