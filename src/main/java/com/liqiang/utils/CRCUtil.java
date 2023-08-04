package com.liqiang.utils;


public class CRCUtil {
    public static short GetCRC_XMODEM(byte[] bytes) {
//        byte[] bytes = ByteUtil.hexToByteArray(str);//16进制字符串转成16进制字符串数组
        int i = CRC16_XMODEM(bytes);//进行CRC—XMODEM校验得到十进制校验数
        String CRC = Integer.toHexString(i);//10进制转16进制
        return (short)i;
//        return CRC;
    }

    //TODO 具体的实现CRC_XMODEM的方法
    public static int CRC16_XMODEM(byte[] buffer) {
        /* StringUtil.getByteArrayByString();*/

        int wCRCin = 0x0000; // initial value 65535
        int wCPoly = 0x1021; // 0001 0000 0010 0001 (0, 5, 12)
        for (byte b : buffer) {
            for (int i = 0; i < 8; i++) {
                boolean bit = ((b >> (7 - i) & 1) == 1);
                boolean c15 = ((wCRCin >> 15 & 1) == 1);
                wCRCin <<= 1;
                if (c15 ^ bit)
                    wCRCin ^= wCPoly;
            }
        }
        wCRCin &= 0xffff;
        return wCRCin ^= 0x0000;
    }


}
