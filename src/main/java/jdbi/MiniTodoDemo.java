package jdbi;

import jdbi.dao.AuditDao;
import jdbi.dao.TodoDao;
import jdbi.dto.Status;
import jdbi.dto.Todo;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

import java.time.LocalDate;

public class MiniTodoDemo {
	public static void main(String[] args) {
		// 1. Konfiguracja Jdbi + plugin SQL Object
		Jdbi jdbi = Jdbi.create("jdbc:h2:mem:todo;DB_CLOSE_DELAY=-1");
		jdbi.installPlugin(new SqlObjectPlugin());

		// 2. Tworzenie schematu w H2
		jdbi.useHandle(handle -> {
			handle.execute("""
                    create table todo (
                      id identity primary key,
                      title varchar(200) not null,
                      due date,
                      status varchar(10)
                    )""");

			handle.execute("""
                    create table audit (
                      id identity primary key,
                      action varchar(50),
                      ts timestamp default current_timestamp,
                      todo_id bigint
                    )""");
		});

		// 3. DAO onDemand – JDBI tworzy implementacje w locie
		TodoDao todoDao = jdbi.onDemand(TodoDao.class);
		AuditDao auditDao = jdbi.onDemand(AuditDao.class);

		Todo dataToInsert = new Todo(0, "Nagrać demo", LocalDate.now().plusDays(1), Status.OPEN);

		// 4. Wstawienie zadania + wpis w audycie (jedna transakcja)
		long id = todoDao.insertWithAudit(dataToInsert, auditDao);

		System.out.println("Saved id=" + id);
		System.out.println(todoDao.list());
	}
}