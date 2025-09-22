package com.example.demo.entities;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="memberships")
public class Membership {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String membershipId;
	
	private String planName;
	
	private String maxBooks;
	
	private int borrowDuration;

	private String penaltyPerDay;
	
    private LocalDate startDate;
    
	private LocalDate endDate;
    
    

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}


	public String getMembershipId() {
		return membershipId;
	}

	public void setMembershipId(String membershipId) {
		this.membershipId = membershipId;
	}

	public String getPlanName() {
		return planName;
	}

	public void setPlanName(String planName) {
		this.planName = planName;
	}

	public String getMaxBooks() {
		return maxBooks;
	}

	public void setMaxBooks(String maxBooks) {
		this.maxBooks = maxBooks;
	}

	public int getBorrowDuration() {
		return borrowDuration;
	}

	public void setBorrowDuration(int borrowDuration) {
		this.borrowDuration = borrowDuration;
	}

	public String getPenaltyPerDay() {
		return penaltyPerDay;
	}

	public void setPenaltyPerDay(String penaltyPerDay) {
		this.penaltyPerDay = penaltyPerDay;
	}
	
	
}

