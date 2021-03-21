package com.oschain.fastchaindb.client.dto;

import com.oschain.fastchaindb.client.constants.GlobalConsts;

public class ResultDTO<T> {
    private static final long serialVersionUID = 1L;
    private int code;
    private String msg="ok";
    private T data;


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

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

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

//        String str="{\"msg\":\"交易成功\",\"code\":200,\"data\":\"1->2\"}";
//        ResultDto<String> resultDto=new ResultDto<String>();
//        resultDto =  GsonUtil.gsonToBean(str,ResultDto.class);
//        System.out.println(resultDto.code);
    }
}
