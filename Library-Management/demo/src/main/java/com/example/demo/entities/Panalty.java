package com.example.demo.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="Panalties")
public class Panalty {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String penaltyId;
	
	@OneToOne
	@JoinColumn(name = "transaction_id", referencedColumnName = "transactionId", nullable = false)
	private Transaction transaction;
	
	private Double penaltyAmount;
	
	private boolean paid;
	
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "userId", nullable = false)
    private Users user;

	public String getPenaltyId() {
		return penaltyId;
	}

	public void setPenaltyId(String penaltyId) {
		this.penaltyId = penaltyId;
	}

	public Transaction getTransaction() {
		return transaction;
	}

	public void setTransaction(Transaction transaction) {
		this.transaction = transaction;
	}

	public Double getPenaltyAmount() {
		return penaltyAmount;
	}

	public void setPenaltyAmount(Double penaltyAmount) {
		this.penaltyAmount = penaltyAmount;
	}

	public boolean isPaid() {
		return paid;
	}

	public void setPaid(boolean paid) {
		this.paid = paid;
	}
	
	
}
