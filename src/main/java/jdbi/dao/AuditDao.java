package jdbi.dao;

import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

public interface AuditDao {
	@SqlUpdate("insert into audit(action, todo_id) values (:action, :todoId)")
	void log(@Bind("action") String action, @Bind("todoId") long todoId);
}