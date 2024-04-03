package com.becoder.repository;

import com.becoder.entity.Notes;
import com.becoder.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotesRepository extends JpaRepository<Notes,Integer> {

    public List<Notes> findByUser(User user);
}
