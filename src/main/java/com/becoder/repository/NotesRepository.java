package com.becoder.repository;

import com.becoder.entity.Notes;
import com.becoder.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface NotesRepository extends JpaRepository<Notes,Integer> {

    public List<Notes> findByUser(User user);

    List<Notes> findByCategoryAndUser(String category, User user);

    @Query("SELECT DISTINCT n.category FROM Notes n")
    List<String> findAllCategories();

    Optional<Notes> findById(int id);

    List<Notes> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndUser(String query, String query1, User user);
}
