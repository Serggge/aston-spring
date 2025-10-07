package ru.serggge.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.serggge.entity.Mail;
import java.util.Collection;
import java.util.List;

public interface MailRepository extends JpaRepository<Mail, Long> {

    @Query("""
            SELECT m
            FROM Mail m
            WHERE m.email LIKE :mail
            """)
    Slice<Mail> findByMail(@Param("mail") String mail, Pageable pageRequest);

    @Modifying
    @Query("""
            UPDATE Mail m
            SET m.isDelivered = true
            WHERE m.id IN :ids
            """)
    void markAsDelivered(@Param("ids") Collection<Long> ids);

    @Query("""
            SELECT m
            FROM Mail m
            WHERE m.isDelivered = false
            """)
    List<Mail> findUnsent();
}
