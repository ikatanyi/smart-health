package io.smarthealth.supplier.domain;

import io.smarthealth.organization.domain.Partner;
import io.smarthealth.product.domain.Product;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import lombok.Data;

/**
 * Organization Partner who supplies Products and Service
 *
 * @author Kelsas
 */
@Entity
@Data
@Table(name = "supplier")
public class Supplier extends Partner {
 
    @ManyToMany(cascade = {CascadeType.PERSIST,CascadeType.MERGE})
    @JoinTable(name = "supplier_product",
                joinColumns = @JoinColumn(name = "supplier_id"),
                inverseJoinColumns = @JoinColumn(name = "product_id")
            )
    private Set<Product> products = new HashSet<>();
    
//    private BankDetails bankDetails;
//    private Account creditorAccount;
// TODO:  A list of contacts and address to be include here
    //contact Type for - Sales| Accounts | Purchase
    public void addProduct(Product product){
        this.products.add(product);
        product.getSuppliers().add(this);
    }
    public void removeProduct(Product product){
        this.products.remove(product);
        product.getSuppliers().remove(this);
    }
    public void removeProducts(){
        Iterator<Product> iterator = this.products.iterator();
        
        while(iterator.hasNext()){
            Product product=iterator.next();
            
            product.getSuppliers().remove(this);
            iterator.remove();
        }
    }
}
