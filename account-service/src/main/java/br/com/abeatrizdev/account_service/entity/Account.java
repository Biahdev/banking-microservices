package br.com.abeatrizdev.account_service.entity;

import br.com.abeatrizdev.account_service.dto.account.UpdateAccountRequest;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private UUID publicId;

    @Column(nullable = false, length = 150)
    String name;

    @Column(unique = true, nullable = false, length = 14)
    String document;

    BigDecimal balance = new BigDecimal(0);

    @Enumerated(EnumType.STRING)
    AccountStatus status = AccountStatus.ACTIVE;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    @PrePersist
    public void generatePublicId() {
        if (publicId == null) {
            publicId = UUID.randomUUID();
        }
    }

    public Account() {
    }

    public Account(String name, String document, BigDecimal balance, AccountStatus status) {
        this.name = name;
        this.document = document;
        this.balance = balance;
        this.status = status;
    }

    public Account(Long id, UUID publicId, String name, String document, BigDecimal balance, AccountStatus status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.publicId = publicId;
        this.name = name;
        this.document = document;
        this.balance = balance;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void update(UpdateAccountRequest request) {
        this.name = request.name() != null ? request.name() : this.name;
        this.document = request.document() != null ? request.document() : this.document;
        this.balance = request.balance() != null ? request.balance() : this.balance;
    }

    public void softDelete() {
        if (this.status == AccountStatus.INACTIVE) {
            throw new IllegalStateException("Account is already inactive");
        }

        if (this.balance.compareTo(BigDecimal.ZERO) != 0) {
            throw new IllegalStateException("Cannot delete account with non-zero balance.");
        }

        this.status = AccountStatus.INACTIVE;
    }

    public void activate() {
        if (this.status == AccountStatus.ACTIVE) {
            throw new IllegalStateException("Account is already active");
        }

        this.status = AccountStatus.ACTIVE;
    }

    public Long getId() {
        return id;
    }

    public UUID getPublicId() {
        return publicId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return Objects.equals(getId(), account.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", publicId=" + publicId +
                ", name='" + name + '\'' +
                ", document='" + document + '\'' +
                ", balance=" + balance +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
