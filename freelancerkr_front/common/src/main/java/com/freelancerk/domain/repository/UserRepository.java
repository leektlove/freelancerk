package com.freelancerk.domain.repository;

import com.freelancerk.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    User findByUsername(String username);
    User findByUsernameAndFpUser(String username, String fpUser);
    User findByUid(String uid);

    List<User> findByRolesContaining(String role);

    Page<User> findByRolesContaining(String role, Pageable pageable);

    int countByEmailAndIdNotAndLeaveAtIsNull(String email, Long userId);
    int countByEmailAndIdNotAndLeaveAtIsNullAndFpUser(String email, Long userId, String fpUser);
    
    int countByNicknameAndIdNotAndLeaveAtIsNull(String nickname, Long userId);
    int countByNicknameAndIdNotAndLeaveAtIsNullAndFpUser(String nickname, Long userId, String fpUser);

    int countByUsername(String username);

    @Transactional
    @Modifying
    @Query("update User u set u.passwordFailCnt = u.passwordFailCnt+1 where u.id =:id")
    void updatePasswordFailCnt(@Param("id") Long id);

    @Transactional
    @Modifying
    @Query("update User u set u.passwordFailCnt = 0 where u.id =:id")
    void resetPasswordFailCnt(@Param("id") Long id);

    int countByUsernameAndAuthType(String email, User.AuthType authType);
    
    int countByUsernameAndAuthTypeAndFpUser(String email, User.AuthType authType, String fpUser);

    User findTop1ByEmailAndLeaveAtIsNull(String email);

    List<User> findByIdGreaterThan(long l);

    int countByEmailAndLeaveAtIsNull(String email);
    int countByEmailAndLeaveAtIsNullAndFpUser(String email,String fpUser);

    User findTop1ByEmailAndLeaveAtIsNullAndAuthType(String email, User.AuthType authType);

    List<User> findByEmailAndLeaveAtIsNull(String email);

    List<User> findByLeavedFalse();

    @Query(nativeQuery = true, value = "select name, email, cellphone, receive_email from user where id in (select user_id from user_categories where categories_id in (select id from category where name = :keyword)) and leaved = 0")
    List<Object[]> getUserInfoByKeyword(@Param("keyword") String keyword);

    @Query(nativeQuery = true, value = "SELECT id from user where roles like '%FREELANCER%' and created_at > '2019-08-18' and created_at < '2019-08-20' and auth_type != 'EMAIL'")
    List<BigInteger> findStrangeFreelancer();
	
}
