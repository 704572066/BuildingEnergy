package com.liqiang.utils;

public class ByteUtil {

    public static short reverseBytes(short value) {
        return (short) (((value & 0xFF00) >>> 8) | ((value & 0x00FF) << 8));
    }

    public static int reverseBytes(int value) {
        int byte1 = (value >> 8) & 0xFF;
        int byte2 = value & 0xFF;
        int byte3 = (value >> 24) & 0xFF;
        int byte4 = (value >> 16) & 0xFF;

        return (byte1 << 24) | (byte2 << 16) | (byte3 << 8) | byte4;
    }

    /*小端，低字节在后*/
    public static int bytesToIntLittleEndian(byte[] bytes) {
        // byte数组中序号小的在右边
        return bytes[0] & 0xFF | //
                (bytes[1] & 0xFF) << 8 | //
                (bytes[2] & 0xFF) << 16 | //
                (bytes[3] & 0xFF) << 24; //
    }

    /*大端，高字节在后*/
    public static int bytesToIntBigEndian(byte[] bytes) {
        // byte数组中序号大的在右边
        return bytes[3] & 0xFF | //
                (bytes[2] & 0xFF) << 8 | //
                (bytes[1] & 0xFF) << 16 | //
                (bytes[0] & 0xFF) << 24; //
    }

    /*大端，高字节在后*/
    public static byte[] intToBytesBigEndian(int intValue) {
        // byte数组中序号大的在右边
        // 右边的数据放到byte数组中序号大的位置
        byte[] bytes = new byte[Integer.BYTES];
        bytes[3] = (byte) (intValue & 0xff);
        bytes[2] = (byte) ((intValue >> Byte.SIZE) & 0xff);
        bytes[1] = (byte) ((intValue >> Byte.SIZE * 2) & 0xff);
        bytes[0] = (byte) ((intValue >> Byte.SIZE * 3) & 0xff);
        return bytes;
    }

    //System.arraycopy()方法
    public static byte[] byteMerger(byte[] bt1, byte[] bt2){
        byte[] bt3 = new byte[bt1.length+bt2.length];
        System.arraycopy(bt1, 0, bt3, 0, bt1.length);
        System.arraycopy(bt2, 0, bt3, bt1.length, bt2.length);
        return bt3;
    }

}
