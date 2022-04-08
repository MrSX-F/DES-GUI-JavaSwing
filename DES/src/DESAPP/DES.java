package DESAPP;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DES {
    /**
     * 明文中包含中文字符时直接转为二进制进行DES加密解密得到的最终结果可能会出现乱码，
     * 而对于英文字母、数字等ASCII码表中的字符进行同样操作时则不会出现问题，
     * 所以需要先将明文转换为16进制字符串再转换成二进制字符串进行DES加密，DES解密反之即可。
     */

    /**
     * DES加密
     * @param msg 明文消息
     * @param key 明文秘钥
     * @return 16进制密文字符串
     */
    public String encryption(String msg, String key){
        //字符串->16进制字符串->二进制bit数为64整数倍的字符串
        String msgbin = Check64(hex2Bin(str2Hex(msg)));
        String keybin = Check64(hex2Bin(str2Hex(key)));
        //字符串分组，64bit为一组
        String[] msgs = regex(msgbin, ".{64}");
        String k = regex(keybin, ".{64}")[0];
        //生成子密钥并将结果存入到字符串数组中
        String[] keys = generateKey(k);
        //轮循，字符串合并（DES 16轮循环）
        StringBuilder mm = new StringBuilder();
        for (String binmsg : msgs) {
            mm.append(Wheel(binmsg, keys));
        }
        //trim()方法去除字符串的首尾空格，toLowerCase()方法将大写字符转换为小写
        return bin2Hex(mm.toString().trim()).toLowerCase();
    }

    /**
     * DES解密
     * @param cipher 16进制密文
     * @param key 解密/加密 秘钥
     * @return 明文字符串
     */
    public String decryption(String cipher, String key){
        //转换为二进制bit数为64整数倍的字符串
        String msgbin = hex2Bin(cipher);
        key = Check64(hex2Bin(str2Hex(key)));
        //字符串分组，64bit为一组
        String[] cpbin = regex(msgbin, ".{64}");
        String k = regex(key, ".{64}")[0];
        //生成子密钥并将结果存入到字符串数组中
        String[] keys = generateKey(k);
        Collections.reverse(Arrays.asList(keys)); //反转数组
        //轮循，字符串合并（DES 16轮循环）
        StringBuilder mm = new StringBuilder();
        for (String binmsg : cpbin) {
            mm.append(Wheel(binmsg, keys));
        }
        return hex2Str(bin2Hex(mm.toString().trim())).trim();
    }

    // =========进制转换===============
    /**
     * 字符串转十六进制字符串
     *
     * @param str
     * @return HexString
     */
    public  String str2Hex(String str) {
        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder stringBuilder = new StringBuilder();
        byte[] bytes = str.getBytes();
        int bt;
        for (byte b : bytes) {
            bt = (b & 0x0f0) >> 4;
            stringBuilder.append(chars[bt]);
            stringBuilder.append(chars[(b & 0x00f)]);
        }
        return stringBuilder.toString().trim().toLowerCase();
    }

    /**
     * 十六进制字符串转字符串
     *
     * @param hexStr
     * @return String
     */
    public  String hex2Str(String hexStr) {
        String str16 = "0123456789ABCDEF";
        char[] chars = str16.toCharArray();
        byte[] bytes = new byte[hexStr.length() / 2];
        char[] hex = hexStr.toUpperCase().toCharArray();
        int i;
        for (int j = 0; j < bytes.length; j++) {
            i = str16.indexOf(hex[j * 2]) << 4;
            i += str16.indexOf(hex[j * 2 + 1]);
            bytes[j] = (byte) i;
        }
        return new String(bytes);
    }

    /**
     * 十六进制字符串转二进制字符串
     *
     * @param hexStr
     * @return binStr
     */
    public  String hex2Bin(String hexStr) {
        char[] hexchr = hexStr.toLowerCase().toCharArray();
        String binStr = "";
        for (char i : hexchr) {
            String bin = Integer.toBinaryString(Integer.parseInt(String.valueOf(i),16));
            if (bin.length() < 4) {
                bin = String.join("", Collections.nCopies(4 - bin.length(), "0")) + bin;
            }
            binStr += bin;
        }
        return binStr;
    }

    /**
     * 二进制字符串转十六进制字符串
     *
     * @param bin
     * @return hexstr
     */
    public String bin2Hex(String bin){
        char[] chars = "0123456789ABCDEF".toCharArray();
        String[] h4 = regex(bin, ".{4}");
        StringBuilder hexstr = new StringBuilder();
        for (String s : h4) {
            hexstr.append(chars[Integer.parseInt(s,2)]);
        }
        return hexstr.toString().trim();
    }

    /**
     * 检查二进制字符串是否是64位
     * @param binStr 字符串
     * @return str64
     */
    public String Check64(String binStr) {
        //如果不是正好全部每组分为64bit，则不足64bit的组补0
        int num = binStr.length() % 64;
        if (num != 0) {
            binStr += String.join("", Collections.nCopies(64 - num, "0"));
        }
        return binStr;
    }

    /**
     * 正则获取匹配的值
     * @param str 字符串
     * @param regex 正则表达式
     * @return String[]
     */
    public String[] regex(String str,String regex){
        Matcher matcher = Pattern.compile(regex).matcher(str);
        //字符串分组存入列表中
        List<String> list = new ArrayList<>();
        while (matcher.find()){
            list.add(matcher.group(0));
        }
        //将列表中的内容存入到字符串数组中
        String[] strings = new String[list.size()];
        list.toArray(strings);
        return  strings;
    }

    /**
     * 0 1 的异或运算
     * @param left 二进制字符串
     * @param right 二进制字符串
     * @return xor 异或运算结果
     */
    public String xor(String left,String right){
        StringBuilder str = new StringBuilder();
        char[] leftchr = left.toCharArray();
        char[] rightchr = right.toCharArray();
        for (int i = 0; i < leftchr.length; i++) {
            str.append(leftchr[i] ^rightchr[i]);
        }
        return str.toString().trim();
    }

    /**
     * F轮函数的实现
     * @param bin32 64位明文的右边32位
     * @param key  当前轮的加密秘钥
     * @return Swap(trim,p) 最后经过P置换得到的字符串
     */
    public String f_function(String bin32,String key){
        //E扩展置换，将32位的输入扩展为48位的输出
        bin32 = Swap(bin32,E);
        //经过E扩展置换得到的二进制字符串再与子密钥进行异或运算
        String xor = xor(bin32, key);
        //运算结果分为每组6bit进入S盒6->4压缩
        String[] slist = regex(xor, ".{6}");
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < slist.length; i++) {
            String s = slist[i];
            int h = Integer.parseInt(s.substring(0,1) + s.substring(5),2);
            int l = Integer.parseInt(s.substring(1,5) ,2);
            String i1 = Integer.toBinaryString(S[i][h * 16 + l]);
            if (i1.length()<4){
                i1 = String.join("",Collections.nCopies(4-i1.length(),"0"))+i1;
            }//将运算后的结果拼接起来，循环结束后得到32位的输出
            builder.append(i1);
        }
        //将得到的32位二进制字符串进行P盒置换
        String trim = builder.toString().trim();
        return Swap(trim,P);
    }

    /**
     * 轮函数
     * @param bin64 64位明文二进制
     * @param keys 16组密钥
     * @return
     */
    public String Wheel(String bin64,String[] keys){
        bin64 = Swap(bin64,IP_table); // 初始置换
        //将明文拆分成各为32bit的两部分
        String leftbin = bin64.substring(0,32);
        String rightbin = bin64.substring(32);
        //从keys中读取子密钥进行16轮迭代运算
        for (String key : keys) {
            String temp = rightbin;
            String f_function = f_function(rightbin, key); //F轮函数
            //F轮函数结果与之前的leftbin进行异或运算得到之后的rightbin
            rightbin = xor(leftbin, f_function);
            //之后的leftbin = 之前的rightbin
            leftbin = temp;
        }//16轮迭代运算完成后将运算完的rightbin和leftbin连接起来再进行初始置换的逆置换得到密文
        return Swap(rightbin+leftbin,IP_re_table);
    }

    /**
     * 生成16组秘钥
     * @param binKey 初始秘钥的二进制
     * @return keys[]
     */
    public String[] generateKey(String binKey) {
        List<String> list = new ArrayList<>();
        String leftbin, rightbin;
        //经过PC-1置换去除奇偶校验位
        binKey = Swap(binKey,PC_1);
        //将得到的56bit密钥分成各为28bit的两部分
        leftbin = binKey.substring(0,28);
        rightbin = binKey.substring(28,56);
        //leftbin和rightbin根据移位次数表进行移位
        for (int i : SHIFT) {
            leftbin = leftbin.substring(i) + leftbin.substring(0,i);
            rightbin = rightbin.substring(i) + rightbin.substring(0,i);
            //leftbin与rightbin拼接后经过PC-2置换得到子密钥
            list.add(Swap(leftbin+rightbin,PC_2));
        }//将列表内容放到字符串数组中
        return  list.toArray(new String[list.size()]);
    }

    /**
     * 置换运算
     * @param swap 待置换的字符串
     * @param table 置换表
     * @return 置换后的字符串
     */
    public static String Swap(String swap, int[] table) {
        char[] array = swap.toCharArray();
        StringBuilder string = new StringBuilder();
        for (int i = 0; i < table.length; i++) {
            string.append(array[table[i]-1]);
        }
        return string.toString().trim();
    }

    //==============置换表与移位表===============
    //PC-1置换表
    public static final int[] PC_1 = {57, 49, 41, 33, 25, 17, 9,
            1, 58, 50, 42, 34, 26, 18,
            10, 2, 59, 51, 43, 35, 27,
            19, 11, 3, 60, 52, 44, 36,
            63, 55, 47, 39, 31, 23, 15,
            7, 62, 54, 46, 38, 30, 22,
            14, 6, 61, 53, 45, 37, 29,
            21, 13, 5, 28, 20, 12, 4};
    //PC-2置换表
    public static final int[] PC_2 = {14, 17, 11, 24, 1, 5, 3, 28,
            15, 6, 21, 10, 23, 19, 12, 4,
            26, 8, 16, 7, 27, 20, 13, 2,
            41, 52, 31, 37, 47, 55, 30, 40,
            51, 45, 33, 48, 44, 49, 39, 56,
            34, 53, 46, 42, 50, 36, 29, 32};
    //左移移位表
    public static final int[] SHIFT = {1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1};
    //S盒
    public static final int[][] S = {
            //S1
            {14, 4, 13, 1, 2, 15, 11, 8, 3, 10, 6, 12, 5, 9, 0, 7,
                    0, 15, 7, 4, 14, 2, 13, 1, 10, 6, 12, 11, 9, 5, 3, 8,
                    4, 1, 14, 8, 13, 6, 2, 11, 15, 12, 9, 7, 3, 10, 5, 0,
                    15, 12, 8, 2, 4, 9, 1, 7, 5, 11, 3, 14, 10, 0, 6, 13},
            //S2
            {15, 1, 8, 14, 6, 11, 3, 4, 9, 7, 2, 13, 12, 0, 5, 10,
                    3, 13, 4, 7, 15, 2, 8, 14, 12, 0, 1, 10, 6, 9, 11, 5,
                    0, 14, 7, 11, 10, 4, 13, 1, 5, 8, 12, 6, 9, 3, 2, 15,
                    13, 8, 10, 1, 3, 15, 4, 2, 11, 6, 7, 12, 0, 5, 14, 9},
            //S3
            {10, 0, 9, 14, 6, 3, 15, 5, 1, 13, 12, 7, 11, 4, 2, 8,
                    13, 7, 0, 9, 3, 4, 6, 10, 2, 8, 5, 14, 12, 11, 15, 1,
                    13, 6, 4, 9, 8, 15, 3, 0, 11, 1, 2, 12, 5, 10, 14, 7,
                    1, 10, 13, 0, 6, 9, 8, 7, 4, 15, 14, 3, 11, 5, 2, 12},
            //S4
            {7, 13, 14, 3, 0, 6, 9, 10, 1, 2, 8, 5, 11, 12, 4, 15,
                    13, 8, 11, 5, 6, 15, 0, 3, 4, 7, 2, 12, 1, 10, 14, 9,
                    10, 6, 9, 0, 12, 11, 7, 13, 15, 1, 3, 14, 5, 2, 8, 4,
                    3, 15, 0, 6, 10, 1, 13, 8, 9, 4, 5, 11, 12, 7, 2, 14},
            //S5
            {2, 12, 4, 1, 7, 10, 11, 6, 8, 5, 3, 15, 13, 0, 14, 9,
                    14, 11, 2, 12, 4, 7, 13, 1, 5, 0, 15, 10, 3, 9, 8, 6,
                    4, 2, 1, 11, 10, 13, 7, 8, 15, 9, 12, 5, 6, 3, 0, 14,
                    11, 8, 12, 7, 1, 14, 2, 13, 6, 15, 0, 9, 10, 4, 5, 3},
            //S6
            {12, 1, 10, 15, 9, 2, 6, 8, 0, 13, 3, 4, 14, 7, 5, 11,
                    10, 15, 4, 2, 7, 12, 9, 5, 6, 1, 13, 14, 0, 11, 3, 8,
                    9, 14, 15, 5, 2, 8, 12, 3, 7, 0, 4, 10, 1, 13, 11, 6,
                    4, 3, 2, 12, 9, 5, 15, 10, 11, 14, 1, 7, 6, 0, 8, 13},
            //S7
            {4, 11, 2, 14, 15, 0, 8, 13, 3, 12, 9, 7, 5, 10, 6, 1,
                    13, 0, 11, 7, 4, 9, 1, 10, 14, 3, 5, 12, 2, 15, 8, 6,
                    1, 4, 11, 13, 12, 3, 7, 14, 10, 15, 6, 8, 0, 5, 9, 2,
                    6, 11, 13, 8, 1, 4, 10, 7, 9, 5, 0, 15, 14, 2, 3, 12},
            //S8
            {13, 2, 8, 4, 6, 15, 11, 1, 10, 9, 3, 14, 5, 0, 12, 7,
                    1, 15, 13, 8, 10, 3, 7, 4, 12, 5, 6, 11, 0, 14, 9, 2,
                    7, 11, 4, 1, 9, 12, 14, 2, 0, 6, 10, 13, 15, 3, 5, 8,
                    2, 1, 14, 7, 4, 10, 8, 13, 15, 12, 9, 0, 3, 5, 6, 11}
    };
    //P盒置换表
    public static final int[] P = {
            16, 7, 20, 21, 29, 12, 28, 17,
            1, 15, 23, 26, 5, 18, 31, 10,
            2, 8, 24, 14, 32, 27, 3, 9,
            19, 13, 30, 6, 22, 11, 4, 25};
    //E扩展置换表
    public static final int[] E = {
            32, 1, 2, 3, 4, 5, 4, 5,
            6, 7, 8, 9, 8, 9, 10, 11,
            12, 13, 12, 13, 14, 15, 16, 17,
            16, 17, 18, 19, 20, 21, 20, 21,
            22, 23, 24, 25, 24, 25, 26, 27,
            28, 29, 28, 29, 30, 31, 32, 1};
    //初始置换表
    public static final int[] IP_table = {
            58, 50, 42, 34, 26, 18, 10, 2,
            60, 52, 44, 36, 28, 20, 12, 4,
            62, 54, 46, 38, 30, 22, 14, 6,
            64, 56, 48, 40, 32, 24, 16, 8,
            57, 49, 41, 33, 25, 17, 9, 1,
            59, 51, 43, 35, 27, 19, 11, 3,
            61, 53, 45, 37, 29, 21, 13, 5,
            63, 55, 47, 39, 31, 23, 15, 7};
    //初始置换逆置换表
    public static final int[] IP_re_table = {
            40, 8, 48, 16, 56, 24, 64, 32, 39,
            7, 47, 15, 55, 23, 63, 31, 38, 6,
            46, 14, 54, 22, 62, 30, 37, 5, 45,
            13, 53, 21, 61, 29, 36, 4, 44, 12,
            52, 20, 60, 28, 35, 3, 43, 11, 51,
            19, 59, 27, 34, 2, 42, 10, 50, 18,
            58, 26, 33, 1, 41, 9, 49, 17, 57, 25};
}
