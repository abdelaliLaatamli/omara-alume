package main.Models.utils;

public class DBConnection {

//    public static final String dbDRIVER  = "com.mysql.jdbc.Driver";
//    public static final String dbURL     = "jdbc:mysql://localhost:3306/omar_alum";
//    public static final String dbUSER    = "root";
//    public static final String dbPASS    = "";
//    public static final String dbDIALECT = "org.hibernate.dialect.MySQL5Dialect";


    private static final String dbHOST   = "localhost";
    public static final String dbPORT   = "3307";
    public static final String dbDTBS   = "omar_alum";
    public static final String dbUSER    = "root";
    public static final String dbPASS    = "omarAlumDb@PAssword@";


    public static final String dbDRIVER  = "com.mysql.jdbc.Driver";
    public static final String dbURL     = "jdbc:mysql://"+dbHOST+":"+dbPORT+"/"+dbDTBS;
    public static final String dbDIALECT = "org.hibernate.dialect.MySQL5Dialect";

}
