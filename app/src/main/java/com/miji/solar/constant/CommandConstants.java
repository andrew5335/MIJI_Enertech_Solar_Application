package com.miji.solar.constant;

public class CommandConstants {

    public static String sendRefresh = "@";    // 블루투스 연결된 경우 데이터를 가져오기 위한 명령어
    public static String sendRequest = "?";    // 신규 데이터 요청 명령어
    //public static String sendRequest = "\\";
    public static String sendRequestOld = "92";    // 1차 변경 데이터 요청 명령어 / 기존 데이터 요청 명령어 \\
    public static String sendOn = "#";    // 램프 켜기 명령어
    public static String sendOff = "$";    // 램프 끄기 명령어
    public static String sendTest = "%";    // 램프 테스트
}
