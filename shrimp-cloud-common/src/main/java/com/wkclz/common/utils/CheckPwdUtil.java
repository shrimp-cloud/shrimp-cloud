package com.wkclz.common.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Description:
 * Created: wangkaicun @ 2018-01-19 下午2:59
 */
public class CheckPwdUtil {

    private String psw;
    //密码长度
    private int length;
    //大写字母长度
    private int upperAlp = 0;
    //小写字母长度
    private int lowerAlp = 0;
    //数字长度
    private int num = 0;
    //特殊字符长度
    private int charlen = 0;

    public static final String PWD_IS_WEEK_WARM = "密码太弱，请保证密码长度，并添加多种字符元素！";

    private static final Pattern PATTERN_A2Z_UPPER = Pattern.compile("[A-Z]");
    private static final Pattern PATTERN_A2Z_LOWWER = Pattern.compile("[a-z]");
    private static final Pattern PATTERN_0_TO_9 = Pattern.compile("[0-9]");


    public CheckPwdUtil(String psw) {
        this.psw = psw.replaceAll("\\s", "");
        this.length = psw.length();
    }

    //密码长度积分
    private int checkPwdLength() {
        return this.length * 4;
    }

    //大写字母积分
    private int checkPwdUpper() {
        Matcher matcher = PATTERN_A2Z_UPPER.matcher(psw);
        int j = 0;
        while (matcher.find()) {
            j++;
        }
        this.upperAlp = j;
        if (j <= 0) {
            return 0;
        }
        return (this.length - j) * 2;
    }

    //测试小写字母字元
    private int checkPwsLower() {
        Matcher matcher = PATTERN_A2Z_LOWWER.matcher(this.psw);
        int j = 0;
        while (matcher.find()) {
            j++;
        }
        this.lowerAlp = j;
        if (j <= 0) {
            return 0;
        }
        return (this.length - j) * 2;
    }

    //测试数字字元
    private int checkNum() {
        Matcher matcher = PATTERN_0_TO_9.matcher(this.psw);
        int j = 0;
        while (matcher.find()) {
            j++;
        }
        this.num = j;
        if (this.num == this.length) {
            return 0;
        }
        return j * 4;
    }

    //测试符号字元
    private int checkChar() {
        charlen = this.length - this.upperAlp
            - this.lowerAlp - this.num;
        return this.charlen * 6;
    }

    //密碼中間穿插數字或符號字元
    private int checkNumOrCharInStr() {
        int j = this.num + this.charlen - 1;
        if (j < 0) {
            j = 0;
        }
        if (this.num + this.charlen == this.length) {
            j = this.length - 2;
        }
        return j * 2;
    }

    /**
     * 最低要求标准
     * 该方法需要在以上加分方法使用后才可以使用
     *
     * @return
     */
    private int lowerQuest() {
        int j = 0;
        if (this.length >= 8) {
            j++;
        }
        if (this.upperAlp > 0) {
            j++;
        }
        if (this.lowerAlp > 0) {
            j++;
        }
        if (this.num > 0) {
            j++;
        }
        if (this.charlen > 0) {
            j++;
        }
        if (j < 4) {
            j = 0;
        }
        return j * 2;
    }

    /**
     * =================分割线===扣分项目=====================
     **/
    //只包含英文字母
    private int onlyHasAlp() {
        if (this.length == (this.upperAlp + this.lowerAlp)) {
            return -this.length;
        }
        return 0;
    }

    //只包含数字
    private int onlyHasNum() {
        if (this.length == this.num) {
            return -this.length;
        }
        return 0;
    }

    //重复字元扣分
    private int repeatDex() {
        char[] c = this.psw.toLowerCase().toCharArray();
        HashMap<Character, Integer> hashMap =
            new HashMap<Character, Integer>();
        for (int i = 0; i < c.length; i++) {
            if (hashMap.containsKey(c[i])) {
                hashMap.put(c[i], hashMap.get(c[i]) + 1);
            } else {
                hashMap.put(c[i], 1);
            }
        }
        int sum = 0;
        Iterator<Map.Entry<Character, Integer>> iterator =
            hashMap.entrySet().iterator();
        while (iterator.hasNext()) {
            int j = iterator.next().getValue();
            if (j > 0) {
                sum = sum + j * (j - 1);
            }
        }
        return -sum;
    }

    //连续英文大写字元
    private int seriseUpperAlp() {
        int j = 0;
        char[] c = this.psw.toCharArray();
        for (int i = 0; i < c.length - 1; i++) {
            if (PATTERN_A2Z_UPPER.matcher(c[i] + "").find()) {
                if (PATTERN_A2Z_UPPER.matcher(c[i + 1] + "").find()) {
                    j++;
                }
            }
        }
        return -2 * j;
    }

    //连续英文小写字元
    private int seriseLowerAlp() {
        int j = 0;
        char[] c = this.psw.toCharArray();
        for (int i = 0; i < c.length - 1; i++) {
            if (PATTERN_A2Z_LOWWER.matcher(c[i] + "").find()
                && c[i] + 1 == c[i + 1]) {
                j++;
            }
        }
        return -2 * j;
    }

    //连续数字字元
    private int seriseNum() {
        char[] c = this.psw.toCharArray();
        int j = 0;
        for (int i = 0; i < c.length - 1; i++) {
            if (PATTERN_0_TO_9.matcher(c[i] + "").matches()
                && PATTERN_0_TO_9.matcher(c[i + 1] + "").matches()) {
                j++;
            }
        }
        return -2 * j;
    }

    //连续字母abc def之类超过3个扣分  不区分大小写字母
    private int seriesAlp2Three() {
        int j = 0;
        char[] c = this.psw.toLowerCase(Locale.CHINA).toCharArray();
        for (int i = 0; i < c.length - 2; i++) {
            if (PATTERN_A2Z_LOWWER.matcher(c[i] + "").find()) {
                if ((c[i + 1] == c[i] + 1) && (c[i + 2] == c[i] + 2)) {
                    j++;
                }
            }
        }
        return -3 * j;
    }

    //连续数字123 234之类超过3个扣分
    private int seriesNum2Three() {
        int j = 0;
        char[] c = this.psw.toLowerCase(Locale.CHINA).toCharArray();
        for (int i = 0; i < c.length - 2; i++) {
            if (PATTERN_0_TO_9.matcher(c[i] + "").find()) {
                if ((c[i + 1] == c[i] + 1) && (c[i + 2] == c[i] + 2)) {
                    j++;
                }
            }
        }
        return -3 * j;
    }

    private int jiafen() {
        System.out.println("密碼字數=" + checkPwdLength());
        System.out.println("大寫英文字元=" + checkPwdUpper());
        System.out.println("小寫英文字元=" + checkPwsLower());
        System.out.println("數字字元=" + checkNum());
        System.out.println("符號字元=" + checkChar());
        System.out.println("密碼中間穿插數字或符號字元=" + checkNumOrCharInStr());
        System.out.println("已達密碼最低要求項目=" + lowerQuest());
        return 0;
    }

    private int jianfen() {
        System.out.println("只有英文字元=" + onlyHasAlp());
        System.out.println("只有數字字元=" + onlyHasNum());
        System.out.println("重複字元 (Case Insensitive)=" + repeatDex());
        System.out.println("連續英文大寫字元=" + seriseUpperAlp());
        System.out.println("連續英文小寫字元=" + seriseLowerAlp());
        System.out.println("連續數字字元=" + seriseNum());
        System.out.println("連續字母超過三個(如abc,def)=" + seriesAlp2Three());
        System.out.println("連續數字超過三個(如123,234)=" + seriesNum2Three());
        return 0;
    }


    /**
     * 高强度密码效验
     *
     * @param pwdStr
     * @return
     */
    public static int checkPwdTopLevel(String pwdStr) {
        CheckPwdUtil pwd = new CheckPwdUtil(pwdStr);
        int score = 0;

        // 加分
        score += pwd.checkPwdLength();
        score += pwd.checkPwdUpper();
        score += pwd.checkPwsLower();
        score += pwd.checkNum();
        score += pwd.checkChar();
        score += pwd.checkNumOrCharInStr();

        // 加分项不满足，归0分
        if (pwd.lowerQuest() == 0) {
            return 0;
        }

        // 减分
        score += pwd.onlyHasAlp();
        score += pwd.onlyHasNum();
        score += pwd.repeatDex();
        score += pwd.seriseUpperAlp();
        score += pwd.seriseLowerAlp();
        score += pwd.seriseNum();
        score += pwd.seriesAlp2Three();
        score += pwd.seriesNum2Three();

        if (score < 0) {
            score = 0;
        }

        if (score > 100) {
            score = 100;
        }
        return score;
    }

    /**
     * 中低强度密码校验：
     * 密码要求 6位以上字符，两种以上元素
     *
     * @param pwdStr
     * @return
     */
    public static int checkPwd(String pwdStr) {
        CheckPwdUtil pwd = new CheckPwdUtil(pwdStr);

        // 密码不能小于6位，不能大于18位
        if (pwd.length < 6 || pwd.length > 18) {
            return 0;
        }
        // 密码出现的种类 计数
        int type = 0;
        if (pwd.checkPwdUpper() > 0 || pwd.checkPwsLower() > 0) {
            type++;
        }
        if (pwd.checkNum() > 0) {
            type++;
        }
        if (type < 2) {
            return 0;
        }
        return 100;
    }

}
