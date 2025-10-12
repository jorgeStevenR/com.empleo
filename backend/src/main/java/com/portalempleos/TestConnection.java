package com.portalempleos;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import javax.sql.DataSource;
import java.sql.Connection;

@Component
public class TestConnection implements CommandLineRunner {

    private final DataSource dataSource;

    public TestConnection(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(String... args) {
        try (Connection conn = dataSource.getConnection()) {
            System.out.println("✅ Conexión exitosa a Supabase PostgreSQL");
            System.out.println("Base de datos: " + conn.getCatalog());
        } catch (Exception e) {
            System.err.println("❌ Error al conectar a Supabase:");
            e.printStackTrace();
        }
    }
}
