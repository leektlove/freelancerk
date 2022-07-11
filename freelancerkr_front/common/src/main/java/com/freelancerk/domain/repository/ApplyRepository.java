package com.freelancerk.domain.repository;

import com.freelancerk.domain.Apply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplyRepository extends JpaRepository<Apply, Long> {
//    List<EmailLog> findByIdGreaterThan(long l);
	List<Apply> findByWork(String work);
	List<Apply> findByWorkAndPass1(String work,String pass1);
	List<Apply> findByWorkAndPass2(String work,String pass2);
	List<Apply> findByWorkAndPass3(String work,String pass3);
	List<Apply> findByUidAndWork(String uid, String work);
}
