package local.nix.financial.management.entity;

import local.nix.financial.management.entity.abstr.OperationCategory;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "income_category")
public class IncomeCategory extends OperationCategory {

    @ManyToMany(mappedBy = "categories")
    private final List<Income> operations = new ArrayList<>();

    public List<Income> getOperations() {
        return operations;
    }

}

