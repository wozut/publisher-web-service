package tcla.testdata

import javax.sql.DataSource

fun DataSource.truncateTables() {
    connection.use { con ->
        val tableNames = mutableListOf<String>()
        con.createStatement().use {
            val rs = it.executeQuery(showTables)
            while (rs.next()) {
                tableNames.add(rs.getString(1))
            }
        }

        con.createStatement().use { stmt ->
            tableNames.forEach {
                stmt.addBatch("ALTER TABLE $it DISABLE TRIGGER ALL")
                stmt.addBatch("TRUNCATE TABLE $it RESTART IDENTITY CASCADE")
                stmt.addBatch("ALTER TABLE $it ENABLE TRIGGER ALL")
            }
            stmt.executeBatch()
        }
    }
}

private val showTables = """
     SELECT concat(schemaname,'.' , tablename) 
     FROM pg_catalog.pg_tables WHERE 
          schemaname != 'pg_catalog' 
     AND  schemaname != 'information_schema'
     AND  tablename != 'flyway_schema_history'
""".trimIndent()
