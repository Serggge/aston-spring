package ru.serggge.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.serggge.entity.Mail;
import java.util.List;

@Repository
public interface MailRepository extends CrudRepository<Mail, String> {

    List<Mail> findAll(Pageable page);

}