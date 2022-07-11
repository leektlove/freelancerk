package com.freelancerk.service.impl;

import com.freelancerk.domain.ClientPointLog;
import com.freelancerk.domain.FreelancerPointLog;
import com.freelancerk.domain.User;
import com.freelancerk.domain.repository.*;
import com.freelancerk.exception.PointShortageException;
import com.freelancerk.service.BaseService;
import com.freelancerk.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserServiceImpl extends BaseService implements UserService {

    private UserRepository userRepository;
    private ProjectRepository projectRepository;
    private PickMeUpRepository pickMeUpRepository;
    private ClientPointLogRepository clientPointLogRepository;
    private FreelancerPointLogRepository freelancerPointLogRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, ProjectRepository projectRepository,
                           PickMeUpRepository pickMeUpRepository, ClientPointLogRepository clientPointLogRepository,
                           FreelancerPointLogRepository freelancerPointLogRepository) {
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.pickMeUpRepository = pickMeUpRepository;
        this.clientPointLogRepository = clientPointLogRepository;
        this.freelancerPointLogRepository = freelancerPointLogRepository;
    }

    @Transactional
    @Override
    public void usePoint(Long userId, Integer pointUsage, String reason) {
        User user = userRepository.getOne(userId);
        if (user.getPoints() < pointUsage) {
            throw PointShortageException.getInstance();
        }
        user.setPoints(user.getPoints() - pointUsage);

        ClientPointLog pointLog = new ClientPointLog();
        pointLog.setAmount(pointUsage);
        pointLog.setCreatedAt(LocalDateTime.now());
        pointLog.setUser(user);
        pointLog.setReason(reason);
        clientPointLogRepository.save(pointLog);
    }

    @Override
    public User getById(Long userId) {
        return userRepository.getOne(userId);
    }

    @Override
    public void save(User user) {
        userRepository.save(user);
    }

    @Transactional
    @Override
    public User getCurrentUser() {
    	return getById(getSessionUserId());
    }

    @Override
    public long getPoints(long userId, User.Role role) {
        if (User.Role.ROLE_FREELANCER.equals(role)) {
            User user = userRepository.getOne(userId);
            List<FreelancerPointLog> pointLogs = freelancerPointLogRepository.findByUserIdAndAddedPointExpiredAtGreaterThan(user.getId(), LocalDateTime.now());
            return pointLogs.stream().mapToLong(FreelancerPointLog::getAddedPoint).sum() - pointLogs.stream().mapToLong(FreelancerPointLog::getUsedPoint).sum();
        } else {
            User user = userRepository.getOne(userId);
            List<ClientPointLog> pointLogs = clientPointLogRepository.findByUserIdAndAddedPointExpiredAtGreaterThan(user.getId(), LocalDateTime.now());
            return pointLogs.stream().mapToLong(ClientPointLog::getAddedPoint).sum() - pointLogs.stream().mapToLong(ClientPointLog::getUsedPoint).sum();
        }
    }

    @Transactional
    @Override
    public void deleteContents(Long userId) {
        pickMeUpRepository.invalidateByUserId(userId);
        projectRepository.invalidateByUserId(userId);
    }
}
