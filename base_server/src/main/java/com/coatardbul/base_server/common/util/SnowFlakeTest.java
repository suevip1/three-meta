package com.coatardbul.base_server.common.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author zrj
 * @date 2021/2/15
 * @since V1.0
 **/
public class SnowFlakeTest {

    public static void main(String[] args) {
        System.out.println( "雪花算法时间" );

        System.out.println( "11111111111111111111111111111111111111111".length() );
        long time = 2199023255551L;

        Date date = new Date();
        date.setTime( time );

        //2039-09-07
        System.out.println( new SimpleDateFormat( "yyyy-MM-dd" ).format( date ) );
    }
}
