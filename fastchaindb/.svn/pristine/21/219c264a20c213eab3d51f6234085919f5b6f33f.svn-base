package com.oschain.fastchaindb.common;

import com.oschain.fastchaindb.common.constants.GlobalConsts;
import com.oschain.fastchaindb.common.utils.GsonUtil;

public class ResultDTO<T> {
    private static final long serialVersionUID = 1L;
    private int code;
    private String msg="ok";
    private T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    //private Object data;


    public ResultDTO()
    {
        super();
        this.code = GlobalConsts.RT_OK;
    }
    public ResultDTO(T data) {
        super();
        this.code = GlobalConsts.RT_OK;
        this.data = data;
    }
    public ResultDTO(int code, String msg)
    {
        super();
        this.code = code;
        this.msg = msg;
    }
    public ResultDTO(int code, T data, String msg) {
        super();
        this.code = code;
        this.data = data;
        this.msg = msg;
    }

//    public Object getData() {
//        return data;
//    }
//
//    public void setData(Object data) {
//        this.data = data;
//    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static void main(String[] args) {

        String str="{\"msg\":\"交易成功\",\"code\":200,\"data\":\"1->2\"}";
        ResultDTO<String> resultDto=new ResultDTO<String>();
        resultDto =  GsonUtil.gsonToBean(str,ResultDTO.class);
        System.out.println(resultDto.data);
    }
}
