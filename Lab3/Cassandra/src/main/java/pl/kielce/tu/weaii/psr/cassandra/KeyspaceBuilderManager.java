package pl.kielce.tu.weaii.psr.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;
import com.datastax.oss.driver.api.querybuilder.schema.CreateKeyspace;
import com.datastax.oss.driver.api.querybuilder.schema.Drop;
import com.datastax.oss.driver.api.querybuilder.select.Select;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.selectFrom;

public class KeyspaceBuilderManager extends SimpleManager {


	public KeyspaceBuilderManager(CqlSession session) {
		super(session);
	}

	public void selectKeyspaces() {
		Select query = selectFrom("system_schema", "keyspaces").column("keyspace_name");
		SimpleStatement statement = query.build();
		ResultSet resultSet = session.execute(statement);

		System.out.print("Keyspaces = ");
		for (Row row : resultSet) {
			System.out.print(row.getString("keyspace_name") + ", ");
		}
		System.out.println();
	}

	public void createKeyspace() {
		CreateKeyspace create = SchemaBuilder.createKeyspace("lib").ifNotExists().withSimpleStrategy(1);
		executeSimpleStatement(create.build());
	}

	public void useKeyspace() {
		executeSimpleStatement("USE " + "lib" + ";");
	}

	public void dropKeyspace() {
		Drop drop = SchemaBuilder.dropKeyspace("lib").ifExists();
		executeSimpleStatement(drop.build());
	}
}
