package local.nix.financial.management.entity;

import org.hibernate.annotations.DiscriminatorFormula;

import javax.persistence.*;
import java.time.Clock;
import java.time.Instant;

@Entity
@Table(name = "operation")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorFormula("CASE WHEN amount > 0 THEN 'income' WHEN amount > 0 ELSE 'expense' END")
public class Operation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(nullable = false, name = "account_id")
    private Account account;

    @Column(nullable = false)
    private Long amount;

    @Column(nullable = false)
    private Instant timestamp;

    public Operation() {
    }

    public Operation(Account account, Long amount) {
        this.account = account;
        this.amount = amount;
        this.timestamp = Instant.now(Clock.systemUTC());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}

