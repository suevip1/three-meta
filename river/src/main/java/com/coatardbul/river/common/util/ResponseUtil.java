package com.coatardbul.river.common.util;

import com.coatardbul.river.model.feign.ResponseDto;
import com.coatardbul.river.model.feign.ResponseHeadDro;

public class ResponseUtil {


    public static <T> ResponseDto setBody(T body) {
        ResponseDto responseDto = new ResponseDto();
        ResponseHeadDro responseHeadDro = new ResponseHeadDro();
        responseDto.setBody(body);
        responseDto.setHead(responseHeadDro);
        return responseDto;
    }
}
