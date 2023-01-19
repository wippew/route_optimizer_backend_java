package DG.DA;

import java.sql.*;

public class DBUtils {


    static final String DB_URL = "jdbc:postgresql://192.168.50.100:5432/";
    static final String USER = "optimointi";
    static final String PASS = "optimointi1";
    static final String QUERY = "SELECT * FROM public.here_sijainnit AS here";

    public static int checkIfHereResultExistsInDB(String wayPointA, String wayPointB) {
        String extendedQuery = QUERY + " WHERE here.waypoint_a = '" + wayPointA + "' AND here.waypoint_b = '" + wayPointB + "'";
        try(Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(extendedQuery);
        ) {
            if (!rs.next()) {
                return -1;
            } else {
                return rs.getInt("ajoaika");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //return negative value if something went wrong
        return -2;
    }


    public static void saveHereResultToDB(String wayPointA, String wayPointB, int driveDuration) {
        String waypointAB = "(" + wayPointA + ")" + ", (" + wayPointB + ")";
        String updateQuery = "INSERT INTO public.here_sijainnit(waypoint_a, waypoint_b, waypoint_ab, ajoaika, luontipvm, luoja, paivityspvm, paivittaja)" +
                " VALUES (" + "'" + wayPointA + "'" + ", " +  "'" + wayPointB + "'" + ", " + "'" + waypointAB + "'" + ", " + driveDuration + ", now(), 'system', now(), 'system')";
        try(Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            Statement stmt = conn.createStatement();
        ) {
            stmt.executeUpdate(updateQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
