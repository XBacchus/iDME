import java.sql.*;

public class DbProbe {
  public static void main(String[] args) throws Exception {
    try (Connection c = DriverManager.getConnection("jdbc:postgresql://127.0.0.1:5433/sdk17?stringtype=unspecified", "postgres", "123456")) {
      System.out.println("=== applicationnameen distribution in xdm_typedefinition ===");
      try (Statement st = c.createStatement(); ResultSet rs = st.executeQuery("select coalesce(applicationnameen,'<null>') app, count(*) cnt from public.xdm_typedefinition where rdmdeleteflag=0 group by coalesce(applicationnameen,'<null>') order by cnt desc")) {
        while (rs.next()) {
          System.out.println(rs.getString(1) + "|" + rs.getLong(2));
        }
      }

      System.out.println("=== samples where app not null ===");
      try (Statement st = c.createStatement(); ResultSet rs = st.executeQuery("select entitynumber,name,applicationnameen,modeltype,entitytype from public.xdm_typedefinition where applicationnameen is not null order by id desc limit 30")) {
        while (rs.next()) {
          System.out.println(rs.getString(1)+"|"+rs.getString(2)+"|"+rs.getString(3)+"|"+rs.getString(4)+"|"+rs.getString(5));
        }
      }

      System.out.println("=== model count by tenant id in xdm_typedefinition ===");
      try (Statement st = c.createStatement(); ResultSet rs = st.executeQuery("select _tenantid, count(*) from public.xdm_typedefinition where rdmdeleteflag=0 group by _tenantid order by count(*) desc")) {
        while (rs.next()) {
          System.out.println(rs.getObject(1)+"|"+rs.getLong(2));
        }
      }
    }
  }
}
