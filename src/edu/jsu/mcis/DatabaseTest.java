package edu.jsu.mcis;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.sql.*;

public class DatabaseTest {
    public static void main(String[] args) {
        JSONArray jsonArray = getJSONData();
    }

    public static JSONArray getJSONData() {
        Connection conn = null;
        PreparedStatement pstSelect = null, pstUpdate = null;
        ResultSet resultSet = null;
        ResultSetMetaData metaData = null;

        String query;

        boolean hasResults;
        int resultCount, columnCount;
        JSONArray jsonArray = null;

        try {
            String server = ("jdbc:mysql://localhost/p2_test");
            String username = "root";
            String pass = "root";
            System.out.println("Attempting to connect with *" + server + "*...");

            Class.forName("com.mysql.jdbc.Driver").newInstance();
            conn = DriverManager.getConnection(server, username, pass);
            if (conn.isValid(0)) {
                System.out.println("Connection successful");

                //Query the system. In this case, retrieve info from table "people"
                query = "SELECT * FROM people";
                pstSelect = conn.prepareStatement(query);
                System.out.println("Submitting query...");

                hasResults = pstSelect.execute();
                System.out.println("Getting results...");

                while (hasResults || pstSelect.getUpdateCount() != -1) {
                    if (hasResults) {
                        resultSet = pstSelect.getResultSet();
                        metaData = resultSet.getMetaData();
                        columnCount = metaData.getColumnCount();

                        JSONObject person;
                        jsonArray = new JSONArray();
                        while (resultSet.next()) {
                            person = new JSONObject();
                            for (int i = 2; i <= columnCount; i++) {
                                person.put(metaData.getColumnLabel(i), resultSet.getString(i));
                            }
                            jsonArray.add(person);

                        }

                    } else {
                        resultCount = pstSelect.getUpdateCount();

                        if (resultCount == -1) {
                            break;
                        }
                    }

                    hasResults = pstSelect.getMoreResults();
                }

            }

            //System.out.println();
            conn.close();

        } catch (Exception e) {
            System.err.println(e.toString());
//            e.printStackTrace();
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                    resultSet = null;
                } catch (Exception e) {
                }
            }

            if (pstSelect != null) {
                try {
                    pstSelect.close();
                    pstSelect = null;
                } catch (Exception e) {
                }
            }

            if (pstUpdate != null) {
                try {
                    pstUpdate.close();
                    pstUpdate = null;
                } catch (Exception e) {
                }
            }
        }
        if (jsonArray == null) {
            System.out.println("ERROR: JSONArray failed to populate");
        } else {
            System.out.println("Results successfully retrieved");
        }
        return jsonArray;
    }

}
