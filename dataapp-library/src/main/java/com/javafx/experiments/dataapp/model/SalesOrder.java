/*
 * Copyright (c) 2008, 2011 Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * This file is available and licensed under the following license:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the distribution.
 *  - Neither the name of Oracle Corporation nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.javafx.experiments.dataapp.model;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

@Entity
@Table(name = "SALES_ORDER", catalog = "", schema = "APP")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "SalesOrder.findAll", query = "SELECT s FROM SalesOrder s"),
    @NamedQuery(name = "SalesOrder.findByOrderId", query = "SELECT s FROM SalesOrder s WHERE s.orderId = :orderId"),
    @NamedQuery(name = "SalesOrder.findByDate", query = "SELECT s FROM SalesOrder s WHERE s.date = :date"),
    @NamedQuery(name = "SalesOrder.findByChannel", query = "SELECT s FROM SalesOrder s WHERE s.channel = :channel")})
public class SalesOrder implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name =     "DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date date;

    @JoinColumn(name = "REGION_ID", referencedColumnName = "REGION_ID")
    @ManyToOne
    private Region region;

    @JoinColumn(name = "EMPLOYEE_ID", referencedColumnName = "EMPLOYEE_ID")
    @ManyToOne
    private Employee employee;

    @JoinColumn(name = "DEALER_ID", referencedColumnName = "DEALER_ID")
    @ManyToOne
    private Dealer dealer;

    @JoinColumn(name = "CUSTOMER_ID", referencedColumnName = "CUSTOMER_ID")
    @ManyToOne
    private Customer customer;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ORDER_ID")
    private Integer orderId;

    @Column(name = "CHANNEL")
    private String channel;
    
    @OneToMany(mappedBy = "order")
    private Collection<SalesOrderLine> salesOrderLineCollection;

    public SalesOrder() {
    }

    public SalesOrder(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    @XmlTransient
    public Collection<SalesOrderLine> getSalesOrderLineCollection() {
        return salesOrderLineCollection;
    }

    public void setSalesOrderLineCollection(Collection<SalesOrderLine> salesOrderLineCollection) {
        this.salesOrderLineCollection = salesOrderLineCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (orderId != null ? orderId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SalesOrder)) {
            return false;
        }
        SalesOrder other = (SalesOrder) object;
        return !((this.orderId == null && other.orderId != null) || (this.orderId != null && !this.orderId.equals(other.orderId)));
    }

    @Override
    public String toString() {
        return "com.javafx.experiments.dataapp.model.SalesOrder[ orderId=" + orderId + " ]";
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Dealer getDealer() {
        return dealer;
    }

    public void setDealer(Dealer dealer) {
        this.dealer = dealer;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
    
}
