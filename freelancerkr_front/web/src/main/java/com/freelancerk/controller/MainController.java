package com.freelancerk.controller;

import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Api(tags = "메인", description = "픽미업/프로젝트/컨테스트 리스트/유저 수 등")
@RestController
public class MainController {
//
//	private UserRepository userRepository;
//	private ProjectRepository projectRepository;
//	private ProjectPaymentRepository projectPaymentRepository;
//	private PickMeUpRepository pickMeUpRepository;
//	private ContestRepository contestRepository;
//
//	@Autowired
//	public MainController(UserRepository userRepository, ProjectRepository projectRepository,
//			ProjectPaymentRepository projectPaymentRepository, PickMeUpRepository pickMeUpRepository,
//			ContestRepository contestRepository) {
//		this.userRepository = userRepository;
//		this.projectRepository = projectRepository;
//		this.projectPaymentRepository = projectPaymentRepository;
//		this.pickMeUpRepository = pickMeUpRepository;
//		this.contestRepository = contestRepository;
//	}
//
//	@ApiOperation("통계")
//	@RequestMapping(method = RequestMethod.GET, value = "/main/stats")
//	public CommonResponse<StatModel> getMainStats() {
//		StatModel statModel = new StatModel();
//		statModel.setTotalUsers(userRepository.count());
//		statModel.setTotalJobPosted(projectRepository.count());
//		return new CommonResponse.Builder<StatModel>().data(statModel).build();
//	}
//
////	@ApiOperation("메인 - 픽미업 조회")
////	@RequestMapping(method = RequestMethod.GET, value = "/main/pick-me-ups")
////	public CommonListResponse<List<PickMeUp>> getMainPickMeUps(
////			@RequestParam(value = "type", required = false, defaultValue = "PRIORITY") MainType type,
////			@RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
////			@RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) {
////		Page<PickMeUp> page = MainType.PRIORITY.equals(type)
////				? pickMeUpRepository.findByPickMeUpOptionsContaining(PaymentPolicy.PickMeUp.DEFAULT_PRIORITY.name(),
////						PageRequest.of(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "id")))
////				: pickMeUpRepository.findByPickMeUpOptionsNotContaining(PaymentPolicy.PickMeUp.DEFAULT_PRIORITY.name(),
////						PageRequest.of(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "id")));
////		return new CommonListResponse.Builder<List<PickMeUp>>().totalCount(page.getTotalElements())
////				.currentPage(pageNumber).totalPages(page.getTotalPages()).data(page.getContent()).build();
////	}
//
//	@ApiOperation("메인 - 프로젝트 조회")
//	@RequestMapping(method = RequestMethod.GET, value = "/main/projects")
//	public CommonListResponse<List<ProjectPayment>> getMainProjects(
//			@RequestParam(value = "type", required = false, defaultValue = "PRIORITY") MainType type,
//			@RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
//			@RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) {
//		Page<ProjectPayment> page = MainType.PRIORITY.equals(type)
//				? projectPaymentRepository.findByProjectOptionsContaining(
//						PaymentPolicy.Project.EXPOSE_EXTERNAL_PRIORITY.name(),
//						PageRequest.of(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "id")))
//				: projectPaymentRepository.findByProjectOptionsNotContaining(
//						PaymentPolicy.Project.EXPOSE_EXTERNAL_PRIORITY.name(),
//						PageRequest.of(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "id")));
//		return new CommonListResponse.Builder<List<ProjectPayment>>().totalCount(page.getTotalElements())
//				.currentPage(pageNumber).totalPages(page.getTotalPages()).data(page.getContent()).build();
//	}
//
//	@ApiOperation("메인 - 컨테스트 조회")
//	@RequestMapping(method = RequestMethod.GET, value = "/main/contests")
//	public CommonListResponse<List<Contest>> getMainContests(
//			@RequestParam(value = "type", required = false, defaultValue = "PRIORITY") MainType type,
//			@RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
//			@RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) {
//		Page<Contest> page = MainType.PRIORITY.equals(type)
//				? contestRepository.findByContestOptionsContaining(
//						PaymentPolicy.Contest.EXPOSE_EXTERNAL_PRIORITY.name(),
//						PageRequest.of(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "id")))
//				: contestRepository.findByContestOptionsNotContaining(
//						PaymentPolicy.Contest.EXPOSE_EXTERNAL_PRIORITY.name(),
//						PageRequest.of(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "id")));
//		return new CommonListResponse.Builder<List<Contest>>().totalCount(page.getTotalElements())
//				.currentPage(pageNumber).totalPages(page.getTotalPages()).data(page.getContent()).build();
//	}
}
