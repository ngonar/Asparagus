package model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Entity representing specific supplier configurations (host, IP, credentials)
 * required to route requests for a given product and supplier switch.
 */
@Entity
@Table(name = "product_supplier_configurations")
@NamedQueries({
    @NamedQuery(name = "ProductSupplierConfigurations.findAll", query = "SELECT s FROM ProductSupplierConfigurations s"),
    @NamedQuery(name = "ProductSupplierConfigurations.findById", query = "SELECT s FROM ProductSupplierConfigurations s WHERE s.id = :id"),
    @NamedQuery(name = "ProductSupplierConfigurations.findByProductCode", query = "SELECT s FROM ProductSupplierConfigurations s WHERE s.productCode = :productCode"),
    @NamedQuery(name = "ProductSupplierConfigurations.findByProductCodeAndSupplierName", query = "SELECT s FROM ProductSupplierConfigurations s WHERE s.productCode = :productCode AND s.supplierName = :supplierName")
})
public class ProductSupplierConfigurations implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "product_code")
    private String productCode;

    @Column(name = "supplier_name")
    private String supplierName;

    @Column(name = "host")
    private String host;

    @Column(name = "ip")
    private String ip;

    @Column(name = "port")
    private Integer port;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "api_key")
    private String apiKey;

    @Column(name = "secret_key")
    private String secretKey;

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

    public ProductSupplierConfigurations() {
    }

    public ProductSupplierConfigurations(Long id) {
        this.id = id;
    }

    public ProductSupplierConfigurations(String productCode, String supplierName, String host, String ip, Integer port) {
        this.productCode = productCode;
        this.supplierName = supplierName;
        this.host = host;
        this.ip = ip;
        this.port = port;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
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
        if (!(object instanceof ProductSupplierConfigurations)) {
            return false;
        }
        ProductSupplierConfigurations other = (ProductSupplierConfigurations) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.ProductSupplierConfigurations[ id=" + id + ", productCode=" + productCode + ", supplierName=" + supplierName + ", host=" + host + " ]";
    }
}
