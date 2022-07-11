package com.freelancerk;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;

@Log
@Setter
@Getter
public class ContestCalculateUtil {
	private static double WITHHOLDING_RATE = 0.033; // 원청징수 비율
	private static double VAT_RATE = 0.1; // VAT 비율
	private static double FK_FEE_RATE = 0.1; // 플랫폼 수수료 비율

	/**
	 * 입력받는 값
	 */
	private int minPrize; // 최소상금
	private int incentive; // 인센티브
	private int depositMoney; // 보증금
	private int fee; // 수수료 부담금(부담 하지 않는 경우 0원)
	private int countOfParticipants; // 참여자 수
	
	/**
	 * 계산하는 값
	 */
	private PolicyRate policyRate; 		// 보증금 및 환불 규정
	private int confirmativePrizeMoney; // 확정상금
	private double incentiveRealRefund;	// 인센티브 환불
	private double minPrizeRealRefund;	// 최소상금 환불
	private double depositRealRefund;	// 보증금 환불
	private int refundTotalMoney;		// 환불산정금액합계
	private int refundTotalMoneyWithVAT;	// 총 환불금액(VAT 포함)
	private int totalDividend; 				// 참여자 배당총액
	private int dividend;					// 1인당 배당금(원청징수 전)
	private int dividendWithWithholding;	// 1인당 실 배당금(원천징수 후)

	public ContestCalculateUtil(Integer minPrize, Integer incentive, Integer depositMoney, Integer fee, Integer countOfParticipants) {
		this.minPrize = (minPrize == null?0:minPrize);
		this.incentive = (incentive == null?0:incentive);
		this.depositMoney = (depositMoney == null?0:depositMoney);
		this.fee = (fee == null?0:fee);
		this.countOfParticipants = (countOfParticipants == null?0:countOfParticipants);
		this.policyRate = new PolicyRate(countOfParticipants);
		
		calculateContest();
	}
	
	private void calculateContest() {
		// 확정상금 = 최소상금 + 인센티브 = 1등 상금 + 2등 상금 + 3등 상금
		this.confirmativePrizeMoney = this.minPrize + this.incentive;
//		log.info("confirmativePrizeMoney : " + confirmativePrizeMoney + "원");
				
		// 클라이언트 - 환불
		// 인센티브 환불(소수점자리까지 그대로 보여줌)
		this.incentiveRealRefund = this.incentive * this.policyRate.getRefundRateForPrize();
//		log.info("incentiveRealRefund : " + this.incentiveRealRefund + "원");
		
		// 최소상금 환불(소수점자리까지 그대로 보여줌)
		this.minPrizeRealRefund = this.minPrize * this.policyRate.getRefundRateForPrize();
//		log.info("minPrizeRealRefund : " + this.minPrizeRealRefund + "원");
		
		// 보증금 환불(소수점자리까지 그대로 보여줌)
		this.depositRealRefund = this.depositMoney * this.policyRate.getRefundRateForPrize();
//		log.info("depositRealRefund : " + this.depositRealRefund + "원 ");
		
		// 환불산정금액합계(절사한 금액)
		this.refundTotalMoney = (int)(this.incentiveRealRefund + this.minPrizeRealRefund + this.depositRealRefund);
//		log.info("refundTotalMoney : " + this.refundTotalMoney);
		
		// 총 환불금액(VAT 포함)
		this.refundTotalMoneyWithVAT = (int)(this.refundTotalMoney + (this.refundTotalMoney * VAT_RATE));
//		log.info("refundTotalMoneyWithVAT : " + this.refundTotalMoneyWithVAT);
		
		// 클라이언트 - 상금
		// TODO 상금계산 : 플랫폼수수료, 사업소득세, 지방소득세 계산
		
		// 프리랜서 - 배당		
		// 참여자 배당총액
		double prizeDividen = this.confirmativePrizeMoney * this.policyRate.getDividendRateForPrize();
		double depositDividen = this.depositMoney * this.policyRate.getDividendRateForDeposite();
		this.totalDividend = (int)(prizeDividen + depositDividen);
//		log.info("totalDividend : " + this.totalDividend);
		
		// 1인당 배당금(원청징수 전)
		if(this.countOfParticipants == 0) {
			this.dividend = this.totalDividend / 1;	
		} else {
			this.dividend = this.totalDividend / this.countOfParticipants;
		}
		
//		log.info("dividend : " + this.dividend);
		
		// 1인당 실 배당금(원천징수 후)
		this.dividendWithWithholding = (int)(this.dividend - (this.dividend * WITHHOLDING_RATE));
//		log.info("dividendWithWithholding : " + this.dividendWithWithholding);
	}	

	private class PolicyRate {
		private int countOfParticipants;

		public PolicyRate(Integer countOfParticipants) {
			this.countOfParticipants = (countOfParticipants==null?0:countOfParticipants);
		}

		public double getRefundRateForPrize() {
			/**
			 * 10인 이상 : 20% 3인 이상 ~ 10인 미만 : 50% 2인 이하 : 70%
			 */
			if (countOfParticipants > 9) {
				return 0.2;
			} else if (countOfParticipants > 2) {
				return 0.5;
			} else {
				return 0.7;
			}
		}

		public double getDividendRateForPrize() {
			/**
			 * 10인 이상 : 70% 3인 이상 ~ 10인 미만 : 40% 2인 이하 : 20%
			 */
			if (countOfParticipants > 9) {
				return 0.7;
			} else if (countOfParticipants > 2) {
				return 0.4;
			} else {
				return 0.2;
			}
		}

		public double getRefundRateForDeposite() {
			/**
			 * 10인 이상 : 20% 3인 이상 ~ 10인 미만 : 50% 2인 이하 : 80%
			 */
			if (countOfParticipants > 9) {
				return 0.2;
			} else if (countOfParticipants > 2) {
				return 0.5;
			} else {
				return 0.8;
			}
		}

		public double getDividendRateForDeposite() {
			/**
			 * 10인 이상 : 80% 3인 이상 ~ 10인 미만 : 50% 2인 이하 : 20%
			 */
			if (countOfParticipants > 9) {
				return 0.8;
			} else if (countOfParticipants > 2) {
				return 0.5;
			} else {
				return 0.2;
			}
		}
	}
}
