package com.freelancerk.controller;

import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;

@Api("프리랜서")
@RestController
public class FreelancerController {

//    private PickMeUpRepository PickMeUpRepository;
//
//    @Autowired
//    public FreelancerController(PickMeUpRepository PickMeUpRepository) {
//        this.PickMeUpRepository = PickMeUpRepository;
//    }
//
//    @ApiOperation("프리랜서 정보 수정")
//    @RequestMapping(method = RequestMethod.POST, value = "/users/freelancers/modifications")
//    public CommonResponse editFreelancer() {
//        // todo
//        return CommonResponse.ok();
//    }

//    @ApiOperation("내 포트폴리오 조회")
//    @RequestMapping(method = RequestMethod.GET, value = "/users/PickMeUps")
//    public CommonListResponse<List<PickMeUp>> getPickMeUps(@RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
//                                                             @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) {
//        Page<PickMeUp> page = PickMeUpRepository.findAll(PageRequest.of(pageNumber, pageSize, new Sort(Sort.Direction.DESC, "id")));
//        return new CommonListResponse.Builder<List<PickMeUp>>()
//                .totalCount(page.getTotalElements())
//                .totalPages(page.getTotalPages())
//                .currentPage(pageNumber)
//                .data(page.getContent()).build();
//    }
//
//    @ApiOperation("내 픽미업 조회")
//    @RequestMapping(method = RequestMethod.GET, value = "/users/pick-me-ups")
//    public CommonListResponse<List<PickMeUp>> getPickMeUps(@RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
//                                                           @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) {
//        return new CommonListResponse();
//    }
}
