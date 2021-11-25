package Constants;

import java.util.regex.Pattern;

public class queryRegex {
    public static String CREATE_QUERY_OUTER = "CREATE\\sTABLE\\s(\\w+)\\s";
    public static String CREATE_QUERY_INNER = "\\(((?:\\w+\\s\\w+\\(?[0-9]*\\)?,?)+)\\);";
    public static final Pattern CREATE_QUERY_FINAL = Pattern.compile(CREATE_QUERY_OUTER+CREATE_QUERY_INNER);

    public static String DATABASE_CREATE = "CREATE\\sDATABASE\\s(\\w+);";
    public static final Pattern DATABASE_CREATE_FINAL = Pattern.compile(DATABASE_CREATE);

    public static String DATABASE_USE = "USE\\s(\\w+);";
    public static final Pattern DATABASE_USE_FINAL = Pattern.compile(DATABASE_USE);

    public static String INSERT_QUERY_OUTER = "INSERT\\sINTO\\s(\\w+)\\s\\(([\\s\\S]+)\\)";
    public static String INSERT_VALUES_QUERY = "\\sVALUES\\s\\(([\\s\\S]+)\\);";
    public static final Pattern INSERT_QUERY_FINAL = Pattern.compile(INSERT_QUERY_OUTER+INSERT_VALUES_QUERY);

    public String SELECT_QUERY_OUTER = "SELECT\\s((\\*)?((\\w+)?((,(\\w+))*)?))\\sFROM\\s(\\w+)";
    public String SELECT_QUERY_CONDITION = "(\\sWHERE\\s(\\w+)=(\\w+))?;";
    public Pattern SELECT_QUERY_FINAL = Pattern.compile(SELECT_QUERY_OUTER+SELECT_QUERY_CONDITION);
}
