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
 * Entity representing product switch priorities routing configuration.
 */
@Entity
@Table(name = "product_switch_priorities")
@NamedQueries({
    @NamedQuery(name = "ProductSwitchPriorities.findAll", query = "SELECT p FROM ProductSwitchPriorities p"),
    @NamedQuery(name = "ProductSwitchPriorities.findById", query = "SELECT p FROM ProductSwitchPriorities p WHERE p.id = :id"),
    @NamedQuery(name = "ProductSwitchPriorities.findByProductCode", query = "SELECT p FROM ProductSwitchPriorities p WHERE p.productCode = :productCode"),
    @NamedQuery(name = "ProductSwitchPriorities.findBySwitchName", query = "SELECT p FROM ProductSwitchPriorities p WHERE p.switchName = :switchName"),
    @NamedQuery(name = "ProductSwitchPriorities.findByProductCodeAndSwitchName", query = "SELECT p FROM ProductSwitchPriorities p WHERE p.productCode = :productCode AND p.switchName = :switchName"),
    @NamedQuery(name = "ProductSwitchPriorities.findByActive", query = "SELECT p FROM ProductSwitchPriorities p WHERE p.active = :active")
})
public class ProductSwitchPriorities implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "product_code")
    private String productCode;

    @Column(name = "switch_name")
    private String switchName;

    @Column(name = "priority")
    private Integer priority;

    @Column(name = "weight")
    private Integer weight;

    @Column(name = "threshold")
    private Integer threshold = 5;

    @Column(name = "failure_count")
    private Integer failureCount = 0;

    @Column(name = "disabled_timestamp")
    @Temporal(TemporalType.TIMESTAMP)
    private Date disabledTimestamp;

    @Column(name = "active")
    private Boolean active = true;

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

    public ProductSwitchPriorities() {
    }

    public ProductSwitchPriorities(Long id) {
        this.id = id;
    }

    public ProductSwitchPriorities(String productCode, String switchName, Integer priority) {
        this.productCode = productCode;
        this.switchName = switchName;
        this.priority = priority;
        this.active = true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getSwitchName() {
        return switchName;
    }

    public void setSwitchName(String switchName) {
        this.switchName = switchName;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public Integer getThreshold() {
        return threshold == null ? 5 : threshold;
    }

    public void setThreshold(Integer threshold) {
        this.threshold = threshold;
    }

    public Integer getFailureCount() {
        return failureCount == null ? 0 : failureCount;
    }

    public void setFailureCount(Integer failureCount) {
        this.failureCount = failureCount;
    }

    public Date getDisabledTimestamp() {
        return disabledTimestamp;
    }

    public void setDisabledTimestamp(Date disabledTimestamp) {
        this.disabledTimestamp = disabledTimestamp;
    }

    public Boolean getActive() {
        return active == null ? Boolean.TRUE : active;
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
        if (!(object instanceof ProductSwitchPriorities)) {
            return false;
        }
        ProductSwitchPriorities other = (ProductSwitchPriorities) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.ProductSwitchPriorities[id=" + id + ", productCode=" + productCode + ", switchName=" + switchName + "]";
    }
}
