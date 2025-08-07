package tcla.testdata

import javax.sql.DataSource

fun DataSource.dropTables() {
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
                stmt.addBatch("DROP TABLE IF EXISTS $it CASCADE")
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
