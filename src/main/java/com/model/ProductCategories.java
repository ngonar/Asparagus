package model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Entity representing product categories.
 */
@Entity
@Table(name = "product_categories")
@NamedQueries({
    @NamedQuery(name = "ProductCategories.findAll", query = "SELECT p FROM ProductCategories p"),
    @NamedQuery(name = "ProductCategories.findById", query = "SELECT p FROM ProductCategories p WHERE p.id = :id"),
    @NamedQuery(name = "ProductCategories.findByCode", query = "SELECT p FROM ProductCategories p WHERE p.code = :code"),
    @NamedQuery(name = "ProductCategories.findByName", query = "SELECT p FROM ProductCategories p WHERE p.name = :name"),
    @NamedQuery(name = "ProductCategories.findByActive", query = "SELECT p FROM ProductCategories p WHERE p.active = :active")
})
public class ProductCategories implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Long id;

    @Column(name = "code")
    private String code;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "active")
    private Boolean active;

    @Column(name = "create_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;

    @Column(name = "create_by")
    private String createBy;

    @Column(name = "update_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateDate;

    @Column(name = "update_by")
    private String updateBy;

    public ProductCategories() {
    }

    public ProductCategories(Long id) {
        this.id = id;
    }

    public ProductCategories(String code, String name) {
        this.code = code;
        this.name = name;
        this.active = true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ProductCategories)) {
            return false;
        }
        ProductCategories other = (ProductCategories) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.ProductCategories[id=" + id + ", name=" + name + "]";
    }
}
