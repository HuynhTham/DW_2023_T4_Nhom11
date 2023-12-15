package warehouse.dao;

import warehouse.entity.Logs;
import org.jdbi.v3.core.Handle;

import java.sql.Timestamp;
import java.util.Optional;

public class LogDAO {
    public void insertLog(String name, String eventType, String status) {
        try (Handle handle = DBContext.connectControl().open()) {
            String query = "INSERT INTO logs (name, event_type, status, created_at) VALUES (?, ?, ?, ?)";
            Timestamp now = Timestamp.from(java.time.Instant.now());
            System.out.println(now);
            handle.createUpdate(query)
                    .bind(0, name)
                    .bind(1, eventType)
                    .bind(2, status)
                    .bind(3, now)
                    .execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public boolean isLastLogStatusRunning(String name, String event_type, String status) {
        try (Handle handle = DBContext.connectControl().open()) {
            String query = "SELECT id,name, event_type, status, created_at FROM logs where  DATE(created_at)=DATE(now())  and status=? ORDER BY created_at DESC LIMIT 1";

            Optional<Logs> lastLog = handle.createQuery(query).bind(0,status)
                    .mapToBean(Logs.class) // Assuming you have a Log class that corresponds to the 'logs' table
                    .findOne();
            return lastLog.isPresent() && status.equals(lastLog.get().getStatus()) && name.equals(lastLog.get().getName()) && event_type.equals(lastLog.get().getEvent_type());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {

    }
}
