package jdbi.dao;

import jdbi.MiniTodoDemo;
import jdbi.dto.Todo;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.transaction.Transaction;

import java.util.List;

public interface TodoDao {
	@SqlUpdate("insert into todo(title, due, status) values (:title, :due, :status)")
	@GetGeneratedKeys("id")
	long insert(@BindBean Todo todo);

	@SqlQuery("select * from todo order by id desc")
	@RegisterBeanMapper(Todo.class)
	List<Todo> list();

	// Deklaratywna transakcja – insert + audit w jednym commicie
	@Transaction
	default long insertWithAudit(Todo todo, AuditDao auditDao) {
		long id = insert(todo);
		auditDao.log("insert", id);
		return id;
	}
}