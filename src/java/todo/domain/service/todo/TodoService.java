/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package todo.domain.service.todo;

import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import todo.domain.common.exception.BusinessException;
import todo.domain.common.exception.ResourceNotFoundException;
import todo.domain.model.Todo;

@Stateless
public class TodoService {

    private static final long MAX_UNFINISHED_COUNT = 5;
    
    @PersistenceContext
    protected EntityManager entityManager;
    
    public List<Todo> findAll() {
        TypedQuery<Todo> q 
                = entityManager.createNamedQuery("Todo.findAll", Todo.class);
        
        return q.getResultList();
    }
    
    public Todo findOne(Integer todoId) {
        // 主キーを指定することで、finde メソッドで検索出来る
        // JPQL, SQL は不要
        Todo todo = entityManager.find(Todo.class, todoId);
        if (todo == null) {
            throw new ResourceNotFoundException("[E404] The requested Todo is not found. (id=" + todoId + ")");
        }
        return todo;
    }
    
    public Todo create(Todo todo) {
        
        // 毎回 JPQL をコンパイルするオーバーヘッドがある
        // NamedQuery として定義した方が高性能
        // リクエストによってクエリが変かする場合を除き、
        // NamedQuery を使用した方が良い
        TypedQuery<Long> q = entityManager
                .createQuery("SELECT COUNT(x) FROM Todo x WHERE x.finished = :finished", Long.class)
                .setParameter("finished", false);
        
        long unfinishedCount = q.getSingleResult();
        if (unfinishedCount >= MAX_UNFINISHED_COUNT) {
            throw new BusinessException("[E01] The count of unfinished Todo must not be over"
                    + MAX_UNFINISHED_COUNT + ".");
        }
        todo.setFinished(false);
        todo.setCreatedAt(new Date());
        
        // EntityManager の管理下に置く
        // メソッド終了時のトランザクションコミットのタイミングで、
        // DB に insert 文が実行される（insert 書く必要はない）
        entityManager.persist(todo);
        return todo;
    }
    
    public Todo finish(Integer todoId) {
        Todo todo = findOne(todoId);
        if (todo.isFinished()) {
            throw new BusinessException("[E02] The requested Todo is already finished. (id=" + todoId + ")");
        }
        
        todo.setFinished(true);
        // update 文が実行される
        // find の段階で EntityManager の管理下になるので、
        // merge を実行しなくても更新される
        entityManager.merge(todo);
        return todo;
    }
    
    public void delete(Integer todoId) {
        Todo todo = findOne(todoId);
        // delete 文が実行される
        entityManager.remove(todo);
    }
    
}
