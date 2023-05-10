package com.liqiang.utils;

public class CBCUtil {

    public static short calculateCBCChecksum(byte[] data) {
        int checksum = 0;
        for (byte b : data) {
            checksum ^= (b & 0xFF);
            for (int i = 0; i < 8; i++) {
                if ((checksum & 0x01) != 0) {
                    checksum = (checksum >>> 1) ^ 0x8408;
                } else {
                    checksum >>>= 1;
                }
            }
        }
        return (short) checksum;
    }
}
