package com.coatardbul.baseCommon.exception;

public class LoginException extends RuntimeException{

    public LoginException(String str) {
        super(str);
    }

    public LoginException(String code, Exception e){
        super(code,e);
    }
}
