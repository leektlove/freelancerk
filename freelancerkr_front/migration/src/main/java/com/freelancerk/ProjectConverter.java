package com.freelancerk;

import com.freelancerk.domain.Project;
import com.freelancerk.legacy.domain.FrProject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class ProjectConverter {

    public static void convertBudget(Project project, String fpHope2) {
        if (StringUtils.isEmpty(fpHope2)) {
            return;
        }

        if ("잘 모르겠음".equalsIgnoreCase(fpHope2.trim())) {
            project.setBudgetFrom(null);
            project.setBudgetTo(null);

            return;
        }

        String[] splittedBudgetArr = fpHope2.split("\\s+");
        if (splittedBudgetArr.length < 2) {
            return;
        }

        if (splittedBudgetArr[1].contains("~")) {
            String[] budgetFromToArr = splittedBudgetArr[1].split("~");
            project.setBudgetFrom(Integer.parseInt(budgetFromToArr[0].replaceAll("\\D+","")));
            project.setBudgetTo(Integer.parseInt(budgetFromToArr[1].replaceAll("\\D+","")));
        } else {
            project.setBudgetFrom(Integer.parseInt(splittedBudgetArr[1].replaceAll("\\D+","")));
        }

        return;
    }

    public static void convertPeriod(Project project, String fpHope1) {
        if (EnumUtils.isValidEnum(Project.ExpectedPeriod.class, fpHope1)) {
            project.setExpectedPeriod(Project.ExpectedPeriod.valueOf(fpHope1));
            return;
        }
        if ("1개월 ~ 3개월".equalsIgnoreCase(fpHope1)) {
            project.setExpectedPeriod(Project.ExpectedPeriod.UNDER_2MONTH);
            return;
        }
        if ("3개월 ~ 6개월".equalsIgnoreCase(fpHope1)) {
            project.setExpectedPeriod(Project.ExpectedPeriod.UNDER_4MONTH);
            return;
        }
        if ("1개월 이하".equalsIgnoreCase(fpHope1)) {
            project.setExpectedPeriod(Project.ExpectedPeriod.UNDER_AMONTH);
            return;
        }
    }

    public static void convertWorkPlace(Project project, String fpHope4) {
        if ("Off-Line 선호".equalsIgnoreCase(fpHope4)) {
            project.setWorkPlace(Project.WorkPlace.OFF_LINE);
        } else if ("On-line 선호".equalsIgnoreCase(fpHope4)) {
            project.setWorkPlace(Project.WorkPlace.ON_LINE);
        } else if ("잘 모르겠음".equalsIgnoreCase(fpHope4)) {
            project.setWorkPlace(Project.WorkPlace.NO_IDEA);
        }
    }

    public static void convertPayCriteria(Project project, String fpHope3) {
        if (EnumUtils.isValidEnum(Project.PayCriteria.class, fpHope3)) {
            project.setPayCriteria(Project.PayCriteria.valueOf(fpHope3));
            return;
        }

        if ("프리랜서와 협의".equalsIgnoreCase(fpHope3)) {
            project.setPayCriteria(Project.PayCriteria.AGREEMENT);
        } else if ("100% 완성 후 지급".equalsIgnoreCase(fpHope3)) {
            project.setPayCriteria(Project.PayCriteria.WORK_OVER);
        }
    }

    public static void convertPostingPeriod(Project project, FrProject frProject, String fpTerm) {
        project.setPostingStartAt(frProject.getFpDisDate());
        if ("1 week".equalsIgnoreCase(fpTerm)) {
            project.setPostingEndAt(project.getPostingStartAt().plusWeeks(1));
        } else if ("2 week".equalsIgnoreCase(fpTerm)) {
            project.setPostingEndAt(project.getPostingStartAt().plusWeeks(2));
        } else if ("1 month".equalsIgnoreCase(fpTerm)) {
            project.setPostingEndAt(project.getPostingStartAt().plusMinutes(1));
        } else if ("2 month".equalsIgnoreCase(fpTerm)) {
            project.setPostingEndAt(project.getPostingStartAt().plusMinutes(2));
        }
    }
}
