package local.nix.financial.management.entity;


import local.nix.financial.management.entity.abstr.OperationCategory;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "expense_category")
public class ExpenseCategory extends OperationCategory {

    @ManyToMany(mappedBy = "categories")
    private final List<Expense> operations = new ArrayList<>();

    public List<Expense> getOperations() {

        return operations;

    }

}
