package Query;

import Analytics.Analytics;
import LogManagement.LogManagementService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

import static Constants.queryRegex.*;

public class QueryParser {
	DatabaseOperation dbOperation = new DatabaseOperation();
	Transaction transaction = new Transaction();
	Analytics analytics = Analytics.getAnalyticsInstance();
	public void parseQuery(String query) {
		Map<String,String> queryLogMap = new HashMap<String,String>();
		queryLogMap.put(LogManagementService.QUERY_EXECUTED_KEY, query);
		LogManagementService.getInstance().writeLog(queryLogMap);
		System.out.println("current database==" + dbOperation.getCurrentDatabase());
		
		Matcher createMatcher = CREATE_QUERY_FINAL.matcher(query);
		Matcher createDatabaseMatcher = DATABASE_CREATE_FINAL.matcher(query);
		Matcher useDatabaseMatcher = DATABASE_USE_FINAL.matcher(query);
		Matcher insertMatcher = INSERT_QUERY_FINAL.matcher(query);
		Matcher selectMatcher = SELECT_QUERY_FINAL.matcher(query);
		Matcher updateMatcher = UPDATE_QUERY_FINAL.matcher(query);
		Matcher deleteMatcher = DELETE_QUERY_FINAL.matcher(query);
		Matcher truncateMatcher = TRUNCATE_QUERY_FINAL.matcher(query);
		Matcher dropMatcher = DROP_QUERY_FINAL.matcher(query);
		Matcher beginTransactionMatcher = BEGIN_TRANSACTION_QUERY_FINAL.matcher(query);

		//TOTAL queries DATABASE
		//INSERT and UPDATE for tables

		if (createDatabaseMatcher.find()) {
			createDatabase(createDatabaseMatcher);
		} else if (useDatabaseMatcher.find()) {
			useDatabase(useDatabaseMatcher);
			System.out.println("Now, current database==" + dbOperation.getCurrentDatabase());
		} else if (createMatcher.find()) {
			createTable(createMatcher);
		} else if (insertMatcher.find()) {
			String tableName = insertMatcher.group(1);
			if (!Lock.checkLock(tableName)) {
				Lock.addLock(tableName);
				insertTable(insertMatcher);
				Lock.removeLock(tableName);
			} else {
				System.out.println("Table Locked. Try again after sometime..");
			}
		} else if (selectMatcher.find()) {
			selectTable(selectMatcher);
		} else if (updateMatcher.find()) {
			String tableName = updateMatcher.group(1);
			if (!Lock.checkLock(tableName)) {
				Lock.addLock(tableName);
				updateTable(updateMatcher);
				Lock.removeLock(tableName);
			} else {
				System.out.println("Table Locked. Try again after sometime..");
			}
		} else if (deleteMatcher.find()) {
			String tableName = deleteMatcher.group(1);
			if (!Lock.checkLock(tableName)) {
				Lock.addLock(tableName);
				deleteTable(deleteMatcher);
				Lock.removeLock(tableName);
			} else {
				System.out.println("Table Locked. Try again after sometime..");
			}
		} else if (truncateMatcher.find()) {
			String tableName = truncateMatcher.group(1);
			if (!Lock.checkLock(tableName)) {
				Lock.addLock(tableName);
				truncateTable(truncateMatcher);
				Lock.removeLock(tableName);
			} else {
				System.out.println("Table Locked. Try again after sometime..");
			}
		} else if (dropMatcher.find()) {
			String tableName = dropMatcher.group(1);
			if (!Lock.checkLock(tableName)) {
				Lock.addLock(tableName);
				dropTable(dropMatcher);
				Lock.removeLock(tableName);
			} else {
				System.out.println("Table Locked. Try again after sometime..");
			}
		} else if (beginTransactionMatcher.find()) {
			Transaction transactionQuery = new Transaction();
			System.out.println("Transaction Begins");
			transactionQuery.processTransaction();
		} else {
			System.out.println("Please enter a valid query");
		}
	}

	public boolean createDatabase(Matcher createDatabaseMatcher) {
		Map<String, String> createDbLogMap = new HashMap<String,String>();
		long queryStartTime = System.nanoTime();
		boolean status = dbOperation.createDb(createDatabaseMatcher.group(1));
		long queryEndTime = System.nanoTime();
		long executionTime = queryEndTime - queryStartTime;
		if (status) {
			if(!analytics.DBqueries.containsKey(createDatabaseMatcher.group(1))) {
				analytics.DBqueries.put(createDatabaseMatcher.group(1),new ArrayList<>(){
					{
						add(1);add(0);
					}
				});
			}
			else {
				analytics.DBqueries.computeIfPresent(createDatabaseMatcher.group(1),(k,v)->new ArrayList<Integer>(){
					{
						add(v.get(0)+1);
						add(v.get(1));
					}
				});
			}
			createDbLogMap.put(LogManagementService.DB_CHANGE_KEY,
					"Database " + createDatabaseMatcher.group(1) + " has been created. 0 row(s) affected.");
			LogManagementService.getInstance().writeLog(createDbLogMap);
			System.out.println("Created database: " + createDatabaseMatcher.group(1));
		} else {
			if(!analytics.DBqueries.containsKey(createDatabaseMatcher.group(1))) {
				analytics.DBqueries.put(createDatabaseMatcher.group(1),new ArrayList<>(){
					{
						add(0);add(1);
					}
				});
			}
			else {
				analytics.DBqueries.computeIfPresent(createDatabaseMatcher.group(1),(k,v)->new ArrayList<Integer>(){
					{
						add(v.get(0));
						add(v.get(1)+1);
					}
				});
			}
			createDbLogMap.put(LogManagementService.DB_CHANGE_KEY, "Failed to create database. 0 row(s) affected.");
			LogManagementService.getInstance().writeLog(createDbLogMap);
		}
		createDbLogMap.put(LogManagementService.DB_STATE_KEY, "Total tables: ");
		createDbLogMap.put(LogManagementService.EXECUTION_TIME_KEY, "Execution time: " + executionTime);
		LogManagementService.getInstance().writeLog(createDbLogMap);
		return status;
	}

	public boolean useDatabase(Matcher useDatabaseMatcher) {
		Map<String, String> useDbLogMap = new HashMap<String,String>();
		long queryStartTime = System.nanoTime();
		boolean status = dbOperation.useDb(useDatabaseMatcher.group(1));

		long queryEndTime = System.nanoTime();
		long executionTime = queryEndTime - queryStartTime;
		if (status) {

			//databse queries
//			analytics.DBqueries.put()
			if(!analytics.DBqueries.containsKey(useDatabaseMatcher.group(1))) {
				analytics.DBqueries.put(useDatabaseMatcher.group(1),new ArrayList<>(){
					{
						add(1);add(0);
					}
				});
			}
			else {
				analytics.DBqueries.computeIfPresent(useDatabaseMatcher.group(1),(k,v)->new ArrayList<Integer>(){
					{
						add(v.get(0)+1);
						add(v.get(1));
					}
				});
			}

			System.out.println(analytics.DBqueries);

			useDbLogMap.put(LogManagementService.DB_CHANGE_KEY, "Currently using " + useDatabaseMatcher.group(1) + ". 0 row(s) affected.");
			LogManagementService.getInstance().writeLog(useDbLogMap);
			System.out.println("Switched the database");
		} else {

			//databse queries
			if(!analytics.DBqueries.containsKey(useDatabaseMatcher.group(1))) {
				analytics.DBqueries.put(useDatabaseMatcher.group(1),new ArrayList<>(){
					{
						add(0);add(1);
					}
				});
			}
			else {
				analytics.DBqueries.computeIfPresent(useDatabaseMatcher.group(1),(k,v)->new ArrayList<Integer>(){
					{
						add(v.get(0));
						add(v.get(1)+1);
					}
				});
			}
			System.out.println(analytics.DBqueries);

			useDbLogMap.put(LogManagementService.DB_CHANGE_KEY, useDatabaseMatcher.group(1) + "' database is not available. 0 row(s) affected.");
			LogManagementService.getInstance().writeLog(useDbLogMap);
			System.out.println("'" + useDatabaseMatcher.group(1) + "' database is not available.");
		}
		
		useDbLogMap.put(LogManagementService.DB_STATE_KEY, "Total tables: ");
		useDbLogMap.put(LogManagementService.EXECUTION_TIME_KEY, "Execution time: " + executionTime);
		LogManagementService.getInstance().writeLog(useDbLogMap);
		return status;
	}

	public boolean createTable(Matcher createMatcher) {
		HashMap<String, String> keySet = new HashMap<>();
		Map<String,String> createTableLogMap = new HashMap<String,String>();
		String tableName = createMatcher.group(1);
		String tableSet = createMatcher.group(2);
		String[] colValSet = tableSet.split(",");
		ArrayList<String> columns = new ArrayList<>();
		ArrayList<String> values = new ArrayList<>();

		for (String columnValue : colValSet) {
			String[] set = columnValue.split(" ");
			columns.add(set[0].strip());
			values.add(set[1].strip());
			if (set.length == 3) {
				if (set[2].strip().equals("PK")) {
					keySet.put(set[0].strip(), "PK");
				}
			} else if (set.length > 3) {
				keySet.put(set[0].strip(), set[2].strip() + " " + set[3].strip() + " " + set[4].strip());
			}
		}

		TableOperation tableOperation = new TableOperation();
		if (dbOperation.getCurrentDatabase() != null) {
			long queryStartTime = System.nanoTime();
			boolean status = tableOperation.createTable(dbOperation.getCurrentDatabase(), tableName, columns, values,
					keySet);
			long queryEndTime = System.nanoTime();
			long executionTime = queryEndTime - queryStartTime;

			if (status) {
				//databse queries
				if(!analytics.DBqueries.containsKey(dbOperation.getCurrentDatabase())) {
					analytics.DBqueries.put(dbOperation.getCurrentDatabase(),new ArrayList<>(){
						{
							add(1);add(0);
						}
					});
				}
				else {
					analytics.DBqueries.computeIfPresent(dbOperation.getCurrentDatabase(),(k,v)->new ArrayList<Integer>(){
						{
							add(v.get(0)+1);
							add(v.get(1));
						}
					});
				}

				createTableLogMap.put(LogManagementService.DB_CHANGE_KEY, tableName + " table created in database. 0 row(s) affected.");
				LogManagementService.getInstance().writeLog(createTableLogMap);
				System.out.println("Successfully creation of new table: " + tableName + " in database: "
						+ dbOperation.getCurrentDatabase());
			} else {

				//databse queries
				if(!analytics.DBqueries.containsKey(dbOperation.getCurrentDatabase())) {
					analytics.DBqueries.put(dbOperation.getCurrentDatabase(),new ArrayList<>(){
						{
							add(0);add(1);
						}
					});
				}
				else {
					analytics.DBqueries.computeIfPresent(dbOperation.getCurrentDatabase(),(k,v)->new ArrayList<Integer>(){
						{
							add(v.get(0));
							add(v.get(1)+1);
						}
					});
				}
				createTableLogMap.put(LogManagementService.DB_CHANGE_KEY, "Failed to create table " + tableName + ". 0 row(s) affected.");
				LogManagementService.getInstance().writeLog(createTableLogMap);
				System.out.println("Failure creation of new table: " + tableName + " in database: "
						+ dbOperation.getCurrentDatabase());
			}
			createTableLogMap.put(LogManagementService.EXECUTION_TIME_KEY, "Execution time: " + executionTime);
			createTableLogMap.put(LogManagementService.DB_STATE_KEY, "Total tables: ");
			LogManagementService.getInstance().writeLog(createTableLogMap);
			return status;
		} else {
			System.out.println("Please select database");
			return false;
		}
	}

	public boolean insertTable(Matcher insertMatcher) {
		String tableName = insertMatcher.group(1);
		String columnSet = insertMatcher.group(2);
		String[] cols = columnSet.split(",");
		String valueSet = insertMatcher.group(3);
		String[] vals = valueSet.split(",");

		ArrayList<String> columns = new ArrayList<String>(Arrays.asList(cols));
		ArrayList<String> values = new ArrayList<String>(Arrays.asList(vals));

		if (dbOperation.getCurrentDatabase() != null) {
			TableOperation tableOperation = new TableOperation();
			long queryStartTime = System.nanoTime();
			boolean status = tableOperation.insert(dbOperation.getCurrentDatabase(), tableName, columns, values);
			long queryEndTime = System.nanoTime();
			long executionTime = queryEndTime - queryStartTime;

			if (status) {
				//databse queries
				if(!analytics.DBqueries.containsKey(dbOperation.getCurrentDatabase())) {
					analytics.DBqueries.put(dbOperation.getCurrentDatabase(),new ArrayList<>(){
						{
							add(1);add(0);
						}
					});
				}
				else {
					analytics.DBqueries.computeIfPresent(dbOperation.getCurrentDatabase(),(k,v)->new ArrayList<Integer>(){
						{
							add(v.get(0)+1);
							add(v.get(1));
						}
					});
				}
				System.out.println("Successfully inserted into the table");
			} else {

				//databse queries
				if(!analytics.DBqueries.containsKey(dbOperation.getCurrentDatabase())) {
					analytics.DBqueries.put(dbOperation.getCurrentDatabase(),new ArrayList<>(){
						{
							add(0);add(1);
						}
					});
				}
				else {
					analytics.DBqueries.computeIfPresent(dbOperation.getCurrentDatabase(),(k,v)->new ArrayList<Integer>(){
						{
							add(v.get(0));
							add(v.get(1)+1);
						}
					});
				}
				System.out.println("Failure insertion of new entry in: " + tableName + " table");
			}
			return status;
		} else {
			System.out.println("Please select database");
			return false;
		}
	}

	public boolean selectTable(Matcher selectMatcher) {
		Map<String, String> selectTableLogMap = new HashMap<String,String>();
		String tableName = selectMatcher.group(8);
		String tableSet = selectMatcher.group(1);
		String[] colValSet = tableSet.split(",");
		ArrayList<String> columns = new ArrayList<>(Arrays.asList(colValSet));
		String conditionColumns = selectMatcher.group(10);
		String conditionValues = selectMatcher.group(11);

		if (dbOperation.getCurrentDatabase() != null) {
			TableOperation tableOperation = new TableOperation();
			long queryStartTime = System.nanoTime();
			int status = tableOperation.select(dbOperation.getCurrentDatabase(), tableName, columns, conditionColumns,
					conditionValues);
			long queryEndTime = System.nanoTime();
			long executionTime = queryEndTime - queryStartTime;

			if (status > 0) {
				//databse queries
				if(!analytics.DBqueries.containsKey(dbOperation.getCurrentDatabase())) {
					analytics.DBqueries.put(dbOperation.getCurrentDatabase(),new ArrayList<>(){
						{
							add(1);add(0);
						}
					});
				}
				else {
					analytics.DBqueries.computeIfPresent(dbOperation.getCurrentDatabase(),(k,v)->new ArrayList<Integer>(){
						{
							add(v.get(0)+1);
							add(v.get(1));
						}
					});
				}
				selectTableLogMap.put(LogManagementService.DB_CHANGE_KEY, "Rows affected: " + status);
				LogManagementService.getInstance().writeLog(selectTableLogMap);
				System.out.println("Successfully performed select query on the " + tableName + " table");
			} else {
				//databse queries
				if(!analytics.DBqueries.containsKey(dbOperation.getCurrentDatabase())) {
					analytics.DBqueries.put(dbOperation.getCurrentDatabase(),new ArrayList<>(){
						{
							add(0);add(1);
						}
					});
				}
				else {
					analytics.DBqueries.computeIfPresent(dbOperation.getCurrentDatabase(),(k,v)->new ArrayList<Integer>(){
						{
							add(v.get(0));
							add(v.get(1)+1);
						}
					});
				}
				System.out.println("Failure to perform selection query on the " + tableName + " table");
				selectTableLogMap.put(LogManagementService.DB_CHANGE_KEY, "Selection query not performed");
				LogManagementService.getInstance().writeLog(selectTableLogMap);
			}
			selectTableLogMap.put(LogManagementService.EXECUTION_TIME_KEY, "Execution time: " + executionTime);
			selectTableLogMap.put(LogManagementService.DB_STATE_KEY, "Total Tables: ");
			LogManagementService.getInstance().writeLog(selectTableLogMap);
			return status > 0;
		} else {
			System.out.println("Please select database");
			return false;
		}
	}

	public boolean updateTable(Matcher updateMatcher) {
		Map<String, String> updateTableLogMap = new HashMap<String,String>();
		String tableName = updateMatcher.group(1);
		String tableSet = updateMatcher.group(2);
		String conditionSet = updateMatcher.group(10);
		ArrayList<String> columns = new ArrayList<>();
		ArrayList<String> values = new ArrayList<>();

		if (tableSet.split(", ").length == 1) {
			columns.add(tableSet.split("=")[0]);
			values.add(tableSet.split("=")[1]);
		}
		String[] colValSet = tableSet.split(", ");
		for (String colVal : colValSet) {
			columns.add(colVal.split("=")[0].strip());
			values.add((colVal.split("=")[1]).strip());
		}
		String conditionColumns = conditionSet.split("=")[0].strip();
		String conditionValues = conditionSet.split("=")[1].strip();

		if (dbOperation.getCurrentDatabase() != null) {
			TableOperation tableOperation = new TableOperation();
			long queryStartTime = System.nanoTime();
			int status = tableOperation.update(dbOperation.getCurrentDatabase(), tableName, columns, values,
					conditionColumns, conditionValues);
			long queryEndTime = System.nanoTime();
			long executionTime = queryEndTime - queryStartTime;

			System.out.println("status===" + status);
			if (status > 0) {
				//databse queries
				if(!analytics.DBqueries.containsKey(dbOperation.getCurrentDatabase())) {
					analytics.DBqueries.put(dbOperation.getCurrentDatabase(),new ArrayList<>(){
						{
							add(1);add(0);
						}
					});
				}
				else {
					analytics.DBqueries.computeIfPresent(dbOperation.getCurrentDatabase(),(k,v)->new ArrayList<Integer>(){
						{
							add(v.get(0)+1);
							add(v.get(1));
						}
					});
				}
				updateTableLogMap.put(LogManagementService.DB_CHANGE_KEY, "Update successful. Rows affected: " + status);
				LogManagementService.getInstance().writeLog(updateTableLogMap);
				System.out.println("Successfully updated into the table");
			} else {
				//databse queries
				if(!analytics.DBqueries.containsKey(dbOperation.getCurrentDatabase())) {
					analytics.DBqueries.put(dbOperation.getCurrentDatabase(),new ArrayList<>(){
						{
							add(0);add(1);
						}
					});
				}
				else {
					analytics.DBqueries.computeIfPresent(dbOperation.getCurrentDatabase(),(k,v)->new ArrayList<Integer>(){
						{
							add(v.get(0));
							add(v.get(1)+1);
						}
					});
				}
				updateTableLogMap.put(LogManagementService.DB_CHANGE_KEY, "Update failed. Rows affected: 0");
				LogManagementService.getInstance().writeLog(updateTableLogMap);
				System.out.println("Failure updating of new entry in: " + tableName + " table");
			}
			updateTableLogMap.put(LogManagementService.EXECUTION_TIME_KEY, "Execution time: " + executionTime);
			updateTableLogMap.put(LogManagementService.DB_STATE_KEY, "No change in database state !");
			LogManagementService.getInstance().writeLog(updateTableLogMap);
			return status > 0;
		} else {
			System.out.println("Please select database");
			return false;
		}
	}

	public boolean deleteTable(Matcher deleMatcher) {
		Map<String, String> updateTableLogMap = new HashMap<String,String>();
		System.out.println("count===" + deleMatcher.groupCount());
		String tableName = deleMatcher.group(1);
		String conditionColumn = deleMatcher.group(3);
		String conditionValue = deleMatcher.group(4);

		if (dbOperation.getCurrentDatabase() != null) {
			TableOperation tableOperation = new TableOperation();
			long queryStartTime = System.nanoTime();
			boolean status = tableOperation.delete(dbOperation.getCurrentDatabase(), tableName, conditionColumn,
					conditionValue);
			long queryEndTime = System.nanoTime();
			long executionTime = queryEndTime - queryStartTime;

			if (status) {
				//databse queries
				if(!analytics.DBqueries.containsKey(dbOperation.getCurrentDatabase())) {
					analytics.DBqueries.put(dbOperation.getCurrentDatabase(),new ArrayList<>(){
						{
							add(1);add(0);
						}
					});
				}
				else {
					analytics.DBqueries.computeIfPresent(dbOperation.getCurrentDatabase(),(k,v)->new ArrayList<Integer>(){
						{
							add(v.get(0)+1);
							add(v.get(1));
						}
					});
				}
				updateTableLogMap.put(LogManagementService.DB_CHANGE_KEY, "Deleted row. 1 row(s) affected.");
				LogManagementService.getInstance().writeLog(updateTableLogMap);
				System.out.println("Successfully deleted entry from the table");
			} else {
				//databse queries
				if(!analytics.DBqueries.containsKey(dbOperation.getCurrentDatabase())) {
					analytics.DBqueries.put(dbOperation.getCurrentDatabase(),new ArrayList<>(){
						{
							add(0);add(1);
						}
					});
				}
				else {
					analytics.DBqueries.computeIfPresent(dbOperation.getCurrentDatabase(),(k,v)->new ArrayList<Integer>(){
						{
							add(v.get(0));
							add(v.get(1)+1);
						}
					});
				}
				updateTableLogMap.put(LogManagementService.DB_CHANGE_KEY, "Deletion failed. 0 row(s) affected.");
				LogManagementService.getInstance().writeLog(updateTableLogMap);
				System.out.println("Failure deleting of new entry in: " + tableName + " table");
			}
			updateTableLogMap.put(LogManagementService.EXECUTION_TIME_KEY, "Execution time: " + executionTime);
			updateTableLogMap.put(LogManagementService.DB_STATE_KEY, "No change in database state !");
			LogManagementService.getInstance().writeLog(updateTableLogMap);
			return status;
		} else {
			System.out.println("Please select database");
			return false;
		}
	}

	public boolean truncateTable(Matcher truncateMatcher) {
		String tableName = truncateMatcher.group(1);
		Map<String,String> truncateTableLogMap = new HashMap<String,String>();
		if (dbOperation.getCurrentDatabase() != null) {
			TableOperation tableOperation = new TableOperation();
			long queryStartTime = System.nanoTime();
			boolean status = tableOperation.truncate(dbOperation.getCurrentDatabase(), tableName);
			long queryEndTime = System.nanoTime();
			long executionTime = queryEndTime - queryStartTime;

			if (status) {

				truncateTableLogMap.put(LogManagementService.DB_CHANGE_KEY, tableName + " table truncated !");
				LogManagementService.getInstance().writeLog(truncateTableLogMap);
				System.out.println("Successfully truncated the " + tableName + " table");
			} else {
				truncateTableLogMap.put(LogManagementService.DB_CHANGE_KEY, "Failed to truncate " + tableName + " table!");
				LogManagementService.getInstance().writeLog(truncateTableLogMap);
				System.out.println("Failure to truncate the : " + tableName + " table");
			}
			truncateTableLogMap.put(LogManagementService.EXECUTION_TIME_KEY, "Execution time: " + executionTime);
			truncateTableLogMap.put(LogManagementService.DB_STATE_KEY, "No change in database state.");
			LogManagementService.getInstance().writeLog(truncateTableLogMap);
			return status;
		} else {
			System.out.println("Please select database");
			return true;
		}

	}

	public boolean dropTable(Matcher dropMatcher) {
		Map<String,String> dropTableLogMap = new HashMap<String,String>();
		String tableName = dropMatcher.group(1);

		if (dbOperation.getCurrentDatabase() != null) {
			TableOperation tableOperation = new TableOperation();
			long queryStartTime = System.nanoTime();
			boolean status = tableOperation.drop(dbOperation.getCurrentDatabase(), tableName);
			long queryEndTime = System.nanoTime();
			long executionTime = queryEndTime - queryStartTime;

			if (status) {
				//databse queries
				if(!analytics.DBqueries.containsKey(dbOperation.getCurrentDatabase())) {
					analytics.DBqueries.put(dbOperation.getCurrentDatabase(),new ArrayList<>(){
						{
							add(1);add(0);
						}
					});
				}
				else {
					analytics.DBqueries.computeIfPresent(dbOperation.getCurrentDatabase(),(k,v)->new ArrayList<Integer>(){
						{
							add(v.get(0)+1);
							add(v.get(1));
						}
					});
				}
				dropTableLogMap.put(LogManagementService.DB_CHANGE_KEY, tableName + " table dropped !");
				LogManagementService.getInstance().writeLog(dropTableLogMap);
				System.out.println("Successfully dropped the table");
			} else {
				//databse queries
				if(!analytics.DBqueries.containsKey(dbOperation.getCurrentDatabase())) {
					analytics.DBqueries.put(dbOperation.getCurrentDatabase(),new ArrayList<>(){
						{
							add(0);add(1);
						}
					});
				}
				else {
					analytics.DBqueries.computeIfPresent(dbOperation.getCurrentDatabase(),(k,v)->new ArrayList<Integer>(){
						{
							add(v.get(0));
							add(v.get(1)+1);
						}
					});
				}
				dropTableLogMap.put(LogManagementService.DB_CHANGE_KEY, tableName + " table failed to drop !");
				LogManagementService.getInstance().writeLog(dropTableLogMap);
				System.out.println("Failure to drop the " + tableName + " table");
			}
			dropTableLogMap.put(LogManagementService.EXECUTION_TIME_KEY, "Execution time: " + executionTime);
			dropTableLogMap.put(LogManagementService.DB_STATE_KEY, "No change in database state");
			LogManagementService.getInstance().writeLog(dropTableLogMap);
			return status;
		} else {
			System.out.println("Please select database");
			return false;
		}
	}
}