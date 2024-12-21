import java.sql.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.*;
public class PostgreSQLConcurrencyTest {
    String url = "jdbc:postgresql://172.16.107.156:0612/test";
    String username = "Alex";
    String password = "1q2w3e4r";
    private static final int NUM_THREADS = 10;  // 模拟的并发用户数
    private static final int NUM_OPERATIONS = 1000;  // 每个线程执行的操作数
    private static final AtomicLong successCount = new AtomicLong(0);
    public static void main(String[] args) throws InterruptedException {
        Logger.getLogger("org.postgresql").setLevel(Level.SEVERE);
        long startTime = System.currentTimeMillis();
        ExecutorService executorService = Executors.newFixedThreadPool(NUM_THREADS);
        for (int i = 0; i < NUM_THREADS; i++) {
            executorService.submit(() -> {
                try {
                    for (int j = 0; j < NUM_OPERATIONS; j++) {
                        performDatabaseOperation();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        }
        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES); 
        long endTime = System.currentTimeMillis();
        long totalOperations = NUM_THREADS * NUM_OPERATIONS; //操作数
        double elapsedTimeSeconds = (endTime - startTime) / 1000.0; //吞吐量
        double throughput = totalOperations / elapsedTimeSeconds;
        System.out.println("Total Time: " + (endTime - startTime) + "ms");
        System.out.println("Total Number of Operations: " + totalOperations);
        System.out.println("Number of Operations per second): " + throughput);
    }
    private static void performDatabaseOperation() throws SQLException {
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD)) {
            conn.setAutoCommit(false);
            String sql = "INSERT INTO test_table (name) VALUES (?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, "User_" + Thread.currentThread().getId() + "_" + System.nanoTime());
                stmt.executeUpdate();
            }
            conn.commit();
            successCount.incrementAndGet();
        }
    }
}
