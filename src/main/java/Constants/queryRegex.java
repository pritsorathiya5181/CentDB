package Constants;

import java.util.regex.Pattern;

public class queryRegex {
    public static String CREATE_QUERY_OUTER = "CREATE\\sTABLE\\s(\\w+)\\s";
    public static String CREATE_QUERY_INNER = "\\(((?:\\w+\\s\\w+\\(?[0-9]*\\)?,?)+)\\);";
    public static final Pattern CREATE_QUERY_FINAL = Pattern.compile(CREATE_QUERY_OUTER + CREATE_QUERY_INNER);

    public static String DATABASE_CREATE = "CREATE\\sDATABASE\\s(\\w+);";
    public static final Pattern DATABASE_CREATE_FINAL = Pattern.compile(DATABASE_CREATE);

    public static String DATABASE_USE = "USE\\s(\\w+);";
    public static final Pattern DATABASE_USE_FINAL = Pattern.compile(DATABASE_USE);

    public static String INSERT_QUERY_OUTER = "INSERT\\sINTO\\s(\\w+)\\s\\(([\\s\\S]+)\\)";
    public static String INSERT_VALUES_QUERY = "\\sVALUES\\s\\(([\\s\\S]+)\\);";
    public static final Pattern INSERT_QUERY_FINAL = Pattern.compile(INSERT_QUERY_OUTER + INSERT_VALUES_QUERY);

    public static String SELECT_QUERY_OUTER = "SELECT\\s((\\*)?((\\w+)?((,(\\w+))*)?))\\sFROM\\s(\\w+)";
    public static String SELECT_QUERY_CONDITION = "(\\sWHERE\\s(\\w+)=(\\w+))?;";
    public static final Pattern SELECT_QUERY_FINAL = Pattern.compile(SELECT_QUERY_OUTER + SELECT_QUERY_CONDITION);

    public static String UPDATE_QUERY_OUTER = "UPDATE\\s(\\w+)\\sSET\\s(((\\w+)=(\\w+))(,\\s((\\w+)=(\\w+)))*)";
    public static String UPDATE_QUERY_CONDITION = "\\sWHERE\\s((\\w+)=(\\w+));";
    public static final Pattern UPDATE_QUERY_FINAL = Pattern.compile(UPDATE_QUERY_OUTER + UPDATE_QUERY_CONDITION);

    public static String DELETE_QUERY_OUTER = "DELETE FROM\\s(\\w+)";
    public static String DELETE_QUERY_CONDITION = "\\sWHERE\\s((\\w+)=(\\w+));";
    public static Pattern DELETE_QUERY_FINAL = Pattern.compile(DELETE_QUERY_OUTER + DELETE_QUERY_CONDITION);

    public static String TRUNCATE_QUERY = "TRUNCATE TABLE\\s(\\w+);";
    public static Pattern TRUNCATE_QUERY_FINAL = Pattern.compile(TRUNCATE_QUERY);

    public static String DROP_QUERY_OUTER = "DROP TABLE\\s(\\w+);";
    public static Pattern DROP_QUERY_FINAL = Pattern.compile(DROP_QUERY_OUTER);

    public static String BEGIN_TRANSACTION_QUERY = "BEGIN TRANSACTION;";
    public static Pattern BEGIN_TRANSACTION_QUERY_FINAL = Pattern.compile(BEGIN_TRANSACTION_QUERY);

    public static String COMMIT_TRANSACTION_QUERY = "COMMIT;";
    public static Pattern COMMIT_TRANSACTION_QUERY_FINAL = Pattern.compile(COMMIT_TRANSACTION_QUERY);

    public static String ROLLBACK_TRANSACTION_QUERY = "ROLLBACK;";
    public static Pattern ROLLBACK_TRANSACTION_QUERY_FINAL = Pattern.compile(ROLLBACK_TRANSACTION_QUERY);

    public static String DATABASE_EXPORT_QUERY = "(EXPORT|export)\\s(\\w+);";
    public static final Pattern DATABASE_EXPORT_QUERY_FINAL = Pattern.compile(DATABASE_EXPORT_QUERY);
}
