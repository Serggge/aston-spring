package ru.serggge.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import ru.serggge.entity.Mail;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MailRepository extends CrudRepository<Mail, UUID> {

    List<Mail> findAll(Pageable page);

    Optional<Mail> findByEmail(String email);
}