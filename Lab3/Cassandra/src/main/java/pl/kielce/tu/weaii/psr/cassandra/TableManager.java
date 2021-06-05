package pl.kielce.tu.weaii.psr.cassandra;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.type.DataTypes;
import com.datastax.oss.driver.api.querybuilder.QueryBuilder;
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder;
import com.datastax.oss.driver.api.querybuilder.update.UpdateWithAssignments;

import java.util.*;
import java.util.stream.Collectors;

public class TableManager extends SimpleManager {

    public TableManager(CqlSession session) {
        super(session);
    }

    public void createMembersTable() {
        var createTable = SchemaBuilder.createTable("members").ifNotExists()
                .withPartitionKey("id", DataTypes.INT)
                .withColumn("name", DataTypes.TEXT)
                .withColumn("surname", DataTypes.TEXT)
                .withColumn("checkOuts", DataTypes.listOf(DataTypes.INT));
        session.execute(createTable.build());
    }

    public void createBooksTable() {
        var createTable = SchemaBuilder.createTable("books").ifNotExists()
                .withPartitionKey("id", DataTypes.INT)
                .withColumn("title", DataTypes.TEXT)
                .withColumn("category", DataTypes.TEXT)
                .withColumn("Authors", DataTypes.listOf(DataTypes.TEXT));
        session.execute(createTable.build());
    }

    public void addMember(Map<String, String> values, Integer id) {
        var insert = QueryBuilder.insertInto("members")
                .value("id", QueryBuilder.raw(id.toString()))
                .value("name", QueryBuilder.raw("'" + values.get("name") + "'"))
                .value("surname", QueryBuilder.raw("'" + values.get("surname") + "'"));
        session.execute(insert.build());
    }

	public void addBook(Map<String, String> values, List<String> authors, Integer id) {
    	var authorsValue = "[" + authors.stream().map(e -> "'" + e + "'").collect(Collectors.joining(",")) + "]";
		var insert = QueryBuilder.insertInto("books")
				.value("id", QueryBuilder.raw(id.toString()))
				.value("title", QueryBuilder.raw("'" + values.get("title") + "'"))
				.value("category", QueryBuilder.raw("'" + values.get("category") + "'"))
				.value("authors", QueryBuilder.raw(authorsValue));
		session.execute(insert.build());
	}

    public boolean updateMember(Integer id, Map<String, String> values) {
        var updateQuery = QueryBuilder.update("members");
        UpdateWithAssignments update = null;
        if (values.containsKey("name")) {
        	update = updateQuery
					.setColumn("name",  QueryBuilder.raw("'" + values.get("name") + "'"));
		}
		if (values.containsKey("surname")) {
			update = (update == null ? updateQuery : update)
					.setColumn("surname",  QueryBuilder.raw("'" + values.get("surname") + "'"));
		}

		if (values.containsKey("checkouts")) {
			update = (update == null ? updateQuery : update)
					.setColumn("checkouts",  QueryBuilder.raw(values.get("checkouts")));
		}

		if (update != null) {
			var result = session.execute(update
					.whereColumn("id")
					.isEqualTo(QueryBuilder.literal(id))
					.ifExists()
					.build());
			return result.wasApplied();
		}
		return false;
    }

    public boolean updateBook(Integer id, Map<String, String> values, List<String> authors) {
        var updateQuery = QueryBuilder.update("books");
        var authorsValue = "[" + authors.stream().map(e -> "'" + e + "'").collect(Collectors.joining(",")) + "]";
        UpdateWithAssignments update = null;
        if (values.containsKey("title")) {
            update = updateQuery
                    .setColumn("title",  QueryBuilder.raw("'" + values.get("title") + "'"));
        }
        if (values.containsKey("category")) {
            update = (update == null ? updateQuery : update)
                    .setColumn("category",  QueryBuilder.raw("'" + values.get("category") + "'"));
        }

        if (values.containsKey("books")) {
            update = (update == null ? updateQuery : update)
                    .setColumn("authors",  QueryBuilder.raw(authorsValue));
        }

        if (update != null) {
            var result = session.execute(update
                    .whereColumn("id")
                    .isEqualTo(QueryBuilder.literal(id))
                    .ifExists()
                    .build());
            return result.wasApplied();
        }
        return false;
    }

    public boolean deleteById(int id, String table) {
        return session.execute(QueryBuilder
                .deleteFrom(table).whereColumn("id")
                .isEqualTo(QueryBuilder.literal(id))
				.ifExists()
                .build()).wasApplied();
    }

    public void printAllMembers() {
        for (Row row : getAll("members")) {
            System.out.println(getMemberAsString(row));
        }
    }

    public void printMemberById(int id) {
        System.out.println(getById("members", id)
                .map(this::getMemberAsString).orElse("Not found"));
    }

    public void printAllBooks() {
        for (Row row : getAll("books")) {
            System.out.println(getBookAsString(row));
        }
    }

    public void printBookById(int id) {
        System.out.println(getById("books", id)
                .map(this::getBookAsString).orElse("Not found"));
    }

    public void printBookWithAuthorsContainsString(String str) {
        for (Row row : getAll("books")) {
            if (row.getList("authors", String.class).stream()
                    .anyMatch(s -> s.contains(str))) {
                System.out.println(getBookAsString(row));
            }
        }
    }

    public void borrowABook(Integer memberId, Integer bookId) {
        var member = getById("members", memberId);
        if (member.isEmpty()) {
            System.out.println("Member not found");
            return;
        }
        var book = getById("books", bookId);
        if (book.isEmpty()) {
            System.out.println("Book not found");
            return;
        }

        var chcks = member.get().getList("checkOuts", Integer.class);
        chcks = chcks != null ? chcks : new ArrayList<>();
        chcks.add(bookId);

        var updateMap = new HashMap<String, String>();
        updateMap.put("checkouts", chcks.toString());

        if(updateMember(memberId, updateMap)) {
            System.out.println("success");
        } else {
            System.out.println("Failed");
        }
    }

    public boolean deleteCheckOut(Integer memberId, Integer bookId) {
        var member = getById("members", memberId);
        if (member.isEmpty()) {
            return false;
        }

        var checkOuts = member.get().getList("checkouts", Integer.class);
        if (checkOuts == null) {
            return false;
        }

        if (!checkOuts.remove(bookId)) {
            return false;
        }

        var updateMap = new HashMap<String, String>();
        updateMap.put("checkouts", checkOuts.toString());

        return updateMember(memberId, updateMap);
    }

    public void deleteAllBookCheckouts(Integer bookId) {
        getById("books", bookId).ifPresent(b -> {
            for (Row memberRow : getAll("members")) {
                deleteCheckOut(memberRow.getInt("id"), bookId);
            }
        });
    }

    public double getAverageBorrowCount() {
        return getAll("members")
                .all()
                .stream()
                .mapToInt(e -> e.getList("checkouts", Integer.class).size())
                .average()
                .orElse(0.0);
    }

    private String getMemberAsString(Row row) {
        StringBuilder sb = new StringBuilder("Member: { ");
        sb.append(String.format("\"id\" : %s, ", row.getInt("id")));
        sb.append(String.format("\"name\" : \"%s\", ", row.getString("name")));
        sb.append(String.format("\"surname\" : \"%s\", ", row.getString("surname")));
        sb.append(String.format("\"checkOuts\" : %s", row.getList("checkOuts", Integer.class)));
        sb.append(" }");
        return sb.toString();
    }

    private String getBookAsString(Row row) {
        StringBuilder sb = new StringBuilder("Book: { ");
        sb.append(String.format("\"id\" : %s, ", row.getInt("id")));
        sb.append(String.format("\"title\" : \"%s\", ", row.getString("title")));
        sb.append(String.format("\"category\" : \"%s\", ", row.getString("category")));
        sb.append(String.format("\"authors\" : %s", row.getList("authors", String.class)));
        sb.append(" }");
        return sb.toString();
    }

    private ResultSet getAll(String table) {
        return session.execute(QueryBuilder.selectFrom(table).all().build());
    }

    private Optional<Row> getById(String table, int id) {
        return Optional.ofNullable(session.execute(QueryBuilder
                .selectFrom(table)
                .all().whereColumn("id")
                .isEqualTo(QueryBuilder.literal(id))
                .build()).one());
    }



}
