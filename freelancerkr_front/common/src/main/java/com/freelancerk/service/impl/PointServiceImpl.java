package com.freelancerk.service.impl;

import com.freelancerk.domain.*;
import com.freelancerk.domain.repository.ClientPointLogRepository;
import com.freelancerk.domain.repository.FreelancerPointLogRepository;
import com.freelancerk.domain.repository.UserRepository;
import com.freelancerk.exception.PointShortageException;
import com.freelancerk.service.PointService;
import com.freelancerk.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
public class PointServiceImpl implements PointService {

	private UserService userService;
    private UserRepository userRepository;
    private ClientPointLogRepository clientPointLogRepository;
    private FreelancerPointLogRepository freelancerPointLogRepository;

    @Autowired
    public PointServiceImpl(UserService userService, UserRepository userRepository, ClientPointLogRepository clientPointLogRepository,
							FreelancerPointLogRepository freelancerPointLogRepository) {
    	this.userService = userService;
        this.userRepository = userRepository;
        this.clientPointLogRepository = clientPointLogRepository;
		this.freelancerPointLogRepository = freelancerPointLogRepository;
    }

    @Transactional
  	@Override
	public void getRidOfPointsFromClient(Long userId, Integer points, String reason, Purchase purchase, boolean minusAvailable) {
    	if (points == null || points == 0) return;
    	long currentPoint = userService.getPoints(userId, User.Role.ROLE_CLIENT);
    	if (minusAvailable && currentPoint < points) throw PointShortageException.getInstance();
		long remainPointForUse = points;
    	while (remainPointForUse > 0) {
			ClientPointLog pointLog = clientPointLogRepository.findTop1ByUserIdAndAddedPointExpiredAtGreaterThanAndRemainPointGreaterThanOrderByIdAsc(userId, LocalDateTime.now(), 0);
			if (pointLog == null) {
				log.warn("<<< no point for subtraction. current point: {}, need point: {}", currentPoint, points);
				break;
			}
			if (pointLog.getRemainPoint() >= remainPointForUse) {
				pointLog.setUsedPoint(pointLog.getUsedPoint() + remainPointForUse);
				pointLog.setRemainPoint(pointLog.getRemainPoint() - remainPointForUse);
				remainPointForUse = 0;
			} else {
				pointLog.setUsedPoint(pointLog.getAddedPoint());
				pointLog.setRemainPoint(0);
				remainPointForUse -= pointLog.getRemainPoint();
			}

			clientPointLogRepository.save(pointLog);
		}

    	ClientPointLog clientPointLog = new ClientPointLog();
    	clientPointLog.setUser(userRepository.getOne(userId));
    	clientPointLog.setPurchase(purchase);
    	clientPointLog.setUsePoint(points);
    	clientPointLog.setProject(purchase.getProject());
    	clientPointLog.setPriorPoints(currentPoint);
    	clientPointLog.setAfterPoints(currentPoint -  points);
    	clientPointLogRepository.save(clientPointLog);
	}

	@Override
	public void getRidOfPointsFromFreelancerForPickMeUp(Long userId, Integer points, Purchase purchase) {
		if (points == null || points == 0) return;
		long currentPoint = userService.getPoints(userId, User.Role.ROLE_FREELANCER);
		if (currentPoint < points) throw PointShortageException.getInstance();
		long remainPointForUse = points;
		while (remainPointForUse > 0) {
			FreelancerPointLog pointLog = freelancerPointLogRepository.findTop1ByUserIdAndAddedPointExpiredAtGreaterThanAndRemainPointGreaterThanOrderByIdAsc(userId, LocalDateTime.now(), 0);
			if (pointLog == null) {
				log.warn("<<< no point for subtraction. current point: {}, need point: {}", currentPoint, points);
				break;
			}
			if (pointLog.getRemainPoint() >= remainPointForUse) {
				pointLog.setUsedPoint(pointLog.getUsedPoint() + remainPointForUse);
				pointLog.setRemainPoint(pointLog.getRemainPoint() - remainPointForUse);
				remainPointForUse = 0;
			} else {
				pointLog.setUsedPoint(pointLog.getAddedPoint());
				pointLog.setRemainPoint(0);
				remainPointForUse -= pointLog.getRemainPoint();
			}

			freelancerPointLogRepository.save(pointLog);
		}
	}

	@Override
	public void getRidOfPointsFromFreelancerForContestEntry(Long userId, Integer points, Purchase purchase) {
		if (points == null || points == 0) return;
		long currentPoint = userService.getPoints(userId, User.Role.ROLE_FREELANCER);
		if (currentPoint < points) throw PointShortageException.getInstance();
		long remainPointForUse = points;
		while (remainPointForUse > 0) {
			FreelancerPointLog pointLog = freelancerPointLogRepository.findTop1ByUserIdAndAddedPointExpiredAtGreaterThanAndRemainPointGreaterThanOrderByIdAsc(userId, LocalDateTime.now(), 0);
			if (pointLog == null) {
				log.warn("<<< no point for subtraction. current point: {}, need point: {}", currentPoint, points);
				break;
			}
			if (pointLog.getRemainPoint() >= remainPointForUse) {
				pointLog.setUsedPoint(pointLog.getUsedPoint() + remainPointForUse);
				pointLog.setRemainPoint(pointLog.getRemainPoint() - remainPointForUse);
				remainPointForUse = 0;
			} else {
				pointLog.setUsedPoint(pointLog.getAddedPoint());
				pointLog.setRemainPoint(0);
				remainPointForUse -= pointLog.getRemainPoint();
			}

			freelancerPointLogRepository.save(pointLog);
		}

		FreelancerPointLog log = new FreelancerPointLog();
		log.setUsePoint(points);
		log.setPurchase(purchase);
		log.setType(FreelancerPointLog.Type.CONTEST_ENTRY);
		log.setReason("컨테스트 작품 제출");
		freelancerPointLogRepository.save(log);
	}

	@Override
	public void givePointsToClient(Long userId, Integer points, String reason, Purchase purchase) {
		if (points == null || points == 0) return;
		long currentPoint = userService.getPoints(userId, User.Role.ROLE_FREELANCER);
		User user = userRepository.getOne(userId);
		long afterPoints = currentPoint + points;

		ClientPointLog pointLog = new ClientPointLog();
		pointLog.setReason(reason);
		pointLog.setUser(user);
		pointLog.setCreatedAt(LocalDateTime.now());
		pointLog.setAmount(points);
		pointLog.setAddedPoint(points);
		pointLog.setRemainPoint(points);
		pointLog.setAfterPoints(afterPoints);
		pointLog.setPriorPoints(currentPoint);
		pointLog.setPurchase(purchase);
		pointLog.setAddedPointExpiredAt(LocalDateTime.now().plusYears(1));

		clientPointLogRepository.save(pointLog);
	}

	@Override
	public void givePointsToClientExpiredAt(Long userId, Integer points, String reason, LocalDateTime expiredAt) {
		if (points == null || points == 0) return;
		long currentPoint = userService.getPoints(userId, User.Role.ROLE_FREELANCER);
		User user = userRepository.getOne(userId);
		long afterPoints = currentPoint + points;

		ClientPointLog pointLog = new ClientPointLog();
		pointLog.setReason(reason);
		pointLog.setUser(user);
		pointLog.setCreatedAt(LocalDateTime.now());
		pointLog.setAmount(points);
		pointLog.setAddedPoint(points);
		pointLog.setRemainPoint(points);
		pointLog.setAfterPoints(afterPoints);
		pointLog.setPriorPoints(currentPoint);
		pointLog.setAddedPointExpiredAt(expiredAt);

		clientPointLogRepository.save(pointLog);
	}

	@Transactional
	@Override
	public void givePointsToFreelancerForPickMeUp(User user, int usedPoints, int addedPoint, String reason, PickMeUp pickMeUp, Purchase purchase) {
		if (usedPoints == 0 && addedPoint == 0) return;
		long currentPoint = userService.getPoints(user.getId(), User.Role.ROLE_FREELANCER);
		long afterPoints = currentPoint + addedPoint;

		FreelancerPointLog freelancerPointLog = new FreelancerPointLog();
		freelancerPointLog.setAddedPoint(addedPoint);
		freelancerPointLog.setRemainPoint(addedPoint);
		freelancerPointLog.setAddedPointExpiredAt(LocalDateTime.now().plusYears(1));
		freelancerPointLog.setCreatedAt(LocalDateTime.now());
		freelancerPointLog.setType(FreelancerPointLog.Type.PICK_ME_UP);
		freelancerPointLog.setUser(user);
		freelancerPointLog.setUsePoint(usedPoints);
		freelancerPointLog.setPriorPoints(currentPoint);
		freelancerPointLog.setAfterPoints(afterPoints);
		freelancerPointLog.setReason(reason);
		freelancerPointLog.setPurchase(purchase);
		freelancerPointLog.setPickMeUp(pickMeUp);
		freelancerPointLogRepository.save(freelancerPointLog);
	}

	@Override
	public void givePointsToFreelancerForContestEntry(User user, int point, String reason, Purchase purchase) {
		if (point == 0) return;
		long currentPoint = userService.getPoints(user.getId(), User.Role.ROLE_FREELANCER);
		long afterPoints = currentPoint + point;

		FreelancerPointLog freelancerPointLog = new FreelancerPointLog();
		freelancerPointLog.setAddedPoint(point);
		freelancerPointLog.setRemainPoint(point);
		freelancerPointLog.setAddedPointExpiredAt(LocalDateTime.now().plusYears(1));
		freelancerPointLog.setCreatedAt(LocalDateTime.now());
		freelancerPointLog.setContest(purchase.getProject());
		freelancerPointLog.setType(FreelancerPointLog.Type.CONTEST_ENTRY);
		freelancerPointLog.setUser(purchase.getUser());
		freelancerPointLog.setPriorPoints(point);
		freelancerPointLog.setAfterPoints(afterPoints);
		freelancerPointLog.setReason(reason);
		freelancerPointLog.setPurchase(purchase);
		freelancerPointLog.setContest(purchase.getProject());
		freelancerPointLogRepository.save(freelancerPointLog);
	}

	@Override
	public void givePointsToFreelancerForEtcExpiredAt(User user, int point, String reason, LocalDateTime expiredAt) {
		if (point == 0) return;
		long currentPoint = userService.getPoints(user.getId(), User.Role.ROLE_FREELANCER);
		long afterPoints = currentPoint + point;

		FreelancerPointLog freelancerPointLog = new FreelancerPointLog();
		freelancerPointLog.setAddedPoint(point);
		freelancerPointLog.setRemainPoint(point);
		freelancerPointLog.setAddedPointExpiredAt(expiredAt);
		freelancerPointLog.setCreatedAt(LocalDateTime.now());
		freelancerPointLog.setType(FreelancerPointLog.Type.ETC);
		freelancerPointLog.setPriorPoints(point);
		freelancerPointLog.setAfterPoints(afterPoints);
		freelancerPointLog.setReason(reason);
		freelancerPointLog.setUser(user);
		freelancerPointLogRepository.save(freelancerPointLog);
	}

	@Override
	public void givePointsToFreelancerForEtc(User user, int point, String reason) {
		if (point == 0) return;
		long currentPoint = userService.getPoints(user.getId(), User.Role.ROLE_FREELANCER);
		long afterPoints = currentPoint + point;

		FreelancerPointLog freelancerPointLog = new FreelancerPointLog();
		freelancerPointLog.setAddedPoint(point);
		freelancerPointLog.setRemainPoint(point);
		freelancerPointLog.setAddedPointExpiredAt(LocalDateTime.now().plusYears(1));
		freelancerPointLog.setCreatedAt(LocalDateTime.now());
		freelancerPointLog.setType(FreelancerPointLog.Type.ETC);
		freelancerPointLog.setPriorPoints(currentPoint);
		freelancerPointLog.setAfterPoints(afterPoints);
		freelancerPointLog.setReason(reason);
		freelancerPointLog.setUser(user);
		freelancerPointLogRepository.save(freelancerPointLog);
	}

	@Transactional
	@Override
	public void givePointsToFreelancerForContestRefund(Long userId, int points, String reason, Project project) {
    	if (points == 0) return;
		long currentPoint = userService.getPoints(userId, User.Role.ROLE_FREELANCER);
		long afterPoints = currentPoint + points;

		FreelancerPointLog freelancerPointLog = new FreelancerPointLog();
		freelancerPointLog.setAddedPoint(points);
		freelancerPointLog.setRemainPoint(points);
		freelancerPointLog.setUsedPoint(0);
		freelancerPointLog.setAddedPointExpiredAt(LocalDateTime.now().plusYears(1));
		freelancerPointLog.setCreatedAt(LocalDateTime.now());
		freelancerPointLog.setContest(project);
		freelancerPointLog.setPriorPoints(currentPoint);
		freelancerPointLog.setAfterPoints(afterPoints);
		freelancerPointLog.setReason(reason);
		freelancerPointLog.setType(FreelancerPointLog.Type.REFUND_FOR_CONTEST_ENTRY);
		freelancerPointLog.setUser(userRepository.getOne(userId));
		freelancerPointLogRepository.save(freelancerPointLog);
	}
}
