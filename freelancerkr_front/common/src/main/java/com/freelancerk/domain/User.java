package com.freelancerk.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@EqualsAndHashCode(exclude = {"userSkillList", "userCertificationList", "userCareerList", "categories", "userJobPreference"})
@ToString(exclude = {"userSkillList", "userCertificationList", "userCareerList", "categories", "userJobPreference"})
@Data
@Entity
public class User implements UserDetails, Authentication {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String username;

	private String uid;
	private String thirdPartyUserId;
	private String thirdPartyAccessToken;
	@JsonIgnore
	private String password;
	private int passwordFailCnt = 0;
	private LocalDateTime passwordFailedAt;
	private String nickname;
	private String fpUser;
	private String name;
	private String realName;
	private String profileImageUrl;
	private String profileImageUrl1;
	private String profileImageUrl2;
	private String profileImageUrl3;
	private String profileImageUrl4;
	private String profileImageUrl5;
	private String profileImageUrl6;
	private String agreeFileUrl;
	private String logoImageUrl;
	private String businessLicenseUrl;
	private String businessLicenseFileName;

	private String registrationNumber;

	private int level;
	private Integer careerYear;
	private String roles;
	@Enumerated(EnumType.STRING)
	private ExposeType exposeType = ExposeType.NAME;
	@Enumerated(EnumType.STRING)
	private BusinessType businessType;
	@Enumerated(EnumType.STRING)
	private TaxType taxType;
	@ManyToOne
	private BankType bankForReceivingPayment;
	private String bankAccountForReceivingPayment;
	private String bankAccountName;
	private String corporateName;
	private String corporateNumber;
	@Enumerated(EnumType.STRING)
	private AuthType authType;
	private int balance;
	private int escrowPrice;
	
	private String email;
	private boolean receiveEmail;
	private boolean exposeEmail;

	private String cellphone;
	private boolean exposeCellphone;
	private boolean cellphoneCertified;

	private boolean useEscrow;

	
	private boolean exposeSns;
	private String facebookLinkUrl;
	private String instagramLinkUrl;
	private String twitterLinkUrl;
	private String youtubeLinkUrl;
	private String linkedInLinkUrl;
	private String githubLinkUrl;
	private String homepageUrl;
	private String blogLinkUrl;

	@Column(length = 1000)
	private String myClientInfo;
	// 나의 소개
	@Column(columnDefinition = "TEXT")
	private String aboutMe;
	private String aboutMeFileName;
	private String aboutMeFileUrl;

	private LocalDate certifiedBirth;
	private String certifiedGender;
	private String certifiedName;
	private boolean legacyUser;

	@JsonIgnore
	@CreationTimestamp
	private LocalDateTime createdAt;
	@UpdateTimestamp
	private LocalDateTime updatedAt;

//	@Formula("(select coalesce(count(*), 0) from project as p where p.contracted_freelancer_id = id )")
	@Formula("(select coalesce(count(*), 0) from project_bid as p where p.participant_id = id and p.bid_status = 'PICKED' )")
	private int contractedProjectCount;
	@Formula("(select coalesce(count(*), 0) from project_rate as r left join project as p on r.project_id = p.id where r.rated_user_id = id and r.rater_type = 'CLIENT' and p.status = 'COMPLETED')")
	private int ratedProjectCountAsFreelancer;
	@Formula("(select coalesce(sum((r.type1rate + r.type2rate + r.type3rate + r.type4rate + r.type5rate))/5/count(*), 0) from project_rate as r left join project as p on r.project_id = p.id where r.rated_user_id = id and r.rater_type = 'CLIENT' and p.status = 'COMPLETED')")
	private float ratingsAsFreelancer;

	@Formula("(select coalesce(count(*), 0) from project as p where p.posting_client_id = id and p.status = 'COMPLETED')")
	private int completedProjectCount;
	@Formula("(select coalesce(count(*), 0) from project_rate as r left join project as p on r.project_id = p.id where r.rated_user_id = id and r.rater_type = 'FREELANCER' and p.status = 'COMPLETED')")
	private int ratedProjectCountAsClient;
	@Formula("(select coalesce(sum((r.type1rate + r.type2rate + r.type3rate + r.type4rate + r.type5rate))/5/count(*), 0) from project_rate as r left join project as p on r.project_id = p.id where r.rated_user_id = id and r.rater_type = 'FREELANCER' and p.status = 'COMPLETED')")
	private float ratingsAsClient;

	@Transient
	private long totalTransactionAmount;
	@Transient
	private long totalTransactionCount;
	@Transient
	private long numberOfAccessCount;
	@Transient
	private long numberOfProjectCount;
	@Transient
	private long numberOfContestCount;
	@Transient
	private long numberOfProjectPropositionCount;
	@Transient
	private long numberOfDirectDealCount;
	@Transient
	private long numberOfOptionCount;
	@Transient
	private long totalChargedOptionPrice;
	@Transient
	private long numberOfProjectBids;
	@Transient
	private long numberOfContestEntries;
	@Transient
	private long numberOfCompletedProject;

	@Transient
	private long points;

	@Transient
	public boolean alarm;
	@Transient
	public boolean message;
	@Transient
	public int escrowSum = 0;
	@Transient
	public boolean skillCertified;

	@JsonIgnore
	@OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	private UserJobPreference userJobPreference;

	// 보유 기술
    @JsonBackReference
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval=true)
	private List<UserSkill> userSkillList;

	// 자격 사항
    @JsonManagedReference
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval=true)
	private List<UserCertification> userCertificationList;

	// 경력 사항
    @JsonManagedReference
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval=true)
	private List<UserCareer> userCareerList;

	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	public Set<Category> categories;

	@Transient
	private String accessToken;
	@Transient
	private Role loginRole;
	@JsonIgnore
	private LocalDateTime leaveAt;
	private String leaveType;
	@Column(columnDefinition = "TEXT")
	private String leaveText;
	private boolean leaved;

	@Transient
	public boolean isFreelancer() {
		return roles != null && roles.contains(Role.ROLE_FREELANCER.name());
	}

	@Transient
	public boolean isClient() {
		return roles != null && roles.contains(Role.ROLE_CLIENT.name());
	}

	public enum ExposeType {
		NAME, NICKNAME
	}

	public enum Role {
		ROLE_FREELANCER, ROLE_CLIENT
	}

	public enum AuthType {
		EMAIL, FACEBOOK, KAKAO, NAVER
	}
	

	@Transient
	private String encodedPassword;
	@Transient
	private String token;

	@JsonIgnore
	@Transient
	private LocalDateTime lastAccessAt;

	@JsonIgnore
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> authorities = new HashSet<>();
		if (isFreelancer()) {
			authorities.add(new SimpleGrantedAuthority(Role.ROLE_FREELANCER.name()));
		}
		if (isClient()) {
			authorities.add(new SimpleGrantedAuthority(Role.ROLE_CLIENT.name()));
		}
		return authorities.isEmpty() ? null : authorities;
	}

	@JsonIgnore
	@Override
	public Object getCredentials() {
		return password;
	}

	@JsonIgnore
	@Override
	public User getDetails() {
		return this;
	}

	@JsonIgnore
	@Override
	public Object getPrincipal() {
		return username;
	}

	@JsonIgnore
	@Override
	public boolean isAuthenticated() {
		return true;
	}

	@JsonIgnore
	@Override
	public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

	}

	@JsonIgnore
	@Override
	public String getPassword() {
		return password;
	}

	@JsonIgnore
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@JsonIgnore
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@JsonIgnore
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@JsonIgnore
	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public int hashCode() {

		return Objects.hash(super.hashCode(), id, username, uid, password, nickname, name, email, cellphone,
				profileImageUrl, level, careerYear, roles, businessType, corporateName, corporateNumber, authType,
				accessToken, createdAt, encodedPassword, token);
	}

	public boolean isLoginAsClient() {
		return Role.ROLE_CLIENT.equals(loginRole);
	}

	public enum BusinessType {
		INDIVIDUAL("개인"), INDIV_BUSINESS("개인사업자"), CORP_BUSINESS("법인사업자");

		final private String name;

		private BusinessType(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}

	@Transient
	public String getProfileImageUrl() {
		if (StringUtils.isNotEmpty(profileImageUrl)) {
			if (profileImageUrl.startsWith("/")) {
				return "https://www.freelancerk.com" + profileImageUrl;
			}
		}
		return profileImageUrl;
	}
	@Transient
	public String getProfileImageUrl1() {
		if (StringUtils.isNotEmpty(profileImageUrl1)) {
			if (profileImageUrl1.startsWith("/")) {
				return "https://www.freelancerk.com" + profileImageUrl1;
			}
		}
		return profileImageUrl1;
	}
	@Transient
	public String getProfileImageUrl2() {
		if (StringUtils.isNotEmpty(profileImageUrl2)) {
			if (profileImageUrl2.startsWith("/")) {
				return "https://www.freelancerk.com" + profileImageUrl2;
			}
		}
		return profileImageUrl2;
	}
	@Transient
	public String getProfileImageUrl3() {
		if (StringUtils.isNotEmpty(profileImageUrl3)) {
			if (profileImageUrl3.startsWith("/")) {
				return "https://www.freelancerk.com" + profileImageUrl3;
			}
		}
		return profileImageUrl3;
	}
	@Transient
	public String getProfileImageUrl4() {
		if (StringUtils.isNotEmpty(profileImageUrl4)) {
			if (profileImageUrl4.startsWith("/")) {
				return "https://www.freelancerk.com" + profileImageUrl4;
			}
		}
		return profileImageUrl4;
	}
	@Transient
	public String getProfileImageUrl5() {
		if (StringUtils.isNotEmpty(profileImageUrl5)) {
			if (profileImageUrl5.startsWith("/")) {
				return "https://www.freelancerk.com" + profileImageUrl5;
			}
		}
		return profileImageUrl5;
	}
	@Transient
	public String getProfileImageUrl6() {
		if (StringUtils.isNotEmpty(profileImageUrl6)) {
			if (profileImageUrl6.startsWith("/")) {
				return "https://www.freelancerk.com" + profileImageUrl6;
			}
		}
		return profileImageUrl6;
	}


	@Transient
	public boolean isExposeName() {
		return exposeType != null && ExposeType.NAME.equals(exposeType);
	}

	@Transient
	public boolean isExposeNickname() {
		return exposeType != null && ExposeType.NICKNAME.equals(exposeType);
	}

	@Transient
	public boolean isAlarm() {
		return alarm;
	}

	@Transient
	public boolean isMessage() {
		return message;
	}

	@Transient
	public String getExposedEmail() {
		if (exposeEmail) {
			return email;
		}
		return "비공개";
	}

	@Transient
	public String getExposedCellphone() {
		if (exposeCellphone) {
			return cellphone;
		}
		return "비공개";
	}

	@Transient
	public String getExposeName() {
		if (isExposeName()) {
			return name;
		} else {
			return nickname;
		}
	}

	@Transient
	public Map<Category, List<Category>> getSectorsByParent() {
		Map<Category, List<Category>> parentSectorNameChildListMap = new HashMap<>();
		for (Category category : categories) {
			List<Category> childList = parentSectorNameChildListMap.get(category.getParentCategory());
			if (parentSectorNameChildListMap.containsKey(category.getParentCategory())) {
				childList.add(category);
			} else {
				List<Category> categories = new ArrayList<>();
				categories.add(category);
				parentSectorNameChildListMap.put(category.getParentCategory(), categories);
			}
		}
		return parentSectorNameChildListMap;
	}

	@Transient
	public boolean isRequisiteInfoForEscrow() {
		if (BusinessType.CORP_BUSINESS.equals(businessType) || BusinessType.INDIV_BUSINESS.equals(businessType)) {
			return StringUtils.isNotEmpty(corporateNumber);
		}
		return StringUtils.isNotEmpty(registrationNumber);
	}

	@Transient
	public String getPaymentInfo() {
		return String.format("%s %s %s", bankForReceivingPayment != null?bankForReceivingPayment.getName():"미등록", bankAccountForReceivingPayment, bankAccountName);
	}

	@Transient
	public String getImageUrl() {
		if (StringUtils.isNotEmpty(logoImageUrl)) return logoImageUrl;
		return profileImageUrl;
	}

	@Transient
	public boolean isRequestPaymentAvailable() {
		if (BusinessType.CORP_BUSINESS.equals(businessType) || BusinessType.INDIV_BUSINESS.equals(businessType)) {
			return !StringUtils.isAnyEmpty(corporateName, corporateNumber, bankAccountName, bankAccountForReceivingPayment) && bankForReceivingPayment != null;
		}
		return !StringUtils.isAnyEmpty(registrationNumber, realName, bankAccountName, bankAccountForReceivingPayment) && bankForReceivingPayment != null;
	}

	@Transient
	public String getFreelancerBusinessInfo() {
		if (businessType == null) return null;
		if (BusinessType.INDIVIDUAL.equals(businessType)) {
			return registrationNumber;
		} else {
			return String.format("%s (%s)", corporateName, corporateNumber);
		}
	}

	public boolean isInfoInput() {
		return !StringUtils.isAnyEmpty(nickname, cellphone, email);
	}

	public float getRatingPercentageAsFreelancer() {
		return ratingsAsFreelancer * 100 / 5;
	}

	public String getRatingsAsFreelancer() {
		return String.format("%.1f", ratingsAsFreelancer);
	}

	public String getRatingsAsClient() {
		return String.format("%.1f", ratingsAsClient);
	}

	public float getRatingPercentageAsClient() {
		return ratingsAsClient * 100 / 5;
	}

	public boolean isSampleLogoImage() {
		return StringUtils.isNotEmpty(logoImageUrl) && logoImageUrl.startsWith("/static/");
	}
}
