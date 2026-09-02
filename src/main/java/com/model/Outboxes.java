/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author ngonar
 */
@Entity
@Table(name = "outboxes")
@NamedQueries({
    @NamedQuery(name = "Outboxes.findAll", query = "SELECT o FROM Outboxes o"),
    @NamedQuery(name = "Outboxes.findById", query = "SELECT o FROM Outboxes o WHERE o.id = :id"),
    @NamedQuery(name = "Outboxes.findByInboxId", query = "SELECT o FROM Outboxes o WHERE o.inboxId = :inboxId"),
    @NamedQuery(name = "Outboxes.findByStatus", query = "SELECT o FROM Outboxes o WHERE o.status = :status"),
    @NamedQuery(name = "Outboxes.findByCreateDate", query = "SELECT o FROM Outboxes o WHERE o.createDate = :createDate"),
    @NamedQuery(name = "Outboxes.findByReceiver", query = "SELECT o FROM Outboxes o WHERE o.receiver = :receiver"),
    @NamedQuery(name = "Outboxes.findByReceiverType", query = "SELECT o FROM Outboxes o WHERE o.receiverType = :receiverType"),
    @NamedQuery(name = "Outboxes.findBySms", query = "SELECT o FROM Outboxes o WHERE o.sms = :sms"),
    @NamedQuery(name = "Outboxes.findByStatusDate", query = "SELECT o FROM Outboxes o WHERE o.statusDate = :statusDate"),
    @NamedQuery(name = "Outboxes.findByTransactionId", query = "SELECT o FROM Outboxes o WHERE o.transactionId = :transactionId"),
    @NamedQuery(name = "Outboxes.findByUserId", query = "SELECT o FROM Outboxes o WHERE o.userId = :userId"),
    @NamedQuery(name = "Outboxes.findByFreeOfCharge", query = "SELECT o FROM Outboxes o WHERE o.freeOfCharge = :freeOfCharge"),
    @NamedQuery(name = "Outboxes.findByIsCommand", query = "SELECT o FROM Outboxes o WHERE o.isCommand = :isCommand"),
    @NamedQuery(name = "Outboxes.findByModuleId", query = "SELECT o FROM Outboxes o WHERE o.moduleId = :moduleId"),
    @NamedQuery(name = "Outboxes.findByPrioritas", query = "SELECT o FROM Outboxes o WHERE o.prioritas = :prioritas"),
    @NamedQuery(name = "Outboxes.findBySender", query = "SELECT o FROM Outboxes o WHERE o.sender = :sender"),
    @NamedQuery(name = "Outboxes.findByMediaTypeId", query = "SELECT o FROM Outboxes o WHERE o.mediaTypeId = :mediaTypeId"),
    @NamedQuery(name = "Outboxes.findByResponseCode", query = "SELECT o FROM Outboxes o WHERE o.responseCode = :responseCode")})
public class Outboxes implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "inbox_id")
    private Long inboxId;
    @Lob
    @Column(name = "message")
    private String message;
    @Column(name = "status")
    private Integer status;
    @Column(name = "create_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;
    @Column(name = "receiver")
    private String receiver;
    @Column(name = "receiver_type")
    private String receiverType;
    @Column(name = "sms")
    private Boolean sms;
    @Column(name = "status_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date statusDate;
    @Column(name = "transaction_id")
    private Integer transactionId;
    @Column(name = "user_id")
    private Integer userId;
    @Column(name = "free_of_charge")
    private Boolean freeOfCharge;
    @Column(name = "is_command")
    private Boolean isCommand;
    @Column(name = "module_id")
    private Integer moduleId;
    @Column(name = "prioritas")
    private Short prioritas;
    @Column(name = "sender")
    private String sender;
    @Column(name = "media_type_id")
    private Integer mediaTypeId;
    @Column(name = "response_code")
    private String responseCode;
    
    @OneToOne(optional = false)
    @JoinColumn(name = "inbox_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Inboxes inboxes;

    public Outboxes() {
    }

    public Outboxes(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getInboxId() {
        return inboxId;
    }

    public void setInboxId(Long inboxId) {
        this.inboxId = inboxId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getReceiverType() {
        return receiverType;
    }

    public void setReceiverType(String receiverType) {
        this.receiverType = receiverType;
    }

    public Boolean getSms() {
        return sms;
    }

    public void setSms(Boolean sms) {
        this.sms = sms;
    }

    public Date getStatusDate() {
        return statusDate;
    }

    public void setStatusDate(Date statusDate) {
        this.statusDate = statusDate;
    }

    public Integer getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Integer transactionId) {
        this.transactionId = transactionId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Boolean getFreeOfCharge() {
        return freeOfCharge;
    }

    public void setFreeOfCharge(Boolean freeOfCharge) {
        this.freeOfCharge = freeOfCharge;
    }

    public Boolean getIsCommand() {
        return isCommand;
    }

    public void setIsCommand(Boolean isCommand) {
        this.isCommand = isCommand;
    }

    public Integer getModuleId() {
        return moduleId;
    }

    public void setModuleId(Integer moduleId) {
        this.moduleId = moduleId;
    }

    public Short getPrioritas() {
        return prioritas;
    }

    public void setPrioritas(Short prioritas) {
        this.prioritas = prioritas;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public Integer getMediaTypeId() {
        return mediaTypeId;
    }

    public void setMediaTypeId(Integer mediaTypeId) {
        this.mediaTypeId = mediaTypeId;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Outboxes)) {
            return false;
        }
        Outboxes other = (Outboxes) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.Outboxes[id=" + id + "]";
    }

    /**
     * @return the inboxes
     */
    public Inboxes getInboxes() {
        return inboxes;
    }

}
