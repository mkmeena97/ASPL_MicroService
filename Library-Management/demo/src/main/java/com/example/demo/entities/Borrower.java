package com.example.demo.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="borrowers")
public class Borrower {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String borrowerId;
	
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "userId", nullable = false)
    private Users user;
    
    private int activeBorrowCount;
    

	public String getBorrowerId() {
		return borrowerId;
	}

	public void setBorrowerId(String borrowerId) {
		this.borrowerId = borrowerId;
	}

	public Users getUser() {
		return user;
	}

	public void setUser(Users user) {
		this.user = user;
	}

	public int getActiveBorrowCount() {
		return activeBorrowCount;
	}

	public void setActiveBorrowCount(int activeBorrowCount) {
		this.activeBorrowCount = activeBorrowCount;
	}
    
    
}
